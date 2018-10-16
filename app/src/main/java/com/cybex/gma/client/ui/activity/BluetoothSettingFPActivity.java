package com.cybex.gma.client.ui.activity;

import android.graphics.Color;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

import com.cybex.componentservice.db.dao.WalletEntityDao;
import com.cybex.componentservice.db.entity.WalletEntity;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.job.BluetoothConnectKeepJob;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.utils.bluetooth.BlueToothWrapper;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 设置指纹识别窗口
 *
 * Created by wanglin on 2018/9/18.
 */
public class BluetoothSettingFPActivity extends XActivity {

    @BindView(R.id.imv_finger_print) ImageView imvFingerPrint;
    private BlueToothWrapper enrollFPThread;
    private FPHandler mHandler;
    private Bundle bd;
    private long contextHandle = 0;
    public int stage = 1;
    public HashMap<Integer, AnimatedVectorDrawable> vectorDrawableHashMap = new HashMap<>();
    public AnimatedVectorDrawable bezierAnimator;


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


    /**
     * 处理指纹逻辑
     *
     * @param state
     */
    private void doFPLogic(int state) {
        LoggerManager.d("FP state: " + state);
        if (state == ParamConstants.FINGER_GOOD) {
            drawFingerprint();
        } else if (state == ParamConstants.FINGER_SUCCESS) {
            stage = 1;
            imvFingerPrint.setImageResource(R.drawable.bezier_svg);
            GemmaToastUtils.showShortToast(getString(R.string.finger_set_success));
            setCurrentWalletStatus();
            UISkipMananger.launchHome(BluetoothSettingFPActivity.this);
            finish();
        }

    }

    private void setCurrentWalletStatus() {
        WalletEntityDao dao = DBManager.getInstance().getWalletEntityDao();
        WalletEntity entity = dao.getCurrentWalletEntity();
        entity.setIsSetBluetoothFP(1);
        dao.saveOrUpateEntity(entity);
    }

    public void initVectorDrawable() {
        vectorDrawableHashMap.put(1,
                (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.bezier_anim01, null));
        vectorDrawableHashMap.put(2,
                (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.bezier_anim02, null));
        vectorDrawableHashMap.put(3,
                (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.bezier_anim03, null));
        vectorDrawableHashMap.put(4,
                (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.bezier_anim04, null));
        vectorDrawableHashMap.put(5,
                (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.bezier_anim05, null));
        vectorDrawableHashMap.put(6,
                (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.bezier_anim06, null));
        vectorDrawableHashMap.put(7,
                (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.bezier_anim07, null));
        vectorDrawableHashMap.put(8,
                (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.bezier_anim08, null));
        vectorDrawableHashMap.put(9,
                (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.bezier_anim09, null));
        vectorDrawableHashMap.put(10,
                (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.bezier_anim10, null));
        vectorDrawableHashMap.put(11,
                (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.bezier_anim11, null));
        vectorDrawableHashMap.put(12,
                (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.bezier_anim12, null));

    }

    private void drawFingerprint() {
        if (bezierAnimator != null && bezierAnimator.isRunning()) {
            return;
        }
        bezierAnimator = vectorDrawableHashMap.get(stage);
        if (bezierAnimator != null) {
            imvFingerPrint.setImageDrawable(bezierAnimator);
            bezierAnimator.start();
        }

        stage++;
    }

    @Override
    public void bindUI(View rootView) {
        ButterKnife.bind(this);

        mHandler = new FPHandler();
        bd = getIntent().getExtras();
        if (bd != null){
            contextHandle = bd.getLong(ParamConstants.CONTEXT_HANDLE);
        }
        //初始化指纹动画
        initVectorDrawable();

        if ((enrollFPThread == null) || (enrollFPThread.getState() == Thread.State.TERMINATED)) {
            enrollFPThread = new BlueToothWrapper(mHandler);
            enrollFPThread.setEnrollFPWrapper(contextHandle, 0);
            enrollFPThread.start();
        }
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setNavibarTitle(getString(R.string.title_setting_fp), false);
        mTitleBar.setActionTextColor(Color.WHITE);
        mTitleBar.setActionTextSize(18);
        mTitleBar.addAction(new TitleBar.TextAction(getString(R.string.btn_title_skip)) {
            @Override
            public void performAction(View view) {
                UISkipMananger.launchHome(BluetoothSettingFPActivity.this);
                finish();
            }
        });

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_bluetooth_setting_fp;
    }

    @Override
    public Object newP() {
        return null;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        BluetoothConnectKeepJob.removeJob();
    }
}
