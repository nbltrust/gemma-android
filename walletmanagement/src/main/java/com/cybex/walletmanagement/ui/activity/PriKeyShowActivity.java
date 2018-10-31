package com.cybex.walletmanagement.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;

import com.cybex.base.view.tablayout.SlidingTabLayout;
import com.cybex.componentservice.config.BaseConst;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.componentservice.manager.TabLayoutManager;
import com.cybex.walletmanagement.R;
import com.cybex.walletmanagement.ui.fragment.BackUpPriKeyFragment;
import com.cybex.walletmanagement.ui.fragment.BackUpPriKeyQRFragment;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.siberiadante.customdialoglib.CustomDialog;

import java.util.ArrayList;
import java.util.List;

import me.framework.fragmentation.anim.DefaultHorizontalAnimator;
import me.framework.fragmentation.anim.FragmentAnimator;


public class PriKeyShowActivity extends XActivity {

    ViewPager vpContent;
    SlidingTabLayout mSlidingTab;

    private List<Fragment> listFragment = new ArrayList<>();
    private MultiWalletEntity wallet;
    private String priKey;

    @Override
    public void bindUI(View view) {
        vpContent = findViewById(R.id.vp_content);
        mSlidingTab =  findViewById(R.id.view_sliding_tab);
        setNavibarTitle(getResources().getString(R.string.walletmanage_pri_key_backup_title), true);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        wallet = getIntent().getParcelableExtra(BaseConst.KEY_WALLET_ENTITY);
        priKey = getIntent().getStringExtra(BaseConst.KEY_PRI_KEY);
        this.setTab();
        showAlertDialog();
    }

    private void setTab() {

        vpContent.removeAllViews();
        listFragment.clear();

        listFragment.add(BackUpPriKeyFragment.newInstance(priKey,wallet));
        listFragment.add(BackUpPriKeyQRFragment.newInstance(priKey,wallet));

        TabLayoutManager.getInstance().setSlidingTabData(this, mSlidingTab, vpContent,
                getTitles(), listFragment);
        mSlidingTab.setCurrentTab(0);
    }

    private List<String> getTitles() {
        List<String> list = new ArrayList<>();
        list.add(getResources().getString(R.string.walletmanage_privatekey));
        list.add(getResources().getString(R.string.walletmanage_barcode));
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

    /**
     * 显示请勿截图Dialog
     */
    private void showAlertDialog() {
        int[] listenedItems = {R.id.tv_understand};
        CustomDialog dialog = new CustomDialog(this,
                R.layout.walletmanage_dialog_no_screenshot_mne, listenedItems, false, Gravity.CENTER);
        dialog.setOnDialogItemClickListener(new CustomDialog.OnCustomDialogItemClickListener() {

            @Override
            public void OnCustomDialogItemClick(CustomDialog dialog, View view) {
                if(view.getId()==R.id.tv_understand){
                    dialog.cancel();
                }
            }
        });
        dialog.show();
    }
}
