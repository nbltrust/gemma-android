package com.cybex.gma.client.ui.presenter;

import com.cybex.gma.client.api.callback.JsonCallback;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.manager.LoggerManager;
import com.cybex.gma.client.ui.fragment.ActivateByRMBFragment;
import com.cybex.gma.client.ui.model.request.WXPayInitialOrderReqParams;
import com.cybex.gma.client.ui.model.response.WXPayPlaceOrderResult;
import com.cybex.gma.client.ui.model.response.WXPayQueryOrderInfoResult;
import com.cybex.gma.client.ui.request.WXPayInitialOrderRequest;
import com.cybex.gma.client.ui.request.WXPayPlaceOrderRequest;
import com.cybex.gma.client.ui.request.WXPayQueryOrderInfoRequest;
import com.hxlx.core.lib.mvp.lite.XPresenter;
import com.hxlx.core.lib.utils.GsonUtils;
import com.hxlx.core.lib.utils.android.SysUtils;
import com.lzy.okgo.model.Response;

public class ActivateByRMBPresenter extends XPresenter<ActivateByRMBFragment> {

    public void getPrepaidInfo(String account_name, String public_key){
        WXPayInitialOrderReqParams params = new WXPayInitialOrderReqParams();
        params.setAccount_name(account_name);
        params.setPlatform(ParamConstants.PLATFORM_ANDROID);
        params.setPublic_key(public_key);
        final String serial_num = SysUtils.getDeviceId(getV().getContext());
        LoggerManager.d("serial_num", serial_num);
        params.setSerial_number(serial_num);

        String jsonParams = GsonUtils.objectToJson(params);

        new WXPayInitialOrderRequest(WXPayQueryOrderInfoRequest.class)
                .setJsonParams(jsonParams)
                .initialWXPayOrder(new JsonCallback<WXPayQueryOrderInfoResult>() {
                    @Override
                    public void onSuccess(Response<WXPayQueryOrderInfoResult> response) {
                        if (response != null && response.body() != null){
                            WXPayQueryOrderInfoResult result = response.body();
                            LoggerManager.d("QueryOrderResult", result);
                            if (result.getResult() != null){
                                WXPayQueryOrderInfoResult.ResultBean resultBean = result.getResult();
                                String orderId = resultBean.get_id();

                                //调用下一个接口
                                placeOrder(orderId);
                            }
                        }
                    }

                    @Override
                    public void onError(Response<WXPayQueryOrderInfoResult> response) {
                        super.onError(response);
                    }
                });
    }

    public void placeOrder(String orderId){
        new WXPayPlaceOrderRequest(WXPayPlaceOrderResult.class, orderId)
                .getWXPayPlaceOrderInfo(new JsonCallback<WXPayPlaceOrderResult>() {
                    @Override
                    public void onSuccess(Response<WXPayPlaceOrderResult> response) {
                        if (response != null && response.body() != null){
                            WXPayPlaceOrderResult result = response.body();
                            LoggerManager.d("PlaceOrderResult", result);

                            if (result.getResult() != null){
                                WXPayPlaceOrderResult.ResultBean resultBean = result.getResult();
                                getV().callWXPay(resultBean);
                            }

                        }
                    }

                    @Override
                    public void onError(Response<WXPayPlaceOrderResult> response) {
                        super.onError(response);
                    }
                });
    }
}
