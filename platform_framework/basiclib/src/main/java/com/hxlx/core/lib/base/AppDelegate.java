package com.hxlx.core.lib.base;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.alibaba.android.arouter.launcher.ARouter;
import com.hxlx.core.lib.BuildConfig;
import com.hxlx.core.lib.config.AppConfig;
import com.hxlx.core.lib.config.DBConfig;
import com.hxlx.core.lib.config.FileConfig;
import com.hxlx.core.lib.config.NetWorkConfig;
import com.hxlx.core.lib.utils.android.ActivityStackManager;
import com.hxlx.core.lib.utils.android.SysUtils;
import com.hxlx.core.lib.utils.android.logger.Log;
import com.hxlx.core.lib.utils.android.logger.LogLevel;
import com.hxlx.core.lib.utils.android.logger.MemoryLog;
import com.hxlx.core.lib.utils.android.view.ScreenUtils;

/**
 * 应用全局配置委托
 */
class AppDelegate implements Application.ActivityLifecycleCallbacks {

    private BaseApplication app;
    private static int appCreateCount;

    /**
     * 是否初始化webview
     */
    private boolean mIsWebViewInit;


    public AppDelegate(BaseApplication app) {
        this.app = app;
    }

    public void onCreate() {

        if (!app.getApplicationInfo().packageName.equals(SysUtils.getCurProcessName(app))) { return; }
        appCreateCount = 0;

        //  创建或更新数据库
        DBConfig.init(app);

        // Log 配置
        Log.init().logLevel(BuildConfig.DEBUG ? LogLevel.FULL : LogLevel.NONE);

        // 配置： ANR异常捕获 内存泄露捕获
        /**
        if (!LeakCanary.isInAnalyzerProcess(app)) {
            BlockCanary.install(app, new AppBlockCanaryContext()).start();
            LeakCanary.install(app);
        }*/

        // init  CrashReport
       //CrashReportConfig.init(app);
        //  Device ID
        AppConfig.setDeviceId(app);
        //  渠道号
        AppConfig.setChannel(app);

        // 生命周期
        app.registerActivityLifecycleCallbacks(this);


        if (BuildConfig.DEBUG) {
            SysUtils.getPhoneIp();
        }
        // 屏幕宽高
        ScreenUtils.init(app);

        if (BuildConfig.DEBUG) {           // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog();     // 打印日志
            ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(app);

    }


    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (MemoryLog.DEBUG_MEMORY) {// 堆栈和内存使用log
            MemoryLog.printMemory(activity.getClass().getName() + "-->onCreate");
        }
        ActivityStackManager.getInstance().pushActivity(activity);

        if (appCreateCount == 0) {
            // 配置文件系统
//            new FileConfig().init(activity, new FileConfig.OnFileCreatedListener() {
//                @Override
//                public void onCreated() {
//                    // 配置WebView,预先加载WEBVIEW提高反应速度，如果不使用weView可以忽略
//
//                }
//
//                @Override
//                public void onFailure() {
//                }
//            });

            if (SysUtils.hasNougat()) {
                // 配置网络监听
                NetWorkConfig.initNetNotify(activity);
            }

        }
        appCreateCount++;
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        ActivityStackManager.getInstance().popActivity(activity);
    }


    public void onLowMemory() {
    }

    public void onTrimMemory(int level) {
    }

    public void exit() {
        try {

            ActivityStackManager.getInstance().popAllActivity();


            ActivityManager activityMgr =
                    (ActivityManager) app.getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.killBackgroundProcesses(app.getPackageName());
            System.exit(0);
        } catch (Exception er) {
            Log.e(er, "exit app error");
        }

    }
}
