package com.cybex.gma.client.ui.activity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cybex.gma.client.R;
import com.cybex.gma.client.ui.UISkipMananger;
import com.cybex.gma.client.ui.presenter.CreateWalletPresenter;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.xujiaji.happybubble.BubbleLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import cxy.com.validate.Validate;

public class CreateWalletActivity extends XActivity<CreateWalletPresenter> {
    private CreateWalletPresenter curPresenter;
    private UISkipMananger uiSkipMananger;
    private boolean isUserNameValid;
    private boolean isPasswordMatched;

    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.tv_in_bubble) TextView tvInBubble;
    @BindView(R.id.bubble) BubbleLayout bubble;
    @BindView(R.id.single_input_eosName) LinearLayout singleInputEosName;
    @BindView(R.id.textView_eosName) TextView textViewEosName;
    @BindView(R.id.editText_eosName) MaterialEditText editTextEosName;
    @BindView(R.id.textView_password) TextView textViewPassword;
    @BindView(R.id.editText_password) MaterialEditText editTextPassword;
    @BindView(R.id.textView_repeatPass) TextView textViewRepeatPass;
    @BindView(R.id.editText_repeatPass) MaterialEditText editTextRepeatPass;
    @BindView(R.id.textView_passHint) TextView textViewPassHint;
    @BindView(R.id.editText_passHint) MaterialEditText editTextPassHint;
    @BindView(R.id.single_input_passHint) LinearLayout singleInputPassHint;
    @BindView(R.id.textView_invCode) TextView textViewInvCode;
    @BindView(R.id.tv_get_invCode) TextView tvGetInvCode;
    @BindView(R.id.editText_invCode) MaterialEditText editTextInvCode;
    @BindView(R.id.checkbox) CheckBox checkbox;
    @BindView(R.id.tv_service_agreement) TextView tvServiceAgreement;
    @BindView(R.id.layout_checkBox) LinearLayout layoutCheckBox;
    @BindView(R.id.bt_create_wallet) Button btCreateWallet;

    @OnTextChanged(value = R.id.editText_eosName, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterNameChanged(){
        if (editTextEosName.getText().toString().trim().length() == 12){
            //当eos用户名为12位时，恢复初始样式
            textViewEosName.setTextColor(getResources().getColor(R.color.steel));
            textViewEosName.setText("EOS账户名");
            editTextEosName.setUnderlineColor(getResources().getColor(R.color.steel));
            isUserNameValid = true;
            if (isAllTextFilled() && checkbox.isChecked()){
                setClickable(btCreateWallet);
            }else{
                setUnclickable(btCreateWallet);
            }
        }else{
            //不为12位时，更改样式示意
            isUserNameValid = false;
            textViewEosName.setText("由小写字母a-z与数字1-5组成，须为12位");
            textViewEosName.setTextColor(getResources().getColor(R.color.scarlet));
            editTextEosName.setUnderlineColor(getResources().getColor(R.color.scarlet));
            setUnclickable(btCreateWallet);
        }
    }
    @OnTextChanged(value = R.id.editText_password, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterPasswordChanged(){
        if (isAllTextFilled() && checkbox.isChecked() && isUserNameValid){
            setClickable(btCreateWallet);
        }else{
            setUnclickable(btCreateWallet);
        }
    }
    @OnTextChanged(value = R.id.editText_repeatPass, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterRepeatPassChanged(){
        if (getPassword().equals(getRepeatPassword())){
            textViewRepeatPass.setText("重复密码");
            textViewRepeatPass.setTextColor(getResources().getColor(R.color.steel));
            editTextRepeatPass.setUnderlineColor(getResources().getColor(R.color.steel));
            if (isAllTextFilled() && checkbox.isChecked()){
                setClickable(btCreateWallet);
            }else{
                setUnclickable(btCreateWallet);
            }
        }else{
            textViewRepeatPass.setText("密码不一致");
            textViewRepeatPass.setTextColor(getResources().getColor(R.color.scarlet));
            editTextRepeatPass.setUnderlineColor(getResources().getColor(R.color.scarlet));
            setUnclickable(btCreateWallet);
        }
    }
    @OnTextChanged(value = R.id.editText_invCode, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterInvCodeChanged(){
        if (isAllTextFilled() && checkbox.isChecked() && isUserNameValid){
            setClickable(btCreateWallet);
        }else{
            setUnclickable(btCreateWallet);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isUserNameValid = false;
        curPresenter = getP();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_wallet);
        ButterKnife.bind(this);
        Validate.reg(this);
        initView();
        uiSkipMananger = new UISkipMananger();
    }


    @OnClick(R.id.bt_create_wallet)
    public void Jump(){
        uiSkipMananger.launchIntent(CreateWalletActivity.this, MainTabActivity.class);
    }

    public void initView() {
        bubble.setVisibility(View.GONE);
        setUnclickable(btCreateWallet);
        editTextPassword.setOnTouchListener(new View.OnTouchListener() {
            int flag = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                flag++;
                if (flag % 2 == 0) {
                    singleInputEosName.setVisibility(View.GONE);
                    bubble.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });
        editTextRepeatPass.setOnTouchListener(new View.OnTouchListener() {
            int flag = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                flag++;
                if (flag % 2 == 0) {
                    bubble.setVisibility(View.GONE);
                    singleInputEosName.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && isAllTextFilled() && isUserNameValid ){
                    setClickable(btCreateWallet);
                }else{
                    setUnclickable(btCreateWallet);
                }
            }
        });
        btnNavibar.setTitle("创建钱包");
        btnNavibar.setTitleColor(R.color.white);
        btnNavibar.setTitleSize(20);
        btnNavibar.setLeftImageResource(R.drawable.icback24px);
        btnNavibar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void bindUI(View rootView) {

    }

    @Override
    public void initData(Bundle savedInstanceState) {

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_create_wallet;
    }

    @Override
    public CreateWalletPresenter newP() {
        return new CreateWalletPresenter();
    }

    @Override
    protected CreateWalletPresenter getP() {
        return super.getP();
    }

    public String getWalletName() {
        return editTextEosName.getText().toString().trim();
    }

    public String getPassword() {
        return editTextPassword.getText().toString().trim();
    }

    public String getRepeatPassword() {
        return editTextRepeatPass.getText().toString().trim();
    }

    public String getInvCode() {
        return editTextInvCode.getText().toString().trim();
    }

    public void setClickable(Button button) {
        button.setClickable(true);
        button.setBackgroundColor(getResources().getColor(R.color.cornflowerBlueTwo));
    }

    public void setUnclickable(Button button) {
        button.setClickable(false);
        button.setBackgroundColor(getResources().getColor(R.color.cloudyBlueTwo));
    }

    public boolean isAllTextFilled() {
        if (EmptyUtils.isEmpty(getPassword())
                || EmptyUtils.isEmpty(getRepeatPassword())
                || EmptyUtils.isEmpty(getWalletName())
                || EmptyUtils.isEmpty(getInvCode())) {
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Validate.unreg(this);
    }

}
