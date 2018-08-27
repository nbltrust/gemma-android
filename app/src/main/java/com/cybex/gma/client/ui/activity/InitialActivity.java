package com.cybex.gma.client.ui.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cybex.gma.client.R;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.utils.repeatclick.NoDoubleClick;
import com.hxlx.core.lib.mvp.lite.XActivity;

import java.lang.reflect.Field;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InitialActivity extends XActivity {

    @BindView(R.id.bt_create_new) Button btCreateNew;
    @BindView(R.id.bt_import) Button btImport;

    @OnClick(R.id.bt_create_new)
    public void createWallet() {
        if (!NoDoubleClick.isDoubleClick()) {
            UISkipMananger.launchIntent(this, CreateWalletActivity.class);
        }
    }

    @OnClick(R.id.bt_import)
    public void importWallet() {
        if (!NoDoubleClick.isDoubleClick()) {
            UISkipMananger.launchIntent(this, ImportWalletActivity.class);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setNavigationBarStatusBarTranslucent();
        ButterKnife.bind(this);
    }

    @Override
    public void bindUI(View rootView) {

    }

    @Override
    public void initData(Bundle savedInstanceState) {

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_initial;
    }

    @Override
    public Object newP() {
        return null;
    }

    public void setNavigationBarStatusBarTranslucent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(Color.TRANSPARENT);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //Android 7.0以上适配
                try {
                    Class decorViewClazz = Class.forName("com.android.internal.policy.DecorView");
                    Field field = decorViewClazz.getDeclaredField("mSemiTransparentStatusBarColor");
                    field.setAccessible(true);
                    field.setInt(getWindow().getDecorView(), Color.TRANSPARENT);  //改为透明
                } catch (Exception e) {}
            }
        }
    }


}
