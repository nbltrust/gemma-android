package com.cybex.gma.client.ui.request;

import com.cybex.gma.client.api.ApiMethod;
import com.cybex.gma.client.api.ApiPath;
import com.cybex.gma.client.api.callback.JsonCallback;
import com.cybex.gma.client.api.request.GMAHttpRequest;
import com.cybex.gma.client.ui.model.response.FetchBPDetailsResult;

public class FetchBPDetailRequest extends GMAHttpRequest {
    public static final String TAG = "FetchBPDetailRequest";

    /**
     * @param clazz 想要请求返回的Bean
     */

    public FetchBPDetailRequest(Class clazz) {
        super(clazz);
        setMethod(ApiPath.HOST_ON_CHAIN + ApiMethod.API_GET_KEY_ACCOUNTS);
    }

    public FetchBPDetailRequest setJsonParams(String jsonParams) {
        super.setJsonParams(jsonParams);
        return this;
    }

    public FetchBPDetailRequest FetchBPDetailRequest(JsonCallback<FetchBPDetailsResult> callback) {
        postJsonNoRxRequest(TAG, callback);
        return this;
    }
}
