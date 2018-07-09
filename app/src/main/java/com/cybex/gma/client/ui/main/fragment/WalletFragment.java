package com.cybex.gma.client.ui.main.fragment;

import android.os.Bundle;
import android.view.View;

import com.cybex.gma.client.R;
import com.cybex.gma.client.ui.main.presenter.WalletPresenter;
import com.hxlx.core.lib.mvp.lite.XFragment;

/**
 *
 * 钱包Fragment
 *
 * Created by wanglin on 2018/7/9.
 */
public class WalletFragment extends XFragment<WalletPresenter>{

    public static WalletFragment newInstance() {
        Bundle args = new Bundle();
        WalletFragment fragment = new WalletFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void bindUI(View rootView) {

    }

    @Override
    public void initData(Bundle savedInstanceState) {

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_wallet;
    }

    @Override
    public WalletPresenter newP() {
        return new WalletPresenter();
    }
}
