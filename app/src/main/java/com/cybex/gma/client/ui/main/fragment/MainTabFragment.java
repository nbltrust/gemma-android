package com.cybex.gma.client.ui.main.fragment;

import android.os.Bundle;
import android.view.View;

import com.cybex.gma.client.ui.main.presenter.MainPresenter;
import com.hxlx.core.lib.mvp.lite.XFragment;

/**
 * Created by wanglin on 2018/7/5.
 */
public class MainTabFragment extends XFragment<MainPresenter>{


    @Override
    public void bindUI(View rootView) {

    }

    @Override
    public void initData(Bundle savedInstanceState) {

    }

    @Override
    public int getLayoutId() {
        return 0;
    }

    @Override
    public MainPresenter newP() {
        return null;
    }
}
