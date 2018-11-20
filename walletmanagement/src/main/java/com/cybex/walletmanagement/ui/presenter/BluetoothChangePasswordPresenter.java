package com.cybex.walletmanagement.ui.presenter;


import android.text.TextUtils;

import com.cybex.componentservice.db.entity.EosWalletEntity;
import com.cybex.componentservice.db.entity.EthWalletEntity;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.db.util.DBCallback;
import com.cybex.componentservice.event.RefreshWalletPswEvent;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.DeviceOperationManager;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.componentservice.utils.FormatValidateUtils;
import com.cybex.walletmanagement.R;
import com.cybex.walletmanagement.ui.activity.BluetoothChangePasswordActivity;
import com.cybex.walletmanagement.ui.activity.ChangePasswordActivity;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.mvp.lite.XPresenter;
import com.hxlx.core.lib.utils.common.utils.HashGenUtil;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import seed39.Seed39;

public class BluetoothChangePasswordPresenter extends XPresenter<BluetoothChangePasswordActivity> {

    @Override
    protected BluetoothChangePasswordActivity getV() {
        return super.getV();
    }



    public boolean isPasswordMatch() {
        return getV().getPassword().equals(getV().getRepeatPassword());
    }

    public void doChangePsw(final String oldPsw, final String newPsw, final int walletID,final String hint){
        getV().showProgressDialog("");

        Disposable subscribe = Observable.create(new ObservableOnSubscribe<MultiWalletEntity>() {
            @Override
            public void subscribe(final ObservableEmitter<MultiWalletEntity> emitter) throws Exception {


                final MultiWalletEntity walletEntity = DBManager.getInstance().getMultiWalletEntityDao().getMultiWalletEntityByID(walletID);

                walletEntity.setPasswordTip(hint);

                DeviceOperationManager.getInstance().changePin(BluetoothChangePasswordPresenter.this.toString(), walletEntity.getBluetoothDeviceName(), oldPsw, newPsw, new DeviceOperationManager.ChangePinCallback() {
                    @Override
                    public void onChangePinSuccess() {


                        DBManager.getInstance().getMultiWalletEntityDao().saveOrUpateEntity(walletEntity, new DBCallback() {
                            @Override
                            public void onSucceed() {
//                                List<MultiWalletEntity> list =
//                                        SQLite.select().from(MultiWalletEntity.class)
//                                                .queryList();
//                                LoggerManager.d("list=" + list);
                                emitter.onNext(walletEntity);
                                emitter.onComplete();
                            }

                            @Override
                            public void onFailed(Throwable error) {
                                emitter.onError(error);
                                emitter.onComplete();
                            }
                        });

                    }

                    @Override
                    public void onChangePinFail() {
                        emitter.onError(new Exception("change pin failed"));
                        emitter.onComplete();
                    }
                });


            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<MultiWalletEntity>() {
                    @Override
                    public void accept(MultiWalletEntity walletEntity) {
                        getV().dissmisProgressDialog();
                        EventBusProvider.post(new RefreshWalletPswEvent(walletEntity));
                        GemmaToastUtils.showShortToast(
                                getV().getResources().getString(R.string.walletmanage_psw_change_success));
                        getV().finish();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        LoggerManager.d("throwable="+throwable.getMessage());
                        getV().dissmisProgressDialog();
                        GemmaToastUtils.showShortToast(
                                getV().getResources().getString(R.string.walletmanage_psw_change_fail));
                    }
                });



    }

    public boolean isPasswordValid() {
        return FormatValidateUtils.isPasswordValid(getV().getPassword());
    }

    @Override
    public void detachV() {
        super.detachV();

    }
}