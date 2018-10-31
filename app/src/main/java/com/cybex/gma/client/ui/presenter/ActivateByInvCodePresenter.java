package com.cybex.gma.client.ui.presenter;

import android.support.annotation.NonNull;

import com.cybex.componentservice.api.callback.CustomRequestCallback;
import com.cybex.componentservice.api.data.response.CustomData;
import com.cybex.componentservice.db.entity.EosWalletEntity;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.db.entity.WalletEntity;
import com.cybex.componentservice.config.CacheConstants;
import com.cybex.componentservice.db.util.DBCallback;
import com.cybex.gma.client.config.HttpConst;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.job.TimeStampValidateJob;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.gma.client.ui.JNIUtil;
import com.cybex.gma.client.ui.fragment.ActivateByInvCodeFragment;
import com.cybex.gma.client.ui.model.request.UserRegisterReqParams;
import com.cybex.gma.client.ui.model.response.UserRegisterResult;
import com.cybex.gma.client.ui.request.UserRegisterRequest;
import com.hxlx.core.lib.mvp.lite.XPresenter;
import com.hxlx.core.lib.utils.GsonUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

public class ActivateByInvCodePresenter extends XPresenter<ActivateByInvCodeFragment> {
    /**
     * 创建账户
     */
    public void createAccount(String eos_username, String public_key, String invCode){

        UserRegisterReqParams params = new UserRegisterReqParams();
        params.setApp_id(ParamConstants.APP_ID_GEMMA);
        params.setAccount_name(eos_username);
        params.setInvitation_code(invCode);
        params.setPublic_key(public_key);

        String jsonParams = GsonUtils.objectToJson(params);

        LoggerManager.d(jsonParams);

        new UserRegisterRequest(UserRegisterResult.class)
                .setJsonParams(jsonParams)
                .postJson(new CustomRequestCallback<UserRegisterResult>() {
                    @Override
                    public void onBeforeRequest(@NonNull Disposable disposable) {
                        if (getV() != null){
                            getV().showProgressDialog("");
                        }
                    }

                    @Override
                    public void onSuccess(@NonNull CustomData<UserRegisterResult> data) {
                        if (getV() != null){
                            getV().dissmisProgressDialog();

                            if (data.code == HttpConst.CODE_RESULT_SUCCESS) {
                                UserRegisterResult registerResult = data.result;
                                if (registerResult != null) {
                                    String txId = registerResult.txId;
                                    updateWallet(eos_username, txId, invCode);
                                    TimeStampValidateJob.executedCreateLogic(eos_username, public_key);
                                }
                            } else {
                                LoggerManager.d("err");
                            }
                        }

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        if (getV() != null){
                            //getV().dissmisProgressDialog();
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (getV() != null){
                            //getV().dissmisProgressDialog();
                        }
                    }
                });
    }


    public void updateWallet(
             final String eosUsername, final String txId, final String invCode) {

        MultiWalletEntity multiWalletEntity = DBManager.getInstance().getMultiWalletEntityDao()
                .getCurrentMultiWalletEntity();

        List<EosWalletEntity> eosWalletEntities = DBManager.getInstance().getMultiWalletEntityDao()
                .getCurrentMultiWalletEntity().getEosWalletEntities();
        EosWalletEntity walletEntity = eosWalletEntities.get(0);

        //设置eosNameJson
        List<String> account_names = new ArrayList<>();
        account_names.add(eosUsername);
        final String eosNameJson = GsonUtils.objectToJson(account_names);
        walletEntity.setEosNameJson(eosNameJson);
        //设置currentEosName，创建钱包步骤中可以直接设置，因为默认eosNameJson中只会有一个用户名字符串
        walletEntity.setCurrentEosName(eosUsername);
        //设置当前Transaction的Hash值
        walletEntity.setTxId(txId);
        //设置邀请码
        walletEntity.setInvCode(invCode);

        //更新钱包
        eosWalletEntities.remove(0);
        eosWalletEntities.add(walletEntity);
        multiWalletEntity.setEosWalletEntities(eosWalletEntities);

        DBManager.getInstance().getMultiWalletEntityDao().saveOrUpateEntity(multiWalletEntity, new DBCallback() {
            @Override
            public void onSucceed() {

            }

            @Override
            public void onFailed(Throwable error) {

            }
        });



        //获取当前数据库中已存入的钱包个数
        //int walletNum = walletEntityList.size();
        //int index = walletNum + 1;
        //以默认钱包名称存入
        //walletEntity.setWalletName(CacheConstants.DEFAULT_WALLETNAME_PREFIX + String.valueOf(index));
        //设置公钥
        //walletEntity.setPublicKey(publicKey);
        //设置摘要
        //final String cypher = JNIUtil.get_cypher(password, privateKey);
        //walletEntity.setCypher(cypher);

        //设置是否为当前钱包，默认新建钱包为当前钱包
        //walletEntity.setIsCurrentWallet(CacheConstants.IS_CURRENT_WALLET);
        //设置密码提示
        //walletEntity.setPasswordTip(passwordTip);
        //walletEntity.setWalletType(0);
        //设置为未备份
        //walletEntity.setIsBackUp(CacheConstants.NOT_BACKUP);
        //设置被链上确认状态位未被确认
        //walletEntity.setIsConfirmLib(CacheConstants.NOT_CONFIRMED);

        //执行存入操作之前需要把其他钱包设置为非当前钱包
//        if (walletNum > 0) {
//            WalletEntity curWallet = DBManager.getInstance().getWalletEntityDao().getCurrentWalletEntity();
//            curWallet.setIsCurrentWallet(CacheConstants.NOT_CURRENT_WALLET);
//            DBManager.getInstance().getWalletEntityDao().saveOrUpateEntity(curWallet);
//        }
        //最后执行存入操作，此前包此时为当前钱包
        //DBManager.getInstance().getWalletEntityDao().saveOrUpateEntity(walletEntity);

    }
}
