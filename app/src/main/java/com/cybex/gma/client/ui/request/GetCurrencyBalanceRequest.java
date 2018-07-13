package com.cybex.gma.client.ui.request;

import com.cybex.gma.client.api.ApiMethod;
import com.cybex.gma.client.api.ApiPath;
import com.cybex.gma.client.api.request.GMAHttpRequest;
import com.lzy.okgo.callback.StringCallback;

/**
 * 获取指定资产的余额
 *
 * Created by wanglin on 2018/7/13.
 */
public class GetCurrencyBalanceRequest extends GMAHttpRequest {

    private static final String TAG = "get_currency_banlance";


    /**
     * @param clazz 想要请求返回的Bean
     */
    public GetCurrencyBalanceRequest(Class clazz) {
        super(clazz);
        setMethod(ApiPath.HOST_ON_CHAIN + ApiMethod.API_GET_CUREENCY_BALANCE);
    }

    /**
     * @param callback
     */
    public void getCurrencyBalance(StringCallback callback) {
        postJsonNoRxRequest(TAG, callback);
    }
}
