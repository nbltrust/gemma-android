package io.hypertrack.smart_scheduler;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;


public class SmartSchedulerPeriodicTaskService extends GcmTaskService {
    private static final String TAG = SmartSchedulerPeriodicTaskService.class.getSimpleName();

    @Override
    public int onRunTask(TaskParams taskParams) {
        SmartScheduler smartScheduler = SmartScheduler.getInstance(getApplicationContext());
        smartScheduler.onPeriodicTaskJobScheduled(taskParams.getTag(), taskParams.getExtras());
        return GcmNetworkManager.RESULT_SUCCESS;
    }
}