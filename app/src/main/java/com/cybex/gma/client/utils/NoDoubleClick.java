package com.cybex.gma.client.utils;

/**
 * Created by wanglin on 2018/7/25.
 */
public class NoDoubleClick {

    private static long lastClickTime;
    //这里设定两次点击时的时间间隔
    private final static int SPACE_TIME = 500;

    public static void recordLastClickTime() {
        lastClickTime = 0;
    }

    public synchronized static boolean isDoubleClick() {
        long currentTime = System.currentTimeMillis();
        boolean isClick2;
        if (currentTime - lastClickTime > SPACE_TIME) {
            isClick2 = false;
        } else {
            isClick2 = true;
        }
        lastClickTime = currentTime;
        return isClick2;
    }
}