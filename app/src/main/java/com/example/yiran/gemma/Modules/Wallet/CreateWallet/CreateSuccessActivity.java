package com.example.yiran.gemma.Modules.Wallet.CreateWallet;

import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yiran.gemma.Base.BaseActivity;
import com.example.yiran.gemma.R;

import butterknife.BindView;


public class CreateSuccessActivity extends BaseActivity {

    private TextView mTitle;
    private ImageView mBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initToolBar();
        setBaseContentView(R.layout.activity_create_success);
        initView();
    }

    public void initView(){
        mTitle = findViewById(R.id.tv_title);
        mBack = findViewById(R.id.iv_back);
        mTitle.setText("备份钱包");
        mTitle.setPadding(-getViewWidth(mBack), getStatusBarHeight(this), 0, 0);
        setMargins(mBack, 0, getStatusBarHeight(this),0,0);
    }


}
