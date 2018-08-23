package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cybex.gma.client.R;
import com.cybex.gma.client.db.entity.WalletEntity;
import com.cybex.gma.client.manager.DBManager;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.JNIUtil;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;

/**
 * 配置钱包界面
 */

public class ChangePasswordFragment extends XFragment implements Validator.ValidationListener {

    Unbinder unbinder;
    private WalletEntity curWallet;
    private String priKey;
    private Validator validator;
    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.tv_set_new_pass) TextView tvSetNewPass;

    @NotEmpty(messageResId = R.string.pass_not_empty, sequence = 2)
    @Password(min = 8, messageResId = R.string.pass_lenth_invalid, sequence = 2)
    @BindView(R.id.edt_set_new_pass) EditText edtSetNewPass;
    @BindView(R.id.tv_repeat_new_pass) TextView tvRepeatNewPass;

    @NotEmpty(messageResId = R.string.repeatPassword_hint, sequence = 1)
    @ConfirmPassword(messageResId = R.string.password_no_match, sequence = 1)
    @BindView(R.id.edt_repeat_new_pass) EditText edtRepeatNewPass;
    @BindView(R.id.tv_new_pass_hint) TextView tvNewPassHint;
    @BindView(R.id.edt_new_pass_hint) EditText edtNewPassHint;
    @BindView(R.id.btn_confirm_change_pass) Button btnConfirmChangePass;
    @BindView(R.id.scroll_change_pass) ScrollView scrollChangePass;

    public static ChangePasswordFragment newInstance(String priKey, int walletID) {
        Bundle args = new Bundle();
        args.putString("key", priKey);
        args.putInt("walletID", walletID);
        ChangePasswordFragment fragment = new ChangePasswordFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @OnTextChanged(value = R.id.edt_set_new_pass, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onPasswordChanged(){
        if (isAllFilled()){
            btnConfirmChangePass.setBackground(getResources().getDrawable(R.drawable.shape_corner_button));
        }
    }

    @OnTextChanged(value = R.id.edt_repeat_new_pass, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onRepeatPassChanged(){
        if (isAllFilled()){
            btnConfirmChangePass.setBackground(getResources().getDrawable(R.drawable.shape_corner_button));
        }
    }

    @OnClick(R.id.btn_confirm_change_pass)
    public void checkValidation() {
        validator.validate();
        /*
        //先检查表单
        Validate.check(this, new IValidateResult() {
            @Override
            public void onValidateSuccess() {
                final String newCypher = JNIUtil.get_cypher(getPassword(), priKey);
                if (!EmptyUtils.isEmpty(curWallet)){
                    curWallet.setCypher(newCypher);
                    curWallet.setPasswordTip(getPassHint());
                    DBManager.getInstance().getWalletEntityDao().saveOrUpateEntity(curWallet);
                    GemmaToastUtils.showLongToast(getResources().getString(R.string.change_pass_success));
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
        */
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
        //Validate.reg(this);
    }

    @Override
    public void onDestroyView() {
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

        setNavibarTitle(getResources().getString(R.string.title_change_pass), true, false);
        if (getArguments() != null){
            final int currentId = getArguments().getInt("walletID");
            //LoggerManager.d("currentID", currentId);
            curWallet = DBManager.getInstance().getWalletEntityDao().getWalletEntityByID(currentId);
            priKey = getArguments().getString("key");

            edtSetNewPass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus){
                        tvSetNewPass.setTextColor(getResources().getColor(R.color.darkSlateBlue));
                    }else{
                        tvSetNewPass.setTextColor(getResources().getColor(R.color.steel));
                    }
                }
            });

            edtRepeatNewPass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (EmptyUtils.isNotEmpty(getRepeatPass())){
                        if (getRepeatPass().equals(getPassword())){
                            setRepeatPassValidStyle();
                            if (hasFocus)setRepeatPassFocusStyle();
                        }else{
                            setRepeatPassInvalidStyle();
                        }
                    }else{
                        setRepeatPassValidStyle();
                        if (hasFocus)setRepeatPassFocusStyle();
                    }
                }
            });

            edtNewPassHint.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus){
                        tvNewPassHint.setTextColor(getResources().getColor(R.color.darkSlateBlue));
                    }else{
                        tvNewPassHint.setTextColor(getResources().getColor(R.color.steel));
                    }
                }
            });


        }

        setEditTextHintStyle(edtSetNewPass, R.string.new_password);
        setEditTextHintStyle(edtRepeatNewPass, R.string.repeatPassword_hint);
        setEditTextHintStyle(edtNewPassHint, R.string.password_hint_hint);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_change_password;
    }

    @Override
    public Object newP() {
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public boolean isAllFilled() {
        if (EmptyUtils.isEmpty(getPassword())
                || EmptyUtils.isEmpty(getRepeatPass())) {
            return false;
        }
        return true;
    }


    public String getPassword() {
        return edtSetNewPass.getText().toString().trim();
    }

    public String getPassHint() {
        return edtNewPassHint.getText().toString().trim();
    }

    public String getRepeatPass() {
        return edtRepeatNewPass.getText().toString().trim();
    }

    public void setRepeatPassValidStyle(){
        tvRepeatNewPass.setText(getResources().getString(R.string.repeat_pass));
        tvRepeatNewPass.setTextColor(getResources().getColor(R.color.steel));
        edtRepeatNewPass.setBackground(getResources().getDrawable(R.drawable.selector_edt_bg));
    }

    public void setRepeatPassFocusStyle(){
        tvRepeatNewPass.setText(getResources().getString(R.string.repeat_pass));
        tvRepeatNewPass.setTextColor(getResources().getColor(R.color.darkSlateBlue));
        edtRepeatNewPass.setBackground(getResources().getDrawable(R.drawable.selector_edt_bg));
    }

    public void setRepeatPassInvalidStyle(){
        tvRepeatNewPass.setText(getResources().getString(R.string.pass_no_match));
        tvRepeatNewPass.setTextColor(getResources().getColor(R.color.scarlet));
        edtRepeatNewPass.setBackground(getResources().getDrawable(R.drawable.selector_edt_bg_scalet));
    }

    public void setEditTextHintStyle(EditText editText, int resId){
        String hintStr = getResources().getString(resId);
        SpannableString ss =  new SpannableString(hintStr);
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(14, true);
        editText.setHintTextColor(getResources().getColor(R.color.cloudyBlue));
        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        editText.setHint(new SpannableString(ss));
    }

    @Override
    public void onValidationSucceeded() {
        final String newCypher = JNIUtil.get_cypher(getPassword(), priKey);
        if (!EmptyUtils.isEmpty(curWallet)){
            curWallet.setCypher(newCypher);
            curWallet.setPasswordTip(getPassHint());
            DBManager.getInstance().getWalletEntityDao().saveOrUpateEntity(curWallet);
            GemmaToastUtils.showLongToast(getResources().getString(R.string.change_pass_success));
            UISkipMananger.launchHome(getActivity());
        }
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
