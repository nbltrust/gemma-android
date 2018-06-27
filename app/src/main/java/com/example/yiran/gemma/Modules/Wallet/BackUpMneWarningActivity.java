package com.example.yiran.gemma.Modules.Wallet;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yiran.gemma.Base.BaseActivity;
import com.example.yiran.gemma.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BackUpMneWarningActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView mTitle;
    @BindView(R.id.iv_back)
    ImageView mBack;
    @BindView(R.id.bt_show_mnemonic)
    Button showMne;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBaseContentView(R.layout.activity_back_up_mne_warning);
        ButterKnife.bind(this);
        initView();
    }

    public void initView(){
        mTitle.setText("备份助记词");
        mTitle.setPadding(-getViewWidth(mBack), getStatusBarHeight(this), 0, 0);
        setMargins(mBack, 0, getStatusBarHeight(this),0,0);
    }

}
