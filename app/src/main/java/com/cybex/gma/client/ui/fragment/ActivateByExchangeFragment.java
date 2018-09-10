package com.cybex.gma.client.ui.fragment;

import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cybex.gma.client.R;
import com.cybex.gma.client.api.ApiPath;
import com.cybex.gma.client.ui.base.CommonWebViewActivity;
import com.hxlx.core.lib.mvp.lite.XFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ActivateByExchangeFragment extends XFragment {

    Unbinder unbinder;
    @BindView(R.id.tv_show_priKey_area) TextView tvShowPriKeyArea;
    @BindView(R.id.bt_click_to_copy_priKey) Button btClickToCopyPriKey;
    @BindView(R.id.tv_tip_mid_exchange_part_one) TextView tvTipMidExchangePartOne;
    @BindView(R.id.tv_tip_mid_exchange_part_two) TextView tvTipMidExchangePartTwo;
    @BindView(R.id.tv_tip_mid_exchange_part_three) TextView tvTipMidExchangePartThree;
    @BindView(R.id.tv_show_memo_area) TextView tvShowMemoArea;
    @BindView(R.id.bt_click_to_copy_memo) Button btClickToCopyMemo;
    @BindView(R.id.tv_hint_activate_by_exchange_bot) TextView tvHintActivateByExchangeBot;


    @OnClick(R.id.tv_tip_mid_exchange_part_two)
    public void goToDapp(){
        CommonWebViewActivity.startWebView(getActivity(), ApiPath.DAPP_SINGUP_EOS, getString(R.string.activate_account));
    }

    public static ActivateByExchangeFragment newInstance() {
        Bundle args = new Bundle();
        ActivateByExchangeFragment fragment = new ActivateByExchangeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        tvHintActivateByExchangeBot.setText(Html.fromHtml(getString(R.string.tip_use_eos_to_activate)));
        tvTipMidExchangePartOne.setText(getString(R.string.tip_activate_by_exchange_part_one));
        tvTipMidExchangePartTwo.setText(getString(R.string.tip_activate_by_exchange_part_two));
        tvTipMidExchangePartTwo.setTextColor(getResources().getColor(R.color.cornflowerBlue));
        tvTipMidExchangePartTwo.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG);
        tvTipMidExchangePartThree.setText(Html.fromHtml(getString(R.string.tip_activate_by_exchange_part_three)));

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_activate_by_exchange;
    }

    @Override
    public Object newP() {
        return null;
    }
}
