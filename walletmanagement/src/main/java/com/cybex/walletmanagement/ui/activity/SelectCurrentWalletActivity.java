package com.cybex.walletmanagement.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.cybex.componentservice.bean.CoinType;
import com.cybex.componentservice.config.BaseConst;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.event.RefreshCurrentWalletEvent;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.walletmanagement.R;
import com.cybex.walletmanagement.config.WalletManageConst;
import com.cybex.walletmanagement.event.SelectCoinTypeEvent;
import com.cybex.walletmanagement.event.SelectImportWhichWalletEvent;
import com.cybex.walletmanagement.ui.adapter.CoinTypeListAdapter;
import com.cybex.walletmanagement.ui.adapter.ImportWalletListAdapter;
import com.cybex.walletmanagement.ui.adapter.SelectCurrentWalletAdapter;
import com.cybex.walletmanagement.ui.model.CoinTypeBean;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.mvp.lite.XActivity;

import java.util.ArrayList;
import java.util.List;

import me.framework.fragmentation.anim.DefaultHorizontalAnimator;
import me.framework.fragmentation.anim.FragmentAnimator;


public class SelectCurrentWalletActivity extends XActivity {


    private RecyclerView rvWalletList;
    private SelectCurrentWalletAdapter adatper;
    private MultiWalletEntity checkedWallet;

    private List<MultiWalletEntity> wallets=new ArrayList<>();


    @Override
    public void bindUI(View view) {
        rvWalletList = findViewById(R.id.rv_wallets);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager
                .VERTICAL, false);
        rvWalletList.setLayoutManager(layoutManager);
        setNavibarTitle(getResources().getString(R.string.walletmanage_select_wallet_title), true);

        List<MultiWalletEntity> allWallets = DBManager.getInstance().getMultiWalletEntityDao().getMultiWalletEntityList();
        wallets.addAll(allWallets);

        for (MultiWalletEntity wallet : wallets) {
            if(wallet.getIsCurrentWallet()!=0){
                wallet.setChecked(true);
                checkedWallet=wallet;
                break;
            }
        }

        adatper = new SelectCurrentWalletAdapter(wallets);
        rvWalletList.setAdapter(adatper);
        adatper.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

                if(view.getId()==R.id.iv_tip){
                    MultiWalletEntity wallet = wallets.get(position);

                    int walletType = wallet.getWalletType();
                    if(walletType==MultiWalletEntity.WALLET_TYPE_HARDWARE){


                    }
//                    else if(walletType==MultiWalletEntity.WALLET_TYPE_MNEMONIC){
//                        Intent intent = new Intent(context, WalletManageInnerActivity.class);
//                        intent.putExtra(BaseConst.KEY_WALLET_ENTITY,wallet);
//                        startActivity(intent);
//                    }
                    else{
                        Intent intent = new Intent(context, WalletManageInnerActivity.class);
                        intent.putExtra(BaseConst.KEY_WALLET_ENTITY,wallet);
                        startActivity(intent);
                    }


                }else if(view.getId()==R.id.rootview_wallet){
                    for (MultiWalletEntity wallet : wallets) {
                        wallet.setChecked(false);
                    }
                    wallets.get(position).setChecked(true);
                    adatper.notifyDataSetChanged();
                    EventBusProvider.post(new SelectImportWhichWalletEvent(wallets.get(position)));
                }

            }
        });

    }

    @Override
    public void initData(Bundle savedInstanceState) {

    }

    @Override
    public int getLayoutId() {
        return R.layout.walletmanage_activity_select_current_wallet;
    }

    @Override
    protected void onDestroy() {
        MultiWalletEntity current = DBManager.getInstance().getMultiWalletEntityDao().getCurrentMultiWalletEntity();
        if(checkedWallet!=null&&current!=null&&checkedWallet.getId()!=current.getId()){

            current.setIsCurrentWallet(0);
            checkedWallet.setIsCurrentWallet(1);
            current.save();
            checkedWallet.save();
            EventBusProvider.post(new RefreshCurrentWalletEvent(checkedWallet));
        }
        super.onDestroy();
    }

    @Override
    public Object newP() {
        return null;
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        // 设置横向(和安卓4.x动画相同)
        return new DefaultHorizontalAnimator();
    }
}
