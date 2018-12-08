package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import com.cybex.componentservice.api.ApiPath;
import com.cybex.gma.client.R;
import com.cybex.gma.client.manager.UISkipMananger;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.SPUtils;
import com.hxlx.core.lib.utils.common.utils.AppManager;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ChangeNodeFragment extends XFragment {

    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.radioButton_node_cybex) RadioButton radioButtonNodeCybex;
    @BindView(R.id.radioButton_node_starteos) RadioButton radioButtonNodeStarteos;
    @BindView(R.id.radioButton_node_greymass) RadioButton radioButtonNodeGreymass;
    @BindView(R.id.radioButton_node_eosgravity) RadioButton radioButtonNodeEosgravity;
    @BindView(R.id.radioButton_node_helloeos) RadioButton radioButtonNodeHelloeos;
    @BindView(R.id.radioButton_node_hkeos) RadioButton radioButtonNodeHkeos;

    Unbinder unbinder;
    @BindView(R.id.radioButton_node_beijing_test) RadioButton radioButtonNodeBeijingTest;

    @OnClick({R.id.radioButton_node_cybex, R.id.radioButton_node_eosgravity,
            R.id.radioButton_node_greymass, R.id.radioButton_node_helloeos,
            R.id.radioButton_node_hkeos, R.id.radioButton_node_starteos,
            R.id.radioButton_node_beijing_test})
    public void onChangeNodeListener(View v) {
        switch (v.getId()) {
            case R.id.radioButton_node_cybex:
                ApiPath.setHOST_ON_CHAIN(ApiPath.EOS_CYBEX);
                ApiPath.setHostCenterServer(ApiPath.EOS_SERVER_PORT_STAGING);
                SPUtils.getInstance().put("curNode", ApiPath.EOS_CYBEX);
                AppManager.getAppManager().finishAllActivity();
                UISkipMananger.launchHomeSingle(getActivity());
                break;
            case R.id.radioButton_node_eosgravity:
                ApiPath.setHOST_ON_CHAIN(ApiPath.EOS_GRAVITY);
                SPUtils.getInstance().put("curNode", ApiPath.EOS_GRAVITY);
                AppManager.getAppManager().finishAllActivity();
                UISkipMananger.launchHomeSingle(getActivity());
                break;
            case R.id.radioButton_node_greymass:
                ApiPath.setHOST_ON_CHAIN(ApiPath.EOS_GREY_MASS);
                SPUtils.getInstance().put("curNode", ApiPath.EOS_GREY_MASS);
                AppManager.getAppManager().finishAllActivity();
                UISkipMananger.launchHomeSingle(getActivity());
                break;
            case R.id.radioButton_node_helloeos:
                ApiPath.setHOST_ON_CHAIN(ApiPath.EOS_HELLOEOS);
                SPUtils.getInstance().put("curNode", ApiPath.EOS_HELLOEOS);
                AppManager.getAppManager().finishAllActivity();
                UISkipMananger.launchHomeSingle(getActivity());
                break;
            case R.id.radioButton_node_hkeos:
                ApiPath.setHOST_ON_CHAIN(ApiPath.EOS_HK_EOS);
                SPUtils.getInstance().put("curNode", ApiPath.EOS_HK_EOS);
                AppManager.getAppManager().finishAllActivity();
                UISkipMananger.launchHomeSingle(getActivity());
                break;
            case R.id.radioButton_node_starteos:
                ApiPath.setHOST_ON_CHAIN(ApiPath.EOS_START_EOS);
                SPUtils.getInstance().put("curNode", ApiPath.EOS_START_EOS);
                AppManager.getAppManager().finishAllActivity();
                UISkipMananger.launchHomeSingle(getActivity());
                break;
            case R.id.radioButton_node_beijing_test:
                ApiPath.setHOST_ON_CHAIN(ApiPath.EOS_TEST_BEIJING);
                SPUtils.getInstance().put("curNode", ApiPath.EOS_TEST_BEIJING);
                AppManager.getAppManager().finishAllActivity();
                UISkipMananger.launchHomeSingle(getActivity());
                break;
            default:
                ApiPath.setHOST_ON_CHAIN(ApiPath.EOS_CYBEX);
                ApiPath.setHostCenterServer(ApiPath.EOS_SERVER_PORT_STAGING);
                SPUtils.getInstance().put("curNode", ApiPath.EOS_CYBEX);
                AppManager.getAppManager().finishAllActivity();
                UISkipMananger.launchHomeSingle(getActivity());
                break;
        }
    }

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
        setNavibarTitle(getResources().getString(R.string.eos_node_select), true, false);
        mTitleBar.setActionTextColor(getResources().getColor(R.color.whiteTwo));
        mTitleBar.setActionTextSize(18);

        showLogic();

    }

    @Override
    public int getLayoutId() {
        return R.layout.eos_fragment_change_node;
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

    public void showLogic() {
        String selectedNode = SPUtils.getInstance().getString("curNode");
        switch (selectedNode) {
            case ApiPath.EOS_CYBEX:
                radioButtonNodeCybex.setChecked(true);
                break;
            case ApiPath.EOS_GRAVITY:
                radioButtonNodeEosgravity.setChecked(true);
                break;
            case ApiPath.EOS_GREY_MASS:
                radioButtonNodeGreymass.setChecked(true);
                break;
            case ApiPath.EOS_HELLOEOS:
                radioButtonNodeHelloeos.setChecked(true);
                break;
            case ApiPath.EOS_HK_EOS:
                radioButtonNodeHkeos.setChecked(true);
                break;
            case ApiPath.EOS_START_EOS:
                radioButtonNodeStarteos.setChecked(true);
                break;
            case ApiPath.EOS_TEST_BEIJING:
                radioButtonNodeBeijingTest.setChecked(true);
                break;
            default:
                radioButtonNodeCybex.setChecked(true);
        }
    }
}
