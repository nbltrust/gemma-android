package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.allen.library.SuperTextView;
import com.cybex.componentservice.db.entity.EosWalletEntity;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.db.entity.WalletEntity;
import com.cybex.gma.client.R;
import com.cybex.componentservice.config.CacheConstants;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.event.KeySendEvent;
import com.cybex.gma.client.event.ValidateResultEvent;
import com.cybex.gma.client.event.WXPayStatusEvent;
import com.cybex.gma.client.event.WalletIDEvent;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.model.response.WXPayBillResult;
import com.cybex.gma.client.ui.model.response.WXPayPlaceOrderResult;
import com.cybex.gma.client.ui.presenter.ActivateByRMBPresenter;
import com.cybex.componentservice.utils.AlertUtil;
import com.hxlx.core.lib.common.async.TaskManager;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.common.utils.AppManager;
import com.siberiadante.customdialoglib.CustomDialog;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ActivateByRMBFragment extends XFragment<ActivateByRMBPresenter> {

    Unbinder unbinder;
    IWXAPI iwxapi;
    EosWalletEntity curEosWallet;
    @BindView(R.id.iv_dot_one) ImageView ivDotOne;
    @BindView(R.id.tv_look_around_hint) TextView tvLookAroundHint;
    @BindView(R.id.tv_CPU) TextView tvCPU;
    @BindView(R.id.tv_NET) TextView tvNET;
    @BindView(R.id.tv_RAM) TextView tvRAM;
    @BindView(R.id.tv_rmb_amount) TextView tvRmbAmount;
    @BindView(R.id.bt_wechat_pay) Button btWechatPay;

    public void setNewPrice(String newPrice) {
        this.newPrice = newPrice;
    }

    private String newPrice;
    private String orderId = "";
    private String account_name;
    private String public_key;

    @OnClick(R.id.bt_wechat_pay)
    public void initPayProcess(){
        if (getArguments() != null){
            String publicKey = curEosWallet.getPublicKey();
            if (EmptyUtils.isNotEmpty(publicKey)){
                account_name = getArguments().getString(ParamConstants.EOS_USERNAME);
                public_key = publicKey;
            }

            //String rmbFee = tvRmbAmount.getText().toString().trim();
            LoggerManager.d("newPrice", newPrice);
            getP().getPrepaidInfo(account_name, public_key, newPrice);
        }
    }

    public static ActivateByRMBFragment newInstance(Bundle args) {
        ActivateByRMBFragment fragment = new ActivateByRMBFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * 支付最终状态判断
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void recievePayStatus(WXPayStatusEvent event){
        dissmisProgressDialog();
        if (event != null && !event.isUsed()){
            event.setUsed(true);
            switch (event.getStatus()){
                case ParamConstants.WX_NOTPAY_INIT:
                    //未支付，等待支付
                    showUnfinishDialog();
                    break;
                case ParamConstants.WX_NOTPAY_CLOSED:
                    //订单超时，已关闭
                    showOvertimeDialog();
                    break;
                case ParamConstants.WX_SUCCESS_DONE:
                    //支付完成
                    AlertUtil.showLongCommonAlert(getActivity(), "支付成功");
                    //调真正创建账户接口
                    LoggerManager.d("orderId", orderId);
                    if (getArguments() != null){
                        getP().createAccount(account_name, public_key, orderId);
                    }
                    break;
                case ParamConstants.WX_SUCCESS_TOREFUND:
                    //支付完成但订单金额和现在的创建账户金额已经不符，需要退款
                    showOvertimeDialog();
                    break;
                case ParamConstants.WX_REFUND:
                    //已退款
                    showUnfinishDialog();
                    break;
                case ParamConstants.WX_CLOSED:
                    //订单已关闭
                    showUnfinishDialog();
                    break;
                case ParamConstants.WX_USERPAYING:
                    //正在支付
                    break;
                case ParamConstants.WX_PAYERROR:
                    //支付错误
                    showUnfinishDialog();
                    break;
            }
        }
    }

    /**
     * 时间戳比较验证状态判断
     */
//    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
//    public void onCreateStatusReceieved(ValidateResultEvent event){
//        if (event != null){
//            dissmisProgressDialog();
//            if(event.isSuccess()){//创建成功
//                //跳转到主界面
//                UISkipMananger.launchHome(getActivity());
//                /*
//                if (curEosWallet != null && getArguments() != null){
//                    final String private_key_cypher = curEosWallet.getPrivateKey();
//                    final String private_key = Seed39.keyDecrypt(password, private_key_cypher);
//
//                    KeySendEvent keySendEvent = new KeySendEvent(private_key);
//                    EventBusProvider.postSticky(keySendEvent);
//                    WalletIDEvent walletIDEvent = new WalletIDEvent(curEosWallet.getId());
//                    EventBusProvider.postSticky(walletIDEvent);
//                    UISkipMananger.launchBakupGuide(getActivity());
//                }
//                */
//
//            }else {
//                //创建失败,弹框，删除当前钱包，判断是否还有钱包，跳转
//
//                List<MultiWalletEntity> walletEntityList = DBManager.getInstance().getMultiWalletEntityDao()
//                        .getMultiWalletEntityList();
//                showFailDialog(EmptyUtils.isEmpty(walletEntityList));
//
//                /*
//                WalletEntity curWallet = DBManager.getInstance().getWalletEntityDao().getCurrentWalletEntity();
//                DBManager.getInstance().getWalletEntityDao().deleteEntity(curWallet);
//                List<WalletEntity> walletEntityList = DBManager.getInstance().getWalletEntityDao()
//                        .getWalletEntityList();
//                showFailDialog(EmptyUtils.isEmpty(walletEntityList));
//                */
//            }
//
//        }
//    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        getP().getRMBFeeAmount();
        if (getActivity() != null){
            AlertUtil.showShortCommonAlert(getActivity(), getActivity().getString(R.string.eos_tip_username_valid));
        }
        //注册微信支付
        iwxapi = WXAPIFactory.createWXAPI(getActivity(), null);
        iwxapi.registerApp(ParamConstants.WXPAY_APPID);

        curEosWallet = getCurEosWallet();

    }

    @Override
    public int getLayoutId() {
        return R.layout.eos_fragment_activate_by_rmb;
    }

    @Override
    public ActivateByRMBPresenter newP() {
        return new ActivateByRMBPresenter();
    }

    public void callWXPay(WXPayPlaceOrderResult.ResultBean result){
        TaskManager.execute(new Runnable() {
            @Override
            public void run() {
                PayReq req = new PayReq();
                req.appId =ParamConstants.WXPAY_APPID;
                req.partnerId = ParamConstants.WXPAY_PARTNER_ID;
                req.prepayId = result.getPrepayid();
                req.packageValue = ParamConstants.WXPAY_PACKAGE_VALUE;
                req.nonceStr = result.getNonceStr();
                req.timeStamp = String.valueOf(result.getTimestamp());
                req.sign = result.getSign();
                iwxapi.sendReq(req);
            }
        });
    }

    public void setFee(WXPayBillResult.ResultBean result){
        tvRmbAmount.setText(String.format(getString(R.string.eos_rmb_fee), result.getRmbPrice()));
        newPrice = result.getRmbPrice();
        tvCPU.setText(result.getCpu());
        tvNET.setText(result.getNet());
        tvRAM.setText(result.getRam());
    }

    @Override
    public boolean useEventBus() {
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * 显示价格变动Dialog
     */
    public void showPriceChangedDialog() {
        int[] listenedItems = {R.id.tv_cancel, R.id.tv_ok};
        CustomDialog dialog = new CustomDialog(getContext(),
                R.layout.eos_dialog_payment_price_changed, listenedItems, false, Gravity.CENTER);
        dialog.setOnDialogItemClickListener(new CustomDialog.OnCustomDialogItemClickListener() {

            @Override
            public void OnCustomDialogItemClick(CustomDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.tv_cancel:
                        dialog.cancel();
                        break;
                    case R.id.tv_ok:
                        //确认支付
                        initPayProcess();
                        dialog.cancel();
                        break;
                    default:
                        break;
                }
            }
        });
        dialog.show();
        TextView tv_price_changed = dialog.findViewById(R.id.tv_price_changed);
        tv_price_changed.setText(String.format(getString(R.string.eos_payment_price_changed), newPrice));
    }

    /**
     * 显示支付超时Dialog
     */
    private void showOvertimeDialog() {
        int[] listenedItems = {R.id.tv_i_understand};
        CustomDialog dialog = new CustomDialog(getContext(),
                R.layout.eos_dialog_payment_overtime, listenedItems, false, Gravity.CENTER);
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
     * 显示支付未完成Dialog
     */
    private void showUnfinishDialog() {
        int[] listenedItems = {R.id.tv_i_understand};
        CustomDialog dialog = new CustomDialog(getContext(),
                R.layout.eos_dialog_payment_fail, listenedItems, false, Gravity.CENTER);
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
     * 显示创建失败Dialog
     */
    private void showFailDialog(boolean isWalletListEmpty) {
        int[] listenedItems = {R.id.tv_i_understand};
        CustomDialog dialog = new CustomDialog(getContext(),
                R.layout.eos_dialog_create_fail, listenedItems, false, Gravity.CENTER);
        dialog.setOnDialogItemClickListener(new CustomDialog.OnCustomDialogItemClickListener() {

            @Override
            public void OnCustomDialogItemClick(CustomDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.tv_i_understand:
                        if (isWalletListEmpty){
                            //如果没有钱包了
                            UISkipMananger.launchGuide(getActivity());
                        }else {
                            //如果有钱包
                            //更新当前钱包为最后一个钱包，跳转主页面
                            List<MultiWalletEntity> walletList = DBManager.getInstance().getMultiWalletEntityDao()
                                    .getMultiWalletEntityList();
                            MultiWalletEntity newCurWallet = walletList.get(walletList.size() - 1);
                            newCurWallet.setIsCurrentWallet(CacheConstants.IS_CURRENT_WALLET);
                            DBManager.getInstance().getMultiWalletEntityDao().saveOrUpateEntitySync(newCurWallet);
                            AppManager.getAppManager().finishAllActivity();
                            UISkipMananger.launchHomeSingle(getActivity());
                        }

                        dialog.cancel();
                        break;
                    default:
                        break;
                }
            }
        });
        dialog.show();
    }


    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }


    /**
     * 获取当前EOS钱包
     * @return
     */
    private EosWalletEntity getCurEosWallet(){
        EosWalletEntity curEosWallet = new EosWalletEntity();
        MultiWalletEntity multiWalletEntity = DBManager.getInstance().getMultiWalletEntityDao()
                .getCurrentMultiWalletEntity();

        if (multiWalletEntity != null){
            curEosWallet = multiWalletEntity.getEosWalletEntities().get(0);
        }

        return curEosWallet;
    }

}
