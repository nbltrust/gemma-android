package com.cybex.gma.client.ui.main.activity;

import android.os.Bundle;
import android.view.View;

import com.cybex.gma.client.R;
import com.cybex.gma.client.ui.main.fragment.MainTabFragment;
import com.hxlx.core.lib.mvp.lite.XActivity;

import me.framework.fragmentation.anim.DefaultHorizontalAnimator;
import me.framework.fragmentation.anim.FragmentAnimator;

/**
 * Created by wanglin on 2018/7/5.
 */
public class MainTabActivity extends XActivity {

    @Override
    public void bindUI(View rootView) {

        if (findFragment(MainTabFragment.class) == null) {
            loadRootFragment(R.id.fl_container, MainTabFragment.newInstance());
        }

    }


    @Override
    public void onBackPressedSupport() {
        // 对于 4个类别的主Fragment内的回退back逻辑,已经在其onBackPressedSupport里各自处理了
        super.onBackPressedSupport();
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        // 设置横向(和安卓4.x动画相同)
        return new DefaultHorizontalAnimator();
    }

    @Override
    public void initData(Bundle savedInstanceState) {

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main_tab;
    }

    @Override
    public Object newP() {
        return null;
    }
}
