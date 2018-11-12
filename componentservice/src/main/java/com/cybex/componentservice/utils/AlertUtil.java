package com.cybex.componentservice.utils;

import android.app.Activity;

import com.cybex.componentservice.R;
import com.tapadoo.alerter.Alert;
import com.tapadoo.alerter.Alerter;

public class AlertUtil {

    /**
     * 显示标准（蓝底）alert
     * @param context
     * @param info
     */
    public static void showShortCommonAlert(Activity context, String info){
        Alerter.create(context)
                .setText(info)
                .showIcon(false)
                .setDuration(1500)
                .setContentGravity(Alert.TEXT_ALIGNMENT_GRAVITY)
                .setBackgroundColorRes(R.color.color_success)
                .setDismissable(true)
                .show();
    }

    public static void showLongCommonAlert(Activity context, String info){
        Alerter.create(context)
                .setText(info)
                .showIcon(false)
                .setDuration(3000)
                .setContentGravity(Alert.TEXT_ALIGNMENT_GRAVITY)
                .setBackgroundColorRes(R.color.color_success)
                .setDismissable(true)
                .show();
    }


    /**
     * 显示标准（绿底）alert
     * @param context
     * @param info
     */
    public static void showShortSuccessAlert(Activity context, String info){
        Alerter.create(context)
                .setText(info)
                .showIcon(false)
                .setDuration(1500)
                .setContentGravity(Alert.TEXT_ALIGNMENT_GRAVITY)
                .setBackgroundColorRes(R.color.successGreen)
                .setDismissable(true)
                .show();
    }

    public static void showLongSuccessAlert(Activity context, String info){
        Alerter.create(context)
                .setText(info)
                .showIcon(false)
                .setDuration(3000)
                .setContentGravity(Alert.TEXT_ALIGNMENT_GRAVITY)
                .setBackgroundColorRes(R.color.successGreen)
                .setDismissable(true)
                .show();
    }

    /**
     * 显示紧急（红底）alert
     * @param context
     * @param info
     */
    public static void showShortUrgeAlert(Activity context, String info){
        Alerter.create(context)
                .setText(info)
                .showIcon(false)
                .setDuration(1500)
                .setContentGravity(Alert.TEXT_ALIGNMENT_GRAVITY)
                .setBackgroundColorRes(R.color.scarlet)
                .setDismissable(true)
                .show();
    }

    public static void showLongUrgeAlert(Activity context, String info){
        Alerter.create(context)
                .setText(info)
                .showIcon(false)
                .setDuration(3000)
                .setContentGravity(Alert.TEXT_ALIGNMENT_GRAVITY)
                .setBackgroundColorRes(R.color.scarlet)
                .setDismissable(true)
                .show();
    }


}
