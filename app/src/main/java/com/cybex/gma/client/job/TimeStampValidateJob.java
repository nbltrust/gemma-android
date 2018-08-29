package com.cybex.gma.client.job;

import android.content.Context;

import com.cybex.gma.client.GmaApplication;
import com.cybex.gma.client.api.callback.JsonCallback;
import com.cybex.gma.client.config.HttpConst;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.manager.LoggerManager;
import com.cybex.gma.client.ui.JNIUtil;
import com.cybex.gma.client.ui.model.request.GetAccountInfoReqParams;
import com.cybex.gma.client.ui.model.request.GetBlockReqParams;
import com.cybex.gma.client.ui.model.request.GetkeyAccountReqParams;
import com.cybex.gma.client.ui.model.response.AccountInfo;
import com.cybex.gma.client.ui.model.response.GetKeyAccountsResult;
import com.cybex.gma.client.ui.request.EOSConfigInfoRequest;
import com.cybex.gma.client.ui.request.GetAccountinfoRequest;
import com.cybex.gma.client.ui.request.GetBlockRequest;
import com.cybex.gma.client.ui.request.GetKeyAccountsRequest;
import com.hxlx.core.lib.utils.GsonUtils;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.hypertrack.smart_scheduler.Job;
import io.hypertrack.smart_scheduler.SmartScheduler;

/**
 * 通过比较时间戳方法验证账户是否存在的方法
 * 邀请好友创建钱包，导入钱包时使用
 *
 */
public class TimeStampValidateJob {

    private static final int OPERATION_CREATE = 0;
    private static final int OPERATION_IMPORT = 1;

    private static final String ACTIVE_KEY = "active";
    private static final String OWNER_KEY = "owner";

    public static void setStopAlarmForPolling(int waitTime){
        SmartScheduler smartScheduler = SmartScheduler.getInstance(GmaApplication.getAppContext());
        if (smartScheduler.contains(ParamConstants.ALARM_JOB)) {
            smartScheduler.removeJob(ParamConstants.ALARM_JOB);
        }
        SmartScheduler.JobScheduledCallback callback = new SmartScheduler.JobScheduledCallback() {
            @Override
            public void onJobScheduled(Context context, Job job) {
                LoggerManager.d("alarm executed");
                removePollingJob();
            }

        };

        Job job = JobUtils.createAlarmJob(ParamConstants.ALARM_JOB, callback, waitTime);
        smartScheduler.addJob(job);
    }

    /**
     * 开启一次获取账户信息轮询
     * 时间单位毫秒
     */
    public static void startgetAccountPolling(int intervalTime, String account_name, String public_key) {
        SmartScheduler smartScheduler = SmartScheduler.getInstance(GmaApplication.getAppContext());
        if (smartScheduler.contains(ParamConstants.POLLING_JOB)) {
            smartScheduler.removeJob(ParamConstants.POLLING_JOB);
        }
        SmartScheduler.JobScheduledCallback callback = new SmartScheduler.JobScheduledCallback() {
            @Override
            public void onJobScheduled(Context context, Job job) {
                LoggerManager.d("get account polling executed");
                    getAccount(account_name, public_key);
            }

        };

        Job job = JobUtils.createPeriodicHandlerJob(ParamConstants.POLLING_JOB, callback, intervalTime);
        smartScheduler.addJob(job);
    }

    /**
     * 开启一次比较时间戳验证轮询
     * 时间单位毫秒
     */
    public static void startValidatePolling(int intervalTime, String created) {
        SmartScheduler smartScheduler = SmartScheduler.getInstance(GmaApplication.getAppContext());
        if (smartScheduler.contains(ParamConstants.POLLING_JOB)) {
            smartScheduler.removeJob(ParamConstants.POLLING_JOB);
        }
        SmartScheduler.JobScheduledCallback callback = new SmartScheduler.JobScheduledCallback() {
            @Override
            public void onJobScheduled(Context context, Job job) {
                LoggerManager.d("validate polling executed");
                    getInfo(created);
            }

        };

        Job job = JobUtils.createPeriodicHandlerJob(ParamConstants.POLLING_JOB, callback, intervalTime);
        smartScheduler.addJob(job);
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
                        if (response != null && response.body() != null){
                            GetKeyAccountsResult result = response.body();
                            List<String> account_names = result.account_names;
                            final String curEOSName = account_names.get(0);
                            executeValidateLogic(curEOSName, public_key);
                        }
                    }

                    @Override
                    public void onError(Response<GetKeyAccountsResult> response) {
                        super.onError(response);
                    }
                });
    }

    /**
     * 根据账户名查询账户信息
     * 需要在callback中解析并获取该账户的创建时间
     * @param account_name
     */
    public static void getAccount(String account_name, String public_key){
        GetAccountInfoReqParams params = new GetAccountInfoReqParams();
        params.setAccount_name(account_name);

        String jsonParams = GsonUtils.objectToJson(params);

        new GetAccountinfoRequest(AccountInfo.class)
                .setJsonParams(jsonParams)
                .getAccountInfo(new JsonCallback<AccountInfo>() {
                    @Override
                    public void onSuccess(Response<AccountInfo> response) {
                        if (response.body() != null && response.code() != HttpConst.SERVER_INTERNAL_ERR){
                            //已查询到此账户
                            AccountInfo info = response.body();
                            LoggerManager.d("account_info", info);
                            //获取此账户的创建时间戳
                            final String created = info.getCreated();
                            //检查公钥是否在结构体里
                            List<AccountInfo.PermissionsBean> permissions = info.getPermissions();
                            for (AccountInfo.PermissionsBean permission : permissions){
                                //检查active key中是否是此公钥
                                if (isBeanContainsPublicKey(permission, public_key)){
                                    //如果有
                                    startValidatePolling(15000, created);
                                }else {
                                    //如果没有
                                }
                            }

                        }else {
                            //未查询到此账户
                            startgetAccountPolling(20000, account_name, public_key);
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
    public static void getInfo(String created) {
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

                                    getBlock(lib_num, created);
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
    public static void getBlock(String block_num, String created){

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
                                        //成功
                                        //todo 关闭所有网络请求及轮询
                                        removePollingJob();
                                    }else {
                                        //todo 失败，轮询
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
            if (pubKey.equals(public_key))return true;
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
        String[] timestamp_arr = timestamp.split("-");
        String[] created_arr = created.split("-");

        String year_timestamp = timestamp_arr[0];
        String year_created = created_arr[0];

        String month_timestamp = timestamp_arr[1];
        String month_created = created_arr[1];

        String date_timestamp = timestamp_arr[2];
        String date_created = created_arr[2];

        //先比较年份
        if (Integer.parseInt(year_created) < Integer.parseInt(year_timestamp)){
            return timestamp;
        }else if (Integer.parseInt(year_created) > Integer.parseInt(year_timestamp)){
            return created;
        }else {
            //年份相等，比较月份
            if (Integer.parseInt(month_created) < Integer.parseInt(month_timestamp)){
                return timestamp;
            }else if(Integer.parseInt(month_created) > Integer.parseInt(month_timestamp)){
                return created;
            }else {
                //月份相等，比较具体时间
                String res =  compareDate(date_timestamp, date_created);
                if (res.equals(date_timestamp)){
                    return timestamp;
                }else {
                    return created;
                }
            }
        }

    }

    /**
     * 比较具体时间，返回时间靠后的一个
     * 08T08:08:08.500
     * 21T03:34:17.500
     * @param date_timestamp
     * @param date_created
     * @return
     */

    public static String compareDate(String date_timestamp, String date_created){
        String[] date_timestamp_arr = date_timestamp.split(":");
        String[] date_created_arr = date_created.split(":");

        String day_timestamp = date_timestamp_arr[0].substring(0,2);
        String day_created = date_created_arr[0].substring(0,2);

        String hour_timestamp = date_timestamp_arr[0].substring(3);
        String hour_created = date_created_arr[0].substring(3);

        String min_timestamp = date_timestamp_arr[1];
        String min_created = date_created_arr[1];

        String sec_timestamp = date_timestamp_arr[2].substring(0,2);
        String sec_created = date_created_arr[2].substring(0,2);

        String millisec_timestamp = date_timestamp_arr[2].substring(3);
        String millisec_created = date_created_arr[2].substring(3);

        if (Integer.parseInt(day_created) < Integer.parseInt(day_timestamp)){
            //先比较日期
            return date_timestamp;
        }else if (Integer.parseInt(day_created) > Integer.parseInt(day_timestamp)){
            return date_created;
        }else {
            //日期一样，比较小时
            if (Integer.parseInt(hour_created) < Integer.parseInt(hour_timestamp)){
                return date_timestamp;
            }else if(Integer.parseInt(hour_created) > Integer.parseInt(hour_timestamp)){
                return date_created;
            }else {
                //小时一样，比较分钟
                if (Integer.parseInt(min_created) < Integer.parseInt(min_timestamp)){
                    return date_timestamp;
                }else if (Integer.parseInt(min_created) > Integer.parseInt(min_timestamp)){
                    return date_created;
                }else {
                    //分钟一样，比较秒数
                    if (Integer.parseInt(sec_created) < Integer.parseInt(sec_timestamp)){
                        return date_timestamp;
                    }else if (Integer.parseInt(sec_created) > Integer.parseInt(sec_timestamp)){
                        return date_created;
                    }else {
                        //秒数一样，比较毫秒
                        if (Integer.parseInt(millisec_created) < Integer.parseInt(millisec_timestamp)){
                            return date_timestamp;
                        }else if (Integer.parseInt(sec_created) > Integer.parseInt(sec_timestamp)){
                            return date_created;
                        }else {
                            return date_created;
                        }
                    }
                }
            }
        }

    }


    private static void removePollingJob() {
        SmartScheduler smartScheduler = SmartScheduler.getInstance(GmaApplication.getAppContext());
        if (smartScheduler != null && smartScheduler.contains(ParamConstants.POLLING_JOB)) {
            smartScheduler.removeJob(ParamConstants.POLLING_JOB);
        }
    }

    public static void executeValidateLogic(String account_name, String public_key){
        getAccount(account_name, public_key);
    }

    public static void executeImportLogic(String private_key){
        final String public_key = JNIUtil.get_public_key(private_key);
        getKeyAccounts(public_key);
    }

    public static void executedCreateLogic(String account_name, String public_key){
        getAccount(account_name, public_key);
    }

}
