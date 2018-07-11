package com.cybex.gma.client.ui.main.fragment;

import android.os.Bundle;
import android.view.View;

import com.cybex.gma.client.R;
import com.cybex.gma.client.ui.main.presenter.MinePresenter;
import com.hxlx.core.lib.mvp.lite.XFragment;

/**
 * 我的
 *
 * Created by wanglin on 2018/7/9.
 */
public class MineFragment extends XFragment<MinePresenter> {


    public static MineFragment newInstance() {
        Bundle args = new Bundle();
        MineFragment fragment = new MineFragment();
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
        return R.layout.fragment_me;
    }

    @Override
    public MinePresenter newP() {
        return new MinePresenter();
    }
}
