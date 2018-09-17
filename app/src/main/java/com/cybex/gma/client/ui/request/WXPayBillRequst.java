package com.cybex.gma.client.ui.request;

import com.cybex.gma.client.api.ApiMethod;
import com.cybex.gma.client.api.ApiPath;
import com.cybex.gma.client.api.callback.JsonCallback;
import com.cybex.gma.client.api.request.GMAHttpRequest;
import com.cybex.gma.client.ui.model.response.WXPayBillResult;
import com.cybex.gma.client.ui.model.response.WXPayQueryOrderInfoResult;

/**
 * 向中心化服务器请求创建账户法币支付账单明细
 */
public class WXPayBillRequst extends GMAHttpRequest<WXPayBillResult> {

    public static final String TAG = "WXPayBillRequst";

    /**
     * @param clazz 想要请求返回的Bean
     */

    public WXPayBillRequst(Class clazz) {
        super(clazz);
        setMethod(ApiPath.HOST_CENTER_SERVER + ApiMethod.WXPAY_PAY_BILL);
    }


    public WXPayBillRequst getWXPayBill(JsonCallback<WXPayBillResult> callback) {
        getJsonNoRxRequest(TAG, callback);
        return this;
    }
}
