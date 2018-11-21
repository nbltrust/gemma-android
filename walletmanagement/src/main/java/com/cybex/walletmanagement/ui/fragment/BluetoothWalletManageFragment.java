package com.cybex.walletmanagement.ui.fragment;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.allen.library.SuperTextView;

import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.event.DeviceConnectStatusUpdateEvent;
import com.cybex.componentservice.event.HeartBeatRefreshDataEvent;
import com.cybex.componentservice.event.WalletNameChangedEvent;

import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.DeviceOperationManager;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.componentservice.utils.WookongBioPswValidateHelper;
import com.cybex.componentservice.utils.WookongConnectHelper;
import com.cybex.walletmanagement.R;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class BluetoothWalletManageFragment extends XFragment {

    public static BluetoothWalletManageFragment newInstance() {
        Bundle args = new Bundle();
        BluetoothWalletManageFragment fragment = new BluetoothWalletManageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    TitleBar btnNavibar;
    TextView tvWalletNameInDetailPage;
    ConstraintLayout layoutWalletBriefInfo;
    Button btClickToConnect;
    ScrollView scrollWalletDetail;
    SuperTextView superTextViewChangePass;
    LinearLayout batteryContainer;
    Button btDisconnect;
    TextView tvConnectionStatus;

    ImageView ivWookongLogo;
    ImageView ivRight;


    //    private BlueToothWrapper connectThread;
//    private BlueToothWrapper getAddressThread;
//    private BlueToothWrapper disconnectThread;
//    private long mContextHandle;
//    private ConnectHandler mConnectHandler;
//    private String publicKey;
//    private String deviceName = "WOOKONG BIO####ED:C1:FF:D5:9C:FA";
    private MultiWalletEntity multiWalletEntity;
    //private final String deviceName = "WOOKONG BIO####E7:D8:54:5C:33:82";

    @Override
    public void bindUI(View rootView) {
        setNavibarTitle(getString(R.string.walletmanage_title_wooKong_bio), true, true);

        btnNavibar = rootView.findViewById(R.id.btn_navibar);
        tvWalletNameInDetailPage = rootView.findViewById(R.id.tv_walletName_in_detailPage);
        layoutWalletBriefInfo = rootView.findViewById(R.id.layout_wallet_briefInfo);
        btClickToConnect = rootView.findViewById(R.id.bt_click_to_connect);
        scrollWalletDetail = rootView.findViewById(R.id.scroll_wallet_detail);
        superTextViewChangePass = rootView.findViewById(R.id.superTextView_fp_and_pass);
        batteryContainer = rootView.findViewById(R.id.view_bio_management);
        btDisconnect = rootView.findViewById(R.id.bt_disconnect);
        tvConnectionStatus = rootView.findViewById(R.id.tv_connection_status);

        ivWookongLogo = rootView.findViewById(R.id.iv_wooKong_logo);
        ivRight = rootView.findViewById(R.id.iv_manage_wooKong);



    }

    @Override
    public void initData(Bundle savedInstanceState) {

        multiWalletEntity = DBManager.getInstance().getMultiWalletEntityDao().getBluetoothWalletList().get(0);

        tvWalletNameInDetailPage.setText(multiWalletEntity.getWalletName());
        checkConnection();
//        mConnectHandler = new ConnectHandler();
//        deviceName = SPUtils.getInstance().getString(WalletManageConst.DEVICE_NAME);

        //连接
        btClickToConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showProgressDialog("connecting");

//                if ((connectThread == null) || (connectThread.getState() == Thread.State.TERMINATED)) {
//                    connectThread = new BlueToothWrapper(mConnectHandler);
//                    connectThread.setInitContextWithDevNameWrapper(getActivity(),
//                            deviceName);
//                    connectThread.start();
//                }

                WookongConnectHelper wookongConnectHelper = new WookongConnectHelper(multiWalletEntity, getActivity());
                wookongConnectHelper.startConnectDevice(new WookongConnectHelper.ConnectWookongBioCallback() {
                    @Override
                    public void onConnectSuccess() {
                        //更换展示UI
                        showConnectedLayout();
                    }

                    @Override
                    public void onConnectFail() {

                    }
                });

            }
        });

        //断开连接
        btDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if ((disconnectThread == null) || (disconnectThread.getState() == Thread.State.TERMINATED))
//                {
//                    disconnectThread = new BlueToothWrapper(mConnectHandler);
//                    LoggerManager.d("mContextHandle", mContextHandle);
//                    disconnectThread.setFreeContextWrapper(mContextHandle);
//                    disconnectThread.start();
//                }

                DeviceOperationManager.getInstance().freeContext(BluetoothWalletManageFragment.this.toString(),
                        multiWalletEntity.getBluetoothDeviceName(),
                        new DeviceOperationManager.FreeContextCallback() {
                    @Override
                    public void onFreeStart() {

                    }

                    @Override
                    public void onFreeSuccess() {
                        showDisconnectedLayout();
                    }

                    @Override
                    public void onFreeFailed() {
                        LoggerManager.d("onFreeFailed");
                    }
                });
            }
        });

        //指纹与密码
        superTextViewChangePass.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {

                //first verify pin
                WookongBioPswValidateHelper wookongBioPswValidateHelper = new WookongBioPswValidateHelper(multiWalletEntity, getActivity());
                wookongBioPswValidateHelper.startValidatePassword(new WookongBioPswValidateHelper.PasswordValidateCallback() {
                    @Override
                    public void onValidateSuccess(String password) {
                        start(BluetoothFPAndPasswordFragment.newInstance(password));
                    }

                    @Override
                    public void onValidateFail(int failedCount) {

                    }
                });
            }
        });

        //蓝牙钱包详情
        layoutWalletBriefInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start(BluetoothWalletDetailFragment.newInstance());
            }
        });
    }


    @Override
    public void onDestroy() {
        DeviceOperationManager.getInstance().clearCallback(this.toString());
        super.onDestroy();
    }

    @Override
    public int getLayoutId() {
        return R.layout.walletmanage_fragment_bluetooth_wallet_manage;
    }

    @Override
    public Object newP() {
        return null;
    }


//    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
//    public void onContextHandleRecieved(ContextHandleEvent event){
//        mContextHandle = event.getContextHanle();
//    }

    /**
     * 显示蓝牙卡连接时UI
     */
    public void showConnectedLayout() {
        layoutWalletBriefInfo.setEnabled(true);
        batteryContainer.setVisibility(View.VISIBLE);
        btDisconnect.setVisibility(View.VISIBLE);
        btClickToConnect.setVisibility(View.GONE);

        ivRight.setVisibility(View.VISIBLE);
        ivWookongLogo.setImageResource(R.drawable.wookong_logo);

        int deviceBatteryChargeMode = DeviceOperationManager.getInstance().getDeviceBatteryChargeMode(multiWalletEntity.getBluetoothDeviceName());
        if(deviceBatteryChargeMode==1){
            int powerAmount = DeviceOperationManager.getInstance().getDevicePowerAmount(multiWalletEntity.getBluetoothDeviceName());
            tvConnectionStatus.setText(getString(R.string.walletmanage_battery_left)+Math.abs(powerAmount));
        }else{
            tvConnectionStatus.setText(R.string.walletmanage_status_connected);
        }


    }

    /**
     * 显示蓝牙卡断开连接时UI
     */
    public void showDisconnectedLayout() {
        layoutWalletBriefInfo.setEnabled(false);
        batteryContainer.setVisibility(View.GONE);
        btDisconnect.setVisibility(View.GONE);
        btClickToConnect.setVisibility(View.VISIBLE);

        tvConnectionStatus.setText(R.string.walletmanage_status_not_connected);
        ivRight.setVisibility(View.GONE);
        ivWookongLogo.setImageResource(R.drawable.wookong_logo_gray);

    }

    /**
     * 每次进入页面检查是否连接
     */
    public void checkConnection() {
//        int status = SPUtils.getInstance().getInt(CacheConstants.BIO_CONNECT_STATUS);
//        LoggerManager.d("status", status);
//        switch (status){
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
        if (isConnected) {
            showConnectedLayout();
        } else {
            showDisconnectedLayout();
        }

    }

//    class ConnectHandler extends Handler {
//
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case BlueToothWrapper.MSG_INIT_CONTEXT_START:
//                    LoggerManager.d("MSG_INIT_CONTEXT_START");
//                    break;
//                case BlueToothWrapper.MSG_INIT_CONTEXT_FINISH:
//                    //连接流程完成
//                    LoggerManager.d("MSG_INIT_CONTEXT_FINISH");
//                    BlueToothWrapper.InitContextReturnValue returnValue = (BlueToothWrapper.InitContextReturnValue) msg.obj;
//                    if ((returnValue != null) && (returnValue.getReturnValue()
//                            == MiddlewareInterface.PAEW_RET_SUCCESS)) {
//                        //连接成功
//                        SPUtils.getInstance().put(CacheConstants.BIO_CONNECT_STATUS, CacheConstants.STATUS_BLUETOOTH_CONNCETED);
//
//                        //post 设备句柄
//                        mContextHandle = returnValue.getContextHandle();
//                        ContextHandleEvent event = new ContextHandleEvent();
//                        event.setContextHanle(mContextHandle);
//                        EventBusProvider.postSticky(event);
//                        //更换展示UI
//                        showConnectedLayout();
//
//                    } else {
//                        //连接超时或失败
//                        SPUtils.getInstance().put(CacheConstants.BIO_CONNECT_STATUS, CacheConstants.STATUS_BLUETOOTH_DISCONNCETED);
//                        dissmisProgressDialog();
//                        AlertUtil.showShortUrgeAlert(getActivity(), "Connect Fail");
//                    }
//
//                    if (connectThread!= null)connectThread.interrupt();
//
//                    break;
//                case BlueToothWrapper.MSG_FREE_CONTEXT_START:
//                    //断开连接流程开始
//                    //LoggerManager.d("MSG_FREE_CONTEXT_START");
//                    break;
//                case BlueToothWrapper.MSG_FREE_CONTEXT_FINISH:
//                    //断开连接流程结束
//                    //BluetoothConnectKeepJob.removeJob();
//                    LoggerManager.d("MSG_FREE_CONTEXT_FINISH");
//                    SPUtils.getInstance().put(CacheConstants.BIO_CONNECT_STATUS, CacheConstants.STATUS_BLUETOOTH_DISCONNCETED);
//                    AlertUtil.showShortUrgeAlert(getActivity(), "Bio Disconnected");
//                    showDisconnectedLayout();
//                    break;
//                default:
//                    break;
//            }
//        }
//    }

    @Override
    public boolean useEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveConnectEvent(DeviceConnectStatusUpdateEvent event){
        if(event.status==DeviceConnectStatusUpdateEvent.STATUS_BLUETOOTH_CONNCETED){
            showConnectedLayout();
        }else{
            showDisconnectedLayout();
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveHeartBeatEvent(HeartBeatRefreshDataEvent event){
        boolean isConnected = DeviceOperationManager.getInstance().isDeviceConnectted(multiWalletEntity.getBluetoothDeviceName());
        if (isConnected) {
            int deviceBatteryChargeMode = DeviceOperationManager.getInstance().getDeviceBatteryChargeMode(multiWalletEntity.getBluetoothDeviceName());
            if(deviceBatteryChargeMode==1){
                int powerAmount = DeviceOperationManager.getInstance().getDevicePowerAmount(multiWalletEntity.getBluetoothDeviceName());
                tvConnectionStatus.setText(getString(R.string.walletmanage_battery_left)+Math.abs(powerAmount));
            }else{
                tvConnectionStatus.setText(R.string.walletmanage_status_connected);
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changeWalletName(WalletNameChangedEvent event) {
        if(event.getWalletID()==multiWalletEntity.getId()){
            multiWalletEntity.setWalletName(event.getWalletName());
            tvWalletNameInDetailPage.setText(multiWalletEntity.getWalletName());
        }
    }

}
