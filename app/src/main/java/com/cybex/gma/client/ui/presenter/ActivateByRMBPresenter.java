package com.cybex.gma.client.ui.presenter;

import android.support.annotation.NonNull;

import com.cybex.componentservice.api.callback.CustomRequestCallback;
import com.cybex.componentservice.api.callback.JsonCallback;
import com.cybex.componentservice.api.data.response.CustomData;
import com.cybex.componentservice.db.entity.EosWalletEntity;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.db.entity.WalletEntity;
import com.cybex.componentservice.db.util.DBCallback;
import com.cybex.gma.client.R;
import com.cybex.componentservice.config.CacheConstants;
import com.cybex.gma.client.config.HttpConst;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.event.OrderIdEvent;
import com.cybex.gma.client.job.TimeStampValidateJob;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.JNIUtil;
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
     * 创建账户
     */
    public void createAccount(String eos_username, String public_key, String orderId){

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
                                    //saveAccount(public_key, private_key, password, eos_username, passwordTip, txId,
                                            //orderId);
                                    TimeStampValidateJob.executedCreateLogic(eos_username, public_key);
                                    UISkipMananger.launchEOSHome(getV().getActivity());
                                }
                            } else {
                                LoggerManager.d("err");
                            }
                        }

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        if (getV() != null){
                            //getV().dissmisProgressDialog();
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


    public void saveAccount(
            final String publicKey, final String privateKey, final String
            password, final String eosUsername, final String passwordTip,
            final String txId, final String invCode) {

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


    public void updateWallet(
            final String eosUsername, final String txId, final String invCode) {

        MultiWalletEntity multiWalletEntity = DBManager.getInstance().getMultiWalletEntityDao()
                .getCurrentMultiWalletEntity();

        List<EosWalletEntity> eosWalletEntities = DBManager.getInstance().getMultiWalletEntityDao()
                .getCurrentMultiWalletEntity().getEosWalletEntities();
        EosWalletEntity walletEntity = eosWalletEntities.get(0);

        //设置eosNameJson
        List<String> account_names = new ArrayList<>();
        account_names.add(eosUsername);
        final String eosNameJson = GsonUtils.objectToJson(account_names);
        walletEntity.setEosNameJson(eosNameJson);
        //设置currentEosName，创建钱包步骤中可以直接设置，因为默认eosNameJson中只会有一个用户名字符串
        walletEntity.setCurrentEosName(eosUsername);
        //设置当前Transaction的Hash值
        walletEntity.setTxId(txId);
        //设置邀请码
        walletEntity.setInvCode(invCode);

        //更新钱包
        eosWalletEntities.remove(0);
        eosWalletEntities.add(walletEntity);
        multiWalletEntity.setEosWalletEntities(eosWalletEntities);

        DBManager.getInstance().getMultiWalletEntityDao().saveOrUpateEntitySync(multiWalletEntity);
    }

}


