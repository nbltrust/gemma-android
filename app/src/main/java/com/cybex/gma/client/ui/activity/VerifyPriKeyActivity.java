package com.cybex.gma.client.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cybex.gma.client.R;
import com.cybex.gma.client.manager.UISkipMananger;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.hxlx.core.lib.utils.common.utils.AppManager;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.rengwuxian.materialedittext.MaterialEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VerifyPriKeyActivity extends XActivity {

    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.edt_input_priKey) MaterialEditText edtInputPriKey;
    @BindView(R.id.bt_verify_input) Button btVerifyInput;

    @OnClick(R.id.bt_verify_input)
    public void onVerifyFinish(){
        AppManager.getAppManager().finishActivity();
        AppManager.getAppManager().finishActivity(BackUpPrivatekeyActivity.class);
        AppManager.getAppManager().finishActivity(BackUpWalletGuideActivity.class);
        UISkipMananger.launchHomeSingle(this);
    }

    @Override
    public void bindUI(View rootView) {
        ButterKnife.bind(this);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setNavibarTitle(getString(R.string.backup_prikey), true);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_verify_prikey;
    }

    @Override
    public Object newP() {
        return null;
    }

}
