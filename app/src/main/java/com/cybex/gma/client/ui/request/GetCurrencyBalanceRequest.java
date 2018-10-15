package com.cybex.gma.client.ui.request;

import com.cybex.componentservice.api.ApiMethod;
import com.cybex.componentservice.api.ApiPath;
import com.cybex.componentservice.api.request.GMAHttpRequest;
import com.lzy.okgo.callback.StringCallback;

/**
 * 获取指定资产的余额
 *
 * Created by wanglin on 2018/7/13.
 */
public class GetCurrencyBalanceRequest extends GMAHttpRequest {

    public static final String TAG = "get_currency_banlance";


    /**
     * @param clazz 想要请求返回的Bean
     */
    public GetCurrencyBalanceRequest(Class clazz) {
        super(clazz);
        setMethod(ApiPath.getHOST_ON_CHAIN() + ApiMethod.API_GET_CUREENCY_BALANCE);
    }


    public GetCurrencyBalanceRequest setJsonParams(String jsonParams) {
        super.setJsonParams(jsonParams);
        return this;
    }


    /**
     * @param callback
     */
    public GetCurrencyBalanceRequest getCurrencyBalance(StringCallback callback) {
        postJsonNoRxRequest(TAG, callback);
        return this;
    }
}
