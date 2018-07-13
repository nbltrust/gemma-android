package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.allen.library.SuperTextView;
import com.cybex.gma.client.R;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.mvp.lite.XPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class TransferRecordDetailFragment extends XFragment {


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

    public static TransferRecordDetailFragment newInstance() {
        Bundle args = new Bundle();
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
