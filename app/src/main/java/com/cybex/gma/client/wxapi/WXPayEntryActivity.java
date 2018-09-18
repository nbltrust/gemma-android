package com.cybex.gma.client.wxapi;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.cybex.gma.client.R;
import com.cybex.gma.client.api.callback.JsonCallback;
import com.cybex.gma.client.config.HttpConst;
import com.cybex.gma.client.event.OrderIdEvent;
import com.cybex.gma.client.manager.LoggerManager;
import com.cybex.gma.client.ui.model.response.WXPayQueryOrderInfoResult;
import com.cybex.gma.client.ui.presenter.WXPayEntryPresenter;
import com.cybex.gma.client.ui.request.WXPayQueryOrderInfoRequest;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.lzy.okgo.model.Response;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class WXPayEntryActivity extends XActivity<WXPayEntryPresenter> implements IWXAPIEventHandler {

    private String orderId;


    @Override
    public boolean useEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.POSTING, sticky = true)
    public void getOrderId(OrderIdEvent event){
        this.orderId = event.getOrderId();
    }
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
        LoggerManager.d("resp err code", baseResp.errCode);
        if(baseResp.getType()== ConstantsAPI.COMMAND_PAY_BY_WX){
            switch (baseResp.errCode){
                case HttpConst.WXPAY_SUCCESS:
                    LoggerManager.d("payment success");
                    if (EmptyUtils.isNotEmpty(orderId)){
                        LoggerManager.d("orderId", orderId);
                        //getP().verifyOrderStatus(orderId);
                    }
                    //成功，去后台查询
                    break;
                case HttpConst.WXPAY_ERROR:
                    LoggerManager.d("payment err");
                    //失败
                    break;
                case HttpConst.WXPAY_CANCEL:
                    LoggerManager.d("payment canceled");
                    finish();
                    //被取消
                    break;
            }
        }
    }


    @Override
    public void bindUI(View rootView) {

    }

    @Override
    public void initData(Bundle savedInstanceState) {

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    public WXPayEntryPresenter newP() {
        return new WXPayEntryPresenter();
    }
}
