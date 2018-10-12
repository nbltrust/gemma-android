package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.view.View;

import com.cybex.gma.client.R;
import com.hxlx.core.lib.mvp.lite.XFragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BluetoothChangeFPNameFragment extends XFragment {

    Unbinder unbinder;

    public static BluetoothChangeFPNameFragment newInstance() {
        Bundle args = new Bundle();
        BluetoothChangeFPNameFragment fragment = new BluetoothChangeFPNameFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(BluetoothChangeFPNameFragment.this, rootView);
        setNavibarTitle(getResources().getString(R.string.about), true, true);
    }

    @Override
    public void initData(Bundle savedInstanceState) {


    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_bluetooth_change_fp_name;
    }

    @Override
    public Object newP() {
        return null;
    }
}
