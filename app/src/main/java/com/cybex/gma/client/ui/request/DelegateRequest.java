package com.cybex.gma.client.ui.request;

import com.cybex.componentservice.api.ApiMethod;
import com.cybex.componentservice.api.ApiPath;
import com.cybex.componentservice.api.callback.JsonCallback;
import com.cybex.componentservice.api.request.GMAHttpRequest;
import com.cybex.gma.client.ui.model.response.AccountInfo;
import com.cybex.gma.client.ui.model.response.DelegateReqResult;

public class DelegateRequest extends GMAHttpRequest<DelegateReqResult> {

    public static final String TAG = "get_account";

    /**
     * @param clazz 想要请求返回的Bean
     */
    public DelegateRequest(Class clazz) {
        super(clazz);
        setMethod(ApiPath.getHostCenterServer() + ApiMethod.API_BIO_DELEGATE);
    }


    public DelegateRequest setJsonParams(String jsonParams) {
        super.setJsonParams(jsonParams);
        return this;
    }


    public DelegateRequest delegate(JsonCallback<DelegateReqResult> callback) {
        postJsonNoRxRequest(TAG, callback);
        return this;
    }
}
