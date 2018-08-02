package com.cybex.gma.client.ui.presenter;

import android.content.Context;

import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.db.entity.WalletEntity;
import com.cybex.gma.client.job.JobUtils;
import com.cybex.gma.client.manager.DBManager;
import com.cybex.gma.client.manager.LoggerManager;
import com.cybex.gma.client.ui.fragment.WalletFragment;
import com.cybex.gma.client.ui.model.request.GetTransactionReqParams;
import com.cybex.gma.client.ui.model.response.EOSConfigInfo;
import com.cybex.gma.client.ui.request.EOSConfigInfoRequest;
import com.cybex.gma.client.ui.request.GetTransactionRequest;
import com.hxlx.core.lib.mvp.lite.XPresenter;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.GsonUtils;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import org.json.JSONException;
import org.json.JSONObject;

import io.hypertrack.smart_scheduler.Job;
import io.hypertrack.smart_scheduler.SmartScheduler;

/**
 * 钱包Presenter
 *
 * Created by wanglin on 2018/7/9.
 */
public class WalletPresenter extends XPresenter<WalletFragment>{

    /**
     * 设置轮询
     * 先设置一个alarmJob，可设置延时多久之后开始做轮询
     * 时间单位毫秒
     */
    public void startDelayPolling(int waitngTime, int intervalTime){
        SmartScheduler smartScheduler = SmartScheduler.getInstance(getV().getActivity().getApplicationContext());

        SmartScheduler.JobScheduledCallback pollingCallback = new SmartScheduler.JobScheduledCallback() {
            @Override
            public void onJobScheduled(Context context, Job job) {
                LoggerManager.d("polling executed");
                /*
                //todo 执行 get_Info request 查询，需要手动设置轮询关闭逻辑
                int[] getInfoResult = getConfigInfo();
                String[] getTResult = getTransaction();
                int head = getInfoResult[1];
                LoggerManager.d("head", head);
                int lib = getInfoResult[0];
                LoggerManager.d("lib", lib);
                int curBlockNum = Integer.parseInt(getTResult[0]);
                LoggerManager.d("block_num", curBlockNum);
                String trx = getTResult[1];

                if (trx == null){
                    //fail
                    smartScheduler.removeJob(ParamConstants.POLLING_JOB);
                    LoggerManager.d("status","failed");
                    //UISkipMananger.launchCreateWallet(getV().getActivity());
                }else if (curBlockNum < lib){
                    //done
                    smartScheduler.removeJob(ParamConstants.POLLING_JOB);
                    LoggerManager.d("status", "Done");
                }else if(curBlockNum >= lib && curBlockNum <= head){
                    //pending
                    LoggerManager.d("status", "pending");
                }else{
                    //online
                    LoggerManager.d("status", "online");
                }
                */
            }

        };

        SmartScheduler.JobScheduledCallback alarmCallback = new SmartScheduler.JobScheduledCallback() {
            @Override
            public void onJobScheduled(Context context, Job job) {
                Job pollingJob = JobUtils.createPeriodicHandlerJob(ParamConstants.POLLING_JOB, pollingCallback, waitngTime);
                boolean isPolling_existed = smartScheduler.addJob(pollingJob);
                if (isPolling_existed){
                    LoggerManager.d("polling added");
                }
                if (smartScheduler.contains(ParamConstants.POLLING_JOB)){
                    LoggerManager.d("true");
                }

            }
        };

        Job alarmJob = JobUtils.createAlarmJob(ParamConstants.ALARM_JOB, alarmCallback, intervalTime);
        boolean alarmJob_exist = smartScheduler.addJob(alarmJob);
        if (alarmJob_exist){
            LoggerManager.d("alarm added");
        }
    }

    /**
     * 不等待，直接轮询，可设置间隔
     * 时间单位毫秒
     */
    public void startPolling(int intervalTime){
        SmartScheduler smartScheduler = SmartScheduler.getInstance(getV().getActivity().getApplicationContext());
        if (smartScheduler.contains(ParamConstants.ALARM_JOB) || smartScheduler.contains(ParamConstants.POLLING_JOB)){
            smartScheduler.removeJob(ParamConstants.POLLING_JOB);
            smartScheduler.removeJob(ParamConstants.ALARM_JOB);
        }
        SmartScheduler.JobScheduledCallback callback = new SmartScheduler.JobScheduledCallback() {
            @Override
            public void onJobScheduled(Context context, Job job) {
                LoggerManager.d("job executed");
                /*
                int[] getInfoResult = getConfigInfo();
                String[] getTResult = getTransaction();
                int head = getInfoResult[1];
                LoggerManager.d("head", head);
                int lib = getInfoResult[0];
                LoggerManager.d("lib", lib);
                int curBlockNum = Integer.parseInt(getTResult[0]);
                LoggerManager.d("block_num", curBlockNum);
                String trxStatus = getTResult[1];

                if (trxStatus.equals("true")){
                    //fail
                    smartScheduler.removeJob(ParamConstants.POLLING_JOB);
                    LoggerManager.d("status","failed");
                    //UISkipMananger.launchCreateWallet(getV().getActivity());
                }else if (curBlockNum < lib){
                    //done
                    smartScheduler.removeJob(ParamConstants.POLLING_JOB);
                    LoggerManager.d("status", "Done");
                }else if(curBlockNum >= lib && curBlockNum <= head){
                    //pending
                    LoggerManager.d("status", "pending");
                }else{
                    //online
                    LoggerManager.d("status", "online");
                }
                  */
            }

        };

        Job job = JobUtils.createPeriodicHandlerJob(ParamConstants.POLLING_JOB, callback, intervalTime);
        smartScheduler.addJob(job);
    }

    /**
     * 调取RPC get_info接口查询当前链的配置信息
     * 获取head_block_num和lib返回
     * result[0]返回lib
     * result[1]返回head
     */
    public int[] getConfigInfo(){
        int[] result = new int[2];
        new EOSConfigInfoRequest(EOSConfigInfo.class)
                .getInfo(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null && EmptyUtils.isNotEmpty(response.body())) {
                            String infoJson = response.body();
                            //LoggerManager.d("config info:" + infoJson);
                            try {
                                JSONObject obj = new JSONObject(infoJson);
                                if(obj!=null){
                                    String last_irreversible_block_num = obj.optString("last_irreversible_block_num");
                                    String head_block_num = obj.optString("head_block_num");
                                    result[0] = Integer.parseInt(last_irreversible_block_num);
                                    result[1] = Integer.parseInt(head_block_num);
                                    LoggerManager.d("lib", last_irreversible_block_num);
                                    LoggerManager.d("head_num", head_block_num);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
        return result;
    }

    /**
     *
     * @return
     * 返回block_num
     *
     */

    public String[] getTransaction(){
        String[] res = new String[2];
        GetTransactionReqParams reqParams = new GetTransactionReqParams();
        WalletEntity curWallet = DBManager.getInstance().getWalletEntityDao().getCurrentWalletEntity();
        if (EmptyUtils.isNotEmpty(curWallet)){
            LoggerManager.d("txId", curWallet.getTxId());
            reqParams.setid(curWallet.getTxId());
        }
        String jsonParams = GsonUtils.objectToJson(reqParams);
        new GetTransactionRequest(String.class)
                .setJsonParams(jsonParams)
                .getInfo(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null && EmptyUtils.isNotEmpty(response.body())) {
                            String infoJson = response.body();
                            LoggerManager.d("transaction info:" + infoJson);
                            try {
                                JSONObject obj = new JSONObject(infoJson);
                                if(obj!=null){
                                    String block_num = obj.optString("block_num");
                                    JSONObject trx = obj.optJSONObject("trx");
                                    LoggerManager.d("block_num", block_num);
                                    res[0] = block_num;
                                    if (trx == null){
                                        res[1] = "false";
                                    }else{
                                        res[1] = "true";
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
        return res;
    }

}
