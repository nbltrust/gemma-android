package com.cybex.walletmanagement.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.cybex.walletmanagement.R;
import com.hxlx.core.lib.mvp.lite.XActivity;

import me.framework.fragmentation.anim.DefaultHorizontalAnimator;
import me.framework.fragmentation.anim.FragmentAnimator;

public class MnemonicBackupGuideActivity extends XActivity {

    @Override
    public void bindUI(View view) {
        Bundle  bd = getIntent().getExtras();

//        if (findFragment(BluetoothBackupMneGuideFragment.class) == null) {
//            loadRootFragment(R.id.fl_container_backup_mnemonic_guide, BluetoothBackupMneGuideFragment.newInstance(bd));
//        }

        //让布局向上移来显示软键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
    }

    @Override
    public int getLayoutId() {
        return R.layout.walletmanage_activity_mnemonic_backup_guide;
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
