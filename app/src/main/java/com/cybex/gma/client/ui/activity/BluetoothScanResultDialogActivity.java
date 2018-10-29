package com.cybex.gma.client.ui.activity;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.cybex.base.view.statusview.MultipleStatusView;
import com.cybex.componentservice.config.CacheConstants;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.gma.client.GmaApplication;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.event.ContextHandleEvent;
import com.cybex.gma.client.job.BluetoothConnectKeepJob;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.manager.WookongBioManager;
import com.cybex.gma.client.ui.adapter.BluetoothScanDeviceListAdapter;
import com.cybex.gma.client.ui.model.vo.BluetoothDeviceVO;
import com.cybex.gma.client.utils.bluetooth.BlueToothWrapper;
import com.extropies.common.MiddlewareInterface;
import com.github.ybq.android.spinkit.SpinKitView;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.SPUtils;
import com.siberiadante.customdialoglib.CustomFullDialog;

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


    private static final String DEVICE_PREFIX = "WOOKONG";
    private static final int DEVICE_LIFE_CYCLE_PRODUCE = 2;//produce
    private static final int DEVICE_LIFE_CYCLE_USER = 4;//user
    @BindView(R.id.imv_close) ImageView imvClose;
    @BindView(R.id.rv_list) RecyclerView rvList;
    @BindView(R.id.list_multiple_status_view) MultipleStatusView statusView;
    @BindView(R.id.view_spinKit) SpinKitView viewSpinKit;
    private BluetoothScanDeviceListAdapter mAdapter;
    private List<BluetoothDeviceVO> deviceNameList = new ArrayList<>();
    private BlueToothWrapper mScanThread;
    private ScanDeviceHandler mHandler;
    private BlueToothWrapper connectThread;
    private BlueToothWrapper getDeviceInfoThread;
    private BlueToothWrapper getFPListThread;
    private int updatePosition = 0;
    private long contextHandle = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.eos_dialog_bluetooth_scan_result);
        getWindow().setGravity(Gravity.BOTTOM);
        ButterKnife.bind(this);

        mHandler = new ScanDeviceHandler();
        WookongBioManager.getInstance().init(mHandler);
        //调起Activity时执行一次扫描
        this.startScan();
        this.initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        WookongBioManager.getInstance().disconnect(contextHandle);

        if (mHandler != null){
            WookongBioManager.getInstance().freeThread();
            WookongBioManager.getInstance().freeResource();
            mHandler = null;
        }
    }

    private void startScan() {


//        if ((mScanThread == null) || (mScanThread.getState() == Thread.State.TERMINATED)) {
//            mScanThread = new BlueToothWrapper(mHandler);
//            mScanThread.setGetDevListWrapper(this, null);
//            mScanThread.start();
//
//        }

        WookongBioManager.getInstance().startScan(this, DEVICE_PREFIX);

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
                    String deviceName = deviceNameList.get(position).deviceName;
                    SPUtils.getInstance().put(ParamConstants.DEVICE_NAME, deviceName);

                    updatePosition = position;

                    int status = deviceNameList.get(position).status;
                    LoggerManager.d(status);
                    if (status == -1) {

                        WookongBioManager.getInstance().startConnect(BluetoothScanResultDialogActivity.this,
                                deviceName);
                        deviceNameList.get(position).isShowProgress = true;
                        mAdapter.notifyDataSetChanged();


//                        if ((connectThread == null) || (connectThread.getState() == Thread.State.TERMINATED)) {
//                            connectThread = new BlueToothWrapper(mHandler);
//                            connectThread.setInitContextWithDevNameWrapper(BluetoothScanResultDialogActivity.this,
//                                    deviceName);
//                            LoggerManager.d("deviceName", deviceName);
//                            connectThread.start();
//
//                            deviceNameList.get(position).isShowProgress = true;
//                            mAdapter.notifyDataSetChanged();
//                        }

                    } else if (status == 0) {
                        //status == 0 未初始化
                        //status == 1 已初始化但没有配对信息
                        showInitWookongBioDialog();
                    }
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

    /**
     * 显示Wookong bio对话框
     */
    private void showInitWookongBioDialog() {
        int[] listenedItems = {R.id.btn_close, R.id.btn_create_wallet, R.id.btn_import_mne};

        CustomFullDialog dialog = new CustomFullDialog(this,
                R.layout.eos_dialog_un_init_wookongbio, listenedItems, false, Gravity.BOTTOM);


        dialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
            @Override
            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.btn_close:
                        dialog.cancel();
                        break;
                    case R.id.btn_create_wallet:
                        dialog.cancel();

                        Bundle bd = new Bundle();
                        bd.putLong(ParamConstants.CONTEXT_HANDLE, contextHandle);

                        UISkipMananger.skipBluetoothConfigWookongBioActivity(BluetoothScanResultDialogActivity.this,
                                bd);
                        //开启连接请求轮询
                        BluetoothConnectKeepJob.startConnectPolling(contextHandle, mHandler, updatePosition);
                        finish();
                        break;
                    case R.id.btn_import_mne:
                        //dialog.cancel();
                        break;
                    default:
                        break;
                }
            }
        });
        dialog.show();
    }

    /**
     * 显示重新连接Wookong bio对话框
     */
    private void showConnectBioFailDialog() {
        int[] listenedItems = {R.id.imv_back, R.id.btn_reconnect};
        CustomFullDialog dialog = new CustomFullDialog(this,
                R.layout.eos_dialog_transfer_bluetooth_connect_failed, listenedItems, false, Gravity.BOTTOM);
        dialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
            @Override
            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.imc_cancel:
                        dialog.cancel();
                        break;
                    case R.id.btn_reconnect:
                        //重新连接
                        dialog.cancel();
                        startScan();
                        break;
                    default:
                        break;
                }
            }
        });
        dialog.show();
    }

    class ScanDeviceHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BlueToothWrapper.MSG_ENUM_START:
                    //开始一次新的扫描
                    LoggerManager.d("MSG_ENUM_START");
                    viewSpinKit.setVisibility(View.VISIBLE);
                    deviceNameList.clear();
                    mAdapter.notifyDataSetChanged();
                    statusView.showLoading();
                    break;
                case BlueToothWrapper.MSG_ENUM_UPDATE:
                    LoggerManager.d("MSG_ENUM_UPDATE");
                    //更新蓝牙设备列表
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
                        if (statusView != null) {
                            showConnectBioFailDialog();
                            //statusView.showEmpty();
                        }
                    } else {
                        if (statusView != null) {
                            statusView.showContent();
                        }
                    }

                    break;
                case BlueToothWrapper.MSG_INIT_FINISH:
                    break;
                case BlueToothWrapper.MSG_ENUM_FINISH:
                    LoggerManager.d("MSG_ENUM_FINISH");
                    viewSpinKit.setVisibility(View.GONE);
                    //完成列表更新
                    if (EmptyUtils.isNotEmpty(deviceNameList) && deviceNameList.size() > 0) {
                        deviceNameList.get(updatePosition).isShowProgress = false;
                    } else {
                        if (statusView != null) {
                            //statusView.showEmpty();
                            showConnectBioFailDialog();
                        }

                        //mScanThread.interrupt();

                        if (mHandler != null){
                            WookongBioManager.getInstance().freeThread();
                        }

                    }

                    break;
                case BlueToothWrapper.MSG_INIT_CONTEXT_START:
                    break;
                case BlueToothWrapper.MSG_INIT_CONTEXT_FINISH:
                    BlueToothWrapper.InitContextReturnValue returnValue = (BlueToothWrapper.InitContextReturnValue) msg.obj;
                    if ((returnValue != null) && (returnValue.getReturnValue()
                            == MiddlewareInterface.PAEW_RET_SUCCESS)) {
                        SPUtils.getInstance()
                                .put(CacheConstants.BIO_CONNECT_STATUS, CacheConstants.STATUS_BLUETOOTH_CONNCETED);
                        deviceNameList.get(updatePosition).isShowProgress = false;
                        mAdapter.notifyDataSetChanged();

                        contextHandle = returnValue.getContextHandle();

                        ContextHandleEvent event = new ContextHandleEvent();
                        event.setContextHanle(contextHandle);
                        EventBusProvider.postSticky(event);


//                        if ((getDeviceInfoThread == null) || (getDeviceInfoThread.getState()
//                                == Thread.State.TERMINATED)) {
//                            getDeviceInfoThread = new BlueToothWrapper(mHandler);
//                            getDeviceInfoThread.setGetInfoWrapper(returnValue.getContextHandle()
//                                    , updatePosition);
//                            getDeviceInfoThread.start();
//
//                        }


                        if (mHandler != null){
                            WookongBioManager.getInstance().getDeviceInfo(contextHandle, 0);
                        }
                    }

                    if (mHandler != null){
                        WookongBioManager.getInstance().freeThread();
                    }
                    //connectThread.interrupt();
                    break;
                case BlueToothWrapper.MSG_GET_DEV_INFO_FINISH:
                    //获得设备信息
                    BlueToothWrapper.GetDevInfoReturnValue reValue = (BlueToothWrapper.GetDevInfoReturnValue) msg.obj;
                    if (reValue.getReturnValue() == MiddlewareInterface.PAEW_RET_SUCCESS) {
                        MiddlewareInterface.PAEW_DevInfo devInfo = reValue.getDeviceInfo();
                        if (devInfo.ucLifeCycle == DEVICE_LIFE_CYCLE_PRODUCE) {
                            //在全新（或已Format）的设备上
                            deviceNameList.get(updatePosition).isShowProgress = false;
                            deviceNameList.get(updatePosition).status = 0;
                            mAdapter.notifyDataSetChanged();

                        } else if (devInfo.ucLifeCycle == DEVICE_LIFE_CYCLE_USER) {
                            //在InitPIN之后，LifeCycle变为User
                            deviceNameList.get(updatePosition).isShowProgress = false;
                            deviceNameList.get(updatePosition).status = 1;
                            mAdapter.notifyDataSetChanged();


//                            if ((getFPListThread == null) || (getFPListThread.getState()
//                                    == Thread.State.TERMINATED)) {
//                                getFPListThread = new BlueToothWrapper(mHandler);
//                                getFPListThread.setGetFPListWrapper(0, updatePosition);
//                                getFPListThread.start();
//                            }

                            WookongBioManager.getInstance().getFPList(contextHandle, 0);
                        }

                    }

                    break;
                case BlueToothWrapper.MSG_GET_FP_LIST_FINISH:
                    BlueToothWrapper.GetFPListReturnValue fpListReturnValue = (BlueToothWrapper.GetFPListReturnValue) msg.obj;
                    if (fpListReturnValue.getReturnValue() == MiddlewareInterface.PAEW_RET_SUCCESS) {
                        if (fpListReturnValue.getFPCount() > 0) {
                            deviceNameList.get(updatePosition).isShowProgress = false;
                            deviceNameList.get(updatePosition).status = 2;
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
