package com.cybex.walletmanagement.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.cybex.componentservice.config.BaseConst;
import com.cybex.componentservice.config.RouterConst;
import com.cybex.componentservice.db.dao.MultiWalletEntityDao;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.DeviceOperationManager;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.componentservice.utils.bluetooth.BlueToothWrapper;
import com.cybex.walletmanagement.R;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.siberiadante.customdialoglib.CustomDialog;

/**
 * 录入指纹窗口
 */
@Route(path = RouterConst.PATH_TO_WALLET_ENROOL_FP_PAGE)
public class BluetoothEnrollFPActivity extends XActivity {

    @Autowired(name = BaseConst.KEY_INIT_TYPE)
    int initType; //0 是配对设备添加指纹入口, 1 则是初始化指纹入口


    //指纹指令错误
    int FINGER_PRINT_COMMAND_ERROR = -2147483599;
    //指纹冗余
    int FINGER_REDUNDANT = -2147483598;
    //指纹录入成功
    int FINGER_GOOD = -2147483597;
    //指纹录入失败
    int FINGER_NOT = -2147483596;
    //指纹采集不全
    int FINGER_NOT_FULL = -2147483595;
    //指纹采集错误图片
    int FINGER_PRINT_BAND_IMAGE = -2147483594;
    //指纹录入成功
    int FINGER_SUCCESS = 0;

    public int stage = 1;
    //    public HashMap<Integer, AnimatedVectorDrawable> vectorDrawableHashMap = new HashMap<>();
    TitleBar btnNavibar;
    WebView mWebView;
    private BlueToothWrapper enrollFPThread;
    //    private FPHandler mHandler;
    private Bundle bd;
    boolean isEnrolling;
//    private long contextHandle = 0;

    @Override
    public void bindUI(View rootView) {

        btnNavibar = findViewById(R.id.btn_navibar);
        mWebView = findViewById(R.id.webView_fingerprints);


        //初始化指纹动画
        //initVectorDrawable();
        initWebView();

//        if ((enrollFPThread == null) || (enrollFPThread.getState() == Thread.State.TERMINATED)) {
//            enrollFPThread = new BlueToothWrapper(mHandler);
//            enrollFPThread.setEnrollFPWrapper(contextHandle, 0);
//            enrollFPThread.start();
//        }

        isEnrolling = true;

        DeviceOperationManager.getInstance()
                .enrollFP(this.toString(), DeviceOperationManager.getInstance().getCurrentDeviceName(),
                        new DeviceOperationManager.EnrollFPCallback() {

                            @Override
                            public void onEnrollFPUpate(int state) {
                                if (isDestroyed()) { return; }
                                doFPLogic(state);
                            }

                            @Override
                            public void onEnrollFinish(int state) {
                                if (isDestroyed()) { return; }
                                isEnrolling = false;
                                if (state == FINGER_SUCCESS) {
                                    stage = 1;
                                    GemmaToastUtils.showShortToast(getString(R.string.walletmanage_finger_set_success));
                                    setCurrentWalletStatus();
//                  UISkipMananger.launchHome(BluetoothSettingFPActivity.this);
                                    finish();
                                }
                            }
                        });

    }

    @Override
    public boolean useArouter() {
        return true;
    }

    @Override
    protected void onDestroy() {
        //WookongBioManager.getInstance().stopHeartBeat();
        //BluetoothConnectKeepJob.removeJob();
        DeviceOperationManager.getInstance().clearCallback(this.toString());
        if (isEnrolling) {
            DeviceOperationManager.getInstance()
                    .abortEnrollFp(DeviceOperationManager.getInstance().getCurrentDeviceName());
        }
        super.onDestroy();


    }

    /**
     * 处理指纹逻辑
     *
     * @param state
     */
    private void doFPLogic(int state) {
        LoggerManager.d("FP state: " + state);
        if (stage <= 12) {
            //前12次录入
            if (state == FINGER_GOOD) {
                drawFingerprint();
                stage++;
            }
//            else if (state ==FINGER_SUCCESS) {
//                stage = 1;
////                imvFingerPrint.setImageResource(R.drawable.eos_bezier_svg);
////                GemmaToastUtils.showShortToast(getString(R.string.eos_finger_set_success));
//                setCurrentWalletStatus();
////                UISkipMananger.launchHome(BluetoothSettingFPActivity.this);
////                finish();
//            }
        } else {
            //12-16次录入，每次都有可能成功
            if (state == FINGER_GOOD) {
                drawFingerprint();
                stage++;
            }
//            else if (state == FINGER_SUCCESS) {
//                stage = 1;
////                imvFingerPrint.setImageResource(R.drawable.eos_bezier_svg);
////                GemmaToastUtils.showShortToast(getString(R.string.eos_finger_set_success));
//                setCurrentWalletStatus();
////                UISkipMananger.launchHome(BluetoothSettingFPActivity.this);
////                finish();
//            }
        }


    }

    private void setCurrentWalletStatus() {
        MultiWalletEntityDao dao = DBManager.getInstance().getMultiWalletEntityDao();
        MultiWalletEntity entity = dao.getCurrentMultiWalletEntity();
        entity.setIsSetBluetoothFP(1);
        dao.saveOrUpateEntitySync(entity);
    }

    private void drawFingerprint() {
        mWebView.post(new Runnable() {
            @Override
            public void run() {
                mWebView.evaluateJavascript("next()", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {

                    }
                });
            }
        });
        /*
        if (bezierAnimator != null && bezierAnimator.isRunning()) {
            return;
        }
        bezierAnimator = vectorDrawableHashMap.get(stage);
        if (bezierAnimator != null) {
            imvFingerPrint.setImageDrawable(bezierAnimator);
            bezierAnimator.start();
        }

        stage++;
        */
    }


    @Override
    public void initData(Bundle savedInstanceState) {
        if (initType == 1) {
            setNavibarTitle(getString(R.string.walletmanage_title_add_fp), true);
        } else {
            setNavibarTitle(getString(R.string.walletmanage_title_setting_fp), false);
            mTitleBar.addAction(new TitleBar.TextAction(getString(R.string.walletmanage_btn_title_skip)) {
                @Override
                public void performAction(View view) {
                    String deviceName = DBManager.getInstance().getMultiWalletEntityDao().getCurrentMultiWalletEntity
                            ().getBluetoothDeviceName();
                    DeviceOperationManager.getInstance().abortEnrollFp(deviceName);
                    finish();
                }
            });
        }
        mTitleBar.setActionTextColor(Color.BLACK);
        mTitleBar.setActionTextSize(18);

    }

    @Override
    public int getLayoutId() {
        return R.layout.walletmanage_activity_bluetooth_enroll_fp;
    }

    @Override
    public Object newP() {
        return null;
    }

    public void initWebView() {
        WebSettings webSettings = mWebView.getSettings();
        // 设置与Js交互的权限
        webSettings.setJavaScriptEnabled(true);
        //载入JS代码
//        mWebView.evaluateJavascript("file:///android_asset/finger.html", new ValueCallback<String>() {
//            @Override
//            public void onReceiveValue(String value) {
//                //JS返回的字符串
//            }
//        });

        mWebView.loadUrl("file:///android_asset/finger.html");


    }

    /**
     * 显示录入超时Dialog
     */
    private void showOvertimeDialog() {
        int[] listenedItems = {R.id.tv_jump, R.id.tv_retry};
        CustomDialog dialog = new CustomDialog(this,
                R.layout.walletmanage_dialog_bluetooth_enroll_fp_overtime, listenedItems, false, Gravity.CENTER);
        dialog.setOnDialogItemClickListener(new CustomDialog.OnCustomDialogItemClickListener() {

            @Override
            public void OnCustomDialogItemClick(CustomDialog dialog, View view) {
                if (view.getId() == R.id.tv_jump) {
                    dialog.cancel();
                    ARouter.getInstance().build(RouterConst.PATH_TO_WALLET_HOME)
                            .navigation();
                } else if (view.getId() == R.id.tv_retry) {
                    initWebView();
                    DeviceOperationManager.getInstance()
                            .enrollFP(this.toString(), DeviceOperationManager.getInstance().getCurrentDeviceName(),
                                    new DeviceOperationManager.EnrollFPCallback() {

                                        @Override
                                        public void onEnrollFPUpate(int state) {
                                            if (isDestroyed()) { return; }
                                            doFPLogic(state);
                                        }

                                        @Override
                                        public void onEnrollFinish(int state) {
                                            if (isDestroyed()) { return; }
                                            isEnrolling = false;
                                            if (state == FINGER_SUCCESS) {
                                                stage = 1;
                                                GemmaToastUtils.showShortToast(
                                                        getString(R.string.walletmanage_finger_set_success));
                                                setCurrentWalletStatus();
                                                ARouter.getInstance().build(RouterConst.PATH_TO_WALLET_HOME)
                                                        .navigation();
                                                finish();
                                            }
                                        }
                                    });
                }

            }
        });
        dialog.show();
    }

//    class FPHandler extends Handler {
//
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case BlueToothWrapper.MSG_ENROLL_UPDATE:
//                    int state = msg.arg1;
//                    doFPLogic(state);
//                    break;
//                default:
//                    break;
//
//            }
//
//        }
//    }
}
