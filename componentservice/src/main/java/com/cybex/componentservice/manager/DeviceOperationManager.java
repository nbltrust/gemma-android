package com.cybex.componentservice.manager;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.cybex.base.view.flowlayout.FlowLayout;
import com.cybex.base.view.flowlayout.TagAdapter;
import com.cybex.componentservice.config.CacheConstants;
import com.cybex.componentservice.event.DeviceConnectStatusUpdateEvent;
import com.cybex.componentservice.utils.bluetooth.BlueToothWrapper;
import com.extropies.common.CommonUtility;
import com.extropies.common.MiddlewareInterface;
import com.hxlx.core.lib.base.BaseApplication;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.utils.EmptyUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class DeviceOperationManager {


    String currentDeviceName;
    BlueToothWrapper scanThread;
    ScanHandler scanHandler;
    private Map<String, DeviceComm> deviceMaps = new HashMap<>();
    private Map<String, DeviceCallbacsBean> callbackMaps = new HashMap<>();

    private DeviceOperationManager() {
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
        if (deviceMaps.get(deviceName) == null) {
            return false;
        }
        if (deviceMaps.get(deviceName).currentState != CacheConstants.STATUS_BLUETOOTH_CONNCETED) {
            return false;
        }
        return true;

    }

    public void clearCallback(String tag) {
        callbackMaps.remove(tag);
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
    }


    public void connectDevice(String tag, String deviceName, DeviceConnectCallback connectCallback) {
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
        if ((deviceComm.connectThread == null) || (deviceComm.connectThread.getState() == Thread.State.TERMINATED)) {
            deviceComm.connectThread = new BlueToothWrapper(deviceComm.mDeviceHandler);
            deviceComm.connectThread.setInitContextWithDevNameWrapper(BaseApplication.getAppContext(),
                    deviceName);
            deviceComm.connectThread.start();
        }
    }

    public void getDeviceInfo(String tag, String deviceName, GetDeviceInfoCallback getDeviceInfoCallback) {
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
        if ((deviceComm.getDeviceInfoThread == null) || (deviceComm.getDeviceInfoThread.getState() == Thread.State.TERMINATED)) {
            deviceComm.getDeviceInfoThread = new BlueToothWrapper(deviceComm.mDeviceHandler);
            deviceComm.getDeviceInfoThread.setGetInfoWrapper(deviceComm.contextHandle,
                    0);
            deviceComm.getDeviceInfoThread.start();
        }

    }


    public void startFormat(String tag, String deviceName, DeviceFormatCallback formatCallback) {
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
        if ((deviceComm.formatThread == null) || (deviceComm.formatThread.getState() == Thread.State.TERMINATED)) {
            deviceComm.formatThread = new BlueToothWrapper(deviceComm.mDeviceHandler);
            deviceComm.formatThread.setFormatDeviceWrapper(deviceComm.contextHandle,
                    0);
            deviceComm.formatThread.start();
        }
    }

    public void startVerifyFP(String tag, String deviceName, DeviceVerifyFPCallback verifyFPCallback) {
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
        if ((deviceComm.verifyFPThread == null) || (deviceComm.verifyFPThread.getState() == Thread.State.TERMINATED)) {
            deviceComm.verifyFPThread = new BlueToothWrapper(deviceComm.mDeviceHandler);
            deviceComm.verifyFPThread.setVerifyFPWrapper(deviceComm.contextHandle,
                    0);
            deviceComm.verifyFPThread.start();
        }
    }

    public void freeContext(String tag, String deviceName, FreeContextCallback freeContextCallback) {
        DeviceCallbacsBean deviceCallbacks = callbackMaps.get(tag);
        if (deviceCallbacks == null) {
            deviceCallbacks = new DeviceCallbacsBean();
            callbackMaps.put(tag, deviceCallbacks);
        }
        deviceCallbacks.freeContextCallback = freeContextCallback;


        DeviceComm deviceComm = deviceMaps.get(deviceName);
        if (deviceComm == null) {
            deviceComm = new DeviceComm(deviceName);
            deviceMaps.put(deviceName, deviceComm);
        }
        if (deviceComm.mDeviceHandler == null) {
            deviceComm.mDeviceHandler = new DeviceHandler(deviceName);
        }
        if ((deviceComm.freeContextThread == null) || (deviceComm.freeContextThread.getState() == Thread.State.TERMINATED)) {
            deviceComm.freeContextThread = new BlueToothWrapper(deviceComm.mDeviceHandler);
            deviceComm.freeContextThread.setFreeContextWrapper(deviceComm.contextHandle);
            deviceComm.freeContextThread.start();
        }
    }


    public void enrollFP(String tag, String deviceName, EnrollFPCallback enrollFPCallback) {
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
        if ((deviceComm.enrollFPThread == null) || (deviceComm.enrollFPThread.getState() == Thread.State.TERMINATED)) {
            deviceComm.enrollFPThread = new BlueToothWrapper(deviceComm.mDeviceHandler);
            deviceComm.enrollFPThread.setEnrollFPWrapper(deviceComm.contextHandle,
                    0);
            deviceComm.enrollFPThread.start();
        }
    }


    public void jsonSerialization(String tag, String jsonTxStr, String deviceName, JsonSerilizeCallback jsonSerilizeCallback) {

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
        if ((deviceComm.jsonSerializeThread == null) || (deviceComm.jsonSerializeThread.getState() == Thread.State.TERMINATED)) {
            deviceComm.jsonSerializeThread = new BlueToothWrapper(deviceComm.mDeviceHandler);
            deviceComm.jsonSerializeThread.setEOSTxSerializeWrapper(jsonTxStr);
            deviceComm.jsonSerializeThread.start();
        }

    }

    public void initPin(String tag, String deviceName, String password, InitPinCallback initPinCallback) {
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
        if ((deviceComm.initPinThread == null) || (deviceComm.initPinThread.getState() == Thread.State.TERMINATED)) {
            deviceComm.initPinThread = new BlueToothWrapper(deviceComm.mDeviceHandler);
            deviceComm.initPinThread.setInitPINWrapper(deviceComm.contextHandle,
                    0, password);
            deviceComm.initPinThread.start();
        }

    }


    /**
     * 调用中间件setGenerateSeedGetMnesWrapper来产生种子并由种子生成助记词
     */
    public void generateMnemonic(String tag, String deviceName, GenerateMnemonicCallback generateMnemonicCallback) {

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
        if ((deviceComm.generateMnemonicThread == null) || (deviceComm.generateMnemonicThread.getState() == Thread.State.TERMINATED)) {
            deviceComm.generateMnemonicThread = new BlueToothWrapper(deviceComm.mDeviceHandler);
            deviceComm.generateMnemonicThread.setGenerateSeedGetMnesWrapper(deviceComm.contextHandle,
                    0, 16);
            deviceComm.generateMnemonicThread.start();
        }
    }


    public int checkMnemonic(String deviceName, String strDestMnes) {
        DeviceComm deviceComm = deviceMaps.get(deviceName);
        if (deviceComm == null) {
            deviceComm = new DeviceComm(deviceName);
            deviceMaps.put(deviceName, deviceComm);
        }
        int status = MiddlewareInterface.generateSeed_CheckMnes(deviceComm.contextHandle, 0,
                strDestMnes);
        return status;
    }


    public interface DeviceConnectCallback {
        void onConnectStart();

        void onConnectSuccess();

        void onConnectFailed();
    }

    public interface DeviceFormatCallback {
        void onFormatStart();

        void onFormatSuccess();

        void onFormatFailed();
    }

    public interface DeviceVerifyFPCallback {
        void onVerifyStart();

        void onVerifySuccess();

        void onVerifyFailed();
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
    }

    public interface GetDeviceInfoCallback {
        void onGetSuccess(MiddlewareInterface.PAEW_DevInfo deviceInfo);

        void onGetFail();
    }

    public interface InitPinCallback {
        void onInitSuccess();

        void onInitFail();
    }

    public interface GenerateMnemonicCallback {
        void onGenerateSuccess(BlueToothWrapper.GenSeedMnesReturnValue mnemonic);

        void onGenerateFail();
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
                        if (callbackMaps.get(tag).scanDeviceCallback != null)
                            callbackMaps.get(tag).scanDeviceCallback.onScanStart();
                    }

                    break;
                case BlueToothWrapper.MSG_ENUM_UPDATE:
                    //更新蓝牙设备列表
                    String[] devNames = (String[]) msg.obj;
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
                        if (callbackMaps.get(tag).scanDeviceCallback != null)
                            callbackMaps.get(tag).scanDeviceCallback.onScanUpdate(devNames);
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

            Set<String> tags = callbackMaps.keySet();
            Iterator<String> iterator;

            switch (msg.what) {

                case BlueToothWrapper.MSG_INIT_PIN_START:
                    LoggerManager.d("MSG_INIT_PIN_START");
                    //设置PIN
                    break;
                case BlueToothWrapper.MSG_INIT_PIN_FINISH:
                    LoggerManager.d("MSG_INIT_PIN_FINISH status=" + msg.arg1);
                    //已完成设置PIN
                    if (msg.arg1 == MiddlewareInterface.PAEW_RET_SUCCESS) {
                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag).initPinCallback != null)
                                callbackMaps.get(tag).initPinCallback.onInitSuccess();
                        }
                    } else {
                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag).initPinCallback != null)
                                callbackMaps.get(tag).initPinCallback.onInitFail();
                        }
                    }
                    break;

                case BlueToothWrapper.MSG_INIT_CONTEXT_START:
                    LoggerManager.d("MSG_INIT_CONTEXT_START");

                    iterator = tags.iterator();
                    while (iterator.hasNext()) {
                        String tag = iterator.next();
                        if (callbackMaps.get(tag).connectCallback != null)
                            callbackMaps.get(tag).connectCallback.onConnectStart();
                    }

                    break;
                case BlueToothWrapper.MSG_INIT_CONTEXT_FINISH:
                    //连接完成
                    BlueToothWrapper.InitContextReturnValue returnValueConnect = (BlueToothWrapper
                            .InitContextReturnValue) msg.obj;
                    LoggerManager.d("MSG_INIT_CONTEXT_FINISH  rtValue=" + MiddlewareInterface.getReturnString(returnValueConnect.getReturnValue()));
                    if ((returnValueConnect != null) && (returnValueConnect.getReturnValue()
                            == MiddlewareInterface.PAEW_RET_SUCCESS)) {
                        //连接成功
//                        SPUtils.getInstance()
//                                .put(CacheConstants.BIO_CONNECT_STATUS, CacheConstants.STATUS_BLUETOOTH_CONNCETED);
                        deviceMaps.get(deviceName).currentState = CacheConstants.STATUS_BLUETOOTH_CONNCETED;
                        deviceMaps.get(deviceName).contextHandle = returnValueConnect.getContextHandle();

                        currentDeviceName = deviceName;
                        EventBusProvider.post(new DeviceConnectStatusUpdateEvent(CacheConstants.STATUS_BLUETOOTH_CONNCETED, deviceName));

                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag).connectCallback != null)
                                callbackMaps.get(tag).connectCallback.onConnectSuccess();
                        }


                    } else {
                        //连接超时或失败
//                        SPUtils.getInstance()
//                                .put(CacheConstants.BIO_CONNECT_STATUS, CacheConstants.STATUS_BLUETOOTH_DISCONNCETED);
                        deviceMaps.get(deviceName).currentState = CacheConstants.STATUS_BLUETOOTH_DISCONNCETED;

                        EventBusProvider.post(new DeviceConnectStatusUpdateEvent(CacheConstants.STATUS_BLUETOOTH_DISCONNCETED, deviceName));

                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag).connectCallback != null)
                                callbackMaps.get(tag).connectCallback.onConnectFailed();
                        }
                    }
                    if (deviceMaps.get(deviceName).connectThread != null) {
                        deviceMaps.get(deviceName).connectThread.interrupt();
                    }
                    deviceMaps.get(deviceName).connectThread = null;
                    break;


                case BlueToothWrapper.MSG_GET_DEV_INFO_START:
                    LoggerManager.d("MSG_GET_DEV_INFO_START");
                    break;
                case BlueToothWrapper.MSG_GET_DEV_INFO_FINISH:

                    //获得设备信息
                    BlueToothWrapper.GetDevInfoReturnValue reValue = (BlueToothWrapper.GetDevInfoReturnValue) msg.obj;
                    LoggerManager.d("MSG_GET_DEV_INFO_FINISH  rtValue=" + MiddlewareInterface.getReturnString(reValue.getReturnValue()));
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

                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag).getDeviceInfoCallback != null)
                                callbackMaps.get(tag).getDeviceInfoCallback.onGetSuccess(devInfo);
                        }

                    } else {
                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag).getDeviceInfoCallback != null)
                                callbackMaps.get(tag).getDeviceInfoCallback.onGetFail();
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
                        if (callbackMaps.get(tag).formatCallback != null)
                            callbackMaps.get(tag).formatCallback.onFormatStart();
                    }
                    break;
                case BlueToothWrapper.MSG_FORMAT_DEVICE_FINISH:
                    LoggerManager.d("MSG_FORMAT_DEVICE_FINISH");
//                    BlueToothWrapper.GetDevInfoReturnValue reValue = (BlueToothWrapper.GetDevInfoReturnValue) msg.obj;

                    //格式化完成
                    iterator = tags.iterator();
                    while (iterator.hasNext()) {
                        String tag = iterator.next();
                        if (callbackMaps.get(tag).formatCallback != null)
                            callbackMaps.get(tag).formatCallback.onFormatSuccess();
                    }
                    //断开连接
                    break;


                //verify fp
                case BlueToothWrapper.MSG_VERIFYFP_START:
                    LoggerManager.d("MSG_VERIFYFP_START");
                    //验证指纹开始
                    iterator = tags.iterator();
                    while (iterator.hasNext()) {
                        String tag = iterator.next();
                        if (callbackMaps.get(tag).verifyFPCallback != null)
                            callbackMaps.get(tag).verifyFPCallback.onVerifyStart();
                    }
                    break;
                case BlueToothWrapper.MSG_VERIFYFP_FINISH:
                    LoggerManager.d("MSG_VERIFYFP_FINISH");
                    //验证指纹完成
                    iterator = tags.iterator();
                    while (iterator.hasNext()) {
                        String tag = iterator.next();
                        if (callbackMaps.get(tag).verifyFPCallback != null)
                            callbackMaps.get(tag).verifyFPCallback.onVerifySuccess();
                    }
                    break;

                //free context

                case BlueToothWrapper.MSG_FREE_CONTEXT_START:
                    LoggerManager.d("MSG_FREE_CONTEXT_START");
                    iterator = tags.iterator();
                    while (iterator.hasNext()) {
                        String tag = iterator.next();
                        if (callbackMaps.get(tag).freeContextCallback != null)
                            callbackMaps.get(tag).freeContextCallback.onFreeStart();
                    }

                    break;
                case BlueToothWrapper.MSG_FREE_CONTEXT_FINISH:
                    LoggerManager.d("MSG_FREE_CONTEXT_FINISH");
//                    SPUtils.getInstance()
//                            .put(CacheConstants.BIO_CONNECT_STATUS, CacheConstants.STATUS_BLUETOOTH_DISCONNCETED);
                    deviceMaps.get(deviceName).currentState = CacheConstants.STATUS_BLUETOOTH_DISCONNCETED;
                    EventBusProvider.post(new DeviceConnectStatusUpdateEvent(CacheConstants.STATUS_BLUETOOTH_DISCONNCETED, deviceName));

                    iterator = tags.iterator();
                    while (iterator.hasNext()) {
                        String tag = iterator.next();
                        if (callbackMaps.get(tag).freeContextCallback != null)
                            callbackMaps.get(tag).freeContextCallback.onFreeSuccess();
                    }
                    break;


                //serialize
                case BlueToothWrapper.MSG_EOS_SERIALIZE_START:
                    LoggerManager.d("MSG_EOS_SERIALIZE_START");

                    iterator = tags.iterator();
                    while (iterator.hasNext()) {
                        String tag = iterator.next();
                        if (callbackMaps.get(tag).jsonSerilizeCallback != null)
                            callbackMaps.get(tag).jsonSerilizeCallback.onSerilizeStart();
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
                            if (callbackMaps.get(tag).jsonSerilizeCallback != null)
                                callbackMaps.get(tag).jsonSerilizeCallback.onSerilizeSuccess(serializedStr);
                        }
                    } else {
                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag).jsonSerilizeCallback != null)
                                callbackMaps.get(tag).jsonSerilizeCallback.onSerilizeFail();
                        }
                    }
                    LoggerManager.d(
                            "Return Value: " + MiddlewareInterface.getReturnString(returnValue.getReturnValue()));
                    break;


                //generate mnemonic
                case BlueToothWrapper.MSG_GENERATE_SEED_MNES_FINISH:
                    BlueToothWrapper.GenSeedMnesReturnValue value = (BlueToothWrapper.GenSeedMnesReturnValue) msg.obj;
                    if (EmptyUtils.isNotEmpty(value)) {
                        String[] mnes = value.getStrMneWord();
                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag).generateMnemonicCallback != null)
                                callbackMaps.get(tag).generateMnemonicCallback.onGenerateSuccess(value);
                        }
                    } else {
                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            if (callbackMaps.get(tag).generateMnemonicCallback != null)
                                callbackMaps.get(tag).generateMnemonicCallback.onGenerateFail();
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
                        if (callbackMaps.get(tag).enrollFPCallback != null)
                            callbackMaps.get(tag).enrollFPCallback.onEnrollFPUpate(state);
                    }
                    break;
                case BlueToothWrapper.MSG_ENROLL_FINISH:
                    int rtValue = msg.arg1;
                    LoggerManager.d("MSG_ENROLL_FINISH  rtValue=" + MiddlewareInterface.getReturnString(rtValue));
                    iterator = tags.iterator();
                    while (iterator.hasNext()) {
                        String tag = iterator.next();
                        if (callbackMaps.get(tag).enrollFPCallback != null)
                            callbackMaps.get(tag).enrollFPCallback.onEnrollFinish(rtValue);
                    }
                    break;

                case BlueToothWrapper.MSG_GET_ADDRESS_START:
                    break;
                case BlueToothWrapper.MSG_GET_ADDRESS_FINISH:
//                    //获取EOS地址（公钥）结束
//                    BlueToothWrapper.GetAddressReturnValue returnValueAddress = (BlueToothWrapper
//                            .GetAddressReturnValue) msg.obj;
//                    if (returnValueAddress.getReturnValue() == MiddlewareInterface.PAEW_RET_SUCCESS) {
//                        if (returnValueAddress.getCoinType() == MiddlewareInterface.PAEW_COIN_TYPE_EOS) {
//                            //LoggerManager.d("EOS Address: " + returnValueAddress.getAddress());
//                            String[] strArr = returnValueAddress.getAddress().split("####");
//                            String publicKey = strArr[0];
//
//                            DeviceInfoEvent event = new DeviceInfoEvent();
//                            event.setEosPublicKey(publicKey);
//                            EventBusProvider.postSticky(event);
//
//                            getP().executeBluetoothTransferLogic(currentEOSName, getCollectionAccount(),
//                                    getAmount() + " EOS", getNote());
//                        }
//                    } else {
//
//                    }
//                    dissmisProgressDialog();

                    break;
                default:
                    break;
            }
        }
    }

    class DeviceComm {

        String deviceName;
        long contextHandle;
        int currentState;
        DeviceHandler mDeviceHandler;
        BlueToothWrapper connectThread;
        BlueToothWrapper getDeviceInfoThread;
        BlueToothWrapper formatThread;
        BlueToothWrapper verifyFPThread;
        BlueToothWrapper freeContextThread;
        BlueToothWrapper enrollFPThread;
        BlueToothWrapper jsonSerializeThread;
        BlueToothWrapper initPinThread;
        BlueToothWrapper generateMnemonicThread;


        public DeviceComm(String deviceName) {
            this.deviceName = deviceName;
        }
    }

    class DeviceCallbacsBean {
        DeviceConnectCallback connectCallback;
        DeviceFormatCallback formatCallback;
        DeviceVerifyFPCallback verifyFPCallback;
        FreeContextCallback freeContextCallback;
        EnrollFPCallback enrollFPCallback;
        JsonSerilizeCallback jsonSerilizeCallback;
        ScanDeviceCallback scanDeviceCallback;
        GetDeviceInfoCallback getDeviceInfoCallback;
        InitPinCallback initPinCallback;
        GenerateMnemonicCallback generateMnemonicCallback;
    }


}
