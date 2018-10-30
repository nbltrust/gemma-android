package com.cybex.walletmanagement.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.cybex.componentservice.config.RouterConst;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.walletmanagement.R;
import com.hxlx.core.lib.mvp.lite.XActivity;

import java.util.List;

import me.framework.fragmentation.anim.DefaultHorizontalAnimator;
import me.framework.fragmentation.anim.FragmentAnimator;


@Route(path = RouterConst.PATH_TO_IMPORT_WALLET_GUIDE_PAGE)
public class ImportWalletGuideActivity extends XActivity {


    private ImageView ivDotOne;
    private TextView tvLookAroundHint;
    private Button btStartImport;



    @Override
    public void bindUI(View view) {
        ivDotOne = (ImageView) findViewById(R.id.iv_dot_one);
        tvLookAroundHint = (TextView) findViewById(R.id.tv_look_around_hint);
        btStartImport = (Button) findViewById(R.id.bt_start_import);
        btStartImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ImportWalletGuideActivity.this, ImportWalletActivity.class);
                startActivity(intent);
                finish();
            }
        });
        setNavibarTitle(getResources().getString(R.string.walletmanage_import_wallet), true);
    }

    @Override
    public void initData(Bundle savedInstanceState) {


    }

    @Override
    public int getLayoutId() {
        return R.layout.walletmanage_activity_import_wallet_guide;
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
