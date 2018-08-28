package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import com.cybex.gma.client.R;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 更改默认货币单位页面
 */

public class CurrencyUnitFragment extends XFragment {

    Unbinder unbinder;
    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.radioButton_CNY) RadioButton radioButtonCNY;
    @BindView(R.id.radioButton_USD) RadioButton radioButtonUSD;

    @OnClick({R.id.radioButton_CNY, R.id.radioButton_USD})
    public void OnClick(){

    }

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
        setNavibarTitle(getResources().getString(R.string.title_currency_unit), true, false);

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
