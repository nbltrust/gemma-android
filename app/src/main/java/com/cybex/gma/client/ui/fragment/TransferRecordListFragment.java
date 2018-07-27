package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.cybex.base.view.refresh.CommonRefreshLayout;
import com.cybex.base.view.statusview.MultipleStatusView;
import com.cybex.gma.client.R;
import com.cybex.gma.client.ui.adapter.TransferRecordListAdapter;
import com.cybex.gma.client.ui.model.response.TransferHistory;
import com.cybex.gma.client.ui.presenter.TransferRecordListPresenter;
import com.hxlx.core.lib.mvp.lite.XFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 转账记录列表主界面
 *
 * Created by wanglin on 2018/7/11.
 */
public class TransferRecordListFragment extends XFragment<TransferRecordListPresenter> {


    Unbinder unbinder;
    @BindView(R.id.list_multiple_status_view) MultipleStatusView listMultipleStatusView;
    @BindView(R.id.view_refresh) CommonRefreshLayout viewRefresh;
    @BindView(R.id.rv_list) RecyclerView mRechclerView;

    private TransferRecordListAdapter mAdapter;
    private List<TransferHistory> data;

    public static TransferRecordListFragment newInstance() {
        Bundle args = new Bundle();
        TransferRecordListFragment fragment = new TransferRecordListFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
        setNavibarTitle(getString(R.string.title_transfer_record), true, true);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        data = new ArrayList<>();

        TransferHistory h = new TransferHistory();
        h.from = "test1";
        h.to = "to1";
        h.status = 2;
        h.time = "2018-06-21T09:26:18.500";
        h.value = "1.9999 EOS";
        data.add(h);

        h = new TransferHistory();
        h.from = "test2";
        h.to = "to1";
        h.status = 3;
        h.time = "2018-06-21T09:26:18.500";
        h.value = "1.9999 EOS";
        data.add(h);


        for (int i = 0; i < 10; i++) {
            h = new TransferHistory();
            h.from = "data" + i;
            h.to = "to1";
            h.status = 2;
            h.time = "2018-06-21T09:26:18.500";
            h.value = "1.9999 EOS";
            data.add(h);
        }

        mAdapter = new TransferRecordListAdapter(data);
        mRechclerView.setAdapter(mAdapter);


    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_transfer_record_list;
    }

    @Override
    public TransferRecordListPresenter newP() {
        return new TransferRecordListPresenter();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


}
