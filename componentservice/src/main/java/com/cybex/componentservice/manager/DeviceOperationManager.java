package com.cybex.componentservice.manager;

import android.os.Handler;
import android.os.Message;

import com.cybex.componentservice.config.CacheConstants;
import com.cybex.componentservice.event.DeviceConnectStatusUpdateEvent;
import com.cybex.componentservice.utils.bluetooth.BlueToothWrapper;
import com.extropies.common.CommonUtility;
import com.extropies.common.MiddlewareInterface;
import com.hxlx.core.lib.base.BaseApplication;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class DeviceOperationManager {

    String currentDeviceName;
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


    interface DeviceConnectCallback {
        void onConnectStart();

        void onConnectSuccess();

        void onConnectFailed();
    }

    interface DeviceFormatCallback {
        void onFormatStart();

        void onFormatSuccess();

        void onFormatFailed();
    }

    interface DeviceVerifyFPCallback {
        void onVerifyStart();

        void onVerifySuccess();

        void onVerifyFailed();
    }

    interface FreeContextCallback {
        void onFreeStart();

        void onFreeSuccess();

        void onFreeFailed();
    }

    interface EnrollFPCallback {
        void onEnrollFPStart();

        void onEnrollFPUpate(int state);
    }

    interface JsonSerilizeCallback {
        void onSerilizeStart();

        void onSerilizeSuccess(String serializeResult);

        void onSerilizeFail();
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
                case BlueToothWrapper.MSG_INIT_CONTEXT_START:
                    LoggerManager.d("MSG_INIT_CONTEXT_START");

                    iterator = tags.iterator();
                    while (iterator.hasNext()) {
                        String tag = iterator.next();
                        callbackMaps.get(tag).connectCallback.onConnectStart();
                    }

                    break;
                case BlueToothWrapper.MSG_INIT_CONTEXT_FINISH:
                    //连接完成
                    LoggerManager.d("MSG_INIT_CONTEXT_FINISH");

                    BlueToothWrapper.InitContextReturnValue returnValueConnect = (BlueToothWrapper
                            .InitContextReturnValue) msg.obj;
                    if ((returnValueConnect != null) && (returnValueConnect.getReturnValue()
                            == MiddlewareInterface.PAEW_RET_SUCCESS)) {
                        //连接成功
//                        SPUtils.getInstance()
//                                .put(CacheConstants.BIO_CONNECT_STATUS, CacheConstants.STATUS_BLUETOOTH_CONNCETED);
                        deviceMaps.get(deviceName).currentState = CacheConstants.STATUS_BLUETOOTH_CONNCETED;
                        deviceMaps.get(deviceName).contextHandle = returnValueConnect.getContextHandle();

                        EventBusProvider.post(new DeviceConnectStatusUpdateEvent(CacheConstants.STATUS_BLUETOOTH_CONNCETED, deviceName));

                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
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
                            callbackMaps.get(tag).connectCallback.onConnectFailed();
                        }
                    }
                    if (deviceMaps.get(deviceName).connectThread != null) {
                        deviceMaps.get(deviceName).connectThread.interrupt();
                    }
                    deviceMaps.get(deviceName).connectThread = null;
                    break;


                //free
                case BlueToothWrapper.MSG_FORMAT_DEVICE_START:
                    LoggerManager.d("MSG_FORMAT_DEVICE_START");
                    //格式化开始
                    iterator = tags.iterator();
                    while (iterator.hasNext()) {
                        String tag = iterator.next();
                        callbackMaps.get(tag).formatCallback.onFormatStart();
                    }
                    break;
                case BlueToothWrapper.MSG_FORMAT_DEVICE_FINISH:
                    LoggerManager.d("MSG_FORMAT_DEVICE_FINISH");
                    //格式化完成
                    iterator = tags.iterator();
                    while (iterator.hasNext()) {
                        String tag = iterator.next();
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
                        callbackMaps.get(tag).verifyFPCallback.onVerifyStart();
                    }
                    break;
                case BlueToothWrapper.MSG_VERIFYFP_FINISH:
                    LoggerManager.d("MSG_VERIFYFP_FINISH");
                    //验证指纹完成
                    iterator = tags.iterator();
                    while (iterator.hasNext()) {
                        String tag = iterator.next();
                        callbackMaps.get(tag).verifyFPCallback.onVerifySuccess();
                    }
                    break;

                //free context

                case BlueToothWrapper.MSG_FREE_CONTEXT_START:
                    LoggerManager.d("MSG_FREE_CONTEXT_START");
                    iterator = tags.iterator();
                    while (iterator.hasNext()) {
                        String tag = iterator.next();
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
                        callbackMaps.get(tag).freeContextCallback.onFreeSuccess();
                    }
                    break;


                //serialize
                case BlueToothWrapper.MSG_EOS_SERIALIZE_START:
                    LoggerManager.d("MSG_EOS_SERIALIZE_START");

                    iterator = tags.iterator();
                    while (iterator.hasNext()) {
                        String tag = iterator.next();
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
                            callbackMaps.get(tag).jsonSerilizeCallback.onSerilizeSuccess(serializedStr);
                        }
                    } else {
                        iterator = tags.iterator();
                        while (iterator.hasNext()) {
                            String tag = iterator.next();
                            callbackMaps.get(tag).jsonSerilizeCallback.onSerilizeFail();
                        }
                    }
                    LoggerManager.d(
                            "Return Value: " + MiddlewareInterface.getReturnString(returnValue.getReturnValue()));
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
        BlueToothWrapper formatThread;
        BlueToothWrapper verifyFPThread;
        BlueToothWrapper freeContextThread;
        BlueToothWrapper enrollFPThread;
        BlueToothWrapper jsonSerializeThread;


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
    }




}
