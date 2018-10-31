package com.cybex.walletmanagement.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.cybex.componentservice.bean.CoinType;
import com.cybex.componentservice.config.BaseConst;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.walletmanagement.R;
import com.hxlx.core.lib.mvp.lite.XActivity;

import me.framework.fragmentation.anim.DefaultHorizontalAnimator;
import me.framework.fragmentation.anim.FragmentAnimator;


public class SelectWhichCoinExportPriKeyActivity extends XActivity {


    private RelativeLayout containerEos;
    private RelativeLayout containerEth;
    private MultiWalletEntity wallet;


    @Override
    public void bindUI(View view) {
        containerEos = (RelativeLayout) findViewById(R.id.container_eos);
        containerEth = (RelativeLayout) findViewById(R.id.container_eth);
        setNavibarTitle(getResources().getString(R.string.walletmanage_coin_type_title), true);

        wallet = getIntent().getParcelableExtra(BaseConst.KEY_WALLET_ENTITY);

        containerEos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToBackup(CoinType.EOS);
            }
        });

        containerEth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToBackup(CoinType.ETH);
            }
        });

    }

    private void goToBackup(CoinType coinType) {
        Intent intent = new Intent(this, PrivateKeyBackupGuideActivity.class);
        intent.putExtra(BaseConst.KEY_WALLET_ENTITY,wallet);
        intent.putExtra(BaseConst.KEY_COIN_TYPE,coinType);
        startActivity(intent);
    }

    @Override
    public void initData(Bundle savedInstanceState) {

    }

    @Override
    public int getLayoutId() {
        return R.layout.walletmanage_activity_select_coin_export_pri_key;
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
