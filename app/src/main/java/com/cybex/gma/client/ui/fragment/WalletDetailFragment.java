package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.allen.library.SuperTextView;
import com.cybex.gma.client.R;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class WalletDetailFragment extends XFragment {


    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.walletName_in_detailPage) TextView walletNameInDetailPage;
    @BindView(R.id.eosAddress_in_detailPage) TextView eosAddressInDetailPage;
    @BindView(R.id.iv_arrow_in_detailPage) ImageView ivArrowInDetailPage;
    @BindView(R.id.layout_wallet_briefInfo) ConstraintLayout layoutWalletBriefInfo;
    @BindView(R.id.superTextView_exportPriKey) SuperTextView superTextViewExportPriKey;
    @BindView(R.id.superTextView_exportMne) SuperTextView superTextViewExportMne;
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
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setNavibarTitle("管理钱包", true);
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
}
