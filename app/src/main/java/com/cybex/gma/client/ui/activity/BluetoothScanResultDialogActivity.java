package com.cybex.gma.client.ui.activity;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.cybex.base.view.statusview.MultipleStatusView;
import com.cybex.gma.client.R;
import com.cybex.gma.client.ui.adapter.BluetoothScanDeviceListAdapter;
import com.cybex.gma.client.ui.model.vo.BluetoothDeviceVO;
import com.cybex.gma.client.utils.bluetooth.BlueToothWrapper;
import com.hxlx.core.lib.utils.EmptyUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 蓝牙扫描结果对话框界面
 *
 * Created by wanglin on 2018/9/6.
 */
public class BluetoothScanResultDialogActivity extends AppCompatActivity {


    @BindView(R.id.imv_close) ImageView imvClose;
    @BindView(R.id.rv_list) RecyclerView rvList;
    @BindView(R.id.list_multiple_status_view) MultipleStatusView statusView;

    private BluetoothScanDeviceListAdapter mAdapter;
    private List<BluetoothDeviceVO> deviceNameList = new ArrayList<>();

    private BlueToothWrapper m_scanThread;
    private ScanDeviceHandler mScanHandler;

    private static final String DEVICE_PREFIX = "WOOKONG";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_bluetooth_scan_result);
        ButterKnife.bind(this);


        mScanHandler = new ScanDeviceHandler();
        this.startScan();

        this.initView();
    }


    private void startScan() {
        if ((m_scanThread == null) || (m_scanThread.getState() == Thread.State.TERMINATED)) {
            m_scanThread = new BlueToothWrapper(mScanHandler);
            m_scanThread.setGetDevListWrapper(this, null);
            m_scanThread.start();

        }
    }


    private void initView() {
        imvClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });


        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvList.setLayoutManager(manager);
        mAdapter = new BluetoothScanDeviceListAdapter(deviceNameList);
        rvList.setAdapter(mAdapter);

        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (EmptyUtils.isNotEmpty(deviceNameList)) {
                    deviceNameList.get(position).isShowProgress = true;
                    mAdapter.notifyDataSetChanged();
                }
            }
        });


        statusView.setOnRetryClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusView.showLoading();
                startScan();

            }
        });

    }

    class ScanDeviceHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BlueToothWrapper.MSG_ENUM_START:
                    deviceNameList.clear();
                    mAdapter.notifyDataSetChanged();
                    statusView.showLoading();
                    break;
                case BlueToothWrapper.MSG_ENUM_UPDATE:
                    String[] devNames = (String[]) msg.obj;
                    if (EmptyUtils.isNotEmpty(devNames)) {
                        for (int i = 0; i < devNames.length; i++) {
                            String deviceName = devNames[i];
                            if (deviceName.contains(DEVICE_PREFIX)) {
                                BluetoothDeviceVO vo = new BluetoothDeviceVO();
                                vo.deviceName = deviceName;
                                deviceNameList.add(vo);
                            }
                        }

                    }

                    if (EmptyUtils.isEmpty(deviceNameList)) {
                        statusView.showEmpty();
                    } else {
                        statusView.showContent();
                    }

                    break;
                case BlueToothWrapper.MSG_INIT_FINISH:
                    break;
                case BlueToothWrapper.MSG_ENUM_FINISH:
                    break;
                case BlueToothWrapper.MSG_INIT_CONTEXT_FINISH:
                    break;
                default:
                    break;
            }
        }
    }


}
