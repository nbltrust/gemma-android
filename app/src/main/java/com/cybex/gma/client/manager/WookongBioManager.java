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

    public static WookongBioManager getInstance() {
        if (mInstance == null) {
            synchronized (WookongBioManager.class) {
                if (mInstance == null) {
                    mInstance = new WookongBioManager(mHandler);
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


}
