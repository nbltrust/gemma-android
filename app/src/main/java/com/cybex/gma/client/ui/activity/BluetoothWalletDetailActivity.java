package com.cybex.gma.client.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.allen.library.SuperTextView;
import com.cybex.gma.client.R;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BluetoothWalletDetailActivity extends XActivity {

    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.superTextView_bWallet_name) SuperTextView superTextViewBWalletName;
    @BindView(R.id.tv_publicKey) TextView tvPublicKey;
    @BindView(R.id.superTextView_battery_life) SuperTextView superTextViewBatteryLife;
    @BindView(R.id.bt_to_connect) Button btToConnect;
    @BindView(R.id.bt_cancel_pair) Button btCancelPair;
    @BindView(R.id.bt_format) Button btFormat;

    @Override
    public void bindUI(View rootView) {
        ButterKnife.bind(this);
        setNavibarTitle(getString(R.string.title_wookong_bio), true);
    }

    @Override
    public void initData(Bundle savedInstanceState) {

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_bluetooth_wallet_detail;
    }

    @Override
    public Object newP() {
        return null;
    }

}
