package com.cybex.gma.client.ui.request;

import com.cybex.componentservice.api.ApiMethod;
import com.cybex.componentservice.api.ApiPath;
import com.cybex.componentservice.api.callback.JsonCallback;
import com.cybex.componentservice.api.request.GMAHttpRequest;
import com.cybex.gma.client.ui.model.response.WXPayBillResult;

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
