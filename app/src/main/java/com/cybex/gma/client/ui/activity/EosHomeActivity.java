package com.cybex.gma.client.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.allen.library.SuperTextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.cybex.base.view.progress.RoundCornerProgressBar;
import com.cybex.base.view.refresh.CommonRefreshLayout;
import com.cybex.componentservice.api.ApiPath;
import com.cybex.componentservice.config.CacheConstants;
import com.cybex.componentservice.db.entity.EosWalletEntity;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.componentservice.utils.ClipboardUtils;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.event.ChangeAccountEvent;
import com.cybex.gma.client.event.PollEvent;
import com.cybex.gma.client.event.ValidateResultEvent;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.adapter.ChangeAccountAdapter;
import com.cybex.gma.client.ui.adapter.EosTokensAdapter;
import com.cybex.gma.client.ui.model.response.AccountInfo;
import com.cybex.gma.client.ui.model.response.AccountRefoundRequest;
import com.cybex.gma.client.ui.model.vo.EOSNameVO;
import com.cybex.gma.client.ui.model.vo.EosTokenVO;
import com.cybex.gma.client.ui.model.vo.HomeCombineDataVO;
import com.cybex.gma.client.ui.model.vo.ResourceInfoVO;
import com.cybex.gma.client.ui.presenter.EosHomePresenter;
import com.cybex.componentservice.utils.AmountUtil;
import com.hxlx.core.lib.common.async.TaskManager;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.GsonUtils;
import com.hxlx.core.lib.utils.SPUtils;
import com.hxlx.core.lib.utils.common.utils.AppManager;
import com.hxlx.core.lib.utils.common.utils.DateUtil;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.siberiadante.customdialoglib.CustomFullDialog;
import com.tapadoo.alerter.Alerter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.hypertrack.smart_scheduler.SmartScheduler;

public class EosHomeActivity extends XActivity<EosHomePresenter> {

    @BindView(R.id.view_resource_manage) ConstraintLayout viewResourceManage;
    @BindView(R.id.tv_number_of_tokens) TextView tvNumberOfTokens;
    @BindView(R.id.view_eos_tokens) LinearLayout viewEosTokens;
    @BindView(R.id.tv_currency_type) TextView tvCurrencyType;
    @BindView(R.id.iv_eos_asset_logo) ImageView ivEosAssetLogo;
    @BindView(R.id.tv_eos) TextView tvEos;
    @BindView(R.id.tv_eos_amount) TextView tvEosAmount;
    @BindView(R.id.tv_eos_value) TextView tvEosValue;
    @BindView(R.id.view_soft_wallet) ConstraintLayout viewSoftWallet;
    @BindView(R.id.tv_account_name) TextView tvAccountName;
    @BindView(R.id.iv_back) ImageView ivBack;
    @BindView(R.id.iv_copy) ImageView ivCopy;
    @BindView(R.id.tv_current_account) TextView tvCurrentAccount;
    @BindView(R.id.iv_change_account) ImageView ivChangeAccount;
    @BindView(R.id.view_top_navigation) ConstraintLayout viewTopNavigation;
    @BindView(R.id.show_cpu) LinearLayout showCpu;
    @BindView(R.id.tv_assets) TextView tvAssets;
    @BindView(R.id.recycler_token_list) RecyclerView recyclerTokenList;
    private EosWalletEntity curEosWallet;
    private int walletID;
    private String curEosUsername;
    @BindView(R.id.total_EOS_amount) TextView totalEOSAmount;
    @BindView(R.id.total_CNY_amount) TextView totalCNYAmount;
    @BindView(R.id.balance) SuperTextView tvBalance;
    @BindView(R.id.redeem) SuperTextView tvRedeem;
    @BindView(R.id.layout_top_info) LinearLayout layoutTopInfo;
    @BindView(R.id.layout_info) ConstraintLayout layoutInfo;
    @BindView(R.id.scroll_wallet_tab) NestedScrollView mScrollView;
    @BindView(R.id.progressbar_cpu_small) RoundCornerProgressBar progressBarCPU;
    @BindView(R.id.progressbar_net_small) RoundCornerProgressBar progressBarNET;
    @BindView(R.id.progressbar_ram_small) RoundCornerProgressBar progressBarRAM;
    @BindView(R.id.view_cpu) View viewCPU;
    @BindView(R.id.view_net) View viewNET;
    @BindView(R.id.view_ram) View viewRAM;
    @BindView(R.id.view_refresh_wallet) CommonRefreshLayout refreshLayout;
    private Bundle bundle;

    private ResourceInfoVO resourceInfoVO;
    private String curUSDTPrice;
    private int savedCurrency;
    private EosTokensAdapter mAdapter;

    @OnClick(R.id.view_resource_manage)
    public void goAssetDetail() {
//        bundle.putString(ParamConstants.EOS_ASSET_VALUE, getAssetsValue());
//        bundle.putString(ParamConstants.EOS_AMOUNT, getEosAmount());
        bundle.putInt(ParamConstants.COIN_TYPE, ParamConstants.COIN_TYPE_EOS);
        UISkipMananger.launchAssetDetail(EosHomeActivity.this, bundle);
    }

    @OnClick({R.id.view_cpu, R.id.view_net, R.id.view_ram})
    public void clickViews(View view) {
        switch (view.getId()) {
            case R.id.view_cpu:
                goResourceManagement();
                break;
            case R.id.view_net:
                goResourceManagement();
                break;
            case R.id.view_ram:
                goResourceManagement();
                break;
        }
    }

    @OnClick({R.id.iv_copy, R.id.iv_back})
    public void onNavigationBarClicked(View v) {
        switch (v.getId()) {
            case R.id.iv_copy:
                //复制账户名

                ClipboardUtils.copyText(this, getAccountName());
                GemmaToastUtils.showLongToast(getString(R.string.eos_copy_success));

                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

    public String getAccountName() {
        return tvAccountName.getText().toString().trim();
    }

    public void goResourceManagement() {

        UISkipMananger.launchResourceDetail(EosHomeActivity.this, bundle);
    }

    @Override
    public boolean useEventBus() {
        return true;
    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onReceivePollevent(PollEvent pollEvent) {
        if (EmptyUtils.isNotEmpty(pollEvent) && pollEvent.isDone()) {
            if (Alerter.isShowing()) {
                Alerter.hide();
                LoggerManager.d("Alert Hide");
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onChangeAccountEvent(ChangeAccountEvent event) {
        if (EmptyUtils.isNotEmpty(event)) {
            LoggerManager.d("---changeAccount event---");

//            String curEosName = DBManager.getInstance().getMultiWalletEntityDao().getCurrentMultiWalletEntity()
//                    .getEosWalletEntities().get(0).getCurrentEosName();
//            LoggerManager.d("curEosName", curEosName);

            getP().requestHomeCombineDataVO();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onValidateConfirmed(ValidateResultEvent event) {
        if (event.isSuccess()) {
            Alerter.hide();
        }
    }

    /**
     * 显示主界面信息
     *
     * @param vo 账户聚合数据信息
     */
    public void showMainInfo(final HomeCombineDataVO vo) {

        TaskManager.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (vo != null) {

                    //String jsonVO = GsonUtils.objectToJson(vo);
                    //SPUtils.getInstance().put("HomeCombineData", jsonVO);

                    String banlance = vo.getBanlance();
                    String unitPriceEOS = vo.getUnitPrice();
                    curUSDTPrice = vo.getUnitPriceUSDT();
                    AccountInfo info = vo.getAccountInfo();


                    //显示总资产信息
                    showTotalPriceInfo(banlance, unitPriceEOS, info);
                    //显示cpu，net，ram进度
                    showResourceInfo(banlance, info);
                    //显示赎回信息
                    showRefundInfo(info);
                }
            }
        });

    }


    /**
     * 显示赎回信息
     *
     * 上一次请求时间 需要加上72小时跟本地时间对比
     */
    private void showRefundInfo(AccountInfo info) {
        if (info != null) {
            AccountRefoundRequest request = info.getRefund_request();
            if (request != null) {
                String requestTime = request.request_time;
                if (EmptyUtils.isNotEmpty(requestTime)) {
                    long requestOldMills = DateUtil.getMills(requestTime, DateUtil.Format.EOS_DATE_FORMAT);
                    requestOldMills = requestOldMills + 72 * 60 * 60 * 1000;
                    LoggerManager.d("newRequestOldMills", requestOldMills);
                    String refundTime = getP().dateDistance2now(requestOldMills,
                            DateUtil.Format.EOS_DATE_FORMAT_WITH_MILLI);

                    String netAmount = request.net_amount;
                    String netNum = "0";
                    if (EmptyUtils.isNotEmpty(netAmount)) {
                        String[] netArr = netAmount.split(" ");
                        if (EmptyUtils.isNotEmpty(netArr)) {
                            netNum = netArr[0];
                        }
                    }

                    String cpuAmount = request.cpu_amount;
                    String cpuNum = "0";
                    if (EmptyUtils.isNotEmpty(cpuAmount)) {
                        String[] cpuArr = cpuAmount.split(" ");
                        if (EmptyUtils.isNotEmpty(cpuArr)) {
                            cpuNum = cpuArr[0];
                        }
                    }

                    String totalNum = AmountUtil.add(netNum, cpuNum, 4);

                    if (Double.parseDouble(totalNum) > 0) {
                        String totalRefund = totalNum + " EOS";
                        tvRedeem.setRightString(totalRefund);
                    }
                    
//                    if (EmptyUtils.isNotEmpty(refundTime)) {
//                        tvRedeem.setRightBottomString(refundTime);
//                    }

                }
            }

        }

    }

    private void showResourceInfo(String balance, AccountInfo info) {
        if (info != null) {
            //CPU使用进度
            AccountInfo.CpuLimitBean cpuLimitBean = info.getCpu_limit();
            int cpuUsed = 0;
            int cpuTotal = 0;
            if (cpuLimitBean != null) {
                cpuUsed = cpuLimitBean.getUsed();
                cpuTotal = cpuLimitBean.getMax();
            }
            double i = cpuUsed / (double) cpuTotal * 100;
            int cpuProgress = (int) Math.ceil(i);
            progressBarCPU.setMax(100);
            progressBarCPU.setProgress(cpuProgress);
            setProgressColor(progressBarCPU, cpuProgress);


            //NET使用进度
            AccountInfo.NetLimitBean netLimitBean = info.getNet_limit();
            int netUsed = 0;
            int netTotal = 0;
            if (netLimitBean != null) {
                netUsed = netLimitBean.getUsed();
                netTotal = netLimitBean.getMax();
            }
            double j = netUsed / (double) netTotal * 100;
            int netProgress = (int) Math.ceil(j);
            progressBarNET.setMax(100);
            progressBarNET.setProgress(netProgress);
            setProgressColor(progressBarNET, netProgress);


            //ram使用进度
            int ramTotal = info.getRam_quota();
            int ramUsed = info.getRam_usage();
            double k = ramUsed / (double) ramTotal * 100;
            int ramProgress = (int) Math.ceil(k);
            progressBarRAM.setMax(100);
            progressBarRAM.setProgress(ramProgress);
            setProgressColor(progressBarRAM, ramProgress);

//            resourceInfoVO = new ResourceInfoVO();
//            resourceInfoVO.setBanlance(balance);
//            resourceInfoVO.setCpuProgress(cpuProgress);
//            resourceInfoVO.setCpuUsed(cpuUsed);
//            resourceInfoVO.setCpuTotal(cpuTotal);
//            resourceInfoVO.setNetTotal(netTotal);
//            resourceInfoVO.setNetProgress(netProgress);
//            resourceInfoVO.setNetUsed(netUsed);
//            resourceInfoVO.setRamUsed(ramUsed);
//            resourceInfoVO.setRamProgress(ramProgress);
//            resourceInfoVO.setRamTotal(ramTotal);
//            resourceInfoVO.setCpuWeight(info.getCpu_weight());
//            resourceInfoVO.setNetWeight(info.getNet_weight());
//            bundle.putParcelable("resourceInfo", resourceInfoVO);
        }

    }

    private void setProgressColor(RoundCornerProgressBar progressBar, int progress) {
        if (progress < 85) {
            progressBar.setProgressColor(getResources().getColor(R.color.black_context));
        } else {
            progressBar.setProgressColor(getResources().getColor(R.color.scarlet));
        }
    }

    private void showTotalPriceInfo(String balance, String unitPrice, AccountInfo info) {
        String balanceNumber = "0";
        String netNumber;
        String cpuNumber;
        String totalPrice = "0";
        String[] banlanceNumberArr = balance.split(" ");
        if (EmptyUtils.isNotEmpty(banlanceNumberArr)) {
            balanceNumber = banlanceNumberArr[0];
        }
        if (info != null) {
            AccountInfo.SelfDelegatedBandwidthBean selfDelegatedBandwidth = info.getSelf_delegated_bandwidth();
            if (selfDelegatedBandwidth == null) {
                //没有自己给自己抵押的资源,总资产为可用余额
                totalPrice = balanceNumber;
            } else {
                //有抵押资源，总资产为可用余额加上抵押资源
                String[] cpuNumber_arr = selfDelegatedBandwidth.getCpu_weightX().split(" ");
                String[] netNumber_arr = selfDelegatedBandwidth.getNet_weightX().split(" ");

                cpuNumber = cpuNumber_arr[0];
                netNumber = netNumber_arr[0];

                String totalResource = AmountUtil.add(cpuNumber, netNumber, 4);
                totalPrice = AmountUtil.add(totalResource, balanceNumber, 4);
            }

        }


        String totalCNY = AmountUtil.mul(unitPrice, totalPrice, 2);
        String totalUSD = AmountUtil.div(totalCNY, curUSDTPrice, 2);
        totalEOSAmount.setText(totalPrice);
        tvEosAmount.setText(totalPrice);
        savedCurrency = SPUtils.getInstance().getInt("currency_unit");
        switch (savedCurrency) {
            case CacheConstants.CURRENCY_CNY:
                tvCurrencyType.setText("≈ ¥ ");
                tvEosValue.setText(" ≈ " + balance);
                totalCNYAmount.setText(getP().formatCurrency(totalCNY));
                break;
            case CacheConstants.CURRENCY_USD:
                tvCurrencyType.setText("≈ $ ");
                totalCNYAmount.setText(" ≈ " + totalUSD);
                tvEosValue.setText(balance);
                break;
            default:
                tvCurrencyType.setText("≈ ¥ ");
                tvEosValue.setText(balance);
                totalCNYAmount.setText(getP().formatCurrency(totalCNY));
                break;
        }

    }

    /**
     * 账户余额
     *
     * @param banlance
     */
    public void showBalance(String banlance) {
        TaskManager.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                tvBalance.setRightString(banlance);
            }
        });

    }

    @Override
    public void bindUI(View rootView) {
        ButterKnife.bind(this);
        //OverScrollDecoratorHelper.setUpOverScroll(mScrollView);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        bundle = new Bundle();
        getP().requestHomeCombineDataVO();
        //初始化当前节点
        if (SPUtils.getInstance().getString("curNode") != null) {
            String curHost = SPUtils.getInstance().getString("curNode");
            //LoggerManager.d("curNode", curHost);
            //ApiPath.setHOST_ON_CHAIN(curHost);
        } else {
            SPUtils.getInstance().put("curNode", ApiPath.EOS_CYBEX);
        }

        //LoggerManager.d("host", ApiPath.getHOST_ON_CHAIN());
        AppManager.getAppManager().finishActivity(CreateManageActivity.class);
        //下拉刷新
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(200);
                getP().requestHomeCombineDataVO();
            }
        });

        curEosWallet = DBManager.getInstance().getMultiWalletEntityDao().getCurrentMultiWalletEntity()
                .getEosWalletEntities().get(0);

        if (EmptyUtils.isNotEmpty(curEosWallet)) {
            curEosUsername = curEosWallet.getCurrentEosName();
            LoggerManager.d("curEosUsername", curEosUsername);
            tvAccountName.setText(curEosUsername);
            tvCurrentAccount.setText(curEosUsername);

            //多账户切换
            String json = curEosWallet.getEosNameJson();
            List<String> eosNamelist = GsonUtils.parseString2List(json, String.class);
            if (EmptyUtils.isNotEmpty(eosNamelist) && eosNamelist.size() > 1) {
                //如果当前有多个eos账户
                ivChangeAccount.setVisibility(View.VISIBLE);
                ivChangeAccount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showChangeEOSNameDialog();
                    }
                });
                tvCurrentAccount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showChangeEOSNameDialog();
                    }
                });
            } else {
                //只有一个eos账户
                ivChangeAccount.setVisibility(View.GONE);
            }

        } else {
            ivChangeAccount.setVisibility(View.GONE);
        }


        //初始化总资产法币估值显示
        switch (SPUtils.getInstance().getInt("currency_unit")) {
            case CacheConstants.CURRENCY_CNY:
                tvCurrencyType.setText("≈ ¥ ");
                totalCNYAmount.setText(" -- ");
                break;
            case CacheConstants.CURRENCY_USD:
                tvCurrencyType.setText("≈ $ ");
                totalCNYAmount.setText(" -- ");
                break;
            default:
                tvCurrencyType.setText("≈ ¥ ");
                totalCNYAmount.setText(" -- ");
        }
    }

    @Override
    protected void setNavibarTitle(String title, boolean isShowBack) {
        super.setNavibarTitle(title, isShowBack);
        ImageView mCollectView = (ImageView) mTitleBar.addAction(new TitleBar.ImageAction(R.drawable.ic_add_wallet) {
            @Override
            public void performAction(View view) {
                UISkipMananger.launchWalletManagement(EosHomeActivity.this);
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.eos_fragment_wallet;
    }

    @Override
    public EosHomePresenter newP() {
        return new EosHomePresenter();
    }

    @Override
    public void onStart() {
        super.onStart();
        curEosWallet = DBManager.getInstance().getMultiWalletEntityDao().getCurrentMultiWalletEntity()
                .getEosWalletEntities().get(0);
        if (EmptyUtils.isNotEmpty(curEosWallet)) {
            tvAccountName.setText(curEosWallet.getCurrentEosName());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SmartScheduler jobScheduler = SmartScheduler.getInstance(EosHomeActivity.this.getApplicationContext());
        boolean result = jobScheduler.removeJob(2);
        jobScheduler.removeJob(1);
        if (result) {
            LoggerManager.d("Job repeat Removed");
        }

        dissmisProgressDialog();

    }

    public String getAssetsValue() {
        return totalCNYAmount.getText().toString().trim();
    }

    public String getEosAmount() {
        return tvEosAmount.getText().toString().trim();
    }

    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    private void showChangeEOSNameDialog() {
        int[] listenedItems = {R.id.imv_close};

        CustomFullDialog dialog = new CustomFullDialog(this,
                R.layout.eos_dialog_change_account, listenedItems, false, Gravity.BOTTOM);

        dialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
            @Override
            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.imv_close:
                        dialog.cancel();
                        break;
                    default:
                        break;
                }
            }
        });
        dialog.show();

        RecyclerView mRecyclerView = dialog.findViewById(R.id.rv_list);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);
        List<EOSNameVO> voList = getP().getEOSNameVOList();
        ChangeAccountAdapter adapter = new ChangeAccountAdapter(voList);
        mRecyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (EmptyUtils.isNotEmpty(voList)) {
                    for (int i = 0; i < voList.size(); i++) {
                        EOSNameVO vo = voList.get(i);
                        vo.isChecked = i == position;
                    }

                    adapter.notifyDataSetChanged();
                    getP().saveNewEntity(voList.get(position).getEosName());
                    tvAccountName.setText(voList.get(position).getEosName());
                    tvCurrentAccount.setText(voList.get(position).getEosName());
                    EventBusProvider.postSticky(new ChangeAccountEvent());

                    dialog.cancel();

                }
            }
        });

    }

    /**
     * 展示Tokens
     */
    public void showTokens(List<EosTokenVO> eosTokens) {

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager
                .VERTICAL, false);
        recyclerTokenList.setLayoutManager(layoutManager);
        mAdapter = new EosTokensAdapter(eosTokens);
        recyclerTokenList.setAdapter(mAdapter);
        //点击事件
        recyclerTokenList.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                EosTokenVO curToken = eosTokens.get(position);

                Bundle bundle = new Bundle();
                bundle.putInt(ParamConstants.COIN_TYPE, ParamConstants.COIN_TYPE_TOKENS);
                bundle.putParcelable(ParamConstants.EOS_TOKENS, curToken);

                UISkipMananger.launchAssetDetail(EosHomeActivity.this, bundle);
            }
        });

        viewEosTokens.setVisibility(View.VISIBLE);
        tvNumberOfTokens.setText(String.valueOf(eosTokens.size()));
    }
}
