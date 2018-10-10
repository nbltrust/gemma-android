package com.cybex.gma.client.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.allen.library.SuperTextView;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.CacheConstants;
import com.cybex.gma.client.event.ContextHandleEvent;
import com.cybex.gma.client.job.BluetoothConnectKeepJob;
import com.cybex.gma.client.manager.LoggerManager;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.model.vo.BluetoothDeviceVO;
import com.cybex.gma.client.utils.AlertUtil;
import com.cybex.gma.client.utils.bluetooth.BlueToothWrapper;
import com.extropies.common.MiddlewareInterface;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.SPUtils;
import com.hxlx.core.lib.utils.android.logger.Log;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.readystatesoftware.chuck.internal.ui.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BluetoothWalletDetailActivity extends XActivity {

    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.superTextView_bWallet_name) SuperTextView superTextViewBWalletName;
    @BindView(R.id.tv_publicKey) TextView tvPublicKey;
    @BindView(R.id.superTextView_battery_life) SuperTextView superTextViewBatteryLife;
    @BindView(R.id.bt_to_connect) Button btToConnect;
    @BindView(R.id.bt_cancel_pair) Button btCancelPair;
    @BindView(R.id.bt_format) Button btFormat;

    private long contextHandle;
    private BlueToothWrapper connectThread;
    private ConnectHandler mConnectHandler;
    private final String deviceName = "WOOKONG BIO####E7:D8:54:5C:33:82";

    @OnClick(R.id.bt_to_connect)
    public void startConnect(){
        showProgressDialog("connecting");
        mConnectHandler = new ConnectHandler();
        if ((connectThread == null) || (connectThread.getState() == Thread.State.TERMINATED)) {
            connectThread = new BlueToothWrapper(mConnectHandler);
            connectThread.setInitContextWithDevNameWrapper(this,
                    deviceName);
            connectThread.start();
        }
    }

    @Override
    public void bindUI(View rootView) {
        ButterKnife.bind(this);
        setNavibarTitle(getString(R.string.title_wookong_bio), true);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        checkConnection();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_bluetooth_wallet_detail;
    }

    @Override
    public Object newP() {
        return null;
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

    public void showConnectedLayout(){
        btCancelPair.setVisibility(View.VISIBLE);
        btToConnect.setVisibility(View.GONE);
        btFormat.setVisibility(View.VISIBLE);
    }

    public void showDisconnectedLayout(){
        btCancelPair.setVisibility(View.GONE);
        btToConnect.setVisibility(View.VISIBLE);
        btFormat.setVisibility(View.GONE);

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
                        showConnectedLayout();
                        AlertUtil.showShortCommonAlert(BluetoothWalletDetailActivity.this, "Bio Connected");
                        dissmisProgressDialog();

                        contextHandle = returnValue.getContextHandle();

                        ContextHandleEvent event = new ContextHandleEvent();
                        event.setContextHanle(contextHandle);
                        EventBusProvider.postSticky(event);

                    }else {
                        //连接失败
                        AlertUtil.showShortUrgeAlert(BluetoothWalletDetailActivity.this, "Bio Connect Fail");
                        showDisconnectedLayout();
                    }

                    connectThread.interrupt();
                    break;
                case BlueToothWrapper.MSG_GET_DEV_INFO_FINISH:
                    LoggerManager.d("MSG_GET_DEV_INFO_FINISH");
                    //获得设备信息
                    BlueToothWrapper.GetDevInfoReturnValue reValue = (BlueToothWrapper.GetDevInfoReturnValue) msg.obj;
                    if (reValue.getReturnValue() == MiddlewareInterface.PAEW_RET_SUCCESS) {
                        MiddlewareInterface.PAEW_DevInfo devInfo = reValue.getDeviceInfo();
                        LoggerManager.d("devInfo " + devInfo.ucLifeCycle);
                    }

                    break;

                case BlueToothWrapper.MSG_FREE_CONTEXT_START:
                    break;
                case BlueToothWrapper.MSG_FREE_CONTEXT_FINISH:
                    LoggerManager.d("MSG_FREE_CONTEXT_FINISH");
                    SPUtils.getInstance().put("isBioConnected", CacheConstants.STATUS_BLUETOOTH_DISCONNCETED);
                    AlertUtil.showShortUrgeAlert(BluetoothWalletDetailActivity.this, "Bio Disconnected");
                    showDisconnectedLayout();
                    break;
                default:
                    break;
            }
        }
    }


}
