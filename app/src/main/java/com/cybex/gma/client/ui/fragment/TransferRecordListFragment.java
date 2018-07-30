package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.cybex.base.view.refresh.CommonRefreshLayout;
import com.cybex.base.view.statusview.MultipleStatusView;
import com.cybex.gma.client.R;
import com.cybex.gma.client.db.entity.WalletEntity;
import com.cybex.gma.client.manager.DBManager;
import com.cybex.gma.client.ui.adapter.TransferRecordListAdapter;
import com.cybex.gma.client.ui.model.response.TransferHistory;
import com.cybex.gma.client.ui.presenter.TransferRecordListPresenter;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

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
    @BindView(R.id.rv_list) RecyclerView mRecyclerView;

    private TransferRecordListAdapter mAdapter;
    private List<TransferHistory> data = new ArrayList<>();
    private String currentEosName = "cooljadepool";


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
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(manager);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        viewRefresh.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                if (mAdapter != null) {
                    List<TransferHistory> historyList = mAdapter.getData();
                    if (!EmptyUtils.isEmpty(historyList)) {
                        TransferHistory history = historyList.get(historyList.size() - 1);
                        int lastPos = history.last_pos;
                        doRequest(lastPos);
                    }
                }

            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                doRequest(-1);
                viewRefresh.finishRefresh();
            }
        });


        //第一次请求数据
        doRequest(-1);
    }


    public void doRequest(int currentLastPos) {
        getP().requestHistory(currentEosName, currentLastPos);

    }

    public void loadMoreData(List<TransferHistory> dataList) {
        if (EmptyUtils.isEmpty(dataList)) {
            dataList = new ArrayList<>();
        }

        WalletEntity entity = DBManager.getInstance()
                .getWalletEntityDao()
                .getCurrentWalletEntity();
        if (entity != null) {
            currentEosName = entity.getCurrentEosName();
        }

        if (mAdapter == null) {
            //第一次请求
            mAdapter = new TransferRecordListAdapter(dataList, currentEosName);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            //加载更多
            mAdapter.addData(dataList);
        }

    }

    public void refreshData(List<TransferHistory> dataList) {
        if (EmptyUtils.isEmpty(dataList)) {
            dataList = new ArrayList<>();
        }

        mAdapter.getData().clear();
        mAdapter.setNewData(dataList);
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

    public void showLoading() {
        listMultipleStatusView.showLoading();
    }

    public void showError() {
        listMultipleStatusView.showError();
    }

    public void showContent() {
        listMultipleStatusView.showContent();
    }

    /**
     * 显示加载更多完成和空数据界面逻辑
     */
    public void showEmptyOrFinish() {
        if (mAdapter != null) {
            List<TransferHistory> historyList = mAdapter.getData();
            if (EmptyUtils.isEmpty(historyList)) {
                listMultipleStatusView.showEmpty();
            } else {
                listMultipleStatusView.showContent();
                viewRefresh.finishLoadmore();
            }

        } else {
            listMultipleStatusView.showEmpty();
        }

    }

}
