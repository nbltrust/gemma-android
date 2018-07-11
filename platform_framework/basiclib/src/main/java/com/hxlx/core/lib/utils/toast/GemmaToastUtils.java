package com.hxlx.core.lib.utils.toast;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.hxlx.core.lib.config.GlobalConfig;


public class GemmaToastUtils {

    private static final int TOAST_SHOW_TIME_SHORT = 1000;
    private static final int TOAST_SHOW_TIME_LONG = 2000;
    private static Toast mToast;
    private static Handler mHandler = new Handler(Looper.getMainLooper());
    private static Runnable r = new Runnable() {
        public void run() {
            mToast.cancel();
            mToast = null;// toast隐藏后，将其置为null
        }
    };

    public static void showShortToast(String message) {
        showCusTimeToast(message, TOAST_SHOW_TIME_SHORT);
    }

    public static void showLongToast(String message) {
        showCusTimeToast(message, TOAST_SHOW_TIME_LONG);
    }

    public static void showCusTimeToast(String message, int time) {
        mHandler.removeCallbacks(r);
        if (mToast == null) {// 只有mToast==null时才重新创建，否则只需更改提示文字
            mToast = Toast.makeText(GlobalConfig.getAppContext(), message, Toast.LENGTH_LONG);
            mToast.setDuration(Toast.LENGTH_LONG);
        } else { mToast.setText(message); }
        mHandler.postDelayed(r, time);// 延迟1秒隐藏toast
        mToast.show();
    }
}
