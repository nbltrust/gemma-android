package com.cybex.eth.ui.request;


import com.cybex.componentservice.api.ApiPath;
import com.cybex.componentservice.api.callback.JsonCallback;
import com.cybex.componentservice.api.request.GMAHttpRequest;
import com.cybex.eth.ui.model.reponse.GetGasPriceResult;

public class GetGasPriceRequest extends GMAHttpRequest<GetGasPriceResult> {


    public static final String TAG = "GetGasPriceRequest";

    public GetGasPriceRequest(Class clazz) {
        super(clazz);
        setMethod(ApiPath.HOST_GAS_STATION);
    }

    public GetGasPriceRequest getJson(JsonCallback<GetGasPriceResult> callback) {
        getJsonNoRxRequest(TAG, callback);
        return this;
    }
}


