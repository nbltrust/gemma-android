package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cybex.gma.client.R;
import com.hxlx.core.lib.mvp.lite.XFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class BackUpPriKeyGuideFragment extends XFragment {

    @BindView(R.id.show_priKey) Button btShowPrikey;
    Unbinder unbinder;

    @OnClick(R.id.show_priKey)
    public void showPriKey(){

    }

    public static BackUpPriKeyGuideFragment newInstance() {
        Bundle args = new Bundle();
        BackUpPriKeyGuideFragment fragment = new BackUpPriKeyGuideFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(BackUpPriKeyGuideFragment.this, rootView);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setNavibarTitle("备份私钥", true, true);
    }


    @Override
    public int getLayoutId() {
        return R.layout.fragment_backup_guide;
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
