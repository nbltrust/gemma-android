package com.cybex.gma.client.ui.fragment;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.allen.library.SuperTextView;
import com.cybex.gma.client.R;
import com.cybex.componentservice.api.ApiPath;
import com.cybex.componentservice.ui.activity.CommonWebViewActivity;
import com.cybex.gma.client.ui.activity.AboutActivity;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.LanguageManager;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.cybex.gma.client.config.ParamConstants.*;

public class AboutFragment extends XFragment {


    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.superTextView_version_info) SuperTextView superTextViewVersionInfo;
    @BindView(R.id.superTextView_update) SuperTextView superTextViewUpdate;
    @BindView(R.id.tv_package_name_and_version) TextView tvPackageNameAndVersion;
    Unbinder unbinder;
    public static AboutFragment newInstance() {
        Bundle args = new Bundle();
        AboutFragment fragment = new AboutFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(AboutFragment.this, rootView);
        setNavibarTitle(getResources().getString(R.string.about), true, true);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        //版本说明
        superTextViewVersionInfo.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                int savedLanguageType = LanguageManager.getInstance(getContext()).getLanguageType();
                switch (savedLanguageType){
                    case LanguageManager.LanguageType.LANGUAGE_CHINESE_SIMPLIFIED:
                        CommonWebViewActivity.startWebView(getActivity(), ApiPath.VERSION_NOTE_CN, getResources().getString(R.string.version_info));
                        break;
                    case LanguageManager.LanguageType.LANGUAGE_EN:
                        CommonWebViewActivity.startWebView(getActivity(), ApiPath.VERSION_NOTE_EN, getResources().getString(R.string
                                .version_info));
                        break;
                    case  LanguageManager.LanguageType.LANGUAGE_FOLLOW_SYSTEM:
                        Locale systemLanguageType = LanguageManager.getInstance(getContext()).getSysLocale();
                        switch (systemLanguageType.getDisplayLanguage()){
                            case CN:
                                CommonWebViewActivity.startWebView(getActivity(), ApiPath.VERSION_NOTE_CN, getResources()
                                        .getString(R
                                        .string.version_info));
                                break;
                            case EN:
                                CommonWebViewActivity.startWebView(getActivity(), ApiPath.VERSION_NOTE_EN, getResources().getString(R
                                        .string.version_info));
                                break;
                        }
                        break;
                    default:
                        CommonWebViewActivity.startWebView(getActivity(), ApiPath.VERSION_NOTE_CN, getResources().getString(R
                                .string.version_info));
                        break;
                }
            }
        });
        //版本更新
        superTextViewUpdate.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {

                int savedLanguageType = LanguageManager.getInstance(getContext()).getLanguageType();
                switch (savedLanguageType){
                    case LanguageManager.LanguageType.LANGUAGE_CHINESE_SIMPLIFIED:
                        CommonWebViewActivity.startWebView(getActivity(), ApiPath.URL_UPDATE_CN, getResources().getString(R.string.version_update));
                        break;
                    case LanguageManager.LanguageType.LANGUAGE_EN:
                        CommonWebViewActivity.startWebView(getActivity(), ApiPath.URL_UPDATE_EN, getResources().getString(R.string
                                .version_update));
                        break;
                    case  LanguageManager.LanguageType.LANGUAGE_FOLLOW_SYSTEM:
                        Locale systemLanguageType = LanguageManager.getInstance(getContext()).getSysLocale();
                        switch (systemLanguageType.getDisplayLanguage()){
                            case CN:
                                CommonWebViewActivity.startWebView(getActivity(), ApiPath.URL_UPDATE_CN, getResources()
                                        .getString(R
                                                .string.version_update));
                                break;
                            case EN:
                                CommonWebViewActivity.startWebView(getActivity(), ApiPath.URL_UPDATE_EN, getResources().getString(R
                                        .string.version_update));
                                break;
                        }
                        break;
                    default:
                        CommonWebViewActivity.startWebView(getActivity(), ApiPath.VERSION_NOTE_CN, getResources().getString(R
                                .string.version_info));
                        break;
                }
            }
        });


        if (getActivity() != null) {
            PackageManager manager = getActivity().getPackageManager();
            try {
               PackageInfo pi = manager.getPackageInfo(getActivity().getPackageName(), 0);
               String version = pi.versionName;
               tvPackageNameAndVersion.setText(String.format(getString(R.string.package_version_info), version));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.eos_fragment_about;
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
        unbinder.unbind();
    }
}
