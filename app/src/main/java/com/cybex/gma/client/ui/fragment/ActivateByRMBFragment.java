package com.cybex.gma.client.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.allen.library.SuperTextView;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.manager.LoggerManager;
import com.cybex.gma.client.ui.model.response.WXPayPlaceOrderResult;
import com.cybex.gma.client.ui.presenter.ActivateByRMBPresenter;
import com.hxlx.core.lib.common.async.TaskManager;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.mm.opensdk.utils.ILog;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ActivateByRMBFragment extends XFragment<ActivateByRMBPresenter> {

    Unbinder unbinder;
    IWXAPI iwxapi;
    @BindView(R.id.iv_dot_one) ImageView ivDotOne;
    @BindView(R.id.tv_look_around_hint) TextView tvLookAroundHint;
    @BindView(R.id.tv_CPU) SuperTextView tvCPU;
    @BindView(R.id.tv_NET) SuperTextView tvNET;
    @BindView(R.id.tv_RAM) SuperTextView tvRAM;
    @BindView(R.id.tv_rmb_amount) TextView tvRmbAmount;
    @BindView(R.id.bt_wechat_pay) Button btWechatPay;

    private final String test_eos_account = "helloeoscoin";
    private final String test_public_key = "";

    @OnClick(R.id.bt_wechat_pay)
    public void initPayProcess(){
        if (getArguments() != null){
            String account_name = getArguments().getString("account_name");
            String public_key = getArguments().getString("public_key");
            getP().getPrepaidInfo(account_name, public_key);
        }
    }

    public static ActivateByRMBFragment newInstance(Bundle args) {
        ActivateByRMBFragment fragment = new ActivateByRMBFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
    }

    @Override
    public void initData(Bundle savedInstanceState) {




    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_activate_by_rmb;
    }

    @Override
    public ActivateByRMBPresenter newP() {
        return new ActivateByRMBPresenter();
    }

    public void callWXPay(WXPayPlaceOrderResult.ResultBean result){
        //初始化微信支付
        iwxapi = WXAPIFactory.createWXAPI(getContext(), null);
        iwxapi.registerApp(ParamConstants.WXPAY_APPID);

        TaskManager.execute(new Runnable() {
            @Override
            public void run() {
                final String timestamp = String.valueOf(Calendar.getInstance().getTime());
                LoggerManager.d("timestamp", timestamp);
                PayReq req = new PayReq();
                req.appId =result.getAppid();
                req.partnerId = ParamConstants.WXPAY_PARTNER_ID;
                req.prepayId = result.getPrepay_id();
                req.packageValue = "Sign=WXPay";
                req.nonceStr = result.getNonce_str();
                req.timeStamp = timestamp;
                req.sign = result.getSign();
                iwxapi.sendReq(req);
            }
        });

        /*
        Runnable pay = new Runnable() {
            @Override
            public void run() {

            }
        };

        Thread payThread = new Thread(pay);
        payThread.start();
        */


    }
}
