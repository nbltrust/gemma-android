package com.cybex.gma.client.job;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import com.cybex.componentservice.event.DeviceConnectStatusUpdateEvent;
import com.cybex.componentservice.manager.DeviceOperationManager;
import com.cybex.gma.client.GmaApplication;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.componentservice.manager.LoggerManager;
import com.extropies.common.MiddlewareInterface;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;


import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import io.hypertrack.smart_scheduler.Job;
import io.hypertrack.smart_scheduler.SmartScheduler;

/**
 * 蓝牙连接保持job
 *
 * Created by wanglin on 2018/9/17.
 */
public class BluetoothConnectKeepJob {


//    private static BlueToothWrapper getDeviceInfoThread;
//    private static BlueToothWrapper connectThread;
    public static final int intervalTime = 10*1000;

    public int errorCount;

    private static BluetoothConnectKeepJob instance;
    private BluetoothConnectKeepJob(){
        EventBusProvider.register(this);
    }

    public static BluetoothConnectKeepJob getInstance(){
        if(instance==null){
            synchronized (BluetoothConnectKeepJob.class){
                if(instance==null){
                    instance=new BluetoothConnectKeepJob();
                }
            }
        }
        return instance;
    }


    public void startHeartBeat() {

        SmartScheduler smartScheduler = SmartScheduler.getInstance(GmaApplication.getAppContext());
        if (smartScheduler.contains(ParamConstants.BLUETOOTH_CONNECT_JOB)) {
            smartScheduler.removeJob(ParamConstants.BLUETOOTH_CONNECT_JOB);
        }
        SmartScheduler.JobScheduledCallback callback = new SmartScheduler.JobScheduledCallback() {
            @Override
            public void onJobScheduled(Context context, Job job) {
                //LoggerManager.d("bluetooth startHeartBeat...");
                getDeviceInfo();
            }

        };

        Job job = JobUtils.createPeriodicHandlerJob(ParamConstants.BLUETOOTH_CONNECT_JOB, callback, intervalTime);
        smartScheduler.addJob(job);
    }


    private void getDeviceInfo() {

        String currentDeviceName = DeviceOperationManager.getInstance().getCurrentDeviceName();
        DeviceOperationManager.getInstance().getHeartDeviceInfo(this.toString(), currentDeviceName, new DeviceOperationManager.GetDeviceInfoCallback() {
            @Override
            public void onGetSuccess(MiddlewareInterface.PAEW_DevInfo deviceInfo) {
                errorCount=0;
            }

            @Override
            public void onGetFail() {
                //heart beat fail , disconnect
                errorCount++;
                boolean deviceConnectted = DeviceOperationManager.getInstance().isDeviceConnectted(currentDeviceName);
                LoggerManager.e("heart beat onGetFail deviceConnectted:"+deviceConnectted);
                if(deviceConnectted&&errorCount>=2){
//                    DeviceOperationManager.getInstance().freeContext(instance.toString(),false,currentDeviceName,null);
//                    EventBusProvider.post(new DeviceConnectStatusUpdateEvent());
                }

            }
        });

    }

    /**
     * 不等待，直接轮询，可设置间隔
     * 时间单位毫秒
     */
//    public static void startConnectPolling(
//            long contextHandle,
//            Handler mainHandler,
//            int updatePosition) {
//        SmartScheduler smartScheduler = SmartScheduler.getInstance(GmaApplication.getAppContext());
//        if (smartScheduler.contains(ParamConstants.BLUETOOTH_CONNECT_JOB)) {
//            smartScheduler.removeJob(ParamConstants.BLUETOOTH_CONNECT_JOB);
//        }
//        SmartScheduler.JobScheduledCallback callback = new SmartScheduler.JobScheduledCallback() {
//            @Override
//            public void onJobScheduled(Context context, Job job) {
//                LoggerManager.d("bluetooth connect polling executed...");
//                getDeviceInfo(contextHandle, mainHandler, updatePosition);
//            }
//
//        };
//
//        Job job = JobUtils.createPeriodicHandlerJob(ParamConstants.BLUETOOTH_CONNECT_JOB, callback, intervalTime);
//        smartScheduler.addJob(job);
//    }


//    private static void getDeviceInfo(long contextHandle, Handler mainHandler, int updatePosition) {
//        if ((getDeviceInfoThread == null) || (getDeviceInfoThread.getState()
//                == Thread.State.TERMINATED)) {
//            getDeviceInfoThread = new BlueToothWrapper(mainHandler);
//            getDeviceInfoThread.setGetInfoWrapper(contextHandle
//                    , updatePosition);
//            getDeviceInfoThread.start();
//        }
//    }

    public static void removeJob() {
        SmartScheduler smartScheduler = SmartScheduler.getInstance(GmaApplication.getAppContext());
        if (smartScheduler != null && smartScheduler.contains(ParamConstants.BLUETOOTH_CONNECT_JOB)) {
            smartScheduler.removeJob(ParamConstants.BLUETOOTH_CONNECT_JOB);
        }
    }




    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveConnectEvent(DeviceConnectStatusUpdateEvent event){
        if(event.status==DeviceConnectStatusUpdateEvent.STATUS_BLUETOOTH_CONNCETED){
            removeJob();
            startHeartBeat();
        }else{
            removeJob();
        }
    }

}
