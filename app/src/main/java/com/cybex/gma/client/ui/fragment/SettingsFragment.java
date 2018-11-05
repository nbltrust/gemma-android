package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.allen.library.SuperTextView;
import com.cybex.componentservice.api.ApiPath;
import com.cybex.componentservice.ui.activity.CommonWebViewActivity;
import com.cybex.gma.client.R;
import com.cybex.gma.client.manager.UISkipMananger;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.LanguageManager;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

import static com.cybex.gma.client.config.ParamConstants.CN;
import static com.cybex.gma.client.config.ParamConstants.EN;


/**
 * 设置页面
 */
public class SettingsFragment extends XFragment {

    Unbinder unbinder;
    @BindView(R.id.iv_close) ImageView ivClose;
    @BindView(R.id.superTextView_general) SuperTextView superTextViewGeneral;
    @BindView(R.id.superTextView_security) SuperTextView superTextViewSecurity;
    @BindView(R.id.superTextView_help) SuperTextView superTextViewHelp;
    @BindView(R.id.superTextView_service_agreement) SuperTextView superTextViewServiceAgreement;
    @BindView(R.id.superTextView_about) SuperTextView superTextViewAbout;
    @BindView(R.id.scroll_me) ScrollView scrollViewMe;


    public static SettingsFragment newInstance() {
        Bundle args = new Bundle();
        SettingsFragment fragment = new SettingsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
        OverScrollDecoratorHelper.setUpOverScroll(scrollViewMe);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null){
                    getActivity().finish();
                }
            }
        });

        //通用设置
        superTextViewGeneral.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                UISkipMananger.launchGeneralSetting(getActivity());
            }
        });
        //安全设置
        superTextViewSecurity.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                UISkipMananger.launchSecuritySetting(getActivity());
            }
        });
        //帮助与反馈
        superTextViewHelp.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                int savedLanguageType = LanguageManager.getInstance(getContext()).getLanguageType();
                switch (savedLanguageType) {
                    case LanguageManager.LanguageType.LANGUAGE_CHINESE_SIMPLIFIED:
                        CommonWebViewActivity.startWebView(getActivity(), ApiPath.HELP_CN, getResources().getString(R
                                .string.help));
                        break;
                    case LanguageManager.LanguageType.LANGUAGE_EN:
                        CommonWebViewActivity.startWebView(getActivity(), ApiPath.HELP_EN, getResources().getString(R
                                .string.help));
                        break;
                    case LanguageManager.LanguageType.LANGUAGE_FOLLOW_SYSTEM:
                        Locale systemLanguageType = LanguageManager.getInstance(getContext()).getSysLocale();
                        switch (systemLanguageType.getDisplayLanguage()) {
                            case CN:
                                CommonWebViewActivity.startWebView(getActivity(), ApiPath.HELP_CN,
                                        getResources().getString(R
                                                .string.help));
                                break;
                            case EN:
                                CommonWebViewActivity.startWebView(getActivity(), ApiPath.HELP_CN,
                                        getResources().getString(R
                                                .string.help));
                                break;
                        }
                        break;
                    default:
                        CommonWebViewActivity.startWebView(getActivity(), ApiPath.TERMS_OF_SERVICE_CN,
                                getResources().getString(R
                                        .string.terms_of_service));
                        break;
                }
            }
        });
        //服务协议
        superTextViewServiceAgreement.setOnSuperTextViewClickListener(
                new SuperTextView.OnSuperTextViewClickListener() {
                    @Override
                    public void onClickListener(SuperTextView superTextView) {
                        int savedLanguageType = LanguageManager.getInstance(getContext()).getLanguageType();
                        switch (savedLanguageType) {
                            case LanguageManager.LanguageType.LANGUAGE_CHINESE_SIMPLIFIED:
                                CommonWebViewActivity.startWebView(getActivity(), ApiPath.TERMS_OF_SERVICE_CN,
                                        getResources().getString(R
                                                .string.terms_of_service));
                                break;
                            case LanguageManager.LanguageType.LANGUAGE_EN:
                                CommonWebViewActivity.startWebView(getActivity(), ApiPath.TERMS_OF_SERVICE_EN,
                                        getResources().getString(R
                                                .string.terms_of_service));
                                break;
                            case LanguageManager.LanguageType.LANGUAGE_FOLLOW_SYSTEM:
                                Locale systemLanguageType = LanguageManager.getInstance(getContext()).getSysLocale();
                                switch (systemLanguageType.getDisplayLanguage()) {
                                    case CN:
                                        CommonWebViewActivity.startWebView(getActivity(), ApiPath.TERMS_OF_SERVICE_CN,
                                                getResources().getString(R
                                                        .string.terms_of_service));
                                        break;
                                    case EN:
                                        CommonWebViewActivity.startWebView(getActivity(), ApiPath.TERMS_OF_SERVICE_EN,
                                                getResources().getString(R
                                                        .string.terms_of_service));
                                        break;
                                }
                                break;
                            default:
                                CommonWebViewActivity.startWebView(getActivity(), ApiPath.TERMS_OF_SERVICE_CN,
                                        getResources().getString(R
                                                .string.terms_of_service));
                                break;
                        }
                    }
                });
        //关于Gemma
        superTextViewAbout.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                UISkipMananger.launchAbout(getActivity());
            }
        });

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_settings;
    }

    @Override
    public Object newP() {
        return null;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
