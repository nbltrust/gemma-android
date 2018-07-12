package com.cybex.gma.client.ui.request;

import com.cybex.gma.client.api.request.GMAHttpRequest;
import com.cybex.gma.client.ui.model.response.AccountInfo;

/**
 * 获得账户信息
 *
 * Created by wanglin on 2018/7/11.
 */
public class GetAccountinfoRequest extends GMAHttpRequest<AccountInfo> {

    /**
     * @param clazz 想要请求返回的Bean
     */
    public GetAccountinfoRequest(Class clazz) {
        super(clazz);
    }
}
