package com.cybex.gma.client.ui.activity;

import android.os.Bundle;
import android.view.View;

import com.cybex.gma.client.R;
import com.cybex.gma.client.ui.fragment.TransferRecordDetailFragment;
import com.cybex.gma.client.ui.fragment.TransferRecordListFragment;
import com.hxlx.core.lib.mvp.lite.XActivity;

import me.framework.fragmentation.anim.DefaultHorizontalAnimator;
import me.framework.fragmentation.anim.FragmentAnimator;

/**
 * Created by wanglin on 2018/7/11.
 */
public class TransferRecordActivity extends XActivity{

    @Override
    public void bindUI(View rootView) {

    }

    @Override
    public void initData(Bundle savedInstanceState) {

        if (findFragment(TransferRecordListFragment.class) == null) {
            loadRootFragment(R.id.fl_container, TransferRecordDetailFragment.newInstance());
        }

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_transfer_record;
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
