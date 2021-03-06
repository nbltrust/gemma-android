package com.cybex.gma.client.ui.fragment;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.allen.library.SuperTextView;
import com.cybex.componentservice.api.ApiPath;
import com.cybex.componentservice.db.entity.EosWalletEntity;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.ui.activity.CommonWebViewActivity;
import com.cybex.gma.client.ui.model.response.TransferHistory;
import com.cybex.gma.client.utils.ClipboardUtils;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.mvp.lite.XPresenter;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class TransferRecordDetailFragment extends XFragment {


    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.tv_show_transfer_id) TextView tvShowTransferId;
    @BindView(R.id.tv_memo) TextView tvMemo;
    @BindView(R.id.tv_show_memo) TextView tvShowMemo;
    @BindView(R.id.tv_transfer_id) TextView tvTransferId;
    private TransferHistory curTransfer;
    private MultiWalletEntity curWallet;
    private EosWalletEntity curEosWallet;
    private String curEosName;
    @BindView(R.id.arrow) ImageView arrow;
    @BindView(R.id.tv_income_or_out) TextView tvIncomeOrOut;
    @BindView(R.id.tv_amount) TextView tvAmount;
    @BindView(R.id.brief_info) ConstraintLayout briefInfo;
    @BindView(R.id.superTextView_receiver) SuperTextView superTextViewReceiver;
    @BindView(R.id.superTextView_blockTime) SuperTextView superTextViewBlockTime;
    @BindView(R.id.superTextView_transfer_status) SuperTextView superTextViewTransferStatus;

    @BindView(R.id.layout_detail_1) LinearLayout layoutDetail1;
    //@BindView(R.id.superTextView_blockId) SuperTextView superTextViewBlockId;
    @BindView(R.id.layout_detail_2) LinearLayout layoutDetail2;
    @BindView(R.id.tv_see_in_explorer) TextView tvSeeInExplorer;
    @BindView(R.id.view_scroll) ScrollView mScrollView;

    Unbinder unbinder;
    @OnClick(R.id.tv_see_in_explorer)
    public void seeInExplorer(){
        if (EmptyUtils.isNotEmpty(curTransfer)){
            final String url = ApiPath.URL_BLOCK_CHAIN_BROWSER + curTransfer.trx_id;
            CommonWebViewActivity.startWebView(getActivity(), url, getString(R.string.eos_tip_transfer_detail));
        }
    }

    /**
     * 交易状态：1：未确认 2：正在确认 3：已确认 4: 交易失败
     */

    public static TransferRecordDetailFragment newInstance(Bundle args) {
        TransferRecordDetailFragment fragment = new TransferRecordDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public boolean onBackPressedSupport() {
        return super.onBackPressedSupport();
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
        setNavibarTitle(getResources().getString(R.string.eos_title_transfer_detail), true, true);
        OverScrollDecoratorHelper.setUpOverScroll(mScrollView);
    }

    @Override
    public void initData(Bundle savedInstanceState) {

        //判断此交易的类型
        curWallet = DBManager.getInstance().getMultiWalletEntityDao().getCurrentMultiWalletEntity();
        if (curWallet != null && curWallet.getEosWalletEntities().size() > 0){

            curEosWallet = curWallet.getEosWalletEntities().get(0);
            Bundle bd = getArguments();
            if (bd != null) {
                curTransfer = getArguments().getParcelable(ParamConstants.KEY_CUR_TRANSFER);
                if (!EmptyUtils.isEmpty(curWallet) && EmptyUtils.isNotEmpty(curEosWallet)) {
                    curEosName = curEosWallet.getCurrentEosName();
                    //设置收入&支出页面不同的值(箭头，加减号，收入/支出)
                    if (curTransfer.sender.equals(curEosName)) {
                        //转出操作
                        arrow.setImageResource(R.drawable.ic_tab_pay);
                        tvAmount.setText(
                                String.format(getResources().getString(R.string.eos_amount_outcome), curTransfer
                                        .quantity.split(" ")[0]));
                        tvIncomeOrOut.setText(getResources().getString(R.string.eos_tip_payment));
                        superTextViewReceiver.setLeftString(getResources().getString(R.string.eos_title_receiver));
                        superTextViewReceiver.setRightString(curTransfer.receiver);
                    } else {
                        //收入操作
                        tvAmount.setText(
                                String.format(getResources().getString(R.string.eos_amount_income), curTransfer.quantity
                                        .split(" ")[0]));
                        tvIncomeOrOut.setText(getResources().getString(R.string.eos_tip_income));
                        arrow.setImageResource(R.drawable.ic_tab_income);
                        superTextViewReceiver.setLeftString(getResources().getString(R.string.eos_title_payer));
                        superTextViewReceiver.setRightString(curTransfer.sender);

                    }
                    //设置固定的值
                    String time_arr[] = curTransfer.timestamp.split("T");
                    String transferTime = time_arr[0] + " " + time_arr[1].substring(0,8);
                    superTextViewBlockTime.setRightString(transferTime);

                    tvShowMemo.setText(curTransfer.memo);
                    tvShowMemo.getViewTreeObserver().addOnGlobalLayoutListener(
                            new ViewTreeObserver.OnGlobalLayoutListener() {
                                @Override
                                public void onGlobalLayout() {
                                    int lineCount = tvShowMemo.getLineCount();
                                    if (lineCount > 1){
                                        tvShowMemo.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                                    }else {
                                        tvShowMemo.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                                    }
                                    tvShowMemo.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                }
                            });
                    //superTextViewBlockId.setRightString(String.valueOf(curTransfer.block));
                    superTextViewTransferStatus.setRightString(curTransfer.status);
                    tvShowTransferId.setText(curTransfer.trx_id);

                    //其他样式
                    tvSeeInExplorer.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG);
                }
            }
        }
    }

    @OnClick(R.id.tv_show_transfer_id)
    public void executeCopy(View v) {
        String text = String.valueOf(tvShowTransferId.getText());
        if (EmptyUtils.isNotEmpty(text)) {
            ClipboardUtils.copyText(getActivity(), text);
            GemmaToastUtils.showShortToast(getString(R.string.eos_copy_success));
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.eos_fragment_transfer_record_detail;
    }

    @Override
    public XPresenter newP() {
        return null;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void setTransferStatus(int status){
        switch (status){
            case ParamConstants.STATUS_NOT_CONFIRMED:
                superTextViewTransferStatus.setRightString(getResources().getString(R.string.status_un_confirmed));
                break;
            case ParamConstants.STATUS_CONFIRMING:
                superTextViewTransferStatus.setRightString(getResources().getString(R.string.status_confirmed_ing));
                break;
            case ParamConstants.STATUS_CONFIRMED:
                superTextViewTransferStatus.setRightString(getResources().getString(R.string.status_confirmed_ok));
                break;
            case ParamConstants.STATUS_FAIL:
                superTextViewTransferStatus.setRightString(getResources().getString(R.string.status_trade_failed));
                break;
        }
    }
}
