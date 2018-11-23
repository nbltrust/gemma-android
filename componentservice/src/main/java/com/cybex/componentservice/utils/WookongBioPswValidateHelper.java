package com.cybex.componentservice.utils;

import android.app.Activity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cybex.componentservice.R;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.manager.DeviceOperationManager;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.common.utils.HashGenUtil;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.siberiadante.customdialoglib.CustomDialog;
import com.siberiadante.customdialoglib.CustomFullDialog;

import me.jessyan.autosize.AutoSize;

public class WookongBioPswValidateHelper {

    private String tag;
    private MultiWalletEntity walletEntity;
    private Activity activity;

    private String hintStr;
    private int count;
    private boolean isVerifying;



    private String etHintStr;
    private String confirmStr;
    private int iconRes;

    public WookongBioPswValidateHelper(String tag,MultiWalletEntity walletEntity, Activity activity) {
        this.tag = tag;
        this.walletEntity = walletEntity;
        this.activity = activity;
        hintStr= this.activity.getString(R.string.baseservice_pass_validate_hint);
        confirmStr= this.activity.getString(R.string.baseservice_pass_validate_next);
        iconRes=R.drawable.ic_mask_close;
        AutoSize.autoConvertDensityOfGlobal(activity);
    }

    public void setHintStr(String hintStr) {
        this.hintStr = hintStr;
    }

    public void setConfirmStr(String confirmStr) {
        this.confirmStr = confirmStr;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }

    public void setEtHintStr(String etHintStr) {
        this.etHintStr = etHintStr;
    }


    public void startValidatePassword(PasswordValidateCallback callback){



        int[] listenedItems = {R.id.baseservice_passwordvalidate_cancel_icon, R.id.baseservice_passwordvalidate_confirm};
        CustomFullDialog dialog = new CustomFullDialog(activity,
                R.layout.baseservice_dialog_password_validate, listenedItems, false, false,Gravity.BOTTOM);
        dialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
            @Override
            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {
                int i = view.getId();
                if (i == R.id.baseservice_passwordvalidate_cancel_icon) {
                    dialog.cancel();
                } else if (i == R.id.baseservice_passwordvalidate_confirm) {
                    EditText etPasword = dialog.findViewById(R.id.baseservice_passwordvalidate_et_password);
                    String pwd = String.valueOf(etPasword.getText());
                    if (EmptyUtils.isEmpty(pwd)) {
                        GemmaToastUtils.showShortToast(activity.getString(R.string.baseservice_pass_validate_tip_please_input_pass));
                        return;
                    } else {
                        if(isVerifying)return;
                        isVerifying=true;
                        DeviceOperationManager.getInstance().verifyPin(tag, walletEntity.getBluetoothDeviceName(), pwd, new DeviceOperationManager.VerifyPinCallback() {
                            @Override
                            public void onVerifySuccess() {
                                isVerifying=false;
                                //密码正确，
                                dialog.cancel();
                                if(callback!=null){
                                    callback.onValidateSuccess(pwd);
                                }
                            }

                            @Override
                            public void onVerifyFail() {
                                isVerifying=false;
                                String passHint = walletEntity.getPasswordTip();
                                count++;
                                if(count>=3&&!TextUtils.isEmpty(passHint)){
                                    if(callback!=null){
                                        callback.onValidateFail(count);
                                    }
                                    count=0;
                                    //show  password tip
                                    showPasswordHintDialog();
                                }else{
                                    GemmaToastUtils.showShortToast(
                                            activity.getResources().getString(R.string.baseservice_pass_validate_ip_wrong_password));
                                    if(callback!=null){
                                        callback.onValidateFail(count);
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
        dialog.show();
        EditText etPasword = dialog.findViewById(R.id.baseservice_passwordvalidate_et_password);
        ImageView ivCancel = dialog.findViewById(R.id.baseservice_passwordvalidate_cancel_icon);
        Button  btnConfirm= dialog.findViewById(R.id.baseservice_passwordvalidate_confirm);
        TextView  tvHint= dialog.findViewById(R.id.baseservice_passwordvalidate_hint);

        if(TextUtils.isEmpty(etHintStr)){
            etPasword.setHint(String.format(activity.getString(R.string.baseservice_pass_validate_et_hint),walletEntity.getWalletName()));
        }else{
            etPasword.setHint(etHintStr);
        }
        tvHint.setText(hintStr);
        btnConfirm.setText(confirmStr);
        ivCancel.setImageResource(iconRes);
    }



    /**
     * 显示密码提示Dialog
     */
    private void showPasswordHintDialog() {
        int[] listenedItems = {R.id.tv_i_understand};
        CustomDialog dialog = new CustomDialog(activity,
                R.layout.baseservice_dialog_password_hint, listenedItems, false, Gravity.CENTER);
        dialog.setOnDialogItemClickListener(new CustomDialog.OnCustomDialogItemClickListener() {

            @Override
            public void OnCustomDialogItemClick(CustomDialog dialog, View view) {
                int i = view.getId();
                if (i == R.id.tv_i_understand) {
                    dialog.cancel();
                }

            }
        });
        dialog.show();

        TextView tv_pass_hint = dialog.findViewById(R.id.tv_password_hint_hint);
        if (EmptyUtils.isNotEmpty(walletEntity) ) {
            String passHint = walletEntity.getPasswordTip();
            String showInfo = activity.getString(R.string.baseservice_tip_password_hint) + " : " + passHint;
            tv_pass_hint.setText(showInfo);
        }
    }


    public interface PasswordValidateCallback {
        void onValidateSuccess(String password);
        void onValidateFail(int failedCount);
    }
}
