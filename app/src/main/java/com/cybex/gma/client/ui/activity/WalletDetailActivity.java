package com.cybex.gma.client.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.cybex.gma.client.R;
import com.cybex.gma.client.ui.fragment.WalletDetailFragment;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.hxlx.core.lib.utils.EmptyUtils;

import me.framework.fragmentation.anim.DefaultHorizontalAnimator;
import me.framework.fragmentation.anim.FragmentAnimator;

public class WalletDetailActivity extends XActivity {

    @Override
    public void bindUI(View view) {
        Bundle args = new Bundle();
        if (EmptyUtils.isNotEmpty(getIntent().getExtras()))args = getIntent().getExtras();
        if (findFragment(WalletDetailFragment.class) == null) {
            loadRootFragment(R.id.fl_container_wallet_detail, WalletDetailFragment.newInstance(args));
        }

        //让布局向上移来显示软键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_wallet_detail;
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
