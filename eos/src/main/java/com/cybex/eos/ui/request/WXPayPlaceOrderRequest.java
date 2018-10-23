package com.cybex.eos.ui.request;

import com.cybex.componentservice.api.ApiMethod;
import com.cybex.componentservice.api.ApiPath;
import com.cybex.componentservice.api.callback.JsonCallback;
import com.cybex.componentservice.api.request.GMAHttpRequest;
import com.cybex.eos.ui.model.response.WXPayPlaceOrderResult;


public class WXPayPlaceOrderRequest extends GMAHttpRequest<WXPayPlaceOrderResult> {

    public static final String TAG = "WXPayPlaceOrderResult";
    private static final String URL_SUFFIX = "/place";

    /**
     * @param clazz 想要请求返回的Bean
     */

    public WXPayPlaceOrderRequest(Class clazz, String orderId) {
        super(clazz);
        setMethod(ApiPath.HOST_CENTER_SERVER + ApiMethod.WXPAY_PLACE_ORDER+ orderId + URL_SUFFIX);
    }


    public WXPayPlaceOrderRequest getWXPayPlaceOrderInfo(JsonCallback<WXPayPlaceOrderResult> callback) {
        postJsonNoRxRequest(TAG, callback);
        return this;
    }
}
