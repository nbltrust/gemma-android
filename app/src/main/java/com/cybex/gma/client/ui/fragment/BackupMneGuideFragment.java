package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cybex.gma.client.R;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class BackupMneGuideFragment extends XFragment {

    Unbinder unbinder;
    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.iv_dot_one) ImageView ivDotOne;
    @BindView(R.id.tv_look_around_hint) TextView tvLookAroundHint;
    @BindView(R.id.iv_dot_two) ImageView ivDotTwo;
    @BindView(R.id.tv_look_around_hint_two) TextView tvLookAroundHintTwo;
    @BindView(R.id.bt_show_mne) Button btShowMne;
    @BindView(R.id.testTV) TextView testTV;


    @OnClick(R.id.bt_show_mne)
    public void showMne(){
        start(BackupMneFragment.newInstance());
    }

    public static BackupMneGuideFragment newInstance() {
        Bundle args = new Bundle();
        BackupMneGuideFragment fragment = new BackupMneGuideFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(BackupMneGuideFragment.this, rootView);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setNavibarTitle(getResources().getString(R.string.backup_mne), true,
                true);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_backup_mne_guide;
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
