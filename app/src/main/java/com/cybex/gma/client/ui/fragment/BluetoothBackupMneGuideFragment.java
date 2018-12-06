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

public class BluetoothBackupMneGuideFragment extends XFragment {

    Unbinder unbinder;
    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.iv_dot_one) ImageView ivDotOne;
    @BindView(R.id.tv_look_around_hint) TextView tvLookAroundHint;
    @BindView(R.id.iv_dot_two) ImageView ivDotTwo;
    @BindView(R.id.tv_look_around_hint_two) TextView tvLookAroundHintTwo;
    @BindView(R.id.bt_show_mne) Button btShowMne;
    @BindView(R.id.testTV) TextView testTV;

    private Bundle bd = null;


    @OnClick(R.id.bt_show_mne)
    public void showMne() {
        replaceFragment(BluetoothBackupMneFragment.newInstance(bd),false);
    }

    public static BluetoothBackupMneGuideFragment newInstance(Bundle bd) {
        BluetoothBackupMneGuideFragment fragment = new BluetoothBackupMneGuideFragment();
        fragment.setArguments(bd);
        return fragment;
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(BluetoothBackupMneGuideFragment.this, rootView);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setNavibarTitle(getResources().getString(R.string.eos_backup_mne), true,
                true);

        bd = getArguments();
    }


    @Override
    public int getLayoutId() {
        return R.layout.eos_fragment_backup_mne_guide;
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
