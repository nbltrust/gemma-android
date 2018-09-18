package com.cybex.gma.client.ui.activity;

import android.os.Bundle;
import android.text.Editable;
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
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.base.CommonWebViewActivity;
import com.cybex.gma.client.ui.model.vo.BluetoothAccountInfoVO;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.LanguageManager;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Checked;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.xujiaji.happybubble.BubbleLayout;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

import static com.cybex.gma.client.config.ParamConstants.CN;
import static com.cybex.gma.client.config.ParamConstants.EN;

/**
 * 创建EOS账户页面
 */
public class BluetoothCreatEosAccountActivity extends XActivity implements
        Validator.ValidationListener {


    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.tv_in_bubble) TextView tvInBubble;
    @BindView(R.id.bubble) BubbleLayout bubble;
    @BindView(R.id.tv_eos_name) TextView tvEosName;
    @NotEmpty(messageResId = R.string.eos_name_not_empty, sequence = 1)
    @BindView(R.id.edt_eos_name) EditText edtEosName;
    @BindView(R.id.iv_eos_name_clear) ImageView ivEosNameClear;

    @Checked(messageResId = R.string.check_agreement, sequence = 0)
    @BindView(R.id.checkbox_config) CheckBox checkboxConfig;
    @BindView(R.id.tv_service_agreement_create_eos_account) TextView tvServiceAgreement;
    @BindView(R.id.layout_checkBox) LinearLayout layoutCheckBox;
    @BindView(R.id.bt_create_wallet) Button btCreateWallet;
    @BindView(R.id.scroll_create_wallet) ScrollView scrollCreateWallet;

    private Validator validator;
    private Bundle bd;


    @OnTextChanged(value = R.id.edt_eos_name, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onChanged(Editable s) {
        if (EmptyUtils.isNotEmpty(s.toString()) && isUserNameValid() && checkboxConfig.isChecked()) {
            setClickableStyle(btCreateWallet);
        } else {
            setUnClickableStyle(btCreateWallet);
        }
    }


    @OnClick(R.id.tv_service_agreement_create_eos_account)
    public void goToSeeTermsofService() {
        int savedLanguageType = LanguageManager.getInstance(this).getLanguageType();
        switch (savedLanguageType) {
            case LanguageManager.LanguageType.LANGUAGE_CHINESE_SIMPLIFIED:
                CommonWebViewActivity.startWebView(this, ApiPath.TERMS_OF_SERVICE_CN, getResources().getString(R
                        .string.service_agreement));
                break;
            case LanguageManager.LanguageType.LANGUAGE_EN:
                CommonWebViewActivity.startWebView(this, ApiPath.TERMS_OF_SERVICE_EN, getResources().getString(R
                        .string.service_agreement));
                break;
            case LanguageManager.LanguageType.LANGUAGE_FOLLOW_SYSTEM:
                Locale systemLanguageType = LanguageManager.getInstance(this).getSysLocale();
                switch (systemLanguageType.getDisplayLanguage()) {
                    case CN:
                        CommonWebViewActivity.startWebView(this, ApiPath.TERMS_OF_SERVICE_CN, getResources()
                                .getString(R.string.service_agreement));
                        break;
                    case EN:
                        CommonWebViewActivity.startWebView(this, ApiPath.TERMS_OF_SERVICE_EN, getResources()
                                .getString(R.string.service_agreement));
                        break;
                    default:
                        CommonWebViewActivity.startWebView(this, ApiPath.TERMS_OF_SERVICE_CN, getResources()
                                .getString(R.string.service_agreement));
                }
                break;
            default:
                CommonWebViewActivity.startWebView(this, ApiPath.TERMS_OF_SERVICE_CN, getResources().getString(R
                        .string.service_agreement));

        }
    }

    @OnClick(R.id.bt_create_wallet)
    public void createWallet() {
        validator.validate();
    }

    @Override
    public void bindUI(View rootView) {
        ButterKnife.bind(this);
        setNavibarTitle(getString(R.string.title_create_eos_account), true);
        validator = new Validator(this);
        validator.setValidationListener(this);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        bd = getIntent().getExtras();
        btCreateWallet.setClickable(true);
        setUnClickableStyle(btCreateWallet);

        checkboxConfig.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (EmptyUtils.isNotEmpty(getEOSUserName()) && isChecked && isUserNameValid()) {
                    setClickableStyle(btCreateWallet);
                } else {
                    setUnClickableStyle(btCreateWallet);
                }

                if (!isChecked) {
                    setUnClickableStyle(btCreateWallet);

                }
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_bluetooth_create_eos_account;
    }

    @Override
    public Object newP() {
        return null;
    }

    public boolean isUserNameValid() {
        String eosUsername = getEOSUserName();
        String regEx = "^[a-z1-5]{12}$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher((eosUsername));
        boolean res = matcher.matches();
        return res;
    }

    public String getEOSUserName() {
        return edtEosName.getText().toString().trim();
    }

    public void setClickableStyle(Button button) {
        //button.setClickable(true);
        button.setBackground(getDrawable(R.drawable.shape_corner_button));
    }

    public void setUnClickableStyle(Button button) {
        //button.setClickable(false);
        button.setBackground(getDrawable(R.drawable.shape_corner_button_unclickable));
    }


    @Override
    public void onValidationSucceeded() {
        if (isUserNameValid()) {
            //所有验证通过，在这里跳转和调用网络请求
            setClickableStyle(btCreateWallet);
            String eosName = String.valueOf(edtEosName.getText());
            BluetoothAccountInfoVO vo = bd.getParcelable(ParamConstants.KEY_BLUETOOTH_ACCOUNT_INFO);
            vo.setAccountName(eosName);

            bd.putParcelable(ParamConstants.KEY_BLUETOOTH_ACCOUNT_INFO, vo);

            UISkipMananger.skipBackupMneGuideActivity(this, bd);
            finish();
        } else {
            GemmaToastUtils.showLongToast(getString(R.string.eos_name_invalid));
        }

    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            String message = error.getCollatedErrorMessage(this);
            GemmaToastUtils.showLongToast(message);
        }
    }
}
