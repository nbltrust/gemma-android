package com.hxlx.core.lib.config;

import android.util.Log;


public class AppEnvironment {
    /**
     * 修改此值没有意义，因为使用者通常会在 application init 的时候从硬盘初始化此值。
     */
    private static ServerEnvironment sServerEnvironment = ServerEnvironment.Product;
    private static boolean sIsInTestMode = false;
    private static boolean sInitFlag = false;
    private static ServerEnvironmentStub sEnvironmentStub;
    private static TestModeStub sTestModeStub;

    public static synchronized void init(ServerEnvironmentStub environmentStub,
                                         TestModeStub testModeStub) {
        AppEnvironment.sEnvironmentStub = environmentStub;
        AppEnvironment.sTestModeStub = testModeStub;
        if (environmentStub != null) {
            int environment = sEnvironmentStub.getServerEnvironment();
            if (environment == ServerEnvironment.Sit.ordinal()) {
                sServerEnvironment = ServerEnvironment.Sit;
            } else if (environment == ServerEnvironment.Product.ordinal()) {
                sServerEnvironment = ServerEnvironment.Product;
            } else if (environment == ServerEnvironment.Uat.ordinal()) {
                sServerEnvironment = ServerEnvironment.Uat;
            }
        } else {
            throw new RuntimeException("ServerEnvironmentStub should not be null ");
        }

        if (sTestModeStub != null) {
            sIsInTestMode = sTestModeStub.isTestMode();
        } else {
            throw new RuntimeException("TestModeStub should not be null ");
        }
        sInitFlag = true;
    }

    public static synchronized ServerEnvironment getServerApiEnvironment() {
        checkInit();
        return sServerEnvironment;
    }

    public static synchronized boolean isInTestMode() {
        checkInit();
        return sIsInTestMode;
    }

    public static synchronized void setServerEnvironment(ServerEnvironment environment) {
        if (environment != null) {
            sServerEnvironment = environment;
            if (sEnvironmentStub != null) {
                sEnvironmentStub.setServerEnvironmentOrdinal(environment.ordinal());
            }
        }
    }

    public static synchronized void setIsInTestMode(boolean isInTestMode) {
        sIsInTestMode = isInTestMode;
        if (sTestModeStub != null) {
            sTestModeStub.setIsTestMode(isInTestMode);
        }
    }

    private static void checkInit() {
        if (!sInitFlag) {
            Log.e("AppEnvironment--", "AppEnvironment should  be init");
            throw new RuntimeException("AppEnvironment should  be init");
        }
    }

    public enum ServerEnvironment {
        Product, Sit, Uat
    }

    public interface ServerEnvironmentStub {
        int getServerEnvironment();

        void setServerEnvironmentOrdinal(int ordinal);
    }

    public interface TestModeStub {
        void setIsTestMode(boolean isTestMode);

        boolean isTestMode();
    }

}
