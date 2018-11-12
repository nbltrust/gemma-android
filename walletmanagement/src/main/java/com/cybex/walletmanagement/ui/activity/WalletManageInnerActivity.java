package com.cybex.walletmanagement.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cybex.base.view.LabelLayout;
import com.cybex.componentservice.config.BaseConst;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.event.RefreshWalletPswEvent;
import com.cybex.componentservice.event.WalletNameChangedEvent;
import com.cybex.componentservice.utils.PasswordValidateHelper;
import com.cybex.walletmanagement.R;
import com.hxlx.core.lib.mvp.lite.XActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import me.framework.fragmentation.anim.DefaultHorizontalAnimator;
import me.framework.fragmentation.anim.FragmentAnimator;


public class WalletManageInnerActivity extends XActivity {


    private RelativeLayout containerWalletName;
    private TextView tvWalletName;
    private TextView tvWalletType;
    private LabelLayout labelExportMnemonic;
    private LabelLayout labelExportPriKey;
    private LabelLayout labelChangePsw;
    private MultiWalletEntity wallet;

    @Override
    public void bindUI(View view) {
        setNavibarTitle(getResources().getString(R.string.walletmanage_manage_wallet_title), true);

        containerWalletName = (RelativeLayout) findViewById(R.id.container_wallet_name);
        tvWalletName = (TextView) findViewById(R.id.tv_wallet_name);
        tvWalletType = (TextView) findViewById(R.id.tv_wallet_type);
        labelExportMnemonic = (LabelLayout) findViewById(R.id.label_export_mnemonic);
        labelExportPriKey = (LabelLayout) findViewById(R.id.label_export_pri_key);
        labelChangePsw = (LabelLayout) findViewById(R.id.label_change_psw);

        containerWalletName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WalletManageInnerActivity.this, ChangeWalletNameActivity.class);
                intent.putExtra(BaseConst.KEY_WALLET_ENTITY,wallet);
                startActivity(intent);
            }
        });

        labelExportMnemonic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WalletManageInnerActivity.this, MnemonicBackupGuideActivity.class);
                intent.putExtra(BaseConst.KEY_WALLET_ENTITY,wallet);
                startActivity(intent);

            }
        });

        labelExportPriKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WalletManageInnerActivity.this, SelectWhichCoinExportPriKeyActivity.class);
                intent.putExtra(BaseConst.KEY_WALLET_ENTITY,wallet);
                startActivity(intent);

            }
        });

        labelChangePsw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PasswordValidateHelper passwordValidateHelper = new PasswordValidateHelper(wallet, context);
                passwordValidateHelper.startValidatePassword(new PasswordValidateHelper.PasswordValidateCallback() {
                    @Override
                    public void onValidateSuccess(String password) {
                        Intent intent = new Intent(WalletManageInnerActivity.this, ChangePasswordActivity.class);
                        intent.putExtra(BaseConst.KEY_WALLET_ENTITY,wallet);
                        intent.putExtra(BaseConst.KEY_PASSWORD,password);
                        startActivity(intent);
                    }

                    @Override
                    public void onValidateFail(int failedCount) {

                    }
                });
            }
        });

        if(getIntent()!=null){
            wallet = getIntent().getParcelableExtra(BaseConst.KEY_WALLET_ENTITY);
            tvWalletName.setText(wallet.getWalletName());
            int walletType = wallet.getWalletType();

            if(walletType==MultiWalletEntity.WALLET_TYPE_MNEMONIC||walletType==MultiWalletEntity.WALLET_TYPE_IMPORT_MNEMONIC){
                tvWalletType.setText(R.string.walletmanage_wallet_type_multi);
            }else{
                if(wallet.getEosWalletEntities().size()>0&&wallet.getEthWalletEntities().size()>0){
                    tvWalletType.setText(R.string.walletmanage_wallet_type_multi);
                }else if(wallet.getEosWalletEntities().size()>0){
//                    tvWalletType.setText(R.string.walletmanage_wallet_type_eos);
                    tvWalletType.setText(wallet.getEosWalletEntities().get(0).getPublicKey());
                }else{
//                    tvWalletType.setText(R.string.walletmanage_wallet_type_eth);
                    tvWalletType.setText(wallet.getEthWalletEntities().get(0).getAddress());
                }
            }

            if(walletType!=MultiWalletEntity.WALLET_TYPE_MNEMONIC){
                labelExportMnemonic.setVisibility(View.GONE);
                labelExportPriKey.setVisibility(View.GONE);
            }

        }

    }

    @Override
    public void initData(Bundle savedInstanceState) {


    }

    @Override
    public int getLayoutId() {
        return R.layout.walletmanage_activity_wallet_inner_manage;
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
        if (event.getCurrentWallet().getId()==wallet.getId()) {
            wallet=event.getCurrentWallet();
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshWalletName(WalletNameChangedEvent event) {
        if (event.getWalletID()==wallet.getId()) {
            wallet.setWalletName(event.getWalletName());
            tvWalletName.setText(wallet.getWalletName());
        }
    }


}
