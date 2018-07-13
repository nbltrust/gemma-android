package com.cybex.gma.client.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.cybex.gma.client.R;
import com.cybex.gma.client.ui.activity.TransferRecordActivity;
import com.hxlx.core.lib.utils.common.utils.AppManager;

/**
 * 界面跳转管理
 */
public final class UISkipMananger {

    private UISkipMananger() {

    }

    /**
     * 跳转到引导页
     *
     * @param context
     */
    public static void launchGuide(Activity context) {
    }

    /**
     * 调转到主界面
     *
     * @param context
     */
    public static void launchHome(Activity context) {


    }

    /**
     * 跳转到转账记录界面
     *
     * @param mContext
     */
    public static void launchTransferRecord(Activity mContext) {
        launchIntent(mContext, TransferRecordActivity.class);

    }

    /**
     * 跳转到转账记录界面
     */
    public static void launchTransferList(Activity mContext) {
        launchIntent(mContext, TransferRecordActivity.class);

    }

    /**
     * 跳转到登录界面
     *
     * @param context
     */
    public static void launchLogin(Activity context) {
    }


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

    public static void loginOut(Activity mContext) {
        AppManager.getAppManager().finishAllActivity();
        launchLogin(mContext);
    }


}
