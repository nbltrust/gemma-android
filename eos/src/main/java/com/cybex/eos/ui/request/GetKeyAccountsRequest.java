package com.cybex.eos.ui.request;

import com.cybex.componentservice.api.ApiMethod;
import com.cybex.componentservice.api.ApiPath;
import com.cybex.componentservice.api.callback.JsonCallback;
import com.cybex.componentservice.api.request.GMAHttpRequest;
import com.cybex.eos.ui.model.response.GetKeyAccountsResult;


/**
 * 根据公钥查询账户列表
 *
 * Created by wanglin on 2018/7/26.
 */
public class GetKeyAccountsRequest extends GMAHttpRequest<GetKeyAccountsResult> {

    public static final String TAG = "GetKeyAccountsRequest";

    /**
     * @param clazz 想要请求返回的Bean
     */

    public GetKeyAccountsRequest(Class clazz) {
        super(clazz);
        setMethod(ApiPath.getHOST_ON_CHAIN() + ApiMethod.API_GET_KEY_ACCOUNTS);
    }

    public GetKeyAccountsRequest setJsonParams(String jsonParams) {
        super.setJsonParams(jsonParams);
        return this;
    }


    public GetKeyAccountsRequest getKeyAccountsRequest(JsonCallback<GetKeyAccountsResult> callback) {
        postJsonNoRxRequest(TAG, callback);

        return this;
    }

}
