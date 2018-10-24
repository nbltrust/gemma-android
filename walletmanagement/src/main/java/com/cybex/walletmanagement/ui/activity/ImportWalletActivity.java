package com.cybex.walletmanagement.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.cybex.base.view.tablayout.SlidingTabLayout;
import com.cybex.componentservice.manager.PermissionManager;
import com.cybex.componentservice.manager.TabLayoutManager;
import com.cybex.componentservice.utils.listener.PermissionResultListener;
import com.cybex.walletmanagement.R;
import com.cybex.walletmanagement.event.BarcodeScanEvent;
import com.cybex.walletmanagement.event.QrResultEvent;
import com.cybex.walletmanagement.ui.fragment.ImportWalletMnemonicFragment;
import com.cybex.walletmanagement.ui.fragment.ImportWalletPrivateKeyFragment;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import me.framework.fragmentation.anim.DefaultHorizontalAnimator;
import me.framework.fragmentation.anim.FragmentAnimator;

import static com.raizlabs.android.dbflow.config.FlowManager.getContext;

public class ImportWalletActivity extends XActivity {


    ViewPager vpContent;
    SlidingTabLayout mSlidingTab;

    private List<Fragment> listFragment = new ArrayList<>();

    @Override
    public void bindUI(View view) {
        vpContent = findViewById(R.id.vp_content);
        mSlidingTab =  findViewById(R.id.view_sliding_tab);
        setNavibarTitle(getResources().getString(R.string.walletmanage_import_wallet_title), true);

        ImageView mCollectView = (ImageView) mTitleBar.addAction(new TitleBar.ImageAction(R.drawable.walletmanage_ic_notify_scan) {
            @Override
            public void performAction(View view) {
                skipBarcodeScan();
            }
        });
    }


    private void skipBarcodeScan() {
        final PermissionManager manager = PermissionManager.getInstance(this);
        manager.requestPermission(new PermissionResultListener() {
                                      @Override
                                      public void onPermissionGranted() {
                                          startActivity(new Intent(getContext(),BarcodeScanActivity.class));
                                      }

                                      @Override
                                      public void onPermissionDenied(List<String> permissions) {
                                          GemmaToastUtils.showShortToast(getResources().getString(R.string.walletmanage_set_camera_permission));
                                          if (AndPermission.hasAlwaysDeniedPermission(ImportWalletActivity.this, permissions)) {
                                              manager.showSettingDialog(getContext(), permissions);
                                          }

                                      }
                                  }, Permission.CAMERA
                , Permission.READ_EXTERNAL_STORAGE);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        this.setTab();
    }

    private void setTab() {

        vpContent.removeAllViews();
        listFragment.clear();

        listFragment.add(ImportWalletMnemonicFragment.newInstance());
        listFragment.add(ImportWalletPrivateKeyFragment.newInstance());

        TabLayoutManager.getInstance().setSlidingTabData(this, mSlidingTab, vpContent,
                getTitles(), listFragment);
        mSlidingTab.setCurrentTab(0);
    }

    private List<String> getTitles() {
        List<String> list = new ArrayList<>();
        list.add(getResources().getString(R.string.walletmanage_mnemonic));
        list.add(getResources().getString(R.string.walletmanage_privatekey));

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

    @Override
    public boolean useEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveBarcodeMessage(BarcodeScanEvent message) {
        if (!EmptyUtils.isEmpty(message)) {
            boolean isMnemonicType=mSlidingTab.getCurrentTab()==0;
            EventBusProvider.post(new QrResultEvent(isMnemonicType,message.getResult()));
        }
    }
}
