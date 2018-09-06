package com.cybex.gma.client.utils;

import android.app.Activity;

import com.cybex.gma.client.R;
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
                .setBackgroundColorRes(R.color.cornflowerBlue)
                .setDismissable(true)
                .show();
    }

    public static void showLongCommonAlert(Activity context, String info){
        Alerter.create(context)
                .setText(info)
                .showIcon(false)
                .setDuration(3000)
                .setContentGravity(Alert.TEXT_ALIGNMENT_GRAVITY)
                .setBackgroundColorRes(R.color.cornflowerBlue)
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
