package com.cybex.gma.client.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.cybex.base.view.statusview.MultipleStatusView;
import com.cybex.componentservice.WookongUtils;
import com.cybex.componentservice.config.BaseConst;
import com.cybex.componentservice.config.CacheConstants;
import com.cybex.componentservice.config.RouterConst;
import com.cybex.componentservice.db.entity.EosWalletEntity;
import com.cybex.componentservice.db.entity.EthWalletEntity;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.db.util.DBCallback;
import com.cybex.componentservice.event.WookongFormattedEvent;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.DeviceOperationManager;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.componentservice.utils.AlertUtil;
import com.cybex.componentservice.utils.SizeUtil;
import com.cybex.componentservice.utils.bluetooth.BlueToothWrapper;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.activity.BluetoothImportWalletActivity;
import com.cybex.gma.client.ui.adapter.BluetoothScanDeviceListAdapter;
import com.cybex.gma.client.ui.model.vo.BluetoothDeviceVO;
import com.extropies.common.MiddlewareInterface;
import com.github.ybq.android.spinkit.SpinKitView;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.siberiadante.customdialoglib.CustomDialog;
import com.siberiadante.customdialoglib.CustomFullDialog;
import com.cybex.componentservice.widget.CustomFullWithAlertDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.autosize.AutoSize;

public class WookongScanDialog extends Dialog {


    private static final String DEVICE_PREFIX = "WOOKONG";
    private XActivity activity;
    private CustomFullWithAlertDialog verifyDialog;
    private CustomFullDialog powerAlertDialog;
    private CustomFullDialog pinDialog;
//    private CustomFullDialog connectDialog;
    private String deviceName;
    private boolean deviceSelected;//已经选中一个一个设备
    private int status;//设备device状态值 0--未初始化  1-已设置PIN但未完成初始化  2--已经初始化且有配对信息
    private boolean isConnectingDevice;

    private String TAG = this.toString();
    @BindView(com.cybex.gma.client.R.id.iv_retry)
    ImageView ivRetry;
    @BindView(com.cybex.gma.client.R.id.imv_close)
    ImageView imvClose;
    @BindView(com.cybex.gma.client.R.id.rv_list)
    RecyclerView rvList;
    @BindView(com.cybex.gma.client.R.id.list_multiple_status_view)
    MultipleStatusView statusView;
    @BindView(com.cybex.gma.client.R.id.view_spinKit)
    SpinKitView viewSpinKit;
    private BluetoothScanDeviceListAdapter mAdapter;
    private List<BluetoothDeviceVO> deviceNameList = new ArrayList<>();
    //    private ScanDeviceHandler mHandler;
    private int updatePosition = 0;

    public WookongScanDialog(
            XActivity activity,
            boolean cancelAble, boolean canTouchOutsideCancel) {
        super(activity, R.style.Custom_Dialog_Style);
        this.activity=activity;
        setCancelable(cancelAble);
        setCanceledOnTouchOutside(canTouchOutsideCancel);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eos_dialog_bluetooth_scan_result);
        getWindow().setGravity(Gravity.BOTTOM);

        WindowManager windowManager = activity.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = display.getWidth() ;
        getWindow().setAttributes(lp);

        ButterKnife.bind(this);

        //调起Activity时执行一次扫描
        this.initView();
        this.startScan();
    }




    @Override
    public void onDetachedFromWindow() {
        DeviceOperationManager.getInstance().clearCallback(TAG);
//        if (connectDialog != null && connectDialog.isShowing()) {
//            connectDialog.cancel();
//        }
        if (pinDialog != null && pinDialog.isShowing()) {
            pinDialog.dismiss();
        }
        if (verifyDialog != null && verifyDialog.isShowing()) {
            verifyDialog.dismiss();
        }
        super.onDetachedFromWindow();
    }


    private void startScan() {
        if(deviceSelected)return;
        viewSpinKit.setVisibility(View.VISIBLE);
        ivRetry.setVisibility(View.GONE);
        deviceNameList.clear();
        statusView.showLoading();

        DeviceOperationManager.getInstance().scanDevice(TAG, new DeviceOperationManager.ScanDeviceCallback() {
            @Override
            public void onScanStart() {
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
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }

                if (EmptyUtils.isEmpty(deviceNameList)) {
                    if (statusView != null) {
                        statusView.showEmpty();
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

    @Override
    public void onBackPressed() {
        if(isConnectingDevice){
            GemmaToastUtils.showShortToast(activity.getString(R.string.exit_wait_for_connect));
        }else{
            cancel();
        }
//        super.onBackPressed();
    }

    private void initView() {
        AutoSize.autoConvertDensityOfGlobal(activity);
        imvClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(isConnectingDevice){
                    GemmaToastUtils.showShortToast(activity.getString(R.string.exit_wait_for_connect));
                    return;
                }
                cancel();
            }
        });

        setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if(deviceSelected&& !TextUtils.isEmpty(deviceName)&&DeviceOperationManager.getInstance().isDeviceConnectted(deviceName)){
                    freeContext();
                }
            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
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
                if(deviceSelected){
                    return;
                }

//                ivRetry.setVisibility(View.GONE);
                if (EmptyUtils.isNotEmpty(deviceNameList)) {
                    deviceSelected=true;
                    deviceName = deviceNameList.get(position).deviceName;
                    deviceNameList.get(position).isShowProgress=true;
                    status = deviceNameList.get(position).status;
                    mAdapter.notifyDataSetChanged();
                    LoggerManager.d("bluetooth connect status", status);
                    DeviceOperationManager.getInstance()
                            .connectDevice(TAG, deviceName, new DeviceOperationManager.DeviceConnectCallback() {
                                @Override
                                public void onConnectStart() {
//                                    viewSpinKit.setVisibility(View.VISIBLE);
                                    isConnectingDevice=true;
                                }

                                @Override
                                public void onConnectSuccess() {
                                    getDeviceInfo(deviceName);
                                    isConnectingDevice=false;
                                }

                                @Override
                                public void onConnectFailed() {
                                    isConnectingDevice=false;
                                    LoggerManager.d("connectDevice fail");
//                                    viewSpinKit.setVisibility(View.GONE);
//                                    showConnectBioFailDialog();

                                    deviceSelected=false;
                                    revertItemStatus();
                                    mAdapter.notifyDataSetChanged();
                                    GemmaToastUtils.showLongToast(activity.getString(R.string.wookong_connect_fail));
                                }
                            });
                   //mAdapter.notifyDataSetChanged();
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
//                        viewSpinKit.setVisibility(View.GONE);
                        showVerifyFPDialog();
                        verifyFpCount++;
                    }

                    @Override
                    public void onVerifySuccess() {
                        LoggerManager.d("onVerifySuccess ");
                        //1-已设置PIN但未完成初始化  2--已经初始化且有配对信息
                        if (status == 1) {
                            verifyDialog.dismiss();
                            //todo 验证电量
                            WookongUtils.validatePowerLevel(ParamConstants.POWER_LEVEL_ALERT_INIT,
                                    new WookongUtils.ValidatePowerLevelCallback() {
                                        @Override
                                        public void onValidateSuccess() {
                                            showInitWookongBioDialog();
                                        }

                                        @Override
                                        public void onValidateFail() {
                                            freeContext();
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

                            verifyDialog.dismiss();

                            //create wookong wallet firstly
                            createWookongWallet();

//                            UISkipMananger.launchHome(activity);
//                            activity.finish();
                        }
                    }

                    @Override
                    public void onVerifyFailed() {
//                        AlertUtil.showShortUrgeAlert((ViewGroup) verifyDialog.getWindow().getDecorView(), getContext().getString(com.cybex.gma.client.R.string.tip_fp_verify_fail));
                        if (verifyDialog != null) {
                            verifyDialog.showShortUrgeAlert(getContext().getString(com.cybex.gma.client.R.string.tip_fp_verify_fail));
                        }
                        LoggerManager.d("onVerifyFailed   verifyFpCount=" + verifyFpCount);
                        if (verifyFpCount < 4) {
                            doVerifyFp(status);
                        } else {
//                            DeviceOperationManager.getInstance().abortEnrollFp(deviceName);
                            if (verifyDialog != null) {
                                verifyDialog.dismiss();
                            }
                            showConfirmAuthoriDialog(
                                    status == 1 ? BaseConst.STATE_SET_PIN_NOT_INIT : BaseConst.STATE_INIT_DONE);
                        }
                    }

                    @Override
                    public void onVerifyCancelled() {

                    }
                });
    }


    private void createWookongWallet() {
        //获取蓝牙卡中的eos eth公钥后,在本地创建wallet,最后跳转到首页
        activity.showProgressDialog(activity.getString(R.string.creating_wookong_wallet));
        Observable<String> observable1 = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {

                String currentDeviceName = DeviceOperationManager.getInstance().getCurrentDeviceName();

                DeviceOperationManager.getInstance()
                        .getEthAddress(TAG, currentDeviceName,
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
                        .getEosAddress(WookongScanDialog.this.toString(), currentDeviceName,
                                new DeviceOperationManager.GetAddressCallback() {
                                    @Override
                                    public void onGetSuccess(String address) {
                                        String s = address.split("####")[0];
                                        emitter.onNext(s);
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
        })
                .map(new Function<String, String>() {
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
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String addresses) throws Exception {
                        LoggerManager.e("create wookong wallet  success");
                        UISkipMananger.launchHome(activity);
                        activity.finish();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        LoggerManager.e("create wookong wallet fail throwable="+throwable);
                        activity.dissmisProgressDialog();
                        GemmaToastUtils.showLongToast(getContext().getString(com.cybex.gma.client.R.string.wookong_init_fail));
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

                        if (deviceInfo.ucPINState == BaseConst.DEVICE_PIN_STATE_UNSET) {
                            //未初始化
                            LoggerManager.d("not init");
                            status=0;
                            UISkipMananger.skipBluetoothConfigWookongBioActivity(activity,
                                    null);
                            dismiss();
                        } else if ((deviceInfo.ucLifeCycle == BaseConst.DEVICE_LIFE_CYCLE_PRODUCE||deviceInfo.ucLifeCycle == BaseConst.DEVICE_LIFE_CYCLE_AGREE)
                                && deviceInfo.ucPINState != BaseConst.DEVICE_PIN_STATE_INVALID) {
                            //已设置PIN但未完成初始化
                            LoggerManager.d("not pair");
                            status=1;
                            //是否设置指纹
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
                                                showConfirmAuthoriDialog(BaseConst.STATE_SET_PIN_NOT_INIT);
                                            }
                                        }

                                        @Override
                                        public void onFail() {
                                            AlertUtil.showLongUrgeAlert(activity, "get fp"
                                                    + " list failed");
//                                            viewSpinKit.setVisibility(View.GONE);
                                        }
                                    });
                        } else if (deviceInfo.ucLifeCycle == BaseConst.DEVICE_LIFE_CYCLE_USER
                                && deviceInfo.ucPINState != BaseConst.DEVICE_PIN_STATE_UNSET
                                && deviceInfo.ucPINState != BaseConst.DEVICE_PIN_STATE_INVALID) {
                            //已初始化
                            //在InitPIN之后，LifeCycle变为User
                            status=2;
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
//                                                viewSpinKit.setVisibility(View.GONE);
                                                showConfirmAuthoriDialog(BaseConst.STATE_INIT_DONE);
                                            }
                                        }

                                        @Override
                                        public void onFail() {
                                            AlertUtil.showLongUrgeAlert(activity, "get fp"
                                                    + " list failed");
//                                            viewSpinKit.setVisibility(View.GONE);
                                        }
                                    });

                        }
                    }

                    @Override
                    public void onGetFail() {
                        freeContext();
                        GemmaToastUtils.showLongToast(activity.getString(R.string.wookong_connect_fail));
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
//    private void showConnectBioFailDialog() {
//        int[] listenedItems = {R.id.imv_back, R.id.btn_reconnect};
//        connectDialog = new CustomFullDialog(activity,
//                R.layout.eos_dialog_transfer_bluetooth_connect_failed, listenedItems, false, Gravity.BOTTOM);
//        connectDialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
//            @Override
//            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {
//                switch (view.getId()) {
//                    case com.cybex.gma.client.R.id.imv_back:
//                        connectDialog.cancel();
//                        activity.finish();
//                        break;
//                    case com.cybex.gma.client.R.id.btn_reconnect:
//                        //重新连接
//                        connectDialog.cancel();
//                        startScan();
//                        break;
//                    default:
//                        break;
//                }
//            }
//        });
//        connectDialog.show();
//    }

    /**
     * 显示通过蓝牙卡验证指纹dialog
     */
    private void showVerifyFPDialog() {
//        if (verifyDialog != null) {
//            return;
//        }
        if(verifyDialog!=null){
            if(!verifyDialog.isShowing()){
                verifyDialog.show();
            }
            return;
        }
        int[] listenedItems = {R.id.imv_back};
        verifyDialog = new CustomFullWithAlertDialog(activity,
                R.layout.eos_dialog_transfer_bluetooth_finger_sure, listenedItems, false,false, Gravity.BOTTOM);
        verifyDialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                DeviceOperationManager.getInstance().abortEnrollFp("verifyDialog", deviceName, new DeviceOperationManager.AbortFPCallback() {
                    @Override
                    public void onAbortSuccess() {
                        DeviceOperationManager.getInstance().clearCallback("verifyDialog");
                        freeContext();
                    }

                    @Override
                    public void onAbortFail() {
                        DeviceOperationManager.getInstance().clearCallback("verifyDialog");
                        freeContext();
                    }
                });
            }
        });
        verifyDialog.setOnDialogItemClickListener(new CustomFullWithAlertDialog.OnCustomDialogItemClickListener() {
            @Override
            public void OnCustomDialogItemClick(CustomFullWithAlertDialog dialog, View view) {
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
        if (powerAlertDialog != null) {
            return;
        }
        int[] listenedItems = {R.id.tv_i_understand};
        powerAlertDialog = new CustomFullDialog(activity,
                R.layout.dialog_bluetooth_power_level_alert, listenedItems, false, false,Gravity.BOTTOM);
        powerAlertDialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
            @Override
            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {
                switch (view.getId()) {
                    case com.cybex.gma.client.R.id.tv_i_understand:
                        powerAlertDialog.cancel();
//                        activity.finish();
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
        pinDialog = new CustomFullDialog(activity,
                R.layout.eos_dialog_bluetooth_confirm_pair, listenedItems, false,false, Gravity.BOTTOM);

        pinDialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                freeContext();
            }
        });
        pinDialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
            @Override
            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.imc_cancel:
                        dialog.cancel();
                        break;
                    case R.id.btn_confirm_pair:
                        if (state == BaseConst.STATE_SET_PIN_NOT_INIT) {
                            //已设置PIN但未初始化
                            EditText edt_PIN = dialog.findViewById(com.cybex.gma.client.R.id.et_password);
                            if (EmptyUtils.isNotEmpty(edt_PIN.getText())) {
                                //输入不为空
                                String pin = edt_PIN.getText().toString();
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

                                                dialog.dismiss();
                                                //todo 验证电量
                                                showInitWookongBioDialog();
                                            }

                                            @Override
                                            public void onPinLocked() {
                                                showPinLockedDialog();
                                            }

                                            @Override
                                            public void onVerifyFail() {
                                                GemmaToastUtils.showShortToast(getContext().getString(
                                                        com.cybex.gma.client.R.string.baseservice_pass_validate_ip_wrong_password));

                                            }
                                        });
                            } else {
                                //输入为空
                                GemmaToastUtils.showShortToast(getContext().getString(com.cybex.gma.client.R.string.eos_tip_please_input_pass));
                            }
                        } else if (state == BaseConst.STATE_INIT_DONE) {
                            //已完成初始化
                            EditText edt_PIN = dialog.findViewById(com.cybex.gma.client.R.id.et_password);
                            if (EmptyUtils.isNotEmpty(edt_PIN.getText())) {
                                //输入不为空
                                String pin = edt_PIN.getText().toString();
                                DeviceOperationManager.getInstance().verifyPin(TAG, deviceName, pin,
                                        new DeviceOperationManager.VerifyPinCallback() {
                                            @Override
                                            public void onVerifySuccess() {
                                                //todo 验证电量
//                                                UISkipMananger.launchHome(BluetoothScanResultDialogActivity.this);
//                                                finish();
                                                dialog.dismiss();
                                                createWookongWallet();
                                            }

                                            @Override
                                            public void onPinLocked() {
                                                showPinLockedDialog();
                                            }

                                            @Override
                                            public void onVerifyFail() {
                                                GemmaToastUtils.showShortToast(activity.getString(
                                                        com.cybex.gma.client.R.string.baseservice_pass_validate_ip_wrong_password));
                                            }
                                        });
                            } else {
                                //输入为空
                                GemmaToastUtils.showShortToast(activity.getString(com.cybex.gma.client.R.string.eos_tip_please_input_pass));
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




    private void revertItemStatus(){
        for (BluetoothDeviceVO bluetoothDeviceVO : deviceNameList) {
            bluetoothDeviceVO.isShowProgress=false;
        }
    }

    private void freeContext() {
        deviceSelected=false;
        revertItemStatus();
        mAdapter.notifyDataSetChanged();
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
    }


    private void showPinLockedDialog() {

        int[] listenedItems = {com.cybex.componentservice.R.id.tv_cancel, com.cybex.componentservice.R.id.tv_confirm};
        CustomDialog dialog = new CustomDialog(activity,
                com.cybex.componentservice.R.layout.baseservice_dialog_pin_locked, listenedItems,false, Gravity.CENTER);
        dialog.setmWidth(SizeUtil.dp2px(259));
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

            }
        });
        dialog.setOnDialogItemClickListener(new CustomDialog.OnCustomDialogItemClickListener() {

            @Override
            public void OnCustomDialogItemClick(CustomDialog dialog, View view) {
                if(view.getId()== com.cybex.componentservice.R.id.tv_cancel){
                    dialog.cancel();
                }else if(view.getId()== com.cybex.componentservice.R.id.tv_confirm){
                    dialog.dismiss();
                    activity.showProgressDialog("");
                    DeviceOperationManager.getInstance().startFormat(activity.toString(), deviceName, new DeviceOperationManager.DeviceFormatCallback() {
                        @Override
                        public void onFormatStart() {

                        }

                        @Override
                        public void onFormatSuccess() {
                            activity.dissmisProgressDialog();
                            if(pinDialog!=null&&pinDialog.isShowing()){
                                pinDialog.cancel();
                            }
//                            DeviceOperationManager.getInstance().freeContext(activity.toString(), walletEntity.getBluetoothDeviceName(), null);
                        }

                        @Override
                        public void onFormatUpdate(int state) {

                        }

                        @Override
                        public void onFormatFailed() {
                            activity.dissmisProgressDialog();
                        }
                    });

                }
            }
        });
        dialog.show();


    }

    /**
     * 显示Wookong bio对话框
     */
    private void showInitWookongBioDialog() {
        int[] listenedItems = {R.id.btn_close, R.id.btn_create_wallet, R.id.btn_import_mne};

        AutoSize.autoConvertDensityOfGlobal(activity);

        CustomFullDialog dialog = new CustomFullDialog(activity,
                R.layout.eos_dialog_un_init_wookongbio, listenedItems, false,false, Gravity.BOTTOM);
        dialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                freeContext();
            }
        });

        dialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
            @Override
            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {
                switch (view.getId()) {
                    case com.cybex.gma.client.R.id.btn_close:
                        dialog.cancel();
//                        WookongBioManager.getInstance().freeContext(contextHandle);
//                        BluetoothConnectKeepJob.removeJob();
//                        finish();
                        break;
                    case com.cybex.gma.client.R.id.btn_create_wallet:
                        dialog.dismiss();
                        UISkipMananger.skipBackupMneGuideActivity(activity,
                                null);
                        dismiss();
                        break;
                    case com.cybex.gma.client.R.id.btn_import_mne:
                        dialog.dismiss();
                        Intent intent = new Intent(activity,
                                BluetoothImportWalletActivity.class);
                        activity.startActivity(intent);
                        dismiss();
                        break;
                    default:
                        break;
                }
            }
        });
        dialog.show();
    }

    public void dismissAllDialog() {
        DeviceOperationManager.getInstance().clearCallback(TAG);
//        if (connectDialog != null && connectDialog.isShowing()) {
//            connectDialog.cancel();
//        }
        if (pinDialog != null && pinDialog.isShowing()) {
            pinDialog.dismiss();
        }
        if (verifyDialog != null && verifyDialog.isShowing()) {
            verifyDialog.dismiss();
        }
        dismiss();
    }


}
