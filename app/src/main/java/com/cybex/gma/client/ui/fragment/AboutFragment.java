package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.view.View;

import com.allen.library.SuperTextView;
import com.cybex.gma.client.R;
import com.cybex.gma.client.ui.base.CommonWebViewActivity;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.LanguageManager;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AboutFragment extends XFragment {


    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.superTextView_version_info) SuperTextView superTextViewVersionInfo;
    @BindView(R.id.superTextView_update) SuperTextView superTextViewUpdate;
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
                        String url = "https://nebuladownload.oss-cn-beijing.aliyuncs.com/gemma/gemma_release_desc_cn.html";
                        CommonWebViewActivity.startWebView(getActivity(), url, getResources().getString(R.string.version_info));
                        break;
                    case LanguageManager.LanguageType.LANGUAGE_EN:
                        String url_en = "https://nebuladownload.oss-cn-beijing.aliyuncs"
                                + ".com/gemma/gemma_release_desc_en.html";
                        CommonWebViewActivity.startWebView(getActivity(), url_en, getResources().getString(R.string
                                .version_info));
                        break;
                }
            }
        });
        //版本更新
        superTextViewUpdate.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {

            }
        });

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_about;
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
