package com.cybex.gma.client.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.cybex.gma.client.R;
import com.cybex.gma.client.ui.activity.AboutActivity;
import com.cybex.gma.client.ui.activity.BackUpPrivatekeyActivity;
import com.cybex.gma.client.ui.activity.BackUpWalletGuideActivity;
import com.cybex.gma.client.ui.activity.BarcodeScanActivity;
import com.cybex.gma.client.ui.activity.BluetoothBackupMneGuideActivity;
import com.cybex.gma.client.ui.activity.BuySellRamActivity;
import com.cybex.gma.client.ui.activity.BluetoothCreateWalletActivity;
import com.cybex.gma.client.ui.activity.CreateManageActivity;
import com.cybex.gma.client.ui.activity.CreateWalletActivity;
import com.cybex.gma.client.ui.activity.DelegateActivity;
import com.cybex.gma.client.ui.activity.FingerprintVerifyActivity;
import com.cybex.gma.client.ui.activity.GeneralSettingActivity;
import com.cybex.gma.client.ui.activity.GestureCreateActivity;
import com.cybex.gma.client.ui.activity.GestureVerifyActivity;
import com.cybex.gma.client.ui.activity.ImportWalletActivity;
import com.cybex.gma.client.ui.activity.InitialActivity;
import com.cybex.gma.client.ui.activity.MainTabActivity;
import com.cybex.gma.client.ui.activity.ManageWalletActivity;
import com.cybex.gma.client.ui.activity.SecuritySettingActivity;
import com.cybex.gma.client.ui.activity.TransferRecordActivity;
import com.cybex.gma.client.ui.activity.VoteActivity;
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
        launchIntent(context, InitialActivity.class);
    }

    /**
     * 跳转到主界面(MainTab)
     *
     * @param context
     */
    public static void launchHomeSingle(Activity context) {
        launchNewTaskIntent(context, MainTabActivity.class);
    }

    /**
     * 跳转到主界面(MainTab)
     *
     * @param context
     */
    public static void launchHome(Activity context) {
        launchIntent(context, MainTabActivity.class);
    }


    /**
     * 跳转到创建钱包页面
     *
     * @param context
     */
    public static void launchCreateWallet(Context context) {
        startActivity(context, CreateWalletActivity.class);
    }

    public static void launchVote(Activity context) {
        launchIntent(context, VoteActivity.class);
    }

    /**
     * 跳转到买卖RAM页面
     *
     * @param context
     */
    public static void launchRamTransaction(Activity context, Bundle bundle) {
        launchIntent(context, BuySellRamActivity.class, bundle);
    }

    /**
     * 跳转到创建钱包管理界面
     *
     * @param context
     */
    public static void launchCreateManage(Activity context) {
        launchIntent(context, CreateManageActivity.class);
    }

    /**
     * 跳转到导入钱包界面
     */
    public static void launchImportWallet(Activity context) {
        launchIntent(context, ImportWalletActivity.class);
    }

    /**
     * 跳转到钱包管理界面
     *
     * @param mContext
     */
    public static void launchWalletManagement(Activity mContext) {
        launchIntent(mContext, ManageWalletActivity.class);
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
     * 跳转到备份钱包界面
     *
     * @param mContext
     */
    public static void launchBakupGuide(Activity mContext) {
        launchIntent(mContext, BackUpWalletGuideActivity.class);
        //launchIntent(mContext, BackUpWalletGuideActivity.class, bundle);
    }

    /**
     * 跳转到资源抵押界面
     */

    public static void launchDelegate(Activity context, Bundle bundle) {
        launchIntent(context, DelegateActivity.class, bundle);
    }

    /**
     * 跳转到通用设置界面
     *
     * @param context
     */
    public static void launchGeneralSetting(Activity context) {
        launchIntent(context, GeneralSettingActivity.class);
    }


    /**
     * 跳转到创建手势密码
     *
     * @param context
     */
    public static void lauchCreateGestureActivity(Activity context) {

        launchIntent(context, GestureCreateActivity.class);
    }

    /**
     * 跳转到验证手势密码
     *
     * @param context
     */
    public static void launchVerifyGestureActivity(Activity context, Bundle bd) {

        launchIntent(context, GestureVerifyActivity.class, bd);
    }

    /**
     * 跳转到指纹验证
     *
     * @param context
     */
    public static void lauchFingerprintVerifyActivity(Activity context) {
        launchIntent(context, FingerprintVerifyActivity.class);
    }


    /**
     * 跳转到创建蓝牙钱包
     *
     * @param context
     */
    public static void skipCreateBluetoothWalletActivity(Activity context, Bundle bd) {
        launchIntent(context, BluetoothCreateWalletActivity.class, bd);

    }


    /**
     * 跳转到助记词
     *
     * @param context
     */
    public static void skipBackupMneGuideActivity(Activity context, Bundle bd) {
        launchIntent(context, BluetoothBackupMneGuideActivity.class, bd);
    }


    /**
     * 跳转到about界面
     *
     * @param context
     */
    public static void launchAbout(Activity context) {
        launchIntent(context, AboutActivity.class);
    }

    /**
     * 跳转到安全设置界面
     *
     * @param context
     */
    public static void launchSecuritySetting(Activity context) {
        launchIntent(context, SecuritySettingActivity.class);
    }

    /**
     * 跳转到二维码扫描界面
     */
    public static void launchBarcodeScan(Activity mContext) {
        launchIntent(mContext, BarcodeScanActivity.class);
    }

    /**
     * 备份私钥界面
     *
     * @param mContext
     */
    public static void launchBackUpPrivateKey(Activity mContext) {
        launchIntent(mContext, BackUpPrivatekeyActivity.class);
        //launchIntent(mContext, BackUpPrivatekeyActivity.class, bundle);
    }

    /**
     * 跳转到登录界面
     *
     * @param context
     */
    public static void launchLogin(Activity context) {
        launchIntent(context, InitialActivity.class);
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
