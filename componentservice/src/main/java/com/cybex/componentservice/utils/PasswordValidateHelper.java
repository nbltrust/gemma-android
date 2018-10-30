package com.cybex.componentservice.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cybex.componentservice.R;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.common.utils.HashGenUtil;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.siberiadante.customdialoglib.CustomFullDialog;

public class PasswordValidateHelper {

    private MultiWalletEntity walletEntity;
    private Context context;

    private String hintStr;
    private String confirmStr;
    private int iconRes;

    public PasswordValidateHelper(MultiWalletEntity walletEntity, Context context) {
        this.walletEntity = walletEntity;
        this.context = context;
        hintStr=context.getString(R.string.baseservice_pass_validate_hint);
        confirmStr=context.getString(R.string.baseservice_pass_validate_next);
        iconRes=R.drawable.ic_mask_close;
    }

//    public PasswordValidateHelper() {
//
//    }

    public void setHintStr(String hintStr) {
        this.hintStr = hintStr;
    }

    public void setConfirmStr(String confirmStr) {
        this.confirmStr = confirmStr;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }


    public void startValidatePassword(PasswordValidateCallback callback){
        int[] listenedItems = {R.id.baseservice_passwordvalidate_cancel_icon, R.id.baseservice_passwordvalidate_confirm};
        CustomFullDialog dialog = new CustomFullDialog(context,
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
                        GemmaToastUtils.showShortToast(context.getString(R.string.baseservice_pass_validate_tip_please_input_pass));
                        return;
                    } else {
                        String cypher = walletEntity.getCypher();
                        String hashPwd = HashGenUtil.generateHashFromText(pwd, HashGenUtil.TYPE_SHA256);
                        if (!hashPwd.equals(cypher)) {
                            GemmaToastUtils.showShortToast(
                                    context.getResources().getString(R.string.baseservice_pass_validate_ip_wrong_password));
                            if(callback!=null){
                                callback.onValidateFail(0);
                            }
                        } else {
                            //密码正确，
                            dialog.cancel();
                            if(callback!=null){
                                callback.onValidateSuccess(pwd);
                            }
                        }
                    }
                } else
                    {
                }
            }
        });
        dialog.show();
        EditText etPasword = dialog.findViewById(R.id.baseservice_passwordvalidate_et_password);
        ImageView ivCancel = dialog.findViewById(R.id.baseservice_passwordvalidate_cancel_icon);
        Button  btnConfirm= dialog.findViewById(R.id.baseservice_passwordvalidate_confirm);
        TextView  tvHint= dialog.findViewById(R.id.baseservice_passwordvalidate_hint);
        etPasword.setHint(String.format(context.getString(R.string.baseservice_pass_validate_et_hint),walletEntity.getWalletName()));
        tvHint.setText(hintStr);
        btnConfirm.setText(confirmStr);
        ivCancel.setImageResource(iconRes);
    }


    public interface PasswordValidateCallback {
        void onValidateSuccess(String password);
        void onValidateFail(int failedCount);
    }
}
