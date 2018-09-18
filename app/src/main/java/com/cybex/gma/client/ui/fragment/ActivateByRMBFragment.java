package com.cybex.gma.client.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.allen.library.SuperTextView;
import com.cybex.gma.client.GmaApplication;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.event.WXPayStatusEvent;
import com.cybex.gma.client.manager.LoggerManager;
import com.cybex.gma.client.ui.model.response.WXPayBillResult;
import com.cybex.gma.client.ui.model.response.WXPayPlaceOrderResult;
import com.cybex.gma.client.ui.presenter.ActivateByRMBPresenter;
import com.hxlx.core.lib.common.async.Task;
import com.hxlx.core.lib.common.async.TaskManager;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.siberiadante.customdialoglib.CustomDialog;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.mm.opensdk.utils.ILog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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

    @OnClick(R.id.bt_wechat_pay)
    public void initPayProcess(){
        if (getArguments() != null){
            String account_name = getArguments().getString("account_name");
            String public_key = getArguments().getString("public_key");
            String rmbFee = tvRmbAmount.getText().toString().trim();
            getP().getPrepaidInfo(account_name, public_key, rmbFee);
        }
    }

    public static ActivateByRMBFragment newInstance(Bundle args) {
        ActivateByRMBFragment fragment = new ActivateByRMBFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void recievePayStatus(WXPayStatusEvent event){
        switch (event.getStatus()){
            case ParamConstants.WX_NOTPAY_WAIT:
                break;
            case ParamConstants.WX_NOTPAY_CLOSED:
                break;
            case ParamConstants.WX_SUCCESS_DONE:
                break;
            case ParamConstants.WX_SUCCESS_TOREFUND:
                break;
            case ParamConstants.WX_REFUND:
                break;
            case ParamConstants.WX_CLOSED:
                break;
            case ParamConstants.WX_USERPAYING:
                break;
            case ParamConstants.WX_PAYERROR:
                break;
        }
    }
    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        //初始化微信支付
        iwxapi = WXAPIFactory.createWXAPI(getActivity(), null);
        iwxapi.registerApp(ParamConstants.WXPAY_APPID);
        getP().getRMBFeeAmount();
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
        TaskManager.execute(new Runnable() {
            @Override
            public void run() {
                final String timestamp = String.valueOf(Calendar.getInstance().getTimeInMillis()/1000);
                PayReq req = new PayReq();
                req.appId =ParamConstants.WXPAY_APPID;
                req.partnerId = ParamConstants.WXPAY_PARTNER_ID;
                req.prepayId = result.getPrepay_id();
                req.packageValue = ParamConstants.WXPAY_PACKAGE_VALUE;
                req.nonceStr = result.getNonce_str();
                req.timeStamp = timestamp;
                req.sign = result.getSign();
                LoggerManager.d("timestamp", timestamp);
                LoggerManager.d("prepayId",result.getPrepay_id());
                LoggerManager.d("nonceStr",result.getNonce_str());
                LoggerManager.d("sign", result.getSign());
                LoggerManager.d("checkArgs", req.checkArgs());
                iwxapi.sendReq(req);
                LoggerManager.d("weChat pay api called");
            }
        });
    }

    public void setFee(WXPayBillResult.ResultBean result){
        tvRmbAmount.setText(String.format(getString(R.string.rmb_fee), result.getRmbPrice()));
        tvCPU.setRightString(result.getCpu() + " EOS");
        tvNET.setRightString(result.getNet() + " EOS");
        tvRAM.setRightString(result.getRam() + " EOS");
    }


    @Override
    public boolean useEventBus() {
        return true;
    }

    /**
     * 显示价格变动Dialog
     */
    private void showPriceChangedDialog() {
        int[] listenedItems = {R.id.tv_cancel, R.id.tv_ok};
        CustomDialog dialog = new CustomDialog(getContext(),
                R.layout.dialog_payment_price_changed, listenedItems, false, Gravity.CENTER);
        dialog.setOnDialogItemClickListener(new CustomDialog.OnCustomDialogItemClickListener() {

            @Override
            public void OnCustomDialogItemClick(CustomDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.tv_cancel:
                        dialog.cancel();
                        break;
                    case R.id.tv_ok:
                        dialog.cancel();
                        break;
                    default:
                        break;
                }
            }
        });
        dialog.show();
    }

    /**
     * 显示支付超时Dialog
     */
    private void showOvertimeDialog() {
        int[] listenedItems = {R.id.tv_i_understand};
        CustomDialog dialog = new CustomDialog(getContext(),
                R.layout.dialog_payment_overtime, listenedItems, false, Gravity.CENTER);
        dialog.setOnDialogItemClickListener(new CustomDialog.OnCustomDialogItemClickListener() {

            @Override
            public void OnCustomDialogItemClick(CustomDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.tv_i_understand:
                        dialog.cancel();
                        break;
                    default:
                        break;
                }
            }
        });
        dialog.show();
    }

    /**
     * 显示支付超时Dialog
     */
    private void showUnfinishDialog() {
        int[] listenedItems = {R.id.tv_i_understand};
        CustomDialog dialog = new CustomDialog(getContext(),
                R.layout.dialog_payment_fail, listenedItems, false, Gravity.CENTER);
        dialog.setOnDialogItemClickListener(new CustomDialog.OnCustomDialogItemClickListener() {

            @Override
            public void OnCustomDialogItemClick(CustomDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.tv_i_understand:
                        dialog.cancel();
                        break;
                    default:
                        break;
                }
            }
        });
        dialog.show();
    }

    /**
     * 显示支付成功Dialog
     */
    

}
