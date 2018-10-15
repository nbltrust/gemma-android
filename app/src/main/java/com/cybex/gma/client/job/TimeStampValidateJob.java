package com.cybex.gma.client.job;

import android.content.Context;

import com.cybex.componentservice.api.callback.JsonCallback;
import com.cybex.gma.client.GmaApplication;
import com.cybex.gma.client.config.CacheConstants;
import com.cybex.gma.client.config.HttpConst;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.db.entity.WalletEntity;
import com.cybex.gma.client.event.ValidateResultEvent;
import com.cybex.gma.client.manager.DBManager;
import com.cybex.gma.client.manager.LoggerManager;
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
import com.hxlx.core.lib.utils.GsonUtils;
import com.hxlx.core.lib.utils.common.utils.DateUtil;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.hypertrack.smart_scheduler.Job;
import io.hypertrack.smart_scheduler.SmartScheduler;

/**
 * 通过比较时间戳方法验证账户是否存在的方法
 * 邀请好友创建钱包，导入钱包时使用
 *
 */
public class TimeStampValidateJob {

    private static final int TIMESTAMP = 1;
    private static final int CREATED = 2;

    private static final int OPERATION_CREATE = 1;
    private static final int OPERATION_IMPORT = 2;

    private static final int FAIL_OVERTIME = 2;
    private static final int FAIL_EMPTY_ACCOUNT = 3;
    private static final int FAIL_USERNAME_USED = 4;

    private static final String ACTIVE_KEY = "active";
    private static final String OWNER_KEY = "owner";

    /**
     * 开启一次比较时间戳验证轮询
     * 时间单位毫秒
     */
    public static void startValidatePolling(int intervalTime, String created, String account_name, String public_key) {
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

    /**
     * 根据公钥查询对应的eos账户
     * @param public_key
     */
    public static void getKeyAccounts(String public_key){
        GetkeyAccountReqParams params = new GetkeyAccountReqParams();
        params.setPublic_key(public_key);
        String jsonParams = GsonUtils.objectToJson(params);

        new GetKeyAccountsRequest(GetKeyAccountsResult.class)
                .setJsonParams(jsonParams)
                .getKeyAccountsRequest(new JsonCallback<GetKeyAccountsResult>() {
                    @Override
                    public void onSuccess(Response<GetKeyAccountsResult> response) {
                        if (response != null && response.body() != null && response.code() != HttpConst.SERVER_INTERNAL_ERR){
                            GetKeyAccountsResult result = response.body();
                            List<String> account_names = result.account_names;
                            final String curEOSName = account_names.get(0);
                            executeValidateLogic(curEOSName, public_key);
                        }else if (response != null && response.body() != null && response.code() == HttpConst
                                .SERVER_INTERNAL_ERR ){
                            //未找到此账号
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

    /**
     ** 根据账户名查询账户信息
     *
     * @param account_name
     * @param public_key
     * @param isSuccess 时间戳比较是否已经成功
     */
    public static void getAccountCreate(String account_name, String public_key, boolean isSuccess){
        GetAccountInfoReqParams params = new GetAccountInfoReqParams();
        params.setAccount_name(account_name);

        String jsonParams = GsonUtils.objectToJson(params);

        new GetAccountinfoRequest(AccountInfo.class)
                .setJsonParams(jsonParams)
                .getAccountInfo(new JsonCallback<AccountInfo>() {
                    @Override
                    public void onSuccess(Response<AccountInfo> response) {
                        if (response != null && response.body() != null && response.code() != HttpConst.SERVER_INTERNAL_ERR){
                            AccountInfo info = response.body();
                            final String created = info.getCreated();

                            if (!isSuccess){
                                getInfo(created, account_name, public_key);
                            }else {
                                //检查公钥是否在结构体里
                                List<AccountInfo.PermissionsBean> permissions = info.getPermissions();
                                for (AccountInfo.PermissionsBean permission : permissions) {
                                    //检查active key中是否是此公钥
                                    if (isBeanContainsPublicKey(permission, public_key)) {
                                        //如果公钥在账户中,创建成功
                                        LoggerManager.d("create success");

                                        //更改当前钱包状态
                                        WalletEntity successWallet = DBManager.getInstance().getWalletEntityDao()
                                                .getCurrentWalletEntity();
                                        successWallet.setIsConfirmLib(CacheConstants.IS_CONFIRMED);
                                        DBManager.getInstance().getWalletEntityDao().saveOrUpateEntity(successWallet);

                                        //Post成功事件
                                        ValidateResultEvent event_success = new ValidateResultEvent();
                                        event_success.setSuccess(true);
                                        EventBusProvider.postSticky(event_success);

                                    } else {
                                        //公钥不在账户中
                                        //失败，弹框提示账户名已被使用，查询数据库中是否有其他钱包
                                        ValidateResultEvent event = new ValidateResultEvent();
                                        event.setFail_type(FAIL_USERNAME_USED);
                                        EventBusProvider.postSticky(event);
                                    }
                                }
                            }
                        }

                    }

                    @Override
                    public void onError(Response<AccountInfo> response) {
                        super.onError(response);
                    }
                });
    }


    /**
     * 调取RPC get_info接口查询当前链的配置信息
     * 获取lib
     */
    public static void getInfo(String created, String account_name, String public_key) {
        new EOSConfigInfoRequest(String.class)
                .getInfo(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
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
                        super.onError(response);
                    }
                });
    }

    /**
     * 根据block_num查询当前块的信息
     * 需要在callback获取timestamp
     * @param block_num
     */
    public static void getBlock(String block_num, String created, String account_name, String public_key){

        GetBlockReqParams params = new GetBlockReqParams();
        params.setBlock_num_or_id(block_num);

        String jsonParams = GsonUtils.objectToJson(params);

        new GetBlockRequest(String.class)
                .setJsonParams(jsonParams)
                .getBlock(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
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
                                        getAccountCreate(account_name, public_key, true);
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
    public static boolean isBeanContainsPublicKey(AccountInfo.PermissionsBean permissionsBean, String pubKey){
        if (permissionsBean.getPerm_name().equals(ACTIVE_KEY) || permissionsBean.getPerm_name().equals(OWNER_KEY)){
            AccountInfo.PermissionsBean.RequiredAuthBean requiredAuthBean = permissionsBean.getRequired_auth();
            List<AccountInfo.PermissionsBean.RequiredAuthBean.KeysBean> keysBeanList = requiredAuthBean.getKeys();
            String public_key = keysBeanList.get(0).getKey();
            return pubKey.equals(public_key);
        }
        return false;
    }

    /**
     * 比较时间戳大小， 返回时间靠后的一个
     * "timestamp": "2018-06-08T08:08:08.500"
     * "created": "2018-08-21T03:34:17.500"
     * @param timestamp
     * @param created
     * @return
     */
    public static String getLaterTimeStamp(String timestamp, String created){
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
    private static void removePollingJob() {
        SmartScheduler smartScheduler = SmartScheduler.getInstance(GmaApplication.getAppContext());
        if (smartScheduler != null && smartScheduler.contains(ParamConstants.POLLING_JOB)) {
            smartScheduler.removeJob(ParamConstants.POLLING_JOB);
        }
    }

    public static void executeValidateLogic(String account_name, String public_key){
        getAccountCreate(account_name, public_key, false);
    }

    public static void executedCreateLogic(String account_name, String public_key){
        getAccountCreate(account_name, public_key, false);
    }

    public static void executeImportLogic(String public_key){
        getKeyAccounts(public_key);
    }




}
