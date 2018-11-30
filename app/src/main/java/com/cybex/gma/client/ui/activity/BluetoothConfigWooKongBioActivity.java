package com.cybex.gma.client.ui.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.AbsoluteSizeSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.cybex.componentservice.WookongUtils;
import com.cybex.componentservice.config.BaseConst;
import com.cybex.componentservice.manager.DeviceOperationManager;
import com.cybex.componentservice.event.DeviceConnectStatusUpdateEvent;
import com.cybex.componentservice.ui.activity.BluetoothBaseActivity;
import com.cybex.componentservice.utils.AlertUtil;
import com.cybex.componentservice.utils.SoftHideKeyBoardUtil;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.presenter.BluetoothConfigWookongBioPresenter;
import com.cybex.gma.client.utils.bluetooth.BlueToothWrapper;
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
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import me.jessyan.autosize.AutoSize;


/**
 * 配置WooKong Bio页面
 */
public class BluetoothConfigWooKongBioActivity extends BluetoothBaseActivity<BluetoothConfigWookongBioPresenter> implements Validator
        .ValidationListener {

    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.tv_in_bubble) TextView tvInBubble;
    @BindView(R.id.bubble) BubbleLayout bubble;
    @BindView(R.id.tv_set_pass) TextView tvSetPass;
    @NotEmpty(messageResId = R.string.eos_pass_not_empty, sequence = 2)
    @Password(min = 8, messageResId = R.string.eos_pass_lenth_invalid, sequence = 2)
    @BindView(R.id.edt_set_pass) EditText edtSetPass;
    @BindView(R.id.iv_set_pass_clear) ImageView ivSetPassClear;
    @BindView(R.id.iv_set_pass_mask) ImageView ivSetPassMask;
    @BindView(R.id.view_divider_setPass) View viewDividerSetPass;
    @BindView(R.id.tv_repeat_pass) TextView tvRepeatPass;
    @NotEmpty(messageResId = R.string.eos_repeat_input_pass, sequence = 1)
    @ConfirmPassword(messageResId = R.string.eos_password_no_match, sequence = 1)
    @BindView(R.id.et_repeat_pass) EditText edtRepeatPass;
    @BindView(R.id.iv_repeat_pass_clear) ImageView ivRepeatPassClear;
    @BindView(R.id.iv_repeat_pass_mask) ImageView ivRepeatPassMask;
    @BindView(R.id.view_divider_repeatPass) View viewDividerRepeatPass;
    @BindView(R.id.tv_pass_hint) TextView tvPassHint;
    @BindView(R.id.edt_pass_hint) EditText edtPassHint;
    @BindView(R.id.iv_pass_hint_clear) ImageView ivPassHintClear;
    @BindView(R.id.bt_create_wallet) Button btCreateWallet;
    @BindView(R.id.scroll_create_wallet) ScrollView scrollCreateWallet;
    @BindView(R.id.tv_wallet_name) TextView tvWalletName;
    @BindView(R.id.view_divider_passTip) View viewDividerPassTip;
    @Checked(messageResId = R.string.eos_check_agreement, sequence = 0)
    @BindView(R.id.checkbox_config) CheckBox checkboxConfig;
    @BindView(R.id.tv_service_agreement_config) TextView tvServiceAgreementConfig;
    @BindView(R.id.layout_checkBox) LinearLayout layoutCheckBox;
    private Validator validator;
    private boolean isMask;
    private BlueToothWrapper blueToothThread;
    private String TAG = this.toString();
    private String deviceName;

    private CustomFullDialog powerAlertDialog;
    //    private BluetoothHandler mHandler;
//    private long contextHandle = 0;
    private Bundle bd;

    @OnTextChanged(value = R.id.edt_set_pass, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterPassChanged(Editable s) {
        if (EmptyUtils.isNotEmpty(getPassword())) {
            ivSetPassClear.setVisibility(View.VISIBLE);
        } else {
            ivSetPassClear.setVisibility(View.GONE);
        }

        if (EmptyUtils.isNotEmpty(getPassword()) && EmptyUtils.isNotEmpty(getRepeatPassword()) && isPasswordMatch()
                && isPasswordLengthValid() && checkboxConfig.isChecked()) {
            setClickable(btCreateWallet);
        } else {
            setUnclickable(btCreateWallet);
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

        if (EmptyUtils.isNotEmpty(getPassword()) && EmptyUtils.isNotEmpty(getRepeatPassword()) && isPasswordMatch()
                && isPasswordLengthValid() && checkboxConfig.isChecked()) {
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

    /**
     * 点击跳转
     *
     * @param v
     */
    @OnClick({R.id.bt_create_wallet})
    public void onButtonClicked(View v) {
        switch (v.getId()) {
            case R.id.bt_create_wallet:
                validator.validate();
                break;
        }
    }

    /**
     * 明文密文切换显示管理
     *
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
        setEditTextHintStyle(edtSetPass, R.string.eos_tip_input_password);
        setEditTextHintStyle(edtRepeatPass, R.string.eos_tip_repeat_password);
        setEditTextHintStyle(edtPassHint, R.string.eos_tip_input_password_hint);
        bubble.setVisibility(View.GONE);

        setNavibarTitle(getResources().getString(R.string.eos_title_wookong_bio), true);
        /**
         * 设置密码输入区域样式设置
         */
        edtSetPass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    setDividerFocusStyle(viewDividerSetPass);
                    tvSetPass.setTextColor(getResources().getColor(R.color.black_title));
                    edtSetPass.setTypeface(Typeface.DEFAULT_BOLD);
                    if (EmptyUtils.isNotEmpty(getPassword())) {
                        ivSetPassClear.setVisibility(View.VISIBLE);
                    }
                } else {
                    setDividerDefaultStyle(viewDividerSetPass);
                    ivSetPassClear.setVisibility(View.GONE);
                    tvSetPass.setTextColor(getResources().getColor(R.color.black_context));
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
                    tvPassHint.setTextColor(getResources().getColor(R.color.black_title));
                    edtPassHint.setTypeface(Typeface.DEFAULT_BOLD);
                } else {
                    ivPassHintClear.setVisibility(View.GONE);
                    tvPassHint.setTextColor(getResources().getColor(R.color.black_context));
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
    protected void onDestroy() {
        DeviceOperationManager.getInstance().clearCallback(this.toString());
        super.onDestroy();
        clearListeners();
//        WookongBioManager.getInstance().freeThread();
//        WookongBioManager.getInstance().freeResource();

    }

    @Override
    public void initData(Bundle savedInstanceState) {

        deviceName = DeviceOperationManager.getInstance().getCurrentDeviceName();
        setUnclickable(btCreateWallet);
        SoftHideKeyBoardUtil.assistActivity(this);
        validator = new Validator(this);
        validator.setValidationListener(this);
        isMask = true;

        initView();
        checkboxConfig.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && EmptyUtils.isNotEmpty(getPassword()) && EmptyUtils.isNotEmpty(getRepeatPassword())) {
                    setClickable(btCreateWallet);
                } else {
                    setUnclickable(btCreateWallet);
                }
            }
        });

        bd = getIntent().getExtras();
        if (bd != null) {
//            contextHandle = bd.getLong(ParamConstants.CONTEXT_HANDLE, 0);
            if (bd.getBoolean(BaseConst.PIN_STATUS)) {
                //如果已初始化
                showInitWookongBioDialog();
            }
        }

    }

    @Override
    public int getLayoutId() {
        return R.layout.eos_activity_bluetooth_config_wookong_bio;
    }

    @Override
    public BluetoothConfigWookongBioPresenter newP() {
        return new BluetoothConfigWookongBioPresenter();
    }

    @Override
    public void onBackPressedSupport() {
        super.onBackPressedSupport();
    }

    @Override
    protected void setNavibarTitle(String title, boolean isShowBack) {
        if (EmptyUtils.isEmpty(title)) { return; }
        mTitleBar = findViewById(com.hxlx.core.lib.R.id.btn_navibar);
        mTitleBar.setTitle(title);
        mTitleBar.setTitleColor(com.hxlx.core.lib.R.color.black_title);
        mTitleBar.setBackgroundColor(getResources().getColor(com.hxlx.core.lib.R.color.ffffff_white_1000));
        mTitleBar.setTitleSize(18);
        mTitleBar.setTitleBold(true);
        mTitleBar.setImmersive(true);

        if (isShowBack) {
            mTitleBar.setLeftImageResource(com.hxlx.core.lib.R.drawable.ic_notify_back);
            mTitleBar.setLeftClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DeviceOperationManager.getInstance().freeContext(TAG, deviceName,
                            new DeviceOperationManager.FreeContextCallback() {
                                @Override
                                public void onFreeStart() {

                                }

                                @Override
                                public void onFreeSuccess() {
                                    finish();
                                }

                                @Override
                                public void onFreeFailed() {

                                }
                            });
                }
            });
        }

    }

    public void setRepeatPassValidStyle() {
        //两次输入密码匹配
        tvRepeatPass.setText(getResources().getString(R.string.eos_tip_repeat_pass));
        tvRepeatPass.setTextColor(getResources().getColor(R.color.black_context));
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
        tvRepeatPass.setTextColor(getResources().getColor(R.color.black_title));
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
        button.setBackground(getDrawable(R.drawable.shape_corner_button_clickable));
        button.setTextColor(getResources().getColor(R.color.whiteTwo));
    }

    public void setUnclickable(Button button) {
        // button.setClickable(false);
        //button.setBackground(getDrawable(R.drawable.shape_corner_with_black_stroke));
        button.setBackground(getDrawable(R.drawable.shape_corner_button_unclickable));
        // button.setTextColor(getResources().getColor(R.color.black_title));
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
        divider.setBackgroundColor(getResources().getColor(R.color.black_title));

    }

    public void setDividerDefaultStyle(View divider) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2);
        setHorizontalMargins(params, (int) getResources().getDimension(R.dimen.dimen_12), (int) getResources()
                .getDimension(R.dimen.dimen_12));
        divider.setLayoutParams(params);
        divider.setBackgroundColor(getResources().getColor(R.color.very_light_pink));

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
        return getPassword().equals(getRepeatPassword());
    }

    public boolean isPasswordLengthValid() {
        return getPassword().length() >= 8 && getRepeatPassword().length() >= 8;
    }


    /**
     * 验证框架验证成功回调
     */
    @Override
    public void onValidationSucceeded() {
        showInitWookongBioDialog();


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


    /**
     * 显示Wookong bio对话框
     */
    private void showInitWookongBioDialog() {
        int[] listenedItems = {R.id.btn_close, R.id.btn_create_wallet, R.id.btn_import_mne};

        AutoSize.autoConvertDensityOfGlobal(BluetoothConfigWooKongBioActivity.this);

        CustomFullDialog dialog = new CustomFullDialog(this,
                R.layout.eos_dialog_un_init_wookongbio, listenedItems, false, Gravity.BOTTOM);


        dialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
            @Override
            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {

                switch (view.getId()) {
                    case R.id.btn_close:
                        dialog.cancel();

                        break;
                    case R.id.btn_create_wallet:
                        //验证电量
                        showProgressDialog(getString(R.string.creating_wookong_wallet));
                        WookongUtils.validatePowerLevel(ParamConstants.POWER_LEVEL_ALERT_INIT,
                                new WookongUtils.ValidatePowerLevelCallback() {
                                    @Override
                                    public void onValidateSuccess() {
                                        //按键验证
                                        DeviceOperationManager.getInstance().pressConfirm(
                                                TAG,
                                                deviceName,
                                                new DeviceOperationManager.PressConfirmCallback() {
                                                    @Override
                                                    public void onConfirmSuccess() {
                                                        //init PIN
                                                        String password = String.valueOf(edtSetPass.getText());
                                                        String passwordHint = String.valueOf(edtPassHint.getText());

                                                        String currentDeviceName = DeviceOperationManager.getInstance().getCurrentDeviceName();

                                                        DeviceOperationManager.getInstance()
                                                                .initPin(this.toString(), currentDeviceName, password, passwordHint,
                                                                        new DeviceOperationManager.InitPinCallback() {
                                                                            @Override
                                                                            public void onInitSuccess() {
                                                                                dissmisProgressDialog();
                                                                                UISkipMananger.skipBackupMneGuideActivity(
                                                                                        BluetoothConfigWooKongBioActivity.this,
                                                                                        null);
                                                                                finish();
                                                                            }

                                                                            @Override
                                                                            public void onInitFail() {
                                                                                dissmisProgressDialog();
                                                                                GemmaToastUtils.showLongToast(
                                                                                        getString(R.string.wookong_init_pin_fail));

                                                                            }
                                                                        });
                                                    }

                                                    @Override
                                                    public void onConfirmFailed() {
                                                        dissmisProgressDialog();
                                                    }
                                                }
                                        );
                                    }

                                    @Override
                                    public void onValidateFail() {
                                        dissmisProgressDialog();
                                        showPowerLevelAlertDialog();
                                    }
                                });

                        dialog.cancel();
                        break;

                    case R.id.btn_import_mne:
                        //验证电量
                        showProgressDialog(getString(R.string.creating_wookong_wallet));
                        WookongUtils.validatePowerLevel(ParamConstants.POWER_LEVEL_ALERT_INIT,
                                new WookongUtils.ValidatePowerLevelCallback() {
                                    @Override
                                    public void onValidateSuccess() {
                                        //按键确认
                                        DeviceOperationManager.getInstance().pressConfirm(TAG, deviceName,
                                                new DeviceOperationManager.PressConfirmCallback() {
                                                    @Override
                                                    public void onConfirmSuccess() {
                                                        //init PIN
                                                        String password = String.valueOf(edtSetPass.getText());
                                                        String passwordHint = String.valueOf(edtPassHint.getText());

                                                        String currentDeviceName = DeviceOperationManager.getInstance().getCurrentDeviceName();
                                                        DeviceOperationManager.getInstance()
                                                                .initPin(this.toString(), currentDeviceName, password, passwordHint,
                                                                        new DeviceOperationManager.InitPinCallback() {
                                                                            @Override
                                                                            public void onInitSuccess() {
                                                                                //跳转到创建账户名界面
                                                                                dissmisProgressDialog();
                                                                                dialog.cancel();
                                                                                Intent intent = new Intent(BluetoothConfigWooKongBioActivity.this,
                                                                                        BluetoothImportWalletActivity.class);
                                                                                startActivity(intent);
                                                                                finish();
                                                                            }

                                                                            @Override
                                                                            public void onInitFail() {
                                                                                dissmisProgressDialog();
                                                                                GemmaToastUtils.showLongToast(
                                                                                        getString(R.string.wookong_init_pin_fail));
                                                                            }
                                                                        });
                                                    }

                                                    @Override
                                                    public void onConfirmFailed() {
                                                        dissmisProgressDialog();
                                                    }
                                                });
                                    }

                                    @Override
                                    public void onValidateFail() {
                                        dissmisProgressDialog();
                                        showPowerLevelAlertDialog();
                                    }
                                });

                        break;
                    default:
                        break;
                }
            }
        });
        dialog.show();
    }

    /**
     * 显示电量不足dialog
     */
    private void showPowerLevelAlertDialog() {
        if (powerAlertDialog != null) { return; }
        int[] listenedItems = {R.id.tv_i_understand};
        powerAlertDialog = new CustomFullDialog(this,
                R.layout.dialog_bluetooth_power_level_alert, listenedItems, false, Gravity.BOTTOM);
        powerAlertDialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
            @Override
            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.tv_i_understand:
                        powerAlertDialog.cancel();
                        finish();
                        break;
                    default:
                        break;
                }
            }
        });
        powerAlertDialog.show();
    }

//    class BluetoothHandler extends Handler {
//
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case BlueToothWrapper.MSG_INIT_PIN_START:
//                    LoggerManager.d("MSG_INIT_PIN_START");
//                    //设置PIN
//                    showProgressDialog(getString(R.string.eos_progress_set_pin));
//                    break;
//                case BlueToothWrapper.MSG_INIT_PIN_FINISH:
//                    LoggerManager.d("MSG_INIT_PIN_FINISH");
//                    //已完成设置PIN
//
//                    //跳转到创建账户名界面
//                    String password = String.valueOf(edtSetPass.getText());
//                    String pwdTip = String.valueOf(edtPassHint.getText());
//                    BluetoothAccountInfoVO vo = new BluetoothAccountInfoVO();
//                    vo.setPassword(password);
//                    vo.setPasswordTip(pwdTip);
//
//                    if (bd != null) {
//                        bd.putParcelable(ParamConstants.KEY_BLUETOOTH_ACCOUNT_INFO, vo);
//                    }
//
////                    WookongBioManager.getInstance().getAddress(contextHandle, 0, MiddlewareInterface
////                            .PAEW_COIN_TYPE_EOS, CacheConstants.EOS_DERIVE_PATH);
//
//                    break;
//                case BlueToothWrapper.MSG_GET_ADDRESS_FINISH:
//                    LoggerManager.d("MSG_GET_ADDRESS_FINISH");
//                    BlueToothWrapper.GetAddressReturnValue returnValueAddress = (BlueToothWrapper
//                            .GetAddressReturnValue) msg.obj;
//                    if (returnValueAddress.getReturnValue() == MiddlewareInterface.PAEW_RET_SUCCESS) {
//                        if (returnValueAddress.getCoinType() == MiddlewareInterface.PAEW_COIN_TYPE_EOS) {
//
//                            String[] strArr = returnValueAddress.getAddress().split("####");
//                            String publicKey = strArr[0];
//
//                            DeviceInfoEvent event = new DeviceInfoEvent();
//                            event.setEosPublicKey(publicKey);
//                            EventBusProvider.postSticky(event);
//
//                            final String deviceName = SPUtils.getInstance().getString(ParamConstants.DEVICE_NAME);
//                            if (deviceName != null)getP().creatBluetoothWallet(deviceName, publicKey);
//                            UISkipMananger.skipBackupMneGuideActivity(BluetoothConfigWooKongBioActivity.this, bd);
//                            finish();
//
//                        }else {
//                            GemmaToastUtils.showLongToast("Bio Communication Error");
//                        }
//                    }
//                    dissmisProgressDialog();
//
//                    break;
//
//                default:
//                    break;
//            }
//        }
//    }


    @Override
    public boolean useEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveDeviceConnectEvent(DeviceConnectStatusUpdateEvent event){
        fixDeviceDisconnectEvent(event);
    }


}
