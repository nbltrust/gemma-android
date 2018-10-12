package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.view.View;

import com.cybex.gma.client.R;
import com.hxlx.core.lib.mvp.lite.XFragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BluetoothManageFPFragment extends XFragment {

    Unbinder unbinder;

    public static BluetoothManageFPFragment newInstance() {
        Bundle args = new Bundle();
        BluetoothManageFPFragment fragment = new BluetoothManageFPFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(BluetoothManageFPFragment.this, rootView);
        setNavibarTitle(getResources().getString(R.string.about), true, true);
    }

    @Override
    public void initData(Bundle savedInstanceState) {


    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_bluetooth_manage_fp;
    }

    @Override
    public Object newP() {
        return null;
    }
}
