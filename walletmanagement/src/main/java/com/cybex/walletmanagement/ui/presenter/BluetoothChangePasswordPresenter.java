package com.cybex.walletmanagement.ui.presenter;


import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.db.util.DBCallback;
import com.cybex.componentservice.event.BluetoothChangePinEvent;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.DeviceOperationManager;
import com.cybex.componentservice.utils.FormatValidateUtils;
import com.cybex.walletmanagement.R;
import com.cybex.walletmanagement.ui.activity.BluetoothChangePasswordActivity;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.mvp.lite.XPresenter;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;


public class BluetoothChangePasswordPresenter extends XPresenter<BluetoothChangePasswordActivity> {



    private boolean changingPassword;

    @Override
    protected BluetoothChangePasswordActivity getV() {
        return super.getV();
    }


    public boolean isPasswordMatch() {
        return getV().getPassword().equals(getV().getRepeatPassword());
    }


    public void doChangePsw(final String oldPsw, final String newPsw, final int walletID, final String hint) {
       if(changingPassword)return;
        changingPassword=true;
        getV().showProgressDialog("");

        final MultiWalletEntity walletEntity = DBManager.getInstance().getMultiWalletEntityDao().getMultiWalletEntityByID(walletID);

        walletEntity.setPasswordTip(hint);
        DeviceOperationManager.getInstance().changePin(getV().toString(), walletEntity.getBluetoothDeviceName(), oldPsw, newPsw, new DeviceOperationManager.ChangePinCallback() {
            @Override
            public void onChangePinSuccess() {

                DBManager.getInstance().getMultiWalletEntityDao().saveOrUpateEntity(walletEntity, new DBCallback() {
                    @Override
                    public void onSucceed() {
                        changeWalletSuccess(walletID,newPsw);
                    }

                    @Override
                    public void onFailed(Throwable error) {
                        changeWalletSuccess(walletID,newPsw);
                    }
                });
            }

            @Override
            public void onChangePinUpdate(int state) {
                getV().dissmisProgressDialog();
                getV().showConfirmChangePswDialog();
            }

            @Override
            public void onChangePinFail() {
                changingPassword=false;
                getV().dissmisConfirmDialog();
                getV().dissmisProgressDialog();
                GemmaToastUtils.showShortToast(
                        getV().getResources().getString(R.string.walletmanage_psw_change_fail));
            }
        });
    }

    private void changeWalletSuccess(int walletID,String newPsw){
        changingPassword=false;
        getV().dissmisConfirmDialog();
        getV().dissmisProgressDialog();
        EventBusProvider.post(new BluetoothChangePinEvent(walletID, newPsw));
        GemmaToastUtils.showShortToast(
                getV().getResources().getString(R.string.walletmanage_psw_change_success));
        getV().finish();
    }

    public boolean isPasswordValid() {
        return FormatValidateUtils.isPasswordValid(getV().getPassword());
    }

    @Override
    public void detachV() {
        super.detachV();

    }

    public boolean isChangingPassword() {
        return changingPassword;
    }

    public void setChangingPassword(boolean changingPassword) {
        this.changingPassword = changingPassword;
    }
}