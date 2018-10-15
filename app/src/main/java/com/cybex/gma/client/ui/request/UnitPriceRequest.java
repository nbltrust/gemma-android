package com.cybex.gma.client.ui.request;

import com.cybex.componentservice.api.ApiPath;
import com.cybex.componentservice.api.callback.JsonCallback;
import com.cybex.componentservice.api.request.GMAHttpRequest;
import com.cybex.gma.client.ui.model.response.UnitPrice;

/**
 * Created by wanglin on 2018/8/8.
 */
public class UnitPriceRequest extends GMAHttpRequest<UnitPrice> {



    public static final String TAG = "get_unit_price";

    /**
     * @param clazz 想要请求返回的Bean
     */
    public UnitPriceRequest(Class clazz) {
        super(clazz);
        setMethod(ApiPath.URL_UNIT_PRICE);
    }


    public UnitPriceRequest setJsonParams(String jsonParams) {
        super.setJsonParams(jsonParams);
        return this;
    }


    public UnitPriceRequest getUnitPriceRequest(JsonCallback<UnitPrice> callback) {
        super.getJsonNoRxRequest(TAG, callback);

        return this;
    }

}
