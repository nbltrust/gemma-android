package com.cybex.gma.client.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;

import com.cybex.gma.client.R;
import com.cybex.gma.client.ui.adapter.BluetoothScanDeviceListAdapter;
import com.cybex.gma.client.ui.presenter.BluetoothPairPresenter;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.siberiadante.customdialoglib.CustomFullDialog;

import java.util.ArrayList;
import java.util.List;

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
        btnStartScan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_bluetooth_pair;
    }

    @Override
    public BluetoothPairPresenter newP() {
        return new BluetoothPairPresenter();
    }


    private void showDeviceListDialog() {
        int[] listenedItems = {R.id.imv_close};

        CustomFullDialog dialog = new CustomFullDialog(this,
                R.layout.dialog_bluetooth_scan_result, listenedItems, false, Gravity.BOTTOM);

        dialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
            @Override
            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.imv_close:
                        dialog.cancel();
                        break;
                    default:
                        break;
                }
            }
        });
        dialog.show();

        RecyclerView mRecyclerView = dialog.findViewById(R.id.rv_list);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);
        //TODO
        List<String> data = new ArrayList<>();
        BluetoothScanDeviceListAdapter adapter = new BluetoothScanDeviceListAdapter(data);
        mRecyclerView.setAdapter(adapter);

    }


}
