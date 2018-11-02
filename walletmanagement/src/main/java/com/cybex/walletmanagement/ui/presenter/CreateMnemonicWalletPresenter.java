package com.cybex.walletmanagement.ui.presenter;


import com.alibaba.android.arouter.launcher.ARouter;
import com.cybex.componentservice.config.BaseConst;
import com.cybex.componentservice.config.CacheConstants;
import com.cybex.componentservice.config.RouterConst;
import com.cybex.componentservice.db.entity.EosWalletEntity;
import com.cybex.componentservice.db.entity.EthWalletEntity;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.db.util.DBCallback;
import com.cybex.componentservice.event.CloseInitialPageEvent;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.gma.client.ui.JNIUtil;
import com.cybex.walletmanagement.BuildConfig;
import com.cybex.walletmanagement.ui.activity.CreateMnemonicWalletActivity;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.mvp.lite.XPresenter;
import com.hxlx.core.lib.utils.common.utils.HashGenUtil;
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
import seed39.Seed39;

public class CreateMnemonicWalletPresenter extends XPresenter<CreateMnemonicWalletActivity> {

    @Override
    protected CreateMnemonicWalletActivity getV() {
        return super.getV();
    }

    /**
     * 钱包名称是否在数据库存在
     *
     * @return boolean
     */
    public boolean isWalletNameValid() {
        String walletName = getV().getWalletName();
        MultiWalletEntity multiWalletEntity = DBManager.getInstance().getMultiWalletEntityDao().getMultiWalletEntity(walletName);
        return multiWalletEntity == null;
    }

    public boolean isPasswordMatch() {
        return getV().getPassword().equals(getV().getRepeatPassword());
    }

//    public void saveAccount(
//            final String publicKey, final String privateKey, final String
//            password, final String eosUsername, final String passwordTip, final String txId, final String invCode) {
//        WalletEntity walletEntity = new WalletEntity();
//        List<WalletEntity> walletEntityList = DBManager.getInstance().getWalletEntityDao().getWalletEntityList();
//        //获取当前数据库中已存入的钱包个数
//        int walletNum = walletEntityList.size();
//        int index = walletNum + 1;
//        //以默认钱包名称存入
//        walletEntity.setWalletName(CacheConstants.DEFAULT_WALLETNAME_PREFIX + String.valueOf(index));
//        //设置公钥
//        walletEntity.setPublicKey(publicKey);
//        //设置摘要
//        final String cypher = JNIUtil.get_cypher(password, privateKey);
//        walletEntity.setCypher(cypher);
//        //设置eosNameJson
//        List<String> account_names = new ArrayList<>();
//        account_names.add(eosUsername);
//        final String eosNameJson = GsonUtils.objectToJson(account_names);
//        walletEntity.setEosNameJson(eosNameJson);
//        //设置currentEosName，创建钱包步骤中可以直接设置，因为默认eosNameJson中只会有一个用户名字符串
//        walletEntity.setCurrentEosName(eosUsername);
//        //设置是否为当前钱包，默认新建钱包为当前钱包
//        walletEntity.setIsCurrentWallet(CacheConstants.IS_CURRENT_WALLET);
//        //设置密码提示
//        walletEntity.setPasswordTip(passwordTip);
//        walletEntity.setWalletType(0);
//        //设置为未备份
//        walletEntity.setIsBackUp(CacheConstants.NOT_BACKUP);
//        //设置被链上确认状态位未被确认
//        walletEntity.setIsConfirmLib(CacheConstants.NOT_CONFIRMED);
//        //设置当前Transaction的Hash值
//        walletEntity.setTxId(txId);
//        //设置邀请码
//        walletEntity.setInvCode(invCode);
//        //执行存入操作之前需要把其他钱包设置为非当前钱包
//        if (walletNum > 0) {
//            WalletEntity curWallet = DBManager.getInstance().getWalletEntityDao().getCurrentWalletEntity();
//            curWallet.setIsCurrentWallet(CacheConstants.NOT_CURRENT_WALLET);
//            DBManager.getInstance().getWalletEntityDao().saveOrUpateEntity(curWallet);
//        }
//        //最后执行存入操作，此前包此时为当前钱包
//        DBManager.getInstance().getWalletEntityDao().saveOrUpateEntity(walletEntity);
//    }

    public void creatWallet(final String walletName, final String password, final String passwordHint) {
        getV().showProgressDialog("");

        Disposable subscribe = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> emitter) throws Exception {


                MultiWalletEntity multiWalletEntity = new MultiWalletEntity();

                multiWalletEntity.setWalletName(walletName);
                multiWalletEntity.setWalletType(0);
                multiWalletEntity.setIsBackUp(CacheConstants.NOT_BACKUP);
                multiWalletEntity.setPasswordTip(passwordHint);
                multiWalletEntity.setCypher(HashGenUtil.generateHashFromText(password, HashGenUtil.TYPE_SHA256));
                multiWalletEntity.setIsCurrentWallet(1);

                //testcode
                final String mnemonic = Seed39.newMnemonic();
                String encryptMnemonic = Seed39.keyEncrypt(password, mnemonic);
                multiWalletEntity.setMnemonic(encryptMnemonic);

                String seed = Seed39.seedByMnemonic(mnemonic);
                String privKey = Seed39.deriveRaw(seed, BaseConst.MNEMONIC_PATH_ETH);
                String publicKey = Seed39.getEthereumPublicKeyFromPrivateKey(privKey);
                String address = Seed39.getEthereumAddressFromPrivateKey(privKey);
                String eosPriv = Seed39.deriveWIF(seed, BaseConst.MNEMONIC_PATH_EOS, false);
//                String eosPrivCompress = Seed39.deriveWIF(seed, "m/44'/194'/0'/0/", true);
                LoggerManager.d(" mnemonic=" + mnemonic);
                LoggerManager.d(" seed=" + seed);
                LoggerManager.d(" privKey=" + privKey);
                LoggerManager.d(" publicKey=" + publicKey);
                LoggerManager.d(" address=" + address);
                LoggerManager.d(" eosPriv=" + eosPriv);
//                LoggerManager.d(" eosPrivCompress="+eosPrivCompress);

                EthWalletEntity ethWalletEntity = new EthWalletEntity();
                ethWalletEntity.setAddress(address);
                ethWalletEntity.setPrivateKey(Seed39.keyEncrypt(password, privKey));
                ethWalletEntity.setPublicKey(publicKey);
                ethWalletEntity.setBackUp(false);
                ethWalletEntity.setMultiWalletEntity(multiWalletEntity);

                List<EthWalletEntity> ethWalletEntities = new ArrayList<>();
                ethWalletEntities.add(ethWalletEntity);
                multiWalletEntity.setEthWalletEntities(ethWalletEntities);

                String eosPublic = JNIUtil.get_public_key(eosPriv);
                LoggerManager.d(" eosPublic=" + eosPublic);
                EosWalletEntity eosWalletEntity = new EosWalletEntity();
                eosWalletEntity.setPrivateKey(Seed39.keyEncrypt(password, eosPriv));
                eosWalletEntity.setPublicKey(eosPublic);
                eosWalletEntity.setIsBackUp(0);
                eosWalletEntity.setMultiWalletEntity(multiWalletEntity);
                List<EosWalletEntity> eosWalletEntities = new ArrayList<>();
                eosWalletEntities.add(eosWalletEntity);
                multiWalletEntity.setEosWalletEntities(eosWalletEntities);

                LoggerManager.d("descrypt mnemonic=" + Seed39.keyDecrypt(password, encryptMnemonic));

                List<MultiWalletEntity> multiWalletEntityList = DBManager.getInstance().getMultiWalletEntityDao().getMultiWalletEntityList();
                int walletCount = multiWalletEntityList.size();
                if (walletCount > 0) {
                    MultiWalletEntity currentMultiWalletEntity = DBManager.getInstance().getMultiWalletEntityDao().getCurrentMultiWalletEntity();
                    if (currentMultiWalletEntity != null) {
                        currentMultiWalletEntity.setIsCurrentWallet(CacheConstants.NOT_CURRENT_WALLET);
                        currentMultiWalletEntity.save();
                    }
                }
                DBManager.getInstance().getMultiWalletEntityDao().saveOrUpateEntity(multiWalletEntity, new DBCallback() {
                    @Override
                    public void onSucceed() {
                        if(BuildConfig.DEBUG){
                            List<MultiWalletEntity> list =
                                    SQLite.select().from(MultiWalletEntity.class)
                                            .queryList();
                            LoggerManager.d("list=" + list);
                        }
                        emitter.onNext(mnemonic);
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
                        getV().dissmisProgressDialog();
                        ARouter.getInstance().build(RouterConst.PATH_TO_WALLET_HOME)
                                .withInt(BaseConst.KEY_INIT_TYPE, BaseConst.APP_HOME_INITTYPE_TO_BACKUP_MNEMONIC_GUIDE)
                                .withString(BaseConst.KEY_MNEMONIC, mnemonic)
                                .navigation();
                        if (getV().isInitial()) {
                            EventBusProvider.post(new CloseInitialPageEvent());
                        } else {
                        }
                        getV().finish();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        getV().dissmisProgressDialog();
                    }
                });
    }

    public boolean isPasswordValid() {
        return getV().getPassword().length() >= 8;
    }

    @Override
    public void detachV() {
        super.detachV();

    }
}