package com.cybex.gma.client.job;

import io.hypertrack.smart_scheduler.Job;
import io.hypertrack.smart_scheduler.SmartScheduler;

/**
 * Created by wanglin on 2018/7/31.
 */
public class JobUtils {

    public static Job createPeriodicHandlerJob(int JOB_ID, SmartScheduler.JobScheduledCallback callback, int
            intervalTime){

        Job.Builder builder = new Job.Builder(JOB_ID, callback, Job.Type.JOB_TYPE_HANDLER, "")
                .setIntervalMillis(intervalTime)//设置间隔多久时间开始
                .setPeriodic(intervalTime)//设置每隔多少时间执行
                .setRequiredNetworkType(Job.NetworkType.NETWORK_TYPE_ANY)//任何网络下都执行
                .setRequiresCharging(false);//不充电时也执行

        return builder.build();
    }

    public static Job createAlarmJob(int JOB_ID, SmartScheduler.JobScheduledCallback callback, int timeInMillis){

        Job.Builder builder = new Job.Builder(JOB_ID, callback, Job.Type.JOB_TYPE_ALARM, "")
                .setRequiresCharging(false)
                .setRequiredNetworkType(Job.NetworkType.NETWORK_TYPE_ANY)
                .setIntervalMillis(timeInMillis);

        return builder.build();
    }
}
