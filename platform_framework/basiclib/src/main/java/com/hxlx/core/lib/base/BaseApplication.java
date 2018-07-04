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
