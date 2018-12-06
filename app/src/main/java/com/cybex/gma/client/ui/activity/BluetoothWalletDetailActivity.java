//package com.cybex.gma.client.ui.activity;
//
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.view.Gravity;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//
//import com.allen.library.SuperTextView;
//import com.cybex.gma.client.R;
//import com.cybex.componentservice.config.CacheConstants;
//
//import com.cybex.gma.client.config.ParamConstants;
//import com.cybex.gma.client.event.ContextHandleEvent;
//import com.cybex.gma.client.event.DeviceInfoEvent;
//import com.cybex.componentservice.manager.LoggerManager;
//
//import com.cybex.gma.client.job.BluetoothConnectKeepJob;
//import com.cybex.gma.client.manager.UISkipMananger;
//
//import com.cybex.componentservice.utils.AlertUtil;
//import com.cybex.gma.client.utils.bluetooth.BlueToothWrapper;
//import com.extropies.common.MiddlewareInterface;
//import com.hxlx.core.lib.common.eventbus.EventBusProvider;
//import com.hxlx.core.lib.mvp.lite.XActivity;
//import com.hxlx.core.lib.utils.SPUtils;
//import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
//import com.siberiadante.customdialoglib.CustomDialog;
//import com.siberiadante.customdialoglib.CustomFullDialog;
//
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;
//
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import butterknife.OnClick;
//
//public class BluetoothWalletDetailActivity extends XActivity {
//
//    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
//    @BindView(R.id.superTextView_bWallet_name) SuperTextView superTextViewBWalletName;
//    //@BindView(R.id.tv_publicKey) TextView tvPublicKey;
//    @BindView(R.id.superTextView_battery_life) SuperTextView superTextViewBatteryLife;
//    @BindView(R.id.bt_to_connect) Button btToConnect;
//    @BindView(R.id.bt_cancel_pair) Button btCancelPair;
//    @BindView(R.id.bt_format) Button btFormat;
//
//    private long contextHandle;
//    private BlueToothWrapper connectThread;
//    private BlueToothWrapper formatThread;
//    private BlueToothWrapper getAddressThread;
//    private BlueToothWrapper verifyFPThread;
//    private ConnectHandler mConnectHandler;
//    private FreeContextHandler freeContextHandler;
//    private String publicKey;
//    private String deviceName;
//    //private final String deviceName = "WOOKONG BIO####E7:D8:54:5C:33:82";
//
//    /**
//     * 按钮点击事件
//     * @param v
//     */
//    @OnClick({R.id.bt_to_connect, R.id.bt_cancel_pair, R.id.bt_format})
//    public void startConnect(View v){
//        switch (v.getId()){
//            case R.id.bt_to_connect:
//                //点击连接
//                showProgressDialog("connecting");
//                if ((connectThread == null) || (connectThread.getState() == Thread.State.TERMINATED)) {
//                    connectThread = new BlueToothWrapper(mConnectHandler);
//                    connectThread.setInitContextWithDevNameWrapper(this,
//                            deviceName);
//                    connectThread.start();
//                }
//                break;
//            case R.id.bt_cancel_pair:
//                //取消配对
//                break;
//            case R.id.bt_format:
//                //格式化
//                showConfirmFormatDialog();
//                break;
//        }
//    }
//
//    @Override
//    public void bindUI(View rootView) {
//        ButterKnife.bind(this);
//        setNavibarTitle(getString(R.string.eos_title_wookong_bio), true);
//    }
//
//    @Override
//    public void initData(Bundle savedInstanceState) {
//        checkConnection();
//        mConnectHandler = new ConnectHandler();
//        freeContextHandler = new FreeContextHandler();
//        //tvPublicKey.setText(publicKey);
//        deviceName = SPUtils.getInstance().getString(ParamConstants.DEVICE_NAME);
//    }
//
//    @Override
//    public int getLayoutId() {
//        return R.layout.eos_activity_bluetooth_wallet_detail;
//    }
//
//    @Override
//    public Object newP() {
//        return null;
//    }
//
//    @Override
//    public boolean useEventBus() {
//        return true;
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
//    public void onEosAddressRecieved(DeviceInfoEvent event){
//        publicKey = event.getEosPublicKey();
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
//    public void getContextHandle(ContextHandleEvent event){
//        contextHandle = event.getContextHanle();
//    }
//
//    /**
//     * 开始格式化
//     */
//    public void startFormat(){
//        if ((formatThread == null) || (formatThread.getState() == Thread.State.TERMINATED))
//        {
//            formatThread = new BlueToothWrapper(mConnectHandler);
//            formatThread.setFormatDeviceWrapper(contextHandle, 0);
//            formatThread.start();
//        }
//    }
//
//    public void startVerifyFP(){
//        if ((verifyFPThread == null) || (verifyFPThread.getState() == Thread.State.TERMINATED))
//        {
//            verifyFPThread = new BlueToothWrapper(mConnectHandler);
//            verifyFPThread.setVerifyFPWrapper(contextHandle, 0);
//            verifyFPThread.start();
//        }
//    }
//
//    public void freeContext(){
//        if ((connectThread == null) || (connectThread.getState() == Thread.State.TERMINATED)) {
//            connectThread = new BlueToothWrapper(freeContextHandler);
//            connectThread.setFreeContextWrapper(contextHandle);
//            connectThread.start();
//        }
//    }
//
//    /**
//     * 每次进入页面检查是否连接
//     */
//    public void checkConnection(){
//        int status = SPUtils.getInstance().getInt(CacheConstants.BIO_CONNECT_STATUS);
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
//    }
//
//    public void showConnectedLayout(){
//        btCancelPair.setVisibility(View.VISIBLE);
//        btToConnect.setVisibility(View.GONE);
//        btFormat.setVisibility(View.VISIBLE);
//        //tvPublicKey.setText(publicKey);
//    }
//
//    public void showDisconnectedLayout(){
//        btCancelPair.setVisibility(View.GONE);
//        btToConnect.setVisibility(View.VISIBLE);
//        btFormat.setVisibility(View.GONE);
//        //tvPublicKey.setText("");
//
//    }
//
//    /**
//     * 获取EOS地址（公钥）
//     */
//    public void getEosAddress(){
//        //showProgressDialog("Getting Device Information");
//        if ((getAddressThread == null) || (getAddressThread.getState() == Thread.State.TERMINATED))
//        {
//            getAddressThread = new BlueToothWrapper(mConnectHandler);
//            getAddressThread.setGetAddressWrapper(contextHandle, 0, MiddlewareInterface.PAEW_COIN_TYPE_EOS,
//                    CacheConstants.EOS_DERIVE_PATH);
//            getAddressThread.start();
//        }
//    }
//
//    /**
//     * 显示确认格式化Dialog
//     */
//    private void showConfirmFormatDialog() {
//        int[] listenedItems = {R.id.tv_ok, R.id.tv_cancel};
//        CustomDialog dialog = new CustomDialog(this,
//                R.layout.eos_dialog_format_confirm, listenedItems, false, Gravity.CENTER);
//        dialog.setOnDialogItemClickListener(new CustomDialog.OnCustomDialogItemClickListener() {
//
//            @Override
//            public void OnCustomDialogItemClick(CustomDialog dialog, View view) {
//                switch (view.getId()) {
//                    case R.id.tv_cancel:
//                        dialog.cancel();
//                        break;
//                    case R.id.tv_ok:
//                        //todo 确认格式化，调用指纹验证或Pin验证确认
//                        startVerifyFP();
//                        break;
//                    default:
//                        break;
//                }
//            }
//        });
//        dialog.show();
//    }
//
//    /**
//     * 显示确认格式化Dialog
//     */
//    private void showFormatDoneDialog() {
//        int[] listenedItems = {R.id.tv_cancel, R.id.tv_init};
//        CustomDialog dialog = new CustomDialog(this,
//                R.layout.eos_dialog_format_done, listenedItems, false, Gravity.CENTER);
//        dialog.setOnDialogItemClickListener(new CustomDialog.OnCustomDialogItemClickListener() {
//
//            @Override
//            public void OnCustomDialogItemClick(CustomDialog dialog, View view) {
//                switch (view.getId()) {
//                    case R.id.tv_cancel:
//                        //todo 不再初始化Bio，删除硬件钱包，判断是否还有钱包，跳转
//                        dialog.cancel();
//                        break;
//                    case R.id.tv_init:
//                        //todo 初始化Bio 创建or导入
//                        dialog.cancel();
//                        UISkipMananger.skipBluetoothPaireActivity(BluetoothWalletDetailActivity.this, new Bundle());
//                        break;
//                    default:
//                        break;
//                }
//            }
//        });
//        dialog.show();
//    }
//
//
//    class FreeContextHandler extends Handler{
//
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what){
//                case BlueToothWrapper.MSG_FREE_CONTEXT_START:
//                    break;
//                case BlueToothWrapper.MSG_FREE_CONTEXT_FINISH:
//                    SPUtils.getInstance().put(CacheConstants.BIO_CONNECT_STATUS, CacheConstants.STATUS_BLUETOOTH_DISCONNCETED);
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
//                        SPUtils.getInstance().put(CacheConstants.BIO_CONNECT_STATUS, CacheConstants.STATUS_BLUETOOTH_CONNCETED);
//
//                        contextHandle = returnValue.getContextHandle();
//
//                        ContextHandleEvent event = new ContextHandleEvent();
//                        event.setContextHanle(contextHandle);
//                        EventBusProvider.postSticky(event);
//
//                        getEosAddress();
//
//                    }else {
//                        //连接失败
//                        SPUtils.getInstance().put(CacheConstants.BIO_CONNECT_STATUS, CacheConstants.STATUS_BLUETOOTH_DISCONNCETED);
//                        AlertUtil.showShortUrgeAlert(BluetoothWalletDetailActivity.this, "Bio Connect Fail");
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
//                    BluetoothConnectKeepJob.removeJob();
//                    SPUtils.getInstance().put(CacheConstants.BIO_CONNECT_STATUS, CacheConstants.STATUS_BLUETOOTH_DISCONNCETED);
//                    AlertUtil.showShortUrgeAlert(BluetoothWalletDetailActivity.this, "Bio Disconnected");
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
//                    freeContext();
//                    break;
//                case BlueToothWrapper.MSG_GET_ADDRESS_START:
//                    //获取EOS地址（公钥）开始
//                    break;
//                case BlueToothWrapper.MSG_GET_ADDRESS_FINISH:
//                    //获取EOS地址（公钥）结束
//                    BlueToothWrapper.GetAddressReturnValue returnValueAddress = (BlueToothWrapper
//                            .GetAddressReturnValue)
//                            msg.obj;
//                    if (returnValueAddress.getReturnValue() == MiddlewareInterface.PAEW_RET_SUCCESS) {
//                        if (returnValueAddress.getCoinType() == MiddlewareInterface.PAEW_COIN_TYPE_EOS) {
//
//                            //BluetoothConnectKeepJob.startConnectPolling(contextHandle, mConnectHandler, 0);
//                            LoggerManager.d("EOS Address: " + returnValueAddress.getAddress());
//
//                            String[] strArr = returnValueAddress.getAddress().split("####");
//                            String publicKey = strArr[0];
//                            //tvPublicKey.setText(publicKey);
//
//                            DeviceInfoEvent event = new DeviceInfoEvent();
//                            event.setEosPublicKey(publicKey);
//                            EventBusProvider.postSticky(event);
//
//                            showConnectedLayout();
//                            AlertUtil.showShortCommonAlert(BluetoothWalletDetailActivity.this, "Bio Connected");
//
//                        }
//                    }else {
//                        AlertUtil.showShortUrgeAlert(BluetoothWalletDetailActivity.this, "Bio Connect Fail");
//                        showDisconnectedLayout();
//                    }
//                    //LoggerManager.d("Return Value: " + MiddlewareInterface.getReturnString(returnValueAddress
//                        //.getReturnValue()));
//                    dissmisProgressDialog();
//                    break;
//                default:
//                    break;
//            }
//        }
//    }
//
//    CustomFullDialog dialog = null;
//    /**
//     * 显示通过蓝牙卡验证指纹dialog
//     */
//    private void showVerifyFPDialog() {
//        int[] listenedItems = {R.id.imv_back};
//        dialog = new CustomFullDialog(this,
//                R.layout.eos_dialog_transfer_bluetooth_finger_sure, listenedItems, false, Gravity.BOTTOM);
//        dialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
//            @Override
//            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {
//                switch (view.getId()) {
//                    case R.id.imv_back:
//                        dialog.cancel();
//                        break;
//                    default:
//                        break;
//                }
//            }
//        });
//        dialog.show();
//    }
//
//
//}
