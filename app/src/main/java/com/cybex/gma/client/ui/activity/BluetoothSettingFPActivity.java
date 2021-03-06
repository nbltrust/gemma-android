//package com.cybex.gma.client.ui.activity;
//
//import android.graphics.Color;
//import android.graphics.drawable.AnimatedVectorDrawable;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.view.Gravity;
//import android.view.View;
//import android.webkit.ValueCallback;
//import android.webkit.WebSettings;
//import android.webkit.WebView;
//import android.widget.ImageView;
//
//import com.cybex.componentservice.db.dao.MultiWalletEntityDao;
//import com.cybex.componentservice.db.dao.WalletEntityDao;
//import com.cybex.componentservice.db.entity.MultiWalletEntity;
//import com.cybex.componentservice.db.entity.WalletEntity;
//import com.cybex.componentservice.manager.DBManager;
//import com.cybex.componentservice.manager.LoggerManager;
//import com.cybex.gma.client.R;
//import com.cybex.gma.client.config.ParamConstants;
//import com.cybex.gma.client.job.BluetoothConnectKeepJob;
//import com.cybex.gma.client.manager.UISkipMananger;
//import com.cybex.gma.client.manager.WookongBioManager;
//import com.cybex.gma.client.utils.bluetooth.BlueToothWrapper;
//import com.hxlx.core.lib.mvp.lite.XActivity;
//import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
//import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
//import com.siberiadante.customdialoglib.CustomDialog;
//
//import java.util.HashMap;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//
///**
// * 设置指纹识别窗口
// *
// * Created by wanglin on 2018/9/18.
// */
//public class BluetoothSettingFPActivity extends XActivity {
//
//    @BindView(R.id.imv_finger_print) ImageView imvFingerPrint;
//    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
//    @BindView(R.id.webView_fingerprints) WebView mWebView;
//    private BlueToothWrapper enrollFPThread;
//    private FPHandler mHandler;
//    private Bundle bd;
//    private long contextHandle = 0;
//    public int stage = 1;
//    public HashMap<Integer, AnimatedVectorDrawable> vectorDrawableHashMap = new HashMap<>();
//    public AnimatedVectorDrawable bezierAnimator;
//
//
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
//
//
//    /**
//     * 处理指纹逻辑
//     *
//     * @param state
//     */
//    private void doFPLogic(int state) {
//        LoggerManager.d("FP state: " + state);
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
//
//
//    }
//
//    private void setCurrentWalletStatus() {
//        MultiWalletEntityDao dao = DBManager.getInstance().getMultiWalletEntityDao();
//        MultiWalletEntity entity = dao.getCurrentMultiWalletEntity();
//        entity.setIsSetBluetoothFP(1);
//        dao.saveOrUpateEntitySync(entity);
//    }
//
//    public void initVectorDrawable() {
//        vectorDrawableHashMap.put(1,
//                (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.eos_bezier_anim01, null));
//        vectorDrawableHashMap.put(2,
//                (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.eos_bezier_anim02, null));
//        vectorDrawableHashMap.put(3,
//                (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.eos_bezier_anim03, null));
//        vectorDrawableHashMap.put(4,
//                (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.eos_bezier_anim04, null));
//        vectorDrawableHashMap.put(5,
//                (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.eos_bezier_anim05, null));
//        vectorDrawableHashMap.put(6,
//                (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.eos_bezier_anim06, null));
//        vectorDrawableHashMap.put(7,
//                (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.eos_bezier_anim07, null));
//        vectorDrawableHashMap.put(8,
//                (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.eos_bezier_anim08, null));
//        vectorDrawableHashMap.put(9,
//                (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.eos_bezier_anim09, null));
//        vectorDrawableHashMap.put(10,
//                (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.eos_bezier_anim10, null));
//        vectorDrawableHashMap.put(11,
//                (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.eos_bezier_anim11, null));
//        vectorDrawableHashMap.put(12,
//                (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.eos_bezier_anim12, null));
//
//    }
//
//    private void drawFingerprint() {
//        mWebView.post(new Runnable() {
//            @Override
//            public void run() {
//                mWebView.evaluateJavascript("finger:next()", new ValueCallback<String>() {
//                    @Override
//                    public void onReceiveValue(String value) {
//
//                    }
//                });
//            }
//        });
//
//        /*
//        if (bezierAnimator != null && bezierAnimator.isRunning()) {
//            return;
//        }
//        bezierAnimator = vectorDrawableHashMap.get(stage);
//        if (bezierAnimator != null) {
//            imvFingerPrint.setImageDrawable(bezierAnimator);
//            bezierAnimator.start();
//        }
//
//        stage++;
//        */
//
//    }
//
//    @Override
//    public void bindUI(View rootView) {
//        ButterKnife.bind(this);
//
//        mHandler = new FPHandler();
//        bd = getIntent().getExtras();
//        if (bd != null) {
//            contextHandle = bd.getLong(ParamConstants.CONTEXT_HANDLE);
//        }
//        //初始化指纹动画
//        //initVectorDrawable();
//        initWebView();
//
//        if ((enrollFPThread == null) || (enrollFPThread.getState() == Thread.State.TERMINATED)) {
//            enrollFPThread = new BlueToothWrapper(mHandler);
//            enrollFPThread.setEnrollFPWrapper(contextHandle, 0);
//            enrollFPThread.start();
//        }
//    }
//
//    @Override
//    public void initData(Bundle savedInstanceState) {
//        setNavibarTitle(getString(R.string.eos_title_setting_fp), false);
//        mTitleBar.setActionTextColor(Color.BLACK);
//        mTitleBar.setActionTextSize(18);
//        mTitleBar.addAction(new TitleBar.TextAction(getString(R.string.eos_btn_title_skip)) {
//            @Override
//            public void performAction(View view) {
//                UISkipMananger.launchHome(BluetoothSettingFPActivity.this);
//                finish();
//            }
//        });
//    }
//
//    @Override
//    public int getLayoutId() {
//        return R.layout.eos_activity_bluetooth_setting_fp;
//    }
//
//    @Override
//    public Object newP() {
//        return null;
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//       //WookongBioManager.getInstance().stopHeartBeat();
//        BluetoothConnectKeepJob.removeJob();
//    }
//
//    public void initWebView(){
//        WebSettings webSettings = mWebView.getSettings();
//        // 设置与Js交互的权限
//        webSettings.setJavaScriptEnabled(true);
//        //载入JS代码
//        mWebView.evaluateJavascript("file:///android_asset/finger.html", new ValueCallback<String>() {
//            @Override
//            public void onReceiveValue(String value) {
//                //JS返回的字符串
//            }
//        });
//    }
//
//
//}
