package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.allen.library.SuperTextView;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.db.entity.WalletEntity;
import com.cybex.gma.client.manager.DBManager;
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
    private WalletEntity curWallet;
    private String curEosName;
    @BindView(R.id.arrow) ImageView arrow;
    @BindView(R.id.tv_income_or_out) TextView tvIncomeOrOut;
    @BindView(R.id.tv_amount) TextView tvAmount;
    @BindView(R.id.brief_info) ConstraintLayout briefInfo;
    @BindView(R.id.superTextView_receiver) SuperTextView superTextViewReceiver;
    @BindView(R.id.superTextView_blockTime) SuperTextView superTextViewBlockTime;
    @BindView(R.id.superTextView_transfer_status) SuperTextView superTextViewTransferStatus;

    @BindView(R.id.layout_detail_1) LinearLayout layoutDetail1;
    @BindView(R.id.superTextView_blockId) SuperTextView superTextViewBlockId;
    @BindView(R.id.layout_detail_2) LinearLayout layoutDetail2;
    @BindView(R.id.tv_see_in_explorer) TextView tvSeeInExplorer;
    @BindView(R.id.view_scroll) ScrollView mScrollView;

    Unbinder unbinder;

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
        setNavibarTitle("收支详情", true, false);
        OverScrollDecoratorHelper.setUpOverScroll(mScrollView);
    }

    @Override
    public void initData(Bundle savedInstanceState) {

        //判断此交易的类型
        curWallet = DBManager.getInstance().getWalletEntityDao().getCurrentWalletEntity();
        Bundle bd = getArguments();
        if (bd != null) {
            curTransfer = getArguments().getParcelable(ParamConstants.KEY_CUR_TRANSFER);
            if (!EmptyUtils.isEmpty(curWallet)) {
                curEosName = curWallet.getCurrentEosName();
                //设置收入&支出页面不同的值(箭头，加减号，收入/支出)
                if (curTransfer.from.equals(curEosName)) {
                    //转出操作
                    arrow.setImageResource(R.drawable.ic_tab_pay_white);
                    tvAmount.setText(
                            String.format(getResources().getString(R.string.payment_amount), curTransfer.value));
                    tvIncomeOrOut.setText(getResources().getString(R.string.payment));
                    superTextViewReceiver.setLeftString(getResources().getString(R.string.receiver));

                    superTextViewReceiver.setRightString(curTransfer.to);
                } else {
                    //收入操作
                    tvAmount.setText(
                            String.format(getResources().getString(R.string.income_amount), curTransfer.value));
                    tvIncomeOrOut.setText(getResources().getString(R.string.income));
                    arrow.setImageResource(R.drawable.ic_income_white);
                    superTextViewReceiver.setLeftString(getResources().getString(R.string.payer));
                    superTextViewReceiver.setRightString(curTransfer.from);

                }
                //设置固定的值
                superTextViewBlockTime.setRightString(curTransfer.time);
                tvShowMemo.setText(curTransfer.memo);
                superTextViewBlockId.setRightString(String.valueOf(curTransfer.block));
                superTextViewTransferStatus.setRightString(String.valueOf(curTransfer.status));
                tvShowTransferId.setText(curTransfer.hash);
            }

        }

    }

    @OnClick(R.id.tv_show_transfer_id)
    public void executeCopy(View v) {
        String text = String.valueOf(tvShowTransferId.getText());
        if (EmptyUtils.isNotEmpty(text)) {
            ClipboardUtils.copyText(getActivity(), text);
            GemmaToastUtils.showShortToast("复制成功");
        }

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_transfer_record_detail;
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
}
