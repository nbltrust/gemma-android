package com.cybex.gma.client.utils.repeatclick;

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
        isClick2 = currentTime - lastClickTime <= SPACE_TIME;
        lastClickTime = currentTime;
        return isClick2;
    }
}