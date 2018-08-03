package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.view.View;

import com.cybex.gma.client.R;
import com.hxlx.core.lib.mvp.lite.XFragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AboutFragment extends XFragment {


    public static AboutFragment newInstance() {
        Bundle args = new Bundle();
        AboutFragment fragment = new AboutFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(AboutFragment.this, rootView);
        setNavibarTitle("关于", true,true);
    }
    
    Unbinder unbinder;
    @Override
    public void initData(Bundle savedInstanceState) {
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
