package com.cybex.gma.client.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.allen.library.SuperTextView;
import com.cybex.base.view.progress.RoundCornerProgressBar;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.model.vo.ResourceInfoVO;
import com.cybex.gma.client.ui.presenter.ResourceDetailPresenter;
import com.cybex.componentservice.utils.AmountUtil;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ResourceDetailActivity extends XActivity<ResourceDetailPresenter> {

    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.superTextView_cpu_amount) SuperTextView superTextViewCpuAmount;
    @BindView(R.id.progressbar_cpu) RoundCornerProgressBar progressbarCpu;
    @BindView(R.id.superTextView_cpu_status) SuperTextView superTextViewCpuStatus;
    @BindView(R.id.superTextView_net_amount) SuperTextView superTextViewNetAmount;
    @BindView(R.id.progressbar_net) RoundCornerProgressBar progressbarNet;
    @BindView(R.id.superTextView_net_status) SuperTextView superTextViewNetStatus;
    @BindView(R.id.superTextView_ram_amount) SuperTextView superTextViewRamAmount;
    @BindView(R.id.progressbar_ram) RoundCornerProgressBar progressbarRam;
    @BindView(R.id.superTextView_ram_status) SuperTextView superTextViewRamStatus;
    @BindView(R.id.superTV_delegate) SuperTextView superTVDelegate;
    @BindView(R.id.superTV_buy_sell_ram) SuperTextView superTVBuySellRam;
    @BindView(R.id.view_resource_manage_area) LinearLayout viewResourceManageArea;
    private Bundle bundle;
    private ResourceInfoVO resourceInfoVO;
    private String kbPerEOS;
    private String eosPerKb;

    public void setRamUnitPriceKB(String ramUnitPriceKB) {
        this.ramUnitPriceKB = ramUnitPriceKB;
        bundle.putString("ramUnitPriceKB", ramUnitPriceKB);
    }

    private String ramUnitPriceKB;

    @OnClick({R.id.superTV_delegate, R.id.superTV_buy_sell_ram})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.superTV_delegate:
                UISkipMananger.launchDelegate(this, bundle);
                break;
            case R.id.superTV_buy_sell_ram:
                UISkipMananger.launchRamTransaction(this, bundle);
                break;
        }
    }

    @Override
    public void bindUI(View rootView) {
        ButterKnife.bind(this);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        bundle = getIntent().getExtras();
        setNavibarTitle(getString(R.string.eos_title_resource_detail), true);
        getP().getRamMarketInfo();
        if (bundle != null){
            resourceInfoVO = bundle.getParcelable("resourceInfo");
            if (resourceInfoVO != null){
                //cpu总量及已用显示
                String cpuUsed = AmountUtil.div(String.valueOf(resourceInfoVO.getCpuUsed()), "1000", 2);
                String cpuTotal = AmountUtil.div(String.valueOf(resourceInfoVO.getCpuTotal()), "1000", 2);
                String cpuWeight = AmountUtil.round(String.valueOf(resourceInfoVO.getCpuWeight()), 4);
                String cpuAmount = AmountUtil.div(cpuWeight, "10000", 4);
                superTextViewCpuStatus.setLeftString(String.format(getResources().getString(R.string.eos_amount_cpu_used),
                        cpuUsed));
                superTextViewCpuStatus.setRightString(String.format(getResources().getString(R.string.eos_amount_cpu_total), cpuTotal));
                superTextViewCpuAmount.setRightString(cpuAmount + " EOS");
                //CPU Progress
                initProgressBar(progressbarCpu, resourceInfoVO.getCpuProgress());
                //NET 总量及已用显示
                String netUsed = AmountUtil.div(String.valueOf(resourceInfoVO.getNetUsed()), "1024", 2);
                String netTotal = AmountUtil.div(String.valueOf(resourceInfoVO.getNetTotal()), "1024", 2);
                String netWeight = AmountUtil.round(String.valueOf(resourceInfoVO.getNetWeight()), 4);
                String netAmount = AmountUtil.div(netWeight, "10000", 4);
                superTextViewNetStatus.setLeftString(String.format(getResources().getString(R.string.eos_amount_net_used),
                        netUsed));
                superTextViewNetStatus.setRightString(String.format(getResources().getString(R.string.eos_amount_net_total),
                        netTotal));
                superTextViewNetAmount.setRightString(netAmount + " EOS");
                //Net Progress
                initProgressBar(progressbarNet, resourceInfoVO.getNetProgress());

                //Ram总量及已用显示
                String ramUsed = AmountUtil.round(String.valueOf(resourceInfoVO.getRamUsed()), 4);
                String ramUsedKB = AmountUtil.div(ramUsed, "1024", 2);
                superTextViewRamStatus.setLeftString(getResources().getString(R.string.eos_title_used) + ramUsedKB + " KB");
                String ramTotal = AmountUtil.round(String.valueOf(resourceInfoVO.getRamTotal()), 4);
                String ramTotalKB = AmountUtil.div(ramTotal, "1024", 2);
                superTextViewRamStatus.setRightString(
                        getResources().getString(R.string.eos_title_total_amount) + ramTotalKB + " KB");
                //Ram Progress
                initProgressBar(progressbarRam, resourceInfoVO.getRamProgress());
            }
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.eos_activity_resource_detail;
    }

    @Override
    public ResourceDetailPresenter newP() {
        return new ResourceDetailPresenter();
    }

    public void setRamUnitPrice(String ramUnitPriceKB) {
        eosPerKb = ramUnitPriceKB;
        String ramTotalKB = AmountUtil.div(String.valueOf(resourceInfoVO.getRamTotal()), "1024", 2);
        String ramPrice = AmountUtil.mul(ramUnitPriceKB, ramTotalKB, 4);
        superTextViewRamAmount.setRightString(
                String.format(getResources().getString(R.string.eos_amount_ram), ramPrice));
        kbPerEOS = AmountUtil.div("1", ramUnitPriceKB, 8);
        bundle.putString(ParamConstants.RAM_UNIT_PRICE, ramUnitPriceKB);
    }

    /**
     * 初始化ProgressBar样式
     * 85%progress以上要用红色显示
     * @param progress
     */
    public void initProgressBar(RoundCornerProgressBar progressBar, float progress){
        if (progress >= ParamConstants.PROGRESS_MIN){
            if (progress >= ParamConstants.PROGRESS_ALERT && progress <= ParamConstants.PROGRESS_MAX){
                //显示值在85% ～ 100%之间
                progressBar.setProgressColor(getResources().getColor(R.color.scarlet));
                progressBar.setProgress(progress);
                superTextViewNetStatus.setLeftIcon(getResources().getDrawable(R.drawable.ic_dot_red));
            }else if (progress >= ParamConstants.PROGRESS_MAX){
                //progress > 100%
                progressBar.setProgressColor(getResources().getColor(R.color.scarlet));
                progressBar.setProgress(ParamConstants.PROGRESS_MAX);
                superTextViewNetStatus.setLeftIcon(getResources().getDrawable(R.drawable.ic_dot_red));
            }else {
                //progress 0 ~ 85%
                progressBar.setProgressColor(getResources().getColor(R.color.black_context));
                progressBar.setProgress(progress);
                superTextViewNetStatus.setLeftIcon(getResources().getDrawable(R.drawable.ic_dot_black));
            }
        }else {
            //progress < 0
            progressBar.setProgressColor(getResources().getColor(R.color.black_context));
            progressBar.setProgress(ParamConstants.PROGRESS_MIN);
            superTextViewNetStatus.setLeftIcon(getResources().getDrawable(R.drawable.ic_dot_black));
        }
    }
}
