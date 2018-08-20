package com.cybex.gma.client.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.cybex.gma.client.R;
import com.hxlx.core.lib.mvp.lite.XActivity;

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
