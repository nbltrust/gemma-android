package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.allen.library.SuperTextView;
import com.cybex.gma.client.R;
import com.cybex.gma.client.ui.UISkipMananger;
import com.cybex.gma.client.ui.activity.TransferRecordActivity;
import com.cybex.gma.client.ui.presenter.WalletPresenter;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 钱包Fragment
 *
 * Created by wanglin on 2018/7/9.
 */
public class WalletFragment extends XFragment<WalletPresenter> {

    @BindView(R.id.superTextView_card_resource) SuperTextView superTextViewCardResource;
    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.imageView2) ImageView imageView2;
    @BindView(R.id.textView2) TextView textView2;
    @BindView(R.id.textView_total_assets) TextView textViewTotalAssets;
    @BindView(R.id.superTextView_total_assets) SuperTextView superTextViewTotalAssets;
    @BindView(R.id.balance) SuperTextView balance;
    @BindView(R.id.dash_line_vertical) View dashLineVertical;
    @BindView(R.id.superTextView_redeem) SuperTextView superTextViewRedeem;
    @BindView(R.id.tv_remain_time) TextView tvRemainTime;
    @BindView(R.id.superTextView_cpu) SuperTextView superTextViewCpu;
    @BindView(R.id.superTextView_net) SuperTextView superTextViewNet;
    @BindView(R.id.superTextView_ram) SuperTextView superTextViewRam;
    @BindView(R.id.show_cpu) LinearLayout showCpu;
    @BindView(R.id.layout_info) ConstraintLayout layoutInfo;
    @BindView(R.id.superTextView_card_record) SuperTextView superTextViewCardRecord;
    Unbinder unbinder;

    public static WalletFragment newInstance() {
        Bundle args = new Bundle();
        WalletFragment fragment = new WalletFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
        superTextViewCardRecord.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                UISkipMananger.launchIntent(getActivity(), TransferRecordActivity.class);
            }
        });
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setNavibarTitle("GEMMA", false);
    }


    @Override
    public int getLayoutId() {
        return R.layout.fragment_wallet;
    }

    @Override
    public WalletPresenter newP() {
        return new WalletPresenter();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
