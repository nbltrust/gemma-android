package com.cybex.gma.client.ui.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
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
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.utils.AlertUtil;
import com.cybex.gma.client.utils.SoftHideKeyBoardUtil;
import com.cybex.gma.client.utils.bluetooth.BlueToothWrapper;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.xujiaji.happybubble.BubbleLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;


/**
 * 配置WooKong Bio页面
 */
public class BluetoothConfigWooKongBioActivity extends XActivity implements Validator.ValidationListener {

    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.tv_in_bubble) TextView tvInBubble;
    @BindView(R.id.bubble) BubbleLayout bubble;
    @BindView(R.id.tv_set_pass) TextView tvSetPass;
    @BindView(R.id.edt_set_pass) EditText edtSetPass;
    @BindView(R.id.iv_set_pass_clear) ImageView ivSetPassClear;
    @BindView(R.id.iv_set_pass_mask) ImageView ivSetPassMask;
    @BindView(R.id.view_divider_setPass) View viewDividerSetPass;
    @BindView(R.id.tv_repeat_pass) TextView tvRepeatPass;
    @BindView(R.id.et_repeat_pass) EditText edtRepeatPass;
    @BindView(R.id.iv_repeat_pass_clear) ImageView ivRepeatPassClear;
    @BindView(R.id.iv_repeat_pass_mask) ImageView ivRepeatPassMask;
    @BindView(R.id.view_divider_repeatPass) View viewDividerRepeatPass;
    @BindView(R.id.tv_pass_hint) TextView tvPassHint;
    @BindView(R.id.edt_pass_hint) EditText edtPassHint;
    @BindView(R.id.iv_pass_hint_clear) ImageView ivPassHintClear;
    @BindView(R.id.bt_create_wallet) Button btCreateWallet;
    @BindView(R.id.bt_import_mne) Button btImportMne;
    @BindView(R.id.scroll_create_wallet) ScrollView scrollCreateWallet;
    private Validator validator;
    private boolean isMask;
    private BlueToothWrapper blueToothThread;
    private BluetoothHandler mHandler;
    private long contextHandle = 0;
    private Bundle bd;

    @OnTextChanged(value = R.id.edt_set_pass, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterPassChanged(Editable s) {
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

    }

    @OnTextChanged(value = R.id.edt_pass_hint, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterPassHintChanged(Editable s) {
        if (EmptyUtils.isNotEmpty(getPassHint())) {
            ivPassHintClear.setVisibility(View.VISIBLE);
        } else {
            ivPassHintClear.setVisibility(View.GONE);
        }
    }

    /**
     * 点击跳转
     * @param v
     */
    @OnClick({R.id.bt_create_wallet, R.id.bt_import_mne})
    public void onButtonClicked(View v){
        switch (v.getId()){
            case R.id.bt_create_wallet:
                break;
            case R.id.bt_import_mne:
                break;
        }
    }

    /**
     * 明文密文切换显示管理
     * @param v
     */
    @OnClick({R.id.iv_set_pass_mask, R.id.iv_repeat_pass_mask})
    public void onMaskClicked(View v) {
        switch (v.getId()) {
            case R.id.iv_set_pass_mask:
                if (isMask) {
                    //如果当前为密文
                    isMask = false;
                    edtSetPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    ivSetPassMask.setImageResource(R.drawable.ic_invisible);
                    edtSetPass.setSelection(getPassword().length());
                } else {
                    isMask = true;
                    edtSetPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    ivSetPassMask.setImageResource(R.drawable.ic_visible);
                    edtSetPass.setSelection(getPassword().length());
                }
                break;
            case R.id.iv_repeat_pass_mask:
                if (isMask) {
                    //如果当前为密文
                    isMask = false;
                    edtRepeatPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    ivRepeatPassMask.setImageResource(R.drawable.ic_invisible);
                    edtRepeatPass.setSelection(getRepeatPassword().length());
                } else {
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
            case R.id.iv_pass_hint_clear:
                edtPassHint.setText("");
                break;
            case R.id.iv_repeat_pass_clear:
                edtRepeatPass.setText("");
                break;
        }
    }

    public void initView() {
        //动态设置hint样式
        setEditTextHintStyle(edtSetPass, R.string.password_input_hint);
        setEditTextHintStyle(edtRepeatPass, R.string.repeatPassword_hint);
        setEditTextHintStyle(edtPassHint, R.string.password_hint_hint);
        bubble.setVisibility(View.GONE);

        setNavibarTitle(getResources().getString(R.string.title_wookong_bio), true);
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
                    if (EmptyUtils.isNotEmpty(getPassword())) {
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

        OverScrollDecoratorHelper.setUpOverScroll(scrollCreateWallet);
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

        bd = getIntent().getExtras();
        if (bd != null) {
            contextHandle = bd.getLong(ParamConstants.CONTEXT_HANDLE, 0);
        }


        initView();

    }

    @Override
    public void onBackPressedSupport() {
        super.onBackPressedSupport();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_bluetooth_config_wookong_bio;
    }

    @Override
    public Object newP() {
        return null;
    }

    public void setRepeatPassValidStyle() {
        //两次输入密码匹配
        tvRepeatPass.setText(getResources().getString(R.string.repeat_pass));
        tvRepeatPass.setTextColor(getResources().getColor(R.color.steel));
        setDividerDefaultStyle(viewDividerRepeatPass);
        if (EmptyUtils.isNotEmpty(getRepeatPassword())) {
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
        if (EmptyUtils.isNotEmpty(getRepeatPassword())) {
            ivRepeatPassClear.setVisibility(View.VISIBLE);
        } else {
            ivRepeatPassClear.setVisibility(View.GONE);
        }
        //edtRepeatPass.setBackground(getResources().getDrawable(R.drawable.selector_edt_bg));
    }

    public void setRepeatPassInvalidStyle() {
        //两次输入密码不匹配
        tvRepeatPass.setText(getResources().getString(R.string.pass_no_match));
        tvRepeatPass.setTextColor(getResources().getColor(R.color.scarlet));
        setDividerAlertStyle(viewDividerRepeatPass);
        if (EmptyUtils.isNotEmpty(getRepeatPassword())) {
            ivRepeatPassClear.setVisibility(View.VISIBLE);
        } else {
            ivRepeatPassClear.setVisibility(View.GONE);
        }
        //edtRepeatPass.setBackground(getResources().getDrawable(R.drawable.selector_edt_bg_scalet));
    }

    public void setClickable(Button button) {
        button.setClickable(true);
        button.setBackgroundColor(getResources().getColor(R.color.cornflowerBlueTwo));
    }

    public void setUnclickable(Button button) {
        // button.setClickable(false);
        button.setBackgroundColor(getResources().getColor(R.color.cloudyBlueTwo));
    }


    public String getPassword() {
        return edtSetPass.getText().toString().trim();
    }

    public String getRepeatPassword() {
        return edtRepeatPass.getText().toString().trim();
    }

    public String getPassHint() {
        return edtPassHint.getText().toString().trim();
    }

    @Override
    protected void onDestroy() {
        clearListeners();
        super.onDestroy();
        //Validate.unreg(this);
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
        edtRepeatPass.setOnFocusChangeListener(null);
        edtSetPass.setOnFocusChangeListener(null);
        edtPassHint.setOnFocusChangeListener(null);
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

    public boolean isPasswordMatch() {
        if (getPassword().equals(getRepeatPassword())) { return true; }
        return false;
    }


    /**
     * 验证框架验证成功回调
     */
    @Override
    public void onValidationSucceeded() {
        //设置初始化PIN
        String password = String.valueOf(edtSetPass.getText());

        mHandler = new BluetoothHandler();
        blueToothThread = new BlueToothWrapper(mHandler);
        blueToothThread.setInitPINWrapper(0, 0, password);
        blueToothThread.start();
    }


    class BluetoothHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BlueToothWrapper.MSG_INIT_PIN_START:
                    showProgressDialog(getString(R.string.progress_set_pin));
                    break;
                case BlueToothWrapper.MSG_INIT_PIN_FINISH:
                    dissmisProgressDialog();
                    //跳转到备份助记词
                    UISkipMananger.skipBackupMneGuideActivity(BluetoothConfigWooKongBioActivity.this, bd);

                    break;

                default:
                    break;
            }
        }
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
