package com.cybex.gma.client.ui.presenter;

import android.support.annotation.NonNull;

import com.cybex.gma.client.api.callback.CustomRequestCallback;
import com.cybex.gma.client.api.data.response.CustomData;
import com.cybex.gma.client.config.CacheConstants;
import com.cybex.gma.client.config.HttpConst;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.activity.CreateWalletActivity;
import com.cybex.gma.client.ui.activity.MainTabActivity;
import com.cybex.gma.client.ui.model.request.UserRegisterReqParams;
import com.cybex.gma.client.ui.request.UserRegisterRequest;
import com.hxlx.core.lib.common.cache.CacheUtil;
import com.hxlx.core.lib.mvp.lite.XPresenter;
import com.hxlx.core.lib.utils.EmptyUtils;
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
    public void createAccount(String accountname, String invitationCode, String publicKey) {
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

                        if(result.code == HttpConst.CODE_RESULT_SUCCESS){
                            Log.d("result.code", result.code);
                            UISkipMananger.launchIntent(getV(), MainTabActivity.class);
                        }else{
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
     * 用户名规则：12位小写字母a-z+数字1-5
     * @return
     */

    public boolean isUserNameValid(){
        String eosUsername = getV().getEOSUserName();
        String regEx = "^[a-z1-5]{12}$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher((eosUsername));
        boolean res = matcher.matches();
        return res;
    }

    /**
     * 代替监听器检查是否所有edittext输入框都不为空值
     * @return
     */
    public boolean isAllTextFilled(){
        if (EmptyUtils.isEmpty(getV().getPassword())
                || EmptyUtils.isEmpty(getV().getRepeatPassword())
                || EmptyUtils.isEmpty(getV().getEOSUserName())
                || EmptyUtils.isEmpty(getV().getInvCode())) {
            return false;
        }
        return true;
    }

    /**
     * 创建成功之后存
     * 公钥直接存（一个账户对应一个公钥）
     * 私钥+密码加密后存
     */
    public void saveKeypair(String publicKey, String privateKey, String username){
        CacheUtil.put(CacheConstants.PubKey_Prefix + username, publicKey);
        String data = privateKey + getV().getPassword();
        CacheUtil.put(CacheConstants.PriKey_Prefix + username, data, true);
    }

}
