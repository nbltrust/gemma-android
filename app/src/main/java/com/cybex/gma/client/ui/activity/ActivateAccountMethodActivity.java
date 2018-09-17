package com.cybex.gma.client.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.cybex.base.view.tablayout.SlidingTabLayout;
import com.cybex.gma.client.R;
import com.cybex.gma.client.manager.TabLayoutManager;
import com.cybex.gma.client.ui.fragment.ActivateByExchangeFragment;
import com.cybex.gma.client.ui.fragment.ActivateByFriendFragment;
import com.cybex.gma.client.ui.fragment.ActivateByInvCodeFragment;
import com.cybex.gma.client.ui.fragment.ActivateByRMBFragment;
import com.cybex.gma.client.ui.fragment.BackUpPriKeyFragment;
import com.cybex.gma.client.ui.fragment.BackUpPriKeyQRFragment;
import com.cybex.gma.client.ui.presenter.BackUpPrivateKeyPresenter;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActivateAccountMethodActivity extends XActivity {


    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.view_sliding_tab) SlidingTabLayout mSlidingTab;
    @BindView(R.id.vp_content) ViewPager vpContent;
    private List<Fragment> listFragment = new ArrayList<>();


    @Override
    public void bindUI(View rootView) {
        ButterKnife.bind(this);
        setNavibarTitle(getResources().getString(R.string.title_choose_activate_method), true);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        this.setTab();

    }

    private void setTab() {

        vpContent.removeAllViews();
        listFragment.clear();

        Bundle bundle = getIntent().getExtras();
        listFragment.add(ActivateByRMBFragment.newInstance(bundle));
        listFragment.add(ActivateByFriendFragment.newInstance());
        listFragment.add(ActivateByExchangeFragment.newInstance());
        listFragment.add(ActivateByInvCodeFragment.newInstance());

        TabLayoutManager.getInstance().setSlidingTabData(this, mSlidingTab, vpContent,
                getTitles(), listFragment);
        mSlidingTab.setCurrentTab(0);
    }

    private List<String> getTitles() {
        List<String> list = new ArrayList<>();
        list.add(getResources().getString(R.string.tab_title_activate_by_RMB));
        list.add(getResources().getString(R.string.tab_title_activate_by_friend));
        list.add(getResources().getString(R.string.tab_title_activate_by_exchanges));
        list.add(getResources().getString(R.string.tab_title_activate_by_invCode));

        return list;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_activate_account_method;
    }

    @Override
    public BackUpPrivateKeyPresenter newP() {
        return new BackUpPrivateKeyPresenter();
    }
}
