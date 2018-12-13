package com.cybex.gma.client.job;

import android.content.Context;

import com.cybex.gma.client.GmaApplication;
import com.cybex.componentservice.config.CacheConstants;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.componentservice.db.entity.WalletEntity;
import com.cybex.gma.client.event.PollEvent;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.gma.client.ui.model.request.GetTransactionReqParams;
import com.cybex.gma.client.ui.presenter.CreateWalletPresenter;
import com.cybex.gma.client.ui.request.EOSConfigInfoRequest;
import com.cybex.gma.client.ui.request.GetEosTransactionRequest;
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

//
//    private static final int STATUS_FAILED = -1; //状态失败
//    private static final int STATUS_OK = 1;//成功状态
//    private static final int STATUS_PENDING = 1001; //pending状态
//    private static final int STATUS_ON_LINE = 1002; //online状态
//    private static final int STATUS_INVALID_PARAMETER = 10013;//重传失败状态
//
//
//    /**
//     * 不等待，直接轮询，可设置间隔
//     * 时间单位毫秒
//     */
//    public static void startPolling(int intervalTime) {
//        SmartScheduler smartScheduler = SmartScheduler.getInstance(GmaApplication.getAppContext());
//        if (smartScheduler.contains(ParamConstants.POLLING_JOB)) {
//            smartScheduler.removeJob(ParamConstants.POLLING_JOB);
//        }
//        SmartScheduler.JobScheduledCallback callback = new SmartScheduler.JobScheduledCallback() {
//            @Override
//            public void onJobScheduled(Context context, Job job) {
//                LoggerManager.d("polling executed");
//
//                executeLibLogic();
//            }
//
//        };
//
//        Job job = JobUtils.createPeriodicHandlerJob(ParamConstants.POLLING_JOB, callback, intervalTime);
//        smartScheduler.addJob(job);
//    }
//
//    /**
//     * 调取RPC get_info接口查询当前链的配置信息
//     * 获取head_block_num和lib返回
//     * result[0]返回lib
//     * result[1]返回head
//     */
//    public static void getConfigInfo(StringCallback callback) {
//        new EOSConfigInfoRequest(String.class)
//                .getInfo(callback);
//    }
//
//    /**
//     * @return 返回block_num
//     */
//
//    public static void getTransaction(StringCallback callback) {
//        GetTransactionReqParams reqParams = new GetTransactionReqParams();
//        WalletEntity curWallet = DBManager.getInstance().getWalletEntityDao().getCurrentWalletEntity();
//        if (EmptyUtils.isNotEmpty(curWallet)) {
//            LoggerManager.d("txId", curWallet.getTxId());
//            reqParams.setid(curWallet.getTxId());
//            String jsonParams = GsonUtils.objectToJson(reqParams);
//            new GetEosTransactionRequest(String.class, "")
//                    .setJsonParams(jsonParams)
//                    .getTransaction(callback);
//        }
//
//    }
//
//
//   static Observable<String[]> observableTransaction = Observable.create(new ObservableOnSubscribe<String[]>() {
//        @Override
//        public void subscribe(ObservableEmitter<String[]> emitter) {
//            getTransaction(new StringCallback() {
//                @Override
//                public void onSuccess(Response<String> response) {
//                    String[] res = new String[2];
//                    if (response != null && EmptyUtils.isNotEmpty(response.body())) {
//                        String infoJson = response.body();
//                        LoggerManager.d("transaction info:" + infoJson);
//                        try {
//                            JSONObject obj = new JSONObject(infoJson);
//                            if (obj != null) {
//                                String block_num = obj.optString("block_num");
//                                JSONObject trx = obj.optJSONObject("trx");
//                                String code = obj.optString("code");
//                                LoggerManager.d("block_num", block_num);
//                                res[0] = block_num;
//                                //todo 是否需要添加判断处理code不为400的其他错误情况？
//                                // （需要确认链上是否只会返回400？）
//                                if (code.equals("400")) {
//                                    res[1] = "false";
//                                } else {
//                                    res[1] = "true";
//                                }
//
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    emitter.onNext(res);
//                    emitter.onComplete();
//                }
//
//                @Override
//                public void onError(Response<String> response) {
//                    if (response != null && EmptyUtils.isNotEmpty(response.body())) {
//                        LoggerManager.d(response.toString());
//                    }
//                }
//            });
//
//        }
//    }).subscribeOn(Schedulers.io());
//
//
//   static Observable<int[]> observableConfigInfo = Observable.create(new ObservableOnSubscribe<int[]>() {
//        @Override
//        public void subscribe(ObservableEmitter<int[]> emitter) {
//            int[] result = new int[2];
//
//            getConfigInfo(new StringCallback() {
//                @Override
//                public void onSuccess(Response<String> response) {
//                    if (response != null && EmptyUtils.isNotEmpty(response.body())) {
//                        String infoJson = response.body();
//                        //LoggerManager.d("config info:" + infoJson);
//                        try {
//                            JSONObject obj = new JSONObject(infoJson);
//                            if (obj != null) {
//                                String last_irreversible_block_num = obj.optString("last_irreversible_block_num");
//                                String head_block_num = obj.optString("head_block_num");
//                                result[0] = Integer.parseInt(last_irreversible_block_num);
//                                result[1] = Integer.parseInt(head_block_num);
//                                LoggerManager.d("lib", last_irreversible_block_num);
//                                LoggerManager.d("head_num", head_block_num);
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            });
//
//            emitter.onNext(result);
//            emitter.onComplete();
//
//        }
//    }).subscribeOn(Schedulers.io());
//
//
//    /**
//     * 处理Lib 相关逻辑
//     */
//    private static void executeLibLogic() {
//        //通过使用Zip（）对两个网络请求进行合并再发送
//        Observable.zip(observableTransaction, observableConfigInfo,
//                new BiFunction<String[], int[], Integer>() {
//                    // 注：创建BiFunction对象传入的第3个参数 = 合并后数据的数据类型
//                    @Override
//                    public Integer apply(
//                            String[] getTResult,
//                            int[] getInfoResult) {
//                        if (EmptyUtils.isEmpty(getInfoResult)) { return STATUS_FAILED; }
//                        if (EmptyUtils.isEmpty(getTResult)) { return STATUS_FAILED; }
//
//                        int head = getInfoResult[1];
//                        LoggerManager.d("head", head);
//                        int lib = getInfoResult[0];
//                        LoggerManager.d("lib", lib);
//                        int curBlockNum = Integer.parseInt(getTResult[0]);
//                        LoggerManager.d("block_num", curBlockNum);
//                        String res = getTResult[1];
//
//                        if (res.equals("false")) {
//                            return STATUS_FAILED;
//                        } else {
//
//                            if (curBlockNum <= lib) {
//                                return STATUS_OK;
//                            } else if (curBlockNum > lib && curBlockNum <= head) {
//                                //pending  显示alert，一直轮询
//                                LoggerManager.d("status", "pending");
//                                return STATUS_PENDING;
//                            } else {
//                                //online 显示alert，一直轮询
//                                LoggerManager.d("status", "online");
//                                return STATUS_ON_LINE;
//                            }
//                        }
//                    }
//                }).observeOn(AndroidSchedulers.mainThread()) // 在主线程接收 & 处理数据
//                .subscribe(new Consumer<Integer>() {
//                    // 成功返回数据时调用
//                    @Override
//                    public void accept(Integer combine_infro) {
//                        // 结合显示2个网络请求的数据结果
//                        switch (combine_infro.intValue()) {
//                            case STATUS_FAILED:
//                                //如果失败，结束本次轮询，设置当前钱包isConfirm字段为-1，表示失败过，重新调用创建钱包请求
//                                LoggerManager.d("status", "failed");
//                                WalletEntity curWallet = DBManager.getInstance().getWalletEntityDao()
//                                        .getCurrentWalletEntity();
//                                if (EmptyUtils.isNotEmpty(curWallet)){
//                                    curWallet.setIsConfirmLib(CacheConstants.CONFIRM_FAILED);
//                                    DBManager.getInstance().getWalletEntityDao().saveOrUpateEntity(curWallet);
//                                    //todo 调取重新创建请求
//                                    CreateWalletPresenter presenter = new CreateWalletPresenter();
//                                    presenter.reCreateAccount(curWallet);
//                                    removeJob();
//                                }else {
//                                    removeJob();
//                                }
//                                break;
//                            case STATUS_OK:
//                                LoggerManager.d("status", "ok");
//                                //验证通过，结束轮询，结束alert
//                                WalletEntity successWallet = DBManager.getInstance().getWalletEntityDao()
//                                        .getCurrentWalletEntity();
//                                successWallet.setIsConfirmLib(CacheConstants.IS_CONFIRMED);
//                                DBManager.getInstance().getWalletEntityDao().saveOrUpateEntity(successWallet);
//                                EventBusProvider.postSticky(new PollEvent(true));
//                                removeJob();
//                                break;
//                            case STATUS_PENDING:
//                            case STATUS_ON_LINE:
//                            default:
//                                break;
//                        }
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) {
//                        //TODO  网络请求错误时相关处理
//                    }
//                });
//
//
//    }
//
//
//    private static void removeJob() {
//        SmartScheduler smartScheduler = SmartScheduler.getInstance(GmaApplication.getAppContext());
//        if (smartScheduler != null && smartScheduler.contains(ParamConstants.POLLING_JOB)) {
//            smartScheduler.removeJob(ParamConstants.POLLING_JOB);
//        }
//    }

}
