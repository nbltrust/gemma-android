package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import com.cybex.gma.client.R;
import com.cybex.gma.client.config.CacheConstants;
import com.cybex.gma.client.manager.UISkipMananger;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.SPUtils;
import com.hxlx.core.lib.utils.common.utils.AppManager;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 更改默认货币单位页面
 */

public class CurrencyUnitFragment extends XFragment {

    private int savedCurrencyType;
    Unbinder unbinder;
    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.radioButton_CNY) RadioButton radioButtonCNY;
    @BindView(R.id.radioButton_USD) RadioButton radioButtonUSD;

    @OnClick({R.id.radioButton_CNY, R.id.radioButton_USD})
    public void setCurrencyUnitListener(View view){
        switch (view.getId()){
            case R.id.radioButton_CNY:
                SPUtils.getInstance().put("currency_unit", CacheConstants.CURRENCY_CNY);
                AppManager.getAppManager().finishAllActivity();
                UISkipMananger.launchHomeSingle(getActivity());
                break;
            case R.id.radioButton_USD:
                SPUtils.getInstance().put("currency_unit", CacheConstants.CURRENCY_USD);
                AppManager.getAppManager().finishAllActivity();
                UISkipMananger.launchHomeSingle(getActivity());
                break;
        }
    }

    public static CurrencyUnitFragment newInstance() {
        Bundle args = new Bundle();
        CurrencyUnitFragment fragment = new CurrencyUnitFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public boolean useEventBus() {
        return true;
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setNavibarTitle(getResources().getString(R.string.title_currency_unit), true, false);
        savedCurrencyType = SPUtils.getInstance().getInt("currency_unit");
        showSavedCurrency();
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

    private void showSavedCurrency(){
        switch (savedCurrencyType){
            case CacheConstants.CURRENCY_CNY:
                showLogic(true, false);
                break;
            case CacheConstants.CURRENCY_USD:
                showLogic(false, true);
                break;
            default:
            break;
        }
    }
    private void showLogic(boolean cny, boolean usd){
        radioButtonCNY.setChecked(cny);
        radioButtonUSD.setChecked(usd);
    }
}
