package com.cybex.gma.client.ui.presenter;

import com.cybex.gma.client.R;
import com.cybex.gma.client.api.callback.JsonCallback;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.event.OrderIdEvent;
import com.cybex.gma.client.ui.fragment.ActivateByRMBFragment;
import com.cybex.gma.client.ui.model.request.WXPayInitialOrderReqParams;
import com.cybex.gma.client.ui.model.response.WXPayBillResult;
import com.cybex.gma.client.ui.model.response.WXPayPlaceOrderResult;
import com.cybex.gma.client.ui.model.response.WXPayQueryOrderInfoResult;
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
                            getV().showProgressDialog(getV().getString(R.string.tip_payment_time));
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
                            getV().showProgressDialog(getV().getString(R.string.tip_loading_fee));
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
}
