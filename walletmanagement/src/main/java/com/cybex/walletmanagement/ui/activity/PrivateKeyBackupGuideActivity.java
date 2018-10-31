package com.cybex.walletmanagement.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cybex.componentservice.bean.CoinType;
import com.cybex.componentservice.config.BaseConst;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.componentservice.utils.PasswordValidateHelper;
import com.cybex.walletmanagement.R;
import com.hxlx.core.lib.mvp.lite.XActivity;

import me.framework.fragmentation.anim.DefaultHorizontalAnimator;
import me.framework.fragmentation.anim.FragmentAnimator;
import seed39.Seed39;

public class PrivateKeyBackupGuideActivity extends XActivity {

    private Button btnShowPri;
    private MultiWalletEntity wallet;
    private CoinType coinType;


    @Override
    public void bindUI(View view) {
        btnShowPri = findViewById(R.id.bt_show_pri);
        btnShowPri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(wallet!=null){
                    PasswordValidateHelper passwordValidateHelper = new PasswordValidateHelper(wallet,context);
                    passwordValidateHelper.startValidatePassword(new PasswordValidateHelper.PasswordValidateCallback() {
                        @Override
                        public void onValidateSuccess(String password) {
                            String encryptPriKey;
                            if(CoinType.EOS.equals(coinType)){
                                encryptPriKey=wallet.getEosWalletEntities().get(0).getPrivateKey();
                            }else{
                                encryptPriKey=wallet.getEthWalletEntities().get(0).getPrivateKey();
                            }

                            String decryptPriKey = Seed39.keyDecrypt(password, encryptPriKey);
                            Intent intent = new Intent(PrivateKeyBackupGuideActivity.this, PriKeyShowActivity.class);
                            intent.putExtra(BaseConst.KEY_PRI_KEY,decryptPriKey);
                            intent.putExtra(BaseConst.KEY_WALLET_ENTITY,wallet);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onValidateFail(int failedCount) {
                        }
                    });
                }
            }
        });
        setNavibarTitle(getResources().getString(R.string.walletmanage_pri_key_backup_title), true);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        wallet = getIntent().getParcelableExtra(BaseConst.KEY_WALLET_ENTITY);
        coinType = (CoinType) getIntent().getSerializableExtra(BaseConst.KEY_COIN_TYPE);
    }

    @Override
    public int getLayoutId() {
        return R.layout.walletmanage_activity_pri_key_backup_guide;
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
