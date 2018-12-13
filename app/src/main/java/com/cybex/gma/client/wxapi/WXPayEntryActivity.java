package com.cybex.gma.client.wxapi;

import android.os.Bundle;
import android.view.View;

import com.cybex.gma.client.R;
import com.cybex.gma.client.config.HttpConst;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.event.OrderIdEvent;
import com.cybex.gma.client.event.WXPayStatusEvent;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.gma.client.ui.presenter.WXPayEntryPresenter;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class WXPayEntryActivity extends XActivity<WXPayEntryPresenter> implements IWXAPIEventHandler {

    private String orderId;
    IWXAPI api;
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
                    //成功，去后台查询
                    LoggerManager.d("payment success");
                    if (EmptyUtils.isNotEmpty(orderId)){
                        LoggerManager.d("orderId", orderId);
                        getP().verifyOrderStatus(orderId);
                    }
                    break;
                case HttpConst.WXPAY_ERROR:
                    //失败
                    LoggerManager.d("payment err");
                    WXPayStatusEvent event_err = new WXPayStatusEvent();
                    event_err.setStatus(ParamConstants.WX_PAYERROR);
                    EventBusProvider.postSticky(event_err);
                    finish();
                    break;
                case HttpConst.WXPAY_CANCEL:
                    //被取消
                    LoggerManager.d("payment canceled");
                    if (EmptyUtils.isNotEmpty(orderId)){
                        LoggerManager.d("orderId", orderId);
                        getP().verifyOrderStatus(orderId);
                    }
                    /*
                    WXPayStatusEvent event_close = new WXPayStatusEvent();
                    event_close.setStatus(ParamConstants.WX_CLOSED);
                    EventBusProvider.postSticky(event_close);
                    finish();
                    */
                    break;
            }
        }
    }


    @Override
    public void bindUI(View rootView) {

    }

    @Override
    public void initData(Bundle savedInstanceState) {
        api = WXAPIFactory.createWXAPI(this, ParamConstants.WXPAY_APPID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dissmisProgressDialog();
    }

    @Override
    public int getLayoutId() {
        return R.layout.eos_activity_about;
    }

    @Override
    public WXPayEntryPresenter newP() {
        return new WXPayEntryPresenter();
    }
}
