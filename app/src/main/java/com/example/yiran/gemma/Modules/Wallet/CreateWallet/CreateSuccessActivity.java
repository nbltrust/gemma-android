package com.example.yiran.gemma.Modules.Wallet.CreateWallet;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.cybex.gma.client.R;
import com.example.yiran.gemma.Base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CreateSuccessActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView mTitle;
    @BindView(R.id.iv_back)
    ImageView mBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBaseContentView(R.layout.activity_create_success);
        ButterKnife.bind(this);
        initView();
    }

    public void initView(){
        mTitle.setText("备份钱包");
        mTitle.setPadding(-getViewWidth(mBack), getStatusBarHeight(this), 0, 0);
        setMargins(mBack, 0, getStatusBarHeight(this),0,0);
    }


}
