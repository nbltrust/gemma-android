package com.cybex.gma.client.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.cybex.gma.client.R;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.utils.fingerprint.FingerprintScanHelper;
import com.cybex.gma.client.utils.fingerprint.OnAuthResultListener;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 指纹解锁验证
 *
 * Created by wanglin on 2018/8/20.
 */
public class FingerprintVerifyActivity extends XActivity {

    @BindView(R.id.imv_finger_print) ImageView imvFingerPrint;

    @Override
    public void bindUI(View rootView) {
        ButterKnife.bind(this);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        imvFingerPrint.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new FingerprintScanHelper(FingerprintVerifyActivity.this)
                        .startAuth(new OnAuthResultListener() {
                            @Override
                            public void onSuccess() {
                                //指纹验证成功
                                UISkipMananger.launchHome(FingerprintVerifyActivity.this, new Bundle());
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
                                GemmaToastUtils.showShortToast(getString(R.string.finger_tip_device_no_support));

                            }
                        });

            }
        });

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_finger_print_verify;
    }

    @Override
    public Object newP() {
        return null;
    }


}
