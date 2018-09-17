package com.cybex.gma.client.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cybex.gma.client.R;
import com.cybex.gma.client.api.ApiPath;
import com.cybex.gma.client.ui.base.CommonWebViewActivity;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.hxlx.core.lib.utils.LanguageManager;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.xujiaji.happybubble.BubbleLayout;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.cybex.gma.client.config.ParamConstants.CN;
import static com.cybex.gma.client.config.ParamConstants.EN;

/**
 * 创建EOS账户页面
 */
public class BluetoothCreatEosAccountActivity extends XActivity {


    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.tv_in_bubble) TextView tvInBubble;
    @BindView(R.id.bubble) BubbleLayout bubble;
    @BindView(R.id.tv_eos_name) TextView tvEosName;
    @BindView(R.id.edt_eos_name) EditText edtEosName;
    @BindView(R.id.iv_eos_name_clear) ImageView ivEosNameClear;
    @BindView(R.id.checkbox_config) CheckBox checkboxConfig;
    @BindView(R.id.tv_service_agreement_create_eos_account) TextView tvServiceAgreement;
    @BindView(R.id.layout_checkBox) LinearLayout layoutCheckBox;
    @BindView(R.id.bt_create_wallet) Button btCreateWallet;
    @BindView(R.id.scroll_create_wallet) ScrollView scrollCreateWallet;


    @OnClick(R.id.tv_service_agreement_create_eos_account)
    public void goToSeeTermsofService(){
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
    @Override
    public void bindUI(View rootView) {
        ButterKnife.bind(this);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
       setNavibarTitle(getString(R.string.title_create_eos_account),true);

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

    public String getEOSUserName(){
        return edtEosName.getText().toString().trim();
    }
}
