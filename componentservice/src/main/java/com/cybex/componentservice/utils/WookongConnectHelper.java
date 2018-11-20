package com.cybex.componentservice.utils;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cybex.componentservice.R;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.manager.DeviceOperationManager;
import com.extropies.common.MiddlewareInterface;
import com.siberiadante.customdialoglib.CustomFullDialog;

import me.jessyan.autosize.AutoSize;

public class WookongConnectHelper {

    private MultiWalletEntity walletEntity;
    private Activity activity;

    private ImageView ivCancel;
    private Button btnReconnect;
    private TextView tvHint;
    private TextView tv_connecting;
    private View widgetLoading;
    private ConnectWookongBioCallback callback;
    private CustomFullDialog dialog;


    public WookongConnectHelper(MultiWalletEntity walletEntity, Activity activity) {
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

        DeviceOperationManager.getInstance().connectDevice(activity.toString(), walletEntity.getBluetoothDeviceName(), new DeviceOperationManager.DeviceConnectCallback() {
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
        DeviceOperationManager.getInstance().getDeviceInfo(activity.toString(), walletEntity.getBluetoothDeviceName(), new DeviceOperationManager.GetDeviceInfoCallback() {
                    @Override
                    public void onGetSuccess(MiddlewareInterface.PAEW_DevInfo deviceInfo) {
                        if(dialog!=null){
                            dialog.dismiss();
                        }
                        if(callback!=null){
                            callback.onConnectSuccess();
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


    public interface ConnectWookongBioCallback {
        void onConnectSuccess();
        void onConnectFail();
    }
}
