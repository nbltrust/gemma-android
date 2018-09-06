package com.cybex.gma.client.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.cybex.gma.client.R;
import com.cybex.gma.client.ui.fragment.BackUpPriKeyGuideFragment;
import com.hxlx.core.lib.mvp.lite.XActivity;

import me.framework.fragmentation.anim.DefaultHorizontalAnimator;
import me.framework.fragmentation.anim.FragmentAnimator;

/**
 * 备份助记词引导页
 */

public class BackupMneGuideActivity extends XActivity {

    @Override
    public void bindUI(View view) {
        if (findFragment(BackUpPriKeyGuideFragment.class) == null) {
            loadRootFragment(R.id.fl_container_backup_mne_guide, BackUpPriKeyGuideFragment.newInstance());

        }

        //让布局向上移来显示软键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_backup_mne_guide;
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
