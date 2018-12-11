package com.cybex.walletmanagement.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.allen.library.SuperTextView;
import com.cybex.base.view.LabelLayout;
import com.cybex.componentservice.WookongUtils;
import com.cybex.componentservice.config.BaseConst;
import com.cybex.componentservice.config.RouterConst;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.db.util.DBCallback;
import com.cybex.componentservice.event.HeartBeatRefreshDataEvent;
import com.cybex.componentservice.event.WalletNameChangedEvent;
import com.cybex.componentservice.event.WookongFormattedEvent;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.DeviceOperationManager;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.componentservice.utils.bluetooth.BlueToothWrapper;
import com.cybex.walletmanagement.R;
import com.cybex.walletmanagement.ui.activity.ChangeWalletNameActivity;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.siberiadante.customdialoglib.CustomDialog;
import com.siberiadante.customdialoglib.CustomFullDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class BluetoothWalletDetailFragment extends XFragment {

    TitleBar btnNavibar;
    LabelLayout superTextViewBWalletName;
    SuperTextView superTextViewBatteryLife;
//    Button btToConnect;
    //Button btCancelPair;
    Button btFormat;

//    private long contextHandle;
//    private BlueToothWrapper connectThread;
    private BlueToothWrapper formatThread;
//    private BlueToothWrapper getAddressThread;
//    private BlueToothWrapper verifyFPThread;
//    private ConnectHandler mConnectHandler;
//    private FreeContextHandler freeContextHandler;
    private MultiWalletEntity multiWalletEntity ;
    private CustomFullDialog confirmDialog;
    private boolean isFormating;
    //    private String deviceName;
    //private final String deviceName = "WOOKONG BIO####E7:D8:54:5C:33:82";

    public static BluetoothWalletDetailFragment newInstance() {
        Bundle args = new Bundle();
        BluetoothWalletDetailFragment fragment = new BluetoothWalletDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void bindUI(View rootView) {
        setNavibarTitle(getString(R.string.walletmanage_title_wooKong_bio), true);

        if (rootView != null) {
            btnNavibar = rootView.findViewById(R.id.btn_navibar);
            superTextViewBWalletName = rootView.findViewById(R.id.superTextView_bWallet_name);
            superTextViewBatteryLife = rootView.findViewById(R.id.superTextView_battery_life);
//            btToConnect = rootView.findViewById(R.id.bt_to_connect);
            //btCancelPair = rootView.findViewById(R.id.bt_cancel_pair);
            btFormat = rootView.findViewById(R.id.bt_format);
        }
    }

    @Override
    public void initData(Bundle savedInstanceState) {

        multiWalletEntity = DBManager.getInstance().getMultiWalletEntityDao().getBluetoothWalletList().get(0);

        checkConnection();
        updatePowerAmount();

//        mConnectHandler = new ConnectHandler();
//        freeContextHandler = new FreeContextHandler();
//        deviceName = SPUtils.getInstance().getString(WalletManageConst.DEVICE_NAME);

        //点击连接
//        btToConnect.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showProgressDialog("connecting");
//                if ((connectThread == null) || (connectThread.getState() == Thread.State.TERMINATED)) {
//                    connectThread = new BlueToothWrapper(mConnectHandler);
//                    connectThread.setInitContextWithDevNameWrapper(rootView,
//                            deviceName);
//                    connectThread.start();
//                }
//            }
//        });

        //取消配对
        /*
        btCancelPair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }); */

        superTextViewBWalletName.setRightText(multiWalletEntity.getWalletName());

        //格式化
        btFormat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFormat();
//                if(confirmDialog!=null&&confirmDialog.isShowing()){
//
//                }
//                if(confirmDialog!=null){
//                    confirmDialog.dismiss();
//                }

            }
        });

        //修改钱包名称
        superTextViewBWalletName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChangeWalletNameActivity.class);
                intent.putExtra(BaseConst.KEY_WALLET_ENTITY,multiWalletEntity);
                startActivity(intent);
            }
        });
    }

    private void updatePowerAmount() {
        boolean isConnected = DeviceOperationManager.getInstance().isDeviceConnectted(multiWalletEntity.getBluetoothDeviceName());
        if (isConnected) {
            int powerAmount = DeviceOperationManager.getInstance().getDevicePowerAmount(multiWalletEntity.getBluetoothDeviceName());
            int chargeMode = DeviceOperationManager.getInstance().getDeviceBatteryChargeMode(multiWalletEntity.getBluetoothDeviceName());
            if(chargeMode==0){
                //usb
                superTextViewBatteryLife.setRightString(getString(R.string.wookong_charging));
            }else{
                String devicePowerPercent = WookongUtils.getDevicePowerPercent(powerAmount);
                if(!TextUtils.isEmpty(devicePowerPercent)){
                    superTextViewBatteryLife.setRightString(devicePowerPercent + "%");
                }else{
                    superTextViewBatteryLife.setRightString("");
                }
            }
        }else{
            superTextViewBatteryLife.setRightString("");
        }
    }


    @Override
    public void onDestroy() {
        if(isFormating){
            isFormating=false;
            DeviceOperationManager.getInstance().abortButton(BluetoothWalletDetailFragment.this.toString(),multiWalletEntity.getBluetoothDeviceName());
        }
        DeviceOperationManager.getInstance().clearCallback(this.toString());
        super.onDestroy();
    }


    @Override
    public int getLayoutId() {
        return R.layout.walletmanage_activity_bluetooth_wallet_detail;
    }

    @Override
    public Object newP() {
        return null;
    }

    @Override
    public boolean useEventBus() {
        return true;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshWalletName(WalletNameChangedEvent event) {
        if (event.getWalletID()==multiWalletEntity.getId()) {
            multiWalletEntity.setWalletName(event.getWalletName());
            superTextViewBWalletName.setRightText(multiWalletEntity.getWalletName());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveHeartBeatEvent(HeartBeatRefreshDataEvent event){
        updatePowerAmount();
    }

//
//    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
//    public void getContextHandle(ContextHandleEvent event) {
//        contextHandle = event.getContextHanle();
//    }

    /**
     * 开始格式化
     */
    public void startFormat() {
//        if ((formatThread == null) || (formatThread.getState() == Thread.State.TERMINATED)) {
//            formatThread = new BlueToothWrapper(mConnectHandler);
//            formatThread.setFormatDeviceWrapper(contextHandle, 0);
//            formatThread.start();
//        }

        if(isFormating)return;
        isFormating=true;
        showProgressDialog(getString(R.string.walletmanage_formating));
        final String bluetoothDeviceName = multiWalletEntity.getBluetoothDeviceName();
        DeviceOperationManager.getInstance().startFormat(this.toString(),
                bluetoothDeviceName,
                new DeviceOperationManager.DeviceFormatCallback() {
                    @Override
                    public void onFormatStart() {

                    }

                    @Override
                    public void onFormatSuccess() {
                        isFormating=false;
                        if(confirmDialog!=null&&confirmDialog.isShowing()){
                            confirmDialog.dismiss();
                        }
                        dissmisProgressDialog();

                        DeviceOperationManager.getInstance().freeContext(BluetoothWalletDetailFragment.this.toString(), bluetoothDeviceName, new DeviceOperationManager.FreeContextCallback() {
                            @Override
                            public void onFreeStart() {

                            }

                            @Override
                            public void onFreeSuccess() {
                                DeviceOperationManager.getInstance().clearCallback(BluetoothWalletDetailFragment.this.toString());
                            }

                            @Override
                            public void onFreeFailed() {

                            }
                        });

                        //delete db data
                        DBManager.getInstance().getMultiWalletEntityDao().deleteEntityAsync(multiWalletEntity, new DBCallback() {
                            @Override
                            public void onSucceed() {

                                List<MultiWalletEntity> multiWalletEntityList = DBManager.getInstance().getMultiWalletEntityDao().getMultiWalletEntityList();
                                if (multiWalletEntityList.size() > 0) {
                                    MultiWalletEntity walletEntity = multiWalletEntityList.get(0);
                                    walletEntity.setIsCurrentWallet(1);
                                    walletEntity.save();
                                }
                                //show dialog
                                showFormatDoneDialog();
                            }

                            @Override
                            public void onFailed(Throwable error) {
                                LoggerManager.e("deleteEntityAsync error=" + error.getMessage());
                                GemmaToastUtils.showLongToast(getString(R.string.walletmanage_format_fail));
                            }
                        });
                    }

                    @Override
                    public void onFormatUpdate(int state) {
                        dissmisProgressDialog();
                        showConfirmFormatonCardDialog();
                    }

                    @Override
                    public void onFormatFailed() {
                        isFormating=false;
                        if(confirmDialog!=null&&confirmDialog.isShowing()){
                            confirmDialog.dismiss();
                        }
                        dissmisProgressDialog();
                        DeviceOperationManager.getInstance().clearCallback(BluetoothWalletDetailFragment.this.toString());
                        GemmaToastUtils.showLongToast(getString(R.string.walletmanage_format_fail));
                    }
        });
    }

//    public void startVerifyFP() {
//        if ((verifyFPThread == null) || (verifyFPThread.getState() == Thread.State.TERMINATED)) {
//            verifyFPThread = new BlueToothWrapper(mConnectHandler);
//            verifyFPThread.setVerifyFPWrapper(contextHandle, 0);
//            verifyFPThread.start();
//        }
//    }

//    public void freeContext() {
//        if ((connectThread == null) || (connectThread.getState() == Thread.State.TERMINATED)) {
//            connectThread = new BlueToothWrapper(freeContextHandler);
//            connectThread.setFreeContextWrapper(contextHandle);
//            connectThread.start();
//        }
//    }

    /**
     * 每次进入页面检查是否连接
     */
    public void checkConnection() {
//        int status = SPUtils.getInstance().getInt(CacheConstants.BIO_CONNECT_STATUS);
//        switch (status) {
//            case CacheConstants.STATUS_BLUETOOTH_CONNCETED:
//                showConnectedLayout();
//                break;
//            case CacheConstants.STATUS_BLUETOOTH_DISCONNCETED:
//                showDisconnectedLayout();
//                break;
//            default:
//                showDisconnectedLayout();
//        }

        //check status
        boolean isConnected = DeviceOperationManager.getInstance().isDeviceConnectted(multiWalletEntity.getBluetoothDeviceName());
        if(isConnected){
            showConnectedLayout();
        }else{
            showDisconnectedLayout();
        }

    }

    public void showConnectedLayout() {
        btFormat.setVisibility(View.VISIBLE);

    }

    public void showDisconnectedLayout() {
        btFormat.setVisibility(View.GONE);
    }


    /**
     * 显示确认格式化Dialog
     */
//    private void showConfirmFormatDialog() {
//        int[] listenedItems = {R.id.tv_ok, R.id.tv_cancel};
//        CustomDialog dialog = new CustomDialog(rootView,
//                R.layout.walletmanage_dialog_bluetooth_format_confirm, listenedItems, false, Gravity.CENTER);
//        dialog.setOnDialogItemClickListener(new CustomDialog.OnCustomDialogItemClickListener() {
//
//            @Override
//            public void OnCustomDialogItemClick(final CustomDialog dialog, View view) {
//                if (view.getId() == R.id.tv_ok) {
////                    startVerifyFP();
//                } else if (view.getId() == R.id.tv_cancel) {
//                    dialog.cancel();
//                }
//            }
//        });
//        dialog.show();
//    }

    /**
     * 显示按电源键确认格式化Dialog
     */
    private void showConfirmFormatonCardDialog() {
        if (confirmDialog != null) {
            if(!confirmDialog.isShowing()&&isFormating){
                confirmDialog.show();
            }
            return;
        }
        int[] listenedItems = {R.id.imv_back};
        confirmDialog = new CustomFullDialog(getActivity(),
                R.layout.baseservice_dialog_bluetooth_button_confirm, listenedItems, false, Gravity.BOTTOM);
        confirmDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if(isFormating){
                    isFormating=false;
                    DeviceOperationManager.getInstance().abortButton(BluetoothWalletDetailFragment.this.toString(),multiWalletEntity.getBluetoothDeviceName());
                }
            }
        });
        confirmDialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
            @Override
            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {
                if (view.getId() == R.id.imv_back) {
                    dialog.cancel();
                }
            }
        });
        confirmDialog.show();
        TextView tvTip = confirmDialog.getAllView().findViewById(R.id.tv_tip);
        tvTip.setText(getString(R.string.walletmanage_tip_confirm_format));
    }

    /**
     * 显示确认格式化Dialog
     */
    private void showFormatDoneDialog() {
        int[] listenedItems = {R.id.tv_cancel, R.id.tv_init};
        CustomDialog dialog = new CustomDialog(getActivity(),
                R.layout.walletmanage_dialog_bluetooth_format_done, listenedItems, false, Gravity.CENTER);
        dialog.setOnDialogItemClickListener(new CustomDialog.OnCustomDialogItemClickListener() {

            @Override
            public void OnCustomDialogItemClick(CustomDialog dialog, View view) {
                int size = DBManager.getInstance().getMultiWalletEntityDao().getMultiWalletEntityList().size();
                if (view.getId() == R.id.tv_cancel) {
                    dialog.cancel();
                    if(size>0){
//                        ARouter.getInstance().build(RouterConst.PATH_TO_WALLET_HOME)
//                                .withInt(BaseConst.KEY_INIT_TYPE,BaseConst.APP_HOME_INITTYPE_TO_INITI_PAGE)
//                                .navigation();


//                        Intent intent = new Intent(context, SelectCurrentWalletActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(intent);

                        getActivity().finish();

                        EventBusProvider.post(new WookongFormattedEvent(false));

                    }else{
                        ARouter.getInstance().build(RouterConst.PATH_TO_WALLET_HOME)
                                .withInt(BaseConst.KEY_INIT_TYPE,BaseConst.APP_HOME_INITTYPE_TO_INITI_PAGE)
                                .navigation();
                    }



                } else if (view.getId() == R.id.tv_init) {
                    dialog.cancel();
                    //UISkipMananger.skipBluetoothPaireActivity(rootView, new Bundle());
                    if(size>0){
                        getActivity().finish();
                        EventBusProvider.post(new WookongFormattedEvent(true));
                    }else{
                        ARouter.getInstance().build(RouterConst.PATH_TO_WALLET_HOME)
                                .withInt(BaseConst.KEY_INIT_TYPE,BaseConst.APP_HOME_INITTYPE_TO_INITI_PAGE_WOOKONG_PAIR)
                                .navigation();
                    }

                }
            }
        });
        dialog.show();
    }


//    class FreeContextHandler extends Handler {
//
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case BlueToothWrapper.MSG_FREE_CONTEXT_START:
//                    break;
//                case BlueToothWrapper.MSG_FREE_CONTEXT_FINISH:
//                    SPUtils.getInstance()
//                            .put(CacheConstants.BIO_CONNECT_STATUS, CacheConstants.STATUS_BLUETOOTH_DISCONNCETED);
//                    showFormatDoneDialog();
//                    break;
//            }
//        }
//    }
//
//    class ConnectHandler extends Handler {
//
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case BlueToothWrapper.MSG_INIT_CONTEXT_START:
//                    //连接开始
//                    //LoggerManager.d("MSG_INIT_CONTEXT_START");
//                    break;
//                case BlueToothWrapper.MSG_INIT_CONTEXT_FINISH:
//                    //连接结束
//                    //LoggerManager.d("MSG_INIT_CONTEXT_FINISH");
//                    BlueToothWrapper.InitContextReturnValue returnValue = (BlueToothWrapper.InitContextReturnValue) msg.obj;
//                    if ((returnValue != null) && (returnValue.getReturnValue()
//                            == MiddlewareInterface.PAEW_RET_SUCCESS)) {
//                        //连接成功
//
//                        SPUtils.getInstance()
//                                .put(CacheConstants.BIO_CONNECT_STATUS, CacheConstants.STATUS_BLUETOOTH_CONNCETED);
//
//                        contextHandle = returnValue.getContextHandle();
//                        ContextHandleEvent event = new ContextHandleEvent();
//                        event.setContextHanle(contextHandle);
//                        EventBusProvider.postSticky(event);
//
//                        showConnectedLayout();
//                        AlertUtil.showShortCommonAlert(rootView, "Bio Connected");
//
//                    } else {
//                        //连接失败
//                        SPUtils.getInstance()
//                                .put(CacheConstants.BIO_CONNECT_STATUS, CacheConstants.STATUS_BLUETOOTH_DISCONNCETED);
//                        AlertUtil.showShortUrgeAlert(rootView, "Bio Connect Fail");
//                        showDisconnectedLayout();
//                    }
//
//                    connectThread.interrupt();
//                    break;
//                case BlueToothWrapper.MSG_GET_DEV_INFO_FINISH:
//                    //LoggerManager.d("MSG_GET_DEV_INFO_FINISH");
//                    //获得设备信息
//                    BlueToothWrapper.GetDevInfoReturnValue reValue = (BlueToothWrapper.GetDevInfoReturnValue) msg.obj;
//                    if (reValue.getReturnValue() == MiddlewareInterface.PAEW_RET_SUCCESS) {
//                        MiddlewareInterface.PAEW_DevInfo devInfo = reValue.getDeviceInfo();
//                        LoggerManager.d("devInfo " + devInfo.ucLifeCycle);
//                    }
//
//                    break;
//
//                case BlueToothWrapper.MSG_FREE_CONTEXT_START:
//                    //断开连接开始
//                    break;
//                case BlueToothWrapper.MSG_FREE_CONTEXT_FINISH:
//                    //断开连接结束
//                    //LoggerManager.d("MSG_FREE_CONTEXT_FINISH");
//                    //BluetoothConnectKeepJob.removeJob();
//                    SPUtils.getInstance()
//                            .put(CacheConstants.BIO_CONNECT_STATUS, CacheConstants.STATUS_BLUETOOTH_DISCONNCETED);
//                    AlertUtil.showShortUrgeAlert(rootView, "Bio Disconnected");
//                    showDisconnectedLayout();
//
//                    break;
//                case BlueToothWrapper.MSG_VERIFYFP_START:
//                    LoggerManager.d("MSG_VERIFYFP_START");
//                    showVerifyFPDialog();
//                    //验证指纹开始
//                    break;
//                case BlueToothWrapper.MSG_VERIFYFP_FINISH:
//                    dialog.cancel();
//                    startFormat();
//                    //验证指纹完成
//                    break;
//                case BlueToothWrapper.MSG_FORMAT_DEVICE_START:
//                    //格式化开始
//                    break;
//                case BlueToothWrapper.MSG_FORMAT_DEVICE_FINISH:
//                    //格式化完成
//                    //断开连接
//                    freeContext();
//                    break;
//                default:
//                    break;
//            }
//        }
//    }

//    CustomFullDialog dialog = null;

    /**
     * 显示通过蓝牙卡验证指纹dialog
     */
//    private void showVerifyFPDialog() {
//        int[] listenedItems = {R.id.imv_back};
//        dialog = new CustomFullDialog(rootView,
//                R.layout.walletmanage_dialog_bluetooth_finger_authorize, listenedItems, false, Gravity.BOTTOM);
//        dialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
//            @Override
//            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {
//
//                if (view.getId() == R.id.imv_back) {
//                    dialog.cancel();
//                }
//            }
//        });
//        dialog.show();
//    }

}
