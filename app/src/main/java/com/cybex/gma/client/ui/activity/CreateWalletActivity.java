package com.cybex.gma.client.ui.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cybex.gma.client.R;
import com.cybex.gma.client.ui.presenter.CreateWalletPresenter;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.xujiaji.happybubble.BubbleLayout;

import java.lang.reflect.Field;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import cxy.com.validate.IValidateResult;
import cxy.com.validate.Validate;
import cxy.com.validate.ValidateAnimation;
import cxy.com.validate.annotation.Index;
import cxy.com.validate.annotation.MaxLength;
import cxy.com.validate.annotation.MinLength;
import cxy.com.validate.annotation.NotNull;
import cxy.com.validate.annotation.Password1;
import cxy.com.validate.annotation.Password2;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

/**
 * 创建钱包页面
 */
public class CreateWalletActivity extends XActivity<CreateWalletPresenter> {

    @BindView(R.id.scroll_create_wallet) ScrollView scrollViewCreateWallet;
    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.tv_in_bubble) TextView tvInBubble;
    @BindView(R.id.bubble) BubbleLayout bubble;
    @BindView(R.id.tv_eos_name) TextView tvEosName;
    @Index(1)
    @NotNull(msg = "EOS 账户名不能为空")
    @MinLength(length = 12, msg = "EOS账户名需为12位")
    @MaxLength(length = 12, msg = "EOS账户名需为12位")
    @BindView(R.id.edt_eos_name) EditText edtEosName;
    @BindView(R.id.tv_set_pass) TextView tvSetPass;
    @Index(2)
    @NotNull(msg = "密码不能为空")
    @Password1()
    @BindView(R.id.edt_set_pass) EditText edtSetPass;
    @BindView(R.id.tv_repeat_pass) TextView tvRepeatPass;
    @Index(3)
    @Password2(msg = "两次输入的密码不匹配，请重新输入！")
    @NotNull(msg = "请再次输入您的密码")
    @BindView(R.id.edt_repeat_pass) EditText edtRepeatPass;
    @BindView(R.id.tv_pass_hint) TextView tvPassHint;
    @BindView(R.id.edt_pass_hint) EditText edtPassHint;
    @BindView(R.id.tv_invCode) TextView tvInvCode;
    @BindView(R.id.tv_get_invCode) TextView tvGetInvCode;
    @Index(4)
    @NotNull(msg = "邀请码不能为空")
    @BindView(R.id.edt_invCode) EditText edtInvCode;
    @BindView(R.id.checkbox_config) CheckBox checkboxConfig;
    @BindView(R.id.service_agreement_config) TextView serviceAgreementConfig;
    @BindView(R.id.layout_checkBox) LinearLayout layoutCheckBox;
    @BindView(R.id.bt_create_wallet) Button btCreateWallet;

    @OnTextChanged(value = R.id.edt_eos_name, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterEosNameChanged(Editable s) {
        if (getP().isUserNameValid()) {
            setEOSNameValidStyle();
        } else {
            setEOSNameInvalidStyle();
        }
    }

    @OnClick(R.id.bt_create_wallet)
    public void checkAndCreateWallet() {
        //先判断checkbox是否勾选以及EOS账户名是否合法
        if (checkboxConfig.isChecked() && getP().isUserNameValid()) {
            //判断表单验证结果
            Validate.check(this, new IValidateResult() {
                @Override
                public void onValidateSuccess() {
                    //所有验证通过，发送创建钱包请求
                    String[] keyPair = getP().getKeypair();
                    final String publicKey = keyPair[0];
                    final String privateKey = keyPair[1];
                    getP().saveAccount(publicKey, privateKey, getPassword(), getEOSUserName(), getPassHint());
                    //getP().createAccount(getEOSUserName(), getPassword(), getInvCode(), keyPair[0]);
                }

                @Override
                public void onValidateError(String msg, View view) {
                    EditText editText = (EditText) view;
                    editText.setHintTextColor(getResources().getColor(R.color.scarlet));
                    GemmaToastUtils.showLongToast(msg);
                }

                @Override
                public Animation onValidateErrorAnno() {
                    return ValidateAnimation.horizontalTranslate();
                }
            });
        } else if (!checkboxConfig.isChecked() && getP().isUserNameValid()) {
            GemmaToastUtils.showLongToast("请阅读并同意我们的服务协议");
        } else if (checkboxConfig.isChecked() && !getP().isUserNameValid()) {
            GemmaToastUtils.showLongToast("EOS用户名不符合规范，请重新输入");
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_create_wallet);
        ButterKnife.bind(this);
        Validate.reg(this);
        initView();
    }

    public void initView() {
        bubble.setVisibility(View.GONE);
        setUnclickable(btCreateWallet);
        edtSetPass.setOnTouchListener(new View.OnTouchListener() {
            int flag = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                flag++;
                if (flag % 2 == 0) {
                    tvEosName.setVisibility(View.GONE);
                    edtEosName.setVisibility(View.GONE);
                    bubble.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });
        edtRepeatPass.setOnTouchListener(new View.OnTouchListener() {
            int flag = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                flag++;
                if (flag % 2 == 0) {
                    bubble.setVisibility(View.GONE);
                    tvEosName.setVisibility(View.VISIBLE);
                    edtEosName.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });
        checkboxConfig.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && isAllTextFilled() && getP().isUserNameValid()) {
                    setClickable(btCreateWallet);
                } else {
                    setUnclickable(btCreateWallet);
                }
            }
        });
        setNavibarTitle("创建钱包", true);
        OverScrollDecoratorHelper.setUpOverScroll(scrollViewCreateWallet);
    }

    public void setStatusBar() {
        //顶部状态栏适配
        Window window = getWindow();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //Android 5.0 以上适配
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    //| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION  //布局能延伸到navigation bar
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.TRANSPARENT);//设置状态栏颜色为透明
            //window.setNavigationBarColor(Color.TRANSPARENT);//设置导航栏颜色为透明

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //Android 7.0以上适配
                try {
                    Class decorViewClazz = Class.forName("com.android.internal.policy.DecorView");
                    Field field = decorViewClazz.getDeclaredField("mSemiTransparentStatusBarColor");
                    field.setAccessible(true);
                    field.setInt(getWindow().getDecorView(), Color.TRANSPARENT);  //改为透明
                } catch (Exception e) {}
            }
        }
    }

    @Override
    public void bindUI(View rootView) {

    }

    @Override
    public void initData(Bundle savedInstanceState) {


    }

    @Override
    public void onBackPressedSupport() {
        super.onBackPressedSupport();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_create_wallet_old;
    }

    @Override
    public CreateWalletPresenter newP() {
        return new CreateWalletPresenter();
    }

    @Override
    protected CreateWalletPresenter getP() {
        return super.getP();
    }

    public void setEOSNameValidStyle() {
        //当eos用户名为12位时，恢复初始样式
        tvEosName.setTextColor(getResources().getColor(R.color.steel));
        tvEosName.setText("EOS账户名");

    }

    public void setEOSNameInvalidStyle() {
        //不为12位时，更改样式示意
        tvEosName.setText("由小写字母a-z与数字1-5组成，须为12位");
        tvEosName.setTextColor(getResources().getColor(R.color.scarlet));

    }

    public void setRepeatPassValidStyle() {
        //两次输入密码匹配
        tvRepeatPass.setText("重复密码");
        tvRepeatPass.setTextColor(getResources().getColor(R.color.steel));

    }

    public void setRepeatPassInvalidStyle() {
        //两次输入密码不匹配
        tvRepeatPass.setText("密码不一致");
        tvRepeatPass.setTextColor(getResources().getColor(R.color.scarlet));

    }

    public void setClickable(Button button) {
        //button.setClickable(true);
        button.setBackgroundColor(getResources().getColor(R.color.cornflowerBlueTwo));
    }

    public void setUnclickable(Button button) {
        // button.setClickable(false);
        button.setBackgroundColor(getResources().getColor(R.color.cloudyBlueTwo));
    }

    public String getEOSUserName() {
        return edtEosName.getText().toString().trim();
    }

    public String getPassword() {
        return edtSetPass.getText().toString().trim();
    }

    public String getRepeatPassword() {
        return edtRepeatPass.getText().toString().trim();
    }

    public String getInvCode() {
        return edtInvCode.getText().toString().trim();
    }

    public String getPassHint() {
        return edtPassHint.getText().toString().trim();
    }

    public void showOnErrorInfo() {
        //todo 根据返回值判断提醒的内容
        GemmaToastUtils.showLongToast("创建失败，请重新尝试");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Validate.unreg(this);
    }


    /**
     * 代替监听器检查是否所有edittext输入框都不为空值
     *
     * @return
     */
    public boolean isAllTextFilled() {
        if (EmptyUtils.isEmpty(getPassword())
                || EmptyUtils.isEmpty(getRepeatPassword())
                || EmptyUtils.isEmpty(getEOSUserName())
                || EmptyUtils.isEmpty(getInvCode())) {
            return false;
        }
        return true;
    }




}
