package com.cybex.gma.client.ui.request;

import com.cybex.gma.client.api.ApiMethod;
import com.cybex.gma.client.api.ApiPath;
import com.cybex.gma.client.api.callback.JsonCallback;
import com.cybex.gma.client.api.request.GMAHttpRequest;
import com.cybex.gma.client.ui.model.response.GetKeyAccountsResult;

/**
 * 根据公钥查询账户列表
 *
 * Created by wanglin on 2018/7/26.
 */
public class GetKeyAccountsRequest extends GMAHttpRequest<GetKeyAccountsResult> {

    private static final String TAG = "GetKeyAccountsRequest";

    /**
     * @param clazz 想要请求返回的Bean
     */
    public GetKeyAccountsRequest(Class clazz) {
        super(clazz);
        setMethod(ApiPath.HOST_ON_CHAIN_MAIN + ApiMethod.API_GET_KEY_ACCOUNTS);

    }

    public void getkEYAccountS(JsonCallback<GetKeyAccountsResult> callback) {
        postJsonNoRxRequest(TAG, callback);
    }

}
