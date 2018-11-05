package com.cybex.eth.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.cybex.base.view.refresh.CommonRefreshLayout;
import com.cybex.base.view.statusview.MultipleStatusView;
import com.cybex.componentservice.db.entity.EthWalletEntity;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.eth.R;
import com.cybex.eth.ui.adapter.EthTransferRecordListAdapter;
import com.cybex.eth.ui.model.EthTransferRecord;
import com.cybex.eth.ui.presenter.EthAssetDetailPresenter;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;
import java.util.ArrayList;
import java.util.List;


/**
 * 资产详情页面
 */
public class EthAssetDetailActivity extends XActivity<EthAssetDetailPresenter> {

    private EthTransferRecordListAdapter mAdapter;
    private boolean isFirstLoad = true;
    private int currentLastPos = -1;
    private String currentAccountName = "";
    private Bundle bundle;

    TitleBar btnNavibar;
    TextView tvEthAmount;
    TextView tvRmbAmount;
    ImageView ivLogoEthAsset;
    Button btnGoTransfer;
    Button btnCollect;
    ConstraintLayout viewAssetTop;
    MultipleStatusView listMultipleStatusView;
    RecyclerView mRecyclerView;
    CommonRefreshLayout viewRefresh;

    @Override
    public void bindUI(View rootView) {
        viewRefresh = (CommonRefreshLayout) findViewById(R.id.view_refresh_asset);
        listMultipleStatusView = (MultipleStatusView) findViewById(R.id.list_multiple_status_view);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_list);
        viewAssetTop = (ConstraintLayout) findViewById(R.id.view_asset_top);
        btnCollect = (Button) findViewById(R.id.btn_collect);
        btnGoTransfer = (Button) findViewById(R.id.btn_go_transfer);
        ivLogoEthAsset = (ImageView) findViewById(R.id.iv_logo_eth_asset);
        tvEthAmount = (TextView) findViewById(R.id.tv_eth_amount);
        tvRmbAmount = (TextView) findViewById(R.id.tv_rmb_amount);


        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);

        EthWalletEntity entity = DBManager.getInstance()
                .getMultiWalletEntityDao().getCurrentMultiWalletEntity().getEthWalletEntities().get(0);
        if (entity != null) {
            currentAccountName = entity.getAddress();
        }
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setNavibarTitle(getString(R.string.title_asset_detail), true);
//        bundle = getIntent().getExtras();
//        if (bundle != null){
//            String assetsValue = bundle.getString(ParamConstants.EOS_ALL_ASSET_VALUE);
//            tvRmbAmount.setText(assetsValue);
//            LoggerManager.d("assetsValue received", assetsValue);
//            String eosAmount = bundle.getString(ParamConstants.EOS_AMOUNT);
//            tvEosAmount.setText(eosAmount);
//        }
        viewRefresh.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                if (mAdapter != null) {
                    List<EthTransferRecord> historyList = mAdapter.getData();
                    if (!EmptyUtils.isEmpty(historyList)) {
                        EthTransferRecord history = historyList.get(historyList.size() - 1);
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
//                if (mAdapter != null && EmptyUtils.isNotEmpty(mAdapter.getData())) {
//                    Bundle bundle = new Bundle();
//                    TransferHistory curTransfer = mAdapter.getData().get(position);
//                    bundle.putParcelable(ParamConstants.KEY_CUR_TRANSFER, curTransfer);
//                    start(TransferRecordDetailFragment.newInstance(bundle));
//                }

            }
        });


        btnGoTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                UISkipMananger.launchTransfer(AssetDetailActivity.this);
            }
        });

        btnCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,EthCollectActivity.class));
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.eth_activity_asset_detail;
    }

    @Override
    public EthAssetDetailPresenter newP() {
        return new EthAssetDetailPresenter();
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
            List<EthTransferRecord> historyList = mAdapter.getData();
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
        getP().requestHistory(currentAccountName, currentLastPos, isFirstLoad);

    }

    public void setFirstLoad(boolean firstLoad) {
        isFirstLoad = firstLoad;
    }

    public void loadMoreData(List<EthTransferRecord> dataList) {
        if (EmptyUtils.isEmpty(dataList)) {
            dataList = new ArrayList<>();
        }

        if (mAdapter == null) {
            //第一次请求
            mAdapter = new EthTransferRecordListAdapter(dataList, currentAccountName);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            //加载更多
            mAdapter.addData(dataList);
            viewRefresh.finishLoadmore();
        }

    }

    public void refreshData(List<EthTransferRecord> dataList) {
        if (EmptyUtils.isEmpty(dataList)) {
            dataList = new ArrayList<>();
        }

        if (mAdapter != null && EmptyUtils.isNotEmpty(mAdapter.getData())) {
            mAdapter.getData().clear();
            mAdapter.setNewData(dataList);
        } else {
            mAdapter = new EthTransferRecordListAdapter(dataList, currentAccountName);
            mRecyclerView.setAdapter(mAdapter);
        }
        viewRefresh.finishRefresh();
        viewRefresh.setLoadmoreFinished(false);

    }

}
