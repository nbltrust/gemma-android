package com.cybex.gma.client.ui.main.activity;


import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cybex.gma.client.R;
import com.cybex.gma.client.ui.main.presenter.CreateWalletPresenter;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.xujiaji.happybubble.BubbleLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class CreateWalletActivity extends XActivity<CreateWalletPresenter> {

    private CreateWalletPresenter curPresenter;

    @BindView(R.id.editText_1)
    MaterialEditText editText1;
    @BindView(R.id.editText_2)
    MaterialEditText editText2;
    @BindView(R.id.editText_3)
    MaterialEditText editText3;
    @BindView(R.id.editText_5)
    MaterialEditText editText5;
    @BindView(R.id.textView_1)
    TextView textView1;
    @BindView(R.id.textView_3)
    TextView textView3;
    @BindView(R.id.checkbox)
    CheckBox checkBox;
    @BindView(R.id.bt_create_wallet)
    Button createWallet;
    @BindView(R.id.bubble)
    BubbleLayout bubble;
    @BindView(R.id.single_input_1)
    LinearLayout single_input_layout_1;

    @OnClick(R.id.bt_create_wallet)
    public void createWallet(){
        curPresenter.createWallet();
    }

    @OnTextChanged(value = R.id.editText_1, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterPasswordChanged(){
        //检查所有要求输入的文本框是否都有值，以及checkbox是否勾选
        if (isAllTextFilled() && checkBox.isChecked()){
            setClickable(createWallet);
        }else{
            setUnclickable(createWallet);
        }
    }
    @OnTextChanged(value = R.id.editText_2, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterRepeatChanged(){
        if (isAllTextFilled() && checkBox.isChecked()){
            setClickable(createWallet);
        }else{
            setUnclickable(createWallet);
        }

        //检查重复输入的密码是否匹配
        if (!getPassword().equals(getRepeatPassword())){
            editText3.setTextColor(getResources().getColor(R.color.scarlet));
            textView3.setText("密码不匹配");
            textView3.setTextColor(getResources().getColor(R.color.scarlet));//scarlet
        }else{
            editText3.setTextColor(getResources().getColor(R.color.darkSlateBlue));//darkSlateBlue
            textView3.setText("密码一致");
            textView3.setTextColor(getResources().getColor(R.color.darkSlateBlue));
        }
    }
    @OnTextChanged(value = R.id.editText_3, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterWalletNameChanged(){
        if (isAllTextFilled() && checkBox.isChecked()){
            setClickable(createWallet);
        }else{
            setUnclickable(createWallet);
        }
        //检查重复输入的密码是否匹配
        if (!getPassword().equals(getRepeatPassword())){
            editText3.setTextColor(getResources().getColor(R.color.scarlet));
            textView3.setText("密码不匹配");
            textView3.setTextColor(getResources().getColor(R.color.scarlet));//scarlet
        }else{
            editText3.setTextColor(getResources().getColor(R.color.darkSlateBlue));//darkSlateBlue
            textView3.setText("密码一致");
            textView3.setTextColor(getResources().getColor(R.color.darkSlateBlue));
        }

    }
    @OnTextChanged(value = R.id.editText_5, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterInvCodeChanged(){
        if (isAllTextFilled() && checkBox.isChecked()){
            setClickable(createWallet);
        }else{
            setUnclickable(createWallet);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        curPresenter = getP();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_wallet);
        ButterKnife.bind(this);
        initView();
    }

    public void initView(){
        bubble.setVisibility(View.GONE);
        createWallet.setClickable(false);
        editText1.setOnTouchListener(new View.OnTouchListener() {
            int flag = 0;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                flag++;
                if (flag % 2 == 0){
                    textView1.setText("由小写字母a-z与数字1-5组成，须为12位");
                    textView1.setTextColor(getResources().getColor(R.color.scarlet));//scarlet
                    editText1.setUnderlineColor(getResources().getColor(R.color.scarlet));
                }
                return false;
            }
        });
        editText2.setOnTouchListener(new View.OnTouchListener() {
            int flag = 0;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                flag++;
                if (flag % 2 == 0){
                    single_input_layout_1.setVisibility(View.GONE);
                    bubble.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });
        editText3.setOnTouchListener(new View.OnTouchListener() {
            int flag = 0;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                flag++;
                if (flag % 2 == 0){
                    bubble.setVisibility(View.GONE);
                    single_input_layout_1.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    //从未选到选中
                    if (isAllTextFilled()){
                        setClickable(createWallet);
                    }else{
                        setUnclickable(createWallet);
                    }
                }else{
                    //从选中到取消
                    setUnclickable(createWallet);
                }
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

    public String getWalletName(){
        return editText1.getText().toString().trim();
    }

    public String getPassword(){
        return editText2.getText().toString().trim();
    }

    public String getRepeatPassword(){
        return editText3.getText().toString().trim();
    }

    public String getInvCode(){
        return editText5.getText().toString().trim();
    }

    public void setClickable(Button button){
        createWallet.setClickable(true);
        createWallet.setBackgroundColor(getResources().getColor(R.color.cornflowerBlueTwo));
    }

    public void setUnclickable(Button button){
        createWallet.setClickable(false);
        createWallet.setBackgroundColor(getResources().getColor(R.color.cloudyBlueTwo));
    }

    public boolean isAllTextFilled(){
        if (EmptyUtils.isEmpty(getPassword())
                || EmptyUtils.isEmpty(getRepeatPassword())
                || EmptyUtils.isEmpty(getWalletName())
                || EmptyUtils.isEmpty(getInvCode())){
            return false;
        }
        return true;
    }

}
