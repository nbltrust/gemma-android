package com.cybex.gma.client.ui.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.allen.library.SuperTextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.cybex.base.view.progress.RoundCornerProgressBar;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.CacheConstants;
import com.cybex.gma.client.db.entity.WalletEntity;
import com.cybex.gma.client.event.PollEvent;
import com.cybex.gma.client.event.TabSelectedEvent;
import com.cybex.gma.client.event.WalletIDEvent;
import com.cybex.gma.client.manager.DBManager;
import com.cybex.gma.client.manager.LoggerManager;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.adapter.ChangeAccountAdapter;
import com.cybex.gma.client.ui.model.vo.EOSNameVO;
import com.cybex.gma.client.ui.presenter.WalletPresenter;
import com.cybex.gma.client.utils.encryptation.EncryptationManager;
import com.cybex.gma.client.widget.MyScrollView;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.GsonUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.pixplicity.sharp.Sharp;
import com.siberiadante.customdialoglib.CustomFullDialog;
import com.tapadoo.alerter.Alerter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.hypertrack.smart_scheduler.SmartScheduler;
import jdenticon.AvatarHelper;

/**
 * 钱包Fragment
 *
 * Created by wanglin on 2018/7/9.
 */
public class WalletFragment extends XFragment<WalletPresenter> {

    @BindView(R.id.superTextView_card_vote) SuperTextView superTextViewCardVote;
    @BindView(R.id.superTextView_card_buy_ram) SuperTextView superTextViewCardBuyRam;
    private WalletEntity curWallet;
    private int walletID;
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
    @BindView(R.id.show_cpu) LinearLayout showCpu;
    @BindView(R.id.layout_info) ConstraintLayout layoutInfo;
    @BindView(R.id.superTextView_card_record) SuperTextView superTextViewCardRecord;
    @BindView(R.id.superTextView_card_delegate) SuperTextView superTextViewCardDelegate;
    @BindView(R.id.scroll_wallet_tab) MyScrollView scrollViewWalletTab;
    @BindView(R.id.progressbar_cpu_small) RoundCornerProgressBar progressBarCPU;
    @BindView(R.id.progressbar_net_small) RoundCornerProgressBar progressBarNET;
    @BindView(R.id.progressbar_ram_small) RoundCornerProgressBar progressBarRAM;
    Unbinder unbinder;

    @OnClick({R.id.tv_backup_wallet, R.id.textView_username})
    public void backUpWallet(View v) {
        switch (v.getId()) {
            case R.id.tv_backup_wallet:
                if (!EmptyUtils.isEmpty(curWallet)) {
                    walletID = curWallet.getId();
                    EventBusProvider.postSticky(new WalletIDEvent(walletID));
                    UISkipMananger.launchBakupGuide(getActivity());
                }
                break;
            case R.id.textView_username:
                showChangeEOSNameDialog();
                break;
            default:
                break;
        }


    }

    @Override
    public boolean useEventBus() {
        return true;
    }

    @OnClick(R.id.superTextView_card_record)
    public void goToSeeRecord() {
        UISkipMananger.launchTransferRecord(getActivity());
    }

    @OnClick(R.id.superTextView_card_delegate)
    public void goToDelegate() {
        UISkipMananger.launchDelegate(getActivity());
    }

    @OnClick(R.id.superTextView_card_buy_ram)
    public void goToBuySellRam() {
        UISkipMananger.launchRamTransaction(getActivity());
    }

    @OnClick(R.id.superTextView_card_vote)
    public void goToVote() {
        UISkipMananger.launchVote(getActivity());
    }

    public static WalletFragment newInstance() {
        Bundle args = new Bundle();
        WalletFragment fragment = new WalletFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onReceivePollevent(PollEvent pollEvent) {
        if (EmptyUtils.isNotEmpty(pollEvent) && pollEvent.isDone()) {
            LoggerManager.d("isDone", pollEvent.isDone());
            if (Alerter.isShowing()) {
                Alerter.hide();
                LoggerManager.d("Alert Hide");
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onTabSelctedEvent(TabSelectedEvent event) {
        if (EmptyUtils.isNotEmpty(event) && event.getPosition() == 0) {
            LoggerManager.d("wallet tab selected");
            //

        }

    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        textViewBackupWallet.setVisibility(View.VISIBLE);
        setNavibarTitle("GEMMA", false);
        //OverScrollDecoratorHelper.setUpOverScroll(scrollViewWalletTab);
        curWallet = DBManager.getInstance().getWalletEntityDao().getCurrentWalletEntity();
        if (EmptyUtils.isNotEmpty(curWallet)) {
            textViewUsername.setText(curWallet.getCurrentEosName());
            generatePortrait(curWallet.getCurrentEosName());
            if (curWallet.getIsConfirmLib().equals(CacheConstants.NOT_CONFIRMED) && getActivity() != null) {
                Alerter.create(getActivity())
                        .setText(getResources().getString(R.string.please_confirm_alert))
                        .setBackgroundColorRes(R.color.scarlet)
                        .enableSwipeToDismiss()
                        .enableInfiniteDuration(true)
                        .setTextAppearance(R.style.myAlert)
                        .show();
            }

            String json = curWallet.getEosNameJson();
            List<String> eosNamelist = GsonUtils.parseString2List(json, String.class);
            //TODO
            if (EmptyUtils.isEmpty(eosNamelist)) {
                eosNamelist = new ArrayList<>();
                eosNamelist.add("暂时测试1");
                eosNamelist.add("暂时测试2");
            }
            eosNamelist.add("暂时测试3");

            if (EmptyUtils.isNotEmpty(eosNamelist) && eosNamelist.size() > 1) {
                Drawable drawable = getResources().getDrawable(
                        R.drawable.ic_common_drop_white);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                        drawable.getMinimumHeight());
                textViewUsername.setCompoundDrawables(null, null, drawable, null);

                textViewUsername.setClickable(true);
            } else {
                textViewUsername.setCompoundDrawables(null, null,
                        null, null);
                textViewUsername.setClickable(false);

            }

        } else {
            textViewUsername.setCompoundDrawables(null, null,
                    null, null);

            textViewUsername.setClickable(false);
        }

        LoggerManager.d(isCurWalletBackUp());
        if (isCurWalletBackUp()) {
            textViewBackupWallet.setVisibility(View.GONE);
        }

    }

    @Override
    protected void setNavibarTitle(String title, boolean isShowBack) {
        super.setNavibarTitle(title, isShowBack);
        ImageView mCollectView = (ImageView) mTitleBar.addAction(new TitleBar.ImageAction(R.drawable.ic_add_wallet) {
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

    public void generatePortrait(String eosName) {
        String hash = EncryptationManager.getEncrypt().encryptSHA256(eosName);
        final String str = AvatarHelper.Companion.getInstance().getAvatarSvg(hash, 62, null);
        Sharp.loadString(str).into(imageViewPortrait);
    }

    @Override
    public void onStart() {
        super.onStart();
        curWallet = DBManager.getInstance().getWalletEntityDao().getCurrentWalletEntity();
        if (EmptyUtils.isNotEmpty(curWallet)) {
            textViewUsername.setText(curWallet.getCurrentEosName());
            generatePortrait(curWallet.getCurrentEosName());
            if (curWallet.getIsConfirmLib().equals(CacheConstants.NOT_CONFIRMED) && getActivity() != null) {
                Alerter.create(getActivity())
                        .setText(getResources().getString(R.string.please_confirm_alert))
                        .setBackgroundColorRes(R.color.scarlet)
                        .enableSwipeToDismiss()
                        .enableInfiniteDuration(true)
                        .setTextAppearance(R.style.myAlert)
                        .show();
            }

            if (isCurWalletBackUp()) {
                textViewBackupWallet.setVisibility(View.GONE);
            } else {
                textViewBackupWallet.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        SmartScheduler jobScheduler = SmartScheduler.getInstance(getActivity().getApplicationContext());
        boolean reslut = jobScheduler.removeJob(2);
        jobScheduler.removeJob(1);
        if (reslut) {
            LoggerManager.d("Job repeat Removed");
        }

    }

    /**
     * 判断当前钱包是否已经备份过
     *
     * @param
     * @return
     */
    public boolean isCurWalletBackUp() {
        if (!EmptyUtils.isEmpty(curWallet)
                && curWallet.getIsBackUp().equals(CacheConstants.ALREADY_BACKUP)) {
            return true;
        }
        return false;
    }

    /**
     * 将获取到的信息填入页面中相应控件
     *
     * @param wallet
     */
    public void setCurWalletData(WalletEntity wallet) {
        String walletName = wallet.getWalletName();
    }


    private void showChangeEOSNameDialog() {
        int[] listenedItems = {R.id.imv_close};

        CustomFullDialog dialog = new CustomFullDialog(getContext(),
                R.layout.dialog_change_account, listenedItems, false, Gravity.BOTTOM);

        dialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
            @Override
            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.imv_close:
                        dialog.cancel();
                        break;
                    default:
                        break;
                }
            }
        });
        dialog.show();

        RecyclerView mRecyclerView = dialog.findViewById(R.id.rv_list);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(manager);
        List<EOSNameVO> voList = getP().getEOSNameVOList();
        ChangeAccountAdapter adapter = new ChangeAccountAdapter(voList);
        mRecyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (EmptyUtils.isNotEmpty(voList)) {
                    for (int i = 0; i < voList.size(); i++) {
                        EOSNameVO vo = voList.get(i);
                        if (i == position) {
                            vo.isChecked = true;
                        } else {
                            vo.isChecked = false;
                        }
                    }

                    adapter.notifyDataSetChanged();
                    getP().saveNewEntity(voList.get(position).getEosName());

                    dialog.cancel();

                }
            }
        });

    }


}
