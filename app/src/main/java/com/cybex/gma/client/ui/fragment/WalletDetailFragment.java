package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.allen.library.SuperTextView;
import com.cybex.gma.client.R;
import com.cybex.gma.client.db.entity.WalletEntity;
import com.cybex.gma.client.event.WalletIDEvent;
import com.cybex.gma.client.manager.UISkipMananger;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;

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
        start(ChangeWalletNameFragment.newInstance());
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


}
