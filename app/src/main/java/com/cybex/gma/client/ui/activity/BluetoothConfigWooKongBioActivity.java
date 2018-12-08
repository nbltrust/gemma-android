package com.cybex.gma.client.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.AbsoluteSizeSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.cybex.componentservice.WookongUtils;
import com.cybex.componentservice.api.ApiPath;
import com.cybex.componentservice.config.BaseConst;
import com.cybex.componentservice.manager.DeviceOperationManager;
import com.cybex.componentservice.event.DeviceConnectStatusUpdateEvent;
import com.cybex.componentservice.ui.activity.BluetoothBaseActivity;
import com.cybex.componentservice.ui.activity.CommonWebViewActivity;
import com.cybex.componentservice.utils.AlertUtil;
import com.cybex.componentservice.utils.SoftHideKeyBoardUtil;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.presenter.BluetoothConfigWookongBioPresenter;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.KeyboardUtils;
import com.hxlx.core.lib.utils.LanguageManager;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.siberiadante.customdialoglib.CustomFullDialog;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import me.jessyan.autosize.AutoSize;


/**
 * 配置WooKong Bio页面
 */
public class BluetoothConfigWooKongBioActivity extends BluetoothBaseActivity<BluetoothConfigWookongBioPresenter> {


    ImageView ivSetPassMask;
    ImageView ivRepeatPassMask;
    View viewDividerWalletName;
    View viewDividerSetPass;
    View viewDividerRepeatPass;
    View viewDividerPassHint;

    ScrollView scrollViewCreateWallet;
    TextView tvWalletName;
    ViewGroup llWalletname;
    ImageView ivWalletNameClear;
    ImageView ivSetPassClear;
    ImageView ivRepeatPassClear;
    ImageView ivPassHintClear;
    EditText edtWalletName;
    TextView tvSetPass;
    EditText edtSetPass;
    TextView tvRepeatPass;
    EditText edtRepeatPass;
    TextView tvPassHint;
    EditText edtPassHint;
    CheckBox checkboxConfig;
    TextView tvServiceAgreementConfig;
    LinearLayout layoutCheckBox;
    Button btCreateWallet;



    private boolean isMask;//true为密文显示密码
    private boolean isInitial;


    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
//    @BindView(R.id.tv_in_bubble) TextView tvInBubble;
//    @BindView(R.id.bubble) BubbleLayout bubble;
//    @BindView(R.id.tv_set_pass) TextView tvSetPass;
//    @NotEmpty(messageResId = R.string.eos_pass_not_empty, sequence = 2)
//    @Password(min = 8, messageResId = R.string.eos_pass_lenth_invalid, sequence = 2)
//    @BindView(R.id.edt_set_pass) EditText edtSetPass;
//    @BindView(R.id.iv_set_pass_clear) ImageView ivSetPassClear;
//    @BindView(R.id.iv_set_pass_mask) ImageView ivSetPassMask;
//    @BindView(R.id.view_divider_setPass) View viewDividerSetPass;
//    @BindView(R.id.tv_repeat_pass) TextView tvRepeatPass;
//    @NotEmpty(messageResId = R.string.eos_repeat_input_pass, sequence = 1)
//    @ConfirmPassword(messageResId = R.string.eos_password_no_match, sequence = 1)
//    @BindView(R.id.et_repeat_pass) EditText edtRepeatPass;
//    @BindView(R.id.iv_repeat_pass_clear) ImageView ivRepeatPassClear;
//    @BindView(R.id.iv_repeat_pass_mask) ImageView ivRepeatPassMask;
//    @BindView(R.id.view_divider_repeatPass) View viewDividerRepeatPass;
//    @BindView(R.id.tv_pass_hint) TextView tvPassHint;
//    @BindView(R.id.edt_pass_hint) EditText edtPassHint;
//    @BindView(R.id.iv_pass_hint_clear) ImageView ivPassHintClear;
//    @BindView(R.id.bt_create_wallet) Button btCreateWallet;
//    @BindView(R.id.scroll_create_wallet) ScrollView scrollCreateWallet;
//    @BindView(R.id.tv_wallet_name) TextView tvWalletName;
//    @BindView(R.id.view_divider_passTip) View viewDividerPassTip;
//    @Checked(messageResId = R.string.eos_check_agreement, sequence = 0)
//    @BindView(R.id.checkbox_config) CheckBox checkboxConfig;
//    @BindView(R.id.tv_service_agreement_config) TextView tvServiceAgreementConfig;
//    @BindView(R.id.layout_checkBox) LinearLayout layoutCheckBox;
//    private Validator validator;
//    private boolean isMask;



    private String TAG = this.toString();
    private String deviceName;

    private CustomFullDialog powerAlertDialog;
    private Bundle bd;







//    @OnTextChanged(value = R.id.edt_set_pass, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
//    public void afterPassChanged(Editable s) {
//        if (EmptyUtils.isNotEmpty(getPassword())) {
//            ivSetPassClear.setVisibility(View.VISIBLE);
//        } else {
//            ivSetPassClear.setVisibility(View.GONE);
//        }
//
//        if (EmptyUtils.isNotEmpty(getPassword()) && EmptyUtils.isNotEmpty(getRepeatPassword()) && isPasswordMatch()
//                && isPasswordLengthValid() && checkboxConfig.isChecked()) {
//            setClickable(btCreateWallet);
//        } else {
//            setUnclickable(btCreateWallet);
//        }
//    }
//
//    @OnTextChanged(value = R.id.et_repeat_pass, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
//    public void afterRepeatPassChanged(Editable s) {
//        if (EmptyUtils.isEmpty(getRepeatPassword())) {
//            ivRepeatPassClear.setVisibility(View.GONE);
//            if (edtRepeatPass.hasFocus()) { setRepeatPassFocusStyle(); }
//        } else {
//            ivRepeatPassClear.setVisibility(View.VISIBLE);
//        }
//
//        if (EmptyUtils.isNotEmpty(getPassword()) && EmptyUtils.isNotEmpty(getRepeatPassword()) && isPasswordMatch()
//                && isPasswordLengthValid() && checkboxConfig.isChecked()) {
//            setClickable(btCreateWallet);
//        } else {
//            setUnclickable(btCreateWallet);
//        }
//
//    }
//
//    @OnTextChanged(value = R.id.edt_pass_hint, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
//    public void afterPassHintChanged(Editable s) {
//        if (EmptyUtils.isNotEmpty(getPassHint())) {
//            ivPassHintClear.setVisibility(View.VISIBLE);
//        } else {
//            ivPassHintClear.setVisibility(View.GONE);
//        }
//    }
//
//    /**
//     * 明文密文切换显示管理
//     *
//     * @param v
//     */
//    @OnClick({R.id.iv_set_pass_mask, R.id.iv_repeat_pass_mask})
//    public void onMaskClicked(View v) {
//        switch (v.getId()) {
//            case R.id.iv_set_pass_mask:
//                if (isMask) {
//                    //如果当前为密文
//                    isMask = false;
//                    edtSetPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
//                    ivSetPassMask.setImageResource(R.drawable.ic_invisible);
//                    edtSetPass.setSelection(getPassword().length());
//                } else {
//                    isMask = true;
//                    edtSetPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
//                    ivSetPassMask.setImageResource(R.drawable.ic_visible);
//                    edtSetPass.setSelection(getPassword().length());
//                }
//                break;
//            case R.id.iv_repeat_pass_mask:
//                if (isMask) {
//                    //如果当前为密文
//                    isMask = false;
//                    edtRepeatPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
//                    ivRepeatPassMask.setImageResource(R.drawable.ic_invisible);
//                    edtRepeatPass.setSelection(getRepeatPassword().length());
//                } else {
//                    //如果当前为明文
//                    isMask = true;
//                    edtRepeatPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
//                    ivRepeatPassMask.setImageResource(R.drawable.ic_visible);
//                    edtRepeatPass.setSelection(getRepeatPassword().length());
//                }
//                break;
//        }
//    }
//
//    /**
//     * 清除按钮点击事件
//     *
//     * @param v
//     */
//    @OnClick({R.id.iv_set_pass_clear, R.id.iv_repeat_pass_clear, R.id.iv_pass_hint_clear})
//    public void onClearClicked(View v) {
//        switch (v.getId()) {
//            case R.id.iv_set_pass_clear:
//                edtSetPass.setText("");
//                break;
//            case R.id.iv_pass_hint_clear:
//                edtPassHint.setText("");
//                break;
//            case R.id.iv_repeat_pass_clear:
//                edtRepeatPass.setText("");
//                break;
//        }
//    }

    public void initView() {
        //动态设置hint样式
        setEditTextHintStyle(edtWalletName, R.string.tip_walletname_hint);
        setEditTextHintStyle(edtSetPass, R.string.tip_input_password);
        setEditTextHintStyle(edtRepeatPass, R.string.tip_repeat_password);
        setEditTextHintStyle(edtPassHint, R.string.tip_input_password_hint);

        setNavibarTitle(getResources().getString(R.string.eos_title_wookong_bio), true);

        setUnclickable(btCreateWallet);
        checkboxConfig.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && isAllTextFilled() && getP().isWalletNameValid() && getP().isPasswordMatch()) {
                    setClickable(btCreateWallet);
                } else {
                    setUnclickable(btCreateWallet);
                }
            }
        });
        /**
         * walletname输入区域样式设置
         */
        edtWalletName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (EmptyUtils.isEmpty(getWalletName())) {
                    setWalletNameValidStyle();
                    if (hasFocus) { setWalletNameFocusedStyle(); }
                } else {
                    if (getP().isWalletNameValid()) {
                        setWalletNameValidStyle();
                        if (hasFocus) { setWalletNameFocusedStyle(); }
                    } else {
                        setUnclickable(btCreateWallet);
                        setWalletNameInvalidStyle();
                    }
                }

                if (hasFocus) {
                    setDividerFocusStyle(viewDividerWalletName);
                    edtWalletName.setTypeface(Typeface.DEFAULT_BOLD);
                    if (EmptyUtils.isNotEmpty(getWalletName())){
                        ivWalletNameClear.setVisibility(View.VISIBLE);
                    }

                    edtWalletName.requestFocus();
                    KeyboardUtils.showKeyBoard(context,edtWalletName);
                } else {
                    setDividerDefaultStyle(viewDividerWalletName);
                    ivWalletNameClear.setVisibility(View.GONE);
                    if (EmptyUtils.isEmpty(getWalletName())) {
                        edtWalletName.setTypeface(Typeface.DEFAULT);
                    }
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
                    setDividerFocusStyle(viewDividerSetPass);
                    tvSetPass.setTextColor(getResources().getColor(R.color.black_content));
                    edtSetPass.setTypeface(Typeface.DEFAULT_BOLD);
                    if (EmptyUtils.isNotEmpty(getPassword())){
                        ivSetPassClear.setVisibility(View.VISIBLE);
                    }
                    edtSetPass.requestFocus();
                    KeyboardUtils.showKeyBoard(context,edtSetPass);
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
                    edtRepeatPass.requestFocus();
                    KeyboardUtils.showKeyBoard(context,edtRepeatPass);
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
                    tvPassHint.setTextColor(getResources().getColor(R.color.black_content));
                    edtPassHint.setTypeface(Typeface.DEFAULT_BOLD);
                    setDividerFocusStyle(viewDividerPassHint);
                    edtPassHint.requestFocus();
                    KeyboardUtils.showKeyBoard(context,edtPassHint);
                } else {
                    ivPassHintClear.setVisibility(View.GONE);
                    tvPassHint.setTextColor(getResources().getColor(R.color.black_context));
                    setDividerDefaultStyle(viewDividerPassHint);
                    if (EmptyUtils.isEmpty(getPassHint())) { edtPassHint.setTypeface(Typeface.DEFAULT); }
                }
            }
        });


        OverScrollDecoratorHelper.setUpOverScroll(scrollViewCreateWallet);
    }

    @Override
    public void bindUI(View rootView) {
        //ButterKnife.bind(this);


        scrollViewCreateWallet = (ScrollView) findViewById(R.id.scroll_create_wallet);
        tvWalletName = (TextView) findViewById(R.id.tv_wallet_name);
        edtWalletName = (EditText) findViewById(R.id.edt_wallet_name);
        llWalletname = (ViewGroup) findViewById(R.id.ll_walletname);
        ivWalletNameClear = (ImageView) findViewById(R.id.iv_wallet_name_clear);
        viewDividerWalletName = findViewById(R.id.view_divider_walletName);
        tvSetPass = (TextView) findViewById(R.id.tv_set_pass);
        edtSetPass = (EditText) findViewById(R.id.edt_set_pass);
        ivSetPassClear = (ImageView) findViewById(R.id.iv_set_pass_clear);
        ivSetPassMask = (ImageView) findViewById(R.id.iv_set_pass_mask);
        viewDividerSetPass = findViewById(R.id.view_divider_setPass);
        tvRepeatPass = (TextView) findViewById(R.id.tv_repeat_pass);
        edtRepeatPass = (EditText) findViewById(R.id.et_repeat_pass);
        ivRepeatPassClear = (ImageView) findViewById(R.id.iv_repeat_pass_clear);
        ivRepeatPassMask = (ImageView) findViewById(R.id.iv_repeat_pass_mask);
        viewDividerRepeatPass = findViewById(R.id.view_divider_repeatPass);
        tvPassHint = (TextView) findViewById(R.id.tv_pass_hint);
        edtPassHint = (EditText) findViewById(R.id.edt_pass_hint);
        viewDividerPassHint = findViewById(R.id.view_divider_pass_hint);
        ivPassHintClear = (ImageView) findViewById(R.id.iv_pass_hint_clear);
        layoutCheckBox = (LinearLayout) findViewById(R.id.layout_checkBox);
        checkboxConfig = (CheckBox) findViewById(R.id.checkbox_config);
        tvServiceAgreementConfig = (TextView) findViewById(R.id.tv_service_agreement_config);
        btCreateWallet = (Button) findViewById(R.id.bt_create_wallet);
        setNavibarTitle(getResources().getString(R.string.tip_create_wallet), true);



        edtWalletName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isAllTextFilled() && checkboxConfig.isChecked() && getP().isPasswordMatch()) {
                    setClickable(btCreateWallet);
                } else {
                    setUnclickable(btCreateWallet);
                }

                if (EmptyUtils.isEmpty(getWalletName())) {
                    if (edtWalletName.hasFocus()) { setWalletNameFocusedStyle(); }
                } else  {
                    if (getP().isWalletNameValid()) {
                        if(edtWalletName.hasFocus()){
                            setWalletNameFocusedStyle();
                        }
                    } else {
                        setUnclickable(btCreateWallet);
                        setWalletNameInvalidStyle();
                    }
                }

                if (EmptyUtils.isNotEmpty(getWalletName())) {
                    ivWalletNameClear.setVisibility(View.VISIBLE);
                } else {
                    ivWalletNameClear.setVisibility(View.GONE);
                }
            }
        });

        edtSetPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
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
        });

        edtRepeatPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
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
        });

        edtPassHint.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (EmptyUtils.isNotEmpty(getPassHint())) {
                    ivPassHintClear.setVisibility(View.VISIBLE);
                } else {
                    ivPassHintClear.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        DeviceOperationManager.getInstance().clearCallback(this.toString());
        super.onDestroy();
        clearListeners();
    }

    @Override
    public void initData(Bundle savedInstanceState) {

        deviceName = DeviceOperationManager.getInstance().getCurrentDeviceName();
        setUnclickable(btCreateWallet);
        SoftHideKeyBoardUtil.assistActivity(this);
        isMask = true;


        edtWalletName.setText(BaseConst.INITIAL_BIO_WALLET_NAME);
        isInitial = true;
        llWalletname.setVisibility(View.GONE);
        tvWalletName.setVisibility(View.GONE);
        viewDividerWalletName.setVisibility(View.GONE);

        initView();
        btCreateWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkValidation()){
                    showInitWookongBioDialog();
                }
            }
        });
        tvServiceAgreementConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int savedLanguageType = LanguageManager.getInstance(context).getLanguageType();
                switch (savedLanguageType) {
                    case LanguageManager.LanguageType.LANGUAGE_CHINESE_SIMPLIFIED:
                        CommonWebViewActivity.startWebView(context, ApiPath.TERMS_OF_SERVICE_CN, getResources().getString(R
                                .string.terms_of_service));
                        break;
                    case LanguageManager.LanguageType.LANGUAGE_EN:
                        CommonWebViewActivity.startWebView(context, ApiPath.TERMS_OF_SERVICE_EN, getResources().getString(R
                                .string.terms_of_service));
                        break;
                    case LanguageManager.LanguageType.LANGUAGE_FOLLOW_SYSTEM:
                        Locale systemLanguageType = LanguageManager.getInstance(context).getSysLocale();
                        switch (systemLanguageType.getDisplayLanguage()) {
                            case BaseConst.CN:
                                CommonWebViewActivity.startWebView(context, ApiPath.TERMS_OF_SERVICE_CN, getResources()
                                        .getString(R.string.terms_of_service));
                                break;
                            case BaseConst.EN:
                                CommonWebViewActivity.startWebView(context, ApiPath.TERMS_OF_SERVICE_EN, getResources()
                                        .getString(R.string.terms_of_service));
                                break;
                            default:
                                CommonWebViewActivity.startWebView(context, ApiPath.TERMS_OF_SERVICE_CN, getResources()
                                        .getString(R.string.terms_of_service));
                        }
                        break;
                    default:
                        CommonWebViewActivity.startWebView(context, ApiPath.TERMS_OF_SERVICE_CN, getResources().getString(R
                                .string.terms_of_service));
                }
            }
        });

        ivSetPassMask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });

        ivRepeatPassMask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });
        ivWalletNameClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtWalletName.setText("");
            }
        });

        ivSetPassClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtSetPass.setText("");
            }
        });

        ivRepeatPassClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtRepeatPass.setText("");
            }
        });

        ivPassHintClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtPassHint.setText("");
            }
        });


//        bd = getIntent().getExtras();
//        if (bd != null) {
////            contextHandle = bd.getLong(ParamConstants.CONTEXT_HANDLE, 0);
//            if (bd.getBoolean(BaseConst.PIN_STATUS)) {
//                //如果已初始化
//                showInitWookongBioDialog();
//            }
//        }

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

    //----
    public void setWalletNameValidStyle() {
        tvWalletName.setTextColor(getResources().getColor(R.color.black_context));
        tvWalletName.setText(getResources().getString(R.string.title_wallet_name));
        setDividerDefaultStyle(viewDividerWalletName);
    }

    public void setWalletNameInvalidStyle() {

        tvWalletName.setText(getResources().getString(R.string.tip_walletname_error));
        tvWalletName.setTextColor(getResources().getColor(R.color.scarlet));
        setDividerAlertStyle(viewDividerWalletName);
    }

    public void setWalletNameFocusedStyle() {
        tvWalletName.setTextColor(getResources().getColor(R.color.black_title));
        tvWalletName.setText(getResources().getString(R.string.title_wallet_name));
        setDividerFocusStyle(viewDividerWalletName);
    }

    public void setRepeatPassValidStyle() {
        //两次输入密码匹配
        tvRepeatPass.setText(getResources().getString(R.string.tip_repeat_pass));
        tvRepeatPass.setTextColor(getResources().getColor(R.color.black_context));
        setDividerDefaultStyle(viewDividerRepeatPass);
        if (EmptyUtils.isNotEmpty(getRepeatPassword())) {
            ivRepeatPassClear.setVisibility(View.VISIBLE);
        } else {
            ivRepeatPassClear.setVisibility(View.GONE);
        }
        //edtRepeatPass.setBackground(getResources().getDrawable(R.drawable.selector_edt_bg));
    }

    public void setRepeatPassFocusStyle() {
        tvRepeatPass.setText(getResources().getString(R.string.tip_repeat_pass));
        tvRepeatPass.setTextColor(getResources().getColor(R.color.black_title));
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
        tvRepeatPass.setText(getResources().getString(R.string.tip_pass_no_match));
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
//        button.setBackgroundColor(getResources().getColor(R.color.btn_clickable));
        button.setEnabled(true);
    }

    public void setUnclickable(Button button) {
//        button.setBackgroundColor(getResources().getColor(R.color.btn_unclickable));
        button.setEnabled(false);
    }

    public String getWalletName() {
        return edtWalletName.getText().toString().trim();
    }

    public String getPassword() {
        return edtSetPass.getText().toString();
    }

    public String getRepeatPassword() {
        return edtRepeatPass.getText().toString();
    }

    public String getPassHint() {
        return edtPassHint.getText().toString().trim();
    }
    //---------

//    public void setRepeatPassValidStyle() {
//        //两次输入密码匹配
//        tvRepeatPass.setText(getResources().getString(R.string.eos_tip_repeat_pass));
//        tvRepeatPass.setTextColor(getResources().getColor(R.color.black_context));
//        setDividerDefaultStyle(viewDividerRepeatPass);
//        if (EmptyUtils.isNotEmpty(getRepeatPassword())) {
//            ivRepeatPassClear.setVisibility(View.VISIBLE);
//        } else {
//            ivRepeatPassClear.setVisibility(View.GONE);
//        }
//        //edtRepeatPass.setBackground(getResources().getDrawable(R.drawable.eos_selector_edt_bg));
//    }

//    public void setRepeatPassFocusStyle() {
//        tvRepeatPass.setText(getResources().getString(R.string.eos_tip_repeat_pass));
//        tvRepeatPass.setTextColor(getResources().getColor(R.color.black_title));
//        setDividerFocusStyle(viewDividerRepeatPass);
//        if (EmptyUtils.isNotEmpty(getRepeatPassword())) {
//            ivRepeatPassClear.setVisibility(View.VISIBLE);
//        } else {
//            ivRepeatPassClear.setVisibility(View.GONE);
//        }
//        //edtRepeatPass.setBackground(getResources().getDrawable(R.drawable.eos_selector_edt_bg));
//    }

//    public void setRepeatPassInvalidStyle() {
//        //两次输入密码不匹配
//        tvRepeatPass.setText(getResources().getString(R.string.eos_tip_pass_no_match));
//        tvRepeatPass.setTextColor(getResources().getColor(R.color.scarlet));
//        setDividerAlertStyle(viewDividerRepeatPass);
//        if (EmptyUtils.isNotEmpty(getRepeatPassword())) {
//            ivRepeatPassClear.setVisibility(View.VISIBLE);
//        } else {
//            ivRepeatPassClear.setVisibility(View.GONE);
//        }
//        //edtRepeatPass.setBackground(getResources().getDrawable(R.drawable.eos_selector_edt_bg_scalet));
//    }
//
//    public void setClickable(Button button) {
//        //button.setClickable(true);
//        button.setBackground(getDrawable(R.drawable.shape_corner_button_clickable));
//        button.setTextColor(getResources().getColor(R.color.whiteTwo));
//    }
//
//    public void setUnclickable(Button button) {
//        // button.setClickable(false);
//        //button.setBackground(getDrawable(R.drawable.shape_corner_with_black_stroke));
//        button.setBackground(getDrawable(R.drawable.shape_corner_button_unclickable));
//        // button.setTextColor(getResources().getColor(R.color.black_title));
//    }

//    public String getPassword() {
//        return edtSetPass.getText().toString().trim();
//    }
//
//    public String getRepeatPassword() {
//        return edtRepeatPass.getText().toString().trim();
//    }
//
//    public String getPassHint() {
//        return edtPassHint.getText().toString().trim();
//    }

    /**
     * 代替监听器检查是否所有edittext输入框都不为空值
     *
     * @return
     */
    public boolean isAllTextFilled() {
        return !EmptyUtils.isEmpty(getPassword())
                && !EmptyUtils.isEmpty(getRepeatPassword())
                && !EmptyUtils.isEmpty(getWalletName());
    }

    public void setEditTextHintStyle(EditText editText, int resId) {
        String hintStr = getResources().getString(resId);
        SpannableString ss = new SpannableString(hintStr);
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(14, true);
//        editText.setHintTextColor(getResources().getColor(R.color.cloudyBlue));
        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        editText.setHint(new SpannableString(ss));
    }

    public void clearListeners() {
        edtWalletName.setOnFocusChangeListener(null);
        edtRepeatPass.setOnFocusChangeListener(null);
        edtSetPass.setOnFocusChangeListener(null);
        edtPassHint.setOnFocusChangeListener(null);
        checkboxConfig.setOnCheckedChangeListener(null);
    }

    public void setDividerFocusStyle(View divider) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                3);
        setHorizontalMargins(params, (int) getResources().getDimension(R.dimen.common_page_content_padding), (int) getResources()
                .getDimension(R.dimen.common_page_content_padding));
        divider.setLayoutParams(params);
        divider.setBackgroundColor(getResources().getColor(R.color.black_title));

    }

    public void setDividerDefaultStyle(View divider) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2);
        setHorizontalMargins(params, (int) getResources().getDimension(R.dimen.common_page_content_padding), (int) getResources()
                .getDimension(R.dimen.common_page_content_padding));
        divider.setLayoutParams(params);
        divider.setBackgroundColor(getResources().getColor(R.color.dddddd_grey_350));

    }

    public void setDividerAlertStyle(View divider) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 3);
        setHorizontalMargins(params, (int) getResources().getDimension(R.dimen.common_page_content_padding), (int) getResources()
                .getDimension(R.dimen.common_page_content_padding));
        divider.setBackgroundColor(getResources().getColor(R.color.scarlet));
    }

    public void setHorizontalMargins(LinearLayout.LayoutParams params, int marginStart, int marginEnd) {
        params.setMarginStart(marginStart);
        params.setMarginEnd(marginEnd);
    }

    private boolean checkValidation() {
        String walletName = getWalletName();
        if(TextUtils.isEmpty(walletName)){
            GemmaToastUtils.showLongToast(getResources().getString(R.string.name_not_empty));
            return false;
        }

        String password = getPassword();
        if(TextUtils.isEmpty(password)){
            GemmaToastUtils.showLongToast(getResources().getString(R.string.pass_not_empty));
            return false;
        }
        if(!getP().isPasswordValid()){
            GemmaToastUtils.showLongToast(getResources().getString(R.string.pass_lenth_invalid));
            return false;
        }

        String repeatPassword = getRepeatPassword();
        if(TextUtils.isEmpty(repeatPassword)){
            GemmaToastUtils.showLongToast(getResources().getString(R.string.repeat_input_pass));
            return false;
        }
        if(!getP().isPasswordMatch()){
            GemmaToastUtils.showLongToast(getResources().getString(R.string.password_no_match));
            return false;
        }

        if (!checkboxConfig.isChecked()) {
            GemmaToastUtils.showLongToast(getResources().getString(R.string.check_agreement));
            return false;
        }

        if (!getP().isWalletNameValid()) {
            AlertUtil.showShortUrgeAlert(this, getString(R.string.name_invalid));
            return false;
        }

        return true;
    }

    public boolean isInitial() {
        return isInitial;
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
                        dialog.cancel();
                        showProgressDialog(getString(R.string.creating_wookong_wallet));
                        WookongUtils.validatePowerLevel(ParamConstants.POWER_LEVEL_ALERT_INIT,
                                new WookongUtils.ValidatePowerLevelCallback() {
                                    @Override
                                    public void onValidateSuccess() {
                                        //init PIN
                                        String password = String.valueOf(edtSetPass.getText());
                                        String passwordHint = String.valueOf(edtPassHint.getText());

                                        String currentDeviceName = DeviceOperationManager.getInstance().getCurrentDeviceName();
                                        isInitingPin=true;
                                        DeviceOperationManager.getInstance()
                                                .initPin(this.toString(), currentDeviceName, password, passwordHint,
                                                        new DeviceOperationManager.InitPinCallback() {
                                                            @Override
                                                            public void onInitSuccess() {
                                                                isInitingPin=false;
                                                                dissmisProgressDialog();
                                                                UISkipMananger.skipBackupMneGuideActivity(
                                                                        BluetoothConfigWooKongBioActivity.this,
                                                                        null);
                                                                finish();
                                                            }

                                                            @Override
                                                            public void onInitUpdate(int state) {
                                                                dissmisProgressDialog();
                                                                showButtonConfirmDialog();
                                                            }

                                                            @Override
                                                            public void onInitFail() {
                                                                isInitingPin=false;
                                                                if(powerPressDialog!=null&&powerPressDialog.isShowing()){
                                                                    powerPressDialog.cancel();
                                                                }
                                                                dissmisProgressDialog();
                                                                GemmaToastUtils.showLongToast(
                                                                        getString(R.string.wookong_init_pin_fail));
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

                    case R.id.btn_import_mne:
                        //验证电量
                        dialog.cancel();
                        showProgressDialog(getString(R.string.creating_wookong_wallet));
                        WookongUtils.validatePowerLevel(ParamConstants.POWER_LEVEL_ALERT_INIT,
                                new WookongUtils.ValidatePowerLevelCallback() {
                                    @Override
                                    public void onValidateSuccess() {
                                        //init PIN
                                        String password = String.valueOf(edtSetPass.getText());
                                        String passwordHint = String.valueOf(edtPassHint.getText());

                                        String currentDeviceName = DeviceOperationManager.getInstance().getCurrentDeviceName();
                                        isInitingPin=true;
                                        DeviceOperationManager.getInstance()
                                                .initPin(this.toString(), currentDeviceName, password, passwordHint,
                                                        new DeviceOperationManager.InitPinCallback() {
                                                            @Override
                                                            public void onInitSuccess() {
                                                                isInitingPin=false;
                                                                //跳转到创建账户名界面
                                                                dissmisProgressDialog();
                                                                dialog.cancel();
                                                                Intent intent = new Intent(BluetoothConfigWooKongBioActivity.this,
                                                                        BluetoothImportWalletActivity.class);
                                                                startActivity(intent);
                                                                finish();
                                                            }

                                                            @Override
                                                            public void onInitUpdate(int state) {
                                                                dissmisProgressDialog();
                                                                showButtonConfirmDialog();
                                                            }

                                                            @Override
                                                            public void onInitFail() {
                                                                isInitingPin=false;
                                                                if(powerPressDialog!=null&&powerPressDialog.isShowing()){
                                                                    powerPressDialog.dismiss();
                                                                }
                                                                dissmisProgressDialog();
                                                                GemmaToastUtils.showLongToast(
                                                                        getString(R.string.wookong_init_pin_fail));
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


    CustomFullDialog powerPressDialog;
    boolean isInitingPin;

    /**
     * 显示按键确认dialog
     */
    private synchronized void showButtonConfirmDialog() {
        if (powerPressDialog != null) {
            if(!powerPressDialog.isShowing()&&isInitingPin){
                powerPressDialog.show();
            }
            return;
        }
        int[] listenedItems = {R.id.imv_back};
        powerPressDialog = new CustomFullDialog(this,
                R.layout.baseservice_dialog_bluetooth_button_confirm, listenedItems, false, Gravity.BOTTOM);
        powerPressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if(isInitingPin){
                    isInitingPin=false;
                    DeviceOperationManager.getInstance().abortButton(BluetoothConfigWooKongBioActivity.this.toString(),deviceName);
                }
            }
        });
        powerPressDialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
            @Override
            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.imv_back:
                         powerPressDialog.cancel();
                        break;
                    default:
                        break;
                }
            }
        });
        powerPressDialog.show();
        TextView tvTip = powerPressDialog.getAllView().findViewById(R.id.tv_tip);
        tvTip.setText(getString(R.string.baseservice_hint_button_confirm_init_pin));
    }

    /**
     * 显示电量不足dialog
     */
    private void showPowerLevelAlertDialog() {
        if (powerAlertDialog != null) { return; }
        int[] listenedItems = {R.id.tv_i_understand};
        powerAlertDialog = new CustomFullDialog(this,
                R.layout.dialog_bluetooth_power_level_alert, listenedItems, false, Gravity.CENTER);
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


    @Override
    public boolean useEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveDeviceConnectEvent(DeviceConnectStatusUpdateEvent event){
        fixDeviceDisconnectEvent(event);
    }


}
