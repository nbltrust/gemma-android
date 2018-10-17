package com.cybex.gma.client.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cybex.gma.client.R;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.presenter.BluetoothPairPresenter;
import com.hxlx.core.lib.mvp.lite.XActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 蓝牙配对窗口
 *
 * Created by wanglin on 2018/9/3.
 */
public class BluetoothPairActivity extends XActivity<BluetoothPairPresenter> {


    @BindView(R.id.btn_start_scan) Button btnStartScan;

    @Override
    public void bindUI(View rootView) {
        ButterKnife.bind(this);
        setNavibarTitle(getString(R.string.title_paire_bluetooth), true);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        //配对点击事件
        btnStartScan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                UISkipMananger.startActivity(BluetoothPairActivity.this, BluetoothScanResultDialogActivity.class);

            }
        });

    }

    @Override
    public int getLayoutId() {
        return R.layout.eos_activity_bluetooth_pair;
    }

    @Override
    public BluetoothPairPresenter newP() {
        return new BluetoothPairPresenter();
    }


}
