package com.cybex.eos.ui.request;

import com.cybex.componentservice.api.ApiMethod;
import com.cybex.componentservice.api.ApiPath;
import com.cybex.componentservice.api.request.GMAHttpRequest;
import com.lzy.okgo.callback.StringCallback;

/**
 * 获取配置信息
 *
 * Created by wanglin on 2018/8/1.
 */
public class EOSConfigInfoRequest extends GMAHttpRequest {

    public static final String TAG = "get_info";


    /**
     * @param clazz 想要请求返回的Bean
     */
    public EOSConfigInfoRequest(Class clazz) {
        super(clazz);
        setMethod(ApiPath.getHOST_ON_CHAIN() + ApiMethod.API_GET_INFO);
    }


    public EOSConfigInfoRequest setJsonParams(String jsonParams) {
        super.setJsonParams(jsonParams);
        return this;
    }


    /**
     * @param callback
     */
    public EOSConfigInfoRequest getInfo(StringCallback callback) {
        postJsonNoRxRequest(TAG, callback);
        return this;
    }
}

