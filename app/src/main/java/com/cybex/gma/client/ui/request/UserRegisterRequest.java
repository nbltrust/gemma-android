package com.cybex.gma.client.ui.request;

import com.cybex.gma.client.api.ApiMethod;
import com.cybex.gma.client.api.ApiPath;
import com.cybex.gma.client.api.request.GMAHttpRequest;
import com.cybex.gma.client.ui.model.response.UserRegisterResult;

/**
 * 中心化服务器---注册request
 *
 * Created by wanglin on 2018/7/12.
 */
public class UserRegisterRequest extends GMAHttpRequest<UserRegisterResult> {

    /**
     * @param clazz 想要请求返回的Bean
     */
    public UserRegisterRequest(Class clazz) {
        super(clazz);
        setMethod(ApiPath.HOST_ON_CHAIN + ApiMethod.API_REGISTER);
    }
}
