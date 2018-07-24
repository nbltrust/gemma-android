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
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.presenter.WalletPresenter;
import com.cybex.gma.client.utils.encryptation.EncryptationManager;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.pixplicity.sharp.Sharp;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import jdenticon.AvatarHelper;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

/**
 * 钱包Fragment
 *
 * Created by wanglin on 2018/7/9.
 */
public class WalletFragment extends XFragment<WalletPresenter> {

    private final String testUsername = "hellobitcoin";

    @BindView(R.id.tv_backup_wallet) TextView textViewBackupWallet;
    @BindView(R.id.superTextView_total_assets) SuperTextView superTextViewTotalAssets;
    @BindView(R.id.total_EOS_amount) TextView totalEOSAmount;
    @BindView(R.id.total_CNY_amount) SuperTextView totalCNYAmount;
    @BindView(R.id.balance) SuperTextView balance;
    @BindView(R.id.redeem) SuperTextView redeem;
    @BindView(R.id.layout_top_info) LinearLayout layoutTopInfo;
    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.imageView_portrait) ImageView imageViewPortrait;
    @BindView(R.id.textView_username) TextView textViewUsername;
    @BindView(R.id.superTextView_cpu) SuperTextView superTextViewCpu;
    @BindView(R.id.superTextView_net) SuperTextView superTextViewNet;
    @BindView(R.id.superTextView_ram) SuperTextView superTextViewRam;
    @BindView(R.id.show_cpu) LinearLayout showCpu;
    @BindView(R.id.layout_info) ConstraintLayout layoutInfo;
    @BindView(R.id.superTextView_card_record) SuperTextView superTextViewCardRecord;
    @BindView(R.id.superTextView_card_delegate) SuperTextView superTextViewCardDelegate;
    @BindView(R.id.scroll_wallet_tab) ScrollView scrollViewWalletTab;
    Unbinder unbinder;

    @OnClick(R.id.tv_backup_wallet)
    public void backUpWallet(){
        UISkipMananger.launchBakupGuide(getActivity());
    }

    @OnClick(R.id.superTextView_card_record)
    public void goToSeeRecord() {
        UISkipMananger.launchTransferRecord(getActivity());
    }

    @OnClick(R.id.superTextView_card_delegate)
    public void goToDelegate(){
        UISkipMananger.launchDelegate(getActivity());
    }

    public static WalletFragment newInstance() {
        Bundle args = new Bundle();
        WalletFragment fragment = new WalletFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        generatePotrait(testUsername);
        setNavibarTitle("GEMMA", false);
        OverScrollDecoratorHelper.setUpOverScroll(scrollViewWalletTab);
    }

    @Override
    protected void setNavibarTitle(String title, boolean isShowBack) {
        super.setNavibarTitle(title, isShowBack);
        ImageView mCollectView = (ImageView)mTitleBar.addAction(new TitleBar.ImageAction(R.drawable.wallet_add3x) {
            @Override
            public void performAction(View view) {
                UISkipMananger.launchWalletManagement(getActivity());
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_wallet;
    }

    @Override
    public WalletPresenter newP() {
        return new WalletPresenter();
    }

    public void generatePotrait(String eosName) {
        String hash = EncryptationManager.getEncrypt().encryptSHA256(eosName);
        final String str = AvatarHelper.Companion.getInstance().getAvatarSvg(hash, 62, null);
        Sharp.loadString(str).into(imageViewPortrait);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
