package com.hxlx.core.lib.config;

import android.content.Context;


public class GlobalConfig {
  private static Context appContext;

  public static Context getAppContext() {
    return appContext;
  }

  public static void setAppContext(Context context) {
    appContext = context;
  }
}
