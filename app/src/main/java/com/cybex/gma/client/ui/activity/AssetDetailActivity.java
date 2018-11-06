package com.cybex.gma.client.ui.activity;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.cybex.base.view.refresh.CommonRefreshLayout;
import com.cybex.base.view.statusview.MultipleStatusView;
import com.cybex.componentservice.db.entity.EosWalletEntity;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.db.entity.WalletEntity;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.adapter.TransferRecordListAdapter;
import com.cybex.gma.client.ui.fragment.TransferRecordDetailFragment;
import com.cybex.gma.client.ui.model.response.TransferHistory;
import com.cybex.gma.client.ui.presenter.AssetDetailPresenter;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 资产详情页面
 */
public class AssetDetailActivity extends XActivity<AssetDetailPresenter> {

    private TransferRecordListAdapter mAdapter;
    private boolean isFirstLoad = true;
    private int currentLastPos = -1;
    private String currentEosName = "";
    private Bundle bundle;
    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.iv_logo_eos_asset) ImageView ivLogoEosAsset;
    @BindView(R.id.tv_eos_amount) TextView tvEosAmount;
    @BindView(R.id.tv_rmb_amount) TextView tvRmbAmount;
    @BindView(R.id.btn_go_transfer) Button btnGoTransfer;
    @BindView(R.id.btn_collect) Button btnCollect;
    @BindView(R.id.view_asset_buttons) LinearLayout viewAssetButtons;
    @BindView(R.id.view_asset_top) ConstraintLayout viewAssetTop;
    @BindView(R.id.list_multiple_status_view) MultipleStatusView listMultipleStatusView;
    @BindView(R.id.rv_list) RecyclerView mRecyclerView;
    //@BindView(R.id.view_scroll) NestedScrollView viewScroll;
    @BindView(R.id.view_refresh_asset) CommonRefreshLayout viewRefresh;
    @BindView(R.id.tv_vote) TextView tvVote;
    @BindView(R.id.tv_resource_manage) TextView tvResourceManage;

    @Override
    public void bindUI(View rootView) {
        ButterKnife.bind(this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);

        EosWalletEntity entity = DBManager.getInstance()
                .getMultiWalletEntityDao().getCurrentMultiWalletEntity().getEosWalletEntities().get(0);
        if (entity != null) {
            currentEosName = entity.getCurrentEosName();
        }
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setNavibarTitle(getString(R.string.title_asset_detail), true);
        bundle = getIntent().getExtras();
        if (bundle != null){
            String assetsValue = bundle.getString(ParamConstants.EOS_ALL_ASSET_VALUE);
            tvRmbAmount.setText(assetsValue);
            LoggerManager.d("assetsValue received", assetsValue);
            String eosAmount = bundle.getString(ParamConstants.EOS_AMOUNT);
            tvEosAmount.setText(eosAmount);
        }
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
                    bundle.putParcelable(ParamConstants.KEY_CUR_TRANSFER, curTransfer);
                    UISkipMananger.launchTransferRecordDetail(AssetDetailActivity.this, bundle);
                }

            }
        });

        tvResourceManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               UISkipMananger.launchResourceDetail(AssetDetailActivity.this, bundle);
            }
        });

        tvVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MultiWalletEntity curWallet = DBManager.getInstance().getMultiWalletEntityDao().getCurrentMultiWalletEntity();
                EosWalletEntity curEosWallet = curWallet.getEosWalletEntities().get(0);
                if (EmptyUtils.isNotEmpty(curWallet)) {
                    bundle.putString("cur_eos_name", curEosWallet.getCurrentEosName());
                    UISkipMananger.launchVote(AssetDetailActivity.this, bundle);
                }
            }
        });

        btnGoTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UISkipMananger.launchTransfer(AssetDetailActivity.this);
            }
        });

        btnCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UISkipMananger.launchCollect(AssetDetailActivity.this);
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.eos_activity_asset_detail;
    }

    @Override
    public AssetDetailPresenter newP() {
        return new AssetDetailPresenter();
    }

    @Override
    public boolean useEventBus() {
        return true;
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

}
