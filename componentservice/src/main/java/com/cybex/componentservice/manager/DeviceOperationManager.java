package com.cybex.componentservice.manager;

import android.os.Handler;
import android.os.Message;

import com.cybex.componentservice.config.BaseConst;
import com.cybex.componentservice.config.CacheConstants;
import com.cybex.componentservice.event.DeviceConnectStatusUpdateEvent;
import com.cybex.componentservice.event.HeartBeatRefreshDataEvent;
import com.cybex.componentservice.event.PinLockedEvent;
import com.cybex.componentservice.utils.bluetooth.BlueToothWrapper;
import com.extropies.common.CommonUtility;
import com.extropies.common.MiddlewareInterface;
import com.hxlx.core.lib.base.BaseApplication;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.common.utils.HandlerUtil;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

public class DeviceOperationManager {


    String currentDeviceName;
    BlueToothWrapper scanThread;
    BlueToothWrapper heartBeatThread;
    ScanHandler scanHandler;
    HeartBeatHandler heartBeatHandler;
    private ConcurrentHashMap<String, DeviceComm> deviceMaps = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, DeviceCallbacsBean> callbackMaps = new ConcurrentHashMap<>();
    private BlockingQueue<Thread> queue = new LinkedBlockingQueue<>();
    final byte[] coinTypes = {MiddlewareInterface.PAEW_COIN_TYPE_EOS, MiddlewareInterface.PAEW_COIN_TYPE_ETH, MiddlewareInterface.PAEW_COIN_TYPE_CYB};
    final int[][] derivePaths = {
            {0, 0x8000002C, 0x800000c2, 0x80000000, 0x00000000, 0x00000000},
            {0, 0x8000002c, 0x8000003c, 0x80000000, 0x00000000},
            {0, 0, 1, 0x00000080, 0x00000000, 0x00000000}
    };

    private ExecutorService singleExecutor = Executors.newSingleThreadExecutor();


    private DeviceOperationManager() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread thread = queue.take();
                        singleExecutor.execute(thread);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void clearDevice(String deviceName) {
        deviceMaps.remove(currentDeviceName);
        if (currentDeviceName != null && currentDeviceName.equals(deviceName)) {
            currentDeviceName = null;
        }
    }


    public static class SingleTon {

        public static DeviceOperationManager instance = new DeviceOperationManager();
    }

    public static DeviceOperationManager getInstance() {
        return SingleTon.instance;
    }

    public String getCurrentDeviceName() {
        return currentDeviceName;
    }


    public String getCurrentDeviceInitPswHint() {
        DeviceComm deviceComm = deviceMaps.get(currentDeviceName);
        if (deviceComm != null) {
            return deviceComm.initialPswHint;
        }
        return "";
    }

    public int getDeviceConnectStatus(String deviceName) {
        if (deviceMaps.get(deviceName) == null) {
            return CacheConstants.STATUS_BLUETOOTH_DISCONNCETED;
        }
        if (deviceMaps.get(deviceName).currentState != CacheConstants.STATUS_BLUETOOTH_CONNCETED) {
            return CacheConstants.STATUS_BLUETOOTH_DISCONNCETED;
        }
        return CacheConstants.STATUS_BLUETOOTH_CONNCETED;
    }

    public boolean isDeviceConnectted(String deviceName) {
        if (deviceName == null || deviceMaps.get(deviceName) == null) {
            return false;
        }
        if (deviceMaps.get(deviceName).currentState != CacheConstants.STATUS_BLUETOOTH_CONNCETED) {
            return false;
        }
        return true;

    }


    public boolean isDeviceFreeContext(String deviceName) {
        if (deviceName == null || deviceMaps.get(deviceName) == null) {
            return false;
        }
        return deviceMaps.get(deviceName).isFreeContexting;
    }


    public int getDeviceBatteryChargeMode(String deviceName) {
        if (deviceMaps.get(deviceName) == null) {
            return -1;
        }
        return deviceMaps.get(deviceName).batteryMode;
    }

    public int getDevicePowerAmount(String deviceName) {
        if (deviceMaps.get(deviceName) == null) {
            return -1;
        }
        return deviceMaps.get(deviceName).powerAmount;
    }

    public void clearCallback(String tag) {
        LoggerManager.d("clearCallback callbackMaps.size="+callbackMaps.size()+"   tag="+tag+"    containerTag?="+(callbackMaps.get(tag)!=null));
        callbackMaps.remove(tag);
        if(callbackMaps.size()>0){
            Set<String> strings = callbackMaps.keySet();
            LoggerManager.d("clearCallback keys="+Arrays.toString(strings.toArray()));
        }
    }

    public void scanDevice(String tag, ScanDeviceCallback scanDeviceCallback) {

        DeviceCallbacsBean deviceCallbacks = callbackMaps.get(tag);
        if (deviceCallbacks == null) {
            deviceCallbacks = new DeviceCallbacsBean();
            callbackMaps.put(tag, deviceCallbacks);
        }
        deviceCallbacks.scanDeviceCallback = scanDeviceCallback;

        if (scanHandler == null) {
            scanHandler = new ScanHandler();
        }
        if ((scanThread == null) || (scanThread.getState() == Thread.State.TERMINATED)) {
            scanThread = new BlueToothWrapper(scanHandler);
            scanThread.setGetDevListWrapper(BaseApplication.getAppContext(),
                    "WOOKONG");
            scanThread.start();
        }
//        singleExecutor.execute(scanThread);
    }


    public void connectDevice(String tag, String deviceName, DeviceConnectCallback connectCallback) {
        if (isDeviceConnectted(deviceName)) {
            connectCallback.onConnectSuccess();
            return;
        }
        DeviceCallbacsBean deviceCallbacks = callbackMaps.get(tag);
        if (deviceCallbacks == null) {
            deviceCallbacks = new DeviceCallbacsBean();
            callbackMaps.put(tag, deviceCallbacks);
        }
        deviceCallbacks.connectCallback = connectCallback;

        DeviceComm deviceComm = deviceMaps.get(deviceName);
        if (deviceComm == null) {
            deviceComm = new DeviceComm(deviceName);
            deviceMaps.put(deviceName, deviceComm);
        }

        if (deviceComm.mDeviceHandler == null) {
            deviceComm.mDeviceHandler = new DeviceHandler(deviceName);
        }
//        if ((deviceComm.connectThread == null) || (deviceComm.connectThread.getState() == Thread.State.TERMINATED)) {
        deviceComm.connectThread = new BlueToothWrapper(deviceComm.mDeviceHandler);
        deviceComm.connectThread.setInitContextWithDevNameWrapper(BaseApplication.getAppContext(),
                deviceName);
        deviceComm.connectThread.setHeartBeatHandler(deviceComm.mDeviceHandler);
//            deviceComm.connectThread.start();
//            singleExecutor.execute(deviceComm.connectThread);
        try {
            queue.put(deviceComm.connectThread);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        }
    }

    public void getDeviceInfo(String tag, String deviceName, GetDeviceInfoCallback getDeviceInfoCallback) {
        if (!isDeviceConnectted(deviceName)||isDeviceFreeContext(deviceName)) {
            getDeviceInfoCallback.onGetFail();
            return;
        }
        DeviceCallbacsBean deviceCallbacks = callbackMaps.get(tag);
        if (deviceCallbacks == null) {
            deviceCallbacks = new DeviceCallbacsBean();
            callbackMaps.put(tag, deviceCallbacks);
        }
        deviceCallbacks.getDeviceInfoCallback = getDeviceInfoCallback;

        DeviceComm deviceComm = deviceMaps.get(deviceName);
        if (deviceComm == null) {
            deviceComm = new DeviceComm(deviceName);
            deviceMaps.put(deviceName, deviceComm);
        }

        if (deviceComm.mDeviceHandler == null) {
            deviceComm.mDeviceHandler = new DeviceHandler(deviceName);
        }
//        if ((deviceComm.getDeviceInfoThread == null) || (deviceComm.getDeviceInfoThread.getState()
//                == Thread.State.TERMINATED)) {
        deviceComm.getDeviceInfoThread = new BlueToothWrapper(deviceComm.mDeviceHandler);
        deviceComm.getDeviceInfoThread.setGetInfoWrapper(deviceComm.contextHandle,
                0);
//            deviceComm.getDeviceInfoThread.start();
//            singleExecutor.execute(deviceComm.getDeviceInfoThread);
        try {
            queue.put(deviceComm.getDeviceInfoThread);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        }

    }


    public void getHeartDeviceInfo(String tag, String deviceName, GetDeviceInfoCallback getHeartDeviceInfoCallback) {

        if (!isDeviceConnectted(deviceName)||isDeviceFreeContext(deviceName)) {
            getHeartDeviceInfoCallback.onGetFail();
            return;
        }

        DeviceCallbacsBean deviceCallbacks = callbackMaps.get(tag);
        if (deviceCallbacks == null) {
            deviceCallbacks = new DeviceCallbacsBean();
            callbackMaps.put(tag, deviceCallbacks);
        }
        deviceCallbacks.getHeartDeviceInfoCallback = getHeartDeviceInfoCallback;

        if (heartBeatHandler == null) {
            heartBeatHandler = new HeartBeatHandler(deviceName);
        }
        DeviceComm deviceComm = deviceMaps.get(deviceName);
        if (deviceComm == null) {
            deviceComm = new DeviceComm(deviceName);
            deviceMaps.put(deviceName, deviceComm);
        }

//        if ((heartBeatThread == null) || (heartBeatThread.getState() == Thread.State.TERMINATED)) {
//
//        } else {
//            heartBeatThread.interrupt();
//        }
//        queue.remove(heartBeatThread);
        if(heartBeatThread!=null&&queue.contains(heartBeatThread)){
            return;
        }
        heartBeatThread = new BlueToothWrapper(heartBeatHandler);
        heartBeatThread.setGetInfoWrapper(deviceComm.contextHandle,
                0);
//        singleExecutor.execute(heartBeatThread);
        try {
            queue.put(heartBeatThread);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    public void importMnemonics(String tag, String deviceName, String mnemonics, ImportMnemonicCallback importMnemonicCallback) {

        if (!isDeviceConnectted(deviceName)||isDeviceFreeContext(deviceName)) {
            importMnemonicCallback.onImportFailed();
            return;
        }
        DeviceCallbacsBean deviceCallbacks = callbackMaps.get(tag);
        if (deviceCallbacks == null) {
            deviceCallbacks = new DeviceCallbacsBean();
            callbackMaps.put(tag, deviceCallbacks);
        }
        deviceCallbacks.importMnemonicCallback = importMnemonicCallback;


        DeviceComm deviceComm = deviceMaps.get(deviceName);
        if (deviceComm == null) {
            deviceComm = new DeviceComm(deviceName);
            deviceMaps.put(deviceName, deviceComm);
        }
        if (deviceComm.mDeviceHandler == null) {
            deviceComm.mDeviceHandler = new DeviceHandler(deviceName);
        }
        deviceComm.importMnemonicThread = new BlueToothWrapper(deviceComm.mDeviceHandler);
        deviceComm.importMnemonicThread.setImportMneWrapper(deviceComm.contextHandle,
                0, mnemonics);
//        singleExecutor.execute(deviceComm.importMnemonicThread);
        try {
            queue.put(deviceComm.importMnemonicThread);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void startFormat(String tag, String deviceName, DeviceFormatCallback formatCallback) {
        if (!isDeviceConnectted(deviceName)||isDeviceFreeContext(deviceName)) {
            formatCallback.onFormatFailed();
            return;
        }

        DeviceCallbacsBean deviceCallbacks = callbackMaps.get(tag);
        if (deviceCallbacks == null) {
            deviceCallbacks = new DeviceCallbacsBean();
            callbackMaps.put(tag, deviceCallbacks);
        }
        deviceCallbacks.formatCallback = formatCallback;


        DeviceComm deviceComm = deviceMaps.get(deviceName);
        if (deviceComm == null) {
            deviceComm = new DeviceComm(deviceName);
            deviceMaps.put(deviceName, deviceComm);
        }
        if (deviceComm.mDeviceHandler == null) {
            deviceComm.mDeviceHandler = new DeviceHandler(deviceName);
        }
//        if ((deviceComm.formatThread == null) || (deviceComm.formatThread.getState() == Thread.State.TERMINATED)) {
        deviceComm.formatThread = new BlueToothWrapper(deviceComm.mDeviceHandler);
        deviceComm.formatThread.setFormatDeviceWrapper(deviceComm.contextHandle,
                0);
//            deviceComm.formatThread.start();
//            singleExecutor.execute(deviceComm.formatThread);
        try {
            queue.put(deviceComm.formatThread);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        }
    }

    public void pressConfirm(String tag, String deviceName, PressConfirmCallback pressConfirmCallback) {
        HandlerUtil.runOnUiThreadDelay(new Runnable() {
            @Override
            public void run() {
                pressConfirmCallback.onConfirmSuccess();
            }
        }, 4000);

//        DeviceCallbacsBean deviceCallbacks = callbackMaps.get(tag);
//        if (deviceCallbacks == null) {
//            deviceCallbacks = new DeviceCallbacsBean();
//            callbackMaps.put(tag, deviceCallbacks);
//        }
//        deviceCallbacks.pressConfirmCallback = pressConfirmCallback;
//
//
//        DeviceComm deviceComm = deviceMaps.get(deviceName);
//        if (deviceComm == null) {
//            deviceComm = new DeviceComm(deviceName);
//            deviceMaps.put(deviceName, deviceComm);
//        }
//        if (deviceComm.mDeviceHandler == null) {
//            deviceComm.mDeviceHandler = new DeviceHandler(deviceName);
//        }
//        if ((deviceComm.confirmThread == null) || (deviceComm.confirmThread.getState() == Thread.State.TERMINATED)) {
//            deviceComm.confirmThread = new BlueToothWrapper(deviceComm.mDeviceHandler);
//
//            //todo
//            deviceComm.confirmThread.setAbortFPWrapper(deviceComm.contextHandle,
//                    0);
//            deviceComm.confirmThread.start();
//        }
    }


    public void startVerifyFP(String tag, String deviceName, DeviceVerifyFPCallback verifyFPCallback) {
        if (!isDeviceConnectted(deviceName)||isDeviceFreeContext(deviceName)) {
            verifyFPCallback.onVerifyFailed(-1);
            return;
        }

        DeviceCallbacsBean deviceCallbacks = callbackMaps.get(tag);
        if (deviceCallbacks == null) {
            deviceCallbacks = new DeviceCallbacsBean();
            callbackMaps.put(tag, deviceCallbacks);
        }
        deviceCallbacks.verifyFPCallback = verifyFPCallback;


        DeviceComm deviceComm = deviceMaps.get(deviceName);
        if (deviceComm == null) {
            deviceComm = new DeviceComm(deviceName);
            deviceMaps.put(deviceName, deviceComm);
        }
        if (deviceComm.mDeviceHandler == null) {
            deviceComm.mDeviceHandler = new DeviceHandler(deviceName);
        }
//        if ((deviceComm.verifyFPThread == null) || (deviceComm.verifyFPThread.getState() == Thread.State.TERMINATED)) {
        deviceComm.verifyFPThread = new BlueToothWrapper(deviceComm.mDeviceHandler);
        deviceComm.verifyFPThread.setVerifyFPWrapper(deviceComm.contextHandle,
                0);
//            deviceComm.verifyFPThread.start();
//            singleExecutor.execute(deviceComm.verifyFPThread);
        try {
            queue.put(deviceComm.verifyFPThread);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        }
    }


    public void freeContext(String tag, String deviceName, FreeContextCallback freeContextCallback) {
        freeContext(tag, true, deviceName, freeContextCallback);
    }

    public void freeContext(String tag, boolean isManual, String deviceName, FreeContextCallback freeContextCallback) {
        if (!isDeviceConnectted(deviceName)) {
            if (freeContextCallback != null)
                freeContextCallback.onFreeFailed();
            return;
        }

        LoggerManager.e("freeContext isManual=" + isManual);
        DeviceCallbacsBean deviceCallbacks = callbackMaps.get(tag);
        if (deviceCallbacks == null) {
            deviceCallbacks = new DeviceCallbacsBean();
            callbackMaps.put(tag, deviceCallbacks);
        }
        deviceCallbacks.freeContextCallback = freeContextCallback;

        DeviceComm deviceComm = deviceMaps.get(deviceName);
        if (deviceComm != null&&deviceComm.isFreeContexting) {
            return;
        }
        if (deviceComm == null) {
            deviceComm = new DeviceComm(deviceName);
            deviceMaps.put(deviceName, deviceComm);
        }
        deviceComm.isManualFree = isManual;
        if (deviceComm.mDeviceHandler == null) {
            deviceComm.mDeviceHandler = new DeviceHandler(deviceName);
        }
//        if ((deviceComm.freeContextThread == null) || (deviceComm.freeContextThread.getState()
//                == Thread.State.TERMINATED)) {
        deviceComm.freeContextThread = new BlueToothWrapper(deviceComm.mDeviceHandler);
        deviceComm.freeContextThread.setFreeContextWrapper(deviceComm.contextHandle);
//            singleExecutor.execute(deviceComm.freeContextThread);
        try {
            queue.put(deviceComm.freeContextThread);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        }
    }


    public void enrollFP(String tag, String deviceName, EnrollFPCallback enrollFPCallback) {
        if (!isDeviceConnectted(deviceName)||isDeviceFreeContext(deviceName)) {
            enrollFPCallback.onEnrollFinish(-1);
            return;
        }
        LoggerManager.e("enrollFP tag=" + tag + "    deviceName=" + deviceName);
        DeviceCallbacsBean deviceCallbacks = callbackMaps.get(tag);
        if (deviceCallbacks == null) {
            deviceCallbacks = new DeviceCallbacsBean();
            callbackMaps.put(tag, deviceCallbacks);
        }
        deviceCallbacks.enrollFPCallback = enrollFPCallback;


        DeviceComm deviceComm = deviceMaps.get(deviceName);
        if (deviceComm == null) {
            deviceComm = new DeviceComm(deviceName);
            deviceMaps.put(deviceName, deviceComm);
        }
        if (deviceComm.mDeviceHandler == null) {
            deviceComm.mDeviceHandler = new DeviceHandler(deviceName);
        }
//        if ((deviceComm.enrollFPThread == null) || (deviceComm.enrollFPThread.getState() == Thread.State.TERMINATED)) {
        deviceComm.enrollFPThread = new BlueToothWrapper(deviceComm.mDeviceHandler);
        deviceComm.enrollFPThread.setEnrollFPWrapper(deviceComm.contextHandle,
                0);
//            singleExecutor.execute(deviceComm.enrollFPThread);
        try {
            queue.put(deviceComm.enrollFPThread);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        }
    }

    public void abortEnrollFp(String tag,String deviceName) {
        abortEnrollFp(tag,deviceName,null);
    }

    public void abortEnrollFp(String tag,String deviceName,AbortFPCallback abortFPCallback) {
        if (!isDeviceConnectted(deviceName)||isDeviceFreeContext(deviceName)) {
            if(abortFPCallback!=null)
                abortFPCallback.onAbortFail();
            return;
        }

        DeviceCallbacsBean deviceCallbacks = callbackMaps.get(tag);
        if (deviceCallbacks == null) {
            deviceCallbacks = new DeviceCallbacsBean();
            callbackMaps.put(tag, deviceCallbacks);
        }
        deviceCallbacks.abortFPCallback = abortFPCallback;

        DeviceComm deviceComm = deviceMaps.get(deviceName);
        LoggerManager.d("abortEnrollFp currentDeviceName=" + currentDeviceName + "    deviceName=" + deviceName);
        if (deviceComm == null) {
            deviceComm = new DeviceComm(deviceName);
            deviceMaps.put(deviceName, deviceComm);
        }
        if (deviceComm.mDeviceHandler == null) {
            deviceComm.mDeviceHandler = new DeviceHandler(deviceName);
        }

//        if ((deviceComm.abortEnrollFPThread == null) || (deviceComm.abortEnrollFPThread.getState()
//                == Thread.State.TERMINATED)) {
            deviceComm.abortEnrollFPThread = new BlueToothWrapper(deviceComm.mDeviceHandler);
            deviceComm.abortEnrollFPThread.setAbortFPWrapper(deviceComm.contextHandle,
                    0);
            deviceComm.abortEnrollFPThread.start();
//        }
    }


    public void abortButton(String tag,String deviceName) {
        abortButton(tag,deviceName,null);
    }

    public void abortButton(String tag,String deviceName,AbortButtonCallback abortButtonCallback) {
        if (!isDeviceConnectted(deviceName)||isDeviceFreeContext(deviceName)) {
            if(abortButtonCallback!=null)
                abortButtonCallback.onAbortFail();
            return;
        }

        DeviceCallbacsBean deviceCallbacks = callbackMaps.get(tag);
        if (deviceCallbacks == null) {
            deviceCallbacks = new DeviceCallbacsBean();
            callbackMaps.put(tag, deviceCallbacks);
        }
        deviceCallbacks.abortButtonCallback = abortButtonCallback;

        DeviceComm deviceComm = deviceMaps.get(deviceName);
        LoggerManager.d("abortButton currentDeviceName=" + currentDeviceName + "    deviceName=" + deviceName);
        if (deviceComm == null) {
            deviceComm = new DeviceComm(deviceName);
            deviceMaps.put(deviceName, deviceComm);
        }
        if (deviceComm.mDeviceHandler == null) {
            deviceComm.mDeviceHandler = new DeviceHandler(deviceName);
        }

//        if ((deviceComm.abortButtonThread == null) || (deviceComm.abortButtonThread.getState()
//                == Thread.State.TERMINATED)) {
            deviceComm.abortButtonThread = new BlueToothWrapper(deviceComm.mDeviceHandler);
            deviceComm.abortButtonThread.setAbortButtonWrapper(deviceComm.contextHandle,
                    0);
            deviceComm.abortButtonThread.start();
//        }
    }


    public void jsonSerialization(
            String tag,
            String jsonTxStr,
            String deviceName,
            JsonSerilizeCallback jsonSerilizeCallback) {

        if (!isDeviceConnectted(deviceName)||isDeviceFreeContext(deviceName)) {
            jsonSerilizeCallback.onSerilizeFail();
            return;
        }
        DeviceCallbacsBean deviceCallbacks = callbackMaps.get(tag);
        if (deviceCallbacks == null) {
            deviceCallbacks = new DeviceCallbacsBean();
            callbackMaps.put(tag, deviceCallbacks);
        }
        deviceCallbacks.jsonSerilizeCallback = jsonSerilizeCallback;


        DeviceComm deviceComm = deviceMaps.get(deviceName);
        if (deviceComm == null) {
            deviceComm = new DeviceComm(deviceName);
            deviceMaps.put(deviceName, deviceComm);
        }
        if (deviceComm.mDeviceHandler == null) {
            deviceComm.mDeviceHandler = new DeviceHandler(deviceName);
        }
//        if ((deviceComm.jsonSerializeThread == null) || (deviceComm.jsonSerializeThread.getState()
//                == Thread.State.TERMINATED)) {
        deviceComm.jsonSerializeThread = new BlueToothWrapper(deviceComm.mDeviceHandler);
        deviceComm.jsonSerializeThread.setEOSTxSerializeWrapper(jsonTxStr);
//            singleExecutor.execute(deviceComm.jsonSerializeThread);
        try {
            queue.put(deviceComm.jsonSerializeThread);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        }

    }

    public void initPin(String tag, String deviceName, String password, String passwordHint, InitPinCallback initPinCallback) {

        if (!isDeviceConnectted(deviceName)||isDeviceFreeContext(deviceName)) {
            initPinCallback.onInitFail();
            return;
        }

        DeviceCallbacsBean deviceCallbacks = callbackMaps.get(tag);
        if (deviceCallbacks == null) {
            deviceCallbacks = new DeviceCallbacsBean();
            callbackMaps.put(tag, deviceCallbacks);
        }
        deviceCallbacks.initPinCallback = initPinCallback;

        DeviceComm deviceComm = deviceMaps.get(deviceName);
        if (deviceComm == null) {
            deviceComm = new DeviceComm(deviceName);
            deviceMaps.put(deviceName, deviceComm);
        }
        if (deviceComm.mDeviceHandler == null) {
            deviceComm.mDeviceHandler = new DeviceHandler(deviceName);
        }
//        if ((deviceComm.initPinThread == null) || (deviceComm.initPinThread.getState() == Thread.State.TERMINATED)) {
        deviceComm.initPinThread = new BlueToothWrapper(deviceComm.mDeviceHandler);
        deviceComm.initPinThread.setInitPINWrapper(deviceComm.contextHandle,
                0, password);
        deviceComm.initialPswHint = passwordHint;
//            singleExecutor.execute(deviceComm.initPinThread);
        try {
            queue.put(deviceComm.initPinThread);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        }

    }


    public void verifyPin(String tag, String deviceName, String password, VerifyPinCallback verifyPinCallback) {
        if (!isDeviceConnectted(deviceName)||isDeviceFreeContext(deviceName)) {
            verifyPinCallback.onVerifyFail();
            return;
        }

        DeviceCallbacsBean deviceCallbacks = callbackMaps.get(tag);
        if (deviceCallbacks == null) {
            deviceCallbacks = new DeviceCallbacsBean();
            callbackMaps.put(tag, deviceCallbacks);
        }
        deviceCallbacks.verifyPinCallback = verifyPinCallback;

        DeviceComm deviceComm = deviceMaps.get(deviceName);
        if (deviceComm == null) {
            deviceComm = new DeviceComm(deviceName);
            deviceMaps.put(deviceName, deviceComm);
        }
        if (deviceComm.mDeviceHandler == null) {
            deviceComm.mDeviceHandler = new DeviceHandler(deviceName);
        }
//        if ((deviceComm.verifyPinThread == null) || (deviceComm.verifyPinThread.getState()
//                == Thread.State.TERMINATED)) {
        deviceComm.verifyPinThread = new BlueToothWrapper(deviceComm.mDeviceHandler);
        deviceComm.verifyPinThread.setVerifyPINWrapper(deviceComm.contextHandle,
                0, password);
//            singleExecutor.execute(deviceComm.verifyPinThread);
        try {
            queue.put(deviceComm.verifyPinThread);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        }

    }


    public void changePin(
            String tag,
            String deviceName,
            String oldPsw,
            String newPsw,
            ChangePinCallback changePinCallback) {
        if (!isDeviceConnectted(deviceName)||isDeviceFreeContext(deviceName)) {
            changePinCallback.onChangePinFail();
            return;
        }

        DeviceCallbacsBean deviceCallbacks = callbackMaps.get(tag);
        if (deviceCallbacks == null) {
            deviceCallbacks = new DeviceCallbacsBean();
            callbackMaps.put(tag, deviceCallbacks);
        }
        deviceCallbacks.changePinCallback = changePinCallback;

        DeviceComm deviceComm = deviceMaps.get(deviceName);
        if (deviceComm == null) {
            deviceComm = new DeviceComm(deviceName);
            deviceMaps.put(deviceName, deviceComm);
        }
        if (deviceComm.mDeviceHandler == null) {
            deviceComm.mDeviceHandler = new DeviceHandler(deviceName);
        }
//        if ((deviceComm.changePinThread == null) || (deviceComm.changePinThread.getState()
//                == Thread.State.TERMINATED)) {
        deviceComm.changePinThread = new BlueToothWrapper(deviceComm.mDeviceHandler);
        deviceComm.changePinThread.setChangePINWrapper(deviceComm.contextHandle,
                0, oldPsw, newPsw);
//            singleExecutor.execute(deviceComm.changePinThread);
        try {
            queue.put(deviceComm.changePinThread);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        }

    }


    /**
     * 调用中间件setGenerateSeedGetMnesWrapper来产生种子并由种子生成助记词
     */
    public void generateMnemonic(String tag, String deviceName, GenerateMnemonicCallback generateMnemonicCallback) {
        if (!isDeviceConnectted(deviceName)||isDeviceFreeContext(deviceName)) {
            generateMnemonicCallback.onGenerateFail();
            return;
        }
        DeviceCallbacsBean deviceCallbacks = callbackMaps.get(tag);
        if (deviceCallbacks == null) {
            deviceCallbacks = new DeviceCallbacsBean();
            callbackMaps.put(tag, deviceCallbacks);
        }
        deviceCallbacks.generateMnemonicCallback = generateMnemonicCallback;

        DeviceComm deviceComm = deviceMaps.get(deviceName);
        if (deviceComm == null) {
            deviceComm = new DeviceComm(deviceName);
            deviceMaps.put(deviceName, deviceComm);
        }
        if (deviceComm.mDeviceHandler == null) {
            deviceComm.mDeviceHandler = new DeviceHandler(deviceName);
        }
//        if ((deviceComm.generateMnemonicThread == null) || (deviceComm.generateMnemonicThread.getState()
//                == Thread.State.TERMINATED)) {
        deviceComm.generateMnemonicThread = new BlueToothWrapper(deviceComm.mDeviceHandler);
        deviceComm.generateMnemonicThread.setGenerateSeedGetMnesWrapper(deviceComm.contextHandle,
                0, 16);
//            singleExecutor.execute(deviceComm.generateMnemonicThread);
        try {
            queue.put(deviceComm.generateMnemonicThread);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        }
    }


    public int checkMnemonic(String deviceName, String strDestMnes) {
        if (!isDeviceConnectted(deviceName)||isDeviceFreeContext(deviceName)) {
            return -1;
        }
        DeviceComm deviceComm = deviceMaps.get(deviceName);
        if (deviceComm == null) {
            deviceComm = new DeviceComm(deviceName);
            deviceMaps.put(deviceName, deviceComm);
        }
        int status = MiddlewareInterface.generateSeed_CheckMnes(deviceComm.contextHandle, 0,
                strDestMnes);
        return status;
    }

    public void checkMnemonic(String tag, String deviceName, String strDestMnes,CheckMnemonicCallback checkMnemonicCallback) {
        if (!isDeviceConnectted(deviceName)||isDeviceFreeContext(deviceName)) {
            checkMnemonicCallback.onCheckFail(-1);
            return;
        }
        DeviceCallbacsBean deviceCallbacks = callbackMaps.get(tag);
        if (deviceCallbacks == null) {
            deviceCallbacks = new DeviceCallbacsBean();
            callbackMaps.put(tag, deviceCallbacks);
        }
        deviceCallbacks.checkMnemonicCallback = checkMnemonicCallback;

        DeviceComm deviceComm = deviceMaps.get(deviceName);
        if (deviceComm == null) {
            deviceComm = new DeviceComm(deviceName);
            deviceMaps.put(deviceName, deviceComm);
        }
        if (deviceComm.mDeviceHandler == null) {
            deviceComm.mDeviceHandler = new DeviceHandler(deviceName);
        }
        deviceComm.checkMnemonicThread = new BlueToothWrapper(deviceComm.mDeviceHandler);
        deviceComm.checkMnemonicThread.checkMnesWrapper(deviceComm.contextHandle,
                0, strDestMnes);
        try {
            queue.put(deviceComm.checkMnemonicThread);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void getFPList(String tag, String deviceName, GetFPListCallback getFPListCallback) {
        if (!isDeviceConnectted(deviceName)||isDeviceFreeContext(deviceName)) {
            getFPListCallback.onFail();
            return;
        }
        DeviceCallbacsBean deviceCallbacks = callbackMaps.get(tag);
        if (deviceCallbacks == null) {
            deviceCallbacks = new DeviceCallbacsBean();
            callbackMaps.put(tag, deviceCallbacks);
        }
        deviceCallbacks.getFPListCallback = getFPListCallback;

        DeviceComm deviceComm = deviceMaps.get(deviceName);
        if (deviceComm == null) {
            deviceComm = new DeviceComm(deviceName);
            deviceMaps.put(deviceName, deviceComm);
        }
        if (deviceComm.mDeviceHandler == null) {
            deviceComm.mDeviceHandler = new DeviceHandler(deviceName);
        }
//        if ((deviceComm.getFPListThread == null) || (deviceComm.getFPListThread.getState()
//                == Thread.State.TERMINATED)) {
        deviceComm.getFPListThread = new BlueToothWrapper(deviceComm.mDeviceHandler);
        deviceComm.getFPListThread.setGetFPListWrapper(deviceComm.contextHandle,
                0);
//            singleExecutor.execute(deviceComm.getFPListThread);
        try {
            queue.put(deviceComm.getFPListThread);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        }
    }


    public void deleteFp(
            String tag,
            String deviceName,
            MiddlewareInterface.FingerPrintID[] fpList,
            DeleteFPCallback deleteFPCallback) {

        if (!isDeviceConnectted(deviceName)||isDeviceFreeContext(deviceName)) {
            deleteFPCallback.onFail();
            return;
        }
        DeviceCallbacsBean deviceCallbacks = callbackMaps.get(tag);
        if (deviceCallbacks == null) {
            deviceCallbacks = new DeviceCallbacsBean();
            callbackMaps.put(tag, deviceCallbacks);
        }
        deviceCallbacks.deleteFPCallback = deleteFPCallback;

        DeviceComm deviceComm = deviceMaps.get(deviceName);
        if (deviceComm == null) {
            deviceComm = new DeviceComm(deviceName);
            deviceMaps.put(deviceName, deviceComm);
        }
        if (deviceComm.mDeviceHandler == null) {
            deviceComm.mDeviceHandler = new DeviceHandler(deviceName);
        }
//        if ((deviceComm.deleteFPThread == null) || (deviceComm.deleteFPThread.getState() == Thread.State.TERMINATED)) {
        deviceComm.deleteFPThread = new BlueToothWrapper(deviceComm.mDeviceHandler);
        deviceComm.deleteFPThread.setDeleteFPWrapper(deviceComm.contextHandle,
                0, fpList);
//            deviceComm.deleteFPThread.start();
//            singleExecutor.execute(deviceComm.deleteFPThread);
        try {
            queue.put(deviceComm.deleteFPThread);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        }


    }

    public void signEosTransaction(
            String tag,
            String deviceName,
            ReentrantLock uiLock,
            byte[] transaction,
            EosSignCallback eosSignCallback
    ) {

        if (!isDeviceConnectted(deviceName)||isDeviceFreeContext(deviceName)) {
            eosSignCallback.onEosSignFail();
            return;
        }
        DeviceCallbacsBean deviceCallbacks = callbackMaps.get(tag);
        if (deviceCallbacks == null) {
            deviceCallbacks = new DeviceCallbacsBean();
            callbackMaps.put(tag, deviceCallbacks);
        }

        deviceCallbacks.eosSignCallback = eosSignCallback;

        DeviceComm deviceComm = deviceMaps.get(deviceName);
        if (deviceComm == null) {
            deviceComm = new DeviceComm(deviceName);
            deviceMaps.put(deviceName, deviceComm);
        }

        if (deviceComm.mDeviceHandler == null) {
            deviceComm.mDeviceHandler = new DeviceHandler(deviceName);
        }
//        if ((deviceComm.eosSignThread == null) || (deviceComm.eosSignThread.getState()
//                == Thread.State.TERMINATED)) {
        deviceComm.eosSignThread = new BlueToothWrapper(deviceComm.mDeviceHandler);
        deviceComm.eosSignThread.setEOSSignWrapper(deviceComm.contextHandle, 0, uiLock, CacheConstants
                .EOS_DERIVE_PATH, transaction);
//            singleExecutor.execute(deviceComm.eosSignThread);
        try {
            queue.put(deviceComm.eosSignThread);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        }
    }


    public void getEthAddress(String tag, String deviceName, GetAddressCallback getAddressCallback) {
        if (!isDeviceConnectted(deviceName)||isDeviceFreeContext(deviceName)) {
            getAddressCallback.onGetFail();
            return;
        }
        DeviceCallbacsBean deviceCallbacks = callbackMaps.get(tag);
        if (deviceCallbacks == null) {
            deviceCallbacks = new DeviceCallbacsBean();
            callbackMaps.put(tag, deviceCallbacks);
        }
        deviceCallbacks.getEthAddressCallback = getAddressCallback;

        DeviceComm deviceComm = deviceMaps.get(deviceName);
        if (deviceComm == null) {
            deviceComm = new DeviceComm(deviceName);
            deviceMaps.put(deviceName, deviceComm);
        }
        if (deviceComm.mDeviceHandler == null) {
            deviceComm.mDeviceHandler = new DeviceHandler(deviceName);
        }
        int m_coinChoiceIndex = 1;
        deviceComm.getEthAddressThread = new BlueToothWrapper(deviceComm.mDeviceHandler);
        deviceComm.getEthAddressThread.setGetAddressWrapper(deviceComm.contextHandle,
                0, coinTypes[m_coinChoiceIndex], derivePaths[m_coinChoiceIndex]);
//        singleExecutor.execute(deviceComm.getEthAddressThread);
        try {
            queue.put(deviceComm.getEthAddressThread);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void getEosAddress(String tag, String deviceName, GetAddressCallback getAddressCallback) {
        if (!isDeviceConnectted(deviceName)||isDeviceFreeContext(deviceName)) {
            getAddressCallback.onGetFail();
            return;
        }
        DeviceCallbacsBean deviceCallbacks = callbackMaps.get(tag);
        if (deviceCallbacks == null) {
            deviceCallbacks = new DeviceCallbacsBean();
            callbackMaps.put(tag, deviceCallbacks);
        }
        deviceCallbacks.getEosAddressCallback = getAddressCallback;

        DeviceComm deviceComm = deviceMaps.get(deviceName);
        if (deviceComm == null) {
            deviceComm = new DeviceComm(deviceName);
            deviceMaps.put(deviceName, deviceComm);
        }
        if (deviceComm.mDeviceHandler == null) {
            deviceComm.mDeviceHandler = new DeviceHandler(deviceName);
        }
        int m_coinChoiceIndex = 0;
        deviceComm.getEosAddressThread = new BlueToothWrapper(deviceComm.mDeviceHandler);
        deviceComm.getEosAddressThread.setGetAddressWrapper(deviceComm.contextHandle,
                0, coinTypes[m_coinChoiceIndex], derivePaths[m_coinChoiceIndex]);
//        singleExecutor.execute(deviceComm.getEosAddressThread);
        try {
            queue.put(deviceComm.getEosAddressThread);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void getCheckCode(String tag, String deviceName, GetCheckCodeCallback getCheckCodeCallback) {
        if (!isDeviceConnectted(deviceName)||isDeviceFreeContext(deviceName)) {
            getCheckCodeCallback.onCheckCodeFail();
            return;
        }
        DeviceCallbacsBean deviceCallbacks = callbackMaps.get(tag);
        if (deviceCallbacks == null) {
            deviceCallbacks = new DeviceCallbacsBean();
            callbackMaps.put(tag, deviceCallbacks);
        }
        deviceCallbacks.getCheckCodeCallback = getCheckCodeCallback;

        DeviceComm deviceComm = deviceMaps.get(deviceName);
        if (deviceComm == null) {
            deviceComm = new DeviceComm(deviceName);
            deviceMaps.put(deviceName, deviceComm);
        }
        if (deviceComm.mDeviceHandler == null) {
            deviceComm.mDeviceHandler = new DeviceHandler(deviceName);
        }
        int m_coinChoiceIndex = 0;
        deviceComm.getCheckCodeThread = new BlueToothWrapper(deviceComm.mDeviceHandler);
        deviceComm.getCheckCodeThread.setGetCheckCodeWrapper(deviceComm.contextHandle,
                0);
//        singleExecutor.execute(deviceComm.getCheckCodeThread);
        try {
            queue.put(deviceComm.getCheckCodeThread);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //todo 目前写死了只set EOS tx
    public void setTx(String tag, String deviceName, byte[] transaction, SetTxCallback setTxCallback){
        if (!isDeviceConnectted(deviceName)||isDeviceFreeContext(deviceName)) {
            setTxCallback.onSetTxFail();
            return;
        }

        DeviceCallbacsBean deviceCallbacks = callbackMaps.get(tag);
        if (deviceCallbacks == null) {
            deviceCallbacks = new DeviceCallbacsBean();
            callbackMaps.put(tag, deviceCallbacks);
        }
        deviceCallbacks.setTxCallback = setTxCallback;

        DeviceComm deviceComm = deviceMaps.get(deviceName);
        if (deviceComm == null) {
            deviceComm = new DeviceComm(deviceName);
            deviceMaps.put(deviceName, deviceComm);
        }
        if (deviceComm.mDeviceHandler == null) {
            deviceComm.mDeviceHandler = new DeviceHandler(deviceName);
        }
        int m_coinChoiceIndex = 0;
        deviceComm.setTxThread = new BlueToothWrapper(deviceComm.mDeviceHandler);
        deviceComm.setTxThread.setSetTXWrapper(deviceComm.contextHandle,
                0, coinTypes[m_coinChoiceIndex], derivePaths[m_coinChoiceIndex],transaction);
        try {
            queue.put(deviceComm.setTxThread);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //todo 目前写死了只获取EOS Sign res
    public void getSignResult(String tag, String deviceName, byte signType, SetGetSignResultCallback
            setGetSignResultCallback){
        if (!isDeviceConnectted(deviceName)||isDeviceFreeContext(deviceName)) {
            setGetSignResultCallback.onGetSignResultFail(BaseConst.STATUS_NO_DEVICE_NAME);
            return;
        }

        DeviceCallbacsBean deviceCallbacks = callbackMaps.get(tag);
        if (deviceCallbacks == null) {
            deviceCallbacks = new DeviceCallbacsBean();
            callbackMaps.put(tag, deviceCallbacks);
        }
        deviceCallbacks.setGetSignResultCallback = setGetSignResultCallback;

        DeviceComm deviceComm = deviceMaps.get(deviceName);
        if (deviceComm == null) {
            deviceComm = new DeviceComm(deviceName);
            deviceMaps.put(deviceName, deviceComm);
        }
        if (deviceComm.mDeviceHandler == null) {
            deviceComm.mDeviceHandler = new DeviceHandler(deviceName);
        }
        int m_coinChoiceIndex = 0;
        deviceComm.getSignResultThread = new BlueToothWrapper(deviceComm.mDeviceHandler);
        deviceComm.getSignResultThread.setGetSignResultWrapper(deviceComm.contextHandle,
                0, coinTypes[m_coinChoiceIndex], signType);
        try {
            queue.put(deviceComm.getSignResultThread);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void switchSignType(String tag, String deviceName, SwitchSignCallback switchSignCallback){

        if (!isDeviceConnectted(deviceName)||isDeviceFreeContext(deviceName)) {
            switchSignCallback.onSwitchSignFail();
            return;
        }

        DeviceCallbacsBean deviceCallbacks = callbackMaps.get(tag);
        if (deviceCallbacks == null) {
            deviceCallbacks = new DeviceCallbacsBean();
            callbackMaps.put(tag, deviceCallbacks);
        }
        deviceCallbacks.switchSignCallback = switchSignCallback;

        DeviceComm deviceComm = deviceMaps.get(deviceName);
        if (deviceComm == null) {
            deviceComm = new DeviceComm(deviceName);
            deviceMaps.put(deviceName, deviceComm);
        }
        if (deviceComm.mDeviceHandler == null) {
            deviceComm.mDeviceHandler = new DeviceHandler(deviceName);
        }
        int m_coinChoiceIndex = 0;

        if ((deviceComm.getSignResultThread != null) && (deviceComm.getSignResultThread.getState() != Thread.State.TERMINATED)) {
           deviceComm.getSignResultThread.breakGetSignResultLoop();
        }

        deviceComm.switchSignThread = new BlueToothWrapper(deviceComm.mDeviceHandler);
        deviceComm.switchSignThread.setSwitchSignWrapper(deviceComm.contextHandle,
                0);
        try {
            queue.put(deviceComm.switchSignThread);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 在签名时验证PIN的方法
     */
    public void verifySignPin(String tag, String deviceName, String strPIN,
            VerifySignPinCallback verifySignPinCallback){

        if (!isDeviceConnectted(deviceName)||isDeviceFreeContext(deviceName)) {
            verifySignPinCallback.onVerifyFail();
            return;
        }

        DeviceCallbacsBean deviceCallbacks = callbackMaps.get(tag);
        if (deviceCallbacks == null) {
            deviceCallbacks = new DeviceCallbacsBean();
            callbackMaps.put(tag, deviceCallbacks);
        }
        deviceCallbacks.verifySignPinCallback = verifySignPinCallback;

        DeviceComm deviceComm = deviceMaps.get(deviceName);
        if (deviceComm == null) {
            deviceComm = new DeviceComm(deviceName);
            deviceMaps.put(deviceName, deviceComm);
        }
        if (deviceComm.mDeviceHandler == null) {
            deviceComm.mDeviceHandler = new DeviceHandler(deviceName);
        }
        int m_coinChoiceIndex = 0;

        deviceComm.verifySignPinThread = new BlueToothWrapper(deviceComm.mDeviceHandler);
        deviceComm.verifySignPinThread.setVerifySignPINWrapper(deviceComm.contextHandle,
                0, strPIN);
        try {
            queue.put(deviceComm.verifySignPinThread);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 在签名时断开的方法
     */
    public void abortSign(String tag, String deviceName, AbortSignCallback abortSignCallback){

        if (!isDeviceConnectted(deviceName)||isDeviceFreeContext(deviceName)) {
            if(abortSignCallback!=null){
                abortSignCallback.onAbortSignFail();
            }
            return;
        }

        DeviceCallbacsBean deviceCallbacks = callbackMaps.get(tag);
        if (deviceCallbacks == null) {
            deviceCallbacks = new DeviceCallbacsBean();
            callbackMaps.put(tag, deviceCallbacks);
        }
        deviceCallbacks.abortSignCallback = abortSignCallback;

        DeviceComm deviceComm = deviceMaps.get(deviceName);
        if (deviceComm == null) {
            deviceComm = new DeviceComm(deviceName);
            deviceMaps.put(deviceName, deviceComm);
        }
        if (deviceComm.mDeviceHandler == null) {
            deviceComm.mDeviceHandler = new DeviceHandler(deviceName);
        }


        if ((deviceComm.getSignResultThread != null) && (deviceComm.getSignResultThread.getState() != Thread.State.TERMINATED)) {
            deviceComm.getSignResultThread.breakGetSignResultLoop();
        }

//        if (!(deviceComm.getSignResultThread == null || deviceComm.getSignResultThread.getState() == Thread.State
//                .TERMINATED)){
//            (deviceComm.getSignResultThread).breakGetSignResultLoop();
        deviceComm.abortSignThread = new BlueToothWrapper(deviceComm.mDeviceHandler);
        deviceComm.abortSignThread.setAbortSignWrapper(deviceComm.contextHandle, 0);
        deviceComm.abortSignThread.start();
//        }
    }

    public interface DeviceConnectCallback {

        void onConnectStart();

        void onConnectSuccess();

        void onConnectFailed();
    }

    public interface ImportMnemonicCallback {
        void onImportSuccess();

        void onImportFailed();
    }


    public interface DeviceFormatCallback {

        void onFormatStart();

        void onFormatSuccess();

        void onFormatUpdate(int state);

        void onFormatFailed();
    }

    public interface PressConfirmCallback {

        void onConfirmSuccess();

        void onConfirmFailed();
    }

    public interface DeviceVerifyFPCallback {

        void onVerifyStart();

        void onVerifySuccess();

        void onVerifyFailed(int state);

        void onVerifyCancelled();
    }

    public interface FreeContextCallback {

        void onFreeStart();

        void onFreeSuccess();

        void onFreeFailed();
    }

    public interface EnrollFPCallback {

        void onEnrollFPUpate(int state);

        void onEnrollFinish(int state);

    }

    public interface JsonSerilizeCallback {

        void onSerilizeStart();

        void onSerilizeSuccess(String serializeResult);

        void onSerilizeFail();
    }

    public interface ScanDeviceCallback {

        void onScanStart();

        void onScanUpdate(String[] devices);

        void onScanFinish();
    }

    public interface GetDeviceInfoCallback {

        void onGetSuccess(MiddlewareInterface.PAEW_DevInfo deviceInfo);

        void onGetFail();
    }

    public interface InitPinCallback {

        void onInitSuccess();

        void onInitUpdate(int state);

        void onInitFail();
    }

    public interface VerifyPinCallback {

        void onVerifySuccess();

        void onPinLocked();

        void onVerifyFail();

    }

    public interface AbortFPCallback {

        void onAbortSuccess();

        void onAbortFail();
    }

    public interface AbortButtonCallback {

        void onAbortSuccess();

        void onAbortFail();
    }


    public interface ChangePinCallback {

        void onChangePinSuccess();

        void onChangePinUpdate(int state);

        void onChangePinFail();
    }

    public interface GenerateMnemonicCallback {

        void onGenerateSuccess(BlueToothWrapper.GenSeedMnesReturnValue mnemonic);

        void onGenerateFail();
    }

    public interface CheckMnemonicCallback {

        void onCheckSuccess();

        void onCheckFail(int state);
    }

    public interface GetFPListCallback {

        void onSuccess(BlueToothWrapper.GetFPListReturnValue fpListReturnValue);

        void onFail();
    }

    public interface DeleteFPCallback {

        void onSuccess();

        void onFail();
    }

    public interface EosSignCallback {

        void onEosSignStart();

        void onEosSignSuccess(String strSignature);

        void onEosSignFail();
    }


    public interface GetAddressCallback {

        void onGetSuccess(String address);

        void onGetFail();
    }

    public interface GetCheckCodeCallback {
        void onCheckCodeSuccess(byte[] checkCode);

        void onCheckCodeFail();
    }

    public interface SetTxCallback {
        void onSetTxStart();

        void onSetTxSuccess();

        void onSetTxFail();
    }

    public interface SetGetSignResultCallback {
        void onGetSignResultStart();

        void onGetSignResultSuccess(String strSignature);

        void onGetSignResultFail(int status);

        void onGetSignResultUpdate(int errCode);
    }

    public interface SwitchSignCallback{
        void onSwitchSignSuccess();

        void onSwitchSignFail();
    }

    public interface VerifySignPinCallback{
        void onVerifySuccess();

        void onVerifyFail();

        void onVerifyOvertime();
    }

    public interface AbortSignCallback{
        void onAbortSignSuccess();

        void onAbortSignFail();


    }

    public interface GetBatteryCallback {
        void onSuccess(byte[] value);

        void onFail();
    }


    class HeartBeatHandler extends Handler {

        private String deviceName;

        public HeartBeatHandler(String deviceName) {
            this.deviceName = deviceName;
        }


        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Set<String> tags = callbackMaps.keySet();
            Iterator<String> iterator;

            switch (msg.what) {
                case BlueToothWrapper.MSG_GET_DEV_INFO_START:
                    LoggerManager.e("heart MSG_GET_DEV_INFO_START heart");
                    break;
                case BlueToothWrapper.MSG_GET_DEV_INFO_FINISH:
                    LoggerManager.e("heart MSG_GET_DEV_INFO_FINISH heart");
                    //获得设备信息
                    BlueToothWrapper.GetDevInfoReturnValue reValue = (BlueToothWrapper.GetDevInfoReturnValue) msg.obj;
//                    LoggerManager.d("MSG_GET_DEV_INFO_FINISH  heart rtValue=" + MiddlewareInterface.getReturnString(
//                            reValue.getReturnValue()));
                    if (reValue.getReturnValue() == MiddlewareInterface.PAEW_RET_SUCCESS) {
                        MiddlewareInterface.PAEW_DevInfo devInfo = reValue.getDeviceInfo();
//                        if (devInfo.ucLifeCycle == DEVICE_LIFE_CYCLE_PRODUCE) {
//                            //在全新（或已Format）的设备上
//                            deviceNameList.get(updatePosition).isShowProgress = false;
//                            deviceNameList.get(updatePosition).status = 0;
//                            mAdapter.notifyDataSetChanged();
//
//                        } else if (devInfo.ucLifeCycle == DEVICE_LIFE_CYCLE_USER) {
//                            //在InitPIN之后，LifeCycle变为User
//                            deviceNameList.get(updatePosition).isShowProgress = false;
//                            deviceNameList.get(updatePosition).status = 1;
//                            mAdapter.notifyDataSetChanged();
//
//                            WookongBioManager.getInstance().getFPList(contextHandle, 0);
//                        }
                        DeviceComm deviceComm = deviceMaps.get(deviceName);
                        if (deviceComm != null) {
                            if (deviceComm.deviceInfo != null) {
                                if (deviceComm.deviceInfo.ucPINState != 0x02 && deviceComm.deviceInfo.ucPINState == 0x02) {
                                    //pin locked,send event
                                    LoggerManager.e("pin locked,send event...");
//                                    EventBusProvider.post(new PinLockedEvent(deviceName));
                                }
                            }
                            deviceComm.deviceInfo = devInfo;
                        }

                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).getHeartDeviceInfoCallback != null) {
                                GetDeviceInfoCallback getHeartDeviceInfoCallback = callbackMaps.get(tag).getHeartDeviceInfoCallback;
                                getHeartDeviceInfoCallback.onGetSuccess(devInfo);
                                if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).getHeartDeviceInfoCallback==getHeartDeviceInfoCallback){
                                    callbackMaps.get(tag).getHeartDeviceInfoCallback=null;
                                }
                            }
                        }

                    } else {
                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).getHeartDeviceInfoCallback != null) {
                                GetDeviceInfoCallback getHeartDeviceInfoCallback = callbackMaps.get(tag).getHeartDeviceInfoCallback;
                                getHeartDeviceInfoCallback.onGetFail();
                                if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).getHeartDeviceInfoCallback==getHeartDeviceInfoCallback){
                                    callbackMaps.get(tag).getHeartDeviceInfoCallback=null;
                                }
                            }
                        }
                    }
                    break;
                default:
                    break;

            }

        }
    }

    class ScanHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Set<String> tags = callbackMaps.keySet();
            Iterator<String> iterator;

            switch (msg.what) {
                case BlueToothWrapper.MSG_ENUM_START:
                    //开始一次新的扫描
//                    viewSpinKit.setVisibility(View.VISIBLE);
//                    deviceNameList.clear();
//                    mAdapter.notifyDataSetChanged();
//                    statusView.showLoading();


                    iterator = tags.iterator();
                    while (iterator.hasNext()) {
                        String tag = iterator.next();
                        if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).scanDeviceCallback != null) {
                            callbackMaps.get(tag).scanDeviceCallback.onScanStart();
                        }
                    }

                    break;
                case BlueToothWrapper.MSG_ENUM_UPDATE:
                    //更新蓝牙设备列表
                    String[] devNames = (String[]) msg.obj;
                    LoggerManager.d("MSG_ENUM_UPDATE  devNames:" + Arrays.toString(devNames));
//                    if (EmptyUtils.isNotEmpty(devNames)) {
//                        for (int i = 0; i < devNames.length; i++) {
//                            String deviceName = devNames[i];
//                            if (deviceName.contains(DEVICE_PREFIX)) {
//                                BluetoothDeviceVO vo = new BluetoothDeviceVO();
//                                vo.deviceName = deviceName;
//                                deviceNameList.add(vo);
//                            }
//                        }
//
//                    }
//
//                    if (EmptyUtils.isEmpty(deviceNameList)) {
//                        if (statusView != null) {
//                            showConnectBioFailDialog();
//                        }
//                    } else {
//                        if (statusView != null) {
//                            statusView.showContent();
//                        }
//                    }

                    iterator = tags.iterator();
                    while (iterator.hasNext()) {
                        String tag = iterator.next();
                        if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).scanDeviceCallback != null) {
                            callbackMaps.get(tag).scanDeviceCallback.onScanUpdate(devNames);
                        }
                    }
                    break;
                case BlueToothWrapper.MSG_ENUM_FINISH:
                    iterator = tags.iterator();
                    while (iterator.hasNext()) {
                        String tag = iterator.next();
                        if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).scanDeviceCallback != null) {
                            ScanDeviceCallback scanDeviceCallback = callbackMaps.get(tag).scanDeviceCallback;
                            scanDeviceCallback.onScanFinish();
                            if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).scanDeviceCallback==scanDeviceCallback){
                                callbackMaps.get(tag).scanDeviceCallback=null;
                            }
                        }
                    }
                    break;
                default:
                    break;

            }

        }
    }


    class DeviceHandler extends Handler {

        private String deviceName;

        public DeviceHandler(String deviceName) {
            this.deviceName = deviceName;
        }

        @Override
        public void handleMessage(Message msg) {

            if (deviceMaps.get(deviceName) == null) return;

            Set<String> tags = callbackMaps.keySet();
            Iterator<String> iterator;

            switch (msg.what) {

                case BlueToothWrapper.MSG_INIT_PIN_START:
                    LoggerManager.d("MSG_INIT_PIN_START");
                    //设置PIN
                    break;

                case BlueToothWrapper.MSG_INIT_PIN_UPDATE:
                    LoggerManager.d("MSG_INIT_PIN_UPDATE status="+MiddlewareInterface.getReturnString(msg.arg1));
                    iterator = tags.iterator();
                    while (iterator.hasNext()) {
                        String tag = iterator.next();
                        if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).initPinCallback != null) {
                            callbackMaps.get(tag).initPinCallback.onInitUpdate(msg.arg1);
                        }
                    }
                    break;
                case BlueToothWrapper.MSG_INIT_PIN_FINISH:
                    LoggerManager.d("MSG_INIT_PIN_FINISH status=" + MiddlewareInterface.getReturnString(msg.arg1));
                    //已完成设置PIN
                    if (msg.arg1 == MiddlewareInterface.PAEW_RET_SUCCESS) {
                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).initPinCallback != null) {
                                InitPinCallback initPinCallback = callbackMaps.get(tag).initPinCallback;
                                initPinCallback.onInitSuccess();
                                if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).initPinCallback==initPinCallback){
                                    callbackMaps.get(tag).initPinCallback=null;
                                }
                            }
                        }
                    } else {
                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).initPinCallback != null) {
                                InitPinCallback initPinCallback = callbackMaps.get(tag).initPinCallback;
                                initPinCallback.onInitFail();
                                if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).initPinCallback==initPinCallback){
                                    callbackMaps.get(tag).initPinCallback=null;
                                }
                            }
                        }
                    }
                    break;

                //verify pin
                case BlueToothWrapper.MSG_VERIFY_PIN_START:
                    LoggerManager.d("MSG_VERIFY_PIN_START");
                    //设置PIN
                    break;
                case BlueToothWrapper.MSG_VERIFY_PIN_FINISH:
                    LoggerManager.d("MSG_VERIFY_PIN_FINISH status=" + MiddlewareInterface.getReturnString(msg.arg1));
                    //已完成设置PIN
                    if (msg.arg1 == MiddlewareInterface.PAEW_RET_SUCCESS) {
                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).verifyPinCallback != null) {
                                VerifyPinCallback verifyPinCallback = callbackMaps.get(tag).verifyPinCallback;
                                verifyPinCallback.onVerifySuccess();
                                if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).verifyPinCallback==verifyPinCallback){
                                    callbackMaps.get(tag).verifyPinCallback=null;
                                }
                            }
                        }
                    }else if (msg.arg1 == MiddlewareInterface.PAEW_RET_DEV_PIN_LOCKED) {
                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).verifyPinCallback != null) {
                                VerifyPinCallback verifyPinCallback = callbackMaps.get(tag).verifyPinCallback;
                                verifyPinCallback.onPinLocked();
                                if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).verifyPinCallback==verifyPinCallback){
                                    callbackMaps.get(tag).verifyPinCallback=null;
                                }
                            }
                        }
                    } else {
                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).verifyPinCallback != null) {
                                VerifyPinCallback verifyPinCallback = callbackMaps.get(tag).verifyPinCallback;
                                verifyPinCallback.onVerifyFail();
                                if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).verifyPinCallback==verifyPinCallback){
                                    callbackMaps.get(tag).verifyPinCallback=null;
                                }
                            }
                        }
                    }
                    break;

                case BlueToothWrapper.MSG_INIT_CONTEXT_START:
                    LoggerManager.d("MSG_INIT_CONTEXT_START");

                    iterator = tags.iterator();
                    while (iterator.hasNext()) {
                        String tag = iterator.next();
                        if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).connectCallback != null) {
                            callbackMaps.get(tag).connectCallback.onConnectStart();
                        }
                    }

                    break;
                case BlueToothWrapper.MSG_INIT_CONTEXT_FINISH:
                    //连接完成
                    BlueToothWrapper.InitContextReturnValue returnValueConnect = (BlueToothWrapper
                            .InitContextReturnValue) msg.obj;
                    LoggerManager.d("MSG_INIT_CONTEXT_FINISH  rtValue=" + MiddlewareInterface.getReturnString(
                            returnValueConnect.getReturnValue()));
                    if ((returnValueConnect != null) && (returnValueConnect.getReturnValue()
                            == MiddlewareInterface.PAEW_RET_SUCCESS)) {
                        //连接成功
//                        SPUtils.getInstance()
//                                .put(CacheConstants.BIO_CONNECT_STATUS, CacheConstants.STATUS_BLUETOOTH_CONNCETED);
                        deviceMaps.get(deviceName).currentState = CacheConstants.STATUS_BLUETOOTH_CONNCETED;
                        deviceMaps.get(deviceName).contextHandle = returnValueConnect.getContextHandle();

                        currentDeviceName = deviceName;

                        EventBusProvider.post(
                                new DeviceConnectStatusUpdateEvent(CacheConstants.STATUS_BLUETOOTH_CONNCETED,
                                        deviceName));

                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).connectCallback != null) {
                                DeviceConnectCallback connectCallback = callbackMaps.get(tag).connectCallback;
                                connectCallback.onConnectSuccess();
                                if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).connectCallback==connectCallback){
                                    callbackMaps.get(tag).connectCallback=null;
                                }
                            }
                        }
                    } else {
                        //连接超时或失败
//                        SPUtils.getInstance()
//                                .put(CacheConstants.BIO_CONNECT_STATUS, CacheConstants.STATUS_BLUETOOTH_DISCONNCETED);
                        deviceMaps.get(deviceName).currentState = CacheConstants.STATUS_BLUETOOTH_DISCONNCETED;
                        deviceMaps.get(deviceName).msgBackConnectStatus = false;
                        EventBusProvider.post(new DeviceConnectStatusUpdateEvent(DeviceConnectStatusUpdateEvent.STATUS_BLUETOOTH_DISCONNCETED, deviceName));


                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).connectCallback != null) {
                                DeviceConnectCallback connectCallback = callbackMaps.get(tag).connectCallback;
                                connectCallback.onConnectFailed();
                                if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).connectCallback==connectCallback){
                                    callbackMaps.get(tag).connectCallback=null;
                                }
                            }
                        }
                        if (deviceMaps.get(deviceName).connectThread != null) {
                            deviceMaps.get(deviceName).connectThread.interrupt();
                        }
                        deviceMaps.get(deviceName).connectThread = null;
                    }

                    break;


                //heart beat
                case BlueToothWrapper.MSG_HEART_BEAT_DATA_UPDATE:
                    //LoggerManager.d("MSG_HEART_BEAT_DATA_UPDATE ");
                    if (msg.obj != null) {
                        byte[] heartBeatData = (byte[]) msg.obj;
                        if(heartBeatData!=null&&heartBeatData.length>=3){
                            DeviceComm deviceComm = deviceMaps.get(deviceName);
                            if (deviceComm != null) {
                                deviceComm.batteryMode = (heartBeatData[1] == 0x00) ? 0 : 1;
                                deviceComm.powerAmount = Integer.parseInt(String.format("%02x", heartBeatData[2]), 16);
//                            deviceComm.powerAmount=(int) heartBeatData[2];
                                //LoggerManager.d("MSG_HEART_BEAT_DATA_UPDATE batteryMode=" + deviceComm.batteryMode);
                                //LoggerManager.d("MSG_HEART_BEAT_DATA_UPDATE powerAmount=" + deviceComm.powerAmount);
                                EventBusProvider.post(new HeartBeatRefreshDataEvent());
                            }
                        }
                    }
                    break;
                case BlueToothWrapper.MSG_CONNECT_STATE_UPDATE:


//                    LoggerManager.d("MSG_CONNECT_STATE_UPDATE msg.obj="+msg.obj );
                    if ((boolean) msg.obj) {
                        //还在连接状态
                        DeviceComm deviceComm = deviceMaps.get(deviceName);
                        if(deviceComm!=null){
                            deviceComm.msgBackConnectStatus=(boolean)msg.obj;
                        }
                        if(deviceComm!=null&&deviceComm.currentState == CacheConstants.STATUS_BLUETOOTH_DISCONNCETED){
                            LoggerManager.e("MSG_CONNECT_STATE_UPDATE 设备未连接状态下,收到了连接状态的心跳" );
                        }
                    } else {
                        //todo,设备强制断开
                        DeviceComm deviceComm = deviceMaps.get(deviceName);
                        if (deviceComm != null) {
                            if (deviceComm.msgBackConnectStatus&&deviceComm.currentState == CacheConstants.STATUS_BLUETOOTH_CONNCETED) {
                                //manual get device info ,if fail ,do disconnect action
                                LoggerManager.e("receive back disconnect msg,do disconnect action isFreeContexting="+deviceComm.isFreeContexting );
//                                getDeviceInfo(this.toString(), deviceName, new GetDeviceInfoCallback() {
//                                    @Override
//                                    public void onGetSuccess(MiddlewareInterface.PAEW_DevInfo deviceInfo) {
//
//                                    }
//
//                                    @Override
//                                    public void onGetFail() {
//                                        deviceComm.currentState = CacheConstants.STATUS_BLUETOOTH_DISCONNCETED;
//                                        EventBusProvider.post(new DeviceConnectStatusUpdateEvent(DeviceConnectStatusUpdateEvent.STATUS_BLUETOOTH_DISCONNCETED, deviceName, false));
////                                        freeContext();
//                                    }
//                                });

                                if(deviceComm.isFreeContexting)return;
                                deviceMaps.get(deviceName).currentState = CacheConstants.STATUS_BLUETOOTH_DISCONNCETED;
                                deviceMaps.get(deviceName).contextHandle=0;
                                queue.clear();
                                boolean isManualFree = false;
                                EventBusProvider.post(
                                        new DeviceConnectStatusUpdateEvent(CacheConstants.STATUS_BLUETOOTH_DISCONNCETED,
                                                deviceName, isManualFree));
                                singleExecutor.shutdownNow();
                                singleExecutor = Executors.newSingleThreadExecutor();
                            }
                            deviceComm.msgBackConnectStatus=false;
                        }
                    }
                    break;


                case BlueToothWrapper.MSG_GET_DEV_INFO_START:
                    LoggerManager.d("MSG_GET_DEV_INFO_START");
                    break;
                case BlueToothWrapper.MSG_GET_DEV_INFO_FINISH:

                    //获得设备信息
                    BlueToothWrapper.GetDevInfoReturnValue reValue = (BlueToothWrapper.GetDevInfoReturnValue) msg.obj;
                    LoggerManager.d("MSG_GET_DEV_INFO_FINISH  rtValue=" + MiddlewareInterface.getReturnString(
                            reValue.getReturnValue()));
                    if (reValue.getReturnValue() == MiddlewareInterface.PAEW_RET_SUCCESS) {
                        MiddlewareInterface.PAEW_DevInfo devInfo = reValue.getDeviceInfo();
//                        if (devInfo.ucLifeCycle == DEVICE_LIFE_CYCLE_PRODUCE) {
//                            //在全新（或已Format）的设备上
//                            deviceNameList.get(updatePosition).isShowProgress = false;
//                            deviceNameList.get(updatePosition).status = 0;
//                            mAdapter.notifyDataSetChanged();
//
//                        } else if (devInfo.ucLifeCycle == DEVICE_LIFE_CYCLE_USER) {
//                            //在InitPIN之后，LifeCycle变为User
//                            deviceNameList.get(updatePosition).isShowProgress = false;
//                            deviceNameList.get(updatePosition).status = 1;
//                            mAdapter.notifyDataSetChanged();
//
//                            WookongBioManager.getInstance().getFPList(contextHandle, 0);
//                        }
                        DeviceComm deviceComm = deviceMaps.get(deviceName);
                        if (deviceComm != null) {
                            if (deviceComm.deviceInfo != null) {
                                if (deviceComm.deviceInfo.ucPINState != 0x02 && deviceComm.deviceInfo.ucPINState == 0x02) {
                                    //pin locked,send event
                                    LoggerManager.e("pin locked,send event...");
                                    EventBusProvider.post(new PinLockedEvent(deviceName));
                                }
                            }
                            deviceComm.deviceInfo = devInfo;
                        }

                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).getDeviceInfoCallback != null) {
                                GetDeviceInfoCallback getDeviceInfoCallback = callbackMaps.get(tag).getDeviceInfoCallback;
                                getDeviceInfoCallback.onGetSuccess(devInfo);
                                if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).getDeviceInfoCallback==getDeviceInfoCallback){
                                    callbackMaps.get(tag).getDeviceInfoCallback=null;
                                }
                            }
                        }

                    } else {
                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).getDeviceInfoCallback != null) {
                                GetDeviceInfoCallback getDeviceInfoCallback = callbackMaps.get(tag).getDeviceInfoCallback;
                                getDeviceInfoCallback.onGetFail();
                                if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).getDeviceInfoCallback==getDeviceInfoCallback){
                                    callbackMaps.get(tag).getDeviceInfoCallback=null;
                                }
                            }
                        }
                    }
                    break;


                //MSG_IMPORT_MNE_FINISH
                case BlueToothWrapper.MSG_IMPORT_MNE_FINISH:
                    LoggerManager.d("MSG_IMPORT_MNE_FINISH status=" + MiddlewareInterface.getReturnString(msg.arg1));
                    //已完成设置PIN
                    if (msg.arg1 == MiddlewareInterface.PAEW_RET_SUCCESS) {
                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).importMnemonicCallback != null) {
                                ImportMnemonicCallback importMnemonicCallback = callbackMaps.get(tag).importMnemonicCallback;
                                importMnemonicCallback.onImportSuccess();
                                if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).importMnemonicCallback==importMnemonicCallback){
                                    callbackMaps.get(tag).importMnemonicCallback=null;
                                }
                            }
                        }
                    } else {
                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).importMnemonicCallback != null) {
                                ImportMnemonicCallback importMnemonicCallback = callbackMaps.get(tag).importMnemonicCallback;
                                importMnemonicCallback.onImportFailed();
                                if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).importMnemonicCallback==importMnemonicCallback){
                                    callbackMaps.get(tag).importMnemonicCallback=null;
                                }
                            }
                        }
                    }


                    break;
                //free
                case BlueToothWrapper.MSG_FORMAT_DEVICE_START:
                    LoggerManager.d("MSG_FORMAT_DEVICE_START");
                    //格式化开始
                    iterator = tags.iterator();
                    while (iterator.hasNext()) {
                        String tag = iterator.next();
                        if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).formatCallback != null) {
                            callbackMaps.get(tag).formatCallback.onFormatStart();
                        }
                    }
                    break;
                case BlueToothWrapper.MSG_FORMAT_DEVICE_UPDATE:
                    LoggerManager.d("MSG_FORMAT_DEVICE_UPDATE  state=" + MiddlewareInterface.getReturnString(msg.arg1));
                    //格式化开始
                    iterator = tags.iterator();
                    while (iterator.hasNext()) {
                        String tag = iterator.next();
                        if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).formatCallback != null) {
                            callbackMaps.get(tag).formatCallback.onFormatUpdate(msg.arg1);
                        }
                    }
                    break;
                case BlueToothWrapper.MSG_FORMAT_DEVICE_FINISH:
                    LoggerManager.d(
                            "MSG_FORMAT_DEVICE_FINISH  state=" + MiddlewareInterface.getReturnString(msg.arg1));
                    if (msg.arg1 == MiddlewareInterface.PAEW_RET_SUCCESS) {
                        //格式化完成
                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).formatCallback != null) {
                                DeviceFormatCallback formatCallback = callbackMaps.get(tag).formatCallback;
                                formatCallback.onFormatSuccess();
                                if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).formatCallback==formatCallback){
                                    callbackMaps.get(tag).formatCallback=null;
                                }
                            }
                        }
                    } else {
                        //格式化完成
                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).formatCallback != null) {
                                DeviceFormatCallback formatCallback = callbackMaps.get(tag).formatCallback;
                                formatCallback.onFormatFailed();
                                if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).formatCallback==formatCallback){
                                    callbackMaps.get(tag).formatCallback=null;
                                }
                            }
                        }
                    }
                    break;


                //verify fp
                case BlueToothWrapper.MSG_VERIFYFP_START:
                    LoggerManager.d("MSG_VERIFYFP_START");
                    //验证指纹开始
                    iterator = tags.iterator();
                    while (iterator.hasNext()) {
                        String tag = iterator.next();
                        if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).verifyFPCallback != null) {
                            callbackMaps.get(tag).verifyFPCallback.onVerifyStart();
                        }
                    }
                    break;

                case BlueToothWrapper.MSG_VERIFYFP_FINISH:
                    BlueToothWrapper.GetFPListReturnValue verifyFpReturnValue = (BlueToothWrapper.GetFPListReturnValue) msg.obj;
                    LoggerManager.d("MSG_VERIFYFP_FINISH  state=" + MiddlewareInterface.getReturnString(
                            verifyFpReturnValue.getReturnValue()));
                    if (verifyFpReturnValue.getReturnValue() == MiddlewareInterface.PAEW_RET_SUCCESS) {
                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).verifyFPCallback != null) {
                                DeviceVerifyFPCallback verifyFPCallback = callbackMaps.get(tag).verifyFPCallback;
                                verifyFPCallback.onVerifySuccess();
                                if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).verifyFPCallback==verifyFPCallback){
                                    callbackMaps.get(tag).verifyFPCallback=null;
                                }
                            }
                        }
                    } else if (verifyFpReturnValue.getReturnValue() == MiddlewareInterface.PAEW_RET_DEV_OP_CANCEL) {
                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).verifyFPCallback != null) {
                                DeviceVerifyFPCallback verifyFPCallback = callbackMaps.get(tag).verifyFPCallback;
                                verifyFPCallback.onVerifyCancelled();
                                if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).verifyFPCallback==verifyFPCallback){
                                    callbackMaps.get(tag).verifyFPCallback=null;
                                }
                            }
                        }
                    }else {
                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).verifyFPCallback != null) {
                                DeviceVerifyFPCallback verifyFPCallback = callbackMaps.get(tag).verifyFPCallback;
                                verifyFPCallback.onVerifyFailed(verifyFpReturnValue.getReturnValue());
                                if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).verifyFPCallback==verifyFPCallback){
                                    callbackMaps.get(tag).verifyFPCallback=null;
                                }
                            }
                        }
                    }
                    break;
                case BlueToothWrapper.MSG_ABORT_FP_START:
                    LoggerManager.d(
                            "MSG_ABORT_FP_START  ");
                    break;
                    //MSG_ABORT_FP_FINISH
                case BlueToothWrapper.MSG_ABORT_FP_FINISH:
                    LoggerManager.d(
                            "MSG_ABORT_FP_FINISH  state=" + MiddlewareInterface.getReturnString(msg.arg1));
                    if (msg.arg1 == MiddlewareInterface.PAEW_RET_SUCCESS) {
                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).abortFPCallback != null) {
                                AbortFPCallback abortFPCallback = callbackMaps.get(tag).abortFPCallback;
                                abortFPCallback.onAbortSuccess();
                                if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).abortFPCallback==abortFPCallback){
                                    callbackMaps.get(tag).abortFPCallback=null;
                                }
                            }
                        }
                    } else {
                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).abortFPCallback != null) {
                                AbortFPCallback abortFPCallback = callbackMaps.get(tag).abortFPCallback;
                                abortFPCallback.onAbortFail();
                                if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).abortFPCallback==abortFPCallback){
                                    callbackMaps.get(tag).abortFPCallback=null;
                                }
                            }
                        }
                    }
                    break;



                case BlueToothWrapper.MSG_CHANGE_PIN_UPDATE:
                    LoggerManager.d(
                            "MSG_CHANGE_PIN_UPDATE  state=" + MiddlewareInterface.getReturnString(msg.arg1));
                    iterator = tags.iterator();
                    while (iterator.hasNext()) {
                        String tag = iterator.next();
                        if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).changePinCallback != null) {
                            callbackMaps.get(tag).changePinCallback.onChangePinUpdate(msg.arg1);
                        }
                    }
                    break;

                //MSG_CHANGE_PIN_FINISH
                case BlueToothWrapper.MSG_CHANGE_PIN_FINISH:
                    int stateChangePin = msg.arg1;
                    LoggerManager.d(
                            "MSG_CHANGE_PIN_FINISH  state=" + MiddlewareInterface.getReturnString(stateChangePin));
                    if (msg.arg1 == MiddlewareInterface.PAEW_RET_SUCCESS) {
                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).changePinCallback != null) {
                                ChangePinCallback changePinCallback = callbackMaps.get(tag).changePinCallback;
                                changePinCallback.onChangePinSuccess();
                                if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).changePinCallback==changePinCallback){
                                    callbackMaps.get(tag).changePinCallback=null;
                                }

                            }
                        }
                    } else {
                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).changePinCallback != null) {
                                ChangePinCallback changePinCallback = callbackMaps.get(tag).changePinCallback;
                                changePinCallback.onChangePinFail();
                                if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).changePinCallback==changePinCallback){
                                    callbackMaps.get(tag).changePinCallback=null;
                                }
                            }
                        }
                    }
                    break;


                //free context
                case BlueToothWrapper.MSG_FREE_CONTEXT_START:
                    LoggerManager.d("MSG_FREE_CONTEXT_START");
                    deviceMaps.get(deviceName).isFreeContexting=true;
//                    queue.clear();
                    iterator = tags.iterator();
                    while (iterator.hasNext()) {
                        String tag = iterator.next();
                        if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).freeContextCallback != null) {
                            callbackMaps.get(tag).freeContextCallback.onFreeStart();
                        }
                    }

                    break;
                case BlueToothWrapper.MSG_FREE_CONTEXT_FINISH:
                    LoggerManager.d("MSG_FREE_CONTEXT_FINISH");
//                    SPUtils.getInstance()
//                            .put(CacheConstants.BIO_CONNECT_STATUS, CacheConstants.STATUS_BLUETOOTH_DISCONNCETED);
                    deviceMaps.get(deviceName).currentState = CacheConstants.STATUS_BLUETOOTH_DISCONNCETED;
                    deviceMaps.get(deviceName).contextHandle=0;
                    queue.clear();
                    boolean isManualFree = deviceMaps.get(deviceName).isManualFree;
                    EventBusProvider.post(
                            new DeviceConnectStatusUpdateEvent(CacheConstants.STATUS_BLUETOOTH_DISCONNCETED,
                                    deviceName, isManualFree));

                    iterator = tags.iterator();
                    while (iterator.hasNext()) {
                        String tag = iterator.next();
                        if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).freeContextCallback != null) {
                            FreeContextCallback freeContextCallback = callbackMaps.get(tag).freeContextCallback;
                            freeContextCallback.onFreeSuccess();
                            if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).freeContextCallback==freeContextCallback){
                                callbackMaps.get(tag).freeContextCallback=null;
                            }
                        }
                    }
                    singleExecutor.shutdownNow();
                    singleExecutor = Executors.newSingleThreadExecutor();
                    deviceMaps.get(deviceName).isFreeContexting=false;
                    break;


                //serialize
                case BlueToothWrapper.MSG_EOS_SERIALIZE_START:
                    LoggerManager.d("MSG_EOS_SERIALIZE_START");

                    iterator = tags.iterator();
                    while (iterator.hasNext()) {
                        String tag = iterator.next();
                        if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).jsonSerilizeCallback != null) {
                            callbackMaps.get(tag).jsonSerilizeCallback.onSerilizeStart();
                        }
                    }
                    break;
                case BlueToothWrapper.MSG_EOS_SERIALIZE_FINISH:
                    LoggerManager.d("MSG_EOS_SERIALIZE_FINISH");

                    BlueToothWrapper.EOSTxSerializeReturn returnValue = (BlueToothWrapper.EOSTxSerializeReturn) msg.obj;
                    if (returnValue.getReturnValue() == MiddlewareInterface.PAEW_RET_SUCCESS) {
                        //序列化成功
                        String serializedStr = CommonUtility.byte2hex(returnValue.getSerializeData());
                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).jsonSerilizeCallback != null) {
                                JsonSerilizeCallback jsonSerilizeCallback = callbackMaps.get(tag).jsonSerilizeCallback;
                                jsonSerilizeCallback.onSerilizeSuccess(serializedStr);
                                if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).jsonSerilizeCallback==jsonSerilizeCallback){
                                    callbackMaps.get(tag).jsonSerilizeCallback=null;
                                }
                            }
                        }
                    } else {
                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).jsonSerilizeCallback != null) {
                                JsonSerilizeCallback jsonSerilizeCallback = callbackMaps.get(tag).jsonSerilizeCallback;
                                jsonSerilizeCallback.onSerilizeFail();
                                if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).jsonSerilizeCallback==jsonSerilizeCallback){
                                    callbackMaps.get(tag).jsonSerilizeCallback=null;
                                }
                            }
                        }
                    }
                    LoggerManager.d(
                            "Return Value: " + MiddlewareInterface.getReturnString(returnValue.getReturnValue()));
                    break;


                //generate mnemonic
                case BlueToothWrapper.MSG_GENERATE_SEED_MNES_FINISH:

                    LoggerManager.d("MSG_GENERATE_SEED_MNES_FINISH  state="+MiddlewareInterface.getReturnString(msg.arg1));

                    if (msg.arg1 == MiddlewareInterface.PAEW_RET_SUCCESS) {
                        BlueToothWrapper.GenSeedMnesReturnValue value = (BlueToothWrapper.GenSeedMnesReturnValue) msg.obj;
                        if (EmptyUtils.isNotEmpty(value)) {
                            String[] mnes = value.getStrMneWord();
                            iterator = tags.iterator();
                            while (iterator.hasNext()) {
                                String tag = iterator.next();
                                if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).generateMnemonicCallback != null) {
                                    GenerateMnemonicCallback generateMnemonicCallback = callbackMaps.get(tag).generateMnemonicCallback;
                                    generateMnemonicCallback.onGenerateSuccess(value);
                                    if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).generateMnemonicCallback==generateMnemonicCallback){
                                        callbackMaps.get(tag).generateMnemonicCallback=null;
                                    }
                                }
                            }
                        } else {
                            iterator = tags.iterator();
                            while (iterator.hasNext()) {
                                String tag = iterator.next();
                                if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).generateMnemonicCallback != null) {
                                    GenerateMnemonicCallback generateMnemonicCallback = callbackMaps.get(tag).generateMnemonicCallback;
                                    generateMnemonicCallback.onGenerateFail();
                                    if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).generateMnemonicCallback==generateMnemonicCallback){
                                        callbackMaps.get(tag).generateMnemonicCallback=null;
                                    }
                                }
                            }
                        }
                    } else {
                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).generateMnemonicCallback != null) {
                                GenerateMnemonicCallback generateMnemonicCallback = callbackMaps.get(tag).generateMnemonicCallback;
                                generateMnemonicCallback.onGenerateFail();
                                if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).generateMnemonicCallback==generateMnemonicCallback){
                                    callbackMaps.get(tag).generateMnemonicCallback=null;
                                }
                            }
                        }
                    }
                    break;


                //enroll fp
                case BlueToothWrapper.MSG_ENROLL_UPDATE:
                    int state = msg.arg1;
                    LoggerManager.d("MSG_ENROLL_UPDATE  state=" + MiddlewareInterface.getReturnString(state));
                    iterator = tags.iterator();
                    while (iterator.hasNext()) {
                        String tag = iterator.next();
                        if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).enrollFPCallback != null) {
                            callbackMaps.get(tag).enrollFPCallback.onEnrollFPUpate(state);
                        }
                    }
                    break;
                case BlueToothWrapper.MSG_ENROLL_FINISH:
                    int rtValue = msg.arg1;
                    LoggerManager.d("MSG_ENROLL_FINISH  rtValue=" + MiddlewareInterface.getReturnString(rtValue));
                    iterator = tags.iterator();
                    while (iterator.hasNext()) {
                        String tag = iterator.next();
                        if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).enrollFPCallback != null) {
                            EnrollFPCallback enrollFPCallback = callbackMaps.get(tag).enrollFPCallback;
                            enrollFPCallback.onEnrollFinish(rtValue);
                            if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).enrollFPCallback==enrollFPCallback){
                                callbackMaps.get(tag).enrollFPCallback=null;
                            }
                        }
                    }
                    break;

                case BlueToothWrapper.MSG_GET_ADDRESS_START:
                    break;
                case BlueToothWrapper.MSG_GET_ADDRESS_FINISH:

                    BlueToothWrapper.GetAddressReturnValue returnValueAddress = (BlueToothWrapper
                            .GetAddressReturnValue) msg.obj;
                    LoggerManager.d("MSG_GET_ADDRESS_FINISH returnValueAddress: " + MiddlewareInterface.getReturnString(returnValueAddress.getReturnValue()));
                    if (returnValueAddress.getReturnValue() == MiddlewareInterface.PAEW_RET_SUCCESS) {
                        if (returnValueAddress.getCoinType() == MiddlewareInterface.PAEW_COIN_TYPE_EOS) {
                            LoggerManager.d("EOS Address: " + returnValueAddress.getAddress());
                            String address = returnValueAddress.getAddress();
//                            String[] strArr = returnValueAddress.getAddress().split("####");
//                            String publicKey = strArr[0];
//                            String publicKey_sig = strArr[1];

                            iterator = tags.iterator();
                            while (iterator.hasNext()) {
                                String tag = iterator.next();
                                if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).getEosAddressCallback != null) {
                                    GetAddressCallback getEosAddressCallback = callbackMaps.get(tag).getEosAddressCallback;
                                    getEosAddressCallback.onGetSuccess(address);
                                    if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).getEosAddressCallback==getEosAddressCallback){
                                        callbackMaps.get(tag).getEosAddressCallback=null;
                                    }
                                }
                            }
                            return;
                        } else if (returnValueAddress.getCoinType() == MiddlewareInterface.PAEW_COIN_TYPE_ETH) {
                            LoggerManager.d("ETH Address: " + returnValueAddress.getAddress());
//                            String[] strArr = returnValueAddress.getAddress().split("####");
//                            String publicKey = strArr[0];

                            iterator = tags.iterator();
                            while (iterator.hasNext()) {
                                String tag = iterator.next();
                                if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).getEthAddressCallback != null) {
                                    GetAddressCallback getEthAddressCallback = callbackMaps.get(tag).getEthAddressCallback;
                                    getEthAddressCallback.onGetSuccess(returnValueAddress.getAddress());
                                    if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).getEthAddressCallback==getEthAddressCallback){
                                        callbackMaps.get(tag).getEthAddressCallback=null;
                                    }
                                }
                            }
                            return;
                        } else {


                        }
                    } else {

                    }

                    if (returnValueAddress.getCoinType() == MiddlewareInterface.PAEW_COIN_TYPE_EOS) {
                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).getEosAddressCallback != null) {
                                GetAddressCallback getEosAddressCallback = callbackMaps.get(tag).getEosAddressCallback;
                                getEosAddressCallback.onGetFail();
                                if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).getEosAddressCallback==getEosAddressCallback){
                                    callbackMaps.get(tag).getEosAddressCallback=null;
                                }
                            }
                        }
                    } else if (returnValueAddress.getCoinType() == MiddlewareInterface.PAEW_COIN_TYPE_ETH) {
                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).getEthAddressCallback != null) {
                                GetAddressCallback getEthAddressCallback = callbackMaps.get(tag).getEthAddressCallback;
                                getEthAddressCallback.onGetFail();
                                if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).getEthAddressCallback==getEthAddressCallback){
                                    callbackMaps.get(tag).getEthAddressCallback=null;
                                }
                            }
                        }
                    }


                    break;


                //get fp list
                case BlueToothWrapper.MSG_GET_FP_LIST_START:
                    LoggerManager.d("MSG_GET_FP_LIST_START");
                    break;
                case BlueToothWrapper.MSG_GET_FP_LIST_FINISH:
                    BlueToothWrapper.GetFPListReturnValue fpListReturnValue = (BlueToothWrapper.GetFPListReturnValue) msg.obj;
                    LoggerManager.d("MSG_GET_FP_LIST_FINISH  state=" + MiddlewareInterface.getReturnString(
                            fpListReturnValue.getReturnValue()));
                    if (fpListReturnValue.getReturnValue() == MiddlewareInterface.PAEW_RET_SUCCESS) {
                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).getFPListCallback != null) {
                                GetFPListCallback getFPListCallback = callbackMaps.get(tag).getFPListCallback;
                                getFPListCallback.onSuccess(fpListReturnValue);
                                if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).getFPListCallback==getFPListCallback){
                                    callbackMaps.get(tag).getFPListCallback=null;
                                }
                            }
                        }
                        //获取指纹信息成功
                        MiddlewareInterface.FingerPrintID[] fpList = fpListReturnValue.getFPList();
                        for (int i = 0; i < fpListReturnValue.getFPCount(); i++) {
                            LoggerManager.d("FP Index: " + CommonUtility.byte2hex(fpList[i].data));
                        }
                    } else {
                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).getFPListCallback != null) {
                                GetFPListCallback getFPListCallback = callbackMaps.get(tag).getFPListCallback;
                                getFPListCallback.onFail();
                                if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).getFPListCallback==getFPListCallback){
                                    callbackMaps.get(tag).getFPListCallback=null;
                                }
                            }
                        }
                    }
                    break;


                //delete fp
                case BlueToothWrapper.MSG_DELETE_FP_FINISH:
                    int deleteRtValue = msg.arg1;
                    LoggerManager.d(
                            "MSG_DELETE_FP_FINISH  rtValue=" + MiddlewareInterface.getReturnString(deleteRtValue));
                    if (msg.arg1 == MiddlewareInterface.PAEW_RET_SUCCESS) {
                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).deleteFPCallback != null) {
                                DeleteFPCallback deleteFPCallback = callbackMaps.get(tag).deleteFPCallback;
                                deleteFPCallback.onSuccess();
                                if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).deleteFPCallback==deleteFPCallback){
                                    callbackMaps.get(tag).deleteFPCallback=null;
                                }
                            }
                        }
                    } else {
                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).deleteFPCallback != null) {
                                DeleteFPCallback deleteFPCallback = callbackMaps.get(tag).deleteFPCallback;
                                deleteFPCallback.onFail();
                                if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).deleteFPCallback==deleteFPCallback){
                                    callbackMaps.get(tag).deleteFPCallback=null;
                                }
                            }
                        }
                    }
                    break;

                //EOS Transaction 签名
                case BlueToothWrapper.MSG_EOS_SIGN_START:
                    LoggerManager.d("MSG_EOS_SIGN_START");

                    iterator = tags.iterator();
                    while (iterator.hasNext()) {
                        String tag = iterator.next();
                        if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).eosSignCallback != null) {
                            callbackMaps.get(tag).eosSignCallback.onEosSignStart();
                        }
                    }

                    break;
                case BlueToothWrapper.MSG_EOS_SIGN_FINISH:
                    BlueToothWrapper.SignReturnValue returnValueSign = (BlueToothWrapper.SignReturnValue) msg.obj;
                    LoggerManager.d("MSG_EOS_SIGN_FINISH rtValue= "
                            + MiddlewareInterface.getReturnString(returnValueSign.getReturnValue()));

                    if (returnValueSign.getReturnValue() == MiddlewareInterface.PAEW_RET_SUCCESS) {
                        //签名成功
                        String strSignature = new String(returnValueSign.getSignature());
                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).eosSignCallback != null) {
                                EosSignCallback eosSignCallback = callbackMaps.get(tag).eosSignCallback;
                                eosSignCallback.onEosSignSuccess(strSignature);
                                if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).eosSignCallback==eosSignCallback){
                                    callbackMaps.get(tag).eosSignCallback=null;
                                }
                            }
                        }
                    } else {
                        //签名失败
                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).eosSignCallback != null) {
                                EosSignCallback eosSignCallback = callbackMaps.get(tag).eosSignCallback;
                                eosSignCallback.onEosSignFail();
                                if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).eosSignCallback==eosSignCallback){
                                    callbackMaps.get(tag).eosSignCallback=null;
                                }
                            }
                        }
                    }
                    break;
                //checkCode 获取SN及签名
                case BlueToothWrapper.MSG_GET_CHECK_CODE_START:

                    break;
                case BlueToothWrapper.MSG_GET_CHECK_CODE_FINISH:
                    BlueToothWrapper.GetCheckCodeReturnValue getCheckCodeReturnValue = (BlueToothWrapper.GetCheckCodeReturnValue) msg.obj;
                    if (getCheckCodeReturnValue.getReturnValue() == MiddlewareInterface.PAEW_RET_SUCCESS) {

                        byte[] checkedcode = getCheckCodeReturnValue.getCheckCode();
//                        byte[] snbyte = ConvertUtils.subByte(checkedcode, 0, 16);
////                         byte[] snSignByte = ConvertUtils.subByte(checkedcode,17,64);
//
//                        String SN = CommonUtility.byte2hex(snbyte);
//                        String SN_sign = CommonUtility.byte2hex(checkedcode);
//                        SN_sign = SN_sign.substring(32);

                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).getCheckCodeCallback != null) {
                                GetCheckCodeCallback getCheckCodeCallback = callbackMaps.get(tag).getCheckCodeCallback;
                                getCheckCodeCallback.onCheckCodeSuccess(checkedcode);
                                if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).getCheckCodeCallback==getCheckCodeCallback){
                                    callbackMaps.get(tag).getCheckCodeCallback=null;
                                }
                            }
                        }

                    } else {
                        //checkCode 失败
                        LoggerManager.d("checkCode fail");
                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).getCheckCodeCallback != null) {
                                GetCheckCodeCallback getCheckCodeCallback = callbackMaps.get(tag).getCheckCodeCallback;
                                getCheckCodeCallback.onCheckCodeFail();
                                if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).getCheckCodeCallback==getCheckCodeCallback){
                                    callbackMaps.get(tag).getCheckCodeCallback=null;
                                }
                            }
                        }
                    }
                    break;


                    //check mnes
                case BlueToothWrapper.MSG_CHECK_SEED_MNES_START:
                    LoggerManager.d(
                            "MSG_CHECK_SEED_MNES_START  ");
                    break;

                case BlueToothWrapper.MSG_CHECK_SEED_MNES_FINISH:
                    LoggerManager.d(
                            "MSG_CHECK_SEED_MNES_FINISH  state=" + MiddlewareInterface.getReturnString(msg.arg1));
                    if (msg.arg1 == MiddlewareInterface.PAEW_RET_SUCCESS) {
                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).checkMnemonicCallback != null) {
                                CheckMnemonicCallback checkMnemonicCallback = callbackMaps.get(tag).checkMnemonicCallback;
                                checkMnemonicCallback.onCheckSuccess();
                                if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).checkMnemonicCallback==checkMnemonicCallback){
                                    callbackMaps.get(tag).checkMnemonicCallback=null;
                                }
                            }
                        }
                    } else {
                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).checkMnemonicCallback != null) {
                                CheckMnemonicCallback checkMnemonicCallback = callbackMaps.get(tag).checkMnemonicCallback;
                                checkMnemonicCallback.onCheckFail(msg.arg1);
                                if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).checkMnemonicCallback==checkMnemonicCallback){
                                    callbackMaps.get(tag).checkMnemonicCallback=null;
                                }
                            }
                        }
                    }
                    break;
                case BlueToothWrapper.MSG_SET_TX_START:
                    LoggerManager.d(
                            "MSG_SET_TX_START  ");
                    break;
                case BlueToothWrapper.MSG_SET_TX_FINISH:
                    LoggerManager.d(
                            "MSG_SET_TX_FINISH  state="+MiddlewareInterface.getReturnString(msg.arg1));

                    if (msg.arg1 == MiddlewareInterface.PAEW_RET_SUCCESS) {
                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).setTxCallback != null) {
                                SetTxCallback setTxCallback = callbackMaps.get(tag).setTxCallback;
                                setTxCallback.onSetTxSuccess();
                                if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).setTxCallback==setTxCallback){
                                    callbackMaps.get(tag).setTxCallback=null;
                                }
                            }
                        }
                    } else {
                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).setTxCallback != null) {
                                SetTxCallback setTxCallback = callbackMaps.get(tag).setTxCallback;
                                setTxCallback.onSetTxFail();
                                if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).setTxCallback==setTxCallback){
                                    callbackMaps.get(tag).setTxCallback=null;
                                }
                            }
                        }
                    }

                    break;
                case BlueToothWrapper.MSG_GET_SIGN_RESULT_START:
                    LoggerManager.d(
                            "MSG_GET_SIGN_RESULT_START  ");
                    iterator = tags.iterator();
                    while (iterator.hasNext()) {
                        String tag = iterator.next();
                        if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).setGetSignResultCallback != null) {
                            callbackMaps.get(tag).setGetSignResultCallback.onGetSignResultStart();
                        }
                    }

                    break;
                case BlueToothWrapper.MSG_GET_SIGN_RESULT_UPDATE:
                    LoggerManager.d(
                            "MSG_GET_SIGN_RESULT_UPDATE ",MiddlewareInterface.getReturnString(msg.arg1));

                    iterator = tags.iterator();
                    while (iterator.hasNext()) {
                        String tag = iterator.next();
                        if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).setGetSignResultCallback != null) {
                            callbackMaps.get(tag).setGetSignResultCallback.onGetSignResultUpdate(msg.arg1);
                        }
                    }

                    break;

                case BlueToothWrapper.MSG_GET_SIGN_RESULT_FINISH:
                    BlueToothWrapper.SignReturnValue signReturnValue = (BlueToothWrapper.SignReturnValue)msg.obj;
                    LoggerManager.d(
                            "MSG_GET_SIGN_RESULT_FINISH retValue " + MiddlewareInterface.getReturnString
                                    (signReturnValue.getReturnValue()));
                    LoggerManager.d(
                            "MSG_GET_SIGN_RESULT_FINISH retValue err code " + signReturnValue.getReturnValue());
                    int coinType = signReturnValue.getCoinType();


                    if ( signReturnValue.getReturnValue() == MiddlewareInterface.PAEW_RET_SUCCESS) {

                        //getSign成功
                        String strSignature = "";
                        if (coinType == MiddlewareInterface.PAEW_COIN_TYPE_EOS) {
                            strSignature = new String(signReturnValue.getSignature());
                        } else if (coinType == MiddlewareInterface.PAEW_COIN_TYPE_ETH) {
                            strSignature = CommonUtility.byte2hex(signReturnValue.getSignature());
                        } else if (coinType == MiddlewareInterface.PAEW_COIN_TYPE_CYB) {
                            strSignature = CommonUtility.byte2hex(signReturnValue.getSignature());
                        }

                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).setGetSignResultCallback != null) {
                                SetGetSignResultCallback setGetSignResultCallback = callbackMaps.get(tag).setGetSignResultCallback;
                                setGetSignResultCallback.onGetSignResultSuccess(strSignature);
                                if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).setGetSignResultCallback==setGetSignResultCallback){
                                    callbackMaps.get(tag).setGetSignResultCallback=null;
                                }
                            }
                        }
                    }else {
                        //getSign失败
                        if (signReturnValue.getReturnValue() == BaseConst.STATUS_NO_VERIFY_COUNT){
                            //没有指纹录入却调用了指纹验证方法
                            iterator = tags.iterator();
                            while (iterator.hasNext()) {
                                String tag = iterator.next();
                                if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).setGetSignResultCallback != null) {
                                    SetGetSignResultCallback setGetSignResultCallback = callbackMaps.get(tag).setGetSignResultCallback;
                                    setGetSignResultCallback.onGetSignResultFail(BaseConst.STATUS_NO_VERIFY_COUNT);
                                    if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).setGetSignResultCallback==setGetSignResultCallback){
                                        callbackMaps.get(tag).setGetSignResultCallback=null;
                                    }
                                }
                            }
                        }else {
                            //其他失败
                            iterator = tags.iterator();
                            while (iterator.hasNext()) {
                                String tag = iterator.next();
                                if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).setGetSignResultCallback != null) {
                                    SetGetSignResultCallback setGetSignResultCallback = callbackMaps.get(tag).setGetSignResultCallback;
                                    setGetSignResultCallback.onGetSignResultFail(BaseConst.STATUS_OTHER_ERR);
                                    if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).setGetSignResultCallback==setGetSignResultCallback){
                                        callbackMaps.get(tag).setGetSignResultCallback=null;
                                    }
                                }
                            }
                        }

                    }

                    break;
                    //Sign时切换签名模式
                case BlueToothWrapper.MSG_SWITCH_SIGN_START:
                    LoggerManager.d(
                            "MSG_SWITCH_SIGN_START  ");
                    break;
                case BlueToothWrapper.MSG_SWITCH_SIGN_FINISH:
                    LoggerManager.d(
                            "MSG_SWITCH_SIGN_FINISH  ");

                    if ((msg.arg1 == MiddlewareInterface.PAEW_RET_SUCCESS) || (msg.arg1 == MiddlewareInterface.PAEW_RET_DEV_STATE_INVALID)) {
                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).switchSignCallback != null) {
                                SwitchSignCallback switchSignCallback = callbackMaps.get(tag).switchSignCallback;
                                switchSignCallback.onSwitchSignSuccess();
                                if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).switchSignCallback==switchSignCallback){
                                    callbackMaps.get(tag).switchSignCallback=null;
                                }
                            }
                        }
                    }else {
                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).switchSignCallback != null) {
                                SwitchSignCallback switchSignCallback = callbackMaps.get(tag).switchSignCallback;
                                switchSignCallback.onSwitchSignFail();
                                if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).switchSignCallback==switchSignCallback){
                                    callbackMaps.get(tag).switchSignCallback=null;
                                }
                            }
                        }
                    }
                    break;

                case BlueToothWrapper.MSG_VERIFY_SIGN_PIN_START:
                    LoggerManager.d(
                            "MSG_VERIFY_SIGN_PIN_START  ");

                    break;

                case BlueToothWrapper.MSG_VERIFY_SIGN_PIN_FINISH:
                    LoggerManager.d(
                            "MSG_VERIFY_SIGN_PIN_FINISH  retValue =  " + msg.arg1);
                    //BlueToothWrapper.SignReturnValue verifySignPinResult = (BlueToothWrapper.SignReturnValue)msg.obj;
                    if (msg.arg1 == MiddlewareInterface.PAEW_RET_SUCCESS) {
                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).verifySignPinCallback != null) {
                                VerifySignPinCallback verifySignPinCallback = callbackMaps.get(tag).verifySignPinCallback;
                                verifySignPinCallback.onVerifySuccess();
                                if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).verifySignPinCallback==verifySignPinCallback){
                                    callbackMaps.get(tag).verifySignPinCallback=null;
                                }
                            }
                        }
                    }else {
                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).verifySignPinCallback != null) {
                                VerifySignPinCallback verifySignPinCallback = callbackMaps.get(tag).verifySignPinCallback;
                                verifySignPinCallback.onVerifyFail();
                                if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).verifySignPinCallback==verifySignPinCallback){
                                    callbackMaps.get(tag).verifySignPinCallback=null;
                                }
                            }
                        }
                    }
//                    else if (msg.arg1 == MiddlewareInterface.PAEW_RET_DEV_STATE_INVALID){
//                        iterator = tags.iterator();
//                        while (iterator.hasNext()) {
//                            String tag = iterator.next();
//                            if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).verifySignPinCallback != null) {
//                                callbackMaps.get(tag).verifySignPinCallback.onVerifyOvertime();
//                                callbackMaps.get(tag).verifySignPinCallback=null;
//                            }
//                        }
//                    }


                    break;
                case BlueToothWrapper.MSG_ABORT_SIGN_START:
                    LoggerManager.d("MSG_ABORT_SIGN_START ");

                case BlueToothWrapper.MSG_ABORT_SIGN_FINISH:
                    String returnStr = MiddlewareInterface.getReturnString(msg.arg1);
                    LoggerManager.d("MSG_ABORT_SIGN_FINISH  Return Value:", returnStr);
                    if (msg.arg1 == MiddlewareInterface.PAEW_RET_SUCCESS) {
                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).abortSignCallback != null) {
                                AbortSignCallback abortSignCallback = callbackMaps.get(tag).abortSignCallback;
                                abortSignCallback.onAbortSignSuccess();
                                if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).abortSignCallback==abortSignCallback){
                                    callbackMaps.get(tag).abortSignCallback=null;
                                }
                            }
                        }
                    } else {
                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).abortSignCallback != null) {
                                AbortSignCallback abortSignCallback = callbackMaps.get(tag).abortSignCallback;
                                abortSignCallback.onAbortSignFail();
                                if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).abortSignCallback==abortSignCallback){
                                    callbackMaps.get(tag).abortSignCallback=null;
                                }
                            }
                        }
                    }
                    break;


                case BlueToothWrapper.MSG_ABORT_BUTTON_FINISH:
                    LoggerManager.d(
                            "MSG_ABORT_BUTTON_FINISH  state=" + MiddlewareInterface.getReturnString(msg.arg1));
                    if (msg.arg1 == MiddlewareInterface.PAEW_RET_SUCCESS) {
                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).abortButtonCallback != null) {
                                AbortButtonCallback abortButtonCallback = callbackMaps.get(tag).abortButtonCallback;
                                abortButtonCallback.onAbortSuccess();
                                if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).abortButtonCallback==abortButtonCallback){
                                    callbackMaps.get(tag).abortButtonCallback=null;
                                }
                            }
                        }
                    } else {
                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).abortButtonCallback != null) {
                                AbortButtonCallback abortButtonCallback = callbackMaps.get(tag).abortButtonCallback;
                                abortButtonCallback.onAbortFail();
                                if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).abortButtonCallback==abortButtonCallback){
                                    callbackMaps.get(tag).abortButtonCallback=null;
                                }
                            }
                        }
                    }
                    break;

                case BlueToothWrapper.MSG_GET_BATTERY_VALUE_FINISH:
                    BlueToothWrapper.GetBatteryReturnValue batteryReturnValue = (BlueToothWrapper.GetBatteryReturnValue)msg.obj;
                    LoggerManager.d(
                            "MSG_GET_BATTERY_VALUE_FINISH  state=" + MiddlewareInterface.getReturnString(batteryReturnValue.getReturnValue()));
                    if (batteryReturnValue.getReturnValue() == MiddlewareInterface.PAEW_RET_SUCCESS) {
                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).getBatteryCallback != null) {
                                GetBatteryCallback getBatteryCallback = callbackMaps.get(tag).getBatteryCallback;
                                getBatteryCallback.onSuccess(batteryReturnValue.getBatteryValue());
                                if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).getBatteryCallback==getBatteryCallback){
                                    callbackMaps.get(tag).getBatteryCallback=null;
                                }
                            }
                        }
                    } else {
                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag)!= null&&callbackMaps.get(tag).getBatteryCallback != null) {
                                GetBatteryCallback getBatteryCallback = callbackMaps.get(tag).getBatteryCallback;
                                getBatteryCallback.onFail();
                                if(callbackMaps.get(tag)!= null&&callbackMaps.get(tag).getBatteryCallback==getBatteryCallback){
                                    callbackMaps.get(tag).getBatteryCallback=null;
                                }
                            }
                        }
                    }
                    break;

                default:
                    break;
            }
        }
    }

    class DeviceComm {

        String deviceName;
        String initialPswHint;
        long contextHandle;
        int currentState;
        boolean msgBackConnectStatus;
        MiddlewareInterface.PAEW_DevInfo deviceInfo;
        int batteryMode = -1;//0 usb   ,1   battery
        int powerAmount = -1;//剩余电量
        public boolean isManualFree; //是否意外断开
        DeviceHandler mDeviceHandler;
        BlueToothWrapper connectThread;
        BlueToothWrapper getDeviceInfoThread;
        BlueToothWrapper importMnemonicThread;
        BlueToothWrapper formatThread;
        BlueToothWrapper confirmThread;
        BlueToothWrapper verifyFPThread;
        BlueToothWrapper freeContextThread;
        BlueToothWrapper enrollFPThread;
        BlueToothWrapper abortEnrollFPThread;
        BlueToothWrapper abortButtonThread;
        BlueToothWrapper jsonSerializeThread;
        BlueToothWrapper initPinThread;
        BlueToothWrapper verifyPinThread;
        BlueToothWrapper changePinThread;
        BlueToothWrapper generateMnemonicThread;
        BlueToothWrapper checkMnemonicThread;
        BlueToothWrapper getFPListThread;
        BlueToothWrapper deleteFPThread;
        BlueToothWrapper eosSignThread;
        BlueToothWrapper getEosAddressThread;
        BlueToothWrapper getEthAddressThread;
        BlueToothWrapper getCheckCodeThread;
        BlueToothWrapper setTxThread;
        BlueToothWrapper getSignResultThread;
        BlueToothWrapper switchSignThread;
        BlueToothWrapper verifySignPinThread;
        BlueToothWrapper abortSignThread;
        public boolean isFreeContexting;


        public DeviceComm(String deviceName) {
            this.deviceName = deviceName;
        }
    }

    class DeviceCallbacsBean {

        DeviceConnectCallback connectCallback;
        ImportMnemonicCallback importMnemonicCallback;
        DeviceFormatCallback formatCallback;
        PressConfirmCallback pressConfirmCallback;
        DeviceVerifyFPCallback verifyFPCallback;
        FreeContextCallback freeContextCallback;
        EnrollFPCallback enrollFPCallback;
        JsonSerilizeCallback jsonSerilizeCallback;
        ScanDeviceCallback scanDeviceCallback;
        GetDeviceInfoCallback getDeviceInfoCallback;
        GetDeviceInfoCallback getHeartDeviceInfoCallback;
        InitPinCallback initPinCallback;
        VerifyPinCallback verifyPinCallback;
        AbortFPCallback abortFPCallback;
        AbortButtonCallback abortButtonCallback;
        ChangePinCallback changePinCallback;
        GenerateMnemonicCallback generateMnemonicCallback;
        CheckMnemonicCallback checkMnemonicCallback;
        GetFPListCallback getFPListCallback;
        DeleteFPCallback deleteFPCallback;
        EosSignCallback eosSignCallback;
        GetAddressCallback getEthAddressCallback;
        GetAddressCallback getEosAddressCallback;
        GetCheckCodeCallback getCheckCodeCallback;
        SetTxCallback setTxCallback;
        SetGetSignResultCallback setGetSignResultCallback;
        SwitchSignCallback switchSignCallback;
        VerifySignPinCallback verifySignPinCallback;
        AbortSignCallback abortSignCallback;
        GetBatteryCallback getBatteryCallback;
    }


}
