package com.cybex.walletmanagement.ui.activity;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.cybex.componentservice.config.BaseConst;
import com.cybex.componentservice.config.RouterConst;
import com.cybex.walletmanagement.R;
import com.hxlx.core.lib.mvp.lite.XActivity;

import me.framework.fragmentation.anim.DefaultHorizontalAnimator;
import me.framework.fragmentation.anim.FragmentAnimator;

@Route(path = RouterConst.PATH_TO_BACKUP_MNEMONIC_GUIDE_PAGE)
public class MnemonicBackupGuideActivity extends XActivity {

    private Button btnShowMne;
    String mnemonic;


    @Override
    public void bindUI(View view) {
        btnShowMne = findViewById(R.id.bt_show_mne);
        btnShowMne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MnemonicBackupGuideActivity.this, MnemonicShowActivity.class);
                intent.putExtra(BaseConst.KEY_MNEMONIC,mnemonic);
                startActivity(intent);
                finish();
            }
        });
        setNavibarTitle(getResources().getString(R.string.walletmanage_backup_title), true);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mnemonic = getIntent().getStringExtra(BaseConst.KEY_MNEMONIC);
    }

    @Override
    public int getLayoutId() {
        return R.layout.walletmanage_activity_mnemonic_backup_guide;
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
