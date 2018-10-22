package com.cybex.walletmanagement.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.cybex.base.view.tablayout.SlidingTabLayout;
import com.cybex.componentservice.manager.TabLayoutManager;
import com.cybex.walletmanagement.R;
import com.hxlx.core.lib.mvp.lite.XActivity;

import java.util.ArrayList;
import java.util.List;

import me.framework.fragmentation.anim.DefaultHorizontalAnimator;
import me.framework.fragmentation.anim.FragmentAnimator;

public class ImportWalletActivity extends XActivity {


    ViewPager vpContent;
    SlidingTabLayout mSlidingTab;

    private List<Fragment> listFragment = new ArrayList<>();

    @Override
    public void bindUI(View view) {
        vpContent = findViewById(R.id.vp_content);
        mSlidingTab =  findViewById(R.id.view_sliding_tab);
        setNavibarTitle(getResources().getString(R.string.walletmanage_backup_title), true);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        this.setTab();
    }

    private void setTab() {

        vpContent.removeAllViews();
        listFragment.clear();

//        listFragment.add(BackUpPriKeyFragment.newInstance());
//        listFragment.add(BackUpPriKeyQRFragment.newInstance());

        TabLayoutManager.getInstance().setSlidingTabData(this, mSlidingTab, vpContent,
                getTitles(), listFragment);
        mSlidingTab.setCurrentTab(0);
    }

    private List<String> getTitles() {
        List<String> list = new ArrayList<>();
//        list.add(getResources().getString(R.string.walletmanage_mnemonic));
//        list.add(getResources().getString(R.string.walletmanage_privatekey));

        return list;
    }

    @Override
    public int getLayoutId() {
        return R.layout.walletmanage_activity_import_wallet;
    }

    @Override
    public Object newP() {
        return null;
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        // 设置横向(和安卓4.x动画相同)
        return new DefaultHorizontalAnimator();
    }
}
