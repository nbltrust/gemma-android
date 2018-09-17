package com.cybex.gma.client.ui.request;

import com.cybex.gma.client.api.ApiMethod;
import com.cybex.gma.client.api.ApiPath;
import com.cybex.gma.client.api.callback.JsonCallback;
import com.cybex.gma.client.api.request.GMAHttpRequest;
import com.cybex.gma.client.ui.model.response.WXPayQueryOrderInfoResult;


/**
 *向中心化服务器请求创建支付订单
 */
public class WXPayInitialOrderRequest extends GMAHttpRequest<WXPayQueryOrderInfoResult> {

    public final static String TAG = "WXPayInitialOrderRequest";


    /**
     * @param clazz 想要请求返回的Bean
     */
    public WXPayInitialOrderRequest(Class clazz) {
        super(clazz);
        setMethod(ApiPath.HOST_CENTER_SERVER + ApiMethod.WXPAY_INITIAL_ORDER);
    }


    public WXPayInitialOrderRequest setJsonParams(String jsonParams) {
        super.setJsonParams(jsonParams);
        return this;
    }


    /**
     * @param callback
     */
    public WXPayInitialOrderRequest initialWXPayOrder(JsonCallback<WXPayQueryOrderInfoResult> callback) {
        postJsonNoRxRequest(TAG, callback);
        return this;
    }
}
