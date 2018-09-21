package com.cybex.gma.client.ui.presenter;

import com.cybex.gma.client.api.callback.JsonCallback;
import com.cybex.gma.client.config.HttpConst;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.event.WXPayStatusEvent;
import com.cybex.gma.client.manager.LoggerManager;
import com.cybex.gma.client.ui.model.response.WXPayQueryOrderInfoResult;
import com.cybex.gma.client.ui.request.WXPayQueryOrderInfoRequest;
import com.cybex.gma.client.wxapi.WXPayEntryActivity;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.mvp.lite.XPresenter;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

public class WXPayEntryPresenter extends XPresenter<WXPayEntryActivity> {

    /**
     * 微信支付SDK返回成功确认码后发起请求向服务器二次确认
     * @param orderId
     */
    public void verifyOrderStatus(String orderId){
        new WXPayQueryOrderInfoRequest(WXPayQueryOrderInfoResult.class, orderId)
                .getWXPayQueryOrderInfo(new JsonCallback<WXPayQueryOrderInfoResult>() {

                    @Override
                    public void onStart(Request<WXPayQueryOrderInfoResult, ? extends Request> request) {
                        super.onStart(request);
                    }

                    @Override
                    public void onSuccess(Response<WXPayQueryOrderInfoResult> response) {
                        LoggerManager.d("verify status execute");
                        if (getV() != null){
                            if (response != null && response.body() != null
                                    && response.body().getResult() != null){
                                WXPayQueryOrderInfoResult.ResultBean result = response.body().getResult();
                                String pay_state = result.getPay_state();
                                String order_state = result.getStatus();
                                int confirm_status = getRealStatus(pay_state, order_state);
                                LoggerManager.d("confirm_status", confirm_status);
                                WXPayStatusEvent event = new WXPayStatusEvent();
                                event.setStatus(confirm_status);
                                EventBusProvider.postSticky(event);
                                getV().finish();
                            }
                        }
                    }

                    @Override
                    public void onError(Response<WXPayQueryOrderInfoResult> response) {
                        if (getV() != null){
                            super.onError(response);
                        }
                    }
                });
    }


    /**
     * 状态码转换
     * @param pay_state
     * @param order_state
     * @return
     */
    public int getRealStatus(String pay_state, String order_state){
        if (pay_state.equals(HttpConst.WXPAY_STATE_NOTPAY) && order_state.equals(HttpConst.WXPAY_STATUS_INIT)){
            //未支付，等待支付
            return ParamConstants.WX_NOTPAY_INIT;
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
