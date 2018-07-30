package com.cybex.gma.client.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.cybex.base.view.tablayout.SlidingTabLayout;
import com.cybex.gma.client.R;
import com.cybex.gma.client.manager.TabLayoutManager;
import com.cybex.gma.client.ui.fragment.BackUpPriKeyFragment;
import com.cybex.gma.client.ui.fragment.BackUpPriKeyQRFragment;
import com.cybex.gma.client.ui.presenter.BackUpPrivateKeyPresenter;
import com.hxlx.core.lib.mvp.lite.XActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 备份私钥窗口
 *
 * Created by wanglin on 2018/7/25.
 */
public class BackUpPrivatekeyActivity extends XActivity<BackUpPrivateKeyPresenter> {


    @BindView(R.id.vp_content) ViewPager vpContent;
    @BindView(R.id.view_sliding_tab) SlidingTabLayout mSlidingTab;

    private List<Fragment> listFragment = new ArrayList<>();


    @Override
    public void bindUI(View rootView) {
        ButterKnife.bind(this);

        setNavibarTitle("备份私钥", true);


    }

    @Override
    public void initData(Bundle savedInstanceState) {
        this.setTab();
    }

    private void setTab() {

        vpContent.removeAllViews();
        listFragment.clear();

        listFragment.add(BackUpPriKeyFragment.newInstance());
        listFragment.add(BackUpPriKeyQRFragment.newInstance());

        TabLayoutManager.getInstance().setSlidingTabData(this, mSlidingTab, vpContent,
                getTitles(R.array.arrays_tab_backup_private_key), listFragment);
        mSlidingTab.setCurrentTab(0);
    }

    private List<String> getTitles(int array) {
        return Arrays.asList(getResources().getStringArray(array));
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_backup_private_key;
    }

    @Override
    public BackUpPrivateKeyPresenter newP() {
        return new BackUpPrivateKeyPresenter();
    }


}
