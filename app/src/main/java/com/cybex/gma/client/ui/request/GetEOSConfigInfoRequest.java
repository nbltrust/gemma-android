package com.cybex.gma.client.ui.request;

import com.cybex.gma.client.api.ApiMethod;
import com.cybex.gma.client.api.ApiPath;
import com.cybex.gma.client.api.request.GMAHttpRequest;
import com.cybex.gma.client.ui.model.response.EOSConfigInfo;

public class GetEOSConfigInfoRequest extends GMAHttpRequest<EOSConfigInfo> {

    public GetEOSConfigInfoRequest(Class clazz){
        super(clazz);
        super.setMethod(ApiPath.HOST_ON_CHAIN_MAIN + ApiMethod.API_GET_INFO);
    }

}
