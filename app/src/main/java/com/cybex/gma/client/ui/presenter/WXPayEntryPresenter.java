package com.cybex.gma.client.ui.presenter;

import com.cybex.gma.client.api.callback.JsonCallback;
import com.cybex.gma.client.config.HttpConst;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.event.WXPayStatusEvent;
import com.cybex.gma.client.ui.model.response.WXPayQueryOrderInfoResult;
import com.cybex.gma.client.ui.request.WXPayQueryOrderInfoRequest;
import com.cybex.gma.client.wxapi.WXPayEntryActivity;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.mvp.lite.XPresenter;
import com.lzy.okgo.model.Response;

public class WXPayEntryPresenter extends XPresenter<WXPayEntryActivity> {

    public void verifyOrderStatus(String orderId){
        new WXPayQueryOrderInfoRequest(WXPayQueryOrderInfoResult.class, orderId)
                .getWXPayQueryOrderInfo(new JsonCallback<WXPayQueryOrderInfoResult>() {

                    @Override
                    public void onSuccess(Response<WXPayQueryOrderInfoResult> response) {
                        if (response != null && response.body() != null
                                && response.body().getResult() != null){
                            WXPayQueryOrderInfoResult.ResultBean result = response.body().getResult();
                            String pay_state = result.getPay_state();
                            String order_state = result.getStatus();
                            int confirm_status = getRealStatus(pay_state, order_state);
                            WXPayStatusEvent event = new WXPayStatusEvent();
                            event.setStatus(confirm_status);
                            EventBusProvider.postSticky(event);
                        }
                    }

                    @Override
                    public void onError(Response<WXPayQueryOrderInfoResult> response) {
                        super.onError(response);
                    }
                });
    }

    public int getRealStatus(String pay_state, String order_state){
        if (pay_state.equals(HttpConst.WXPAY_STATE_NOTPAY) && order_state.equals(HttpConst.WXPAY_STATUS_INIT)){
            //未支付，等待支付
            return ParamConstants.WX_NOTPAY_WAIT;
        }else if (pay_state.equals(HttpConst.WXPAY_STATE_NOTPAY) && order_state.equals(HttpConst.WXPAY_STATUS_CLOSED)){
            //订单超时，已关闭
            return ParamConstants.WX_NOTPAY_CLOSED;
        }else if (pay_state.equals(HttpConst.WXPAY_STATE_SUCCESS) && order_state.equals(HttpConst.WXPAY_STATUS_DONE)){
            //支付完成
            return ParamConstants.WX_SUCCESS_DONE;
        }else if (pay_state.equals(HttpConst.WXPAY_STATE_SUCCESS) && order_state.equals(HttpConst.WXPAY_STATUS_TOREFUND)){
            //支付完成但订单金额和现在的创建账户金额已经不符，需要退款
            return ParamConstants.WX_SUCCESS_TOREFUND;
        }else if (pay_state.equals(HttpConst.WXPAY_STATE_REFUND)){
            //已退款
            return ParamConstants.WX_REFUND;
        }else if (pay_state.equals(HttpConst.WXPAY_STATE_CLOSED)){
            //订单已关闭
            return ParamConstants.WX_CLOSED;
        }else if (pay_state.equals(HttpConst.WXPAY_STATE_USERPAYING)){
            //正在支付
            return ParamConstants.WX_USERPAYING;
        }else if (pay_state.equals(HttpConst.WXPAY_STATE_PAYERROR)){
            //支付错误
            return ParamConstants.WX_PAYERROR;
        }
        return 0;
    }
}
