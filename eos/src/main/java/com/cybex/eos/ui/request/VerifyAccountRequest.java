package com.cybex.eos.ui.request;

import com.cybex.componentservice.api.ApiMethod;
import com.cybex.componentservice.api.ApiPath;
import com.cybex.componentservice.api.data.response.CustomData;
import com.cybex.componentservice.api.request.GMAHttpRequest;

/**
 * 账户验证请求
 *
 * Created by wanglin on 2018/7/13.
 */
public class VerifyAccountRequest extends GMAHttpRequest<CustomData> {

    /**
     * @param clazz 想要请求返回的Bean
     */
    public VerifyAccountRequest(Class clazz) {
        super(clazz);
        setMethod(ApiPath.HOST_CENTER_SERVER + ApiMethod.API_VERIFY_ACCOUNT);

    }
}
