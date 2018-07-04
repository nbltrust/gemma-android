package io.hypertrack.smart_scheduler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;


public class SmartSchedulerAlarmReceiver extends BroadcastReceiver {
  private static final String TAG = SmartSchedulerAlarmReceiver.class.getSimpleName();

  @Override
  public void onReceive(Context context, Intent intent) {
    Intent onAlarmReceiverServiceIntent =
        new Intent(context, SmartSchedulerAlarmReceiverService.class);
    onAlarmReceiverServiceIntent.putExtras(intent.getExtras());
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      context.startForegroundService(onAlarmReceiverServiceIntent);
    } else {
      context.startService(onAlarmReceiverServiceIntent);
    }
  }
}
