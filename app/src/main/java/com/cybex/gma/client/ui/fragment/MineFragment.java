package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.view.View;

import com.allen.library.SuperTextView;
import com.cybex.gma.client.R;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.presenter.MinePresenter;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

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
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        superTextViewGeneral.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                UISkipMananger.launchGeneralSetting(getActivity());
            }
        });

        superTextViewSecurity.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                UISkipMananger.launchSecuritySetting(getActivity());
            }
        });

        superTextViewHelp.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {

            }
        });

        superTextViewServiceAgreement.setOnSuperTextViewClickListener(
                new SuperTextView.OnSuperTextViewClickListener() {
                    @Override
                    public void onClickListener(SuperTextView superTextView) {

                    }
                });

        superTextViewAbout.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {

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
