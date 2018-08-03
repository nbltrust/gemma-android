package com.cybex.gma.client.job;

import android.content.Context;

import com.cybex.gma.client.GmaApplication;
import com.cybex.gma.client.config.CacheConstants;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.db.entity.WalletEntity;
import com.cybex.gma.client.event.PollEvent;
import com.cybex.gma.client.manager.DBManager;
import com.cybex.gma.client.manager.LoggerManager;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.model.request.GetTransactionReqParams;
import com.cybex.gma.client.ui.request.EOSConfigInfoRequest;
import com.cybex.gma.client.ui.request.GetTransactionRequest;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.GsonUtils;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import org.json.JSONException;
import org.json.JSONObject;

import io.hypertrack.smart_scheduler.Job;
import io.hypertrack.smart_scheduler.SmartScheduler;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 验证创建钱包lib
 *
 * Created by wanglin on 2018/8/2.
 */
public class LibValidateJob {


    private static final int STATUS_FAILED = -1; //状态失败
    private static final int STATUS_OK = 1;//成功状态
    private static final int STATUS_PENDING = 1001; //pending状态
    private static final int STATUS_ON_LINE = 1002; //online状态


    /**
     * 不等待，直接轮询，可设置间隔
     * 时间单位毫秒
     */
    public static void startPolling(int intervalTime) {
        SmartScheduler smartScheduler = SmartScheduler.getInstance(GmaApplication.getAppContext());
        if (smartScheduler.contains(ParamConstants.POLLING_JOB)) {
            smartScheduler.removeJob(ParamConstants.POLLING_JOB);
        }
        SmartScheduler.JobScheduledCallback callback = new SmartScheduler.JobScheduledCallback() {
            @Override
            public void onJobScheduled(Context context, Job job) {
                LoggerManager.d("polling executed");

                executeLibLogic();
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
    public static void getConfigInfo(StringCallback callback) {
        new EOSConfigInfoRequest(String.class)
                .getInfo(callback);
    }

    /**
     * @return 返回block_num
     */

    public static void getTransaction(StringCallback callback) {
        GetTransactionReqParams reqParams = new GetTransactionReqParams();
        WalletEntity curWallet = DBManager.getInstance().getWalletEntityDao().getCurrentWalletEntity();
        if (EmptyUtils.isNotEmpty(curWallet)) {
            LoggerManager.d("txId", curWallet.getTxId());
            reqParams.setid(curWallet.getTxId());
            String jsonParams = GsonUtils.objectToJson(reqParams);
            new GetTransactionRequest(String.class)
                    .setJsonParams(jsonParams)
                    .getInfo(callback);
        }

    }


   static Observable<String[]> observableTransaction = Observable.create(new ObservableOnSubscribe<String[]>() {
        @Override
        public void subscribe(ObservableEmitter<String[]> emitter) throws Exception {
            getTransaction(new StringCallback() {
                @Override
                public void onSuccess(Response<String> response) {
                    String[] res = new String[2];
                    if (response != null && EmptyUtils.isNotEmpty(response.body())) {
                        String infoJson = response.body();
                        LoggerManager.d("transaction info:" + infoJson);
                        try {
                            JSONObject obj = new JSONObject(infoJson);
                            if (obj != null) {
                                String block_num = obj.optString("block_num");
                                JSONObject trx = obj.optJSONObject("trx");
                                LoggerManager.d("block_num", block_num);
                                res[0] = block_num;
                                if (EmptyUtils.isEmpty(trx)) {
                                    res[1] = "false";
                                } else {
                                    res[1] = "true";
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    emitter.onNext(res);
                    emitter.onComplete();
                }
            });

        }
    }).subscribeOn(Schedulers.io());


   static Observable<int[]> observableConfigInfo = Observable.create(new ObservableOnSubscribe<int[]>() {
        @Override
        public void subscribe(ObservableEmitter<int[]> emitter) throws Exception {
            int[] result = new int[2];

            getConfigInfo(new StringCallback() {
                @Override
                public void onSuccess(Response<String> response) {
                    if (response != null && EmptyUtils.isNotEmpty(response.body())) {
                        String infoJson = response.body();
                        //LoggerManager.d("config info:" + infoJson);
                        try {
                            JSONObject obj = new JSONObject(infoJson);
                            if (obj != null) {
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

            emitter.onNext(result);
            emitter.onComplete();

        }
    }).subscribeOn(Schedulers.io());


    /**
     * 处理Lib 相关逻辑
     */
    private static void executeLibLogic() {
        //通过使用Zip（）对两个网络请求进行合并再发送
        Observable.zip(observableTransaction, observableConfigInfo,
                new BiFunction<String[], int[], Integer>() {
                    // 注：创建BiFunction对象传入的第3个参数 = 合并后数据的数据类型
                    @Override
                    public Integer apply(
                            String[] getTResult,
                            int[] getInfoResult) throws Exception {
                        if (EmptyUtils.isEmpty(getInfoResult)) { return STATUS_FAILED; }
                        if (EmptyUtils.isEmpty(getTResult)) { return STATUS_FAILED; }

                        int head = getInfoResult[1];
                        LoggerManager.d("head", head);
                        int lib = getInfoResult[0];
                        LoggerManager.d("lib", lib);
                        int curBlockNum = Integer.parseInt(getTResult[0]);
                        LoggerManager.d("block_num", curBlockNum);
                        String trx = getTResult[1];

                        if (trx.equals("false")) {
                            return STATUS_FAILED;
                        } else {
                            if (curBlockNum <= lib) {
                                return STATUS_OK;
                            } else if (curBlockNum > lib && curBlockNum <= head) {
                                //pending  显示alert，一直轮询
                                LoggerManager.d("status", "pending");
                                return STATUS_PENDING;
                            } else {
                                //online 显示alert，一直轮询
                                LoggerManager.d("status", "online");
                                return STATUS_ON_LINE;
                            }
                        }
                    }
                }).observeOn(AndroidSchedulers.mainThread()) // 在主线程接收 & 处理数据
                .subscribe(new Consumer<Integer>() {
                    // 成功返回数据时调用
                    @Override
                    public void accept(Integer combine_infro) throws Exception {
                        // 结合显示2个网络请求的数据结果
                        switch (combine_infro.intValue()) {
                            case STATUS_FAILED:
                                //如果失败，结束本次轮询，设置当前钱包isConfirm字段为-1，表示失败过，重新创建钱包
                                LoggerManager.d("status", "failed");
                                WalletEntity curWallet = DBManager.getInstance().getWalletEntityDao()
                                        .getCurrentWalletEntity();
                                curWallet.setIsConfirmLib(CacheConstants.CONFIRM_FAILED);
                                DBManager.getInstance().getWalletEntityDao().saveOrUpateMedia(curWallet);
                                UISkipMananger.launchCreateWallet(GmaApplication.getAppContext());
                                removeJob();
                                break;
                            case STATUS_OK:
                                LoggerManager.d("status", "ok");
                                //验证通过，结束轮询，结束alert TODO post eventbus
                                WalletEntity successWallet = DBManager.getInstance().getWalletEntityDao()
                                        .getCurrentWalletEntity();
                                successWallet.setIsConfirmLib(CacheConstants.IS_CONFIRMED);
                                DBManager.getInstance().getWalletEntityDao().saveOrUpateMedia(successWallet);
                                EventBusProvider.postSticky(new PollEvent(true));
                                removeJob();
                                break;
                            case STATUS_PENDING:
                            case STATUS_ON_LINE:
                            default:
                                break;
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        //TODO  网络请求错误时相关处理
                    }
                });


    }


    private static void removeJob() {
        SmartScheduler smartScheduler = SmartScheduler.getInstance(GmaApplication.getAppContext());
        if (smartScheduler != null && smartScheduler.contains(ParamConstants.POLLING_JOB)) {
            smartScheduler.removeJob(ParamConstants.POLLING_JOB);
        }
    }


}