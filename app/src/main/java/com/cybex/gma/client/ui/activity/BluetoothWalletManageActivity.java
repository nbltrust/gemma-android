package com.cybex.gma.client.ui.activity;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.allen.library.SuperTextView;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.CacheConstants;
import com.cybex.gma.client.event.ContextHandleEvent;
import com.cybex.gma.client.manager.LoggerManager;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.utils.AlertUtil;
import com.cybex.gma.client.utils.bluetooth.BlueToothWrapper;
import com.extropies.common.MiddlewareInterface;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.hxlx.core.lib.utils.SPUtils;
import com.hxlx.core.lib.utils.common.utils.AppManager;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.kaopiz.kprogresshud.KProgressHUD;

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
    @BindView(R.id.superTextView_change_FP) SuperTextView superTextViewChangeFP;
    @BindView(R.id.view_bio_management) LinearLayout viewBioManagement;
    @BindView(R.id.bt_disconnect) Button btDisconnect;

    private BlueToothWrapper connectThread;
    private BlueToothWrapper disconnectThread;
    private long mContextHandle;
    private ConnectHandler mConnectHandler;
    private int mDevIndex;
    private final String deviceName = "WOOKONG BIO####E7:D8:54:5C:33:82";

    @OnClick(R.id.bt_click_to_connect)
    public void startConnect() {
        showProgressDialog("connecting");
        mConnectHandler = new ConnectHandler();
        if ((connectThread == null) || (connectThread.getState() == Thread.State.TERMINATED)) {
            connectThread = new BlueToothWrapper(mConnectHandler);
            connectThread.setInitContextWithDevNameWrapper(BluetoothWalletManageActivity.this,
                    deviceName);
            connectThread.start();
        }
    }

    @OnClick(R.id.bt_disconnect)
    public void disconnect(){
        if ((disconnectThread == null) || (disconnectThread.getState() == Thread.State.TERMINATED))
        {
            disconnectThread = new BlueToothWrapper(mConnectHandler);
            disconnectThread.setFreeContextWrapper(mContextHandle);
            disconnectThread.start();
            mContextHandle = 0;
            mDevIndex = MiddlewareInterface.INVALID_DEV_INDEX;
        }
    }

    @OnClick(R.id.layout_wallet_briefInfo)
    public void seeWalletDetail() {
        Bundle bundle = new Bundle();
        UISkipMananger.skipBluetoothWalletDetailActivity(BluetoothWalletManageActivity.this, bundle);
    }

    @Override
    public void bindUI(View rootView) {
        setNavibarTitle(getString(R.string.title_wookong_bio), true);
        ButterKnife.bind(this);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mDevIndex = 0;
        checkConnection();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_bluetooth_wallet_manage;
    }

    @Override
    public Object newP() {
        return null;
    }

    /**
     * 显示蓝牙卡连接时UI
     */
    public void showConnectedLayout() {
        viewBioManagement.setVisibility(View.VISIBLE);
        btDisconnect.setVisibility(View.VISIBLE);
        btClickToConnect.setVisibility(View.GONE);
    }

    /**
     * 显示蓝牙卡断开连接时UI
     */
    public void showDisconnectedLayout() {
        viewBioManagement.setVisibility(View.GONE);
        btDisconnect.setVisibility(View.GONE);
        btClickToConnect.setVisibility(View.VISIBLE);
    }

    /**
     * 每次进入页面检查是否连接
     */
    public void checkConnection(){
        int status = SPUtils.getInstance().getInt("isBioConnected");
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
                        SPUtils.getInstance().put("isBioConnected", CacheConstants.STATUS_BLUETOOTH_CONNCETED);
                        dissmisProgressDialog();

                        mContextHandle = returnValue.getContextHandle();
                        AlertUtil.showShortCommonAlert(BluetoothWalletManageActivity.this, "Bio Connected");
                        showConnectedLayout();

                        ContextHandleEvent event = new ContextHandleEvent();
                        event.setContextHanle(mContextHandle);
                        EventBusProvider.postSticky(event);


                    } else {
                        //连接超时或失败
                        dissmisProgressDialog();
                        AlertUtil.showShortUrgeAlert(BluetoothWalletManageActivity.this, "Connect Fail");
                    }

                    connectThread.interrupt();
                    break;
                case BlueToothWrapper.MSG_FREE_CONTEXT_START:
                    LoggerManager.d("MSG_FREE_CONTEXT_START");
                    break;
                case BlueToothWrapper.MSG_FREE_CONTEXT_FINISH:
                    LoggerManager.d("MSG_FREE_CONTEXT_FINISH");
                    SPUtils.getInstance().put("isBioConnected", CacheConstants.STATUS_BLUETOOTH_DISCONNCETED);
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
