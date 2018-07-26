package com.cybex.gma.client.ui.presenter;

import android.support.annotation.NonNull;

import com.cybex.gma.client.api.callback.CustomRequestCallback;
import com.cybex.gma.client.api.data.response.CustomData;
import com.cybex.gma.client.config.CacheConstants;
import com.cybex.gma.client.config.HttpConst;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.db.entity.WalletEntity;
import com.cybex.gma.client.manager.DBManager;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.JNIUtil;
import com.cybex.gma.client.ui.activity.CreateWalletActivity;
import com.cybex.gma.client.ui.activity.MainTabActivity;
import com.cybex.gma.client.ui.model.request.UserRegisterReqParams;
import com.cybex.gma.client.ui.request.UserRegisterRequest;
import com.hxlx.core.lib.mvp.lite.XPresenter;
import com.hxlx.core.lib.utils.GsonUtils;
import com.hxlx.core.lib.utils.android.logger.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.disposables.Disposable;

public class CreateWalletPresenter extends XPresenter<CreateWalletActivity> {

    @Override
    protected CreateWalletActivity getV() {
        return super.getV();
    }

    /**
     * 创建账户
     *
     * @param accountname
     * @param invitationCode
     * @param publicKey
     */
    public void createAccount(
            final String accountname, final String password, final String invitationCode, final String
            publicKey) {

        UserRegisterReqParams params = new UserRegisterReqParams();
        params.setApp_id(ParamConstants.TYPE_APP_ID_CYBEX);
        params.setAccount_name(accountname);
        params.setInvitation_code(invitationCode);
        params.setPublic_key(publicKey);

        String json = GsonUtils.objectToJson(params);

        new UserRegisterRequest(CustomData.class)
                .setJsonParams(json)
                .postJson(new CustomRequestCallback<CustomData>() {
                    @Override
                    public void onBeforeRequest(@NonNull Disposable disposable) {
                        getV().showProgressDialog("正在创建...");
                    }

                    @Override
                    public void onSuccess(@NonNull CustomData<CustomData> result) {
                        getV().dissmisProgressDialog();

                        if (result.code == HttpConst.CODE_RESULT_SUCCESS) {
                            Log.d("result.code", result.code);
                            UISkipMananger.launchIntent(getV(), MainTabActivity.class);
                        } else {
                            Log.d("Error Code", result.code);
                            getV().showOnErrorInfo();
                        }
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

    /**
     * 调用底层方法生成公私钥对
     *
     * @return
     */
    public String[] getKeypair() {
        String[] keypair = JNIUtil.createKey().split(";");
        return keypair;
    }


    /**
     * 用户名规则：12位小写字母a-z+数字1-5
     *
     * @return
     */

    public boolean isUserNameValid() {
        String eosUsername = getV().getEOSUserName();
        String regEx = "^[a-z1-5]{12}$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher((eosUsername));
        boolean res = matcher.matches();
        return res;
    }

    /**
     * 调用DB Manager将钱包信息存入表中
     * @param publicKey
     * @param privateKey
     * @param password
     * @param eosUsername
     * @param isCurrentWallet 是否为当前钱包 1为是，0为否
     * @param passwordTip
     */

    public void saveAccount(final String publicKey, final String privateKey, final String
            password, final String eosUsername, final int isCurrentWallet, final String passwordTip ){

        WalletEntity walletEntity = new WalletEntity();
        //获取当前数据库中已存入的钱包个数
        int walletNum = DBManager.getInstance().getMediaBeanDao().getWalletEntityList().size();
        int index = walletNum + 1;
        //以默认钱包名称存入
        walletEntity.setWalletName(CacheConstants.DEFAULT_WALLETNAME_PREFIX + String.valueOf(index));
        walletEntity.setPublicKey(publicKey);
        final String cypher = JNIUtil.get_cypher(password, privateKey);
        walletEntity.setPrivateKey(cypher);
        walletEntity.setIsCurrentWallet(isCurrentWallet);
        walletEntity.setEosName(eosUsername);
        walletEntity.setPasswordTip(passwordTip);
        DBManager.getInstance().getMediaBeanDao().saveOrUpateMedia(walletEntity);
    }

}
