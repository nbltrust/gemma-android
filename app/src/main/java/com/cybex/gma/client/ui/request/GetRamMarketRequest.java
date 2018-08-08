package com.cybex.gma.client.ui.request;

import com.cybex.gma.client.api.ApiMethod;
import com.cybex.gma.client.api.ApiPath;
import com.cybex.gma.client.api.request.GMAHttpRequest;
import com.lzy.okgo.callback.StringCallback;

public class GetRamMarketRequest extends GMAHttpRequest{

    public static final String TAG = "GetRamMarketRequest";

    /**
     * @param clazz 想要请求返回的Bean
     */

    public GetRamMarketRequest(Class clazz) {
        super(clazz);
        setMethod(ApiPath.HOST_ON_CHAIN + ApiMethod.API_GET_RAM_MARKET);
    }

    public GetRamMarketRequest setJsonParams(String jsonParams) {
        super.setJsonParams(jsonParams);
        return this;
    }


    public GetRamMarketRequest getRamMarketRequest(StringCallback callback) {
        postJsonNoRxRequest(TAG, callback);
        return this;
    }
}
