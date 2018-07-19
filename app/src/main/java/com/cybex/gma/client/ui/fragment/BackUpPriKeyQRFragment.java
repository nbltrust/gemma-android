package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.view.View;

import com.cybex.gma.client.R;
import com.hxlx.core.lib.mvp.lite.XFragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BackUpPriKeyQRFragment extends XFragment {

    Unbinder unbinder;
    public static BackUpPriKeyQRFragment newInstance() {
        Bundle args = new Bundle();
        BackUpPriKeyQRFragment fragment = new BackUpPriKeyQRFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(BackUpPriKeyQRFragment.this, rootView);
    }

    @Override
    public void initData(Bundle savedInstanceState) {

    }

    @Override
    protected void setNavibarTitle(String title, boolean isShowBack) {
        super.setNavibarTitle(title, isShowBack);

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_backup_prikey_qr;
    }

    @Override
    public Object newP() {
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
