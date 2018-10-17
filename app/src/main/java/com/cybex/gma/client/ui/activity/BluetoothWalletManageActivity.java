package com.cybex.gma.client.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.allen.library.SuperTextView;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.CacheConstants;
import com.cybex.gma.client.event.ContextHandleEvent;
import com.cybex.gma.client.event.DeviceInfoEvent;
import com.cybex.gma.client.job.BluetoothConnectKeepJob;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.utils.AlertUtil;
import com.cybex.gma.client.utils.bluetooth.BlueToothWrapper;
import com.extropies.common.MiddlewareInterface;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.hxlx.core.lib.utils.SPUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BluetoothWalletManageActivity extends XActivity {

    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.tv_walletName_in_detailPage) TextView tvWalletNameInDetailPage;
    @BindView(R.id.eosAddress_in_detailPage) TextView eosAddressInDetailPage;
    @BindView(R.id.layout_wallet_briefInfo) ConstraintLayout layoutWalletBriefInfo;
    @BindView(R.id.bt_click_to_connect) Button btClickToConnect;
    @BindView(R.id.scroll_wallet_detail) ScrollView scrollWalletDetail;
    @BindView(R.id.superTextView_changePass) SuperTextView superTextViewChangePass;
    @BindView(R.id.view_bio_management) LinearLayout viewBioManagement;
    @BindView(R.id.bt_disconnect) Button btDisconnect;
    @BindView(R.id.tv_connection_status) TextView tvConnectionStatus;

    private BlueToothWrapper connectThread;
    private BlueToothWrapper getAddressThread;
    private BlueToothWrapper disconnectThread;
    private long mContextHandle;
    private ConnectHandler mConnectHandler;
    private String publicKey;
    private int mDevIndex;
    private String deviceName = "WOOKONG BIO####ED:C1:FF:D5:9C:FA";
    //private final String deviceName = "WOOKONG BIO####E7:D8:54:5C:33:82";


    @OnClick(R.id.bt_click_to_connect)
    public void startConnect() {
        showProgressDialog("connecting");

        if ((connectThread == null) || (connectThread.getState() == Thread.State.TERMINATED)) {
            connectThread = new BlueToothWrapper(mConnectHandler);
            connectThread.setInitContextWithDevNameWrapper(BluetoothWalletManageActivity.this,
                    deviceName);
            connectThread.start();
        }

        //BluetoothConnectKeepJob.connect(mConnectHandler, this, deviceName);
    }

    @OnClick(R.id.bt_disconnect)
    public void disconnect(){
        if ((disconnectThread == null) || (disconnectThread.getState() == Thread.State.TERMINATED))
        {
            disconnectThread = new BlueToothWrapper(mConnectHandler);
            LoggerManager.d("mContextHandle", mContextHandle);
            disconnectThread.setFreeContextWrapper(mContextHandle);
            disconnectThread.start();
            //mContextHandle = 0;
            //mDevIndex = MiddlewareInterface.INVALID_DEV_INDEX;
        }
    }

    @OnClick(R.id.layout_wallet_briefInfo)
    public void seeWalletDetail() {
        Bundle bundle = new Bundle();
        UISkipMananger.skipBluetoothWalletDetailActivity(BluetoothWalletManageActivity.this, bundle);
    }

    @OnClick(R.id.superTextView_changePass)
    public void goManageFPandPassword(){
        UISkipMananger.skipBluetoothFPAndPasswordActivity(BluetoothWalletManageActivity.this);
    }

    @Override
    public void bindUI(View rootView) {
        setNavibarTitle(getString(R.string.eos_title_wookong_bio), true);
        ButterKnife.bind(this);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
       // deviceName = SPUtils.getInstance().getString(ParamConstants.DEVICE_NAME);
        mDevIndex = 0;
        checkConnection();
        mConnectHandler = new ConnectHandler();
        eosAddressInDetailPage.setText(publicKey);
    }

    @Override
    public int getLayoutId() {
        return R.layout.eos_activity_bluetooth_wallet_manage;
    }

    @Override
    public Object newP() {
        return null;
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkConnection();
        eosAddressInDetailPage.setText(publicKey);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEosAddressRecieved(DeviceInfoEvent event){
        publicKey = event.getEosPublicKey();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onContextHandleRecieved(ContextHandleEvent event){
        mContextHandle = event.getContextHanle();
    }

    /**
     * 显示蓝牙卡连接时UI
     */
    public void showConnectedLayout() {
        viewBioManagement.setVisibility(View.VISIBLE);
        btDisconnect.setVisibility(View.VISIBLE);
        btClickToConnect.setVisibility(View.GONE);
        tvConnectionStatus.setVisibility(View.GONE);
    }

    /**
     * 显示蓝牙卡断开连接时UI
     */
    public void showDisconnectedLayout() {
        viewBioManagement.setVisibility(View.GONE);
        btDisconnect.setVisibility(View.GONE);
        btClickToConnect.setVisibility(View.VISIBLE);
        tvConnectionStatus.setVisibility(View.VISIBLE);
    }

    /**
     * 获取EOS地址（公钥）
     */
    public void getEosAddress(){
        //showProgressDialog("Getting Device Information");
        if ((getAddressThread == null) || (getAddressThread.getState() == Thread.State.TERMINATED))
        {
            getAddressThread = new BlueToothWrapper(mConnectHandler);
            getAddressThread.setGetAddressWrapper(mContextHandle, 0, MiddlewareInterface.PAEW_COIN_TYPE_EOS,
                    CacheConstants.EOS_DERIVE_PATH);
            getAddressThread.start();
        }
    }

    /**
     * 每次进入页面检查是否连接
     */
    public void checkConnection(){
        int status = SPUtils.getInstance().getInt(CacheConstants.BIO_CONNECT_STATUS);
        LoggerManager.d("status", status);
        switch (status){
            case CacheConstants.STATUS_BLUETOOTH_CONNCETED:
                showConnectedLayout();
                break;
            case CacheConstants.STATUS_BLUETOOTH_DISCONNCETED:
                showDisconnectedLayout();
                break;
            default:
                showDisconnectedLayout();
        }
    }

    class ConnectHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BlueToothWrapper.MSG_INIT_CONTEXT_START:
                    LoggerManager.d("MSG_INIT_CONTEXT_START");
                    break;
                case BlueToothWrapper.MSG_INIT_CONTEXT_FINISH:
                    LoggerManager.d("MSG_INIT_CONTEXT_FINISH");
                    BlueToothWrapper.InitContextReturnValue returnValue = (BlueToothWrapper.InitContextReturnValue) msg.obj;
                    if ((returnValue != null) && (returnValue.getReturnValue()
                            == MiddlewareInterface.PAEW_RET_SUCCESS)) {
                        //连接成功
                        SPUtils.getInstance().put(CacheConstants.BIO_CONNECT_STATUS, CacheConstants.STATUS_BLUETOOTH_CONNCETED);

                        mContextHandle = returnValue.getContextHandle();

                        ContextHandleEvent event = new ContextHandleEvent();
                        event.setContextHanle(mContextHandle);
                        EventBusProvider.postSticky(event);

                        getEosAddress();

                    } else {
                        //连接超时或失败
                        SPUtils.getInstance().put(CacheConstants.BIO_CONNECT_STATUS, CacheConstants.STATUS_BLUETOOTH_DISCONNCETED);
                        dissmisProgressDialog();
                        AlertUtil.showShortUrgeAlert(BluetoothWalletManageActivity.this, "Connect Fail");
                    }

                    if (connectThread!= null)connectThread.interrupt();

                    break;
                case BlueToothWrapper.MSG_FREE_CONTEXT_START:
                    LoggerManager.d("MSG_FREE_CONTEXT_START");
                    break;
                case BlueToothWrapper.MSG_FREE_CONTEXT_FINISH:
                    BluetoothConnectKeepJob.removeJob();
                    LoggerManager.d("MSG_FREE_CONTEXT_FINISH");
                    SPUtils.getInstance().put(CacheConstants.BIO_CONNECT_STATUS, CacheConstants.STATUS_BLUETOOTH_DISCONNCETED);
                    AlertUtil.showShortUrgeAlert(BluetoothWalletManageActivity.this, "Bio Disconnected");
                    showDisconnectedLayout();
                    break;
                case BlueToothWrapper.MSG_GET_ADDRESS_START:
                    //获取EOS地址（公钥）开始
                    break;
                case BlueToothWrapper.MSG_GET_ADDRESS_FINISH:
                    //获取EOS地址（公钥）结束
                    BlueToothWrapper.GetAddressReturnValue returnValueAddress = (BlueToothWrapper
                            .GetAddressReturnValue) msg.obj;
                    if (returnValueAddress.getReturnValue() == MiddlewareInterface.PAEW_RET_SUCCESS) {
                        if (returnValueAddress.getCoinType() == MiddlewareInterface.PAEW_COIN_TYPE_EOS) {
                            LoggerManager.d("EOS Address: " + returnValueAddress.getAddress());

                            String[] strArr = returnValueAddress.getAddress().split("####");
                            String publicKey = strArr[0];
                            eosAddressInDetailPage.setText(publicKey);

                            DeviceInfoEvent event = new DeviceInfoEvent();
                            event.setEosPublicKey(publicKey);
                            EventBusProvider.postSticky(event);
                            AlertUtil.showShortCommonAlert(BluetoothWalletManageActivity.this, "Bio Connected");
                            showConnectedLayout();
                            BluetoothConnectKeepJob.startConnectPolling(mContextHandle, mConnectHandler, 0);
                        }
                    }else {
                        showDisconnectedLayout();
                        AlertUtil.showShortUrgeAlert(BluetoothWalletManageActivity.this, "Connect Fail");
                    }
                    dissmisProgressDialog();
                    break;

                case BlueToothWrapper.MSG_HEART_BEAT_DATA_UPDATE:
                    LoggerManager.d("MSG_HEART_BEAT_DATA_UPDATE");
                    if (msg.obj != null) {
                        byte[] heartBeatData = (byte[])msg.obj;
                        if (heartBeatData.length >= 3 ) {
                            LoggerManager.d("power level : ", heartBeatData[2]);
                        }
                    }

                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public boolean useEventBus() {
        return true;
    }

}
