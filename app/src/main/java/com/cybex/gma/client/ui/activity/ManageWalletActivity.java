package com.cybex.gma.client.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.cybex.gma.client.R;
import com.cybex.gma.client.ui.fragment.MainTabFragment;
import com.cybex.gma.client.ui.fragment.ManageWalletFragment;
import com.hxlx.core.lib.mvp.lite.XActivity;

import me.framework.fragmentation.anim.DefaultHorizontalAnimator;
import me.framework.fragmentation.anim.FragmentAnimator;

public class ManageWalletActivity extends XActivity {

    // 再点一次退出程序时间设置
    private static final long WAIT_TIME = 2000L;
    private long TOUCH_TIME = 0;

    @Override
    public void bindUI(View view){
        if (findFragment(MainTabFragment.class) == null) {
            loadRootFragment(R.id.fl_container_manageWallet, ManageWalletFragment.newInstance());
        }

        //让布局向上移来显示软键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public void initData(Bundle savedInstanceState){

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
    public void onBackPressedSupport() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            pop();
        } else {
            if (System.currentTimeMillis() - TOUCH_TIME < WAIT_TIME) {
                finish();
            } else {
                TOUCH_TIME = System.currentTimeMillis();
                Toast.makeText(this, R.string.press_again_exit, Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        // 设置横向(和安卓4.x动画相同)
        return new DefaultHorizontalAnimator();
    }

}
