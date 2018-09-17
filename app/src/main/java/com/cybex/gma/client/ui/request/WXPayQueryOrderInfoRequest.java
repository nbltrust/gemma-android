package com.cybex.gma.client.ui.request;

import com.cybex.gma.client.api.ApiMethod;
import com.cybex.gma.client.api.ApiPath;
import com.cybex.gma.client.api.callback.JsonCallback;
import com.cybex.gma.client.api.request.GMAHttpRequest;
import com.cybex.gma.client.ui.model.response.WXPayQueryOrderInfoResult;

/**
 * 向中心化服务器请求支付订单详情
 */
public class WXPayQueryOrderInfoRequest extends GMAHttpRequest<WXPayQueryOrderInfoResult> {

    public static final String TAG = "WXPayQueryOrderInfoRequest";

    /**
     * @param clazz 想要请求返回的Bean
     */

    public WXPayQueryOrderInfoRequest(Class clazz, String orderId) {
        super(clazz);
        setMethod(ApiPath.HOST_CENTER_SERVER + ApiMethod.WXPAY_QUERY_ORDER_INFO + orderId);
    }


    public WXPayQueryOrderInfoRequest getWXPayQueryOrderInfo(JsonCallback<WXPayQueryOrderInfoResult> callback) {
        getJsonNoRxRequest(TAG, callback);
        return this;
    }
}
