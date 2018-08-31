package com.cybex.gma.client.ui.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cybex.gma.client.R;
import com.cybex.gma.client.api.ApiPath;
import com.cybex.gma.client.db.entity.WalletEntity;
import com.cybex.gma.client.manager.DBManager;
import com.cybex.gma.client.ui.JNIUtil;
import com.cybex.gma.client.ui.base.CommonWebViewActivity;
import com.cybex.gma.client.ui.presenter.ImportWalletConfigPresenter;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.LanguageManager;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.kaopiz.kprogresshud.KProgressHUD;
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
import butterknife.Unbinder;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

import static com.cybex.gma.client.config.ParamConstants.CN;
import static com.cybex.gma.client.config.ParamConstants.EN;

/**
 * 配置钱包界面
 */

public class ImportWalletConfigFragment extends XFragment<ImportWalletConfigPresenter> implements
        Validator.ValidationListener {

    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.tv_set_pass) TextView tvSetPass;

    @NotEmpty(messageResId = R.string.pass_not_empty, sequence = 3)
    @Password(min = 8, messageResId = R.string.pass_lenth_invalid, sequence = 3)
    @BindView(R.id.edt_set_pass) EditText edtSetPass;
    @BindView(R.id.tv_repeat_pass) TextView tvRepeatPass;

    @NotEmpty(messageResId = R.string.repeatPassword_hint, sequence = 2)
    @ConfirmPassword(messageResId = R.string.password_no_match, sequence = 2)
    @BindView(R.id.et_repeat_pass) EditText edtRepeatPass;
    @BindView(R.id.tv_pass_hint_f) TextView tvPassHint;
    @BindView(R.id.edt_pass_hint) EditText edtPassHint;
    @Checked(messageResId = R.string.check_agreement, sequence = 1)
    @BindView(R.id.checkbox_config) CheckBox checkboxConfig;
    @BindView(R.id.tv_service_agreement_config) TextView serviceAgreementConfig;
    @BindView(R.id.layout_checkBox) LinearLayout layoutCheckBox;
    @BindView(R.id.btn_complete_import) Button btnCompleteImport;
    @BindView(R.id.scroll_wallet_config) ScrollView scrollViewWalletConfig;

    Unbinder unbinder;
    @BindView(R.id.iv_set_pass_clear) ImageView ivSetPassClear;
    @BindView(R.id.view_divider_set_pass) View viewDividerSetPass;
    @BindView(R.id.iv_repeat_pass_clear) ImageView ivRepeatPassClear;
    @BindView(R.id.view_divider_repeat_pass) View viewDividerRepeatPass;
    @BindView(R.id.iv_pass_hint_clear) ImageView ivPassHintClear;
    private Validator validator;

    public static ImportWalletConfigFragment newInstance(String privateKey) {
        Bundle args = new Bundle();
        args.putString("priKey", privateKey);
        ImportWalletConfigFragment fragment = new ImportWalletConfigFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @OnTextChanged(value = R.id.edt_set_pass, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onSetPassChanged() {
        if (isAllFilled() && checkboxConfig.isChecked() && getP().isPasswordMatch()) {
            setButtonClickableStyle();
        } else {
            setButtonUnClickableStyle();
        }

        if (EmptyUtils.isNotEmpty(getPassword())){
            ivSetPassClear.setVisibility(View.VISIBLE);
        }else {
            ivSetPassClear.setVisibility(View.GONE);
        }
    }

    @OnTextChanged(value = R.id.et_repeat_pass, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onRepeatPassChanged() {
        if (isAllFilled() && checkboxConfig.isChecked() && getP().isPasswordMatch()) {
            setButtonClickableStyle();
        } else {
            setButtonUnClickableStyle();
        }

        if (EmptyUtils.isEmpty(getRepeatPass())) {
            setRepeatPassFocusStyle();
        }else {
            ivRepeatPassClear.setVisibility(View.VISIBLE);
        }
    }

    @OnTextChanged(value = R.id.edt_pass_hint, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onPasshintChanged(){
        if (EmptyUtils.isNotEmpty(getPassHint())){
            ivPassHintClear.setVisibility(View.VISIBLE);
        }else {
            ivPassHintClear.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.tv_service_agreement_config)
    public void goSeeServiceAgreement() {
        int savedLanguageType = LanguageManager.getInstance(getContext()).getLanguageType();
        switch (savedLanguageType) {
            case LanguageManager.LanguageType.LANGUAGE_CHINESE_SIMPLIFIED:
                CommonWebViewActivity.startWebView(getActivity(), ApiPath.TERMS_OF_SERVICE_CN, getResources().getString(R
                        .string.service_agreement));
                break;
            case LanguageManager.LanguageType.LANGUAGE_EN:
                CommonWebViewActivity.startWebView(getActivity(), ApiPath.TERMS_OF_SERVICE_EN, getResources().getString(R
                        .string.service_agreement));
                break;
            case  LanguageManager.LanguageType.LANGUAGE_FOLLOW_SYSTEM:
                Locale systemLanguageType = LanguageManager.getInstance(getContext()).getSysLocale();
                switch (systemLanguageType.getDisplayLanguage()){
                    case CN:
                        CommonWebViewActivity.startWebView(getActivity(), ApiPath.VERSION_NOTE_CN, getResources()
                                .getString(R.string.version_info));
                        break;
                    case EN:
                        CommonWebViewActivity.startWebView(getActivity(), ApiPath.VERSION_NOTE_EN, getResources()
                                .getString(R.string.version_info));
                        break;
                    default:
                        CommonWebViewActivity.startWebView(getActivity(), ApiPath.VERSION_NOTE_CN, getResources()
                                .getString(R.string.version_info));
                }
                break;
            default:
                CommonWebViewActivity.startWebView(getActivity(), ApiPath.TERMS_OF_SERVICE_CN, getResources().getString(R
                        .string.service_agreement));

        }
    }

    /**
     * 按钮点击事件，执行判断跳转
     */
    @OnClick(R.id.btn_complete_import)
    public void checkValidation() {
        validator.validate();
    }

    @OnClick({R.id.iv_set_pass_clear, R.id.iv_repeat_pass_clear, R.id.iv_pass_hint_clear})
    public void onTextClear(View v){
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

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
        OverScrollDecoratorHelper.setUpOverScroll(scrollViewWalletConfig);
    }

    @Override
    public void onDestroyView() {
        clearListener();
        super.onDestroyView();
        unbinder.unbind();
    }

    public void showProgressDialog(final String prompt) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (kProgressHUD == null) {
                    kProgressHUD = KProgressHUD.create(getActivity())
                            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                            .setCancellable(true)
                            .setAnimationSpeed(2)
                            .setDimAmount(0.5f);
                }
                kProgressHUD.setLabel(prompt);
                kProgressHUD.show();
            }
        });
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        validator = new Validator(this);
        validator.setValidationListener(this);
        setNavibarTitle(getResources().getString(R.string.title_config_wallet), true, false);

        checkboxConfig.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (isAllFilled() && getP().isPasswordMatch()) {
                        setButtonClickableStyle();
                    } else {
                        setButtonUnClickableStyle();
                    }
                } else {
                    setButtonUnClickableStyle();
                }
            }
        });

        edtSetPass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    tvSetPass.setTextColor(getResources().getColor(R.color.darkSlateBlue));
                    edtSetPass.setTypeface(Typeface.DEFAULT_BOLD);
                    if (EmptyUtils.isNotEmpty(getPassword())){
                        ivSetPassClear.setVisibility(View.VISIBLE);
                    }
                } else {
                    ivSetPassClear.setVisibility(View.GONE);
                    tvSetPass.setTextColor(getResources().getColor(R.color.steel));
                    if (EmptyUtils.isEmpty(getPassword())) { edtSetPass.setTypeface(Typeface.DEFAULT); }
                }
            }
        });

        edtRepeatPass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (EmptyUtils.isNotEmpty(getRepeatPass())) {
                    if (getRepeatPass().equals(getPassword())) {
                        setRepeatPassValidStyle();
                        if (hasFocus) { setRepeatPassFocusStyle(); }
                    } else {
                        setRepeatPassInvalidStyle();
                    }
                } else {
                    setRepeatPassValidStyle();
                    if (hasFocus) { setRepeatPassFocusStyle(); }
                }

                if (hasFocus) {
                    if (EmptyUtils.isNotEmpty(getRepeatPass())){
                        ivRepeatPassClear.setVisibility(View.VISIBLE);
                    }
                    edtRepeatPass.setTypeface(Typeface.DEFAULT_BOLD);
                } else  {
                    ivRepeatPassClear.setVisibility(View.GONE);
                    if (EmptyUtils.isEmpty(getRepeatPass())){
                        edtRepeatPass.setTypeface(Typeface.DEFAULT);
                    }

                }
            }
        });

        edtPassHint.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (EmptyUtils.isNotEmpty(getPassHint())){
                        ivPassHintClear.setVisibility(View.VISIBLE);
                    }
                    tvPassHint.setTextColor(getResources().getColor(R.color.darkSlateBlue));
                    edtPassHint.setTypeface(Typeface.DEFAULT_BOLD);
                } else {
                    ivPassHintClear.setVisibility(View.GONE);
                    tvPassHint.setTextColor(getResources().getColor(R.color.steel));
                    if (EmptyUtils.isEmpty(getPassHint())) { edtPassHint.setTypeface(Typeface.DEFAULT); }
                }
            }
        });

        setEditTextHintStyle(edtSetPass, R.string.password_input_hint);
        setEditTextHintStyle(edtPassHint, R.string.password_hint_hint);
        setEditTextHintStyle(edtRepeatPass, R.string.repeatPassword_hint);

        /*
        edtSetPass.setOnTouchListener(new View.OnTouchListener() {
            //按住和松开的标识
            int touch_flag=0;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                touch_flag++;
                if(touch_flag==2){

                }
                return false;
            }
        });
        edtRepeatPass.setOnTouchListener(new View.OnTouchListener() {
            //按住和松开的标识
            int touch_flag=0;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                touch_flag++;
                if(touch_flag==2){

                }
                return false;
            }
        });
        edtPassHint.setOnTouchListener(new View.OnTouchListener() {
            //按住和松开的标识
            int touch_flag=0;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                touch_flag++;
                if(touch_flag==2){

                }
                return false;
            }
        });
        */

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_import_wallet_config;
    }

    @Override
    public ImportWalletConfigPresenter newP() {
        return new ImportWalletConfigPresenter();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * 输入框空判断
     *
     * @return
     */
    public boolean isAllFilled() {
        if (EmptyUtils.isEmpty(getPassword())
                || EmptyUtils.isEmpty(getRepeatPass())) {
            return false;
        }
        return true;
    }

    /**
     * Button可点击样式
     */
    public void setButtonClickableStyle() {
        btnCompleteImport.setBackground(getResources().getDrawable(R.drawable.shape_corner_button));

    }

    /**
     * Button不可点击样式
     */
    public void setButtonUnClickableStyle() {
        btnCompleteImport.setBackground(getResources().getDrawable(R.drawable.shape_corner_button_unclickable));

    }

    /**
     * 重置密码区域默认样式
     */
    public void setRepeatPassValidStyle() {
        tvRepeatPass.setText(getResources().getString(R.string.repeat_pass));
        tvRepeatPass.setTextColor(getResources().getColor(R.color.steel));
        setDividerDefaultStyle(viewDividerRepeatPass);
        if (EmptyUtils.isNotEmpty(getRepeatPass())) {
            ivRepeatPassClear.setVisibility(View.VISIBLE);
        } else {
            ivRepeatPassClear.setVisibility(View.GONE);
        }
        //edtRepeatPass.setBackground(getResources().getDrawable(R.drawable.selector_edt_bg));
    }

    public void setRepeatPassFocusStyle() {
        tvRepeatPass.setText(getResources().getString(R.string.repeat_pass));
        tvRepeatPass.setTextColor(getResources().getColor(R.color.darkSlateBlue));
        setDividerFocusStyle(viewDividerRepeatPass);
        if (EmptyUtils.isNotEmpty(getRepeatPass())) {
            ivRepeatPassClear.setVisibility(View.VISIBLE);
        } else {
            ivRepeatPassClear.setVisibility(View.GONE);
        }
        //edtRepeatPass.setBackground(getResources().getDrawable(R.drawable.selector_edt_bg));
    }

    /**
     * 重置密码区域不匹配时的样式
     */
    public void setRepeatPassInvalidStyle() {
        tvRepeatPass.setText(getResources().getString(R.string.pass_no_match));
        tvRepeatPass.setTextColor(getResources().getColor(R.color.scarlet));
        setDividerAlertStyle(viewDividerRepeatPass);
        if (EmptyUtils.isNotEmpty(getRepeatPass())) {
            ivRepeatPassClear.setVisibility(View.VISIBLE);
        } else {
            ivRepeatPassClear.setVisibility(View.GONE);
        }
        //edtRepeatPass.setBackground(getResources().getDrawable(R.drawable.selector_edt_bg_scalet));
    }

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

    public String getPassword() {
        if (edtSetPass == null) { return ""; }
        String pwd = String.valueOf(edtSetPass.getText());
        if (EmptyUtils.isNotEmpty(pwd)) {
            pwd.trim();
        }

        return pwd;
    }

    public String getPassHint() {
        if (edtPassHint == null) { return ""; }

        String hint = String.valueOf(edtPassHint.getText());
        if (EmptyUtils.isNotEmpty(hint)) {
            hint.trim();
        }

        return hint;
    }

    public String getRepeatPass() {
        if (edtRepeatPass == null) { return ""; }

        String passwrod = String.valueOf(edtRepeatPass.getText());
        if (EmptyUtils.isNotEmpty(passwrod)) {
            passwrod.trim();
        }
        return passwrod;
    }

    public void setEditTextHintStyle(EditText editText, int resId) {
        String hintStr = getResources().getString(resId);
        SpannableString ss = new SpannableString(hintStr);
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(14, true);
        editText.setHintTextColor(getResources().getColor(R.color.cloudyBlue));
        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        editText.setHint(new SpannableString(ss));
    }

    private void clearListener() {
        edtPassHint.setOnFocusChangeListener(null);
        edtSetPass.setOnFocusChangeListener(null);
        edtRepeatPass.setOnFocusChangeListener(null);
        checkboxConfig.setOnCheckedChangeListener(null);
    }

    @Override
    public void onValidationSucceeded() {
        final String priKey = getArguments().getString("priKey");
        final String pubKey = JNIUtil.get_public_key(priKey);
        //验证此公钥是否已在数据库中
        List<WalletEntity> walletEntityList = DBManager.getInstance().getWalletEntityDao().getWalletEntityList();
        if (EmptyUtils.isNotEmpty(walletEntityList)) {
            for (WalletEntity entity : walletEntityList) {
                if (pubKey.equals(entity.getPublicKey())) {
                    //已有相同的钱包
                    GemmaToastUtils.showLongToast(getResources().getString(R.string.dont_import_same_wallet));
                    return;
                }
            }
        }
        getP().saveConfigWallet(priKey, getPassword(), getPassHint());
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(getActivity());

            if (view instanceof EditText) {
                //todo 设置onError样式？
                GemmaToastUtils.showLongToast(message);
            } else {
                GemmaToastUtils.showLongToast(message);
            }
        }
    }
}
