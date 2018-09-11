package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.view.View;

import com.cybex.gma.client.R;
import com.hxlx.core.lib.mvp.lite.XFragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BluetoothImportMneFragment extends XFragment {

    Unbinder unbinder;
    public static BluetoothImportMneFragment newInstance() {
        Bundle args = new Bundle();
        BluetoothImportMneFragment fragment = new BluetoothImportMneFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setNavibarTitle(getResources().getString(R.string.import_wallet), true, false);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_bluetooth_import_mne;
    }

    @Override
    public Object newP() {
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
