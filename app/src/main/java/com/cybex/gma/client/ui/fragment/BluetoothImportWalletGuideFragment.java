package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.view.View;

import com.cybex.gma.client.R;
import com.hxlx.core.lib.mvp.lite.XFragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 导入蓝牙钱包引导页
 */
public class BluetoothImportWalletGuideFragment extends XFragment {

    Unbinder unbinder;
    public static BluetoothImportWalletGuideFragment newInstance() {
        Bundle args = new Bundle();
        BluetoothImportWalletGuideFragment fragment = new BluetoothImportWalletGuideFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setNavibarTitle(getResources().getString(R.string.import_wallet), true, true);
    }

    @Override
    public int getLayoutId() {
        return R.layout.eos_fragment_bluetooth_import_wallet_guide;
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
