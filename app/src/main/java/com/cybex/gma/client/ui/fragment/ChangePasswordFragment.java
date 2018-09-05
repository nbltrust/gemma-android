package com.cybex.gma.client.ui.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cybex.gma.client.R;
import com.cybex.gma.client.db.entity.WalletEntity;
import com.cybex.gma.client.manager.DBManager;
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
    private boolean isMask;
    @BindView(R.id.iv_set_newPass_clear) ImageView ivSetNewPassClear;
    @BindView(R.id.view_set_new_pass) View viewSetNewPass;
    @BindView(R.id.iv_repeat_newPass_clear) ImageView ivRepeatNewPassClear;
    @BindView(R.id.view_repeat_new_pass) View viewRepeatNewPass;
    @BindView(R.id.iv_newPass_hint_clear) ImageView ivNewPassHintClear;
    @BindView(R.id.iv_set_newPass_mask) ImageView ivSetNewPassMask;
    @BindView(R.id.iv_repeat_newPass_mask) ImageView ivRepeatNewPassMask;

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
    public void onPasswordChanged() {
        if (isAllFilled() && isPasswordMatch()) {
            btnConfirmChangePass.setBackground(getResources().getDrawable(R.drawable.shape_corner_button));
        } else {
            btnConfirmChangePass.setBackground(getResources().getDrawable(R.drawable.shape_corner_button_unclickable));
        }

        if (EmptyUtils.isNotEmpty(getPassword())) {
            ivSetNewPassClear.setVisibility(View.VISIBLE);
        } else {
            ivSetNewPassClear.setVisibility(View.GONE);
        }
    }

    @OnTextChanged(value = R.id.edt_repeat_new_pass, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onRepeatPassChanged() {
        if (EmptyUtils.isEmpty(getRepeatPass())) {
            setRepeatPassFocusStyle();
            ivRepeatNewPassClear.setVisibility(View.GONE);
        } else {
            ivRepeatNewPassClear.setVisibility(View.VISIBLE);
        }

        if (isAllFilled() && isPasswordMatch()) {
            btnConfirmChangePass.setBackground(getResources().getDrawable(R.drawable.shape_corner_button));
        } else {
            btnConfirmChangePass.setBackground(getResources().getDrawable(R.drawable.shape_corner_button_unclickable));
        }
    }

    @OnTextChanged(value = R.id.edt_new_pass_hint, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onPasshintChanged() {
        if (EmptyUtils.isNotEmpty(getPassHint())) {
            ivNewPassHintClear.setVisibility(View.VISIBLE);
        } else {
            ivNewPassHintClear.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.btn_confirm_change_pass)
    public void checkValidation() {
        validator.validate();
    }

    @OnClick({R.id.iv_set_newPass_clear, R.id.iv_repeat_newPass_clear, R.id.iv_newPass_hint_clear})
    public void onTextClear(View v) {
        switch (v.getId()) {
            case R.id.iv_set_newPass_clear:
                edtSetNewPass.setText("");
                break;
            case R.id.iv_repeat_newPass_clear:
                edtRepeatNewPass.setText("");
                break;
            case R.id.iv_newPass_hint_clear:
                edtNewPassHint.setText("");
                break;
        }
    }

    @OnClick({R.id.iv_set_newPass_mask, R.id.iv_repeat_newPass_mask})
    public  void onMaskClicked(View v){
        switch (v.getId()){
            case R.id.iv_set_newPass_mask:
                if (isMask){
                    //如果当前为密文
                    isMask = false;
                    edtSetNewPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    ivSetNewPassMask.setImageResource(R.drawable.ic_invisible);
                    edtSetNewPass.setSelection(getPassword().length());
                }else {
                    isMask = true;
                    edtSetNewPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    ivSetNewPassMask.setImageResource(R.drawable.ic_visible);
                    edtSetNewPass.setSelection(getPassword().length());
                }
                break;
            case R.id.iv_repeat_newPass_mask:
                if (isMask){
                    //如果当前为密文
                    isMask = false;
                    edtRepeatNewPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    ivRepeatNewPassMask.setImageResource(R.drawable.ic_invisible);
                    edtRepeatNewPass.setSelection(getRepeatPass().length());
                }else {
                    //如果当前为明文
                    isMask = true;
                    edtRepeatNewPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    ivRepeatNewPassMask.setImageResource(R.drawable.ic_visible);
                    edtRepeatNewPass.setSelection(getRepeatPass().length());
                }
                break;
        }
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        clearListeners();
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

        isMask = true;

        setNavibarTitle(getResources().getString(R.string.title_change_pass), true, false);
        if (getArguments() != null) {
            final int currentId = getArguments().getInt("walletID");
            curWallet = DBManager.getInstance().getWalletEntityDao().getWalletEntityByID(currentId);
            priKey = getArguments().getString("key");

            edtSetNewPass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        tvSetNewPass.setTextColor(getResources().getColor(R.color.darkSlateBlue));
                        edtSetNewPass.setTypeface(Typeface.DEFAULT_BOLD);
                        setDividerFocusStyle(viewSetNewPass);
                        if (EmptyUtils.isNotEmpty(getPassword())) {
                            ivSetNewPassClear.setVisibility(View.VISIBLE);
                        }

                    } else {
                        ivSetNewPassClear.setVisibility(View.GONE);
                        tvSetNewPass.setTextColor(getResources().getColor(R.color.steel));
                        setDividerDefaultStyle(viewSetNewPass);
                        if (EmptyUtils.isEmpty(getPassword())) { edtSetNewPass.setTypeface(Typeface.DEFAULT); }
                    }
                }
            });

            edtRepeatNewPass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
                        edtRepeatNewPass.setTypeface(Typeface.DEFAULT_BOLD);
                    } else {
                        ivRepeatNewPassClear.setVisibility(View.GONE);
                        if (EmptyUtils.isEmpty(getRepeatPass())) {
                            edtRepeatNewPass.setTypeface(Typeface.DEFAULT);
                        }
                    }
                }
            });

            edtNewPassHint.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        tvNewPassHint.setTextColor(getResources().getColor(R.color.darkSlateBlue));
                        edtNewPassHint.setTypeface(Typeface.DEFAULT_BOLD);
                        if (EmptyUtils.isNotEmpty(getPassHint())) {
                            ivNewPassHintClear.setVisibility(View.VISIBLE);
                        }
                    } else {
                        ivNewPassHintClear.setVisibility(View.GONE);
                        tvNewPassHint.setTextColor(getResources().getColor(R.color.steel));
                        if (EmptyUtils.isEmpty(getPassHint())) { edtNewPassHint.setTypeface(Typeface.DEFAULT); }
                    }
                }
            });
        }

        setEditTextHintStyle(edtSetNewPass, R.string.password_input_hint);
        setEditTextHintStyle(edtRepeatNewPass, R.string.repeatPassword_hint);
        setEditTextHintStyle(edtNewPassHint, R.string.password_hint_hint);
        /*
        edtSetNewPass.setOnTouchListener(new View.OnTouchListener() {
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
        edtRepeatNewPass.setOnTouchListener(new View.OnTouchListener() {
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
        edtNewPassHint.setOnTouchListener(new View.OnTouchListener() {
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

    public void setRepeatPassValidStyle() {
        tvRepeatNewPass.setText(getResources().getString(R.string.repeat_pass));
        tvRepeatNewPass.setTextColor(getResources().getColor(R.color.steel));
        setDividerDefaultStyle(viewRepeatNewPass);
        if (EmptyUtils.isNotEmpty(getRepeatPass())) {
            ivRepeatNewPassClear.setVisibility(View.VISIBLE);
        }else {
            ivRepeatNewPassClear.setVisibility(View.GONE);
        }
        //edtRepeatNewPass.setBackground(getResources().getDrawable(R.drawable.selector_edt_bg));
    }

    public void setRepeatPassFocusStyle() {
        tvRepeatNewPass.setText(getResources().getString(R.string.repeat_pass));
        tvRepeatNewPass.setTextColor(getResources().getColor(R.color.darkSlateBlue));
        setDividerFocusStyle(viewRepeatNewPass);
        if (EmptyUtils.isNotEmpty(getRepeatPass())) {
            ivRepeatNewPassClear.setVisibility(View.VISIBLE);
        }else {
            ivRepeatNewPassClear.setVisibility(View.GONE);
        }
        //edtRepeatNewPass.setBackground(getResources().getDrawable(R.drawable.selector_edt_bg));
    }

    public void setRepeatPassInvalidStyle() {
        tvRepeatNewPass.setText(getResources().getString(R.string.pass_no_match));
        tvRepeatNewPass.setTextColor(getResources().getColor(R.color.scarlet));
        setDividerAlertStyle(viewRepeatNewPass);
        if (EmptyUtils.isNotEmpty(getRepeatPass())) {
            ivRepeatNewPassClear.setVisibility(View.VISIBLE);
        }else {
            ivRepeatNewPassClear.setVisibility(View.GONE);
        }
        //edtRepeatNewPass.setBackground(getResources().getDrawable(R.drawable.selector_edt_bg_scalet));
    }

    public void setEditTextHintStyle(EditText editText, int resId) {
        String hintStr = getResources().getString(resId);
        SpannableString ss = new SpannableString(hintStr);
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(14, true);
        editText.setHintTextColor(getResources().getColor(R.color.cloudyBlue));
        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        editText.setHint(new SpannableString(ss));
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


    private void clearListeners() {
        edtNewPassHint.setOnFocusChangeListener(null);
        edtRepeatNewPass.setOnFocusChangeListener(null);
        edtSetNewPass.setOnFocusChangeListener(null);
    }

    private boolean isPasswordMatch() {
        if (getPassword().equals(getRepeatPass())) { return true; }
        return false;
    }

    @Override
    public void onValidationSucceeded() {
        final String newCypher = JNIUtil.get_cypher(getPassword(), priKey);
        if (!EmptyUtils.isEmpty(curWallet)) {
            curWallet.setCypher(newCypher);
            curWallet.setPasswordTip(getPassHint());
            DBManager.getInstance().getWalletEntityDao().saveOrUpateEntity(curWallet);
            GemmaToastUtils.showLongToast(getResources().getString(R.string.change_pass_success));
            pop();
        }
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
