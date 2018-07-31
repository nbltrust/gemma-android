package com.cybex.gma.client.ui.request;

import com.cybex.gma.client.api.ApiMethod;
import com.cybex.gma.client.api.ApiPath;
import com.cybex.gma.client.api.data.response.CustomData;
import com.cybex.gma.client.api.request.GMAHttpRequest;

/**
 * Created by wanglin on 2018/7/13.
 */
public class PushTransactionRequest extends GMAHttpRequest<CustomData> {

    public final static String TAG = "PushTransactionRequest";


    /**
     * @param clazz 想要请求返回的Bean
     */
    public PushTransactionRequest(Class clazz) {
        super(clazz);
        setMethod(ApiPath.HOST_ON_CHAIN + ApiMethod.API_PUSH_TRANSACTION);
    }
}
