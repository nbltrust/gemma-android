package com.cybex.gma.client.utils.taskscheduler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.cybex.gma.client.BuildConfig;


public class SafeDispatchHandler extends Handler {

  private static final String TAG = "SafeDispatchHandler";

  public SafeDispatchHandler(Looper looper) {
    super(looper);
  }

  public SafeDispatchHandler(Looper looper, Callback callback) {
    super(looper, callback);
  }

  public SafeDispatchHandler() {
    super();
  }

  public SafeDispatchHandler(Callback callback) {
    super(callback);
  }

  @Override
  public void dispatchMessage(Message msg) {
    if (BuildConfig.DEBUG) {
      super.dispatchMessage(msg);
    } else {
      try {
        super.dispatchMessage(msg);
      } catch (Exception e) {
        Log.d(TAG, "dispatchMessage Exception " + msg + " , " + e);
      } catch (Error error) {
        Log.d(TAG, "dispatchMessage error " + msg + " , " + error);
      }
    }
  }
}
