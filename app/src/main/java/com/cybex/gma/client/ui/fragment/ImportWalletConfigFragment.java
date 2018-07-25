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
import com.cybex.gma.client.manager.UISkipMananger;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
 *
 */

public class ImportWalletConfigFragment extends XFragment {


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
    @BindView(R.id.bt_create_wallet) Button btCreateWallet;
    @BindView(R.id.scroll_wallet_config) ScrollView scrollViewWalletConfig;

    Unbinder unbinder;

    @OnClick(R.id.bt_create_wallet)
    public void checkValidation(){
        //先检查表单
        Validate.check(this, new IValidateResult() {
            @Override
            public void onValidateSuccess() {
                if (checkboxConfig.isChecked()){//再检查checkBox
                    UISkipMananger.launchHome(getActivity());
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



    public static ImportWalletConfigFragment newInstance() {
        Bundle args = new Bundle();
        ImportWalletConfigFragment fragment = new ImportWalletConfigFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
        Validate.reg(this);
        OverScrollDecoratorHelper.setUpOverScroll(scrollViewWalletConfig);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setNavibarTitle("配置钱包", true, false);
        checkboxConfig.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    if (isAllFilled()){
                        setButtonClickableStyle();
                    }else{
                        setButtonUnClickableStyle();
                    }
                }else{
                    setButtonUnClickableStyle();
                }
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_import_wallet_config;
    }

    @Override
    public Object newP() {
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Validate.unreg(this);
        unbinder.unbind();
    }

    public boolean isAllFilled(){
        if (EmptyUtils.isEmpty(edtSetPass.getText().toString().trim())
                || EmptyUtils.isEmpty(edtRepeatPass.getText().toString().trim())){
            return false;
        }
        return true;
    }

    public void setButtonClickableStyle(){
        btCreateWallet.setBackground(getResources().getDrawable(R.drawable.shape_corner_button));

    }

    public void setButtonUnClickableStyle(){
        btCreateWallet.setBackground(getResources().getDrawable(R.drawable.shape_corner_button_unclickable));

    }

}
