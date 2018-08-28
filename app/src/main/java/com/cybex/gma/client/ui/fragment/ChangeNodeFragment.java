package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import com.cybex.gma.client.R;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ChangeNodeFragment extends XFragment {

    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.radioButton_node_cybex) RadioButton radioButtonNodeCybex;
    @BindView(R.id.radioButton_node_starteos) RadioButton radioButtonNodeStarteos;
    @BindView(R.id.radioButton_node_eosnewyork) RadioButton radioButtonNodeEosnewyork;
    @BindView(R.id.radioButton_node_greymass) RadioButton radioButtonNodeGreymass;
    @BindView(R.id.radioButton_node_eosasia) RadioButton radioButtonNodeEosasia;
    @BindView(R.id.radioButton_node_eosgravity) RadioButton radioButtonNodeEosgravity;
    @BindView(R.id.radioButton_node_helloeos) RadioButton radioButtonNodeHelloeos;
    @BindView(R.id.radioButton_node_hkeos) RadioButton radioButtonNodeHkeos;
    @BindView(R.id.radioButton_node_eos42) RadioButton radioButtonNodeEos42;
    @BindView(R.id.radioButton_node_cypherglass) RadioButton radioButtonNodeCypherglass;
    Unbinder unbinder;

    public static ChangeNodeFragment newInstance() {
        Bundle args = new Bundle();
        ChangeNodeFragment fragment = new ChangeNodeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setNavibarTitle(getResources().getString(R.string.node_select), true, false);
        mTitleBar.setActionTextColor(getResources().getColor(R.color.whiteTwo));
        mTitleBar.setActionTextSize(18);

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_change_node;
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
