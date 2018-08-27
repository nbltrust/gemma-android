package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;

import com.allen.library.SuperTextView;
import com.cybex.gma.client.R;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.base.CommonWebViewActivity;
import com.cybex.gma.client.ui.presenter.MinePresenter;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.LanguageManager;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

/**
 * 我的
 *
 * Created by wanglin on 2018/7/9.
 */
public class MineFragment extends XFragment<MinePresenter> {


    Unbinder unbinder;
    @BindView(R.id.superTextView_general) SuperTextView superTextViewGeneral;
    @BindView(R.id.superTextView_security) SuperTextView superTextViewSecurity;
    @BindView(R.id.superTextView_help) SuperTextView superTextViewHelp;
    @BindView(R.id.superTextView_service_agreement) SuperTextView superTextViewServiceAgreement;
    @BindView(R.id.superTextView_about) SuperTextView superTextViewAbout;
    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.scroll_me) ScrollView scrollViewMe;

    public static MineFragment newInstance() {
        Bundle args = new Bundle();
        MineFragment fragment = new MineFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
        setNavibarTitle(getString(R.string.title_me), false);
        OverScrollDecoratorHelper.setUpOverScroll(scrollViewMe);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
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

            }
        });
        //服务协议
        superTextViewServiceAgreement.setOnSuperTextViewClickListener(
                new SuperTextView.OnSuperTextViewClickListener() {
                    @Override
                    public void onClickListener(SuperTextView superTextView) {
                        int savedLanguageType = LanguageManager.getInstance(getContext()).getLanguageType();
                        switch (savedLanguageType){
                            case LanguageManager.LanguageType.LANGUAGE_CHINESE_SIMPLIFIED:
                                final String url = "https://nebuladownload.oss-cn-beijing.aliyuncs.com/gemma/gemma_policy_cn"
                                        + ".html";
                                CommonWebViewActivity.startWebView(getActivity(), url, getResources().getString(R
                                        .string.service_agreement));
                                break;
                            case LanguageManager.LanguageType.LANGUAGE_EN:
                                final String url_en = "https://nebuladownload.oss-cn-beijing.aliyuncs"
                                        + ".com/gemma/gemma_policy_en"
                                        + ".html";
                                CommonWebViewActivity.startWebView(getActivity(), url_en, getResources().getString(R
                                        .string.service_agreement));
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
        return R.layout.fragment_me;
    }

    @Override
    public MinePresenter newP() {
        return new MinePresenter();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
