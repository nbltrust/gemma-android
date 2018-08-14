package com.cybex.gma.client.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.cybex.gma.client.R;
import com.cybex.gma.client.ui.fragment.BuySellRamFragment;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.hxlx.core.lib.utils.EmptyUtils;

import me.framework.fragmentation.anim.DefaultHorizontalAnimator;
import me.framework.fragmentation.anim.FragmentAnimator;

public class BuySellRamActivity extends XActivity {

    @Override
    public void bindUI(View view){
        Bundle bundle = getIntent().getExtras();
        if (findFragment(BuySellRamFragment.class) == null && EmptyUtils.isNotEmpty(bundle)) {
            loadRootFragment(R.id.fl_container_buy_sell_ram, BuySellRamFragment.newInstance(bundle));
        }

        //让布局向上移来显示软键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public void initData(Bundle savedInstanceState){
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_buy_sell_ram;
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
