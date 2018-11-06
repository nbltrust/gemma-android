package com.cybex.gma.client.ui.activity;

import android.os.Bundle;
import android.view.View;

import com.cybex.gma.client.R;
import com.cybex.gma.client.ui.fragment.TransferRecordDetailFragment;
import com.hxlx.core.lib.mvp.lite.XActivity;

import me.framework.fragmentation.anim.DefaultHorizontalAnimator;
import me.framework.fragmentation.anim.FragmentAnimator;

public class TransferRecordDetailActivity extends XActivity {

    @Override
    public void bindUI(View rootView) {

    }

    @Override
    public void initData(Bundle savedInstanceState) {

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            if (findFragment(TransferRecordDetailFragment.class) == null) {
                loadRootFragment(R.id.fl_container_transfer_detail, TransferRecordDetailFragment.newInstance(bundle));
            }
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.eos_activity_transfer_detail;
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        // 设置横向(和安卓4.x动画相同)
        return new DefaultHorizontalAnimator();
    }

    @Override
    public Object newP() {
        return null;
    }

}
