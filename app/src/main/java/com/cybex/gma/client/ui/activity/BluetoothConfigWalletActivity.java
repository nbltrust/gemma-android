package com.cybex.gma.client.ui.activity;

import android.os.Bundle;
import android.view.View;

import com.cybex.gma.client.R;
import com.hxlx.core.lib.mvp.lite.XActivity;

public class BluetoothConfigWalletActivity extends XActivity {

    @Override
    public void bindUI(View rootView) {

    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setNavibarTitle(getString(R.string.title_config_wallet), true);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_bluetooth_import_wallet;
    }

    @Override
    public Object newP() {
        return null;
    }
}
