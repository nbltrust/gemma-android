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

public class ActivateByFriendFragment extends XFragment {

    Unbinder unbinder;
    @BindView(R.id.tv_hint_activate_by_friend_top) TextView tvHintActivateByFriendTop;
    @BindView(R.id.tv_show_priKey_area) TextView tvShowPriKeyArea;
    @BindView(R.id.bt_click_to_copy_priKey) Button btClickToCopyPriKey;
    @BindView(R.id.tv_tip_mid_friend_part_one) TextView tvTipMidFriendPartOne;
    @BindView(R.id.tv_tip_mid_friend_part_two) TextView tvTipMidFriendPartTwo;
    @BindView(R.id.tv_tip_mid_friend_part_three) TextView tvTipMidFriendPartThree;
    @BindView(R.id.tv_show_memo_area) TextView tvShowMemoArea;
    @BindView(R.id.bt_click_to_copy_memo) Button btClickToCopyMemo;
    @BindView(R.id.tv_hint_activate_by_friend_bot) TextView tvHintActivateByFriendBot;

    @OnClick(R.id.tv_tip_mid_friend_part_two)
    public void goToDapp(){
        CommonWebViewActivity.startWebView(getActivity(), ApiPath.DAPP_SINGUP_EOS, getString(R.string.activate_account));
    }

    public static ActivateByFriendFragment newInstance() {
        Bundle args = new Bundle();
        ActivateByFriendFragment fragment = new ActivateByFriendFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        tvHintActivateByFriendBot.setText(Html.fromHtml(getResources().getString(R.string.tip_use_eos_to_activate)));

        tvTipMidFriendPartOne.setText(getString(R.string.tip_activate_by_exchange_part_one));
        tvTipMidFriendPartTwo.setText(getString(R.string.tip_activate_by_exchange_part_two));
        tvTipMidFriendPartTwo.setTextColor(getResources().getColor(R.color.cornflowerBlue));
        tvTipMidFriendPartTwo.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG);
        tvTipMidFriendPartThree.setText(Html.fromHtml(getString(R.string.tip_activate_by_exchange_part_three)));
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_activate_by_friend;
    }

    @Override
    public Object newP() {
        return null;
    }
}
