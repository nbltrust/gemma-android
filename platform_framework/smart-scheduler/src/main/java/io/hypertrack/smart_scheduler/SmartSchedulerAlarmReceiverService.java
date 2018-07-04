package io.hypertrack.smart_scheduler;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;


public class SmartSchedulerAlarmReceiverService extends IntentService {

  private static final String TAG = SmartSchedulerAlarmReceiverService.class.getSimpleName();
  private static final String CHANNEL_ID_SERVICE = "SmartSchedulerAlarmReceiverService";
  private static final String CHANNEL_ID_NAME = "SmartScheduler_AlarmReceiverService";
  private static final int serviceID = 8001;

  public SmartSchedulerAlarmReceiverService() {
    super(TAG);
  }

  @Override
  public void onCreate() {
    super.onCreate();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationManager manager =
          (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
      NotificationChannel channel = new NotificationChannel(CHANNEL_ID_SERVICE, CHANNEL_ID_NAME,
          NotificationManager.IMPORTANCE_LOW);
      manager.createNotificationChannel(channel);
      Notification status = new Notification.Builder(this).setChannelId(CHANNEL_ID_SERVICE).build();
      startForeground(serviceID, status);
    }
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    if (intent != null && intent.getExtras() != null) {
      Bundle bundle = intent.getExtras();
      final Integer jobID = bundle.getInt(SmartScheduler.ALARM_JOB_ID_KEY, -1);

      SmartScheduler jobScheduler = SmartScheduler.getInstance(getApplicationContext());
      if (jobScheduler != null) {
        jobScheduler.onAlarmJobScheduled(jobID);
        return;
      }
    }
  }
}
