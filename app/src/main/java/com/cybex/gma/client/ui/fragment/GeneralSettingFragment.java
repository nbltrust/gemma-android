package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.view.View;

import com.allen.library.SuperTextView;
import com.cybex.gma.client.R;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class GeneralSettingFragment extends XFragment {

    Unbinder unbinder;
    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.superTextView_change_language) SuperTextView superTextViewChangeLanguage;
    @BindView(R.id.superTextView_change_unit) SuperTextView superTextViewChangeUnit;

    public static GeneralSettingFragment newInstance() {
        Bundle args = new Bundle();
        GeneralSettingFragment fragment = new GeneralSettingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);

    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setNavibarTitle("通用", true, true);

        superTextViewChangeLanguage.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                start(LanguageSettingFragment.newInstance());
            }
        });

        superTextViewChangeUnit.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                start(CurrencyUnitFragment.newInstance());
            }
        });
    }


    @Override
    public int getLayoutId() {
        return R.layout.fragment_general_setting;
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
