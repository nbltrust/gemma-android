package com.cybex.gma.client.ui.presenter;

import android.support.annotation.NonNull;

import com.cybex.gma.client.api.callback.CustomRequestCallback;
import com.cybex.gma.client.api.data.response.CustomData;
import com.cybex.gma.client.config.CacheConstants;
import com.cybex.gma.client.db.entity.WalletEntity;
import com.cybex.gma.client.manager.DBManager;
import com.cybex.gma.client.ui.JNIUtil;
import com.cybex.gma.client.ui.fragment.ImportWalletConfigFragment;
import com.cybex.gma.client.ui.model.request.GetkeyAccountReqParams;
import com.cybex.gma.client.ui.model.response.GetKeyAccountsResult;
import com.cybex.gma.client.ui.request.GetKeyAccountsRequest;
import com.hxlx.core.lib.mvp.lite.XPresenter;
import com.hxlx.core.lib.utils.GsonUtils;

import java.util.List;

import io.reactivex.disposables.Disposable;

public class ImportWalletConfigPresenter extends XPresenter<ImportWalletConfigFragment> {

    /**
     * 存入配置过后的钱包
     * @param privateKey
     * @param password
     * @param passwordTips
     */
    public void saveConfigWallet(final String privateKey, final String password, final String passwordTips ){

        WalletEntity walletEntity = new WalletEntity();
        List<WalletEntity> walletEntityList = DBManager.getInstance().getMediaBeanDao().getWalletEntityList();
        //获取当前数据库中已存入的钱包个数，后以默认钱包名称存入
        int walletNum = walletEntityList.size();
        int index = walletNum + 1;
        walletEntity.setWalletName(CacheConstants.DEFAULT_WALLETNAME_PREFIX + String.valueOf(index));
        //设置公钥
        final String pubKey = JNIUtil.get_public_key(privateKey);
        walletEntity.setPublicKey(pubKey);
        //设置摘要
        final String cypher = JNIUtil.get_cypher(password, privateKey);
        walletEntity.setPrivateKey(cypher);
        walletEntity.setIsCurrentWallet(CacheConstants.IS_CURRENT_WALLET);// 设置是否为当前钱包，默认新建钱包为当前钱包
        walletEntity.setPasswordTip(passwordTips);   //设置密码提示
        walletEntity.setIsBackUp(CacheConstants.NOT_BACKUP);      //设置为未备份
        //postGetKeyAccountRequest(walletEntity, walletEntityList, pubKey, walletNum);
    }


    /**
     * 根据公钥查询eosName列表
     * @param publicKey
     */
    public void postGetKeyAccountRequest(WalletEntity walletEntity, List<WalletEntity> walletEntityList, String
            publicKey, int walletNum){

        GetkeyAccountReqParams getkeyAccountReqParams = new GetkeyAccountReqParams();
        getkeyAccountReqParams.setPublic_key(publicKey);
        String json = GsonUtils.objectToJson(getkeyAccountReqParams);
        GetKeyAccountsRequest request = new GetKeyAccountsRequest(GetKeyAccountsResult.class);
        request.setJsonParams(json)
                .postJson(new CustomRequestCallback<GetKeyAccountsResult>() {
                    @Override
                    public void onBeforeRequest(@NonNull Disposable disposable) {
                        getV().showProgressDialog("正在获取账户信息...");
                    }

                    @Override
                    public void onSuccess(@NonNull CustomData<GetKeyAccountsResult> result) {
                        //todo 正确设置两个参数,这样写是否正确

                        List<String> account_names = result.result.account_names;
                        final String curEosName = account_names.get(0);
                        final String eosNameJson = GsonUtils.objectToJson(account_names);
                        walletEntity.setEosNameJson(eosNameJson);
                        walletEntity.setCurrentEosName(curEosName);

                        //执行存入操作之前需要把其他钱包设置为非当前钱包
                        if (walletNum > 0){
                            for (WalletEntity curWallet : walletEntityList){
                                curWallet.setIsCurrentWallet(CacheConstants.NOT_CURRENT_WALLET);
                                DBManager.getInstance().getMediaBeanDao().saveOrUpateMedia(curWallet);
                            }
                        }
                        //最后执行存入操作，此前包此时为当前钱包
                        DBManager.getInstance().getMediaBeanDao().saveOrUpateMedia(walletEntity);
                        getV().dissmisProgressDialog();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        getV().dissmisProgressDialog();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }




}
