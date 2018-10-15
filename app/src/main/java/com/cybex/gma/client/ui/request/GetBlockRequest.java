package com.cybex.gma.client.ui.request;

import com.cybex.componentservice.api.ApiMethod;
import com.cybex.componentservice.api.ApiPath;
import com.cybex.componentservice.api.request.GMAHttpRequest;
import com.lzy.okgo.callback.StringCallback;

public class GetBlockRequest extends GMAHttpRequest {
    public static final String TAG = "get_block";

    /**
     * @param clazz 想要请求返回的Bean
     */
    public GetBlockRequest(Class clazz) {
        super(clazz);
        setMethod(ApiPath.getHOST_ON_CHAIN() + ApiMethod.API_GET_BLOCK);
    }


    public GetBlockRequest setJsonParams(String jsonParams) {
        super.setJsonParams(jsonParams);
        return this;
    }

    public GetBlockRequest getBlock(StringCallback callback) {
        postJsonNoRxRequest(TAG, callback);
        return this;
    }
}
