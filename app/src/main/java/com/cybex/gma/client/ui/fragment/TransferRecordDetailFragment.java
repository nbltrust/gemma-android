package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.allen.library.SuperTextView;
import com.cybex.gma.client.R;
import com.cybex.gma.client.db.entity.WalletEntity;
import com.cybex.gma.client.manager.DBManager;
import com.cybex.gma.client.ui.model.response.TransferHistory;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.mvp.lite.XPresenter;
import com.hxlx.core.lib.utils.EmptyUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class TransferRecordDetailFragment extends XFragment {

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
    @BindView(R.id.tv_memo) TextView tvMemo;
    @BindView(R.id.layout_detail_1) LinearLayout layoutDetail1;
    @BindView(R.id.superTextView_transferId) SuperTextView superTextViewTransferId;
    @BindView(R.id.superTextView_blockId) SuperTextView superTextViewBlockId;
    @BindView(R.id.layout_detail_2) LinearLayout layoutDetail2;
    @BindView(R.id.tv_see_in_explorer) TextView tvSeeInExplorer;
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
        setNavibarTitle(getString(R.string.title_transfer_record), true);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setNavibarTitle("收支记录", true, false);
        //判断此交易的类型
        curWallet = DBManager.getInstance().getWalletEntityDao().getCurrentWalletEntity();
        if(getArguments() != null){
            curTransfer = getArguments().getParcelable("curTransfer");
            if (!EmptyUtils.isEmpty(curWallet)){
                curEosName = curWallet.getCurrentEosName();
                //设置收入&支出页面不同的值(箭头，加减号，收入/支出)
                if (curTransfer.from.equals(curEosName)){
                    //转出操作
                    arrow.setBackground(getResources().getDrawable(R.drawable.ic_tab_pay_white));
                    tvAmount.setText(String.format(getResources().getString(R.string.payment_amount), curTransfer.value));
                    tvIncomeOrOut.setText(getResources().getString(R.string.payment));
                    superTextViewReceiver.setLeftString(getResources().getString(R.string.receiver));
                }else{
                    //收入操作
                    tvAmount.setText(String.format(getResources().getString(R.string.income_amount), curTransfer.value));
                    tvIncomeOrOut.setText(getResources().getString(R.string.income));
                    arrow.setBackground(getResources().getDrawable(R.drawable.ic_income_white));
                    superTextViewReceiver.setLeftString(getResources().getString(R.string.payer));
                }
                //设置固定的值
                superTextViewBlockTime.setRightString(curTransfer.time);
                tvMemo.setText(curTransfer.memo);
                superTextViewBlockId.setRightString(String.valueOf(curTransfer.block));
                superTextViewTransferStatus.setRightString(String.valueOf(curTransfer.status));
                superTextViewTransferId.setLeftString(curTransfer.hash);
            }
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
