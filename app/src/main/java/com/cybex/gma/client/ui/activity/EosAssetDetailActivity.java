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
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

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

    private String currentEosName = "";
    private Bundle bundle;
    private EosTokenVO curToken;
    private int asset_type;
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
        setNavibarTitle(getString(R.string.title_asset_detail), true);
        bundle = getIntent().getExtras();
        if (bundle != null) {
             asset_type = bundle.getInt(ParamConstants.COIN_TYPE);
            if (asset_type == ParamConstants.COIN_TYPE_EOS) {
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

                btnGoTransfer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UISkipMananger.launchTransfer(EosAssetDetailActivity.this);
                    }
                });

                btnCollect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UISkipMananger.launchCollect(EosAssetDetailActivity.this);
                    }
                });

            } else {
                //EOS Tokens 资产
                curToken = bundle.getParcelable(ParamConstants.EOS_TOKENS);
                tvRmbAmount.setVisibility(View.GONE);
                tvCurrencyType.setVisibility(View.GONE);
                viewAssetDetailBot.setVisibility(View.GONE);
                if (curToken != null) {
                    //设置Token相关UI
                    tvTokenName.setText(curToken.getTokenSymbol());
                    tvEosAmount.setText(curToken.getQuantity());
                    String tokenUrl = curToken.getLogo_url();
                    if (tokenUrl == null || tokenUrl.equals("")){
                        ivLogoEosAsset.setImageResource(R.drawable.ic_token_unknown);
                    }else {
                        Glide.with(ivLogoEosAsset.getContext())
                                .load(tokenUrl)
                                .into(ivLogoEosAsset);
                    }
                }

                btnGoTransfer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UISkipMananger.launchTransferWithBundle(EosAssetDetailActivity.this, bundle);
                    }
                });

                btnCollect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Bundle bd = new Bundle();
//                        bd.putParcelable(ParamConstants.EOS_TOKENS, curToken);
                        UISkipMananger.launchCollectWithBundle(EosAssetDetailActivity.this, bundle);
                    }
                });
            }


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

        //第一次请求数据
        doRequest(curPage);

        //设置点击事件
        mRecyclerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (mAdapter != null && EmptyUtils.isNotEmpty(mAdapter.getData())) {
                    Bundle bundle = new Bundle();
                    TransferHistory curTransfer = mAdapter.getData().get(position);
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
    public void getCybexPrice(CybexPriceEvent event){
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

            if (asset_type == ParamConstants.COIN_TYPE_EOS) {
                //查询EOS交易记录
                getP().requestHistory(
                        currentEosName,
                        page,
                        ParamConstants.TRANSFER_HISTORY_SIZE,//20
                        ParamConstants.SYMBOL_EOS,
                        ParamConstants.CONTRACT_EOS,
                        true);

            }else{
                //查询EOS糖果交易记录
                String symbol = curToken.getTokenSymbol();
                String contract = curToken.getTokenName();
                getP().requestHistory(currentEosName,
                        page,
                        ParamConstants.TRANSFER_HISTORY_SIZE,
                        symbol,
                        contract,
                        false);
            }
        }
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

    public void finishRefresh(){
        viewRefresh.finishRefresh();
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

    public void setLoadMoreFinish(){
        viewRefresh.setLoadmoreFinished(true);
    }

    public void showBalance(String balance){
        tvEosAmount.setText(balance.split(" ")[0]);
        if (curEosPrice != null){
            String eosValue = AmountUtil.mul(balance.split(" ")[0], curEosPrice,2);
            tvRmbAmount.setText(eosValue);
        }
    }

}
