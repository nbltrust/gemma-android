package com.cybex.walletmanagement.ui.activity;

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
import com.cybex.componentservice.config.CacheConstants;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.componentservice.utils.AlertUtil;

import com.cybex.componentservice.utils.bluetooth.BlueToothWrapper;

import com.cybex.walletmanagement.R;
import com.cybex.walletmanagement.config.WalletManageConst;
import com.cybex.walletmanagement.event.ContextHandleEvent;

import com.extropies.common.MiddlewareInterface;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.hxlx.core.lib.utils.SPUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 蓝牙钱包管理页
 */
public class BluetoothWalletManageActivity extends XActivity {

    TitleBar btnNavibar;
    TextView tvWalletNameInDetailPage;
    ConstraintLayout layoutWalletBriefInfo;
    Button btClickToConnect;
    ScrollView scrollWalletDetail;
    SuperTextView superTextViewChangePass;
    LinearLayout viewBioManagement;
    Button btDisconnect;
    TextView tvConnectionStatus;

    private BlueToothWrapper connectThread;
    private BlueToothWrapper getAddressThread;
    private BlueToothWrapper disconnectThread;
    private long mContextHandle;
    private ConnectHandler mConnectHandler;
    private String publicKey;
    //private String deviceName = "";
    private String deviceName = "WOOKONG BIO####ED:C1:FF:D5:9C:FA";
    //private final String deviceName = "WOOKONG BIO####E7:D8:54:5C:33:82";

    @Override
    public void bindUI(View rootView) {
        setNavibarTitle(getString(R.string.walletmanage_title_wooKong_bio), true);

        btnNavibar = findViewById(R.id.btn_navibar);
        tvWalletNameInDetailPage = findViewById(R.id.tv_walletName_in_detailPage);
        layoutWalletBriefInfo = findViewById(R.id.layout_wallet_briefInfo);
        btClickToConnect = findViewById(R.id.bt_click_to_connect);
        scrollWalletDetail = findViewById(R.id.scroll_wallet_detail);
        superTextViewChangePass = findViewById(R.id.superTextView_fp_and_pass);
        viewBioManagement = findViewById(R.id.view_bio_management);
        btDisconnect = findViewById(R.id.bt_disconnect);
        tvConnectionStatus = findViewById(R.id.tv_connection_status);

    }

    @Override
    public void initData(Bundle savedInstanceState) {
        deviceName = SPUtils.getInstance().getString(WalletManageConst.DEVICE_NAME);

        checkConnection();
        mConnectHandler = new ConnectHandler();
        deviceName = SPUtils.getInstance().getString(WalletManageConst.DEVICE_NAME);

        //连接
        btClickToConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog("connecting");

                if ((connectThread == null) || (connectThread.getState() == Thread.State.TERMINATED)) {
                    connectThread = new BlueToothWrapper(mConnectHandler);
                    connectThread.setInitContextWithDevNameWrapper(BluetoothWalletManageActivity.this,
                            deviceName);
                    connectThread.start();
                }
            }
        });

        //断开连接
        btDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });

        //指纹与密码
        superTextViewChangePass.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {

            }
        });

        //蓝牙钱包详情
        layoutWalletBriefInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.walletmanage_activity_bluetooth_wallet_manage;
    }

    @Override
    public Object newP() {
        return null;
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkConnection();
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
                    //连接流程完成
                    LoggerManager.d("MSG_INIT_CONTEXT_FINISH");
                    BlueToothWrapper.InitContextReturnValue returnValue = (BlueToothWrapper.InitContextReturnValue) msg.obj;
                    if ((returnValue != null) && (returnValue.getReturnValue()
                            == MiddlewareInterface.PAEW_RET_SUCCESS)) {
                        //连接成功
                        SPUtils.getInstance().put(CacheConstants.BIO_CONNECT_STATUS, CacheConstants.STATUS_BLUETOOTH_CONNCETED);

                        //post 设备句柄
                        mContextHandle = returnValue.getContextHandle();
                        ContextHandleEvent event = new ContextHandleEvent();
                        event.setContextHanle(mContextHandle);
                        EventBusProvider.postSticky(event);
                        //更换展示UI
                        showConnectedLayout();

                    } else {
                        //连接超时或失败
                        SPUtils.getInstance().put(CacheConstants.BIO_CONNECT_STATUS, CacheConstants.STATUS_BLUETOOTH_DISCONNCETED);
                        dissmisProgressDialog();
                        AlertUtil.showShortUrgeAlert(BluetoothWalletManageActivity.this, "Connect Fail");
                    }

                    if (connectThread!= null)connectThread.interrupt();

                    break;
                case BlueToothWrapper.MSG_FREE_CONTEXT_START:
                    //断开连接流程开始
                    //LoggerManager.d("MSG_FREE_CONTEXT_START");
                    break;
                case BlueToothWrapper.MSG_FREE_CONTEXT_FINISH:
                    //断开连接流程结束
                    //BluetoothConnectKeepJob.removeJob();
                    LoggerManager.d("MSG_FREE_CONTEXT_FINISH");
                    SPUtils.getInstance().put(CacheConstants.BIO_CONNECT_STATUS, CacheConstants.STATUS_BLUETOOTH_DISCONNCETED);
                    AlertUtil.showShortUrgeAlert(BluetoothWalletManageActivity.this, "Bio Disconnected");
                    showDisconnectedLayout();
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
