package com.cybex.gma.client.ui.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cybex.gma.client.R;
import com.cybex.gma.client.config.HttpConst;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.ui.presenter.CreateWalletPresenter;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Checked;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.siberiadante.customdialoglib.CustomFullDialog;
import com.xujiaji.happybubble.BubbleLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

/**
 * 创建钱包页面
 */
public class CreateWalletActivity extends XActivity<CreateWalletPresenter> implements Validator.ValidationListener{

    private Validator validator;
    @BindView(R.id.scroll_create_wallet) ScrollView scrollViewCreateWallet;
    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.tv_in_bubble) TextView tvInBubble;
    @BindView(R.id.bubble) BubbleLayout bubble;
    @BindView(R.id.tv_eos_name) TextView tvEosName;

    @NotEmpty(messageResId = R.string.eos_name_not_empty, sequence = 3)
    @BindView(R.id.edt_eos_name) EditText edtEosName;
    @BindView(R.id.tv_set_pass) TextView tvSetPass;

    @NotEmpty(messageResId = R.string.pass_not_empty, sequence = 2)
    @Password(min = 8, messageResId = R.string.pass_lenth_invalid, sequence = 2)
    @BindView(R.id.edt_set_pass) EditText edtSetPass;
    @BindView(R.id.tv_repeat_pass) TextView tvRepeatPass;

    @NotEmpty(messageResId = R.string.repeat_input_pass, sequence = 1)
    @ConfirmPassword(messageResId = R.string.password_no_match, sequence = 1)
    @BindView(R.id.edt_repeat_pass) EditText edtRepeatPass;
    @BindView(R.id.tv_pass_hint) TextView tvPassHint;
    @BindView(R.id.edt_pass_hint) EditText edtPassHint;
    @BindView(R.id.tv_invCode) TextView tvInvCode;
    @BindView(R.id.tv_get_invCode) TextView tvGetInvCode;

    @BindView(R.id.edt_invCode) EditText edtInvCode;
    @Checked(messageResId = R.string.check_agreement,sequence = 0)
    @BindView(R.id.checkbox_config) CheckBox checkboxConfig;
    @BindView(R.id.service_agreement_config) TextView serviceAgreementConfig;
    @BindView(R.id.layout_checkBox) LinearLayout layoutCheckBox;
    @BindView(R.id.bt_create_wallet) Button btCreateWallet;



    @OnTextChanged(value = R.id.edt_eos_name, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterEosNameChanged(Editable s) {
        if (isAllTextFilled() && checkboxConfig.isChecked() && getP().isPasswordMatch()) {
            setClickable(btCreateWallet);
        } else {
            setUnclickable(btCreateWallet);
        }

        if (EmptyUtils.isEmpty(getEOSUserName())) {
            if (edtEosName.hasFocus()) setEOSNameFocusedStyle();
        } else if (getEOSUserName().length() == ParamConstants.VALID_EOSNAME_LENGTH) {
            if (getP().isUserNameValid()) {
                setEOSNameFocusedStyle();
            } else {
                setUnclickable(btCreateWallet);
                setEOSNameInvalidStyle();
            }
        }
    }

    @OnTextChanged(value = R.id.edt_set_pass, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterPassChanged(Editable s) {
        if (isAllTextFilled() && checkboxConfig.isChecked() && getP().isPasswordMatch()) {
            setClickable(btCreateWallet);
        } else {
            setUnclickable(btCreateWallet);
        }
    }

    @OnTextChanged(value = R.id.edt_repeat_pass, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterRepeatPassChanged(Editable s) {
        if (EmptyUtils.isEmpty(getRepeatPassword())) {
            if (edtRepeatPass.hasFocus())setRepeatPassFocusStyle();
        }
        if (isAllTextFilled() && checkboxConfig.isChecked() && getP().isPasswordMatch()) {
            setClickable(btCreateWallet);
        } else {
            setUnclickable(btCreateWallet);
        }
    }

    @OnTextChanged(value = R.id.edt_invCode, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterInvCodeChanged(Editable s) {
        if (isAllTextFilled() && checkboxConfig.isChecked()) {
            setClickable(btCreateWallet);
        } else {
            setUnclickable(btCreateWallet);
        }

    }

    @OnClick(R.id.bt_create_wallet)
    public void checkAndCreateWallet() {
        validator.validate();
        if (checkboxConfig.isChecked() && !getP().isUserNameValid()) {
            GemmaToastUtils.showLongToast(getResources().getString(R.string.invalid_eos_username));
        }
        /*
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
                    getP().createAccount(getEOSUserName(), getPassword(), getInvCode(), keyPair[1], keyPair[0],
                            getPassHint(), getInvCode());
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
            GemmaToastUtils.showLongToast(getString(R.string.agree_service_agreement));
        } else if (checkboxConfig.isChecked() && !getP().isUserNameValid()) {
            GemmaToastUtils.showLongToast(getResources().getString(R.string.invalid_eos_username));
        }
        */
    }

    public void initView() {
        //动态设置hint样式
        setEditTextHintStyle(edtEosName, R.string.EOS_username_hint);
        setEditTextHintStyle(edtSetPass, R.string.password_input_hint);
        setEditTextHintStyle(edtRepeatPass, R.string.repeatPassword_hint);
        setEditTextHintStyle(edtPassHint, R.string.password_hint_hint);
        setEditTextHintStyle(edtInvCode, R.string.input_invCode_hint);
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
                if (isChecked && isAllTextFilled() && getP().isUserNameValid() && getP().isPasswordMatch()) {
                    setClickable(btCreateWallet);
                } else {
                    setUnclickable(btCreateWallet);
                }
            }
        });
        setNavibarTitle(getResources().getString(R.string.create_wallet), true);
        /**
         * eos用户名输入区域样式设置
         */
        edtEosName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (EmptyUtils.isEmpty(getEOSUserName())) {
                    setEOSNameValidStyle();
                    if (hasFocus)setEOSNameFocusedStyle();
                }else {
                    if (getP().isUserNameValid()) {
                        setEOSNameValidStyle();
                        if (hasFocus)setEOSNameFocusedStyle();
                    } else {
                        setUnclickable(btCreateWallet);
                        setEOSNameInvalidStyle();
                    }
                }

                if (hasFocus){
                    edtEosName.setTypeface(Typeface.DEFAULT_BOLD);
                }else {
                    edtEosName.setTypeface(Typeface.DEFAULT);
                }
            }
        });

        /**
         * 设置密码输入区域样式设置
         */
        edtSetPass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    tvSetPass.setTextColor(getResources().getColor(R.color.darkSlateBlue));
                    edtSetPass.setTypeface(Typeface.DEFAULT_BOLD);

                } else {
                    tvSetPass.setTextColor(getResources().getColor(R.color.steel));
                    edtSetPass.setTypeface(Typeface.DEFAULT);
                }
            }
        });
        /**
         * 重复密码输入区域样式设置
         */
        edtRepeatPass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (EmptyUtils.isEmpty(getRepeatPassword())) {
                    setRepeatPassValidStyle();
                    if (hasFocus)setRepeatPassFocusStyle();
                } else {
                    if (getPassword().equals(getRepeatPassword())) {
                        //两次输入的密码一致
                        setRepeatPassValidStyle();
                        if (hasFocus)setRepeatPassFocusStyle();
                    } else {
                        //两次输入的密码不一致
                        setRepeatPassInvalidStyle();
                    }
                }

                if (hasFocus){
                    edtRepeatPass.setTypeface(Typeface.DEFAULT_BOLD);
                }else {
                    edtRepeatPass.setTypeface(Typeface.DEFAULT);
                }
            }
        });
        /**
         * 密码提示输入区域样式设置
         */
        edtPassHint.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    tvPassHint.setTextColor(getResources().getColor(R.color.darkSlateBlue));
                    edtPassHint.setTypeface(Typeface.DEFAULT_BOLD);
                } else {
                    tvPassHint.setTextColor(getResources().getColor(R.color.steel));
                    edtPassHint.setTypeface(Typeface.DEFAULT);
                }
            }
        });

        OverScrollDecoratorHelper.setUpOverScroll(scrollViewCreateWallet);
    }

    @Override
    public void bindUI(View rootView) {
        ButterKnife.bind(this);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        //Validate.reg(this);
        validator = new Validator(this);
        validator.setValidationListener(this);
        initView();
    }

    @Override
    public void onBackPressedSupport() {
        super.onBackPressedSupport();
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

    public void setEOSNameValidStyle() {
        if (EmptyUtils.isNotEmpty(getEOSUserName())) {

        }
        //当eos用户名为12位时，恢复初始样式
        tvEosName.setTextColor(getResources().getColor(R.color.steel));
        tvEosName.setText(getResources().getString(R.string.eos_username));

    }

    public void setEOSNameInvalidStyle() {
        //不为12位时，更改样式示意
        tvEosName.setText(getResources().getString(R.string.EOS_username_hint));
        tvEosName.setTextColor(getResources().getColor(R.color.scarlet));

    }

    public void setEOSNameFocusedStyle(){
        tvEosName.setTextColor(getResources().getColor(R.color.darkSlateBlue));
        tvEosName.setText(getResources().getString(R.string.eos_username));
    }

    public void setRepeatPassValidStyle() {
        //两次输入密码匹配
        tvRepeatPass.setText(getResources().getString(R.string.repeat_pass));
        tvRepeatPass.setTextColor(getResources().getColor(R.color.steel));
        edtRepeatPass.setBackground(getResources().getDrawable(R.drawable.selector_edt_bg));
    }

    public void setRepeatPassFocusStyle(){
        tvRepeatPass.setText(getResources().getString(R.string.repeat_pass));
        tvRepeatPass.setTextColor(getResources().getColor(R.color.darkSlateBlue));
        edtRepeatPass.setBackground(getResources().getDrawable(R.drawable.selector_edt_bg));
    }

    public void setRepeatPassInvalidStyle() {
        //两次输入密码不匹配
        tvRepeatPass.setText(getResources().getString(R.string.pass_no_match));
        tvRepeatPass.setTextColor(getResources().getColor(R.color.scarlet));
        edtRepeatPass.setBackground(getResources().getDrawable(R.drawable.selector_edt_bg_scalet));
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

    /**
     * 根据返回值不同Toast不同内容
     *
     * @param errorCode
     */
    public void showOnErrorInfo(int errorCode) {

        switch (errorCode) {
            case (HttpConst.INVCODE_USED):
                GemmaToastUtils.showLongToast(getResources().getString(R.string.invCode_used));
                break;
            case (HttpConst.INVCODE_NOTEXIST):
                GemmaToastUtils.showLongToast(getResources().getString(R.string.invCode_not_exist));
                break;
            case (HttpConst.EOSNAME_USED):
                GemmaToastUtils.showLongToast(getResources().getString(R.string.eos_name_used));
                break;
            case (HttpConst.EOSNAME_INVALID):
                GemmaToastUtils.showLongToast(getResources().getString(R.string.eos_name_invalid));
                break;
            case (HttpConst.EOSNAME_LENGTH_INVALID):
                GemmaToastUtils.showLongToast(getResources().getString(R.string.eos_name_len_invalid));
                break;
            case (HttpConst.PARAMETERS_INVALID):
                GemmaToastUtils.showLongToast(getResources().getString(R.string.params_invalid));
                break;
            case (HttpConst.PUBLICKEY_INVALID):
                GemmaToastUtils.showLongToast(getResources().getString(R.string.pubKey_invalid));
                break;
            case (HttpConst.BALANCE_NOT_ENOUGH):
                GemmaToastUtils.showLongToast(getResources().getString(R.string.no_balance));
                break;
            case (HttpConst.CREATE_ACCOUNT_FAIL):
                GemmaToastUtils.showLongToast(getResources().getString(R.string.default_create_fail_info));
                break;
            default:
                GemmaToastUtils.showLongToast(getResources().getString(R.string.default_create_fail_info));
                break;
        }


    }

    @Override
    protected void onDestroy() {
        clearListeners();
        super.onDestroy();
        //Validate.unreg(this);
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

    public void setEditTextHintStyle(EditText editText, int resId) {
        String hintStr = getResources().getString(resId);
        SpannableString ss = new SpannableString(hintStr);
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(14, true);
        editText.setHintTextColor(getResources().getColor(R.color.cloudyBlue));
        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        editText.setHint(new SpannableString(ss));
    }

    private void clearListeners(){
        edtEosName.setOnFocusChangeListener(null);
        edtRepeatPass.setOnFocusChangeListener(null);
        edtSetPass.setOnFocusChangeListener(null);
        edtPassHint.setOnFocusChangeListener(null);
        edtInvCode.setOnFocusChangeListener(null);
        checkboxConfig.setOnCheckedChangeListener(null);
    }

    /**
     * 显示选择创建方式dialog
     */
    private void showChooseMethodDialog() {
        int[] listenedItems = {R.id.btn_close, R.id.btn_use_invCode, R.id.btn_use_cybex, R.id.btn_invite_friend};
        CustomFullDialog dialog = new CustomFullDialog(this,
                R.layout.dialog_choose_create_method, listenedItems, false, Gravity.BOTTOM);
        dialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
            @Override
            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.btn_close:
                        dialog.cancel();
                        break;
                    case R.id.btn_use_invCode:
                        showGetInvCodeDialog();
                        dialog.cancel();
                        break;
                    default:
                        break;
                }
            }
        });
        dialog.show();
    }

    /**
     * 显示获取邀请码dialog
     */
    private void showGetInvCodeDialog() {
        int[] listenedItems = {R.id.imc_cancel, R.id.btn_confirm_create, R.id.tv_get_invCode};
        CustomFullDialog dialog = new CustomFullDialog(this,
                R.layout.dialog_get_invcode, listenedItems, false, Gravity.BOTTOM);
        dialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
            @Override
            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.imc_cancel:
                        dialog.cancel();
                        break;
                    case R.id.btn_use_invCode:


                        break;
                    case R.id.tv_get_invCode:


                        break;
                    default:
                        break;
                }
            }
        });
        dialog.show();
    }

    /**
     * 验证框架验证成功回调
     */
    @Override
    public void onValidationSucceeded() {
        if (getP().isUserNameValid()){
            String[] keyPair = getP().getKeypair();
            final String publicKey = keyPair[0];
            final String privateKey = keyPair[1];
            getP().createAccount(getEOSUserName(), getPassword(), getInvCode(), keyPair[1], keyPair[0],
                    getPassHint(), getInvCode());
        }
    }

    /**
     * 验证失败回调
     * @param errors
     */
    @Override
    public void onValidationFailed(List<ValidationError> errors) {

        for (ValidationError error : errors){
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            if (view instanceof EditText){
                GemmaToastUtils.showLongToast(message);
            }else{
                GemmaToastUtils.showLongToast(message);
            }
        }
    }

}
