package com.cybex.gma.client.ui.activity;


import android.content.Intent;
import android.os.Bundle;
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
import com.cybex.componentservice.WookongUtils;
import com.cybex.componentservice.config.BaseConst;
import com.cybex.componentservice.config.CacheConstants;
import com.cybex.componentservice.db.entity.EosWalletEntity;
import com.cybex.componentservice.db.entity.EthWalletEntity;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.DeviceOperationManager;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.componentservice.utils.AlertUtil;
import com.cybex.componentservice.utils.bluetooth.BlueToothWrapper;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.adapter.BluetoothScanDeviceListAdapter;
import com.cybex.gma.client.ui.model.vo.BluetoothDeviceVO;
import com.extropies.common.MiddlewareInterface;
import com.github.ybq.android.spinkit.SpinKitView;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.siberiadante.customdialoglib.CustomFullDialog;
import com.tapadoo.alerter.Alert;
import com.tapadoo.alerter.Alerter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.autosize.AutoSize;

/**
 * 蓝牙扫描结果对话框界面
 * <p>
 * Created by wanglin on 2018/9/6.
 */
public class BluetoothScanResultDialogActivity extends AppCompatActivity {


    private static final String DEVICE_PREFIX = "WOOKONG";
    private CustomFullDialog verifyDialog;
    private CustomFullDialog powerAlertDialog;
    private CustomFullDialog pinDialog;
    private CustomFullDialog connectDialog;
    private String deviceName;
    private int status;//设备device状态值 0--未初始化  1-已设置PIN但未完成初始化  2--已经初始化且有配对信息


    private String TAG = this.toString();
    @BindView(R.id.iv_retry)
    ImageView ivRetry;
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
        if (connectDialog != null && connectDialog.isShowing()) { connectDialog.cancel(); }
        if (pinDialog != null && pinDialog.isShowing()) { pinDialog.cancel(); }
        if (verifyDialog != null && verifyDialog.isShowing()) { verifyDialog.cancel(); }

        super.onDestroy();
    }

    private void startScan() {
        DeviceOperationManager.getInstance().scanDevice(TAG, new DeviceOperationManager.ScanDeviceCallback() {
            @Override
            public void onScanStart() {
                viewSpinKit.setVisibility(View.VISIBLE);
                ivRetry.setVisibility(View.GONE);
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
                viewSpinKit.setVisibility(View.GONE);

                if (deviceNameList != null) {
                    ivRetry.setVisibility(View.VISIBLE);
                    if (deviceNameList.size() == 0) {
                        statusView.showEmpty();
                    }
                }
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

        ivRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScan();
            }
        });

        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                ivRetry.setVisibility(View.GONE);
                if (EmptyUtils.isNotEmpty(deviceNameList)) {

                    deviceName = deviceNameList.get(position).deviceName;

                    status = deviceNameList.get(position).status;
                    LoggerManager.d("bluetooth connect status", status);
                    DeviceOperationManager.getInstance()
                            .connectDevice(TAG, deviceName, new DeviceOperationManager.DeviceConnectCallback() {
                                @Override
                                public void onConnectStart() {
                                    viewSpinKit.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onConnectSuccess() {
                                    getDeviceInfo(deviceName);
                                }

                                @Override
                                public void onConnectFailed() {
                                    LoggerManager.d("connectDevice fail");
                                    viewSpinKit.setVisibility(View.GONE);
                                    showConnectBioFailDialog();

                                }
                            });
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

    int verifyFpCount = 0;

    private void doVerifyFp(int status) {

        DeviceOperationManager.getInstance().startVerifyFP(TAG, deviceName,
                new DeviceOperationManager.DeviceVerifyFPCallback() {
                    @Override
                    public void onVerifyStart() {
                        viewSpinKit.setVisibility(View.GONE);
                        showVerifyFPDialog();
                        verifyFpCount++;
                    }

                    @Override
                    public void onVerifySuccess() {
                        LoggerManager.d("onVerifySuccess ");
                        //1-已设置PIN但未完成初始化  2--已经初始化且有配对信息
                        if (status == 1) {
                            verifyDialog.cancel();
                            //todo 验证电量
                            WookongUtils.validatePowerLevel(ParamConstants.POWER_LEVEL_ALERT_INIT,
                                    new WookongUtils.ValidatePowerLevelCallback() {
                                        @Override
                                        public void onValidateSuccess() {
                                            showInitWookongBioDialog();
                                        }

                                        @Override
                                        public void onValidateFail() {
                                            showpPowerLevelAlertDialog();
                                        }
                                    });

//                            int powerLevel = DeviceOperationManager.getInstance().getDevicePowerAmount(deviceName);

//                            Bundle bundle = new Bundle();
//                            bundle.putBoolean(BaseConst.PIN_STATUS, true);
//                            UISkipMananger.skipBluetoothConfigWookongBioActivity
//                                    (BluetoothScanResultDialogActivity.this,bundle);
//                            finish();

                        } else if (status == 2) {

                            verifyDialog.cancel();

                            //create wookong wallet firstly
                            createWookongWallet();

                            UISkipMananger.launchHome(BluetoothScanResultDialogActivity.this);
                            finish();
                        }
                    }

                    @Override
                    public void onVerifyFailed() {
                        AlertUtil.showShortUrgeAlert(BluetoothScanResultDialogActivity.this, getString(R.string.tip_fp_verify_fail));
                        LoggerManager.d("onVerifyFailed   verifyFpCount=" + verifyFpCount);
                        if (verifyFpCount < 4) {
                            doVerifyFp(status);
                        } else {
                            DeviceOperationManager.getInstance().abortEnrollFp(deviceName);
                            if (verifyDialog != null) {
                                verifyDialog.cancel();
                            }
                            showConfirmAuthoriDialog(
                                    status == 1 ? BaseConst.STATE_SET_PIN_NOT_INIT : BaseConst.STATE_INIT_DONE);
                        }
                    }
                });
    }


    private void createWookongWallet() {
        //获取蓝牙卡中的eos eth公钥后,在本地创建wallet,最后跳转到首页

        Observable<String> observable1 = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {

                String currentDeviceName = DeviceOperationManager.getInstance().getCurrentDeviceName();

                DeviceOperationManager.getInstance()
                        .getEthAddress(BluetoothScanResultDialogActivity.this.toString(), currentDeviceName,
                                new DeviceOperationManager.GetAddressCallback() {
                                    @Override
                                    public void onGetSuccess(String address) {
                                        emitter.onNext(address);
                                        emitter.onComplete();
                                    }

                                    @Override
                                    public void onGetFail() {
                                        emitter.onError(new Exception("getEthAddress fail"));
                                    }
                                });
            }
        }).subscribeOn(Schedulers.io());

        Observable<String> observable2 = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                String currentDeviceName = DeviceOperationManager.getInstance().getCurrentDeviceName();
                DeviceOperationManager.getInstance()
                        .getEosAddress(BluetoothScanResultDialogActivity.this.toString(), currentDeviceName,
                                new DeviceOperationManager.GetAddressCallback() {
                                    @Override
                                    public void onGetSuccess(String address) {
                                        emitter.onNext(address);
                                        emitter.onComplete();
                                    }

                                    @Override
                                    public void onGetFail() {
                                        emitter.onError(new Exception("getEosAddress fail"));
                                    }
                                });
            }
        }).subscribeOn(Schedulers.io());
        Disposable subscribe = Observable.zip(observable1, observable2, new BiFunction<String, String, String>() {
            @Override
            public String apply(String ethAddress, String eosAddress) throws Exception {
                return ethAddress + "&&&&&&" + eosAddress;
            }
        }).map(new Function<String, String>() {
            @Override
            public String apply(String addresses) throws Exception {
                String[] addressArray = addresses.split("&&&&&&");
                MultiWalletEntity multiWalletEntity = new MultiWalletEntity();

                multiWalletEntity.setWalletName("WOOKONG Bio");
                multiWalletEntity.setWalletType(MultiWalletEntity.WALLET_TYPE_HARDWARE);
                multiWalletEntity.setIsBackUp(CacheConstants.ALREADY_BACKUP);
                multiWalletEntity.setPasswordTip(DeviceOperationManager.getInstance().getCurrentDeviceInitPswHint());
                multiWalletEntity.setIsCurrentWallet(1);
                multiWalletEntity.setBluetoothDeviceName(DeviceOperationManager.getInstance().getCurrentDeviceName());


                EthWalletEntity ethWalletEntity = new EthWalletEntity();
                ethWalletEntity.setAddress(addressArray[0]);
//                ethWalletEntity.setPublicKey(publicKey);
                ethWalletEntity.setBackUp(true);
                ethWalletEntity.setMultiWalletEntity(multiWalletEntity);

                List<EthWalletEntity> ethWalletEntities = new ArrayList<>();
                ethWalletEntities.add(ethWalletEntity);
                multiWalletEntity.setEthWalletEntities(ethWalletEntities);

                String eosPublic = addressArray[1];
                LoggerManager.d(" eosPublic=" + eosPublic);
                EosWalletEntity eosWalletEntity = new EosWalletEntity();
//                eosWalletEntity.setPrivateKey(Seed39.keyEncrypt(password, eosPriv));
                eosWalletEntity.setPublicKey(eosPublic);
                eosWalletEntity.setIsBackUp(1);
                eosWalletEntity.setMultiWalletEntity(multiWalletEntity);
                List<EosWalletEntity> eosWalletEntities = new ArrayList<>();
                eosWalletEntities.add(eosWalletEntity);
                multiWalletEntity.setEosWalletEntities(eosWalletEntities);

                List<MultiWalletEntity> multiWalletEntityList = DBManager.getInstance()
                        .getMultiWalletEntityDao()
                        .getMultiWalletEntityList();
                int walletCount = multiWalletEntityList.size();
                if (walletCount > 0) {
                    MultiWalletEntity currentMultiWalletEntity = DBManager.getInstance()
                            .getMultiWalletEntityDao()
                            .getCurrentMultiWalletEntity();
                    if (currentMultiWalletEntity != null) {
                        currentMultiWalletEntity.setIsCurrentWallet(CacheConstants.NOT_CURRENT_WALLET);
                        currentMultiWalletEntity.save();
                    }
                }

                if (ethWalletEntities != null) {
                    for (EthWalletEntity ethWallet : ethWalletEntities) {
                        ethWallet.save();
                    }
                }
                if (eosWalletEntities != null) {
                    for (EosWalletEntity eosWallet : eosWalletEntities) {
                        eosWallet.save();
                    }
                }
                multiWalletEntity.save();

                return "";
            }
        })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String addresses) throws Exception {
                        UISkipMananger.launchHome(BluetoothScanResultDialogActivity.this);
                        finish();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        GemmaToastUtils.showLongToast(getString(R.string.wookong_init_fail));


                    }
                });


    }

    private void getDeviceInfo(String deviceName) {

        DeviceOperationManager.getInstance()
                .getDeviceInfo(TAG, deviceName, new DeviceOperationManager.GetDeviceInfoCallback() {
                    @Override
                    public void onGetSuccess(MiddlewareInterface.PAEW_DevInfo deviceInfo) {
                        //viewSpinKit.setVisibility(View.GONE);
                        //更新当前设备状态
                        LoggerManager.d("deviceInfo life cycle", deviceInfo.ucLifeCycle);
                        LoggerManager.d("deviceInfo pin state", deviceInfo.ucPINState);

                        if (deviceInfo.ucLifeCycle == BaseConst.DEVICE_LIFE_CYCLE_PRODUCE
                                && deviceInfo.ucPINState == BaseConst.DEVICE_PIN_STATE_UNSET) {
                            //未初始化
                            LoggerManager.d("not init");
                            UISkipMananger.skipBluetoothConfigWookongBioActivity(BluetoothScanResultDialogActivity.this,
                                    null);
                            finish();
                        } else if (deviceInfo.ucLifeCycle == BaseConst.DEVICE_LIFE_CYCLE_PRODUCE
                                && deviceInfo.ucPINState != BaseConst.DEVICE_PIN_STATE_INVALID) {
                            //已设置PIN但未完成初始化
                            LoggerManager.d("not pair");
                            //是否设置指纹
                            DeviceOperationManager.getInstance().getFPList(TAG, deviceName,
                                    new DeviceOperationManager.GetFPListCallback() {
                                        @Override
                                        public void onSuccess(BlueToothWrapper.GetFPListReturnValue fpListReturnValue) {
                                            if (fpListReturnValue.getFPCount() > 0) {
                                                //已设置了指纹
                                                //验证指纹以确认配对

//                                            DeviceOperationManager.getInstance().startVerifyFP(TAG, deviceName,
//                                                    new DeviceOperationManager.DeviceVerifyFPCallback() {
//                                                        @Override
//                                                        public void onVerifyStart() {
//                                                            showVerifyFPDialog();
//                                                        }
//
//                                                        @Override
//                                                        public void onVerifySuccess() {
//                                                            dialog.cancel();
//                                                            //todo 验证电量
//
//                                                            int powerLevel = DeviceOperationManager.getInstance().getDevicePowerAmount(deviceName);
//
//                                                            Bundle bundle = new Bundle();
//                                                            bundle.putBoolean(BaseConst.PIN_STATUS, true);
//                                                            UISkipMananger.skipBluetoothConfigWookongBioActivity
//                                                                    (BluetoothScanResultDialogActivity.this,bundle);
//                                                            finish();
//                                                        }
//
//                                                        @Override
//                                                        public void onVerifyFailed() {
//
//                                                        }
//                                                    });
                                                verifyFpCount = 0;
                                                doVerifyFp(status);

                                            } else {
                                                //未设置指纹
                                                //验证PIN以确认配对
                                                showConfirmAuthoriDialog(BaseConst.STATE_SET_PIN_NOT_INIT);
                                            }
                                        }

                                        @Override
                                        public void onFail() {
                                            AlertUtil.showLongUrgeAlert(BluetoothScanResultDialogActivity.this, "get fp"
                                                    + " list failed");
                                            viewSpinKit.setVisibility(View.GONE);
                                        }
                                    });
                        } else if (deviceInfo.ucLifeCycle == BaseConst.DEVICE_LIFE_CYCLE_USER
                                && deviceInfo.ucPINState != BaseConst.DEVICE_PIN_STATE_UNSET
                                && deviceInfo.ucPINState != BaseConst.DEVICE_PIN_STATE_INVALID) {
                            //已初始化
                            //在InitPIN之后，LifeCycle变为User
                            DeviceOperationManager.getInstance().getFPList(TAG, deviceName,
                                    new DeviceOperationManager.GetFPListCallback() {
                                        @Override
                                        public void onSuccess(BlueToothWrapper.GetFPListReturnValue fpListReturnValue) {
                                            if (fpListReturnValue.getFPCount() > 0) {
                                                //已设置了指纹
                                                //验证指纹以确认配对

                                                verifyFpCount = 0;
                                                doVerifyFp(status);

                                            } else {
                                                //未设置指纹
                                                //验证PIN以确认配对
                                                viewSpinKit.setVisibility(View.GONE);
                                                showConfirmAuthoriDialog(BaseConst.STATE_INIT_DONE);
                                            }
                                        }

                                        @Override
                                        public void onFail() {
                                            AlertUtil.showLongUrgeAlert(BluetoothScanResultDialogActivity.this, "get fp"
                                                    + " list failed");
                                            viewSpinKit.setVisibility(View.GONE);
                                        }
                                    });

                        }
                    }

                    @Override
                    public void onGetFail() {
                        viewSpinKit.setVisibility(View.GONE);
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
        connectDialog = new CustomFullDialog(this,
                R.layout.eos_dialog_transfer_bluetooth_connect_failed, listenedItems, false, Gravity.BOTTOM);
        connectDialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
            @Override
            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.imv_back:
                        connectDialog.cancel();
                        finish();
                        break;
                    case R.id.btn_reconnect:
                        //重新连接
                        connectDialog.cancel();
                        startScan();
                        break;
                    default:
                        break;
                }
            }
        });
        connectDialog.show();
    }

    /**
     * 显示通过蓝牙卡验证指纹dialog
     */
    private void showVerifyFPDialog() {
        if (verifyDialog != null) { return; }
        int[] listenedItems = {R.id.imv_back};
        verifyDialog = new CustomFullDialog(this,
                R.layout.eos_dialog_transfer_bluetooth_finger_sure, listenedItems, false, Gravity.BOTTOM);
        verifyDialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
            @Override
            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.imv_back:
                        verifyDialog.cancel();
                        break;
                    default:
                        break;
                }
            }
        });
        verifyDialog.show();
    }

    /**
     * 显示电量不足dialog
     */
    private void showpPowerLevelAlertDialog() {
        if (powerAlertDialog != null) { return; }
        int[] listenedItems = {R.id.tv_i_understand};
        powerAlertDialog = new CustomFullDialog(this,
                R.layout.dialog_bluetooth_power_level_alert, listenedItems, false, Gravity.BOTTOM);
        powerAlertDialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
            @Override
            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.tv_i_understand:
                        powerAlertDialog.cancel();
                        finish();
                        break;
                    default:
                        break;
                }
            }
        });
        powerAlertDialog.show();
    }

    /**
     * 显示确认授权dialog
     */
    private void showConfirmAuthoriDialog(int state) {
        int[] listenedItems = {R.id.imc_cancel, R.id.btn_confirm_pair};
        pinDialog = new CustomFullDialog(this,
                R.layout.eos_dialog_bluetooth_confirm_pair, listenedItems, false, Gravity.BOTTOM);
        pinDialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
            @Override
            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.imc_cancel:
                        dialog.cancel();
                        DeviceOperationManager.getInstance().freeContext(TAG, deviceName,
                                new DeviceOperationManager.FreeContextCallback() {
                                    @Override
                                    public void onFreeStart() {

                                    }

                                    @Override
                                    public void onFreeSuccess() {

                                    }

                                    @Override
                                    public void onFreeFailed() {

                                    }
                                });
                        break;
                    case R.id.btn_confirm_pair:
                        if (state == BaseConst.STATE_SET_PIN_NOT_INIT) {
                            //已设置PIN但未初始化
                            EditText edt_PIN = dialog.findViewById(R.id.et_password);
                            if (EmptyUtils.isNotEmpty(edt_PIN.getText())) {
                                //输入不为空
                                String pin = edt_PIN.getText().toString().trim();
                                DeviceOperationManager.getInstance().verifyPin(TAG, deviceName, pin,
                                        new DeviceOperationManager.VerifyPinCallback() {
                                            @Override
                                            public void onVerifySuccess() {
                                                //todo 验证电量
//                                                Bundle bundle = new Bundle();
//                                                bundle.putBoolean(BaseConst.PIN_STATUS, true);
//                                                UISkipMananger.skipBluetoothConfigWookongBioActivity
//                                                        (BluetoothScanResultDialogActivity.this, bundle);
//                                                finish();

                                                dialog.cancel();
                                                //todo 验证电量
                                                showInitWookongBioDialog();
                                            }

                                            @Override
                                            public void onVerifyFail() {
                                                GemmaToastUtils.showShortToast(getString(
                                                        R.string.baseservice_pass_validate_ip_wrong_password));

                                            }
                                        });
                            } else {
                                //输入为空
                                GemmaToastUtils.showShortToast(getString(R.string.eos_tip_please_input_pass));
                            }
                        } else if (state == BaseConst.STATE_INIT_DONE) {
                            //已完成初始化
                            EditText edt_PIN = dialog.findViewById(R.id.et_password);
                            if (EmptyUtils.isNotEmpty(edt_PIN.getText())) {
                                //输入不为空
                                String pin = edt_PIN.getText().toString().trim();
                                DeviceOperationManager.getInstance().verifyPin(TAG, deviceName, pin,
                                        new DeviceOperationManager.VerifyPinCallback() {
                                            @Override
                                            public void onVerifySuccess() {
                                                //todo 验证电量
//                                                UISkipMananger.launchHome(BluetoothScanResultDialogActivity.this);
//                                                finish();
                                                dialog.cancel();
                                                createWookongWallet();
                                            }

                                            @Override
                                            public void onVerifyFail() {
                                                GemmaToastUtils.showShortToast(getString(
                                                        R.string.baseservice_pass_validate_ip_wrong_password));
                                            }
                                        });
                            } else {
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
        pinDialog.show();

    }

    /**
     * 显示Wookong bio对话框
     */
    private void showInitWookongBioDialog() {
        int[] listenedItems = {R.id.btn_close, R.id.btn_create_wallet, R.id.btn_import_mne};

        AutoSize.autoConvertDensityOfGlobal(BluetoothScanResultDialogActivity.this);

        CustomFullDialog dialog = new CustomFullDialog(this,
                R.layout.eos_dialog_un_init_wookongbio, listenedItems, false, Gravity.BOTTOM);


        dialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
            @Override
            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.btn_close:
                        dialog.cancel();
//                        WookongBioManager.getInstance().freeContext(contextHandle);
//                        BluetoothConnectKeepJob.removeJob();
//                        finish();
                        break;
                    case R.id.btn_create_wallet:
                        dialog.cancel();
                        UISkipMananger.skipBackupMneGuideActivity(BluetoothScanResultDialogActivity.this,
                                null);
                        finish();
                        break;
                    case R.id.btn_import_mne:
                        dialog.cancel();
                        Intent intent = new Intent(BluetoothScanResultDialogActivity.this,
                                BluetoothImportWalletActivity.class);
                        startActivity(intent);
                        finish();
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
