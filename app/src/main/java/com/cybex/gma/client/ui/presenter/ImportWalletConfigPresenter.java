package com.cybex.gma.client.ui.presenter;

import android.content.Context;

import com.cybex.componentservice.api.callback.JsonCallback;
import com.cybex.componentservice.db.entity.WalletEntity;
import com.cybex.gma.client.GmaApplication;
import com.cybex.gma.client.R;
import com.cybex.componentservice.config.CacheConstants;
import com.cybex.gma.client.config.HttpConst;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.event.ImportResultEvent;
import com.cybex.gma.client.event.ValidateResultEvent;
import com.cybex.gma.client.job.JobUtils;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.JNIUtil;
import com.cybex.gma.client.ui.fragment.ImportWalletConfigFragment;
import com.cybex.gma.client.ui.model.request.GetAccountInfoReqParams;
import com.cybex.gma.client.ui.model.request.GetBlockReqParams;
import com.cybex.gma.client.ui.model.request.GetkeyAccountReqParams;
import com.cybex.gma.client.ui.model.response.AccountInfo;
import com.cybex.gma.client.ui.model.response.GetKeyAccountsResult;
import com.cybex.gma.client.ui.request.EOSConfigInfoRequest;
import com.cybex.gma.client.ui.request.GetAccountinfoRequest;
import com.cybex.gma.client.ui.request.GetBlockRequest;
import com.cybex.gma.client.ui.request.GetKeyAccountsRequest;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.mvp.lite.XPresenter;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.GsonUtils;
import com.hxlx.core.lib.utils.common.utils.AppManager;
import com.hxlx.core.lib.utils.common.utils.DateUtil;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.hypertrack.smart_scheduler.Job;
import io.hypertrack.smart_scheduler.SmartScheduler;

public class ImportWalletConfigPresenter extends XPresenter<ImportWalletConfigFragment> {


    private static final String ACTIVE_KEY = "active";
    private static final String OWNER_KEY = "owner";

    private static final int FAIL_EMPTY_ACCOUNT = 3;
    private static final int FAIL_USERNAME_USED = 4;

    private static final int TIMESTAMP = 1;
    private static final int CREATED = 2;
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
        walletEntity.setIsBackUp(CacheConstants.ALREADY_BACKUP);      //设置为未备份
        walletEntity.setIsConfirmLib(CacheConstants.IS_CONFIRMED); //导入的钱包设置为已被确认
        postGetKeyAccountRequest(walletEntity, pubKey, walletNum);
    }

    /**
     * 根据公钥查询eosName列表
     *
     * @param publicKey
     */
    public void postGetKeyAccountRequest(
            WalletEntity walletEntity, String
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
                    }


                    @Override
                    public void onSuccess(Response<GetKeyAccountsResult> response) {
                        if (getV() != null){
                            getV().dissmisProgressDialog();
                            if (response != null && response.body() != null && EmptyUtils.isNotEmpty(
                                    response.body().account_names) ) {
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
                                AppManager.getAppManager().finishAllActivity();
                                UISkipMananger.launchHomeSingle(getV().getActivity());
                            }
                        }
                    }

                    @Override
                    public void onError(Response<GetKeyAccountsResult> response) {
                        super.onError(response);
                        if (getV() != null){
                            getV().dissmisProgressDialog();
                        }
                    }

                });
    }

    public boolean isPasswordMatch(){
        return getV().getPassword().equals(getV().getRepeatPass());
    }



    /**
     * 根据公钥查询对应的eos账户
     * @param public_key
     */
    public void getKeyAccounts(String public_key){
        GetkeyAccountReqParams params = new GetkeyAccountReqParams();
        params.setPublic_key(public_key);
        String jsonParams = GsonUtils.objectToJson(params);

        new GetKeyAccountsRequest(GetKeyAccountsResult.class)
                .setJsonParams(jsonParams)
                .getKeyAccountsRequest(new JsonCallback<GetKeyAccountsResult>() {
                    @Override
                    public void onStart(Request<GetKeyAccountsResult, ? extends Request> request) {
                        getV().showProgressDialog(getV().getString(R.string.config_wallet_ing));
                    }

                    @Override
                    public void onSuccess(Response<GetKeyAccountsResult> response) {
                        if (response != null && response.body() != null){
                            //找到此账号
                            GetKeyAccountsResult result = response.body();
                            List<String> account_names = result.account_names;
                            if (EmptyUtils.isNotEmpty(account_names) && account_names.size() != 0){
                                LoggerManager.d("getKeyAccountsRes", account_names.get(0));
                                String curEosName = account_names.get(0);
                                getAccount(curEosName, public_key);
                            }

                        }else if (response != null && response.body() != null && response.code() == HttpConst
                                .SERVER_INTERNAL_ERR ){
                            //未找到此账号
                            LoggerManager.d("getKeyAccountsErr");
                            ValidateResultEvent event = new ValidateResultEvent();
                            event.setFail_type(FAIL_EMPTY_ACCOUNT);
                            EventBusProvider.postSticky(event);
                        }
                    }

                    @Override
                    public void onError(Response<GetKeyAccountsResult> response) {
                        super.onError(response);
                    }
                });
    }

    public void getAccount(String account_name, String public_key){
        GetAccountInfoReqParams params = new GetAccountInfoReqParams();
        params.setAccount_name(account_name);

        String jsonParams = GsonUtils.objectToJson(params);

        new GetAccountinfoRequest(AccountInfo.class)
                .setJsonParams(jsonParams)
                .getAccountInfo(new JsonCallback<AccountInfo>() {
                    @Override
                    public void onSuccess(Response<AccountInfo> response) {
                        LoggerManager.d("getAccount");
                        if (response != null && response.body() != null){
                            AccountInfo info = response.body();
                            final String created = info.getCreated();

                            //检查公钥是否在结构体里
                            List<AccountInfo.PermissionsBean> permissions = info.getPermissions();
                            boolean isContain = false;
                            for (AccountInfo.PermissionsBean permission : permissions) {
                                //检查active key中是否是此公钥
                                if (isBeanContainsPublicKey(permission, public_key)) {
                                    //如果公钥在账户中,继续调用getInfo接口
                                    isContain = true;
                                    break;
                                } else {
                                    //公钥不在账户中
                                    //失败，弹框提示账户名已被使用，查询数据库中是否有其他钱包
                                    ValidateResultEvent event = new ValidateResultEvent();
                                    event.setFail_type(FAIL_USERNAME_USED);
                                    EventBusProvider.postSticky(event);
                                }
                            }

                            if (isContain){
                                getInfo(created, account_name, public_key);
                            }
                        }

                    }

                    @Override
                    public void onError(Response<AccountInfo> response) {
                        LoggerManager.d("getAccountsErr");
                        super.onError(response);
                    }
                });
    }

    /**
     * 判断公钥是否在结构体中
     * @param permissionsBean
     * @param pubKey
     *
    "perm_name": "active",
    "parent": "owner",
    "required_auth": {
    "threshold": 1,
    "keys": [
    {
    "key": "EOS5jXTcRmb1crPADnAfQRWGPe2cE1fbEvFVTW4JKGf6Ffr9HoFVo",
    "weight": 1
    }
    ],
    "accounts": [],
    "waits": []
    }
     * @return
     */
    public boolean isBeanContainsPublicKey(AccountInfo.PermissionsBean permissionsBean, String pubKey){
        if (permissionsBean.getPerm_name().equals(ACTIVE_KEY) || permissionsBean.getPerm_name().equals(OWNER_KEY)){
            AccountInfo.PermissionsBean.RequiredAuthBean requiredAuthBean = permissionsBean.getRequired_auth();
            List<AccountInfo.PermissionsBean.RequiredAuthBean.KeysBean> keysBeanList = requiredAuthBean.getKeys();
            String public_key = keysBeanList.get(0).getKey();
            return pubKey.equals(public_key);
        }
        return false;
    }

    /**
     * 调取RPC get_info接口查询当前链的配置信息
     * 获取lib
     */
    public void getInfo(String created, String account_name, String public_key) {
        new EOSConfigInfoRequest(String.class)
                .getInfo(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LoggerManager.d("getInfo");
                        if(response.body() != null){
                            String infoJson = response.body();
                            try {
                                JSONObject obj = new JSONObject(infoJson);
                                if (obj != null) {
                                    String lib_num = obj.optString("last_irreversible_block_num");
                                    LoggerManager.d("lib", lib_num);

                                    getBlock(lib_num, created, account_name, public_key);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        LoggerManager.d("getInfoErr");
                        super.onError(response);
                    }
                });
    }

    /**
     * 根据block_num查询当前块的信息
     * 需要在callback获取timestamp
     * @param block_num
     */
    public void getBlock(String block_num, String created, String account_name, String public_key){

        GetBlockReqParams params = new GetBlockReqParams();
        params.setBlock_num_or_id(block_num);

        String jsonParams = GsonUtils.objectToJson(params);

        new GetBlockRequest(String.class)
                .setJsonParams(jsonParams)
                .getBlock(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LoggerManager.d("getBlock");
                        if(response.body() != null){
                            String infoJson = response.body();
                            try {
                                JSONObject obj = new JSONObject(infoJson);
                                if (obj != null) {
                                    String timestamp = obj.optString("timestamp");
                                    LoggerManager.d("timestamp", timestamp);
                                    //比较timestamp 和 created
                                    String laterTimestamp = getLaterTimeStamp(timestamp, created);
                                    if (laterTimestamp.equals(timestamp)){
                                        //比较成功
                                        verifyAccount(account_name, public_key);
                                        removePollingJob();
                                    }else {
                                        //失败，轮询
                                        startValidatePolling(10000, created, account_name, public_key);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        LoggerManager.d("getBlockErr");
                        super.onError(response);
                    }
                });
    }



    public void verifyAccount(String account_name, String public_key){
        GetAccountInfoReqParams params = new GetAccountInfoReqParams();
        params.setAccount_name(account_name);

        String jsonParams = GsonUtils.objectToJson(params);

        new GetAccountinfoRequest(AccountInfo.class)
                .setJsonParams(jsonParams)
                .getAccountInfo(new JsonCallback<AccountInfo>() {
                    @Override
                    public void onSuccess(Response<AccountInfo> response) {
                        LoggerManager.d("verifyAccount");
                        if (response != null && response.body() != null && response.code() != HttpConst.SERVER_INTERNAL_ERR){
                            AccountInfo info = response.body();

                            //检查公钥是否在结构体里
                            List<AccountInfo.PermissionsBean> permissions = info.getPermissions();
                            boolean isContain = false;
                            for (AccountInfo.PermissionsBean permission : permissions) {
                                //检查active key中是否是此公钥

                                if(isBeanContainsPublicKey(permission, public_key)) {
                                    isContain = true;
                                    break;
                                }
                            }

                            if (isContain){
                                //导入成功
                                ImportResultEvent event_success = new ImportResultEvent();
                                event_success.setSuccess(true);
                                EventBusProvider.post(event_success);
                                LoggerManager.d("event.success at post", event_success.isSuccess());
                                LoggerManager.d("importSuccess Event");
                            }else {
                                //公钥不在账户中
                                //失败，弹框提示账户名已被使用，查询数据库中是否有其他钱包
                                ImportResultEvent event_fail = new ImportResultEvent();
                                EventBusProvider.post(event_fail);
                                LoggerManager.d("importFail Event");
                            }

                        }

                    }

                    @Override
                    public void onError(Response<AccountInfo> response) {
                        LoggerManager.d("verifyAccountsErr");
                        super.onError(response);
                    }
                });
    }


    /**
     * 比较时间戳大小， 返回时间靠后的一个
     * "timestamp": "2018-06-08T08:08:08.500"
     * "created": "2018-08-21T03:34:17.500"
     * @param timestamp
     * @param created
     * @return
     */
    public String getLaterTimeStamp(String timestamp, String created){
        int flag = 0;

        SimpleDateFormat formatter = new SimpleDateFormat(DateUtil.Format.EOS_DATE_FORMAT_WITH_MILLI, Locale.getDefault());

        try {

            Date timestamp_date = formatter.parse(timestamp);
            Date created_date = formatter.parse(created);
            long date_diff = timestamp_date.getTime() - created_date.getTime();

            if (date_diff >= 0){
                flag = TIMESTAMP;
            }else {
                flag = CREATED;
            }
        }catch (ParseException e){
            e.printStackTrace();
        }

        switch (flag){
            case TIMESTAMP:
                return timestamp;
            case CREATED:
                return created;
            default:
                return "err";
        }
    }

    /**
     * 移除轮询
     */
    private void removePollingJob() {
        SmartScheduler smartScheduler = SmartScheduler.getInstance(GmaApplication.getAppContext());
        if (smartScheduler != null && smartScheduler.contains(ParamConstants.POLLING_JOB)) {
            smartScheduler.removeJob(ParamConstants.POLLING_JOB);
        }
    }

    /**
     * 开启一次比较时间戳验证轮询
     * 时间单位毫秒
     */
    public void startValidatePolling(int intervalTime, String created, String account_name, String public_key) {
        SmartScheduler smartScheduler = SmartScheduler.getInstance(GmaApplication.getAppContext());
        if (!smartScheduler.contains(ParamConstants.POLLING_JOB)) {
            SmartScheduler.JobScheduledCallback callback = new SmartScheduler.JobScheduledCallback() {
                @Override
                public void onJobScheduled(Context context, Job job) {
                    LoggerManager.d("validate polling executed");
                    getInfo(created, account_name, public_key);
                }

            };

            Job job = JobUtils.createPeriodicHandlerJob(ParamConstants.POLLING_JOB, callback, intervalTime);
            smartScheduler.addJob(job);
        }

    }




}
