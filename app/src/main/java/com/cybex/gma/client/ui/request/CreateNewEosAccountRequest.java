package com.cybex.gma.client.ui.request;

import com.cybex.componentservice.api.ApiMethod;
import com.cybex.componentservice.api.ApiPath;
import com.cybex.componentservice.api.request.GMAHttpRequest;
import com.lzy.okgo.callback.StringCallback;

public class CreateNewEosAccountRequest extends GMAHttpRequest {

    public static final String TAG = "create_new_eos_account";


    /**
     * @param clazz 想要请求返回的Bean
     */
    public CreateNewEosAccountRequest(Class clazz) {
        super(clazz);
        setMethod(ApiPath.getHOST_ON_CHAIN() + ApiMethod.API_CREATE_ACCOUNT);
    }


    public CreateNewEosAccountRequest setJsonParams(String jsonParams) {
        super.setJsonParams(jsonParams);
        return this;
    }

    /**
     * @param callback
     */
    public CreateNewEosAccountRequest createNewAccount(StringCallback callback) {
        postJsonNoRxRequest(TAG, callback);
        return this;
    }
}
