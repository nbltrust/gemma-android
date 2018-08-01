package com.cybex.gma.client.ui.presenter;

import android.content.Context;

import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.job.JobUtils;
import com.cybex.gma.client.ui.fragment.WalletFragment;
import com.hxlx.core.lib.mvp.lite.XPresenter;

import io.hypertrack.smart_scheduler.Job;
import io.hypertrack.smart_scheduler.SmartScheduler;

/**
 * 钱包Presenter
 *
 * Created by wanglin on 2018/7/9.
 */
public class WalletPresenter extends XPresenter<WalletFragment>{

    /**
     * 设置轮询
     * 先设置一个alarmJob，2分半之后开始做pollingJob轮询
     */
    public void setFirstPolling(){
        SmartScheduler smartScheduler = SmartScheduler.getInstance(getV().getActivity().getApplicationContext());

        SmartScheduler.JobScheduledCallback pollingCallback = new SmartScheduler.JobScheduledCallback() {
            @Override
            public void onJobScheduled(Context context, Job job) {
               //todo 执行 get_Info request 查询
            }
        };

        SmartScheduler.JobScheduledCallback alarmCallback = new SmartScheduler.JobScheduledCallback() {
            @Override
            public void onJobScheduled(Context context, Job job) {

                Job pollingJob = JobUtils.createPeriodicHandlerJob(ParamConstants.ALARM_JOB, pollingCallback, 3000);
                smartScheduler.addJob(pollingJob);
            }
        };
        
        Job alarmJob = JobUtils.createAlarmJob(ParamConstants.POLLING_JOB, alarmCallback, 10000);
        smartScheduler.addJob(alarmJob);
    }


}
