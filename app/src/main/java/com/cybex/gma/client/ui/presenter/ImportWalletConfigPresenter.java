package com.cybex.gma.client.ui.presenter;

import com.cybex.gma.client.R;
import com.cybex.gma.client.api.callback.JsonCallback;
import com.cybex.gma.client.config.CacheConstants;
import com.cybex.gma.client.db.entity.WalletEntity;
import com.cybex.gma.client.manager.DBManager;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.JNIUtil;
import com.cybex.gma.client.ui.fragment.ImportWalletConfigFragment;
import com.cybex.gma.client.ui.model.request.GetkeyAccountReqParams;
import com.cybex.gma.client.ui.model.response.GetKeyAccountsResult;
import com.cybex.gma.client.ui.request.GetKeyAccountsRequest;
import com.hxlx.core.lib.mvp.lite.XPresenter;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.GsonUtils;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import java.util.List;

public class ImportWalletConfigPresenter extends XPresenter<ImportWalletConfigFragment> {

    /**
     * 存入配置过后的钱包
     *
     * @param privateKey
     * @param password
     * @param passwordTips
     */
    public void saveConfigWallet(final String privateKey, final String password, final String passwordTips) {

        WalletEntity walletEntity = new WalletEntity();
        List<WalletEntity> walletEntityList = DBManager.getInstance().getWalletEntityDao().getWalletEntityList();
        //获取当前数据库中已存入的钱包个数，后以默认钱包名称存入
        int walletNum = walletEntityList.size();
        int index = walletNum + 1;
        walletEntity.setWalletName(CacheConstants.DEFAULT_WALLETNAME_PREFIX + String.valueOf(index));
        //根据私钥算出公钥
        final String pubKey = JNIUtil.get_public_key(privateKey);
        walletEntity.setPublicKey(pubKey);
        //设置摘要
        final String cypher = JNIUtil.get_cypher(password, privateKey);
        walletEntity.setCypher(cypher);
        walletEntity.setIsCurrentWallet(CacheConstants.IS_CURRENT_WALLET);// 设置是否为当前钱包，默认新建钱包为当前钱包
        walletEntity.setPasswordTip(passwordTips);   //设置密码提示
        walletEntity.setIsBackUp(CacheConstants.NOT_BACKUP);      //设置为未备份
        walletEntity.setIsConfirmLib(CacheConstants.IS_CONFIRMED); //导入的钱包设置为已被确认
        postGetKeyAccountRequest(walletEntity, walletEntityList, pubKey, walletNum);
    }

    /**
     * 根据公钥查询eosName列表
     *
     * @param publicKey
     */
    public void postGetKeyAccountRequest(
            WalletEntity walletEntity, List<WalletEntity> walletEntityList, String
            publicKey, int walletNum) {

        GetkeyAccountReqParams getkeyAccountReqParams = new GetkeyAccountReqParams();
        getkeyAccountReqParams.setPublic_key(publicKey);
        String json = GsonUtils.objectToJson(getkeyAccountReqParams);
        new GetKeyAccountsRequest(GetKeyAccountsResult.class)
                .setJsonParams(json)
                .getKeyAccountsRequest(new JsonCallback<GetKeyAccountsResult>() {
                    @Override
                    public void onStart(Request<GetKeyAccountsResult, ? extends Request> request) {
                        super.onStart(request);
                        getV().showProgressDialog(getV().getString(R.string.config_wallet_ing));

                    }

                    @Override
                    public void onError(Response<GetKeyAccountsResult> response) {
                        super.onError(response);
                        getV().dissmisProgressDialog();
                        GemmaToastUtils.showShortToast(getV().getString(R.string.import_wallet_failed));
                    }

                    @Override
                    public void onSuccess(Response<GetKeyAccountsResult> response) {
                        getV().dissmisProgressDialog();

                        if (response != null && response.body() != null && EmptyUtils.isNotEmpty(
                                response.body().account_names)) {
                            GetKeyAccountsResult result = response.body();
                            List<String> account_names = result.account_names;
                            final String curEosName = account_names.get(0);
                            final String eosNameJson = GsonUtils.objectToJson(account_names);
                            walletEntity.setEosNameJson(eosNameJson);
                            walletEntity.setCurrentEosName(curEosName);

                            //执行存入操作之前需要把其他钱包设置为非当前钱包
                            if (walletNum > 0) {
                                WalletEntity curWallet = DBManager.getInstance().getWalletEntityDao()
                                        .getCurrentWalletEntity();
                                curWallet.setIsCurrentWallet(CacheConstants.NOT_CURRENT_WALLET);
                                DBManager.getInstance().getWalletEntityDao().saveOrUpateEntity(curWallet);
                            }
                            //最后执行存入操作，此前包此时为当前钱包
                            DBManager.getInstance().getWalletEntityDao().saveOrUpateEntity(walletEntity);
                            //销毁当前Activity
                            getV().getActivity().finish();
                            UISkipMananger.launchHome(getV().getActivity());
                        } else {
                            GemmaToastUtils.showShortToast(getV().getString(R.string.import_wallet_failed));
                        }
                    }
                });
    }

    public boolean isPasswordMatch(){
        if (getV().getPassword().equals(getV().getRepeatPass()))return true;
        return false;
    }
}
