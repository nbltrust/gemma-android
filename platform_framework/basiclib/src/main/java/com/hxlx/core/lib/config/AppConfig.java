package com.hxlx.core.lib.config;

import android.content.Context;
import android.text.TextUtils;

import com.hxlx.core.lib.utils.SPManager;
import com.hxlx.core.lib.utils.android.SysUtils;

/**
 * App配置
 */

public class AppConfig {
    public static void setDeviceId(Context app) {
        if (TextUtils.isEmpty(SPManager.getInstance().getDeviceId())) {
            SPManager.getInstance().putDeviceId(SysUtils.getDeviceId(app));
        }
    }

    public static void setChannel(Context app) {

    }
}
