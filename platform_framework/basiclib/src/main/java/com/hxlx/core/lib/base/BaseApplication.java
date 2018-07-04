package com.hxlx.core.lib.base;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.hxlx.core.lib.config.AppEnvironment;
import com.hxlx.core.lib.config.GlobalConfig;
import com.hxlx.core.lib.utils.SPManager;

/**
 * 全局Application
 */
public class BaseApplication extends Application {

  private static Context sAppContext = null;

  private static AppDelegate mAppDelegate;


  public static Context getAppContext() {
    return sAppContext;
  }

  /**
   * 退出应用
   */
  @SuppressWarnings("unused")
  public static void exitApp() {
    mAppDelegate.exit();
  }

  @Override
  protected void attachBaseContext(Context base) {
    GlobalConfig.setAppContext(base);
    super.attachBaseContext(base);
    MultiDex.install(this);
  }

  @Override
  public void onCreate() {
    super.onCreate();
    sAppContext = getApplicationContext();

    mAppDelegate = new AppDelegate(this);
    mAppDelegate.onCreate();
    initServerEnvironmentStub();
  }

  // private void initBugtags() {
  // BugtagsOptions.Builder builder = new BugtagsOptions.Builder();
  // builder.trackingLocation(true); // 是否获取位置，默认 true
  // builder.trackingCrashLog(true); // 是否收集闪退，默认 true
  // builder.trackingConsoleLog(true); // 是否收集控制台日志，默认 true
  // builder.trackingUserSteps(true); // 是否跟踪用户操作步骤，默认 true
  // builder.crashWithScreenshot(true); // 收集闪退是否附带截图，默认 true
  // builder.trackingAnr(true); // 收集 ANR，默认 false
  // builder.trackingBackgroundCrash(true); // 收集 独立进程 crash，默认 false
  // builder.versionName(BuildConfig.VERSION_NAME); // 自定义版本名称，默认 app versionName
  // builder.versionCode(BuildConfig.VERSION_CODE); // 自定义版本号，默认 app versionCode
  // builder.trackingNetworkURLFilter("(;*)");// 自定义网络请求跟踪的 url 规则，默认 null
  // builder.enableUserSignIn(true); // 是否允许显示用户登录按钮，默认 true
  // builder.startAsync(false); // 设置 为 true 则 SDK 会在异步线程初始化，节省主线程时间，默认 false
  // builder.startCallback(null); // 初始化成功回调，默认 null
  // if (BuildConfig.DEBUG) {
  // builder.remoteConfigDataMode(Bugtags.BTGDataModeProduction);
  // // 设置远程配置数据模式，默认Bugtags.BTGDataModeProduction 参见[文档]
  // } else {
  // builder.remoteConfigDataMode(Bugtags.BTGInvocationEventNone);
  // }
  // builder.remoteConfigCallback(null);
  // // 设置远程配置的回调函数，详见[文档]
  // builder.enableCapturePlus(false);
  // // Bugtags.BTGInvocationEventBubble,在app中显示圆形小球
  // // Bugtags.BTGInvocationEventNone,在app中不显示圆形小球
  // // Bugtags.BTGInvocationEventShake,通过摇一摇让圆形小球显示出来
  // Bugtags.start("744271240fa4cf2adb3db22988474a75", this,
  // BuildConfig.DEBUG ? Bugtags.BTGInvocationEventBubble : Bugtags.BTGInvocationEventNone,
  // builder.build());// 联机debug中上报异常
  // }

  private void initServerEnvironmentStub() {
    AppEnvironment.init(new AppEnvironment.ServerEnvironmentStub() {

      @Override
      public int getServerEnvironment() {
        return SPManager.getInstance().getServerEnvironment();
      }

      @Override
      public void setServerEnvironmentOrdinal(int ordinal) {
        SPManager.getInstance().setServerEnvironment(ordinal);
      }
    }, new AppEnvironment.TestModeStub() {
      @Override
      public void setIsTestMode(boolean isTestMode) {
        SPManager.getInstance().setIsInTestMode(isTestMode);
      }

      @Override
      public boolean isTestMode() {
        return SPManager.getInstance().isInTestMode();
      }
    });
  }

  @Override
  public void onLowMemory() {
    super.onLowMemory();
    mAppDelegate.onLowMemory();
  }

  @Override
  public void onTrimMemory(int level) {
    super.onTrimMemory(level);
    mAppDelegate.onTrimMemory(level);
  }

}
