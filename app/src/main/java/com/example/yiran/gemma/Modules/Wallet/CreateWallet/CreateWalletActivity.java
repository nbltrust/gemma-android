package com.example.yiran.gemma.Modules.Wallet.CreateWallet;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yiran.gemma.Base.BaseActivity;
import com.example.yiran.gemma.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreateWalletActivity extends BaseActivity {

    private int barHeight;
    private int backIconWidth;

    @BindView(R.id.tv_title)
    TextView mTitle;
    @BindView(R.id.iv_back)
    ImageView mBackIcon;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initToolBar();
        setContentView(R.layout.activity_create_wallet);
        ButterKnife.bind(this);
        initView();
    }


    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void initView(){
        backIconWidth = getViewWidth(mBackIcon);
        barHeight = getStatusBarHeight(this);
        mTitle.setPadding(-backIconWidth, barHeight,0,0);
        mTitle.setText("创建钱包");
        setMargins(mBackIcon,0,barHeight,0,0);
    }

}
