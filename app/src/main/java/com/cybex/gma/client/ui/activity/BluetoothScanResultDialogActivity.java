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
import android.widget.EditText;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.cybex.base.view.statusview.MultipleStatusView;
import com.cybex.componentservice.config.BaseConst;
import com.cybex.componentservice.config.CacheConstants;
import com.cybex.componentservice.db.entity.WalletEntity;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.DeviceOperationManager;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.componentservice.utils.AlertUtil;
import com.cybex.componentservice.utils.AmountUtil;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.event.ContextHandleEvent;
import com.cybex.gma.client.job.BluetoothConnectKeepJob;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.manager.WookongBioManager;
import com.cybex.gma.client.ui.JNIUtil;
import com.cybex.gma.client.ui.adapter.BluetoothScanDeviceListAdapter;
import com.cybex.gma.client.ui.model.vo.BluetoothDeviceVO;
import com.cybex.gma.client.utils.bluetooth.BlueToothWrapper;
import com.extropies.common.MiddlewareInterface;
import com.github.ybq.android.spinkit.SpinKitView;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.SPUtils;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.siberiadante.customdialoglib.CustomFullDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 蓝牙扫描结果对话框界面
 * <p>
 * Created by wanglin on 2018/9/6.
 */
public class BluetoothScanResultDialogActivity extends AppCompatActivity {


    private static final String DEVICE_PREFIX = "WOOKONG";
    private CustomFullDialog dialog;
    private String deviceName;


    private String TAG = this.toString();
    @BindView(R.id.imv_close)
    ImageView imvClose;
    @BindView(R.id.rv_list)
    RecyclerView rvList;
    @BindView(R.id.list_multiple_status_view)
    MultipleStatusView statusView;
    @BindView(R.id.view_spinKit)
    SpinKitView viewSpinKit;
    private BluetoothScanDeviceListAdapter mAdapter;
    private List<BluetoothDeviceVO> deviceNameList = new ArrayList<>();
//    private ScanDeviceHandler mHandler;
    private int updatePosition = 0;
//    private long contextHandle = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.eos_dialog_bluetooth_scan_result);
        getWindow().setGravity(Gravity.BOTTOM);
        ButterKnife.bind(this);

        //调起Activity时执行一次扫描
        this.startScan();
        this.initView();
    }

    @Override
    protected void onDestroy() {
        DeviceOperationManager.getInstance().clearCallback(TAG);
        if (dialog != null && dialog.isShowing())dialog.cancel();

        super.onDestroy();
    }

    private void startScan() {
        DeviceOperationManager.getInstance().scanDevice(TAG, new DeviceOperationManager.ScanDeviceCallback() {
            @Override
            public void onScanStart() {
                viewSpinKit.setVisibility(View.VISIBLE);
                deviceNameList.clear();
                mAdapter.notifyDataSetChanged();
                statusView.showLoading();
            }

            @Override
            public void onScanUpdate(String[] devNames) {
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
                    }
                } else {
                    if (statusView != null) {
                        statusView.showContent();
                    }
                }
            }

            @Override
            public void onScanFinish() {

            }
        });

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
                viewSpinKit.setVisibility(View.GONE);
                if (EmptyUtils.isNotEmpty(deviceNameList)) {
                    deviceName = deviceNameList.get(position).deviceName;
//                    SPUtils.getInstance().put(ParamConstants.DEVICE_NAME, deviceName);

                    updatePosition = position;

                    int status = deviceNameList.get(position).status;
                    LoggerManager.d("bluetooth connect status", status);
                    //设备device状态值 0--未初始化  1-已设置PIN但未完成初始化  2--已经初始化且有配对信息
                    if (status == -1) {
                        DeviceOperationManager.getInstance().connectDevice(TAG,deviceName, new DeviceOperationManager.DeviceConnectCallback() {
                            @Override
                            public void onConnectStart() {

                            }

                            @Override
                            public void onConnectSuccess() {
                                getDeviceInfo(deviceName);
                            }

                            @Override
                            public void onConnectFailed() {
                                LoggerManager.d("connectDevice fail");

                            }
                        });
                        deviceNameList.get(position).isShowProgress = true;
                        mAdapter.notifyDataSetChanged();
                    }else if (status == 0){
                        //未初始化
                        LoggerManager.d("not init");
                        UISkipMananger.skipBluetoothConfigWookongBioActivity(BluetoothScanResultDialogActivity.this,null);
                        finish();
                    }else if (status == 1){
                        //已InitPin但未完成初始化
                        LoggerManager.d("not pair");
                        //是否设置指纹
                        DeviceOperationManager.getInstance().getFPList(TAG, deviceName,
                                new DeviceOperationManager.GetFPListCallback() {
                                    @Override
                                    public void onSuccess(com.cybex.componentservice.utils.bluetooth.BlueToothWrapper.GetFPListReturnValue fpListReturnValue) {
                                        if (fpListReturnValue.getFPCount() > 0){
                                            //已设置了指纹
                                            //验证指纹以确认配对

                                            DeviceOperationManager.getInstance().startVerifyFP(TAG, deviceName,
                                                    new DeviceOperationManager.DeviceVerifyFPCallback() {
                                                        @Override
                                                        public void onVerifyStart() {
                                                            showVerifyFPDialog();
                                                        }

                                                        @Override
                                                        public void onVerifySuccess() {
                                                            dialog.cancel();
                                                            //todo 验证电量

                                                            int powerLevel = DeviceOperationManager.getInstance().getDevicePowerAmount(deviceName);

                                                            Bundle bundle = new Bundle();
                                                            bundle.putBoolean(BaseConst.PIN_STATUS, true);
                                                            UISkipMananger.skipBluetoothConfigWookongBioActivity
                                                                    (BluetoothScanResultDialogActivity.this,bundle);
                                                            finish();
                                                        }

                                                        @Override
                                                        public void onVerifyFailed() {

                                                        }
                                                    });

                                        }else {
                                            //未设置指纹
                                            //验证PIN以确认配对
                                            showConfirmAuthoriDialog(BaseConst.STATE_SET_PIN_NOT_INIT);
                                        }
                                    }

                                    @Override
                                    public void onFail() {
                                        AlertUtil.showLongUrgeAlert(BluetoothScanResultDialogActivity.this, "get fp"
                                                + " list failed");
                                    }
                                });


                    }else if (status == 2){
                        //已初始化
                        //LoggerManager.d("init done");
                        //是否设置指纹
                        DeviceOperationManager.getInstance().getFPList(TAG, deviceName,
                                new DeviceOperationManager.GetFPListCallback() {
                                    @Override
                                    public void onSuccess(com.cybex.componentservice.utils.bluetooth.BlueToothWrapper.GetFPListReturnValue fpListReturnValue) {
                                        if (fpListReturnValue.getFPCount() > 0){
                                            //已设置了指纹
                                            //验证指纹以确认配对

                                            DeviceOperationManager.getInstance().startVerifyFP(TAG, deviceName,
                                                    new DeviceOperationManager.DeviceVerifyFPCallback() {
                                                        @Override
                                                        public void onVerifyStart() {
                                                            showVerifyFPDialog();
                                                        }

                                                        @Override
                                                        public void onVerifySuccess() {
                                                            dialog.cancel();
                                                            //todo 验证电量
                                                            UISkipMananger.launchHome(BluetoothScanResultDialogActivity.this);
                                                            finish();
                                                        }

                                                        @Override
                                                        public void onVerifyFailed() {

                                                        }
                                                    });

                                        }else {
                                            //未设置指纹
                                            //验证PIN以确认配对
                                            showConfirmAuthoriDialog(BaseConst.STATE_INIT_DONE);
                                        }
                                    }

                                    @Override
                                    public void onFail() {
                                        AlertUtil.showLongUrgeAlert(BluetoothScanResultDialogActivity.this, "get fp"
                                                + " list failed");
                                    }
                                });

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

    private void getDeviceInfo(String deviceName) {


        DeviceOperationManager.getInstance().getDeviceInfo(TAG, deviceName, new DeviceOperationManager.GetDeviceInfoCallback() {
            @Override
            public void onGetSuccess(MiddlewareInterface.PAEW_DevInfo deviceInfo) {
                //更新当前设备状态
                LoggerManager.d("deviceInfo life cycle", deviceInfo.ucLifeCycle);
                LoggerManager.d("deviceInfo pin state", deviceInfo.ucPINState);

                if (deviceInfo.ucLifeCycle == BaseConst.DEVICE_LIFE_CYCLE_PRODUCE
                        && deviceInfo.ucPINState == BaseConst.DEVICE_PIN_STATE_UNSET) {
                    //未初始化
                    deviceNameList.get(updatePosition).isShowProgress = false;
                    deviceNameList.get(updatePosition).status = 0;
                    mAdapter.notifyDataSetChanged();

                } else if (deviceInfo.ucLifeCycle == BaseConst.DEVICE_LIFE_CYCLE_PRODUCE
//                        && deviceInfo.ucPINState != BaseConst.DEVICE_PIN_STATE_UNSET
                        && deviceInfo.ucPINState != BaseConst.DEVICE_PIN_STATE_INVALID) {
                    //已设置PIN但未完成初始化
                    deviceNameList.get(updatePosition).isShowProgress = false;
                    deviceNameList.get(updatePosition).status = 1;
                    mAdapter.notifyDataSetChanged();
                }else if (deviceInfo.ucLifeCycle == BaseConst.DEVICE_LIFE_CYCLE_USER
                        && deviceInfo.ucPINState != BaseConst.DEVICE_PIN_STATE_UNSET
                        && deviceInfo.ucPINState != BaseConst.DEVICE_PIN_STATE_INVALID){
                    //已初始化
                    //在InitPIN之后，LifeCycle变为User
                    deviceNameList.get(updatePosition).isShowProgress = false;
                    deviceNameList.get(updatePosition).status = 2;
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onGetFail() {

            }
        });
    }


    /**
     * 显示Wookong bio对话框
     */
//    private void showInitWookongBioDialog() {
//        int[] listenedItems = {R.id.btn_close, R.id.btn_create_wallet, R.id.btn_import_mne};
//
//        CustomFullDialog dialog = new CustomFullDialog(this,
//                R.layout.eos_dialog_un_init_wookongbio, listenedItems, false, Gravity.BOTTOM);
//
//
//        dialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
//            @Override
//            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {
//                switch (view.getId()) {
//                    case R.id.btn_close:
//                        dialog.cancel();
//                        WookongBioManager.getInstance().freeContext(contextHandle);
//                        BluetoothConnectKeepJob.removeJob();
//                        finish();
//                        break;
//                    case R.id.btn_create_wallet:
//                        dialog.cancel();
//
//                        Bundle bd = new Bundle();
//                        bd.putLong(ParamConstants.CONTEXT_HANDLE, contextHandle);
//
//                        UISkipMananger.skipBluetoothConfigWookongBioActivity(BluetoothScanResultDialogActivity.this,
//                                bd);
//                        finish();
//                        break;
//                    case R.id.btn_import_mne:
//                        //dialog.cancel();
//                        break;
//                    default:
//                        break;
//                }
//            }
//        });
//        dialog.show();
//    }

    /**
     * 显示重新连接Wookong bio对话框
     */
    private void showConnectBioFailDialog() {
        int[] listenedItems = {R.id.imv_back, R.id.btn_reconnect};
        dialog = new CustomFullDialog(this,
                R.layout.eos_dialog_transfer_bluetooth_connect_failed, listenedItems, false, Gravity.BOTTOM);
        dialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
            @Override
            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.imv_back:
                        dialog.cancel();
                        finish();
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

    /**
     * 显示通过蓝牙卡验证指纹dialog
     */
    private void showVerifyFPDialog() {
        int[] listenedItems = {R.id.imv_back};
        dialog = new CustomFullDialog(this,
                R.layout.eos_dialog_transfer_bluetooth_finger_sure, listenedItems, false, Gravity.BOTTOM);
        dialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
            @Override
            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.imv_back:
                        dialog.cancel();
                        break;
                    default:
                        break;
                }
            }
        });
        dialog.show();
    }

    /**
     * 显示确认授权dialog
     */
    private void showConfirmAuthoriDialog(int state) {
        int[] listenedItems = {R.id.imc_cancel, R.id.btn_confirm_pair};
        dialog = new CustomFullDialog(this,
                R.layout.eos_dialog_bluetooth_confirm_pair, listenedItems, false, Gravity.BOTTOM);
        dialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
            @Override
            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.imc_cancel:
                        dialog.cancel();
                        break;
                    case R.id.btn_confirm_pair:
                        if (state == BaseConst.STATE_SET_PIN_NOT_INIT){
                            //已设置PIN但未初始化
                            EditText edt_PIN = dialog.findViewById(R.id.et_password);
                            if (EmptyUtils.isNotEmpty(edt_PIN.getText())){
                                //输入不为空
                                String pin = edt_PIN.getText().toString().trim();
                                DeviceOperationManager.getInstance().verifyPin(TAG, deviceName, pin,
                                        new DeviceOperationManager.VerifyPinCallback() {
                                            @Override
                                            public void onVerifySuccess() {
                                                //todo 验证电量
                                                Bundle bundle = new Bundle();
                                                bundle.putBoolean(BaseConst.PIN_STATUS, true);
                                                UISkipMananger.skipBluetoothConfigWookongBioActivity
                                                        (BluetoothScanResultDialogActivity.this, bundle);
                                                finish();
                                            }

                                            @Override
                                            public void onVerifyFail() {

                                            }
                                        });
                            }else {
                                //输入为空
                                GemmaToastUtils.showShortToast(getString(R.string.eos_tip_please_input_pass));
                            }
                        }else if (state == BaseConst.STATE_INIT_DONE){
                            //已完成初始化
                            EditText edt_PIN = dialog.findViewById(R.id.et_password);
                            if (EmptyUtils.isNotEmpty(edt_PIN.getText())){
                                //输入不为空
                                String pin = edt_PIN.getText().toString().trim();
                                DeviceOperationManager.getInstance().verifyPin(TAG, deviceName, pin,
                                        new DeviceOperationManager.VerifyPinCallback() {
                                            @Override
                                            public void onVerifySuccess() {
                                                //todo 验证电量
                                                UISkipMananger.launchHome(BluetoothScanResultDialogActivity.this);
                                                finish();
                                            }

                                            @Override
                                            public void onVerifyFail() {

                                            }
                                        });
                            }else {
                                //输入为空
                                GemmaToastUtils.showShortToast(getString(R.string.eos_tip_please_input_pass));
                            }
                        }


                        break;
                    default:
                        break;
                }
            }
        });
        dialog.show();

    }

//    class ScanDeviceHandler extends Handler {
//
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case BlueToothWrapper.MSG_ENUM_START:
//                    //开始一次新的扫描
//                    viewSpinKit.setVisibility(View.VISIBLE);
//                    deviceNameList.clear();
//                    mAdapter.notifyDataSetChanged();
//                    statusView.showLoading();
//                    break;
//                case BlueToothWrapper.MSG_ENUM_UPDATE:
//                    //更新蓝牙设备列表
//                    String[] devNames = (String[]) msg.obj;
//                    if (EmptyUtils.isNotEmpty(devNames)) {
//                        for (int i = 0; i < devNames.length; i++) {
//                            String deviceName = devNames[i];
//                            if (deviceName.contains(DEVICE_PREFIX)) {
//                                BluetoothDeviceVO vo = new BluetoothDeviceVO();
//                                vo.deviceName = deviceName;
//                                deviceNameList.add(vo);
//                            }
//                        }
//
//                    }
//
//                    if (EmptyUtils.isEmpty(deviceNameList)) {
//                        if (statusView != null) {
//                            showConnectBioFailDialog();
//                        }
//                    } else {
//                        if (statusView != null) {
//                            statusView.showContent();
//                        }
//                    }
//
//                    break;
//                case BlueToothWrapper.MSG_INIT_FINISH:
//                    break;
//                case BlueToothWrapper.MSG_ENUM_FINISH:
//                    viewSpinKit.setVisibility(View.GONE);
//                    //完成列表更新
//                    if (EmptyUtils.isNotEmpty(deviceNameList) && deviceNameList.size() > 0) {
//                        deviceNameList.get(updatePosition).isShowProgress = false;
//                    } else {
//                        if (statusView != null) {
//                            showConnectBioFailDialog();
//                        }
//
//                        if (mHandler != null) {
//                            WookongBioManager.getInstance().freeThread();
//                        }
//
//                    }
//
//                    break;
//                case BlueToothWrapper.MSG_INIT_CONTEXT_START:
//                    break;
//                case BlueToothWrapper.MSG_INIT_CONTEXT_FINISH:
//                    //连接完成
//                    BlueToothWrapper.InitContextReturnValue returnValue = (BlueToothWrapper.InitContextReturnValue) msg.obj;
//                    if ((returnValue != null) && (returnValue.getReturnValue()
//                            == MiddlewareInterface.PAEW_RET_SUCCESS)) {
//                        SPUtils.getInstance()
//                                .put(CacheConstants.BIO_CONNECT_STATUS, CacheConstants.STATUS_BLUETOOTH_CONNCETED);
//                        deviceNameList.get(updatePosition).isShowProgress = false;
//                        mAdapter.notifyDataSetChanged();
//
//                        contextHandle = returnValue.getContextHandle();
//
//                        ContextHandleEvent event = new ContextHandleEvent();
//                        event.setContextHanle(contextHandle);
//                        EventBusProvider.postSticky(event);
//                        //连接完成后开启心跳保持连接
//                        BluetoothConnectKeepJob.startConnectPolling(contextHandle, mHandler, 0);
//
//                        if (mHandler != null) {
//                            WookongBioManager.getInstance().getDeviceInfo(contextHandle, 0);
//                        }
//                    }
//
//                    if (mHandler != null) {
//                        WookongBioManager.getInstance().freeThread();
//                    }
//                    break;
//                case BlueToothWrapper.MSG_GET_DEV_INFO_FINISH:
//                    //获得设备信息
//                    BlueToothWrapper.GetDevInfoReturnValue reValue = (BlueToothWrapper.GetDevInfoReturnValue) msg.obj;
//                    if (reValue.getReturnValue() == MiddlewareInterface.PAEW_RET_SUCCESS) {
//                        MiddlewareInterface.PAEW_DevInfo devInfo = reValue.getDeviceInfo();
//                        if (devInfo.ucLifeCycle == BaseConst.DEVICE_LIFE_CYCLE_PRODUCE) {
//                            //在全新（或已Format）的设备上
//                            deviceNameList.get(updatePosition).isShowProgress = false;
//                            deviceNameList.get(updatePosition).status = 0;
//                            mAdapter.notifyDataSetChanged();
//
//                        } else if (devInfo.ucLifeCycle == BaseConst.DEVICE_LIFE_CYCLE_USER) {
//                            //在InitPIN之后，LifeCycle变为User
//                            deviceNameList.get(updatePosition).isShowProgress = false;
//                            deviceNameList.get(updatePosition).status = 1;
//                            mAdapter.notifyDataSetChanged();
//
//                            WookongBioManager.getInstance().getFPList(contextHandle, 0);
//                        }
//
//                    }
//
//                    break;
//                case BlueToothWrapper.MSG_GET_FP_LIST_FINISH:
//                    BlueToothWrapper.GetFPListReturnValue fpListReturnValue = (BlueToothWrapper.GetFPListReturnValue) msg.obj;
//                    if (fpListReturnValue.getReturnValue() == MiddlewareInterface.PAEW_RET_SUCCESS) {
//                        if (fpListReturnValue.getFPCount() > 0) {
//                            deviceNameList.get(updatePosition).isShowProgress = false;
//                            deviceNameList.get(updatePosition).status = 2;
//                            mAdapter.notifyDataSetChanged();
//                        }
//                    }
//                    break;
//                default:
//                    break;
//            }
//        }
//    }
}
