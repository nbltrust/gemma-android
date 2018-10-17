package com.cybex.gma.client.job;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import com.cybex.gma.client.GmaApplication;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.gma.client.utils.bluetooth.BlueToothWrapper;


import io.hypertrack.smart_scheduler.Job;
import io.hypertrack.smart_scheduler.SmartScheduler;

/**
 * 蓝牙连接保持job
 *
 * Created by wanglin on 2018/9/17.
 */
public class BluetoothConnectKeepJob {


    private static BlueToothWrapper getDeviceInfoThread;
    private static BlueToothWrapper connectThread;
    public static final int intervalTime = 15*1000;


    /**
     * 不等待，直接轮询，可设置间隔
     * 时间单位毫秒
     */
    public static void startConnectPolling(
            long contextHandle,
            Handler mainHandler,
            int updatePosition) {
        SmartScheduler smartScheduler = SmartScheduler.getInstance(GmaApplication.getAppContext());
        if (smartScheduler.contains(ParamConstants.BLUETOOTH_CONNECT_JOB)) {
            smartScheduler.removeJob(ParamConstants.BLUETOOTH_CONNECT_JOB);
        }
        SmartScheduler.JobScheduledCallback callback = new SmartScheduler.JobScheduledCallback() {
            @Override
            public void onJobScheduled(Context context, Job job) {
                LoggerManager.d("bluetooth connect polling executed...");
                getDeviceInfo(contextHandle, mainHandler, updatePosition);
            }

        };

        Job job = JobUtils.createPeriodicHandlerJob(ParamConstants.BLUETOOTH_CONNECT_JOB, callback, intervalTime);
        smartScheduler.addJob(job);
    }


    private static void getDeviceInfo(long contextHandle, Handler mainHandler, int updatePosition) {
        if ((getDeviceInfoThread == null) || (getDeviceInfoThread.getState()
                == Thread.State.TERMINATED)) {
            getDeviceInfoThread = new BlueToothWrapper(mainHandler);
            getDeviceInfoThread.setGetInfoWrapper(contextHandle
                    , updatePosition);
            getDeviceInfoThread.start();
        }
    }

    public static void removeJob() {
        SmartScheduler smartScheduler = SmartScheduler.getInstance(GmaApplication.getAppContext());
        if (smartScheduler != null && smartScheduler.contains(ParamConstants.BLUETOOTH_CONNECT_JOB)) {
            smartScheduler.removeJob(ParamConstants.BLUETOOTH_CONNECT_JOB);
        }
    }

    public static void connect(Handler mainHandler, Activity context, String deviceName){
        if ((connectThread == null) || (connectThread.getState() == Thread.State.TERMINATED)) {
            connectThread = new BlueToothWrapper(mainHandler);
            connectThread.setInitContextWithDevNameWrapper(context,
                    deviceName);
            connectThread.start();
        }
    }


}
