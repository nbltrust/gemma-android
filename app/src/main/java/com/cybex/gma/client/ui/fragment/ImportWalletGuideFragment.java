package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cybex.gma.client.R;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 导入钱包引导页面
 * 点击开始导入按钮跳转到的第一个界面
 */

public class ImportWalletGuideFragment extends XFragment {

    Unbinder unbinder;
    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.iv_dot_one) ImageView ivDotOne;
    @BindView(R.id.tv_look_around_hint) TextView tvLookAroundHint;
    @BindView(R.id.bt_start_import) Button btStartImport;

    @OnClick(R.id.bt_start_import)
    public void goToInputPriKey(){
        start(ImportWalletInputKeyFragment.newInstance());
    }

    public static ImportWalletGuideFragment newInstance() {
        Bundle args = new Bundle();
        ImportWalletGuideFragment fragment = new ImportWalletGuideFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(ImportWalletGuideFragment.this, rootView);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setNavibarTitle(getResources().getString(R.string.eos_import_wallet), true, true);
    }

    @Override
    protected void setNavibarTitle(String title, boolean isShowBack, boolean isOnBackFinishActivity) {
        super.setNavibarTitle(title, isShowBack, isOnBackFinishActivity);
    }

    @Override
    public int getLayoutId() {
        return R.layout.eos_fragment_import_wallet_guide;
    }

    @Override
    public Object newP() {
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
