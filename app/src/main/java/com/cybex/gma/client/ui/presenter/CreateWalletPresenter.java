package com.cybex.gma.client.ui.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.cybex.componentservice.api.callback.CustomRequestCallback;
import com.cybex.componentservice.api.callback.JsonCallback;
import com.cybex.componentservice.api.data.response.CustomData;
import com.cybex.componentservice.db.entity.WalletEntity;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.CacheConstants;
import com.cybex.gma.client.config.HttpConst;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.job.LibValidateJob;
import com.cybex.gma.client.manager.DBManager;
import com.cybex.gma.client.manager.LoggerManager;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.JNIUtil;
import com.cybex.gma.client.ui.activity.CreateWalletActivity;
import com.cybex.gma.client.ui.model.request.GetAccountReqParams;
import com.cybex.gma.client.ui.model.request.UserRegisterReqParams;
import com.cybex.gma.client.ui.model.response.AccountInfo;
import com.cybex.gma.client.ui.model.response.UserRegisterResult;
import com.cybex.gma.client.ui.request.GetAccountinfoRequest;
import com.cybex.gma.client.ui.request.UserRegisterRequest;
import com.cybex.gma.client.utils.AlertUtil;
import com.hxlx.core.lib.mvp.lite.XPresenter;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.GsonUtils;
import com.hxlx.core.lib.utils.common.utils.AppManager;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import java.util.ArrayList;
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
            final String accountname, final String password, final String invitationCode, final String privateKey,
            final String
            publicKey, final String passwordTip, final String invCode) {
        UserRegisterReqParams params = new UserRegisterReqParams();

        params.setApp_id(ParamConstants.TYPE_APP_ID_CYBEX);
        params.setAccount_name(accountname);
        params.setInvitation_code(invitationCode);
        params.setPublic_key(publicKey);

        String json = GsonUtils.objectToJson(params);

        new UserRegisterRequest(UserRegisterResult.class)
                .setJsonParams(json)
                .postJson(new CustomRequestCallback<UserRegisterResult>() {
                    @Override
                    public void onBeforeRequest(@NonNull Disposable disposable) {
                        getV().showProgressDialog("正在创建...");
                    }

                    @Override
                    public void onSuccess(@NonNull CustomData<UserRegisterResult> data) {
                        getV().dissmisProgressDialog();

                        if (data.code == HttpConst.CODE_RESULT_SUCCESS) {
                            UserRegisterResult registerResult = data.result;
                            if (registerResult != null) {
                                String txId = registerResult.txId;
                                saveAccount(publicKey, privateKey, password, accountname, passwordTip, txId, invCode);
                                getV().finish();
                                AppManager.getAppManager().finishAllActivity();
                                UISkipMananger.launchCreateManage(getV());
                                LibValidateJob.startPolling(10000);
                            }
                        } else {
                            getV().showOnErrorInfo(data.code);
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
     * 重新请求
     */
    public void reCreateAccount(WalletEntity walletEntity) {
        UserRegisterReqParams params = new UserRegisterReqParams();

        params.setTxId(walletEntity.getTxId());
        params.setApp_id(ParamConstants.TYPE_APP_ID_CYBEX);
        params.setAccount_name(walletEntity.getCurrentEosName());
        params.setInvitation_code(walletEntity.getInvCode());
        params.setPublic_key(walletEntity.getPublicKey());

        String json = GsonUtils.objectToJson(params);

        new UserRegisterRequest(UserRegisterResult.class)
                .setJsonParams(json)
                .postJson(new CustomRequestCallback<UserRegisterResult>() {
                    @Override
                    public void onBeforeRequest(@NonNull Disposable disposable) {

                    }

                    @Override
                    public void onSuccess(@NonNull CustomData<UserRegisterResult> data) {
                        getV().dissmisProgressDialog();

                        if (data.code == HttpConst.CODE_RESULT_SUCCESS) {
                            UserRegisterResult registerResult = data.result;
                            if (registerResult != null) {
                                String txId = registerResult.txId;
                                updateWalletConfirmStatus(walletEntity, txId);
                                LibValidateJob.startPolling(10000);
                            }
                        } else if(data.code == HttpConst.EOSNAME_INVALID) {
                            if (EmptyUtils.isNotEmpty(walletEntity) && walletEntity.getIsConfirmLib().equals
                                    (CacheConstants.CONFIRM_FAILED)){
                                //todo 如果是重传失败且提示EOS用户名被占用，表示该账户名被其他人抢注，此时需要提示用户选择其他用户名，具体方式？
                                //UISkipMananger.launchCreateWallet(getV());
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

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

    public boolean isPasswordMatch(){
        return getV().getPassword().equals(getV().getRepeatPassword());
    }

    /**
     * 调用DB Manager将钱包信息存入表中
     *
     * @param publicKey
     * @param privateKey
     * @param password
     * @param eosUsername
     * @param passwordTip
     */

    public void saveAccount(
            final String publicKey, final String privateKey, final String
            password, final String eosUsername, final String passwordTip, final String txId, final String invCode) {


        WalletEntity walletEntity = new WalletEntity();
        List<WalletEntity> walletEntityList = DBManager.getInstance().getWalletEntityDao().getWalletEntityList();
        //获取当前数据库中已存入的钱包个数
        int walletNum = walletEntityList.size();
        int index = walletNum + 1;
        //以默认钱包名称存入
        walletEntity.setWalletName(CacheConstants.DEFAULT_WALLETNAME_PREFIX + String.valueOf(index));
        //设置公钥
        walletEntity.setPublicKey(publicKey);
        //设置摘要
        final String cypher = JNIUtil.get_cypher(password, privateKey);
        walletEntity.setCypher(cypher);
        //设置eosNameJson
        List<String> account_names = new ArrayList<>();
        account_names.add(eosUsername);
        final String eosNameJson = GsonUtils.objectToJson(account_names);
        walletEntity.setEosNameJson(eosNameJson);
        //设置currentEosName，创建钱包步骤中可以直接设置，因为默认eosNameJson中只会有一个用户名字符串
        walletEntity.setCurrentEosName(eosUsername);
        //设置是否为当前钱包，默认新建钱包为当前钱包
        walletEntity.setIsCurrentWallet(CacheConstants.IS_CURRENT_WALLET);
        //设置密码提示
        walletEntity.setPasswordTip(passwordTip);
        walletEntity.setWalletType(0);
        //设置为未备份
        walletEntity.setIsBackUp(CacheConstants.NOT_BACKUP);
        //设置被链上确认状态位未被确认
        walletEntity.setIsConfirmLib(CacheConstants.NOT_CONFIRMED);
        //设置当前Transaction的Hash值
        walletEntity.setTxId(txId);
        //设置邀请码
        walletEntity.setInvCode(invCode);
        //执行存入操作之前需要把其他钱包设置为非当前钱包
        if (walletNum > 0) {
            WalletEntity curWallet = DBManager.getInstance().getWalletEntityDao().getCurrentWalletEntity();
            curWallet.setIsCurrentWallet(CacheConstants.NOT_CURRENT_WALLET);
            DBManager.getInstance().getWalletEntityDao().saveOrUpateEntity(curWallet);
        }
        //最后执行存入操作，此前包此时为当前钱包
        DBManager.getInstance().getWalletEntityDao().saveOrUpateEntity(walletEntity);
    }

    /**
     * 删除当前钱包，并更新当前钱包为最后一个钱包
     * 数据库中若没有钱包，表示这是第一次创建，不做操作
     */
    public void deleteCurWalletInRequest(){
        WalletEntity curWallet = DBManager.getInstance().getWalletEntityDao().getCurrentWalletEntity();
        if (EmptyUtils.isNotEmpty(curWallet)){
            DBManager.getInstance().getWalletEntityDao().deleteEntity(curWallet);
            //删除钱包完之后查询数据库是否为空
            List<WalletEntity> walletEntityList = DBManager.getInstance().getWalletEntityDao().getWalletEntityList();
            if (EmptyUtils.isNotEmpty(walletEntityList)){
                //数据库中还有钱包，更新当前钱包为最后一个钱包
                WalletEntity newCurWallt = DBManager.getInstance().getWalletEntityDao().getWalletEntityByID
                        (walletEntityList.size());
                newCurWallt.setIsCurrentWallet(CacheConstants.IS_CURRENT_WALLET);
                DBManager.getInstance().getWalletEntityDao().saveOrUpateEntity(newCurWallt);
            }
        }
    }

    public void updateWalletConfirmStatus(WalletEntity walletEntity ,String txId){
        walletEntity.setTxId(txId);
        DBManager.getInstance().getWalletEntityDao().saveOrUpateEntity(walletEntity);
    }

    public void verifyAccount(String account_name){
        GetAccountReqParams params = new GetAccountReqParams();
        params.setAccount_name(account_name);

        String jsonParams = GsonUtils.objectToJson(params);

        new GetAccountinfoRequest(AccountInfo.class)
                .setJsonParams(jsonParams)
                .getAccountInfo(new JsonCallback<AccountInfo>() {
                    @Override
                    public void onStart(Request<AccountInfo, ? extends Request> request) {
                        super.onStart(request);
                        getV().showProgressDialog(getV().getString(R.string.verifying_account));
                    }

                    @Override
                    public void onSuccess(Response<AccountInfo> response) {
                        getV().dissmisProgressDialog();
                        if (response != null && response.body() != null){
                            AccountInfo accountInfo = response.body();
                            String account_name_onChain = accountInfo.getAccount_name();
                            if (account_name.equals(account_name_onChain)){
                                //找到该用户名，该用户名不可以使用
                                AlertUtil.showLongUrgeAlert(getV(), getV().getString(R.string.eos_name_used));
                            }
                        }
                    }

                    @Override
                    public void onError(Response<AccountInfo> response) {
                        super.onError(response);
                        if (response.code() == HttpConst.SERVER_INTERNAL_ERR){
                            //未找到该用户名，该用户名可以使用
                            String[] keyPair = getKeypair();
                            final String publicKey = keyPair[0];
                            final String priateKey = keyPair[1];
                            Bundle bundle = new Bundle();
                            bundle.putString("account_name", getV().getEOSUserName());
                            bundle.putString("public_key", publicKey);
                            bundle.putString("private_key", priateKey);
                            bundle.putString("password", getV().getPassword());
                            bundle.putString("passwordTip", getV().getPassHint());
                            UISkipMananger.launchChooseActivateMethod(getV(), bundle);
                        }else {
                            GemmaToastUtils.showLongToast(getV().getString(R.string.tip_check_network));
                        }
                        getV().dissmisProgressDialog();
                    }
                });

    }


}
