package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cybex.gma.client.R;
import com.cybex.gma.client.db.entity.WalletEntity;
import com.cybex.gma.client.manager.DBManager;
import com.cybex.gma.client.ui.JNIUtil;
import com.cybex.gma.client.ui.presenter.ImportWalletConfigPresenter;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.EmptyUtils;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

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
    @ConfirmPassword (messageResId = R.string.password_no_match, sequence = 2)
    @BindView(R.id.edt_repeat_pass) EditText edtRepeatPass;
    @BindView(R.id.tv_pass_hint_f) TextView tvPassHint;
    @BindView(R.id.edt_pass_hint) EditText edtPassHint;
    @Checked(messageResId = R.string.check_agreement, sequence = 1)
    @BindView(R.id.checkbox_config) CheckBox checkboxConfig;
    @BindView(R.id.service_agreement_config) TextView serviceAgreementConfig;
    @BindView(R.id.layout_checkBox) LinearLayout layoutCheckBox;
    @BindView(R.id.btn_complete_import) Button btnCompleteImport;
    @BindView(R.id.scroll_wallet_config) ScrollView scrollViewWalletConfig;

    Unbinder unbinder;
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
        if (isAllFilled() && checkboxConfig.isChecked()) {
            setButtonClickableStyle();
        } else {
            setButtonUnClickableStyle();
        }
    }

    @OnTextChanged(value = R.id.edt_repeat_pass, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onRepeatPassChanged() {
        if (isAllFilled() && checkboxConfig.isChecked()) {
            setButtonClickableStyle();
        } else {
            setButtonUnClickableStyle();
        }
    }

    /**
     * 按钮点击事件，执行判断跳转
     */
    @OnClick(R.id.btn_complete_import)
    public void checkValidation() {
        validator.validate();
        /*

        Validate.check(this, new IValidateResult() {
            //先检查表单
            @Override
            public void onValidateSuccess() {
                if (checkboxConfig.isChecked() && getArguments() != null) {//再检查checkBox
                    final String priKey = getArguments().getString("priKey");
                    LoggerManager.d("priKey", priKey);
                    getP().saveConfigWallet(priKey, getPassword(), getPassHint());
                }
            }

            @Override
            public void onValidateError(String msg, View view) {
                EditText editText = (EditText) view;
                editText.setHint(msg);
                GemmaToastUtils.showLongToast(msg);
                editText.setHintTextColor(getResources().getColor(R.color.scarlet));
            }

            @Override
            public Animation onValidateErrorAnno() {
                return ValidateAnimation.horizontalTranslate();
            }
        });
        */
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
        //Validate.reg(this);
        OverScrollDecoratorHelper.setUpOverScroll(scrollViewWalletConfig);
    }

    @Override
    public void onDestroyView() {
        edtPassHint.setOnFocusChangeListener(null);
        edtSetPass.setOnFocusChangeListener(null);
        edtRepeatPass.setOnFocusChangeListener(null);
        checkboxConfig.setOnCheckedChangeListener(null);
        super.onDestroyView();
       //Validate.unreg(this);
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
                    if (isAllFilled()) {
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
                } else {
                    tvSetPass.setTextColor(getResources().getColor(R.color.steel));
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
            }
        });

        edtPassHint.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    tvPassHint.setTextColor(getResources().getColor(R.color.darkSlateBlue));
                } else {
                    tvPassHint.setTextColor(getResources().getColor(R.color.steel));
                }
            }
        });

        setEditTextHintStyle(edtSetPass, R.string.password_input_hint);
        setEditTextHintStyle(edtPassHint, R.string.password_hint_hint);
        setEditTextHintStyle(edtRepeatPass, R.string.repeatPassword_hint);
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
        edtRepeatPass.setBackground(getResources().getDrawable(R.drawable.selector_edt_bg));
    }

    public void setRepeatPassFocusStyle() {
        tvRepeatPass.setText(getResources().getString(R.string.repeat_pass));
        tvRepeatPass.setTextColor(getResources().getColor(R.color.darkSlateBlue));
        edtRepeatPass.setBackground(getResources().getDrawable(R.drawable.selector_edt_bg));
    }

    /**
     * 重置密码区域不匹配时的样式
     */
    public void setRepeatPassInvalidStyle() {
        tvRepeatPass.setText(getResources().getString(R.string.pass_no_match));
        tvRepeatPass.setTextColor(getResources().getColor(R.color.scarlet));
        edtRepeatPass.setBackground(getResources().getDrawable(R.drawable.selector_edt_bg_scalet));
    }

    public String getPassword() {
        if(edtSetPass==null) return "";
        String pwd = String.valueOf(edtSetPass.getText());
        if(EmptyUtils.isNotEmpty(pwd)){
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

    @Override
    public void onValidationSucceeded() {
        final String priKey = getArguments().getString("priKey");
        final String pubKey = JNIUtil.get_public_key(priKey);
        //验证此公钥是否已在数据库中
        List<WalletEntity> walletEntityList = DBManager.getInstance().getWalletEntityDao().getWalletEntityList();
        if (EmptyUtils.isNotEmpty(walletEntityList)){
            for (WalletEntity entity : walletEntityList){
                if (pubKey.equals(entity.getPublicKey())){
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
        for (ValidationError error : errors){
            View view = error.getView();
            String message = error.getCollatedErrorMessage(getActivity());

            if (view instanceof EditText){
                //todo 设置onError样式？
                GemmaToastUtils.showLongToast(message);
            }else{
                GemmaToastUtils.showLongToast(message);
            }
        }
    }
}
