package com.cybex.gma.client.ui.presenter;


import com.cybex.componentservice.config.CacheConstants;

import com.cybex.componentservice.db.entity.EosWalletEntity;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.db.util.DBCallback;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.LoggerManager;


import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.ui.activity.BluetoothConfigWooKongBioActivity;
import com.hxlx.core.lib.mvp.lite.XPresenter;

import com.raizlabs.android.dbflow.sql.language.SQLite;


import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class BluetoothConfigWookongBioPresenter extends XPresenter<BluetoothConfigWooKongBioActivity> {
    /**
     * 创建本地蓝牙钱包
     */
    public void creatBluetoothWallet(final String deviceName, final String eosPublicKey) {

        Disposable subscribe = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> emitter) throws Exception {

                MultiWalletEntity multiWalletEntity = new MultiWalletEntity();

                multiWalletEntity.setWalletName("WOOKONG Bio");
                multiWalletEntity.setWalletType(CacheConstants.WALLET_TYPE_BLUETOOTH);
                multiWalletEntity.setIsBackUp(CacheConstants.NOT_BACKUP);
                multiWalletEntity.setBluetoothDeviceName(deviceName);
                multiWalletEntity.setIsCurrentWallet(CacheConstants.IS_CURRENT_WALLET);

                EosWalletEntity eosWalletEntity = new EosWalletEntity();
                eosWalletEntity.setMultiWalletEntity(multiWalletEntity);
                eosWalletEntity.setPublicKey(eosPublicKey);
                List<EosWalletEntity> eosWalletEntities = new ArrayList<>();
                eosWalletEntities.add(eosWalletEntity);

                multiWalletEntity.setEosWalletEntities(eosWalletEntities);


                List<MultiWalletEntity> multiWalletEntityList = DBManager.getInstance()
                        .getMultiWalletEntityDao()
                        .getMultiWalletEntityList();
                int walletCount = multiWalletEntityList.size();
                if (walletCount > 0) {
                    //将其他钱包设置为非当前钱包
                    MultiWalletEntity currentMultiWalletEntity = DBManager.getInstance()
                            .getMultiWalletEntityDao()
                            .getCurrentMultiWalletEntity();
                    if (currentMultiWalletEntity != null) {
                        currentMultiWalletEntity.setIsCurrentWallet(CacheConstants.NOT_CURRENT_WALLET);
                        currentMultiWalletEntity.save();
                    }
                }
                DBManager.getInstance()
                        .getMultiWalletEntityDao()
                        .saveOrUpateEntity(multiWalletEntity, new DBCallback() {
                            @Override
                            public void onSucceed() {
                                List<MultiWalletEntity> list =
                                        SQLite.select().from(MultiWalletEntity.class)
                                                .queryList();
                                LoggerManager.d("list=" + list);
                                //emitter.onNext(mnemonic);
                                emitter.onComplete();
                            }

                            @Override
                            public void onFailed(Throwable error) {
                                emitter.onError(error);
                                emitter.onComplete();
                            }
                        });
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String mnemonic) {


                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {

                    }
                });
    }


}
