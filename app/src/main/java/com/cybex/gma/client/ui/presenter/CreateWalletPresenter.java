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

import java.util.List;
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
     * @param passwordTip
     */

    public void saveAccount(final String publicKey, final String privateKey, final String
            password, final String eosUsername, final String passwordTip ){

        WalletEntity walletEntity = new WalletEntity();
        List<WalletEntity> walletEntityList = DBManager.getInstance().getMediaBeanDao().getWalletEntityList();
        //获取当前数据库中已存入的钱包个数
        int walletNum = walletEntityList.size();
        int index = walletNum + 1;
        //以默认钱包名称存入
        walletEntity.setWalletName(CacheConstants.DEFAULT_WALLETNAME_PREFIX + String.valueOf(index));
        walletEntity.setPublicKey(publicKey);//设置公钥
        final String cypher = JNIUtil.get_cypher(password, privateKey);
        walletEntity.setPrivateKey(cypher);//设置摘要
        walletEntity.setIsCurrentWallet(CacheConstants.IS_CURRENT_WALLET);//设置是否为当前钱包，默认新建钱包为当前钱包
        walletEntity.setIsBackUp(CacheConstants.NOT_BACKUP);//设置为未备份
        walletEntity.setEosName(eosUsername);//设置eosUsername
        walletEntity.setPasswordTip(passwordTip);//设置密码提示
        //执行存入操作之前需要把其他钱包设置为非当前钱包
        if (walletNum > 0){

            for (WalletEntity curWallet : walletEntityList){
                curWallet.setIsCurrentWallet(CacheConstants.NOT_CURRENT_WALLET);
                DBManager.getInstance().getMediaBeanDao().saveOrUpateMedia(curWallet);
            }
        }
        //最后执行存入操作，此前包此时为当前钱包
        DBManager.getInstance().getMediaBeanDao().saveOrUpateMedia(walletEntity);
    }

}
