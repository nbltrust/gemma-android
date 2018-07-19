package com.cybex.gma.client.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.cybex.gma.client.R;
import com.cybex.gma.client.ui.fragment.MainTabFragment;
import com.cybex.gma.client.ui.fragment.ManageWalletFragment;
import com.hxlx.core.lib.mvp.lite.XActivity;

import me.framework.fragmentation.anim.DefaultHorizontalAnimator;
import me.framework.fragmentation.anim.FragmentAnimator;

/**
 * 管理钱包页面的容器Activity
 * 点击钱包主页面右上角的钱包图标跳转进入
 *
 */

public class ManageWalletActivity extends XActivity {

    @Override
    public void bindUI(View view){
        if (findFragment(MainTabFragment.class) == null) {
            loadRootFragment(R.id.fl_container_manageWallet, ManageWalletFragment.newInstance());
        }

        //让布局向上移来显示软键盘
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public void initData(Bundle savedInstanceState){
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_manage_wallet;
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
