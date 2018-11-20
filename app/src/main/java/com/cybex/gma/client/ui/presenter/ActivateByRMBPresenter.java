package com.cybex.gma.client.ui.presenter;

import android.support.annotation.NonNull;

import com.cybex.componentservice.api.callback.CustomRequestCallback;
import com.cybex.componentservice.api.callback.JsonCallback;
import com.cybex.componentservice.api.data.response.CustomData;
import com.cybex.componentservice.db.entity.EosWalletEntity;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.utils.AlertUtil;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.HttpConst;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.event.OrderIdEvent;
import com.cybex.gma.client.job.TimeStampValidateJob;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.activity.ActivateAccountMethodActivity;
import com.cybex.gma.client.ui.activity.CreateEosAccountActivity;
import com.cybex.gma.client.ui.fragment.ActivateByRMBFragment;
import com.cybex.gma.client.ui.model.request.UserRegisterReqParams;
import com.cybex.gma.client.ui.model.request.WXPayInitialOrderReqParams;
import com.cybex.gma.client.ui.model.response.UserRegisterResult;
import com.cybex.gma.client.ui.model.response.WXPayBillResult;
import com.cybex.gma.client.ui.model.response.WXPayPlaceOrderResult;
import com.cybex.gma.client.ui.model.response.WXPayQueryOrderInfoResult;
import com.cybex.gma.client.ui.request.UserRegisterRequest;
import com.cybex.gma.client.ui.request.WXPayBillRequst;
import com.cybex.gma.client.ui.request.WXPayInitialOrderRequest;
import com.cybex.gma.client.ui.request.WXPayPlaceOrderRequest;
import com.cybex.gma.client.ui.request.WXPayQueryOrderInfoRequest;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.mvp.lite.XPresenter;
import com.hxlx.core.lib.utils.GsonUtils;
import com.hxlx.core.lib.utils.NetworkUtils;
import com.hxlx.core.lib.utils.android.SysUtils;
import com.hxlx.core.lib.utils.common.utils.AppManager;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

public class ActivateByRMBPresenter extends XPresenter<ActivateByRMBFragment> {

    /**
     * 先调中心化服务器初始化订单API
     * @param account_name
     * @param public_key
     * @param rmbPrice
     */
    public void getPrepaidInfo(String account_name, String public_key, String rmbPrice){
        WXPayInitialOrderReqParams params = new WXPayInitialOrderReqParams();
        params.setAccount_name(account_name);
        params.setPlatform(ParamConstants.PLATFORM_ANDROID);
        params.setPublic_key(public_key);
        final String serial_num = SysUtils.getDeviceId(getV().getContext());
        params.setSerial_number(serial_num);
        final String ip_address = NetworkUtils.getIPAddress(true);
        params.setClient_ip(ip_address);

        String jsonParams = GsonUtils.objectToJson(params);

        new WXPayInitialOrderRequest(WXPayQueryOrderInfoRequest.class)
                .setJsonParams(jsonParams)
                .initialWXPayOrder(new JsonCallback<WXPayQueryOrderInfoResult>() {
                    @Override
                    public void onStart(Request<WXPayQueryOrderInfoResult, ? extends Request> request) {
                        super.onStart(request);
                        if (getV() != null){
                            getV().showProgressDialog(getV().getString(R.string.eos_tip_payment_time));
                        }
                    }

                    @Override
                    public void onSuccess(Response<WXPayQueryOrderInfoResult> response) {
                        if (response != null && response.body() != null){
                            WXPayQueryOrderInfoResult result = response.body();
                            if (result.getResult() != null){
                                WXPayQueryOrderInfoResult.ResultBean resultBean = result.getResult();
                                String orderId = resultBean.get_id();
                                getV().setResultBean(resultBean);

                                OrderIdEvent orderIdEvent = new OrderIdEvent();
                                orderIdEvent.setOrderId(orderId);
                                EventBusProvider.postSticky(orderIdEvent);
                                getV().setOrderId(orderId);

                                String curRMBPrice = resultBean.getRmb_price();

                                getV().setNewPrice(curRMBPrice);

                                if (!curRMBPrice.equals(rmbPrice)){
                                    //价格已变动，提醒用户
                                    getV().dissmisProgressDialog();
                                    getV().setNewPrice(curRMBPrice);
                                    getV().showPriceChangedDialog();

                                }else {
                                    //价格未变
                                    //调用下一个接口,支付订单
                                    placeOrder(orderId);
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Response<WXPayQueryOrderInfoResult> response) {
                        super.onError(response);
                        getV().dissmisProgressDialog();
                    }
                });
    }

    /**
     * 根据orderID再调中心化服务器支付订单API
     * @param orderId
     */
    public void placeOrder(String orderId){
        new WXPayPlaceOrderRequest(WXPayPlaceOrderResult.class, orderId)
                .getWXPayPlaceOrderInfo(new JsonCallback<WXPayPlaceOrderResult>() {
                    @Override
                    public void onSuccess(Response<WXPayPlaceOrderResult> response) {
                        if (response != null && response.body() != null){
                            WXPayPlaceOrderResult result = response.body();

                            if (result.getResult() != null){
                                WXPayPlaceOrderResult.ResultBean resultBean = result.getResult();
                                getV().callWXPay(resultBean);
                            }
                        }
                    }

                    @Override
                    public void onError(Response<WXPayPlaceOrderResult> response) {
                        super.onError(response);
                        getV().dissmisProgressDialog();
                    }
                });
    }

    /**
     * 获取当前创建账户大约需要多少RMB
     */
    public void getRMBFeeAmount(){
        new WXPayBillRequst(WXPayBillResult.class)
                .getWXPayBill(new JsonCallback<WXPayBillResult>() {
                    @Override
                    public void onStart(Request<WXPayBillResult, ? extends Request> request) {
                        super.onStart(request);
                        if (getV() != null){
                            getV().showProgressDialog(getV().getString(R.string.eos_tip_loading_fee));
                        }
                    }

                    @Override
                    public void onSuccess(Response<WXPayBillResult> response) {

                        if (response != null && response.body() != null && response.body().getResult() != null){
                            WXPayBillResult.ResultBean result = response.body().getResult();
                            getV().setFee(result);
                            getV().dissmisProgressDialog();
                        }
                    }

                    @Override
                    public void onError(Response<WXPayBillResult> response) {
                        super.onError(response);
                        getV().dissmisProgressDialog();
                    }
                });
    }

    /**
     * 查询创建账户状态
     */
    public void checkCreateAccountStatus(String eos_username, String public_key, String orderId){

        UserRegisterReqParams params = new UserRegisterReqParams();
        params.setApp_id(ParamConstants.APP_ID_GEMMA);
        params.setAccount_name(eos_username);
        params.setInvitation_code(orderId);
        params.setPublic_key(public_key);

        String jsonParams = GsonUtils.objectToJson(params);
        LoggerManager.d("create req params", jsonParams);

        new UserRegisterRequest(UserRegisterResult.class)
                .setJsonParams(jsonParams)
                .postJson(new CustomRequestCallback<UserRegisterResult>() {
                    @Override
                    public void onBeforeRequest(@NonNull Disposable disposable) {
                        if (getV() != null){
                            getV().showProgressDialog("");
                        }
                    }

                    @Override
                    public void onSuccess(@NonNull CustomData<UserRegisterResult> data) {
                        if (getV() != null){
                            getV().dissmisProgressDialog();

                            if (data.code == HttpConst.CODE_RESULT_SUCCESS) {
                                UserRegisterResult registerResult = data.result;
                                if (registerResult != null) {
                                    String txId = registerResult.txId;
                                    updateWallet(eos_username, txId, orderId);
                                    TimeStampValidateJob.executedCreateLogic(eos_username, public_key);
                                    AppManager.getAppManager().finishActivity(ActivateAccountMethodActivity.class);
                                    AppManager.getAppManager().finishActivity(CreateEosAccountActivity.class);
                                    UISkipMananger.launchEOSHome(getV().getActivity());
                                }
                            } else if (data.code == 10022){
                                //todo 上链失败,手动再调创建接口
                                checkCreateAccountStatus(eos_username, public_key, orderId);
                            }else if (data.code == 10020){
                                //todo 该Account被抢注
                                GemmaToastUtils.showLongToast(getV().getString(R.string.eos_create_fail));
                            }else {

                            }
                        }

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        if (getV() != null){
                            AlertUtil.showLongUrgeAlert(getV().getActivity(), getV().getString(R.string.eos_tip_check_network));
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (getV() != null){
                            //getV().dissmisProgressDialog();
                        }
                    }
                });
    }


    private void updateWallet(
            final String eosUsername, final String txId, final String invCode) {

        MultiWalletEntity multiWalletEntity = DBManager.getInstance().getMultiWalletEntityDao()
                .getCurrentMultiWalletEntity();

        List<EosWalletEntity> eosWalletEntities = DBManager.getInstance().getMultiWalletEntityDao()
                .getCurrentMultiWalletEntity().getEosWalletEntities();
        EosWalletEntity walletEntity = eosWalletEntities.get(0);

        //设置eosNameJson
        List<String> account_names = new ArrayList<>();
        account_names.add(eosUsername);
        //final String eosNameJson = GsonUtils.objectToJson(account_names);
        //walletEntity.setEosNameJson(eosNameJson);
        //设置currentEosName，创建钱包步骤中可以直接设置，因为默认eosNameJson中只会有一个用户名字符串
        walletEntity.setCurrentEosName(eosUsername);
        //设置当前Transaction的Hash值
        walletEntity.setTxId(txId);
        //设置邀请码
        walletEntity.setInvCode(invCode);
        //设置EOS钱包状态为正在创建
        walletEntity.setIsConfirmLib(ParamConstants.EOSNAME_CONFIRMING);

        //更新钱包
        eosWalletEntities.remove(0);
        eosWalletEntities.add(walletEntity);
        multiWalletEntity.setEosWalletEntities(eosWalletEntities);

        DBManager.getInstance().getMultiWalletEntityDao().saveOrUpateEntitySync(multiWalletEntity);
    }

}


