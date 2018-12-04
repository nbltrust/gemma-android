package com.cybex.componentservice.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.cybex.componentservice.manager.LoggerManager;

/**
 * 屏幕尺寸相关方法
 */
public class SizeUtil {

    public static DisplayMetrics getDisplayMetrics() {
        return Resources.getSystem().getDisplayMetrics();
    }

    public static int getScreenWidth() {
        return getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return getDisplayMetrics().heightPixels;
    }

    public static int getStatusBarHeight() {
        return getInternalDimensionSize(Resources.getSystem(), "status_bar_height");
    }

    /**
     * 获取导航栏高度
     * @return
     */
    public static int getNavBarHeight() {
        int resourceId=0;
        int rid = Resources.getSystem().getIdentifier("config_showNavigationBar", "bool", "android");
//        LoggerManager.e("getNavBarHeight rid="+rid);
        if (rid!=0){
            resourceId = Resources.getSystem().getIdentifier("navigation_bar_height", "dimen", "android");
//            CMLog.show("高度："+resourceId);
//            CMLog.show("高度："+context.getResources().getDimensionPixelSize(resourceId) +"");
            return Resources.getSystem().getDimensionPixelSize(resourceId);
        }else
            return 0;
    }

    private static int getInternalDimensionSize(Resources res, String key) {
        int result = 0;
        int resourceId = res.getIdentifier(key, "dimen", "android");
        if (resourceId > 0) {
            result = res.getDimensionPixelSize(resourceId);
        }
        return result;
    }


    /**
     * 判断底部navigator是否已经显示
     * @param windowManager
     * @return
     */
    public static boolean hasSoftKeys(WindowManager windowManager){
        Display d = windowManager.getDefaultDisplay();


        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        d.getRealMetrics(realDisplayMetrics);


        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;


        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);


        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;


        return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
    }

    public static int px2dp(float pxValue) {
        final float scale = getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int dp2px(float dpValue) {
        final float scale = getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2sp(float pxValue) {
        final float fontScale = getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int sp2px(float spValue) {
        final float fontScale = getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

}
