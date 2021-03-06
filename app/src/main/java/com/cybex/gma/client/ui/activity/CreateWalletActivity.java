package com.cybex.gma.client.ui.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.AbsoluteSizeSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cybex.componentservice.api.ApiPath;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.HttpConst;
import com.cybex.componentservice.ui.activity.CommonWebViewActivity;
import com.cybex.gma.client.ui.presenter.CreateWalletPresenter;
import com.cybex.componentservice.utils.AlertUtil;
import com.cybex.componentservice.utils.SoftHideKeyBoardUtil;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.LanguageManager;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Checked;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

import static com.cybex.gma.client.config.ParamConstants.CN;
import static com.cybex.gma.client.config.ParamConstants.EN;

/**
 * 创建钱包页面
 */
public class CreateWalletActivity extends XActivity<CreateWalletPresenter> implements Validator.ValidationListener {

    @BindView(R.id.iv_set_pass_mask) ImageView ivSetPassMask;
    @BindView(R.id.iv_repeat_pass_mask) ImageView ivRepeatPassMask;
    private Validator validator;
    //@BindView(R.id.view_divider_eosName) View viewDividerEosName;
    @BindView(R.id.view_divider_setPass) View viewDividerSetPass;
    @BindView(R.id.view_divider_repeatPass) View viewDividerRepeatPass;

    @BindView(R.id.scroll_create_wallet) ScrollView scrollViewCreateWallet;
    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    //@BindView(R.id.tv_in_bubble) TextView tvInBubble;
    //@BindView(R.id.bubble) BubbleLayout bubble;
    //@BindView(R.id.tv_eos_name) TextView tvEosName;
    //@BindView(R.id.iv_eos_name_clear) ImageView ivEosNameClear;
    @BindView(R.id.iv_set_pass_clear) ImageView ivSetPassClear;
    @BindView(R.id.iv_repeat_pass_clear) ImageView ivRepeatPassClear;
    @BindView(R.id.iv_pass_hint_clear) ImageView ivPassHintClear;

    /*
    @NotEmpty(messageResId = R.string.eos_name_not_empty, sequence = 3)
    @BindView(R.id.edt_eos_name) EditText edtEosName;
    */
    @BindView(R.id.tv_set_pass) TextView tvSetPass;
    @NotEmpty(messageResId = R.string.eos_pass_not_empty, sequence = 2)
    @Password(min = 8, messageResId = R.string.eos_pass_lenth_invalid, sequence = 2)
    @BindView(R.id.edt_set_pass) EditText edtSetPass;
    @BindView(R.id.tv_repeat_pass) TextView tvRepeatPass;

    @NotEmpty(messageResId = R.string.eos_repeat_input_pass, sequence = 1)
    @ConfirmPassword(messageResId = R.string.eos_password_no_match, sequence = 1)
    @BindView(R.id.et_repeat_pass) EditText edtRepeatPass;
    @BindView(R.id.tv_pass_hint) TextView tvPassHint;
    @BindView(R.id.edt_pass_hint) EditText edtPassHint;

    @Checked(messageResId = R.string.eos_check_agreement, sequence = 0)
    @BindView(R.id.checkbox_config) CheckBox checkboxConfig;
    @BindView(R.id.tv_service_agreement_config) TextView tvServiceAgreementConfig;
    @BindView(R.id.layout_checkBox) LinearLayout layoutCheckBox;
    @BindView(R.id.bt_create_wallet) Button btCreateWallet;

    private boolean isMask;//true为密文显示密码


    /*
    @OnTextChanged(value = R.id.edt_eos_name, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterEosNameChanged(Editable s) {
        if (isAllTextFilled() && checkboxConfig.isChecked() && getP().isPasswordMatch()) {
            setClickable(btCreateWallet);
        } else {
            setUnclickable(btCreateWallet);
        }

        if (EmptyUtils.isEmpty(getEOSUserName())) {
            if (edtEosName.hasFocus()) { setEOSNameFocusedStyle(); }
        } else if (getEOSUserName().length() == ParamConstants.VALID_EOSNAME_LENGTH) {
            if (getP().isUserNameValid()) {
                setEOSNameFocusedStyle();
            } else {
                setUnclickable(btCreateWallet);
                setEOSNameInvalidStyle();
            }
        }

        if (EmptyUtils.isNotEmpty(getEOSUserName())) {
            ivEosNameClear.setVisibility(View.VISIBLE);
        } else {
            ivEosNameClear.setVisibility(View.GONE);
        }
    }
    */

    @OnTextChanged(value = R.id.edt_set_pass, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterPassChanged(Editable s) {
        if (isAllTextFilled() && checkboxConfig.isChecked() && getP().isPasswordMatch()) {
            setClickable(btCreateWallet);
        } else {
            setUnclickable(btCreateWallet);
        }

        if (EmptyUtils.isNotEmpty(getPassword())) {
            ivSetPassClear.setVisibility(View.VISIBLE);
        } else {
            ivSetPassClear.setVisibility(View.GONE);
        }
    }

    @OnTextChanged(value = R.id.et_repeat_pass, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterRepeatPassChanged(Editable s) {
        if (EmptyUtils.isEmpty(getRepeatPassword())) {
            ivRepeatPassClear.setVisibility(View.GONE);
            if (edtRepeatPass.hasFocus()) { setRepeatPassFocusStyle(); }
        } else {
            ivRepeatPassClear.setVisibility(View.VISIBLE);
        }

        if (isAllTextFilled() && checkboxConfig.isChecked() && getP().isPasswordMatch()) {
            setClickable(btCreateWallet);
        } else {
            setUnclickable(btCreateWallet);
        }
    }

    @OnTextChanged(value = R.id.edt_pass_hint, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterPassHintChanged(Editable s) {
        if (EmptyUtils.isNotEmpty(getPassHint())) {
            ivPassHintClear.setVisibility(View.VISIBLE);
        } else {
            ivPassHintClear.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.bt_create_wallet)
    public void checkAndCreateWallet() {
        validator.validate();
        if (checkboxConfig.isChecked()) {
            GemmaToastUtils.showLongToast(getResources().getString(R.string.eos_invalid_eos_username));
        }
    }

    @OnClick(R.id.tv_service_agreement_config)
    public void goSeeServiceAgreement() {
        int savedLanguageType = LanguageManager.getInstance(this).getLanguageType();
        switch (savedLanguageType) {
            case LanguageManager.LanguageType.LANGUAGE_CHINESE_SIMPLIFIED:
                CommonWebViewActivity.startWebView(this, ApiPath.TERMS_OF_SERVICE_CN, getResources().getString(R
                        .string.terms_of_service));
                break;
            case LanguageManager.LanguageType.LANGUAGE_EN:
                CommonWebViewActivity.startWebView(this, ApiPath.TERMS_OF_SERVICE_EN, getResources().getString(R
                        .string.terms_of_service));
                break;
            case LanguageManager.LanguageType.LANGUAGE_FOLLOW_SYSTEM:
                Locale systemLanguageType = LanguageManager.getInstance(this).getSysLocale();
                switch (systemLanguageType.getDisplayLanguage()) {
                    case CN:
                        CommonWebViewActivity.startWebView(this, ApiPath.TERMS_OF_SERVICE_CN, getResources()
                                .getString(R.string.terms_of_service));
                        break;
                    case EN:
                        CommonWebViewActivity.startWebView(this, ApiPath.TERMS_OF_SERVICE_EN, getResources()
                                .getString(R.string.terms_of_service));
                        break;
                    default:
                        CommonWebViewActivity.startWebView(this, ApiPath.TERMS_OF_SERVICE_CN, getResources()
                                .getString(R.string.terms_of_service));
                }
                break;
            default:
                CommonWebViewActivity.startWebView(this, ApiPath.TERMS_OF_SERVICE_CN, getResources().getString(R
                        .string.terms_of_service));
        }
    }

    @OnClick({R.id.iv_set_pass_mask, R.id.iv_repeat_pass_mask})
    public  void onMaskClicked(View v){
        switch (v.getId()){
            case R.id.iv_set_pass_mask:
                if (isMask){
                    //如果当前为密文
                    isMask = false;
                    edtSetPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    ivSetPassMask.setImageResource(R.drawable.ic_invisible);
                    edtSetPass.setSelection(getPassword().length());
                }else {
                    isMask = true;
                    edtSetPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    ivSetPassMask.setImageResource(R.drawable.ic_visible);
                    edtSetPass.setSelection(getPassword().length());
                }
                break;
            case R.id.iv_repeat_pass_mask:
                if (isMask){
                    //如果当前为密文
                    isMask = false;
                    edtRepeatPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    ivRepeatPassMask.setImageResource(R.drawable.ic_invisible);
                    edtRepeatPass.setSelection(getRepeatPassword().length());
                }else {
                    //如果当前为明文
                    isMask = true;
                    edtRepeatPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    ivRepeatPassMask.setImageResource(R.drawable.ic_visible);
                    edtRepeatPass.setSelection(getRepeatPassword().length());
                }
                break;
        }
    }

    /**
     * 清除按钮点击事件
     *
     * @param v
     */
    @OnClick({R.id.iv_set_pass_clear, R.id.iv_repeat_pass_clear, R.id.iv_pass_hint_clear})
    public void onClearClicked(View v) {
        switch (v.getId()) {
            case R.id.iv_set_pass_clear:
                edtSetPass.setText("");
                break;
            case R.id.iv_repeat_pass_clear:
                edtRepeatPass.setText("");
                break;
            case R.id.iv_pass_hint_clear:
                edtPassHint.setText("");
                break;
        }
    }

    public void initView() {
        //动态设置hint样式
        setEditTextHintStyle(edtSetPass, R.string.eos_tip_input_password);
        setEditTextHintStyle(edtRepeatPass, R.string.eos_tip_repeat_password);
        setEditTextHintStyle(edtPassHint, R.string.eos_tip_input_password_hint);

        //bubble.setVisibility(View.GONE);
        setUnclickable(btCreateWallet);
        edtSetPass.setOnTouchListener(new View.OnTouchListener() {
            int flag = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                flag++;
                if (flag % 2 == 0) {
                    //showBubble();
                    //scheduleDismiss();
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
                    //hideBubble();
                }
                return false;
            }
        });
        checkboxConfig.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && isAllTextFilled() && getP().isPasswordMatch()) {
                    setClickable(btCreateWallet);
                } else {
                    setUnclickable(btCreateWallet);
                }
            }
        });
        setNavibarTitle(getResources().getString(R.string.eos_tip_create_wallet), true);
        /**
         * eos用户名输入区域样式设置

        edtEosName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (EmptyUtils.isEmpty(getEOSUserName())) {
                    setEOSNameValidStyle();
                    if (hasFocus) { setEOSNameFocusedStyle(); }
                } else {
                    if (getP().isUserNameValid()) {
                        setEOSNameValidStyle();
                        if (hasFocus) { setEOSNameFocusedStyle(); }
                    } else {
                        setUnclickable(btCreateWallet);
                        setEOSNameInvalidStyle();
                    }
                }

                if (hasFocus) {
                    setDividerFocusStyle(viewDividerEosName);
                    edtEosName.setTypeface(Typeface.DEFAULT_BOLD);
                    if (EmptyUtils.isNotEmpty(getEOSUserName())){
                        ivEosNameClear.setVisibility(View.VISIBLE);
                    }
                } else {
                    setDividerDefaultStyle(viewDividerEosName);
                    ivEosNameClear.setVisibility(View.GONE);
                    if (EmptyUtils.isEmpty(getEOSUserName())) {
                        edtEosName.setTypeface(Typeface.DEFAULT);
                    }
                }

            }
        });
         */

        /**
         * 设置密码输入区域样式设置
         */
        edtSetPass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    setDividerFocusStyle(viewDividerSetPass);
                    tvSetPass.setTextColor(getResources().getColor(R.color.darkSlateBlue));
                    edtSetPass.setTypeface(Typeface.DEFAULT_BOLD);
                    if (EmptyUtils.isNotEmpty(getPassword())){
                        ivSetPassClear.setVisibility(View.VISIBLE);
                    }
                } else {
                    setDividerDefaultStyle(viewDividerSetPass);
                    ivSetPassClear.setVisibility(View.GONE);
                    tvSetPass.setTextColor(getResources().getColor(R.color.steel));
                    if (EmptyUtils.isEmpty(getPassword())) { edtSetPass.setTypeface(Typeface.DEFAULT); }
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
                    if (hasFocus) { setRepeatPassFocusStyle(); }
                } else {
                    if (getPassword().equals(getRepeatPassword())) {
                        //两次输入的密码一致
                        setRepeatPassValidStyle();
                        if (hasFocus) { setRepeatPassFocusStyle(); }
                    } else {
                        //两次输入的密码不一致
                        setRepeatPassInvalidStyle();
                    }
                }

                if (hasFocus) {
                    edtRepeatPass.setTypeface(Typeface.DEFAULT_BOLD);
                } else {
                    ivRepeatPassClear.setVisibility(View.GONE);
                    if (EmptyUtils.isEmpty(getRepeatPassword())) { edtRepeatPass.setTypeface(Typeface.DEFAULT); }
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
                    if (EmptyUtils.isNotEmpty(getPassHint())) { ivPassHintClear.setVisibility(View.VISIBLE); }
                    tvPassHint.setTextColor(getResources().getColor(R.color.darkSlateBlue));
                    edtPassHint.setTypeface(Typeface.DEFAULT_BOLD);
                } else {
                    ivPassHintClear.setVisibility(View.GONE);
                    tvPassHint.setTextColor(getResources().getColor(R.color.steel));
                    if (EmptyUtils.isEmpty(getPassHint())) { edtPassHint.setTypeface(Typeface.DEFAULT); }
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
        SoftHideKeyBoardUtil.assistActivity(this);
        validator = new Validator(this);
        validator.setValidationListener(this);
        isMask = true;
        initView();
    }

    @Override
    public void onBackPressedSupport() {
        super.onBackPressedSupport();
    }

    @Override
    public int getLayoutId() {
        return R.layout.eos_activity_create_wallet;
    }

    @Override
    public CreateWalletPresenter newP() {
        return new CreateWalletPresenter();
    }

    @Override
    protected CreateWalletPresenter getP() {
        return super.getP();
    }

    /*
    public void setEOSNameValidStyle() {
        if (EmptyUtils.isNotEmpty(getEOSUserName())) {

        }
        //当eos用户名为12位时，恢复初始样式
        tvEosName.setTextColor(getResources().getColor(R.color.steel));
        tvEosName.setText(getResources().getString(R.string.eos_title_eos_username));

    }

    public void setEOSNameInvalidStyle() {
        //不为12位时，更改样式示意
        tvEosName.setText(getResources().getString(R.string.eos_tip_eos_username));
        tvEosName.setTextColor(getResources().getColor(R.color.scarlet));

    }

    public void setEOSNameFocusedStyle() {
        tvEosName.setTextColor(getResources().getColor(R.color.darkSlateBlue));
        tvEosName.setText(getResources().getString(R.string.eos_title_eos_username));
    }
    */

    public void setRepeatPassValidStyle() {
        //两次输入密码匹配
        tvRepeatPass.setText(getResources().getString(R.string.eos_tip_repeat_pass));
        tvRepeatPass.setTextColor(getResources().getColor(R.color.steel));
        setDividerDefaultStyle(viewDividerRepeatPass);
        if (EmptyUtils.isNotEmpty(getRepeatPassword())) {
            ivRepeatPassClear.setVisibility(View.VISIBLE);
        } else {
            ivRepeatPassClear.setVisibility(View.GONE);
        }
        //edtRepeatPass.setBackground(getResources().getDrawable(R.drawable.eos_selector_edt_bg));
    }

    public void setRepeatPassFocusStyle() {
        tvRepeatPass.setText(getResources().getString(R.string.eos_tip_repeat_pass));
        tvRepeatPass.setTextColor(getResources().getColor(R.color.darkSlateBlue));
        setDividerFocusStyle(viewDividerRepeatPass);
        if (EmptyUtils.isNotEmpty(getRepeatPassword())) {
            ivRepeatPassClear.setVisibility(View.VISIBLE);
        } else {
            ivRepeatPassClear.setVisibility(View.GONE);
        }
        //edtRepeatPass.setBackground(getResources().getDrawable(R.drawable.eos_selector_edt_bg));
    }

    public void setRepeatPassInvalidStyle() {
        //两次输入密码不匹配
        tvRepeatPass.setText(getResources().getString(R.string.eos_tip_pass_no_match));
        tvRepeatPass.setTextColor(getResources().getColor(R.color.scarlet));
        setDividerAlertStyle(viewDividerRepeatPass);
        if (EmptyUtils.isNotEmpty(getRepeatPassword())) {
            ivRepeatPassClear.setVisibility(View.VISIBLE);
        } else {
            ivRepeatPassClear.setVisibility(View.GONE);
        }
        //edtRepeatPass.setBackground(getResources().getDrawable(R.drawable.eos_selector_edt_bg_scalet));
    }

    public void setClickable(Button button) {
        //button.setClickable(true);
        button.setBackgroundColor(getResources().getColor(R.color.cornflowerBlueTwo));
    }

    public void setUnclickable(Button button) {
        // button.setClickable(false);
        button.setBackgroundColor(getResources().getColor(R.color.cloudyBlueTwo));
    }

    /*
    public String getEOSUserName() {
        return edtEosName.getText().toString().trim();
    }
    */

    public String getPassword() {
        return edtSetPass.getText().toString().trim();
    }

    public String getRepeatPassword() {
        return edtRepeatPass.getText().toString().trim();
    }

    public String getPassHint() {
        return edtPassHint.getText().toString();
    }

    /**
     * 根据返回值不同Toast不同内容
     *
     * @param errorCode
     */
    public void showOnErrorInfo(int errorCode) {

        switch (errorCode) {
            case (HttpConst.INVCODE_USED):
                GemmaToastUtils.showLongToast(getResources().getString(R.string.eos_invCode_used));
                break;
            case (HttpConst.INVCODE_NOTEXIST):
                GemmaToastUtils.showLongToast(getResources().getString(R.string.eos_invCode_not_exist));
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
                GemmaToastUtils.showLongToast(getResources().getString(R.string.eos_params_invalid));
                break;
            case (HttpConst.PUBLICKEY_INVALID):
                GemmaToastUtils.showLongToast(getResources().getString(R.string.eos_pubKey_invalid));
                break;
            case (HttpConst.BALANCE_NOT_ENOUGH):
                GemmaToastUtils.showLongToast(getResources().getString(R.string.eos_no_balance));
                break;
            case (HttpConst.CREATE_ACCOUNT_FAIL):
                GemmaToastUtils.showLongToast(getResources().getString(R.string.eos_default_create_fail_info));
                break;
            default:
                GemmaToastUtils.showLongToast(getResources().getString(R.string.eos_default_create_fail_info));
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
        return !EmptyUtils.isEmpty(getPassword())
                && !EmptyUtils.isEmpty(getRepeatPassword());
    }

    public void setEditTextHintStyle(EditText editText, int resId) {
        String hintStr = getResources().getString(resId);
        SpannableString ss = new SpannableString(hintStr);
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(14, true);
        editText.setHintTextColor(getResources().getColor(R.color.cloudyBlue));
        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        editText.setHint(new SpannableString(ss));
    }

    public void clearListeners() {
        //edtEosName.setOnFocusChangeListener(null);
        edtRepeatPass.setOnFocusChangeListener(null);
        edtSetPass.setOnFocusChangeListener(null);
        edtPassHint.setOnFocusChangeListener(null);
        checkboxConfig.setOnCheckedChangeListener(null);
    }

    /*
    public void showBubble() {
        tvEosName.setVisibility(View.GONE);
        edtEosName.setVisibility(View.GONE);
        bubble.setVisibility(View.VISIBLE);
    }

    public void hideBubble() {
        tvEosName.setVisibility(View.VISIBLE);
        edtEosName.setVisibility(View.VISIBLE);
        bubble.setVisibility(View.GONE);
    }
    */

    public void setDividerFocusStyle(View divider) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                3);
        setHorizontalMargins(params, (int) getResources().getDimension(R.dimen.dimen_12), (int) getResources()
                .getDimension(R.dimen.dimen_12));
        divider.setLayoutParams(params);
        divider.setBackgroundColor(getResources().getColor(R.color.dark_slate_blue));

    }

    public void setDividerDefaultStyle(View divider) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2);
        setHorizontalMargins(params, (int) getResources().getDimension(R.dimen.dimen_12), (int) getResources()
                .getDimension(R.dimen.dimen_12));
        divider.setLayoutParams(params);
        divider.setBackgroundColor(getResources().getColor(R.color.paleGrey));

    }

    public void setDividerAlertStyle(View divider) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 3);
        setHorizontalMargins(params, (int) getResources().getDimension(R.dimen.dimen_12), (int) getResources()
                .getDimension(R.dimen.dimen_12));
        divider.setBackgroundColor(getResources().getColor(R.color.scarlet));
    }

    public void setHorizontalMargins(LinearLayout.LayoutParams params, int marginStart, int marginEnd) {
        params.setMarginStart(marginStart);
        params.setMarginEnd(marginEnd);
    }

    /*


    private void showChooseMethodDialog() {
        int[] listenedItems = {R.id.btn_close, R.id.btn_use_invCode, R.id.btn_use_cybex, R.id.btn_invite_friend};
        CustomFullDialog dialog = new CustomFullDialog(this,
                R.layout.eos_dialog_choose_create_method, listenedItems, false, Gravity.BOTTOM);
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


    private void showGetInvCodeDialog() {
        int[] listenedItems = {R.id.imc_cancel, R.id.btn_confirm_create, R.id.tv_get_invCode};
        CustomFullDialog dialog = new CustomFullDialog(this,
                R.layout.eos_dialog_get_invcode, listenedItems, false, Gravity.BOTTOM);
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




    private void scheduleDismiss(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideBubble();
            }
        }, 5000);
    }
     */


    /**
     * 验证框架验证成功回调
     */
    @Override
    public void onValidationSucceeded() {
        /*
        if (getP().isUserNameValid()) {
            getP().verifyAccount(getEOSUserName());
        }else {
            AlertUtil.showShortUrgeAlert(this, getString(R.string.eos_name_invalid));
        }
        */


    }

    /**
     * 验证失败回调
     *
     * @param errors
     */
    @Override
    public void onValidationFailed(List<ValidationError> errors) {

        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            AlertUtil.showShortUrgeAlert(this, message);
            if (view instanceof EditText) {
                GemmaToastUtils.showLongToast(message);
            } else {
                GemmaToastUtils.showLongToast(message);
            }
        }
    }

}
