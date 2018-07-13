package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.view.View;

import com.cybex.gma.client.R;
import com.cybex.gma.client.ui.presenter.TransferRecordListPresenter;
import com.hxlx.core.lib.mvp.lite.XFragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 转账记录列表主界面
 *
 * Created by wanglin on 2018/7/11.
 */
public class TransferRecordListFragment extends XFragment<TransferRecordListPresenter> {


    Unbinder unbinder;

    public static TransferRecordListFragment newInstance() {
        Bundle args = new Bundle();

        TransferRecordListFragment fragment = new TransferRecordListFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
        setNavibarTitle(getString(R.string.title_transfer_record),true,true);
    }

    @Override
    public void initData(Bundle savedInstanceState) {

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_transfer_record_list;
    }

    @Override
    public TransferRecordListPresenter newP() {
        return null;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
