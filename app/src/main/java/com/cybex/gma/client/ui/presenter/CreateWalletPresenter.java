package com.cybex.gma.client.ui.presenter;

import android.support.annotation.NonNull;

import com.cybex.gma.client.api.callback.CustomRequestCallback;
import com.cybex.gma.client.api.data.response.CustomData;
import com.cybex.gma.client.config.CacheConstants;
import com.cybex.gma.client.config.HttpConst;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.db.entity.WalletEntity;
import com.cybex.gma.client.job.LibValidateJob;
import com.cybex.gma.client.manager.DBManager;
import com.cybex.gma.client.manager.LoggerManager;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.JNIUtil;
import com.cybex.gma.client.ui.activity.CreateWalletActivity;
import com.cybex.gma.client.ui.model.request.UserRegisterReqParams;
import com.cybex.gma.client.ui.model.response.UserRegisterResult;
import com.cybex.gma.client.ui.request.UserRegisterRequest;
import com.hxlx.core.lib.mvp.lite.XPresenter;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.GsonUtils;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;

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
            publicKey, final String passwordTip) {

        UserRegisterReqParams params = new UserRegisterReqParams();
        WalletEntity curWallet = DBManager.getInstance().getWalletEntityDao().getCurrentWalletEntity();
        if (EmptyUtils.isNotEmpty(curWallet)){

        }


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
                                saveAccount(publicKey, privateKey, password, accountname, passwordTip, txId);
                                GemmaToastUtils.showLongToast("本地创建成功，请等待链上确认！");
                                UISkipMananger.launchHome(getV());

                                LibValidateJob.startPolling(5000);
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
     *
     * @param publicKey
     * @param privateKey
     * @param password
     * @param eosUsername
     * @param passwordTip
     */

    public void saveAccount(
            final String publicKey, final String privateKey, final String
            password, final String eosUsername, final String passwordTip, final String txId) {

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
        LoggerManager.d("eosnamejson", eosNameJson);
        //设置currentEosName，创建钱包步骤中可以直接设置，因为默认eosNameJson中只会有一个用户名字符串
        walletEntity.setCurrentEosName(eosUsername);
        //设置是否为当前钱包，默认新建钱包为当前钱包
        walletEntity.setIsCurrentWallet(CacheConstants.IS_CURRENT_WALLET);
        //设置密码提示
        walletEntity.setPasswordTip(passwordTip);
        //设置为未备份
        walletEntity.setIsBackUp(CacheConstants.NOT_BACKUP);
        //设置未被链完全确认
        walletEntity.setIsConfirmLib(CacheConstants.NOT_CONFIRMED);
        //设置当前Transaction的Hash值
        walletEntity.setTxId(txId);
        //执行存入操作之前需要把其他钱包设置为非当前钱包
        if (walletNum > 0) {
            WalletEntity curWallet = DBManager.getInstance().getWalletEntityDao().getCurrentWalletEntity();
            curWallet.setIsCurrentWallet(CacheConstants.NOT_CURRENT_WALLET);
            DBManager.getInstance().getWalletEntityDao().saveOrUpateMedia(curWallet);
        }
        //最后执行存入操作，此前包此时为当前钱包
        DBManager.getInstance().getWalletEntityDao().saveOrUpateMedia(walletEntity);
    }

}
