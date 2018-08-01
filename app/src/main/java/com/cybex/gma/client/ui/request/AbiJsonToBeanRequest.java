package com.cybex.gma.client.ui.request;

import com.cybex.gma.client.api.ApiMethod;
import com.cybex.gma.client.api.ApiPath;
import com.cybex.gma.client.api.callback.JsonCallback;
import com.cybex.gma.client.api.request.GMAHttpRequest;
import com.cybex.gma.client.ui.model.response.AbiJsonToBeanResult;

/**
 * Created by wanglin on 2018/7/13.
 */
public class AbiJsonToBeanRequest extends GMAHttpRequest<AbiJsonToBeanResult> {

    public static final String TAG = "abi_json_to_bin";

    /**
     * @param clazz 想要请求返回的Bean
     */
    public AbiJsonToBeanRequest(Class clazz) {
        super(clazz);
        setMethod(ApiPath.HOST_ON_CHAIN + ApiMethod.API_ABI_JSON_TO_BIN);
    }

    public AbiJsonToBeanRequest setJsonParams(String jsonParams) {
        super.setJsonParams(jsonParams);
        return this;
    }

    public AbiJsonToBeanRequest getAbiJsonToBean(JsonCallback<AbiJsonToBeanResult> callback) {
        postJsonNoRxRequest(TAG, callback);

        return this;
    }

}
