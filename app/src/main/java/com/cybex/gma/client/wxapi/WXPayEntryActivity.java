package com.cybex.gma.client.wxapi;

import android.app.Activity;

import com.cybex.gma.client.R;
import com.cybex.gma.client.api.callback.JsonCallback;
import com.cybex.gma.client.config.HttpConst;
import com.cybex.gma.client.manager.LoggerManager;
import com.cybex.gma.client.ui.model.response.WXPayQueryOrderInfoResult;
import com.cybex.gma.client.ui.request.WXPayQueryOrderInfoRequest;
import com.lzy.okgo.model.Response;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {


    /**
     * 微信发送的请求的回调
     * @param baseReq
     */
    @Override
    public void onReq(BaseReq baseReq) {

    }

    /**
     * 发送到微信的请求的回调
     * @param baseResp
     */
    @Override
    public void onResp(BaseResp baseResp) {
        if(baseResp.getType()== ConstantsAPI.COMMAND_PAY_BY_WX){
            switch (baseResp.errCode){
                case HttpConst.WXPAY_SUCCESS:
                    //成功
                    break;
                case HttpConst.WXPAY_ERROR:
                    //失败
                    break;
                case HttpConst.WXPAY_CANCEL:
                    //被取消
                    break;
            }
        }
    }

    public void verifyOrderStatus(String orderId){
        new WXPayQueryOrderInfoRequest(WXPayQueryOrderInfoResult.class, orderId)
                .getWXPayQueryOrderInfo(new JsonCallback<WXPayQueryOrderInfoResult>() {
                    @Override
                    public void onSuccess(Response<WXPayQueryOrderInfoResult> response) {
                        if (response != null && response.body() != null
                                && response.body().getResult() != null){
                            WXPayQueryOrderInfoResult.ResultBean result = response.body().getResult();
                            String pay_state = result.getPay_state();

                        }
                    }

                    @Override
                    public void onError(Response<WXPayQueryOrderInfoResult> response) {
                        super.onError(response);
                    }
                });
    }

    public void checkStatus(int status){

    }
}
