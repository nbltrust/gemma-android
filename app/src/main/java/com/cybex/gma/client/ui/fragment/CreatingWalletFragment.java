package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cybex.gma.client.R;
import com.cybex.gma.client.db.entity.WalletEntity;
import com.cybex.gma.client.event.WalletIDEvent;
import com.cybex.gma.client.manager.DBManager;
import com.cybex.gma.client.manager.UISkipMananger;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.EmptyUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
/**
 *账户创建中页面
 * 创建钱包页面跳转到此页面
 */
public class CreatingWalletFragment extends XFragment {


    private WalletEntity curWallet;
    @BindView(R.id.bt_backup_later) Button btBackupLater;
    @BindView(R.id.bt_export_priKey) Button btExportPriKey;
    Unbinder unbinder;

    @OnClick(R.id.bt_backup_later)
    public void goToMainTab(){//前往MainTab如果选择稍后备份
        UISkipMananger.launchHome(getActivity(), new Bundle());
    }

    @OnClick(R.id.bt_export_priKey)
    public void goToBackUpPriKey(){
        //如果选择马上备份，前往备份引导页
        curWallet = DBManager.getInstance().getWalletEntityDao().getCurrentWalletEntity();
        if (EmptyUtils.isNotEmpty(curWallet)){
            EventBusProvider.postSticky(new WalletIDEvent(curWallet.getId()));
            UISkipMananger.launchBakupGuide(getActivity());
        }
    }

    @Override
    public boolean useEventBus() {
        return true;
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
        setNavibarTitle(getResources().getString(R.string.create_wallet), true, true);
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
