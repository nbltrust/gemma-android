package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cybex.gma.client.R;
import com.cybex.gma.client.manager.UISkipMananger;
import com.hxlx.core.lib.mvp.lite.XFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
/**
 *账户创建中页面
 * 创建钱包页面跳转到此页面
 */
public class CreatingWalletFragment extends XFragment {



    @BindView(R.id.bt_backup_later) Button btBackupLater;
    @BindView(R.id.bt_export_priKey) Button btExportPriKey;
    Unbinder unbinder;

    @OnClick(R.id.bt_backup_later)
    public void goToMainTab(){//前往MainTab如果选择稍后备份
        UISkipMananger.launchHome(getActivity());
    }

    @OnClick(R.id.bt_export_priKey)
    public void goToBackUpPriKey(){
        //如果选择马上备份，前往备份引导页
        UISkipMananger.launchBakupGuide(getActivity());
    }

    public static CreatingWalletFragment newInstance() {
        Bundle args = new Bundle();
        CreatingWalletFragment fragment = new CreatingWalletFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);

    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setNavibarTitle("创建钱包", true, true);
    }


    @Override
    public int getLayoutId() {
        return R.layout.fragment_creating_wallet;
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
