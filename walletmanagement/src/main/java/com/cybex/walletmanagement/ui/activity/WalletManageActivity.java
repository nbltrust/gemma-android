package com.cybex.walletmanagement.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.cybex.base.view.LabelLayout;
import com.cybex.componentservice.config.CacheConstants;
import com.cybex.componentservice.config.RouterConst;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.event.ChangeSelectedWalletEvent;
import com.cybex.componentservice.event.WalletNameChangedEvent;
import com.cybex.componentservice.event.WookongFormattedEvent;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.DeviceOperationManager;
import com.cybex.componentservice.utils.SizeUtil;
import com.cybex.walletmanagement.R;
import com.hxlx.core.lib.mvp.lite.XActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import me.framework.fragmentation.anim.DefaultHorizontalAnimator;
import me.framework.fragmentation.anim.FragmentAnimator;

@Route(path= RouterConst.PATH_TO_WALLET_MANAGE_PAGE)
public class WalletManageActivity extends XActivity {


    private ViewGroup rootview;
    private LabelLayout labelCurrentWallet;
    private LabelLayout labelImportWallet;
    private LabelLayout labelCreateWallet;
    private RelativeLayout containerWookong;
    private MultiWalletEntity currentWallet;
    private ImageView ivClose;


    @Override
    public void bindUI(View view) {

        rootview =  findViewById(R.id.wallet_manage_home);
        rootview.setPaddingRelative(0, SizeUtil.getStatusBarHeight(), 0, 0);

        ivClose = (ImageView) findViewById(R.id.iv_close);
        labelCurrentWallet = (LabelLayout) findViewById(R.id.label_current_wallet);
        labelImportWallet = (LabelLayout) findViewById(R.id.label_import_wallet);
        labelCreateWallet = (LabelLayout) findViewById(R.id.label_create_wallet);
        containerWookong = (RelativeLayout) findViewById(R.id.container_wookong);

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        containerWookong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build(RouterConst.PATH_TO_BLUETOOTH_PAIR)
                        .navigation();
            }
        });

        labelCurrentWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SelectCurrentWalletActivity.class);
                startActivity(intent);
            }
        });
        labelImportWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ImportWalletGuideActivity.class);
                startActivity(intent);
            }
        });
        labelCreateWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CreateMnemonicWalletActivity.class);
                startActivity(intent);
            }
        });


        currentWallet = DBManager.getInstance().getMultiWalletEntityDao().getCurrentMultiWalletEntity();
        labelCurrentWallet.setRightText(currentWallet.getWalletName());

    }

    @Override
    public void initData(Bundle savedInstanceState) {

        //如果已有蓝牙钱包，隐藏配对入口
//        List<MultiWalletEntity> bluetoothWalletList = DBManager.getInstance().getMultiWalletEntityDao()
//                .getBluetoothWalletList();
//
//        if ( bluetoothWalletList!= null && bluetoothWalletList.size() > 0){
//            containerWookong.setVisibility(View.GONE);
//        }


    }

    @Override
    public int getLayoutId() {
        return R.layout.walletmanage_activity_wallet_manage;
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
    public void changeWallet(ChangeSelectedWalletEvent event) {
        if(event.getCurrentWallet().getId()!=currentWallet.getId()){
            currentWallet=event.getCurrentWallet();
            labelCurrentWallet.setRightText(currentWallet.getWalletName());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changeWalletName(WalletNameChangedEvent event) {
        if(event.getWalletID()==currentWallet.getId()){
            currentWallet.setWalletName(event.getWalletName());
            labelCurrentWallet.setRightText(currentWallet.getWalletName());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWookongFormatted(WookongFormattedEvent event) {
        currentWallet = DBManager.getInstance().getMultiWalletEntityDao().getCurrentMultiWalletEntity();
        labelCurrentWallet.setRightText(currentWallet.getWalletName());

        List<MultiWalletEntity> bluetoothWalletList = DBManager.getInstance().getMultiWalletEntityDao().getBluetoothWalletList();
        if(bluetoothWalletList.size()>0){
            containerWookong.setVisibility(View.GONE);
        }else{
            containerWookong.setVisibility(View.VISIBLE);
        }

    }

    /**
     * 获取当前蓝牙钱包对应的设备名称
     * @return
     */
    public String getBluetoothDeviceName() {

        List<MultiWalletEntity> bluetoothWalletList = DBManager.getInstance().getMultiWalletEntityDao()
                .getBluetoothWalletList();

        if (bluetoothWalletList != null && bluetoothWalletList.size() > 0) {
            return bluetoothWalletList.get(0).getBluetoothDeviceName();
        }

        return "list empty err";

    }

}
