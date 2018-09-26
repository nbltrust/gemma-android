package com.cybex.gma.client.ui.request;

import com.cybex.gma.client.api.ApiMethod;
import com.cybex.gma.client.api.ApiPath;
import com.cybex.gma.client.api.request.GMAHttpRequest;
import com.lzy.okgo.callback.StringCallback;

/**
 * Created by wanglin on 2018/7/13.
 */
public class PushTransactionRequest extends GMAHttpRequest {

    public final static String TAG = "PushTransactionRequest";


    /**
     * @param clazz 想要请求返回的Bean
     */
    public PushTransactionRequest(Class clazz) {
        super(clazz);
        setMethod(ApiPath.getHOST_ON_CHAIN() + ApiMethod.API_PUSH_TRANSACTION);
    }


    public PushTransactionRequest setJsonParams(String jsonParams) {
        super.setJsonParams(jsonParams);
        return this;
    }


    /**
     * @param callback
     */
    public PushTransactionRequest pushTransaction(StringCallback callback) {
        postJsonNoRxRequest(TAG, callback);
        return this;
    }
}
