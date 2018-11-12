package com.cybex.gma.client.ui.fragment;

import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cybex.componentservice.api.ApiPath;
import com.cybex.componentservice.db.entity.EosWalletEntity;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.componentservice.ui.activity.CommonWebViewActivity;
import com.cybex.gma.client.utils.ClipboardUtils;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ActivateByFriendFragment extends XFragment {

    Unbinder unbinder;
    @BindView(R.id.tv_tip_mid_friend_part_one) TextView tvTipMidFriendPartOne;
    @BindView(R.id.tv_tip_mid_friend_part_two) TextView tvTipMidFriendPartTwo;
    @BindView(R.id.tv_tip_mid_friend_part_three) TextView tvTipMidFriendPartThree;
    @BindView(R.id.tv_show_memo_area) TextView tvShowMemoArea;
    @BindView(R.id.bt_click_to_copy_memo) Button btClickToCopyMemo;
    @BindView(R.id.tv_hint_activate_by_friend_bot) TextView tvHintActivateByFriendBot;


    @OnClick(R.id.tv_tip_mid_friend_part_two)
    public void goToDapp() {
        CommonWebViewActivity.startWebView(getActivity(), ApiPath.DAPP_SINGUP_EOS,
                getString(R.string.eos_activate_account));
    }

    @OnClick( R.id.bt_click_to_copy_memo)
    public void onCopyClicked(View v) {
        switch (v.getId()) {
            case R.id.bt_click_to_copy_memo:
                if (getContext() != null) {
                    ClipboardUtils.copyText(getContext(), tvShowMemoArea.getText().toString().trim());
                    GemmaToastUtils.showLongToast(getString(R.string.eos_memo_copied));
                }
                break;
        }
    }

    public static ActivateByFriendFragment newInstance(Bundle args) {
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
        //初始化各个textView
        tvHintActivateByFriendBot.setText(
                Html.fromHtml(getResources().getString(R.string.eos_tip_use_eos_to_activate)));
        tvTipMidFriendPartOne.setText(getString(R.string.eos_tip_activate_by_exchange_part_one));
        tvTipMidFriendPartTwo.setText(getString(R.string.eos_tip_activate_by_exchange_part_two));
        tvTipMidFriendPartTwo.setTextColor(getResources().getColor(R.color.highlight));
        tvTipMidFriendPartTwo.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        tvTipMidFriendPartThree.setText(Html.fromHtml(getString(R.string.eos_tip_activate_by_exchange_part_three)));

        if (getArguments() != null) {
            String account_name = getArguments().getString("account_name");

            String memo = account_name + getCurEosPublickey();
            tvShowMemoArea.setText(memo);
        }

    }

    @Override
    public int getLayoutId() {
        return R.layout.eos_fragment_activate_by_friend;
    }

    @Override
    public Object newP() {
        return null;
    }

    public String formatKey(String key) {
        String res = "";
        for (int i = 0; i < key.length(); i++) {
            if (i + 4 < key.length()) {
                res += key.substring(i, i + 4) + " ";
            } else {
                res += key.substring(i, key.length());
            }
            i += 3;
        }
        return res;
    }

    public String getCurEosPublickey(){
        MultiWalletEntity entity = DBManager.getInstance().getMultiWalletEntityDao().getCurrentMultiWalletEntity();
        if (entity != null && entity.getEosWalletEntities().size() > 0){
            EosWalletEntity eosEntity = entity.getEosWalletEntities().get(0);
            if (eosEntity != null){
                return eosEntity.getPublicKey();
            }
        }
        return "";
    }

}
