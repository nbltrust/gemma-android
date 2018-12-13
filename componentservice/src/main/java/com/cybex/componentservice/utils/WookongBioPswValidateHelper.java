package com.cybex.componentservice.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.cybex.componentservice.R;
import com.cybex.componentservice.config.BaseConst;
import com.cybex.componentservice.config.RouterConst;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.db.util.DBCallback;
import com.cybex.componentservice.event.WookongFormattedEvent;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.DeviceOperationManager;
import com.cybex.componentservice.manager.LoggerManager;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.common.utils.HashGenUtil;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.siberiadante.customdialoglib.CustomDialog;
import com.siberiadante.customdialoglib.CustomFullDialog;

import java.util.List;

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

//    private CustomFullDialog confirmDialog;
//    private boolean isFormating;

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
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
//                if(isFormating){
//                    isFormating=false;
//                    DeviceOperationManager.getInstance().abortButton(activity.toString(),walletEntity.getBluetoothDeviceName());
//                }
            }
        });
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
                            public void onPinLocked() {
                                isVerifying=false;
                                showPinLockedDialog();
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

    private void showPinLockedDialog() {

        int[] listenedItems = {R.id.tv_cancel,R.id.tv_confirm};
        CustomDialog dialog = new CustomDialog(activity,
                R.layout.baseservice_dialog_pin_locked, listenedItems,false, Gravity.CENTER);
        dialog.setmWidth(SizeUtil.dp2px(259));
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

            }
        });
        dialog.setOnDialogItemClickListener(new CustomDialog.OnCustomDialogItemClickListener() {

            @Override
            public void OnCustomDialogItemClick(CustomDialog dialog, View view) {
                if(view.getId()== R.id.tv_cancel){
                    dialog.cancel();
                }else if(view.getId()== R.id.tv_confirm){
                    dialog.dismiss();

                    //jump to  format page
                    ARouter.getInstance().build(RouterConst.PATH_TO_FORMAT_PAGE).navigation();

//                    if(isFormating)return;
//                    isFormating=true;
//                    if(activity instanceof XActivity){
//                        ((XActivity) activity).showProgressDialog("");
//                    }
//                    DeviceOperationManager.getInstance().startFormat(activity.toString(), walletEntity.getBluetoothDeviceName(), new DeviceOperationManager.DeviceFormatCallback() {
//                        @Override
//                        public void onFormatStart() {
//
//                        }
//
//                        @Override
//                        public void onFormatSuccess() {
//                            isFormating=false;
//                            if(confirmDialog!=null&&confirmDialog.isShowing()){
//                                confirmDialog.dismiss();
//                            }
//                            DeviceOperationManager.getInstance().freeContext(activity.toString(), walletEntity.getBluetoothDeviceName(), null);
//                            //delete db data
//                            DBManager.getInstance().getMultiWalletEntityDao().deleteEntityAsync(walletEntity, new DBCallback() {
//                                @Override
//                                public void onSucceed() {
//                                    List<MultiWalletEntity> multiWalletEntityList = DBManager.getInstance().getMultiWalletEntityDao().getMultiWalletEntityList();
//                                    if(multiWalletEntityList.size()>0){
//                                        MultiWalletEntity walletEntity = multiWalletEntityList.get(0);
//                                        walletEntity.setIsCurrentWallet(1);
//                                        walletEntity.save();
//                                    }
//                                    //show dialog
//                                    showFormatDoneDialog();
////                                    int size = multiWalletEntityList.size();
////                                    if(size>0){
////                                        EventBusProvider.post(new WookongFormattedEvent(false));
////                                        ARouter.getInstance().build(RouterConst.PATH_TO_WALLET_HOME)
////                                                .navigation();
////                                    }else{
////                                        ARouter.getInstance().build(RouterConst.PATH_TO_WALLET_HOME)
////                                                .withInt(BaseConst.KEY_INIT_TYPE,BaseConst.APP_HOME_INITTYPE_TO_INITI_PAGE)
////                                                .navigation();
////                                    }
//                                }
//
//                                @Override
//                                public void onFailed(Throwable error) {
//                                    LoggerManager.e("deleteEntityAsync error="+error.getMessage());
//                                }
//                            });
//
//
//                        }
//
//                        @Override
//                        public void onFormatUpdate(int state) {
//                            if(activity instanceof XActivity){
//                                ((XActivity) activity).dissmisProgressDialog();
//                            }
//                            showConfirmFormatonCardDialog();
//                        }
//
//                        @Override
//                        public void onFormatFailed() {
//                            isFormating=false;
//                            if(confirmDialog!=null&&confirmDialog.isShowing()){
//                                confirmDialog.dismiss();
//                            }
//                            if(activity instanceof XActivity){
//                                ((XActivity) activity).dissmisProgressDialog();
//                            }
//                            GemmaToastUtils.showLongToast(activity.getString(R.string.walletmanage_format_fail));
//                        }
//                    });

                }
            }
        });
        dialog.show();


    }


    /**
     * 显示确认格式化Dialog
     */
//    private void showFormatDoneDialog() {
//        int[] listenedItems = {R.id.tv_cancel, R.id.tv_init};
//        CustomDialog dialog = new CustomDialog(activity,
//                R.layout.walletmanage_dialog_bluetooth_format_done, listenedItems, 0,false,false, Gravity.CENTER);
//
//        dialog.setOnDialogItemClickListener(new CustomDialog.OnCustomDialogItemClickListener() {
//
//            @Override
//            public void OnCustomDialogItemClick(CustomDialog dialog, View view) {
//                int size = DBManager.getInstance().getMultiWalletEntityDao().getMultiWalletEntityList().size();
//                if (view.getId() == R.id.tv_cancel) {
//                    dialog.cancel();
//                    if(size>0){
//                        activity.finish();
//                        EventBusProvider.post(new WookongFormattedEvent(false));
//                        ARouter.getInstance().build(RouterConst.PATH_TO_WALLET_HOME)
//                                .navigation();
//
//                    }else{
//                        ARouter.getInstance().build(RouterConst.PATH_TO_WALLET_HOME)
//                                .withInt(BaseConst.KEY_INIT_TYPE,BaseConst.APP_HOME_INITTYPE_TO_INITI_PAGE)
//                                .navigation();
//                    }
//
//
//
//                } else if (view.getId() == R.id.tv_init) {
//                    dialog.cancel();
//                    //UISkipMananger.skipBluetoothPaireActivity(rootView, new Bundle());
//                    if(size>0){
//                        activity.finish();
//                        EventBusProvider.post(new WookongFormattedEvent(true));
//                    }else{
//                        ARouter.getInstance().build(RouterConst.PATH_TO_WALLET_HOME)
//                                .withInt(BaseConst.KEY_INIT_TYPE,BaseConst.APP_HOME_INITTYPE_TO_INITI_PAGE_WOOKONG_PAIR)
//                                .navigation();
//                    }
//
//                }
//            }
//        });
//        dialog.show();
//    }


    /**
     * 显示按电源键确认格式化Dialog
     */
//    private void showConfirmFormatonCardDialog() {
//        if (confirmDialog != null) {
//            if(!confirmDialog.isShowing()&&isFormating){
//                confirmDialog.show();
//            }
//            return;
//        }
//        int[] listenedItems = {R.id.imv_back};
//        confirmDialog = new CustomFullDialog(activity,
//                R.layout.baseservice_dialog_bluetooth_button_confirm, listenedItems, false, Gravity.BOTTOM);
//        confirmDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialog) {
//                if(isFormating){
//                    isFormating=false;
//                    DeviceOperationManager.getInstance().abortButton(activity.toString(),walletEntity.getBluetoothDeviceName());
//                }
//            }
//        });
//        confirmDialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
//            @Override
//            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {
//                if (view.getId() == R.id.imv_back) {
//                    dialog.cancel();
//                }
//            }
//        });
//        confirmDialog.show();
//        TextView tvTip = confirmDialog.getAllView().findViewById(R.id.tv_tip);
//        tvTip.setText(activity.getString(R.string.walletmanage_tip_confirm_format));
//    }


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

//    public void release(){
//        if(isFormating){
//            isFormating=false;
//            DeviceOperationManager.getInstance().abortButton(activity.toString(),walletEntity.getBluetoothDeviceName());
//        }
//    }


    public interface PasswordValidateCallback {
        void onValidateSuccess(String password);
        void onValidateFail(int failedCount);
    }
}
