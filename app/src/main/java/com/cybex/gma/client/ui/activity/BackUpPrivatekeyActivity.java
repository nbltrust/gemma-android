package com.cybex.gma.client.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.cybex.base.view.tablayout.SlidingTabLayout;
import com.cybex.gma.client.R;
import com.cybex.componentservice.manager.TabLayoutManager;
import com.cybex.gma.client.ui.fragment.BackUpPriKeyFragment;
import com.cybex.gma.client.ui.fragment.BackUpPriKeyQRFragment;
import com.cybex.gma.client.ui.presenter.BackUpPrivateKeyPresenter;
import com.hxlx.core.lib.mvp.lite.XActivity;

import java.util.ArrayList;
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
        setNavibarTitle(getResources().getString(R.string.eos_btn_backup_prikey), true);
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
                getTitles(), listFragment);
        mSlidingTab.setCurrentTab(0);
    }

    private List<String> getTitles() {
        List<String> list = new ArrayList<>();
        list.add(getResources().getString(R.string.eos_tab_title_private_key));
        list.add(getResources().getString(R.string.eos_tab_title_qr_code));

        return list;
    }

    @Override
    public int getLayoutId() {
        return R.layout.eos_activity_backup_private_key;
    }

    @Override
    public BackUpPrivateKeyPresenter newP() {
        return new BackUpPrivateKeyPresenter();
    }


}
