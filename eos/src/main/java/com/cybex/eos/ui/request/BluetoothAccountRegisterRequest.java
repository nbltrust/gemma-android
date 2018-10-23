package com.cybex.eos.ui.request;

import com.cybex.componentservice.api.ApiMethod;
import com.cybex.componentservice.api.ApiPath;
import com.cybex.componentservice.api.request.GMAHttpRequest;
import com.cybex.eos.ui.model.response.UserRegisterResult;


/**
 * 中心化服务器---注册蓝牙钱包账户
 *
 * Created by wanglin on 2018/9/11.
 */
public class BluetoothAccountRegisterRequest extends GMAHttpRequest<UserRegisterResult> {


    /**
     * @param clazz 想要请求返回的Bean
     */
    public BluetoothAccountRegisterRequest(Class clazz) {
        super(clazz);
        setMethod(ApiPath.HOST_CENTER_SERVER + ApiMethod.API_BLUETOOTH_REGISTER_ACCOUNT);

    }
}
