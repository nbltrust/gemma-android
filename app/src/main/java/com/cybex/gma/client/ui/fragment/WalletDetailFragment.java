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


    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.walletName_in_detailPage) TextView walletNameInDetailPage;
    @BindView(R.id.eosAddress_in_detailPage) TextView eosAddressInDetailPage;
    @BindView(R.id.iv_arrow_in_detailPage) ImageView ivArrowInDetailPage;
    @BindView(R.id.layout_wallet_briefInfo) ConstraintLayout layoutWalletBriefInfo;
    @BindView(R.id.superTextView_exportPriKey) SuperTextView superTextViewExportPriKey;
    @BindView(R.id.superTextView_exportMne) SuperTextView superTextViewExportMne;
    @BindView(R.id.scroll_wallet_detail) ScrollView scrollViewWalletDetail;
    Unbinder unbinder;



    @OnClick(R.id.layout_wallet_briefInfo)
    public void goChangeWalletName(){
        start(ChangeWalletNameFragment.newInstance());
    }

    public static WalletDetailFragment newInstance() {
        Bundle args = new Bundle();
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
        superTextViewExportPriKey.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                showConfirmAuthoriDialog();
            }
        });

        superTextViewExportMne.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                showAlertDialog();
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

    private void showAlertDialog(){
        int[] listenedItems = {R.id.tv_i_understand};
        CustomFullDialog dialog = new CustomFullDialog(getContext(),
                R.layout.dialog_no_screenshot, listenedItems, false, Gravity.CENTER);
        dialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
            @Override
            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {
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

}
