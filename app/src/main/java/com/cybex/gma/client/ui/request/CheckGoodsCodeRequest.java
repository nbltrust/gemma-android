package com.cybex.gma.client.ui.request;

import com.cybex.componentservice.api.ApiMethod;
import com.cybex.componentservice.api.ApiPath;
import com.cybex.componentservice.api.callback.JsonCallback;
import com.cybex.componentservice.api.request.GMAHttpRequest;
import com.cybex.gma.client.ui.model.response.CheckGoodsCodeResult;


/**
 * 使用SN检查该SN对应的权益情况
 */
public class CheckGoodsCodeRequest extends GMAHttpRequest<CheckGoodsCodeResult> {

    public static final String TAG = "check_goods_code";

    /**
     * @param clazz 想要请求返回的Bean
     */
    public CheckGoodsCodeRequest(Class clazz, String SN) {
        super(clazz);
        setMethod(ApiPath.getHostCenterServer() + ApiMethod.API_CHECK_GOOD_CODE + SN);
    }


    public CheckGoodsCodeRequest checkGoodsCode(JsonCallback<CheckGoodsCodeResult> callback) {
        getJsonNoRxRequest(TAG, callback);
        return this;
    }

}
