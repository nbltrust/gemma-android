package com.cybex.gma.client.ui.request;

import com.cybex.componentservice.api.ApiMethod;
import com.cybex.componentservice.api.ApiPath;
import com.cybex.componentservice.api.callback.JsonCallback;
import com.cybex.componentservice.api.request.GMAHttpRequest;
import com.cybex.gma.client.ui.model.response.CheckActionStatusResult;
import com.cybex.gma.client.ui.model.response.UserRegisterResult;
import com.lzy.okgo.callback.StringCallback;

/**
 * 中心化服务器---注册蓝牙钱包账户
 *
 */
public class BluetoothAccountRegisterRequest extends GMAHttpRequest<UserRegisterResult> {


    public static final String TAG = "bluetooth_register";

    /**
     * @param clazz 想要请求返回的Bean
     */
    public BluetoothAccountRegisterRequest(Class clazz) {
        super(clazz);
        setMethod(ApiPath.getHostCenterServer() + ApiMethod.API_CREATE_ACCOUNT);
    }


    public BluetoothAccountRegisterRequest setJsonParams(String jsonParams) {
        super.setJsonParams(jsonParams);
        return this;
    }

    public BluetoothAccountRegisterRequest registerAccount(JsonCallback<UserRegisterResult> callback) {
        postJsonNoRxRequest(TAG, callback);
        return this;
    }

}
