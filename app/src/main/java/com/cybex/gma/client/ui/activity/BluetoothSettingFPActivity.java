package com.cybex.gma.client.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.cybex.gma.client.R;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;

/**
 * 设置指纹识别窗口
 *
 * Created by wanglin on 2018/9/18.
 */
public class BluetoothSettingFPActivity extends XActivity {

    @Override
    public void bindUI(View rootView) {

    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setNavibarTitle("设置指纹", false);
        mTitleBar.setActionTextColor(Color.WHITE);
        mTitleBar.setActionTextSize(18);
        mTitleBar.addAction(new TitleBar.TextAction("跳过") {
            @Override
            public void performAction(View view) {
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
}
