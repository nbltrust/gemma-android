package com.cybex.gma.client.ui.main.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.cybex.gma.client.R;
import com.cybex.gma.client.ui.UISkipMananger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InitialActivity extends AppCompatActivity {
    private UISkipMananger uiSkipMananger;

    @BindView(R.id.bt_create_new) Button btCreateNew;
    @BindView(R.id.bt_import) Button btImport;

    @OnClick(R.id.bt_create_new)
    public void createWallet() {
        uiSkipMananger.launchIntent(this, CreateWalletActivity.class);
    }

    @OnClick(R.id.bt_import)
    public void importWallet() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);
        ButterKnife.bind(this);
        uiSkipMananger = new UISkipMananger();
    }


}
