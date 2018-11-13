package com.cybex.walletmanagement.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;

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
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.SPUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.siberiadante.customdialoglib.CustomDialog;
import com.siberiadante.customdialoglib.CustomFullDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class BluetoothWalletDetailFragment extends XFragment {

    TitleBar btnNavibar;
    SuperTextView superTextViewBWalletName;
    SuperTextView superTextViewBatteryLife;
    Button btToConnect;
    //Button btCancelPair;
    Button btFormat;

    private long contextHandle;
    private BlueToothWrapper connectThread;
    private BlueToothWrapper formatThread;
    private BlueToothWrapper getAddressThread;
    private BlueToothWrapper verifyFPThread;
    private ConnectHandler mConnectHandler;
    private FreeContextHandler freeContextHandler;
    private String deviceName;
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

        if (getActivity() != null) {
            btnNavibar = getActivity().findViewById(R.id.btn_navibar);
            superTextViewBWalletName = getActivity().findViewById(R.id.superTextView_bWallet_name);
            superTextViewBatteryLife = getActivity().findViewById(R.id.superTextView_battery_life);
            btToConnect = getActivity().findViewById(R.id.bt_to_connect);
            //btCancelPair = getActivity().findViewById(R.id.bt_cancel_pair);
            btFormat = getActivity().findViewById(R.id.bt_format);
        }
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        checkConnection();
        mConnectHandler = new ConnectHandler();
        freeContextHandler = new FreeContextHandler();
        deviceName = SPUtils.getInstance().getString(WalletManageConst.DEVICE_NAME);

        //点击连接
        btToConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog("connecting");
                if ((connectThread == null) || (connectThread.getState() == Thread.State.TERMINATED)) {
                    connectThread = new BlueToothWrapper(mConnectHandler);
                    connectThread.setInitContextWithDevNameWrapper(getActivity(),
                            deviceName);
                    connectThread.start();
                }
            }
        });

        //取消配对
        /*
        btCancelPair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }); */
        //格式化
        btFormat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmFormatDialog();
            }
        });

        //修改钱包名称
        superTextViewBWalletName.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {

            }
        });
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


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void getContextHandle(ContextHandleEvent event) {
        contextHandle = event.getContextHanle();
    }

    /**
     * 开始格式化
     */
    public void startFormat() {
        if ((formatThread == null) || (formatThread.getState() == Thread.State.TERMINATED)) {
            formatThread = new BlueToothWrapper(mConnectHandler);
            formatThread.setFormatDeviceWrapper(contextHandle, 0);
            formatThread.start();
        }
    }

    public void startVerifyFP() {
        if ((verifyFPThread == null) || (verifyFPThread.getState() == Thread.State.TERMINATED)) {
            verifyFPThread = new BlueToothWrapper(mConnectHandler);
            verifyFPThread.setVerifyFPWrapper(contextHandle, 0);
            verifyFPThread.start();
        }
    }

    public void freeContext() {
        if ((connectThread == null) || (connectThread.getState() == Thread.State.TERMINATED)) {
            connectThread = new BlueToothWrapper(freeContextHandler);
            connectThread.setFreeContextWrapper(contextHandle);
            connectThread.start();
        }
    }

    /**
     * 每次进入页面检查是否连接
     */
    public void checkConnection() {
        int status = SPUtils.getInstance().getInt(CacheConstants.BIO_CONNECT_STATUS);
        switch (status) {
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

    public void showConnectedLayout() {
        //btCancelPair.setVisibility(View.VISIBLE);
        btToConnect.setVisibility(View.GONE);
        btFormat.setVisibility(View.VISIBLE);
    }

    public void showDisconnectedLayout() {
        //btCancelPair.setVisibility(View.GONE);
        btToConnect.setVisibility(View.VISIBLE);
        btFormat.setVisibility(View.GONE);
    }


    /**
     * 显示确认格式化Dialog
     */
    private void showConfirmFormatDialog() {
        int[] listenedItems = {R.id.tv_ok, R.id.tv_cancel};
        CustomDialog dialog = new CustomDialog(getActivity(),
                R.layout.walletmanage_dialog_bluetooth_format_confirm, listenedItems, false, Gravity.CENTER);
        dialog.setOnDialogItemClickListener(new CustomDialog.OnCustomDialogItemClickListener() {

            @Override
            public void OnCustomDialogItemClick(final CustomDialog dialog, View view) {
                if (view.getId() == R.id.tv_ok) {
                    startVerifyFP();
                } else if (view.getId() == R.id.tv_cancel) {
                    dialog.cancel();
                }
            }
        });
        dialog.show();
    }

    /**
     * 显示按电源键确认格式化Dialog
     */
    private void showConfirmFormatonCardDialog() {
        int[] listenedItems = {R.id.imv_back};
        CustomDialog dialog = new CustomDialog(getActivity(),
                R.layout.walletmanage_dialog_bluetooth_power_confirm, listenedItems, false, Gravity.CENTER);
        dialog.setOnDialogItemClickListener(new CustomDialog.OnCustomDialogItemClickListener() {

            @Override
            public void OnCustomDialogItemClick(final CustomDialog dialog, View view) {
                if (view.getId() == R.id.imv_back) {
                    dialog.cancel();
                }
            }
        });
        dialog.show();
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

                if (view.getId() == R.id.tv_cancel) {
                    //todo 不再初始化Bio，删除硬件钱包，判断是否还有钱包，跳转
                    dialog.cancel();
                } else if (view.getId() == R.id.tv_init) {
                    dialog.cancel();
                    //UISkipMananger.skipBluetoothPaireActivity(getActivity(), new Bundle());
                }
            }
        });
        dialog.show();
    }


    class FreeContextHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BlueToothWrapper.MSG_FREE_CONTEXT_START:
                    break;
                case BlueToothWrapper.MSG_FREE_CONTEXT_FINISH:
                    SPUtils.getInstance()
                            .put(CacheConstants.BIO_CONNECT_STATUS, CacheConstants.STATUS_BLUETOOTH_DISCONNCETED);
                    showFormatDoneDialog();
                    break;
            }
        }
    }

    class ConnectHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BlueToothWrapper.MSG_INIT_CONTEXT_START:
                    //连接开始
                    //LoggerManager.d("MSG_INIT_CONTEXT_START");
                    break;
                case BlueToothWrapper.MSG_INIT_CONTEXT_FINISH:
                    //连接结束
                    //LoggerManager.d("MSG_INIT_CONTEXT_FINISH");
                    BlueToothWrapper.InitContextReturnValue returnValue = (BlueToothWrapper.InitContextReturnValue) msg.obj;
                    if ((returnValue != null) && (returnValue.getReturnValue()
                            == MiddlewareInterface.PAEW_RET_SUCCESS)) {
                        //连接成功

                        SPUtils.getInstance()
                                .put(CacheConstants.BIO_CONNECT_STATUS, CacheConstants.STATUS_BLUETOOTH_CONNCETED);

                        contextHandle = returnValue.getContextHandle();
                        ContextHandleEvent event = new ContextHandleEvent();
                        event.setContextHanle(contextHandle);
                        EventBusProvider.postSticky(event);

                        showConnectedLayout();
                        AlertUtil.showShortCommonAlert(getActivity(), "Bio Connected");

                    } else {
                        //连接失败
                        SPUtils.getInstance()
                                .put(CacheConstants.BIO_CONNECT_STATUS, CacheConstants.STATUS_BLUETOOTH_DISCONNCETED);
                        AlertUtil.showShortUrgeAlert(getActivity(), "Bio Connect Fail");
                        showDisconnectedLayout();
                    }

                    connectThread.interrupt();
                    break;
                case BlueToothWrapper.MSG_GET_DEV_INFO_FINISH:
                    //LoggerManager.d("MSG_GET_DEV_INFO_FINISH");
                    //获得设备信息
                    BlueToothWrapper.GetDevInfoReturnValue reValue = (BlueToothWrapper.GetDevInfoReturnValue) msg.obj;
                    if (reValue.getReturnValue() == MiddlewareInterface.PAEW_RET_SUCCESS) {
                        MiddlewareInterface.PAEW_DevInfo devInfo = reValue.getDeviceInfo();
                        LoggerManager.d("devInfo " + devInfo.ucLifeCycle);
                    }

                    break;

                case BlueToothWrapper.MSG_FREE_CONTEXT_START:
                    //断开连接开始
                    break;
                case BlueToothWrapper.MSG_FREE_CONTEXT_FINISH:
                    //断开连接结束
                    //LoggerManager.d("MSG_FREE_CONTEXT_FINISH");
                    //BluetoothConnectKeepJob.removeJob();
                    SPUtils.getInstance()
                            .put(CacheConstants.BIO_CONNECT_STATUS, CacheConstants.STATUS_BLUETOOTH_DISCONNCETED);
                    AlertUtil.showShortUrgeAlert(getActivity(), "Bio Disconnected");
                    showDisconnectedLayout();

                    break;
                case BlueToothWrapper.MSG_VERIFYFP_START:
                    LoggerManager.d("MSG_VERIFYFP_START");
                    showVerifyFPDialog();
                    //验证指纹开始
                    break;
                case BlueToothWrapper.MSG_VERIFYFP_FINISH:
                    dialog.cancel();
                    startFormat();
                    //验证指纹完成
                    break;
                case BlueToothWrapper.MSG_FORMAT_DEVICE_START:
                    //格式化开始
                    break;
                case BlueToothWrapper.MSG_FORMAT_DEVICE_FINISH:
                    //格式化完成
                    //断开连接
                    freeContext();
                    break;
                default:
                    break;
            }
        }
    }

    CustomFullDialog dialog = null;

    /**
     * 显示通过蓝牙卡验证指纹dialog
     */
    private void showVerifyFPDialog() {
        int[] listenedItems = {R.id.imv_back};
        dialog = new CustomFullDialog(getActivity(),
                R.layout.walletmanage_dialog_bluetooth_finger_authorize, listenedItems, false, Gravity.BOTTOM);
        dialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
            @Override
            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {

                if (view.getId() == R.id.imv_back) {
                    dialog.cancel();
                }
            }
        });
        dialog.show();
    }

}
