package com.cybex.gma.client.ui.activity;

import android.os.Bundle;
import android.view.View;

import com.cybex.gma.client.R;
import com.cybex.gma.client.ui.fragment.MainTabFragment;
import com.cybex.gma.client.ui.fragment.TransferRecordListFragment;
import com.hxlx.core.lib.mvp.lite.XActivity;

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
            loadRootFragment(R.id.fl_container, MainTabFragment.newInstance());
        }

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_transfer_record;
    }

    @Override
    public Object newP() {
        return null;
    }
}
