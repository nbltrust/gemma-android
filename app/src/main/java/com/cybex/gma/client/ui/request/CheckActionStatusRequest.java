package com.cybex.gma.client.ui.request;

import com.cybex.componentservice.api.ApiMethod;
import com.cybex.componentservice.api.ApiPath;
import com.cybex.componentservice.api.callback.JsonCallback;
import com.cybex.componentservice.api.request.GMAHttpRequest;
import com.cybex.gma.client.ui.model.response.CheckActionStatusResult;

public class CheckActionStatusRequest extends GMAHttpRequest<CheckActionStatusResult> {

    public static final String TAG = "check_action_status";

    /**
     * @param clazz 想要请求返回的Bean
     */
    public CheckActionStatusRequest(Class clazz, String action_id) {
        super(clazz);
        setMethod(ApiPath.getHostCenterServer() + ApiMethod.API_QUERY_ACTION_STATUS + action_id);
    }


    public CheckActionStatusRequest checkActionStatus(JsonCallback<CheckActionStatusResult> callback) {
        getJsonNoRxRequest(TAG, callback);
        return this;
    }
}
