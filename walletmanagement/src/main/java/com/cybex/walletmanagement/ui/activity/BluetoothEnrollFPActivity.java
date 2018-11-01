package com.cybex.walletmanagement.ui.activity;

import android.graphics.Color;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.cybex.componentservice.db.dao.MultiWalletEntityDao;
import com.cybex.componentservice.db.dao.WalletEntityDao;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.db.entity.WalletEntity;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.componentservice.utils.bluetooth.BlueToothWrapper;
import com.cybex.walletmanagement.R;
import com.cybex.walletmanagement.config.WalletManageConst;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;

import java.util.HashMap;

/**
 * 录入指纹窗口
 */
public class BluetoothEnrollFPActivity extends XActivity {


    public int stage = 1;
    public HashMap<Integer, AnimatedVectorDrawable> vectorDrawableHashMap = new HashMap<>();
    TitleBar btnNavibar;
    WebView mWebView;
    private BlueToothWrapper enrollFPThread;
    private FPHandler mHandler;
    private Bundle bd;
    private long contextHandle = 0;

    @Override
    public void bindUI(View rootView) {

        btnNavibar = findViewById(R.id.btn_navibar);
        mWebView = findViewById(R.id.webView_fingerprints);


        mHandler = new FPHandler();
        bd = getIntent().getExtras();
        if (bd != null) {
            contextHandle = bd.getLong(WalletManageConst.CONTEXT_HANDLE);
        }
        //初始化指纹动画
        //initVectorDrawable();
        initWebView();

        if ((enrollFPThread == null) || (enrollFPThread.getState() == Thread.State.TERMINATED)) {
            enrollFPThread = new BlueToothWrapper(mHandler);
            enrollFPThread.setEnrollFPWrapper(contextHandle, 0);
            enrollFPThread.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //WookongBioManager.getInstance().stopHeartBeat();
        //BluetoothConnectKeepJob.removeJob();
    }

    /**
     * 处理指纹逻辑
     *
     * @param state
     */
    private void doFPLogic(int state) {
        LoggerManager.d("FP state: " + state);
//        if (stage <= 12){
//            //前12次录入
//            if (state == ParamConstants.FINGER_GOOD) {
//                drawFingerprint();
//            } else if (state == ParamConstants.FINGER_SUCCESS) {
//                stage = 1;
//                imvFingerPrint.setImageResource(R.drawable.eos_bezier_svg);
//                GemmaToastUtils.showShortToast(getString(R.string.eos_finger_set_success));
//                setCurrentWalletStatus();
//                UISkipMananger.launchHome(BluetoothSettingFPActivity.this);
//                finish();
//            }
//        }else {
//            //12-16次录入，每次都有可能成功
//            if (state == ParamConstants.FINGER_GOOD) {
//                drawFingerprint();
//            } else if (state == ParamConstants.FINGER_SUCCESS) {
//                stage = 1;
//                imvFingerPrint.setImageResource(R.drawable.eos_bezier_svg);
//                GemmaToastUtils.showShortToast(getString(R.string.eos_finger_set_success));
//                setCurrentWalletStatus();
//                UISkipMananger.launchHome(BluetoothSettingFPActivity.this);
//                finish();
//            }
//        }


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
                mWebView.evaluateJavascript("finger:next()", new ValueCallback<String>() {
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
        setNavibarTitle(getString(R.string.walletmanage_title_setting_fp), false);
        mTitleBar.setActionTextColor(Color.WHITE);
        mTitleBar.setActionTextSize(18);
        mTitleBar.addAction(new TitleBar.TextAction(getString(R.string.walletmanage_btn_title_skip)) {
            @Override
            public void performAction(View view) {
                //UISkipMananger.launchHome(BluetoothSettingFPActivity.this);
                //finish();
            }
        });
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
        mWebView.evaluateJavascript("file:///android_asset/finger.html", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                //JS返回的字符串
            }
        });


    }

    class FPHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BlueToothWrapper.MSG_ENROLL_UPDATE:
                    int state = msg.arg1;
                    doFPLogic(state);
                    break;
                default:
                    break;

            }

        }
    }
}
