package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.allen.library.SuperTextView;
import com.cybex.base.view.switchbutton.SwitchButton;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.CacheConstants;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.event.FingerprintEvent;
import com.cybex.gma.client.event.RefreshGestureEvent;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.utils.SPUtils;
import com.cybex.gma.client.utils.fingerprint.FingerprintScanHelper;
import com.cybex.gma.client.utils.fingerprint.OnAuthResultListener;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 安全设置界面
 */

public class SecuritySettingFragment extends XFragment {

    Unbinder unbinder;
    //指纹解锁
    @BindView(R.id.switch_fingerprint) SwitchButton switchFingerprint;
    //手势密码
    @BindView(R.id.switch_pattern) SwitchButton switchGesture;
    //修改手势密码
    @BindView(R.id.superTextView_change_pattern) SuperTextView tvChangePattern;

    public static SecuritySettingFragment newInstance() {
        Bundle args = new Bundle();
        SecuritySettingFragment fragment = new SecuritySettingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setNavibarTitle(getString(R.string.title_security_settings), true, true);
        this.isShowChangeGestureView();

        switchFingerprint.setEnableCheckedListener(true);
        switchGesture.setEnableCheckedListener(true);

        switchFingerprint.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switchFingerprint.setEnableCheckedListener(true);
                return false;
            }
        });


        switchGesture.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switchGesture.setEnableCheckedListener(true);
                return false;
            }
        });

        switchFingerprint.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                boolean isOpenFinger = SPUtils.getInstance().getBoolean(CacheConstants.KEY_OPEN_FINGER_PRINT);
                new FingerprintScanHelper(getActivity())
                        .startAuth(new OnAuthResultListener() {
                            @Override
                            public void onSuccess() {
                                SPUtils.getInstance().put(CacheConstants.KEY_OPEN_FINGER_PRINT, !isOpenFinger);
                                EventBusProvider.post(new FingerprintEvent());
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
                                GemmaToastUtils.showShortToast(getString(R.string.finger_tip_device_no_support));

                            }
                        }, false, false);
            }
        });


        switchGesture.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (isChecked) {
                    boolean isOpenFinger = SPUtils.getInstance().getBoolean(CacheConstants.KEY_OPEN_FINGER_PRINT);
                    if (isOpenFinger) {
                        new FingerprintScanHelper(getActivity())
                                .startAuth(new OnAuthResultListener() {
                                    @Override
                                    public void onSuccess() {
                                        //指纹验证成功
                                        UISkipMananger.lauchCreateGestureActivity(getActivity());
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
                    } else {
                        //创建手势密码
                        UISkipMananger.lauchCreateGestureActivity(getActivity());
                    }

                } else {
                    //关闭手势密码，需要先验证原来手势
                    Bundle bd = new Bundle();
                    bd.putInt(ParamConstants.GESTURE_SKIP_TYPE, ParamConstants.GESTURE_SKIP_TYPE_CLOSE);
                    UISkipMananger.launchVerifyGestureActivity(getActivity(), bd);
                }
            }
        });


        tvChangePattern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bd = new Bundle();
                bd.putInt(ParamConstants.GESTURE_SKIP_TYPE, ParamConstants.GESTURE_SKIP_TYPE_CHANGE);
                UISkipMananger.launchVerifyGestureActivity(getActivity(), bd);
            }
        });


    }

    private void isShowChangeGestureView() {
        switchGesture.setEnableCheckedListener(false);
        boolean isopenGesture = SPUtils.getInstance().getBoolean(CacheConstants.KEY_OPEN_GESTURE);
        switchGesture.setChecked(isopenGesture);
        if (isopenGesture) {
            tvChangePattern.setVisibility(View.VISIBLE);
        } else {
            tvChangePattern.setVisibility(View.GONE);
        }
        switchFingerprint.setEnableCheckedListener(false);
        boolean isopenFinger = SPUtils.getInstance().getBoolean(CacheConstants.KEY_OPEN_FINGER_PRINT);
        switchFingerprint.setChecked(isopenFinger);
    }

    @Override
    public boolean useEventBus() {
        return true;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveRefreshEvent(RefreshGestureEvent gestureEvent) {
        if (EmptyUtils.isNotEmpty(gestureEvent)) {
            isShowChangeGestureView();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveFingerprintEvent(FingerprintEvent fingerEvent) {
        if (EmptyUtils.isNotEmpty(fingerEvent)) {
            isShowChangeGestureView();
        }

    }


    @Override
    public int getLayoutId() {
        return R.layout.fragment_security_setting;
    }

    @Override
    public Object newP() {
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


}
