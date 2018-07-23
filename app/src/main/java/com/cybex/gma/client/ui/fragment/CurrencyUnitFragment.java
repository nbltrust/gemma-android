package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.view.View;

import com.cybex.gma.client.R;
import com.hxlx.core.lib.mvp.lite.XFragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 更改默认货币单位页面
 */

public class CurrencyUnitFragment extends XFragment {

    Unbinder unbinder;
    public static CurrencyUnitFragment newInstance() {
        Bundle args = new Bundle();
        CurrencyUnitFragment fragment = new CurrencyUnitFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setNavibarTitle("货币单位", true, false);
    }


    @Override
    public int getLayoutId() {
        return R.layout.fragment_currency_unit;
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
