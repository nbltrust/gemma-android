package com.cybex.gma.client.manager;


import android.app.Activity;
import android.os.Handler;

import com.cybex.gma.client.utils.bluetooth.BlueToothWrapper;

/**
 * 蓝牙连接管理类
 */
public class WookongBioManager {

    private static BlueToothWrapper connectThread = null;
    private static volatile WookongBioManager mInstance = null;
    private static Handler mHandler;

    private WookongBioManager(Handler handler){
        mHandler = handler;
        if ((connectThread == null) || (connectThread.getState() == Thread.State.TERMINATED)) {
            connectThread = new BlueToothWrapper(mHandler);
        }
    }

    public static WookongBioManager getInstance(Handler curHandler) {
        if (mInstance == null) {
            synchronized (WookongBioManager.class) {
                if (mInstance == null) {
                    mInstance = new WookongBioManager(curHandler);
                }
            }
        }
        return mInstance;
    }

    /**
     * 调用中间件initContextWithDevName方法与设备建立连接
     */
    public void startConnect(Activity mContext, String deviceName){
        connectThread.setInitContextWithDevNameWrapper(mContext, deviceName);
        connectThread.start();
    }

    /**
     * 调用中间件freeContext方法与设备断开连接
     */
    public void disconnect(long mContextHandle){
            connectThread.setFreeContextWrapper(mContextHandle);
            connectThread.start();
    }

    /**
     * 调用中间件setGetInfoWrapper来获取设备信息
     * @param mContextHandle
     * @param devIndex
     */
    public void getDeviceInfo(long mContextHandle, int devIndex){
        connectThread.setGetInfoWrapper(mContextHandle, devIndex);
        connectThread.start();
    }

    /**
     * 调用中间件setGetFPListWrapper来获取已录入的指纹列表信息
     * @param mContextHandle
     * @param devIndex
     */
    public void getFPList(long mContextHandle, int devIndex){
        connectThread.setGetFPListWrapper(mContextHandle, devIndex);
        connectThread.start();
    }


    /**
     * 调用中间件setEnrollFPWrapper来获取录入指纹信息
     * @param mContextHandle
     * @param devIndex
     */
    public void erollFP(long mContextHandle, int devIndex){
        connectThread.setEnrollFPWrapper(mContextHandle, devIndex);
        connectThread.start();
    }


    /**
     * 调用中间件setVerifyFPWrapper来验证指纹
     * @param mContextHandle
     * @param devIndex
     */
    public void verifyFP(long mContextHandle, int devIndex){
        connectThread.setVerifyFPWrapper(mContextHandle, devIndex);
        connectThread.start();
    }


    /**
     * 调用中间件setFormatDeviceWrapper来执行格式化硬件
     * @param mContextHandle
     * @param devIndex
     */
    public void startFormat(long mContextHandle, int devIndex){
        connectThread.setFormatDeviceWrapper(mContextHandle, devIndex);
        connectThread.start();
    }


    /**
     * 调用中间件setGetAddressWrapper来获取币种地址（公钥）
     * @param mContextHandle
     * @param devIndex
     */
    public void getAddress(long mContextHandle, int devIndex, byte coinType, int[] derivePath){
        connectThread.setGetAddressWrapper(mContextHandle, devIndex, coinType, derivePath);
        connectThread.start();
    }


    /**
     * 调用中间件setGetDevListWrapper来查找设备
     */
    public void startScan(Activity mContext, String deviceName){
        connectThread.setGetDevListWrapper(mContext, deviceName);
        connectThread.start();
    }


    /**
     * 调用中间件setEOSTxSerializeWrapper来执行EOS签名前的结构体序列化操作
     */
    public void startEosSerialization(String strEOSTxString){
        connectThread.setEOSTxSerializeWrapper(strEOSTxString);
        connectThread.start();
    }


    /**
     * 调用中间件setGenerateSeedGetMnesWrapper来产生种子并由种子生成助记词
     */
    public void startGenerateSeedGetMnes(long mContextHandle, int devIndex, int seedLen){
        connectThread.setGenerateSeedGetMnesWrapper(mContextHandle, devIndex, seedLen);
        connectThread.start();
    }

    /**
     * 调用中间件setInitPINWrapper来初始化PIN码
     */
    public void initPIN(long mContextHandle,int devIndex, String password){
        connectThread.setInitPINWrapper(mContextHandle, devIndex, password);
        connectThread.start();
    }


    /**
     * 调用中间件setGetCheckCodeWrapper来初始化PIN码
     */
    public void getCheckCode(long mContextHandle, int devIndex){
        connectThread.setGetCheckCodeWrapper(mContextHandle, 0);
        connectThread.start();
    }


    /**
     * 释放Handler资源避免内存泄露
     */
    public void freeResource(){
        if (mHandler != null){
            mHandler.removeCallbacksAndMessages(null);
            mHandler=null;
        }
    }

    /**
     * 终止线程
     */
    public void freeThread(){
        connectThread.interrupt();
    }

}
