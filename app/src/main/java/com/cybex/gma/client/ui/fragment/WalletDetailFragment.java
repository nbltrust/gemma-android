package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.allen.library.SuperTextView;
import com.cybex.gma.client.R;
import com.cybex.gma.client.db.entity.WalletEntity;
import com.cybex.gma.client.manager.DBManager;
import com.cybex.gma.client.manager.UISkipMananger;
import com.hxlx.core.lib.mvp.lite.XFragment;
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


    @BindView(R.id.layout_wallet_briefInfo) ConstraintLayout layoutWalletBriefInfo;
    @BindView(R.id.superTextView_exportPriKey) SuperTextView superTextViewExportPriKey;
    @BindView(R.id.superTextView_changePass) SuperTextView superTextViewChangePass;
    @BindView(R.id.scroll_wallet_detail) ScrollView scrollViewWalletDetail;
    Unbinder unbinder;
    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.tv_walletName_in_detailPage) TextView tvWalletNameInDetailPage;
    @BindView(R.id.eosAddress_in_detailPage) TextView tvPublicKey;
    @BindView(R.id.iv_arrow_in_detailPage) ImageView ivArrowInDetailPage;

    @OnClick(R.id.layout_wallet_briefInfo)
    public void goChangeWalletName() {
        start(ChangeWalletNameFragment.newInstance());
    }

    public static WalletDetailFragment newInstance(String walletName) {
        Bundle args = new Bundle();
        args.putString("thisWalletName", walletName);
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
    public void initData(Bundle savedInstanceState) {
        setNavibarTitle("管理钱包", true);
        //显示当前钱包名称
        final String curWalletName = getArguments().getString("thisWalletName");
        tvWalletNameInDetailPage.setText(curWalletName);
        //显示当前钱包公钥
        final String pubKey = getPublicKey(curWalletName);
        tvPublicKey.setText(pubKey);

        superTextViewExportPriKey.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                UISkipMananger.launchBakupGuide(getActivity());
            }
        });

        superTextViewChangePass.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                start(ChangePasswordFragment.newInstance());
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

    /**
     * 显示输入密码Dialog
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

                        break;
                    default:
                        break;
                }
            }
        });
        dialog.show();
    }

    /**
     *根据钱包名称去数据库查询公钥
     */

    public String getPublicKey(String walletname){
       WalletEntity curWallet = DBManager.getInstance().getMediaBeanDao().getWalletEntity(walletname);
       return curWallet.getPublicKey();
    }


}
