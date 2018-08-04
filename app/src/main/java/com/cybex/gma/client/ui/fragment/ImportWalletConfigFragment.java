package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cybex.gma.client.R;
import com.cybex.gma.client.manager.LoggerManager;
import com.cybex.gma.client.ui.presenter.ImportWalletConfigPresenter;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.kaopiz.kprogresshud.KProgressHUD;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import cxy.com.validate.IValidateResult;
import cxy.com.validate.Validate;
import cxy.com.validate.ValidateAnimation;
import cxy.com.validate.annotation.Index;
import cxy.com.validate.annotation.MinLength;
import cxy.com.validate.annotation.NotNull;
import cxy.com.validate.annotation.Password1;
import cxy.com.validate.annotation.Password2;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

/**
 * 配置钱包界面
 */

public class ImportWalletConfigFragment extends XFragment<ImportWalletConfigPresenter> {

    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.tv_set_pass) TextView tvSetPass;
    @Index(1)
    @NotNull(msg = "密码不能为空")
    @MinLength(length = 8, msg = "密码请至少输入8位！")
    @Password1()
    @BindView(R.id.edt_set_pass) EditText edtSetPass;
    @BindView(R.id.tv_repeat_pass) TextView tvRepeatPass;
    @Index(2)
    @NotNull(msg = "重复密码不能为空")
    @Password2(msg = "两次密码不一致")
    @BindView(R.id.edt_repeat_pass) EditText edtRepeatPass;
    @BindView(R.id.tv_pass_hint) TextView tvPassHint;
    @BindView(R.id.edt_pass_hint) EditText edtPassHint;
    @BindView(R.id.checkbox_config) CheckBox checkboxConfig;
    @BindView(R.id.service_agreement_config) TextView serviceAgreementConfig;
    @BindView(R.id.layout_checkBox) LinearLayout layoutCheckBox;
    @BindView(R.id.btn_complete_import) Button btnCompleteImport;
    @BindView(R.id.scroll_wallet_config) ScrollView scrollViewWalletConfig;

    Unbinder unbinder;

    public static ImportWalletConfigFragment newInstance(String privateKey) {
        Bundle args = new Bundle();
        args.putString("priKey", privateKey);
        ImportWalletConfigFragment fragment = new ImportWalletConfigFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @OnTextChanged(value = R.id.edt_set_pass, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onSetPassChanged(){
        if (isAllFilled() && checkboxConfig.isChecked()){
            setButtonClickableStyle();
        }else{
            setButtonUnClickableStyle();
        }
    }

    @OnTextChanged(value = R.id.edt_repeat_pass, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onRepeatPassChanged(){
        if (isAllFilled() && checkboxConfig.isChecked()){
            setButtonClickableStyle();
        }else{
            setButtonUnClickableStyle();
        }
    }

    @OnClick(R.id.btn_complete_import)
    public void checkValidation() {
        //先检查表单
        Validate.check(this, new IValidateResult() {
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
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
        Validate.reg(this);
        OverScrollDecoratorHelper.setUpOverScroll(scrollViewWalletConfig);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Validate.unreg(this);
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
        setNavibarTitle("配置钱包", true, false);
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

        edtRepeatPass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (EmptyUtils.isNotEmpty(getRepeatPass())){
                    if (getRepeatPass().equals(getPassword())){
                        setRepeatPassValidStyle();
                    }else{
                        setRepeatPassInvalidStyle();
                    }
                }else{
                    setRepeatPassValidStyle();
                }
            }
        });
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
    public void setRepeatPassValidStyle(){
        tvRepeatPass.setText("重复密码");
        tvRepeatPass.setTextColor(getResources().getColor(R.color.steel));
        edtRepeatPass.setBackground(getResources().getDrawable(R.drawable.selector_edt_bg));
    }
    /**
     * 重置密码区域不匹配时的样式
     */
    public void setRepeatPassInvalidStyle(){
        tvRepeatPass.setText("密码不一致");
        tvRepeatPass.setTextColor(getResources().getColor(R.color.scarlet));
        edtRepeatPass.setBackground(getResources().getDrawable(R.drawable.selector_edt_bg_scalet));
    }

    public String getPassword() {
        return edtSetPass.getText().toString().trim();
    }

    public String getPassHint() {
        return edtPassHint.getText().toString().trim();
    }

    public String getRepeatPass() {
        return edtRepeatPass.getText().toString().trim();
    }

}
