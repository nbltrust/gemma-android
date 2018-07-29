package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.allen.library.SuperTextView;
import com.cybex.gma.client.R;
import com.cybex.gma.client.db.entity.WalletEntity;
import com.cybex.gma.client.event.WalletIDEvent;
import com.cybex.gma.client.manager.DBManager;
import com.cybex.gma.client.manager.LoggerManager;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.JNIUtil;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.siberiadante.customdialoglib.CustomFullDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;


/**
 * 单独钱包详情管理页面
 * 在管理钱包一级界面中点击钱包名称进入的界面
 */
public class WalletDetailFragment extends XFragment {

    private WalletEntity curWallet;
    private Integer currentID;

    @BindView(R.id.layout_wallet_briefInfo) ConstraintLayout layoutWalletBriefInfo;
    @BindView(R.id.superTextView_exportPriKey) SuperTextView superTextViewExportPriKey;
    @BindView(R.id.superTextView_changePass) SuperTextView superTextViewChangePass;
    @BindView(R.id.scroll_wallet_detail) ScrollView scrollViewWalletDetail;
    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.tv_walletName_in_detailPage) TextView tvWalletNameInDetailPage;
    @BindView(R.id.eosAddress_in_detailPage) TextView tvPublicKey;
    @BindView(R.id.iv_arrow_in_detailPage) ImageView ivArrowInDetailPage;
    Unbinder unbinder;

    @OnClick(R.id.layout_wallet_briefInfo)
    public void goChangeWalletName() {
        start(ChangeWalletNameFragment.newInstance(curWallet.getId()));
    }

    public static WalletDetailFragment newInstance(Bundle bundle) {
        Bundle args = new Bundle();
        WalletEntity walletEntity = bundle.getParcelable("curWallet");
        args.putParcelable("thisWallet", walletEntity);
        WalletDetailFragment fragment = new WalletDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
        OverScrollDecoratorHelper.setUpOverScroll(scrollViewWalletDetail);
    }

    @Override
    public boolean useEventBus() {
        return true;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setNavibarTitle("管理钱包", true);
        curWallet = getArguments().getParcelable("thisWallet");
        currentID = curWallet.getId();
        //显示当前钱包名称
        final String walletName = curWallet.getWalletName();
        tvWalletNameInDetailPage.setText(walletName);
        //显示当前钱包公钥
        final String pubKey = curWallet.getPublicKey();
        tvPublicKey.setText(pubKey);
        //导出私钥点击事件
        superTextViewExportPriKey.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                EventBusProvider.post(new WalletIDEvent(curWallet.getId()));
                UISkipMananger.launchBakupGuide(getActivity());
            }
        });
        //修改密码点击事件
        superTextViewChangePass.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                showConfirmAuthoriDialog();

            }
        });

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_wallet_detail;
    }

    @Override
    public Object newP() {
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onStart() {
        super.onStart();
        curWallet = DBManager.getInstance().getMediaBeanDao().getWalletEntityByID(currentID);
        final String walletName = curWallet.getWalletName();
        LoggerManager.d("walletName", walletName);
        tvWalletNameInDetailPage.setText(walletName);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * 显示确认授权dialog
     */
    private void showConfirmAuthoriDialog() {
        int[] listenedItems = {R.id.imc_cancel, R.id.btn_confirm_authorization};
        CustomFullDialog dialog = new CustomFullDialog(getContext(),
                R.layout.dialog_input_transfer_password, listenedItems, false, Gravity.BOTTOM);
        dialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
            @Override
            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.imc_cancel:
                        dialog.cancel();
                        break;
                    case R.id.btn_confirm_authorization:
                        //检查密码是否正确
                        EditText edtPassword = dialog.findViewById(R.id.et_password);
                        final String inputPass = edtPassword.getText().toString().trim();
                        final String cypher = curWallet.getCypher();
                        final String priKey = JNIUtil.get_private_key(cypher, inputPass);
                        final String generatedCypher = JNIUtil.get_cypher(inputPass, priKey);
                        if (cypher.equals(generatedCypher)){
                            //验证通过
                            start(ChangePasswordFragment.newInstance(priKey,currentID));
                            dialog.cancel();
                        }else {
                            GemmaToastUtils.showLongToast("密码错误，请重新输入");
                        }

                        break;
                    default:
                        break;
                }
            }
        });
        dialog.show();
    }
}
