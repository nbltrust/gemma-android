package com.cybex.componentservice.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.cybex.componentservice.R;
import com.cybex.componentservice.config.BaseConst;
import com.cybex.componentservice.config.RouterConst;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.manager.DeviceOperationManager;
import com.cybex.componentservice.manager.LoggerManager;
import com.extropies.common.MiddlewareInterface;
import com.siberiadante.customdialoglib.CustomDialog;
import com.siberiadante.customdialoglib.CustomFullDialog;

import me.jessyan.autosize.AutoSize;

public class WookongConnectHelper {

    private String tag;
    private MultiWalletEntity walletEntity;
    private Activity activity;

    private ImageView ivCancel;
    private Button btnReconnect;
    private TextView tvHint;
    private TextView tv_connecting;
    private View widgetLoading;
    private ConnectWookongBioCallback callback;
    private CustomFullDialog dialog;


    public WookongConnectHelper(String tag,MultiWalletEntity walletEntity, Activity activity) {
        this.tag = tag;
        this.walletEntity = walletEntity;
        this.activity = activity;
        AutoSize.autoConvertDensityOfGlobal(activity);
    }

    public void startConnectDevice(ConnectWookongBioCallback callback){
        this.callback=callback;
        int[] listenedItems = {R.id.baseservice_connect_close, R.id.baseservice_connect_reconnect};
        dialog = new CustomFullDialog(activity,
                R.layout.baseservice_dialog_wookong_connect, listenedItems, false, false, Gravity.BOTTOM);
        dialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
            @Override
            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {
                int i = view.getId();
                if (i == R.id.baseservice_connect_close) {
                    dialog.cancel();
                } else if (i == R.id.baseservice_connect_reconnect) {
                    showConnecting();
                    startConnectWookongBio();
                }
            }
        });
        dialog.show();
        ivCancel = dialog.findViewById(R.id.baseservice_connect_close);
        btnReconnect = dialog.findViewById(R.id.baseservice_connect_reconnect);
        tvHint = dialog.findViewById(R.id.baseservice_connect_hint);
        tv_connecting = dialog.findViewById(R.id.baseservice_connect_tv_connecting);
        widgetLoading = dialog.findViewById(R.id.baseservice_connect_loading);

        showConnecting();
        startConnectWookongBio();
    }

    private void startConnectWookongBio(){

        DeviceOperationManager.getInstance().connectDevice(tag, walletEntity.getBluetoothDeviceName(), new DeviceOperationManager.DeviceConnectCallback() {
                    @Override
                    public void onConnectStart() {

                    }

                    @Override
                    public void onConnectSuccess() {
                        innerGetDeviceInfo();
                    }

                    @Override
                    public void onConnectFailed() {
                        doConnectError();
                    }
                }
        );

    }

    private void innerGetDeviceInfo(){
        DeviceOperationManager.getInstance().getDeviceInfo(tag, walletEntity.getBluetoothDeviceName(), new DeviceOperationManager.GetDeviceInfoCallback() {
                    @Override
                    public void onGetSuccess(MiddlewareInterface.PAEW_DevInfo deviceInfo) {
                        if(dialog!=null){
                            dialog.dismiss();
                        }
                        if(callback!=null){
                            callback.onConnectSuccess();
                        }

                        if (deviceInfo.ucPINState == BaseConst.DEVICE_PIN_STATE_LOCKED) {
                            LoggerManager.d("direct connect:PIN LOCKED");
                            showPinLockedDialog();
                        }
                    }

                    @Override
                    public void onGetFail() {
                        doConnectError();
                    }
                }
        );
    }

    private void doConnectError() {
        if(callback!=null){
            callback.onConnectFail();
        }
        btnReconnect.setVisibility(View.VISIBLE);
        tv_connecting.setVisibility(View.GONE);
        widgetLoading.setVisibility(View.INVISIBLE);
        tvHint.setText(activity.getString(R.string.baseservice_wookong_connect_error_tip));
    }

    private void showConnecting(){
        btnReconnect.setVisibility(View.GONE);
        tv_connecting.setVisibility(View.VISIBLE);
        widgetLoading.setVisibility(View.VISIBLE);
        tv_connecting.setText(activity.getString(R.string.baseservice_wookong_bio_connecting)+walletEntity.getWalletName());
        tvHint.setText(activity.getString(R.string.baseservice_wookong_connect_tip));
    }


    private void showPinLockedDialog() {

        int[] listenedItems = {R.id.tv_cancel, R.id.tv_confirm};
        CustomDialog dialog = new CustomDialog(activity,
                R.layout.baseservice_dialog_pin_locked, listenedItems, false, Gravity.CENTER);
        dialog.setmWidth(SizeUtil.dp2px(259));
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

            }
        });
        dialog.setOnDialogItemClickListener(new CustomDialog.OnCustomDialogItemClickListener() {

            @Override
            public void OnCustomDialogItemClick(CustomDialog dialog, View view) {
                if (view.getId() == R.id.tv_cancel) {
                    dialog.cancel();
                } else if (view.getId() == R.id.tv_confirm) {
                    dialog.dismiss();
                    //jump to  format page
                    ARouter.getInstance().build(RouterConst.PATH_TO_FORMAT_PAGE).navigation();
                }
            }
        });
        dialog.show();
    }


    public interface ConnectWookongBioCallback {
        void onConnectSuccess();
        void onConnectFail();
    }
}
