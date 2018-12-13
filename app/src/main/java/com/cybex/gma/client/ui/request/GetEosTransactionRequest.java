package com.cybex.gma.client.ui.request;

import com.cybex.componentservice.api.ApiMethod;
import com.cybex.componentservice.api.ApiPath;
import com.cybex.componentservice.api.callback.JsonCallback;
import com.cybex.componentservice.api.request.GMAHttpRequest;
import com.cybex.gma.client.ui.model.response.GetEosTransactionResult;
import com.lzy.okgo.callback.StringCallback;

/**
 * 获取Transaction
 */
public class GetEosTransactionRequest extends GMAHttpRequest<GetEosTransactionResult> {

    public static final String TAG = "eos_spark_get_transaction";


    /**
     * @param clazz 想要请求返回的Bean
     */
    public GetEosTransactionRequest(Class clazz, String txId) {
        super(clazz);
        setMethod(ApiPath.EOS_WEB + ApiMethod.EOS_WEB_GET_TRANSACTION + txId);
    }


//    public GetEosTransactionRequest setJsonParams(String jsonParams) {
//        super.setJsonParams(jsonParams);
//        return this;
//    }

    /**
     * @param callback
     */
    public GetEosTransactionRequest getTransaction(JsonCallback<GetEosTransactionResult> callback) {
        getJsonNoRxRequest(TAG, callback);
        return this;
    }


}
