package com.cybex.walletmanagement.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.cybex.walletmanagement.R;

/**
 * 界面跳转管理
 */
public final class UISkipMananger {

    private UISkipMananger() {

    }

//    **
////     * 跳转到投票页面
////     * @param context
////     */
////    public static void launchVote(Activity context, Bundle bundle) {
////        launchIntent(context, VoteActivity.class, bundle);
////    }





    public static void launchIntent(Activity context, Class<? extends Activity> cls) {
        launchNewTaskIntent(context, cls, null, false);
    }

    private static void launchIntent(Activity context, Class<? extends Activity> cls, Bundle bundle) {
        launchNewTaskIntent(context, cls, bundle, false);
    }

    private static void launchNewTaskIntent(Activity context, Class<? extends Activity> cls) {
        launchNewTaskIntent(context, cls, null, true);
    }

    private static void launchNewTaskIntent(
            Activity context, Class<? extends Activity> cls,
            Bundle bundle) {
        launchNewTaskIntent(context, cls, bundle, true);
    }

    private static void launchNewTaskIntent(
            Activity context, Class<? extends Activity> cls,
            Bundle bundle, boolean isNewTask) {
        Intent intent = new Intent(context, cls);
        if (isNewTask) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);

    }


    /**
     * 判断当前设备是手机还是平板，代码来自 Google I/O App for Android
     *
     * @param context
     * @return 平板返回 True，手机返回 False
     */
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static void jumpMarket(Context mContext) {
        jumpMarket(mContext, null);
    }

    /**
     * 跳转应用市场详情
     *
     * @param mContext
     * @param packageName
     */
    public static void jumpMarket(Context mContext, String packageName) {
        if (mContext == null) {
            return;
        }
        if (TextUtils.isEmpty(packageName)) {
            packageName = mContext.getPackageName();
        }
        String mAddress = "market://details?id=" + packageName;
        try {
            Intent marketIntent = new Intent("android.intent.action.VIEW");
            marketIntent.setData(Uri.parse(mAddress));
            mContext.startActivity(marketIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param context
     * @param activity 跳转Activity
     * @param bundle
     * @param isSingle
     */
    public static void startActivity(
            Context context,
            Class<? extends Activity> activity,
            Bundle bundle,
            boolean isSingle) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, activity);
        intent.setFlags(isSingle ? Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP : Intent.FLAG_ACTIVITY_NEW_TASK);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        context.startActivity(intent);
        //TODO
        //Activity act = (Activity) context;
        //act.overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);

    }

    public static void startActivity(Context context, Class<? extends Activity> activity, Bundle bundle) {
        startActivity(context, activity, bundle, true);
    }

    public static void startActivity(Context context, Class<? extends Activity> activity) {
        startActivity(context, activity, null);
    }

    public static void startActivity(Context context, Class<? extends Activity> activity, boolean isSingle) {
        startActivity(context, activity, null, isSingle);
    }


}
