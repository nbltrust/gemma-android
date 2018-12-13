package com.cybex.componentservice.manager;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.cybex.componentservice.R;
import com.cybex.componentservice.utils.listener.PermissionResultListener;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.Setting;

import java.lang.reflect.Field;
import java.util.List;

/**
 * AndPermission权限管理
 *
 * Created by wanglin on 2018/7/23.
 */
public class PermissionManager {

    private static PermissionManager instance;

    private Activity mContext;

    private PermissionManager(Activity context) {
        this.mContext = context;
    }

    public static PermissionManager getInstance(Activity mContext) {
        if (instance == null) {
            synchronized (PermissionManager.class) {
                if (instance == null) {
                    instance = new PermissionManager(mContext);
                }
            }
        }
        return instance;
    }

    /**
     * Request permissions.
     */
    public void requestPermission(final PermissionResultListener resultListener, String... permissions) {
        AndPermission.with(mContext)
                .runtime()
                .permission(permissions)
                .rationale(new RuntimeRationale())
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> permissions) {
                        if (resultListener != null) {
                            resultListener.onPermissionGranted();
                        }

                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(@NonNull List<String> permissions) {
                        if (resultListener != null) {
                            resultListener.onPermissionDenied(permissions);
                        }

                    }
                })
                .start();
    }


    /**
     * Request permissions.
     */
    public void requestPermission(String... permissions) {
        AndPermission.with(mContext)
                .runtime()
                .permission(permissions)
                .rationale(new RuntimeRationale())
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> permissions) {
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(@NonNull List<String> permissions) {
                        GemmaToastUtils.showShortToast(mContext.getString(R.string.baseservice_failure));
                        if (AndPermission.hasAlwaysDeniedPermission(mContext, permissions)) {
                            showSettingDialog(mContext, permissions);
                        }
                    }
                })
                .start();
    }


    /**
     * Display setting dialog.
     */
    public void showSettingDialog(Context context, final List<String> permissions) {
        List<String> permissionNames = Permission.transformText(context, permissions);
        String message = context.getString(R.string.baseservice_message_permission_always_failed,
                TextUtils.join("\n", permissionNames));
        AlertDialog dialog = new AlertDialog.Builder(context, R.style.common_dialog_alert)
                .setCancelable(false)
                .setTitle(R.string.baseservice_title_dialog)
                .setMessage(message)
                .setPositiveButton(R.string.baseservice_setting, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setPermission();
                    }
                })
                .setNegativeButton(R.string.baseservice_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();

        try {
            Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
            mAlert.setAccessible(true);
            Object mAlertController = mAlert.get(dialog);
            Field mMessage = mAlertController.getClass().getDeclaredField("mMessageView");
            mMessage.setAccessible(true);
            TextView mMessageView = (TextView) mMessage.get(mAlertController);
            mMessageView.setTextColor(Color.parseColor("#333333"));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }


    }

    /**
     * Set permissions.
     */
    private void setPermission() {
        AndPermission.with(mContext)
                .runtime()
                .setting()
                .onComeback(new Setting.Action() {
                    @Override
                    public void onAction() {
                        Toast.makeText(mContext, R.string.baseservice_message_setting_comeback, Toast.LENGTH_SHORT).show();
                    }
                })
                .start();
    }


    public final class RuntimeRationale implements Rationale<List<String>> {

        @Override
        public void showRationale(Context context, List<String> permissions, final RequestExecutor executor) {
            List<String> permissionNames = Permission.transformText(context, permissions);
            String message = context.getString(R.string.baseservice_message_permission_rationale,
                    TextUtils.join("\n", permissionNames));

            new android.app.AlertDialog.Builder(context)
                    .setCancelable(false)
                    .setTitle(R.string.baseservice_title_dialog)
                    .setMessage(message)
                    .setPositiveButton(R.string.baseservice_resume, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            executor.execute();
                        }
                    })
                    .setNegativeButton(R.string.baseservice_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            executor.cancel();
                        }
                    })
                    .show();
        }
    }


}
