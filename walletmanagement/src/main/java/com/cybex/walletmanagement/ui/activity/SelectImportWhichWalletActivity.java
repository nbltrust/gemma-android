package com.cybex.walletmanagement.ui.activity;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.walletmanagement.R;
import com.cybex.walletmanagement.config.WalletManageConst;
import com.cybex.walletmanagement.event.SelectImportWhichWalletEvent;
import com.cybex.walletmanagement.ui.adapter.ImportWalletListAdapter;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.mvp.lite.XActivity;

import java.util.ArrayList;
import java.util.List;

import me.framework.fragmentation.anim.DefaultHorizontalAnimator;
import me.framework.fragmentation.anim.FragmentAnimator;


public class SelectImportWhichWalletActivity extends XActivity {


    private RecyclerView rvWalletList;
    private ViewGroup containerNew;
    private ImageView ivNewWallet;
    private ImportWalletListAdapter adatper;

    private List<MultiWalletEntity> wallets =new ArrayList<>();


    @Override
    public void bindUI(View view) {
        containerNew = findViewById(R.id.container_new);
        ivNewWallet = findViewById(R.id.iv_new);
        rvWalletList = findViewById(R.id.recycleview_walletlist);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager
                .VERTICAL, false);
        rvWalletList.setLayoutManager(layoutManager);
        setNavibarTitle(getResources().getString(R.string.walletmanage_import_which_wallet_title), true);

        //test data
//        MultiWalletEntity entity = new MultiWalletEntity();
//        entity.setId(0);
//        entity.setWalletName("wallet1");
//        wallets.add(entity);
//
//        entity = new MultiWalletEntity();
//        entity.setId(1);
//        entity.setWalletName("wallet2");
//        wallets.add(entity);

        List<MultiWalletEntity> priKeyWallets = DBManager.getInstance().getMultiWalletEntityDao().getMultiWalletEntityListByWalletType(MultiWalletEntity.WALLET_TYPE_PRI_KEY);
        wallets.addAll(priKeyWallets);

        setNewWalletSelect(true);
        MultiWalletEntity selectedWallet = getIntent().getParcelableExtra(WalletManageConst.KEY_SELECT_IMPORT_WHICH_WALLET);
        LoggerManager.d("selectedWallet="+selectedWallet);
        if(selectedWallet!=null){
            for (MultiWalletEntity wallet : wallets) {
                wallet.setChecked(false);
                if(wallet.getId()==selectedWallet.getId()){
                    wallet.setChecked(true);
                    setNewWalletSelect(false);
                }
            }
        }

        adatper = new ImportWalletListAdapter(wallets);
        rvWalletList.setAdapter(adatper);
        adatper.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                for (MultiWalletEntity wallet : wallets) {
                    wallet.setChecked(false);
                }
                setNewWalletSelect(false);
                wallets.get(position).setChecked(true);
                adatper.notifyDataSetChanged();
                EventBusProvider.post(new SelectImportWhichWalletEvent(wallets.get(position)));
            }
        });

        containerNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (MultiWalletEntity wallet : wallets) {
                    wallet.setChecked(false);
                }
                setNewWalletSelect(true);
                adatper.notifyDataSetChanged();
                EventBusProvider.post(new SelectImportWhichWalletEvent(null));
            }
        });
    }

    @Override
    public void initData(Bundle savedInstanceState) {

    }

    @Override
    public int getLayoutId() {
        return R.layout.walletmanage_activity_import_which_wallet;
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

    private void setNewWalletSelect(boolean isSelected){
        if(isSelected){
            //如果该选项卡被选择
            ivNewWallet.setImageResource(R.drawable.ic_radio_button_selected);
        }else {
            //如果该选项卡未被选择
            ivNewWallet.setImageResource(R.drawable.ic_radio_button_unselected);
        }
    }
}
