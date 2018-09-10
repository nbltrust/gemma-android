package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.view.View;

import com.cybex.gma.client.R;
import com.hxlx.core.lib.mvp.lite.XFragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ActivateByRMBFragment extends XFragment {

    Unbinder unbinder;
    public static ActivateByRMBFragment newInstance() {
        Bundle args = new Bundle();
        ActivateByRMBFragment fragment = new ActivateByRMBFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
    }

    @Override
    public void initData(Bundle savedInstanceState) {

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_activate_by_rmb;
    }

    @Override
    public Object newP() {
        return null;
    }
}
