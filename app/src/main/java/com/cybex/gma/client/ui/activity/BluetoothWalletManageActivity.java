package com.cybex.gma.client.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.cybex.gma.client.R;
import com.cybex.gma.client.event.ContextHandleEvent;
import com.cybex.gma.client.manager.LoggerManager;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.fragment.BluetoothTransferFragment;
import com.cybex.gma.client.utils.AlertUtil;
import com.cybex.gma.client.utils.bluetooth.BlueToothWrapper;
import com.extropies.common.MiddlewareInterface;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.hxlx.core.lib.utils.common.utils.AppManager;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;

import java.util.concurrent.locks.ReentrantLock;

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

    public static int m_authTypeResult = 0;
    public static int m_authTypeChoiceIndex = 0; //-1 means cancel
    public static byte m_authType;
    public static int m_getPINResult;
    public static String m_getPINString;
    private static final int m_minPINLen = 6;
    private static final int m_maxPINLen = 16;

    private static final String m_strDefaultPIN = "12345678";
    private static final String m_strDefaultNewPIN = "88888888";

    private ReentrantLock m_uiLock;
    private BlueToothWrapper signThread;
    private BlueToothWrapper connectThread;
    private long mContextHandle;
    private ConnectHandler mConnectHandler;
    private SignHandler mSignHandler;
    AlertDialog dlg = null;
    private final String deviceName = "WOOKONG BIO####E7:D8:54:5C:33:82";
    @OnClick(R.id.bt_click_to_connect)
    public void startConnect(){
        showProgressDialog("connecting");
        mConnectHandler = new ConnectHandler();
        if ((connectThread == null) || (connectThread.getState() == Thread.State.TERMINATED)) {
            connectThread = new BlueToothWrapper(mConnectHandler);
            connectThread.setInitContextWithDevNameWrapper(BluetoothWalletManageActivity.this,
                    deviceName);
            connectThread.start();
        }
    }

    @OnClick(R.id.layout_wallet_briefInfo)
    public void seeWalletDetail(){
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
        m_uiLock = new ReentrantLock();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_bluetooth_wallet_manage;
    }

    @Override
    public Object newP() {
        return null;
    }

    public void startSign(String transaction){
        mSignHandler = new SignHandler();
        if ((signThread == null) || (signThread.getState() == Thread.State.TERMINATED)) {
            LoggerManager.d("startSign executed");
            int[] derivePath = {0, 0x8000002C, 0x800000c2, 0x80000000, 0x00000000, 0x00000000};
            byte[] testTransaction = {(byte)0x74, (byte)0x09, (byte)0x70, (byte)0xd9, (byte)0xff, (byte)0x01, (byte)
                    0xb5, (byte)0x04, (byte)0x63, (byte)0x2f, (byte)0xed, (byte)0xe1, (byte)0xad, (byte)0xc3, (byte)0xdf, (byte)0xe5, (byte)0x59, (byte)0x90, (byte)0x41, (byte)0x5e, (byte)0x4f, (byte)0xde, (byte)0x01, (byte)0xe1, (byte)0xb8, (byte)0xf3, (byte)0x15, (byte)0xf8, (byte)0x13, (byte)0x6f, (byte)0x47, (byte)0x6c, (byte)0x14, (byte)0xc2, (byte)0x67, (byte)0x5b, (byte)0x01, (byte)0x24, (byte)0x5f, (byte)0x70, (byte)0x5d, (byte)0xd7, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0xa6, (byte)0x82, (byte)0x34, (byte)0x03, (byte)0xea, (byte)0x30, (byte)0x55, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x57, (byte)0x2d, (byte)0x3c, (byte)0xcd, (byte)0xcd, (byte)0x01, (byte)0x20, (byte)0x29, (byte)0xc2, (byte)0xca, (byte)0x55, (byte)0x7a, (byte)0x73, (byte)0x57, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xa8, (byte)0xed, (byte)0x32, (byte)0x32, (byte)0x21, (byte)0x20, (byte)0x29, (byte)0xc2, (byte)0xca, (byte)0x55, (byte)0x7a, (byte)0x73, (byte)0x57, (byte)0x90, (byte)0x55, (byte)0x8c, (byte)0x86, (byte)0x77, (byte)0x95, (byte)0x4c, (byte)0x3c, (byte)0x10, (byte)0x27, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x04, (byte)0x45, (byte)0x4f, (byte)0x53, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};
            signThread = new BlueToothWrapper(mSignHandler);
            byte[] transaction_bytes = transaction.getBytes();
            //todo 传入正确的参数
            int mDevIndex = 0;
            LoggerManager.d("ContextHandle at Set Wrapper", mContextHandle);
            m_uiLock.lock();
            signThread.setEOSSignWrapper(mContextHandle, mDevIndex, m_uiLock, derivePath, testTransaction);

            signThread.start();
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
                        dissmisProgressDialog();

                        mContextHandle = returnValue.getContextHandle();
                        LoggerManager.d("mContextHandle at Post", mContextHandle);
                        GemmaToastUtils.showShortToast("Connected");

                        ContextHandleEvent event = new ContextHandleEvent();
                        event.setContextHanle(mContextHandle);
                        EventBusProvider.postSticky(event);

                        //startSign("");
                        AppManager.getAppManager().finishActivity();
                        UISkipMananger.launchHomeSingle(BluetoothWalletManageActivity.this);

                    }else {
                        //连接超时或失败
                        dissmisProgressDialog();
                        AlertUtil.showShortUrgeAlert(BluetoothWalletManageActivity.this, "Connect Fail");
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
                default:
                    break;
            }
        }
    }

    class SignHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BlueToothWrapper.MSG_EOS_SIGN_START:
                    LoggerManager.d("MSG_EOS_SIGN_START");
                    break;
                case BlueToothWrapper.MSG_EOS_SIGN_FINISH:
                    LoggerManager.d("MSG_EOS_SIGN_FINISH");
                    BlueToothWrapper.SignReturnValue returnValue = (BlueToothWrapper.SignReturnValue)msg.obj;
                    LoggerManager.d("Return Value: " + MiddlewareInterface.getReturnString(returnValue
                            .getReturnValue()));

                    if (returnValue.getReturnValue() == MiddlewareInterface.PAEW_RET_SUCCESS) {
                        LoggerManager.d("Sign Success");
                        String strSignature = new String(returnValue.getSignature());
                        LoggerManager.d("\nSignature Value: " + strSignature);
                    }else {
                        LoggerManager.d("Sign Fail");
                    }

                    break;
                case BlueToothWrapper.MSG_GET_AUTH_TYPE:
                    //显示选择验证方式dialog
                final String[] authTypeString = {"Sign by Finger Print", "Sign by PIN"};
                final byte[] authTypes = {MiddlewareInterface.PAEW_SIGN_AUTH_TYPE_FP, MiddlewareInterface.PAEW_SIGN_AUTH_TYPE_PIN};
                m_authTypeChoiceIndex = 0;

                dlg = new AlertDialog.Builder(BluetoothWalletManageActivity.this)
                        .setIcon(R.mipmap.app_icon)
                        .setTitle("Please Select Sign Type:")
                        .setSingleChoiceItems(authTypeString, m_authTypeChoiceIndex, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                m_authTypeChoiceIndex = which;
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                m_authTypeResult = MiddlewareInterface.PAEW_RET_DEV_OP_CANCEL;
                                m_uiLock.unlock();
                            }
                        })
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                m_authType = authTypes[m_authTypeChoiceIndex];
                                m_authTypeResult = MiddlewareInterface.PAEW_RET_SUCCESS;

                                if (m_authType == MiddlewareInterface.PAEW_SIGN_AUTH_TYPE_PIN) {
                                    Message msg = mSignHandler.obtainMessage();
                                    msg.what = BlueToothWrapper.MSG_GET_USER_PIN;
                                    msg.sendToTarget();
                                } else {
                                    m_uiLock.unlock();
                                }
                            }
                        })
                        .setCancelable(false)
                        .create();
                dlg.show();
                break;
                case BlueToothWrapper.MSG_GET_USER_PIN:
                    View dlgView = getLayoutInflater().inflate(R.layout.ui_dlg_verify_pin, null);
                    final EditText editPIN = dlgView.findViewById(R.id.edit_pin);
                    editPIN.setText(m_strDefaultPIN);
                    editPIN.selectAll();
                    dlg = new AlertDialog.Builder(BluetoothWalletManageActivity.this)
                            .setIcon(R.mipmap.app_icon)
                            .setTitle("Please Input PIN:")
                            .setView(dlgView).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    m_getPINResult = MiddlewareInterface.PAEW_RET_DEV_OP_CANCEL;
                                    m_uiLock.unlock();
                                }
                            })
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int PINLength = editPIN.getText().toString().length();
                                    if ((PINLength < m_minPINLen) || (PINLength > m_maxPINLen)) {
                                        Toast toast = Toast.makeText(getApplicationContext(), "Please input PIN between " + m_minPINLen + " and " + m_maxPINLen, Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                        return;
                                    }

                                    m_getPINResult = MiddlewareInterface.PAEW_RET_SUCCESS;
                                    m_getPINString = editPIN.getText().toString();
                                    m_uiLock.unlock();
                                }
                            })
                            .setCancelable(false)
                            .create();
                    dlg.show();
                    break;

                default:
                    break;
            }

        }
    }

}
