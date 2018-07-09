package com.cybex.gma.client.ui.main.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.cybex.gma.client.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InitialActivity extends AppCompatActivity {

    @BindView(R.id.bt_create_new)
    Button createWallet;
    @BindView(R.id.bt_import)
    Button restoreWallet;

    @OnClick(R.id.bt_create_new)
    public void createWallet(){
        Intent intent  = new Intent(this, CreateWalletActivity.class);
        startActivity(intent);
    }
    @OnClick(R.id.bt_import)
    public void importWallet(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);
        ButterKnife.bind(this);
    }


}
