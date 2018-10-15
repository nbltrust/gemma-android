package com.cybex.gma.client.ui.request;

import com.cybex.componentservice.api.ApiMethod;
import com.cybex.componentservice.api.ApiPath;
import com.cybex.componentservice.api.request.GMAHttpRequest;
import com.lzy.okgo.callback.StringCallback;

/**
 * 获取Transaction
 */
public class GetTransactionRequest extends GMAHttpRequest {

    public static final String TAG = "get_transaction";


    /**
     * @param clazz 想要请求返回的Bean
     */
    public GetTransactionRequest(Class clazz) {
        super(clazz);
        setMethod(ApiPath.getHOST_ON_CHAIN() + ApiMethod.API_GET_TRANSACTION);
    }


    public GetTransactionRequest setJsonParams(String jsonParams) {
        super.setJsonParams(jsonParams);
        return this;
    }

    /**
     * @param callback
     */
    public GetTransactionRequest getInfo(StringCallback callback) {
        postJsonNoRxRequest(TAG, callback);
        return this;
    }


}
