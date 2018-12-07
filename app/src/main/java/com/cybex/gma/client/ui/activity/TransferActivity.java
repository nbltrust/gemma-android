package com.cybex.gma.client.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.cybex.componentservice.utils.SoftHideKeyBoardUtil;
import com.cybex.gma.client.R;
import com.cybex.gma.client.ui.fragment.EosTokenTransferFragment;
import com.hxlx.core.lib.mvp.lite.XActivity;

import me.framework.fragmentation.anim.DefaultHorizontalAnimator;
import me.framework.fragmentation.anim.FragmentAnimator;

public class TransferActivity extends XActivity {

    @Override
    public void bindUI(View view) {
        Bundle bd = getIntent().getExtras();
        if (bd != null){
            if (findFragment(EosTokenTransferFragment.class) == null) {
                loadRootFragment(R.id.fl_container_transfer, EosTokenTransferFragment.newInstance(bd));
            }
        }
//        else {
//            if (findFragment(TransferFragment.class) == null) {
//                loadRootFragment(R.id.fl_container_transfer, TransferFragment.newInstance());
//            }
//        }

        //让布局向上移来显示软键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        SoftHideKeyBoardUtil.assistActivity(TransferActivity.this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.eos_activity_transfer;
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
