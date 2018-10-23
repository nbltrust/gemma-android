package com.cybex.eos.ui.request;

import com.cybex.componentservice.api.ApiMethod;
import com.cybex.componentservice.api.ApiPath;
import com.cybex.componentservice.api.callback.JsonCallback;
import com.cybex.componentservice.api.request.GMAHttpRequest;
import com.cybex.eos.ui.model.response.FetchBPDetailsResult;


public class FetchBPDetailRequest extends GMAHttpRequest {
    public static final String TAG = "FetchBPDetailRequest";

    /**
     * @param clazz 想要请求返回的Bean
     */

    public FetchBPDetailRequest(Class clazz, String number) {
        super(clazz);
        setMethod(ApiPath.HOST_CENTER_SERVER + ApiMethod.API_FETCH_BP_DETAILS + number);
    }

    /*
    public FetchBPDetailRequest setJsonParams(String jsonParams) {
        super.setJsonParams(jsonParams);
        return this;
    }
    */

    public FetchBPDetailRequest FetchBPDetailRequest(JsonCallback<FetchBPDetailsResult> callback) {
        getJsonNoRxRequest(TAG, callback);
        //postJsonNoRxRequest(TAG, callback);
        return this;
    }
}
