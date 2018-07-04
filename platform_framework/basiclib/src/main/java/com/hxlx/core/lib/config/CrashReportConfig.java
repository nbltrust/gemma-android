package com.hxlx.core.lib.config;

import android.content.Context;

/**
 * Crash报告配置
 */

public class CrashReportConfig {


    public static void init(Context app) {
        /***
         // 初始化Bugly
         if (!BuildConfig.DEBUG) {
         // 获取当前包名
         String packageName = app.getPackageName();
         // 获取当前进程名
         String processName = SysUtils.getProcessName(android.os.Process.myPid());
         // 设置是否为上报进程
         CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(app);
         strategy.setUploadProcess(processName == null || processName.equals(packageName));
         CrashReport.initCrashReport(app.getApplicationContext(),app.getString(R.string.bugly_app_id), false, strategy);
         } else {
         if (app.getResources().getBoolean(R.bool.crash_logger_enable)) {
         Thread.currentThread()
         .setUncaughtExceptionHandler(new CrashLogger(getAppContext()));
         } else {
         try{
         CustomActivityOnCrash.install(app);
         }catch (Exception e){
         Log.e(e,"CrashReportConfig on CustomActivityOnCrash init");
         }

         }

         }
         */

    }
}
