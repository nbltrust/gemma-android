package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.cybex.base.view.refresh.CommonRefreshLayout;
import com.cybex.base.view.statusview.MultipleStatusView;
import com.cybex.componentservice.db.entity.EosWalletEntity;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.db.entity.WalletEntity;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.event.TabSelectedEvent;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.gma.client.ui.adapter.TransferRecordListAdapter;
import com.cybex.gma.client.ui.model.response.TransferHistory;
import com.cybex.gma.client.ui.presenter.TransferRecordListPresenter;
import com.cybex.gma.client.ui.request.TransferHistoryListRequest;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.lzy.okgo.OkGo;
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
    private static final int TAB_WALLET = 0;//钱包

    @BindView(R.id.list_multiple_status_view) MultipleStatusView listMultipleStatusView;
    @BindView(R.id.view_refresh) CommonRefreshLayout viewRefresh;
    @BindView(R.id.rv_list) RecyclerView mRecyclerView;
    @BindView(R.id.btn_navibar) TitleBar naviBar;

    private TransferRecordListAdapter mAdapter;
    private String currentEosName = "";
    private int currentLastPos = -1;
    private boolean isFirstLoad = true;

    public static TransferRecordListFragment newInstance() {
        Bundle args = new Bundle();
        TransferRecordListFragment fragment = new TransferRecordListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public boolean useEventBus() {
        return true;
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
        setNavibarTitle(getString(R.string.eos_title_transfer_record), true, true);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(manager);

        MultiWalletEntity entity = DBManager.getInstance()
                .getMultiWalletEntityDao()
                .getCurrentMultiWalletEntity();
        EosWalletEntity eosEntity = entity.getEosWalletEntities().get(0);
        if (entity != null && eosEntity != null) {
            currentEosName = eosEntity.getCurrentEosName();
        }
    }

    @Override
    protected void setNavibarTitle(String title, boolean isShowBack, boolean isOnBackFinishActivity) {
        mTitleBar = naviBar;
        mTitleBar.setTitle(title);
        mTitleBar.setTitleColor(com.hxlx.core.lib.R.color.ffffff_white_1000);
        mTitleBar.setTitleSize(20);
        mTitleBar.setImmersive(true);
        if (isShowBack) {
            mTitleBar.setLeftImageResource(com.hxlx.core.lib.R.drawable.ic_btn_back);
            mTitleBar.setLeftClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TabSelectedEvent tabSelectedEvent = new TabSelectedEvent();
                    tabSelectedEvent.setPosition(TAB_WALLET);
                    tabSelectedEvent.setRefresh(true);
                    EventBusProvider.postSticky(tabSelectedEvent);
                    if (isOnBackFinishActivity) {
                        getActivity().finish();
                    } else {
                        pop();
                    }
                }
            });
        }
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
                        currentLastPos = history.last_pos;
                        doRequest(currentLastPos);
                    }
                }

            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                currentLastPos = -1;
                doRequest(currentLastPos);
            }
        });


        //第一次请求数据
        doRequest(-1);

        //设置点击事件
        mRecyclerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (mAdapter != null && EmptyUtils.isNotEmpty(mAdapter.getData())) {
                    Bundle bundle = new Bundle();
                    TransferHistory curTransfer = mAdapter.getData().get(position);
                    bundle.putParcelable(ParamConstants. KEY_CUR_TRANSFER, curTransfer);
                    start(TransferRecordDetailFragment.newInstance(bundle));
                }

            }
        });
    }

    public void doRequest(int currentLastPos) {
        getP().requestHistory(currentEosName, currentLastPos, isFirstLoad);

    }

    public void setFirstLoad(boolean firstLoad) {
        isFirstLoad = firstLoad;
    }

    public void loadMoreData(List<TransferHistory> dataList) {
        if (EmptyUtils.isEmpty(dataList)) {
            dataList = new ArrayList<>();
        }

        if (mAdapter == null) {
            //第一次请求
            mAdapter = new TransferRecordListAdapter(dataList, currentEosName);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            //加载更多
            mAdapter.addData(dataList);
            viewRefresh.finishLoadmore();
        }

    }

    public void refreshData(List<TransferHistory> dataList) {
        if (EmptyUtils.isEmpty(dataList)) {
            dataList = new ArrayList<>();
        }

        if (mAdapter != null && EmptyUtils.isNotEmpty(mAdapter.getData())) {
            mAdapter.getData().clear();
            mAdapter.setNewData(dataList);
        } else {
            mAdapter = new TransferRecordListAdapter(dataList, currentEosName);
            mRecyclerView.setAdapter(mAdapter);
        }
        viewRefresh.finishRefresh();
        viewRefresh.setLoadmoreFinished(false);

    }


    @Override
    public int getLayoutId() {
        return R.layout.eos_fragment_transfer_record_list;
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
                viewRefresh.setLoadmoreFinished(true);

            }

        } else {
            listMultipleStatusView.showEmpty();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(TransferHistoryListRequest.TAG);
    }
}
