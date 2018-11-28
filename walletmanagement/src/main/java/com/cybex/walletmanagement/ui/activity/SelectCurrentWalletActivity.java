package com.cybex.walletmanagement.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.cybex.componentservice.config.BaseConst;
import com.cybex.componentservice.config.RouterConst;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.event.ChangeSelectedWalletEvent;
import com.cybex.componentservice.event.RefreshWalletPswEvent;
import com.cybex.componentservice.event.WalletNameChangedEvent;
import com.cybex.componentservice.event.WookongFormattedEvent;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.DeviceOperationManager;
import com.cybex.componentservice.utils.SizeUtil;
import com.cybex.walletmanagement.R;
import com.cybex.walletmanagement.ui.adapter.SelectCurrentWalletAdapter;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.siberiadante.customdialoglib.CustomDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.ArrayList;
import java.util.List;
import me.framework.fragmentation.anim.DefaultHorizontalAnimator;
import me.framework.fragmentation.anim.FragmentAnimator;


public class SelectCurrentWalletActivity extends XActivity {


    private RecyclerView rvWalletList;
    private SelectCurrentWalletAdapter adatper;
    private MultiWalletEntity checkedWallet;
    private MultiWalletEntity originCheckedWallet;

    private List<MultiWalletEntity> wallets = new ArrayList<>();


    @Override
    public void bindUI(View view) {
        rvWalletList = findViewById(R.id.rv_wallets);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager
                .VERTICAL, false);
        rvWalletList.setLayoutManager(layoutManager);
        setNavibarTitle(getResources().getString(R.string.walletmanage_select_wallet_title), true);
        mTitleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkStatusAndFinish();
            }
        });

        List<MultiWalletEntity> allWallets = DBManager.getInstance().getMultiWalletEntityDao().getMultiWalletEntityList();
        wallets.addAll(allWallets);

        for (MultiWalletEntity wallet : wallets) {
            if (wallet.getIsCurrentWallet() != 0) {
                wallet.setChecked(true);
                checkedWallet = wallet;
                originCheckedWallet=wallet;
                break;
            }
        }

        adatper = new SelectCurrentWalletAdapter(wallets);
        rvWalletList.setAdapter(adatper);
        adatper.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

                if (view.getId() == R.id.iv_tip) {
                    MultiWalletEntity wallet = wallets.get(position);

                    int walletType = wallet.getWalletType();
                    if (walletType == MultiWalletEntity.WALLET_TYPE_HARDWARE) {
                        //蓝牙钱包
                        Intent intent = new Intent(context, BluetoothWalletManageActivity.class);
                        startActivity(intent);
                    }
//                    else if(walletType==MultiWalletEntity.WALLET_TYPE_MNEMONIC){
//                        Intent intent = new Intent(context, WalletManageInnerActivity.class);
//                        intent.putExtra(BaseConst.KEY_WALLET_ENTITY,wallet);
//                        startActivity(intent);
//                    }
                    else {
                        //软钱包
                        Intent intent = new Intent(context, WalletManageInnerActivity.class);
                        intent.putExtra(BaseConst.KEY_WALLET_ENTITY, wallet);
                        startActivity(intent);
                    }


                } else if (view.getId() == R.id.rootview_wallet) {
                    for (MultiWalletEntity wallet : wallets) {
                        wallet.setChecked(false);
//                        wallet.setIsCurrentWallet(0);
                    }
                    wallets.get(position).setChecked(true);
                    checkedWallet=wallets.get(position);
//                    checkedWallet.setIsCurrentWallet(1);
                    adatper.notifyDataSetChanged();
                }

            }
        });

    }

    private void checkStatusAndFinish() {

        if(originCheckedWallet!=null&&originCheckedWallet.getWalletType()==MultiWalletEntity.WALLET_TYPE_HARDWARE
                &&checkedWallet.getId() != originCheckedWallet.getId()&& DeviceOperationManager.getInstance().isDeviceConnectted(originCheckedWallet.getBluetoothDeviceName())){
            //show dialog
            int[] listenedItems = {R.id.tv_cancel,R.id.tv_confirm};
            CustomDialog dialog = new CustomDialog(this,
                    R.layout.baseservice_dialog_change_bluetoothwallet, listenedItems,false, Gravity.CENTER);
            dialog.setmWidth(SizeUtil.dp2px(259));
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {

                }
            });
            dialog.setOnDialogItemClickListener(new CustomDialog.OnCustomDialogItemClickListener() {

                @Override
                public void OnCustomDialogItemClick(CustomDialog dialog, View view) {
                    if(view.getId()== R.id.tv_cancel){
                        dialog.cancel();
                    }else if(view.getId()== R.id.tv_confirm){
                        DeviceOperationManager.getInstance().freeContext(SelectCurrentWalletActivity.this.toString(),originCheckedWallet.getBluetoothDeviceName(),null);
                        saveCheckedWallet();
                        finish();
                    }
                }
            });
            dialog.show();
        }else{
            //save current wallet
            saveCheckedWallet();
            finish();
        }
    }


    private void saveCheckedWallet(){
        if (checkedWallet != null && originCheckedWallet != null && checkedWallet.getId() != originCheckedWallet.getId()) {
            originCheckedWallet.setIsCurrentWallet(0);
            checkedWallet.setIsCurrentWallet(1);
            originCheckedWallet.save();
            checkedWallet.save();
            EventBusProvider.post(new ChangeSelectedWalletEvent(checkedWallet));
        }
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
        DeviceOperationManager.getInstance().clearCallback(this.toString());
        super.onDestroy();
    }


    @Override
    public void onBackPressedSupport() {
        checkStatusAndFinish();
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

    @Override
    public boolean useEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshWallet(RefreshWalletPswEvent event) {
        int index=-1;
        for (int i = 0; i < wallets.size(); i++) {
            MultiWalletEntity walletEntity = wallets.get(i);
            if (event.getCurrentWallet().getId() == walletEntity.getId()) {
                index=i;
                break;
            }
        }
        if(index>=0){
            wallets.set(index,event.getCurrentWallet());
            if (wallets.get(index).getId() == checkedWallet.getId()) {
                wallets.get(index).setChecked(true);
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshWalletName(WalletNameChangedEvent event) {
        for (MultiWalletEntity walletEntity : wallets) {
            if (event.getWalletID() == walletEntity.getId()) {
                walletEntity.setWalletName(event.getWalletName());
                adatper.notifyDataSetChanged();
                break;
            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWookongFormatted(WookongFormattedEvent event) {
        wallets.clear();
        List<MultiWalletEntity> allWallets = DBManager.getInstance().getMultiWalletEntityDao().getMultiWalletEntityList();
        wallets.addAll(allWallets);
        for (MultiWalletEntity wallet : wallets) {
            if (wallet.getIsCurrentWallet() != 0) {
                wallet.setChecked(true);
                checkedWallet = wallet;
                originCheckedWallet=wallet;
                break;
            }
        }
        adatper.notifyDataSetChanged();

        if(event.isNeedReinit()){
            ARouter.getInstance().build(RouterConst.PATH_TO_BLUETOOTH_PAIR)
                    .navigation();
        }
    }




}
