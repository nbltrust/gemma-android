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

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.cybex.base.view.refresh.CommonRefreshLayout;
import com.cybex.base.view.statusview.MultipleStatusView;
import com.cybex.componentservice.db.entity.EosTransactionEntity;
import com.cybex.componentservice.db.entity.EosWalletEntity;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.componentservice.utils.AmountUtil;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.event.CybexPriceEvent;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.adapter.TransferRecordListAdapter;
import com.cybex.gma.client.ui.model.response.TransferHistory;
import com.cybex.gma.client.ui.model.vo.EosTokenVO;
import com.cybex.gma.client.ui.presenter.AssetDetailPresenter;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;
import com.tapadoo.alerter.Alerter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 资产详情页面
 */
public class EosAssetDetailActivity extends XActivity<AssetDetailPresenter> {

    @BindView(R.id.tv_currency_type) TextView tvCurrencyType;
    @BindView(R.id.view_asset_value) LinearLayout viewAssetValue;
    @BindView(R.id.tv_token_name) TextView tvTokenName;
    @BindView(R.id.view_asset_detail_bot) LinearLayout viewAssetDetailBot;
    private TransferRecordListAdapter mAdapter;
    List<TransferHistory> curDataList;
    List<EosTransactionEntity> curTransactionList;//给Adapter的数据源
    List<EosTransactionEntity> updateTransactionList;//暂存的给Adapter的数据源，更新用

    public String getCurHead() {
        return curHead;
    }

    public void setCurHead(String curHead) {
        this.curHead = curHead;
    }

    public String getCurLib() {
        return curLib;
    }

    public void setCurLib(String curLib) {
        this.curLib = curLib;
        //getBlockNum(curTransactionList);
    }

    private String curHead;//当前head block num
    private String curLib;//当前Lib num

    private String currentEosName = "";
    private Bundle bundle;
    private EosTokenVO curToken;
    private String asset_type;
    private int curPage;//交易记录当前页数
    private String curEosPrice;
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
    @BindView(R.id.view_refresh_token_asset) CommonRefreshLayout viewRefresh;
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
        curPage = 1;
        curDataList = new ArrayList<>();
        setNavibarTitle(getString(R.string.title_asset_detail), true);
        bundle = getIntent().getExtras();

        int walletType = DBManager.getInstance().getMultiWalletEntityDao().getCurrentMultiWalletEntity()
                .getWalletType();
        if (walletType == MultiWalletEntity.WALLET_TYPE_HARDWARE) {
            viewAssetDetailBot.setVisibility(View.GONE);
        }

        if (bundle != null) {
            curToken = bundle.getParcelable(ParamConstants.EOS_TOKENS);

            if (curToken != null) {
                //EOS和TOKENS的公共逻辑
                asset_type = curToken.getTokenSymbol();
                btnGoTransfer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UISkipMananger.launchTransferWithBundle(EosAssetDetailActivity.this, bundle);
                    }
                });

                btnCollect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UISkipMananger.launchCollectWithBundle(EosAssetDetailActivity.this, bundle);
                    }
                });

                //设置Token相关UI
                tvTokenName.setText(curToken.getTokenSymbol());
                tvEosAmount.setText(curToken.getQuantity());
                String tokenUrl = curToken.getLogo_url();
                if (tokenUrl == null || tokenUrl.equals("")) {
                    ivLogoEosAsset.setImageResource(R.drawable.ic_token_unknown);
                } else {
                    Glide.with(ivLogoEosAsset.getContext())
                            .load(tokenUrl)
                            .into(ivLogoEosAsset);
                }

                //EOS和TOKENS差异处理
                if (asset_type != null) {
                    if (asset_type.equals(ParamConstants.SYMBOL_EOS)) {
                        //EOS资产
                        //资源管理
                        tvResourceManage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                UISkipMananger.launchResourceDetail(EosAssetDetailActivity.this, bundle);
                            }
                        });

                        //投票
                        tvVote.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                MultiWalletEntity curWallet = DBManager.getInstance()
                                        .getMultiWalletEntityDao()
                                        .getCurrentMultiWalletEntity();
                                EosWalletEntity curEosWallet = curWallet.getEosWalletEntities().get(0);
                                if (EmptyUtils.isNotEmpty(curWallet)) {
                                    bundle.putString("cur_eos_name", curEosWallet.getCurrentEosName());
                                    UISkipMananger.launchVote(EosAssetDetailActivity.this, bundle);
                                }
                            }
                        });

                    } else {
                        //EOS Tokens 资产
                        tvRmbAmount.setVisibility(View.GONE);
                        tvCurrencyType.setVisibility(View.GONE);
                        viewAssetDetailBot.setVisibility(View.GONE);
                    }
                }
            }
            //第一次请求数据
            doRequest(curPage);
        }


        viewRefresh.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                doRequest(++curPage);
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                curPage = 1;
                doRequest(curPage);
                viewRefresh.finishLoadmore();
            }
        });

    }

    public void setmRecyclerViewOnClick() {
        //设置点击事件
        mRecyclerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (mAdapter != null && EmptyUtils.isNotEmpty(mAdapter.getData())) {
                    Bundle bundle = new Bundle();
                    TransferHistory curTransfer = curDataList.get(position);
                    bundle.putParcelable(ParamConstants.KEY_CUR_TRANSFER, curTransfer);
                    UISkipMananger.launchTransferRecordDetail(EosAssetDetailActivity.this, bundle);
                }

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

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void getCybexPrice(CybexPriceEvent event) {
        curEosPrice = event.getEosPrice();
    }

    public void showLoading() {
        viewAssetTop.setVisibility(View.GONE);
        listMultipleStatusView.showLoading();
    }

    public void showError() {
        listMultipleStatusView.showError();
    }

    public void showContent() {
        viewAssetTop.setVisibility(View.VISIBLE);
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
        viewRefresh.finishRefresh();

    }

    public void doRequest(int page) {

        LoggerManager.d("curPage", page);

        if (bundle != null) {
            if (asset_type.equals(ParamConstants.SYMBOL_EOS)) {
                //EOS
                getP().getInfo(
                        currentEosName,
                        page,
                        ParamConstants.TRANSFER_HISTORY_SIZE,//5
                        ParamConstants.SYMBOL_EOS,
                        ParamConstants.CONTRACT_EOS,
                        true);
            } else {
                //Tokens
                if (curToken != null) {
                    String symbol = curToken.getTokenSymbol();
                    String contract = curToken.getTokenName();
                    getP().getInfo(currentEosName,
                            page,
                            ParamConstants.TRANSFER_HISTORY_SIZE,
                            symbol,
                            contract,
                            false);
                }
            }
        }
    }

    /**
     * 第一次请求和刷新数据方法
     *
     * @param dataList
     */
    public void refreshData(List<TransferHistory> dataList) {

        if (EmptyUtils.isEmpty(dataList)) {
            dataList = new ArrayList<>();
        }

        curDataList = dataList;
        getP().saveTransaction(dataList);
        getP().queryStatus(curDataList);

        if (mAdapter != null && EmptyUtils.isNotEmpty(mAdapter.getData())) {
            mAdapter.getData().clear();
            mAdapter.setNewData(curDataList);
            getP().updateDataSource(curDataList);
        } else {
            //首次请求
            mAdapter = new TransferRecordListAdapter(curDataList, currentEosName);
            mAdapter.setHasStableIds(true);
            mRecyclerView.setAdapter(mAdapter);
            getP().updateDataSource(curDataList);
        }

        viewRefresh.finishRefresh();
        viewRefresh.setLoadmoreFinished(false);
    }


    public void loadMoreData(List<TransferHistory> dataList) {
        if (EmptyUtils.isEmpty(dataList)) {
            dataList = new ArrayList<>();
        }

        curDataList.addAll(dataList);
        getP().saveTransaction(dataList);
        getP().queryStatus(dataList);

        if (mAdapter == null) {
            //第一次请求
            mAdapter = new TransferRecordListAdapter(curDataList, currentEosName);
            mRecyclerView.setAdapter(mAdapter);
            getP().updateDataSource(curDataList);
        } else {
            //加载更多
            mAdapter.addData(dataList);
            getP().updateDataSource(curDataList);
            viewRefresh.finishLoadmore();
        }
    }


    public void setLoadMoreFinish() {
        viewRefresh.setLoadmoreFinished(true);
    }

    public void showBalance(String balance) {
        tvEosAmount.setText(balance.split(" ")[0]);
        if (curEosPrice != null) {
            String eosValue = AmountUtil.mul(balance.split(" ")[0], curEosPrice, 2);
            tvRmbAmount.setText(eosValue);
        }
    }

    public void updateTransactionStatus(){
        curDataList = getP().updateDataSource(curDataList);
        mAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Alerter.clearCurrent(this);
    }
}
