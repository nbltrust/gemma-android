package com.cybex.walletmanagement.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.cybex.componentservice.bean.CoinType;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.walletmanagement.R;
import com.cybex.walletmanagement.config.WalletManageConst;
import com.cybex.walletmanagement.event.BarcodeScanEvent;
import com.cybex.walletmanagement.event.SelectCoinTypeEvent;
import com.cybex.walletmanagement.ui.adapter.CoinTypeListAdapter;
import com.cybex.walletmanagement.ui.model.CoinTypeBean;
import com.hxlx.core.lib.common.eventbus.BusEvent;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.mvp.lite.XActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import me.framework.fragmentation.anim.DefaultHorizontalAnimator;
import me.framework.fragmentation.anim.FragmentAnimator;


public class SelectWalletCoinTypeActivity extends XActivity {


    private RecyclerView rvCoinType;
    private CoinTypeListAdapter adatper;

    private List<CoinTypeBean> coins=new ArrayList<>();


    @Override
    public void bindUI(View view) {
        rvCoinType = findViewById(R.id.recycleview_cointype);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager
                .VERTICAL, false);
        rvCoinType.setLayoutManager(layoutManager);
        setNavibarTitle(getResources().getString(R.string.walletmanage_coin_type_title), true);

        CoinType selectedCoinType = (CoinType) getIntent().getSerializableExtra(WalletManageConst.KEY_SELECT_COINTYPE);

        LoggerManager.e("czc selectedCoinType="+selectedCoinType.coinName);
        LoggerManager.e("czc CoinType.EOS.equals(selectedCoinType))="+CoinType.EOS.equals(selectedCoinType));

        coins.add(new CoinTypeBean(CoinType.EOS,CoinType.EOS.equals(selectedCoinType)));
        coins.add(new CoinTypeBean(CoinType.ETH,CoinType.ETH.equals(selectedCoinType)));

        adatper = new CoinTypeListAdapter(coins);
        rvCoinType.setAdapter(adatper);
        adatper.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                for (CoinTypeBean coin : coins) {
                    coin.isChecked=false;
                }
                coins.get(position).isChecked=true;
                adatper.notifyDataSetChanged();
                EventBusProvider.post(new SelectCoinTypeEvent(coins.get(position).cointype));
            }
        });
    }

    @Override
    public void initData(Bundle savedInstanceState) {

    }

    @Override
    public int getLayoutId() {
        return R.layout.walletmanage_activity_wallet_coin_type;
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
