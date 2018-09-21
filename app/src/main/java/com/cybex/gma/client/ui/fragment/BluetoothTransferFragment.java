package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.view.View;

import com.cybex.gma.client.R;
import com.cybex.gma.client.ui.presenter.BluetoothTransferPresenter;
import com.hxlx.core.lib.mvp.lite.XFragment;

/**
 * 蓝牙卡硬件钱包转账
 *
 * Created by wanglin on 2018/9/20.
 */
public class BluetoothTransferFragment extends XFragment<BluetoothTransferPresenter> {

    @Override
    public void bindUI(View rootView) {

    }

    @Override
    public void initData(Bundle savedInstanceState) {

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_bluetooth_transfer;
    }

    @Override
    public BluetoothTransferPresenter newP() {
        return new BluetoothTransferPresenter();
    }
}
