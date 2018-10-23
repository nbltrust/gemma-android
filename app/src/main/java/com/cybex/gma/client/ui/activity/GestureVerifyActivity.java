package com.cybex.gma.client.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.cybex.base.view.lockpattern.util.LockPatternUtil;
import com.cybex.base.view.lockpattern.widget.LockPatternView;
import com.cybex.gma.client.R;
import com.cybex.componentservice.config.CacheConstants;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.event.RefreshGestureEvent;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.utils.fingerprint.FingerprintScanHelper;
import com.cybex.gma.client.utils.fingerprint.OnAuthResultListener;
import com.hxlx.core.lib.common.cache.ACache;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.hxlx.core.lib.utils.SPUtils;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 验证手势密码
 *
 * Created by wanglin on 2018/8/17.
 */
public class GestureVerifyActivity extends XActivity {

    private static final String TAG = "LoginGestureActivity";

    @BindView(R.id.lockPatternView)
    LockPatternView lockPatternView;
    @BindView(R.id.messageTv)
    TextView messageTv;
    @BindView(R.id.forgetGestureBtn)
    Button forgetGestureBtn;

    private ACache aCache;
    private byte[] gesturePassword;
    private Animation mShakeAnim;
    private int mFailedPatternAttemptsSinceLastTimeout = 0;
    private CountDownTimer mCountdownTimer = null;

    //定义计时器
    private Timer mTimer;
    private TimerTask mTimerTask;
    //记录是否失败次数超过限制
    private boolean mTimeOut;
    //剩余的等待时间
    private int leftTime;
    private int waitTime = 30;
    //记录是否手势密码处于可用状态
    private boolean mError;
    // 手势密码输入错误超过5次时间
    private static final String GESTURE_TIME = "gesture_time";

    private int gestureType = -1;


    //接受TimerTask消息,通知UI
    private Handler handler = new Handler() {
        @SuppressLint("StringFormatInvalid")
        @Override
        public void handleMessage(Message msg) {
            leftTime--;
            if (leftTime == 0) {
                if (mTimer != null) { mTimerTask.cancel(); }
                mTimeOut = false;
                mError = false;
                //将计时信息还原
                reSet();
                return;
            } else {
                messageTv.setText(String.format(getString(R.string.gesture_error_max_retry), leftTime));
                messageTv.setTextColor(Color.RED);
                lockPatternView.setEnabled(false);
                lockPatternView.setEnableGesture(false);
                lockPatternView.postClearPatternRunnable(LockPatternUtil.DELAYTIME);
            }


            mError = true;
        }
    };

    private void reSet() {
        leftTime = waitTime;
        lockPatternView.setEnabled(true);
        updateStatus(Status.DEFAULT);
        lockPatternView.postClearPatternRunnable(LockPatternUtil.DELAYTIME);
        mFailedPatternAttemptsSinceLastTimeout = 0;
        mTimeOut = false;
        lockPatternView.setEnableGesture(true);
    }

    @Override
    public void bindUI(View rootView) {
        ButterKnife.bind(this);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        Bundle bd = getIntent().getExtras();
        if (bd != null) {
            gestureType = bd.getInt(ParamConstants.GESTURE_SKIP_TYPE);
        }

        this.showFingerVerify();

        aCache = ACache.get(GestureVerifyActivity.this);
        //得到当前用户的手势密码
        gesturePassword = aCache.getAsBinary(CacheConstants.GESTURE_PASSWORD);
        lockPatternView.setOnPatternListener(patternListener);
        updateStatus(Status.DEFAULT);
        mShakeAnim = AnimationUtils.loadAnimation(this, R.anim.gesture_shake);

        mTimer = new Timer();
        //计算上次失败时间与现在的时间差
        try {
            long lastTime = SPUtils.getInstance().getLong(GESTURE_TIME);
            Date date = new Date();
            if (lastTime != 0 && (date.getTime() - lastTime) / 1000 < waitTime) {
                //失败时间未到，还处于锁定状态
                mTimeOut = true;
                leftTime = (int) (waitTime - ((date.getTime() - lastTime)) / 1000);
                mTimerTask = new InnerTimerTask(handler);
                mTimer.schedule(mTimerTask, 0, 1000);
            } else {
                mTimeOut = false;
                leftTime = waitTime;
            }

        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }


    private void showFingerVerify() {
        if (gestureType == ParamConstants.GESTURE_SKIP_TYPE_LOGIN_VERIFY) {
            boolean isOpenFinger = SPUtils.getInstance().getBoolean(CacheConstants.KEY_OPEN_FINGER_PRINT);
            if (isOpenFinger) {
                new FingerprintScanHelper(this)
                        .startAuth(new OnAuthResultListener() {
                            @Override
                            public void onSuccess() {
                                //指纹验证成功，直接跳转到主页
                                UISkipMananger.launchHome(GestureVerifyActivity.this);
                                finish();
                            }

                            @Override
                            public void onInputPwd(String pwd) {

                            }

                            @Override
                            public void onFailed(String msg) {
                                GemmaToastUtils.showShortToast(msg);

                            }

                            @Override
                            public void onDeviceNotSupport() {
                                GemmaToastUtils.showShortToast(
                                        getString(R.string.finger_tip_device_no_support));

                            }
                        }, false, false);
            }

        }

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            EventBusProvider.post(new RefreshGestureEvent());

            return super.onKeyDown(keyCode, event);
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }

    @Override
    public int getLayoutId() {
        return R.layout.eos_activity_gesture_login;
    }

    @Override
    public Object newP() {
        return null;
    }


    private LockPatternView.OnPatternListener patternListener = new LockPatternView.OnPatternListener() {

        @Override
        public void onPatternStart() {
            lockPatternView.removePostClearPatternRunnable();
        }

        @Override
        public void onPatternComplete(List<LockPatternView.Cell> pattern) {
            if (pattern != null) {
                if (LockPatternUtil.checkPattern(pattern, gesturePassword)) {
                    updateStatus(Status.CORRECT);
                } else {
                    if (pattern.size() >= LockPatternUtil.MIN_PATTERN_REGISTER_FAIL) {
                        mFailedPatternAttemptsSinceLastTimeout++;
                        int retry = LockPatternUtil.FAILED_ATTEMPTS_BEFORE_TIMEOUT
                                - mFailedPatternAttemptsSinceLastTimeout;
                        if (retry >= 0) {
                            if (retry == 0) {
                                mError = true;
                                mTimeOut = true;
                                Date date = new Date();
                                SPUtils.getInstance().put(GESTURE_TIME, date.getTime());
                                mTimerTask = new InnerTimerTask(handler);
                                mTimer.schedule(mTimerTask, 0, 1000);
                                lockPatternView.setPattern(LockPatternView.DisplayMode.ERROR);
                                lockPatternView.postClearPatternRunnable(LockPatternUtil.DELAYTIME);
                            } else {
                                messageTv.setText(
                                        getString(R.string.gesture_tip_password_error_retry) + " " + retry + " "
                                                + getString(
                                                R.string.gesture_error_tip_total));
                                messageTv.setTextColor(Color.RED);
                                lockPatternView.postClearPatternRunnable(LockPatternUtil.DELAYTIME);
                            }
                        }

                    } else {
                        updateStatus(Status.ERROR_MIN_PATTERN_REGISTER_FAIL);
                    }

                    messageTv.startAnimation(mShakeAnim);
                }
            }
        }
    };


    /**
     * 更新状态
     *
     * @param status
     */
    private void updateStatus(Status status) {
        messageTv.setText(status.strId);
        messageTv.setTextColor(getResources().getColor(status.colorId));
        switch (status) {
            case DEFAULT:
                lockPatternView.setPattern(LockPatternView.DisplayMode.DEFAULT);
                break;
            case ERROR:
                lockPatternView.setPattern(LockPatternView.DisplayMode.ERROR);
                lockPatternView.postClearPatternRunnable(LockPatternUtil.DELAYTIME);
                break;
            case ERROR_MAX_RETRY:
                lockPatternView.setPattern(LockPatternView.DisplayMode.ERROR);
                lockPatternView.postClearPatternRunnable(LockPatternUtil.DELAYTIME);
                break;
            case ERROR_MIN_PATTERN_REGISTER_FAIL:
                lockPatternView.setPattern(LockPatternView.DisplayMode.ERROR);
                lockPatternView.postClearPatternRunnable(LockPatternUtil.DELAYTIME);
                break;
            case CORRECT:
                lockPatternView.setPattern(LockPatternView.DisplayMode.DEFAULT);
                loginGestureSuccess();
                break;
        }
    }

    /**
     * 手势登录成功
     */
    private void loginGestureSuccess() {
        switch (gestureType) {
            case ParamConstants.GESTURE_SKIP_TYPE_CHANGE:
                //修改手势密码验证逻辑跳转
                UISkipMananger.lauchCreateGestureActivity(this);
                finish();
                break;
            case ParamConstants.GESTURE_SKIP_TYPE_LOGIN_VERIFY:
                //登录进入跳转逻辑
                UISkipMananger.launchHome(this);
                finish();
                break;
            case ParamConstants.GESTURE_SKIP_TYPE_CLOSE:
                //关闭手势密码跳转逻辑
                SPUtils.getInstance().put(CacheConstants.KEY_OPEN_GESTURE, false);
                EventBusProvider.post(new RefreshGestureEvent());
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 忘记手势密码
     */
    @OnClick(R.id.forgetGestureBtn)
    void forgetGesturePasswrod() {
        Intent intent = new Intent(GestureVerifyActivity.this, GestureCreateActivity.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCountdownTimer != null) { mCountdownTimer.cancel(); }
    }

    private enum Status {
        //默认的状态
        DEFAULT(R.string.gesture_default, R.color.eos_grey_a5a5a5),
        //密码输入错误
        ERROR(R.string.gesture_error, R.color.eos_red_f4333c),
        //密码输入错误
        ERROR_MAX_RETRY(R.string.gesture_error_max_retry, R.color.eos_red_f4333c),
        //至少连接4个点
        ERROR_MIN_PATTERN_REGISTER_FAIL(R.string.create_gesture_less_error, R.color.eos_red_f4333c),
        //密码输入正确
        CORRECT(R.string.gesture_correct, R.color.eos_grey_a5a5a5);

        Status(int strId, int colorId) {
            this.strId = strId;
            this.colorId = colorId;
        }

        private int strId;
        private int colorId;
    }

    //定义一个内部TimerTask类用于记录，错误倒计时
    static class InnerTimerTask extends TimerTask {

        Handler handler;

        public InnerTimerTask(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void run() {
            handler.sendMessage(handler.obtainMessage());
        }
    }

}
