package com.cybex.gma.client.ui.request;

import com.cybex.componentservice.api.ApiMethod;
import com.cybex.componentservice.api.ApiPath;
import com.cybex.componentservice.api.callback.JsonCallback;
import com.cybex.componentservice.api.request.GMAHttpRequest;
import com.cybex.gma.client.ui.model.response.GetEosTokensResult;

public class GetEosTokensRequest extends GMAHttpRequest {
    public static final String TAG = "GetEosTokensRequest";

    /**
     * @param clazz 想要请求返回的Bean
     */

    public GetEosTokensRequest(Class clazz, String account_name) {
        super(clazz);
        setMethod(ApiPath.HOST_CENTER_SERVER + ApiMethod.API_GET_EOS_TOKENS + account_name);
    }


    public GetEosTokensRequest getEosTokens(JsonCallback<GetEosTokensResult> callback) {
        getJsonNoRxRequest(TAG, callback);
        return this;
    }
}
