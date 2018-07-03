package com.example.yiran.gemma.Modules.Wallet.CreateWallet;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yiran.gemma.Base.BaseActivity;
import com.example.yiran.gemma.R;
import com.xujiaji.happybubble.BubbleDialog;
import com.xujiaji.happybubble.BubbleLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class CreateWalletActivity extends BaseActivity {

    private int barHeight;
    private int backIconWidth;
    private BubbleDialog.Position mPosition;

    @BindView(R.id.tv_title)
    TextView mTitle;
    @BindView(R.id.iv_back)
    ImageView mBackIcon;
    @BindView(R.id.btw_create_wallet)
    Button mCreateWallet;
    @BindView(R.id.bubble)
    BubbleLayout mBubbleLayout;
    @BindView(R.id.editText_password)
    EditText mPassword;

    @OnClick(R.id.btw_create_wallet)
    public void createWallet(){
        Intent mIntent = new Intent(this, CreateSuccessActivity.class);
        startActivity(mIntent);
    }
    @OnClick(R.id.editText_password)
    public void showDialog(){
        mBubbleLayout.setVisibility(View.VISIBLE);
    }


    @OnTextChanged(value = R.id.editText_password, callback = OnTextChanged.Callback.BEFORE_TEXT_CHANGED)
    void beforeTextChanged(CharSequence s, int start, int count, int after) {
        mBubbleLayout.setVisibility(View.VISIBLE);
    }
    @OnTextChanged(value = R.id.editText_password, callback = OnTextChanged.Callback.TEXT_CHANGED)
    void onTextChanged(CharSequence s, int start, int before, int count) {
        //mBubbleLayout.setVisibility(View.VISIBLE);

    }
    @OnTextChanged(value = R.id.editText_password, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterTextChanged(Editable s) {

    }



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
        mBubbleLayout.setVisibility(View.GONE);
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
        mBubbleLayout.setVisibility(View.GONE);


    }

}
