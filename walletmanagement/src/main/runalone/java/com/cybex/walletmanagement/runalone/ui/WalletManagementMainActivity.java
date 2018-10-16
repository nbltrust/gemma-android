package com.cybex.walletmanagement.runalone.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.cybex.componentservice.bean.TransferTestBean;
import com.cybex.componentservice.service.EosWalletService;
import com.cybex.walletmanagement.R;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.mrzhang.component.componentlib.router.Router;

public class WalletManagementMainActivity extends XActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.walletmanage_activity_wallet_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Router router= Router.getInstance();
                EosWalletService service = (EosWalletService) router.getService(EosWalletService.class.getSimpleName());
                Toast.makeText(context, "balance="+service.getEosBalance(0), Toast.LENGTH_SHORT).show();


                ARouter.getInstance().build("/test/activity")
                        .withString("name", "888")
                        .withObject("bean", new TransferTestBean("Jack", 11))
                        .navigation();
            }
        });

    }

    @Override
    public void bindUI(View rootView) {

    }

    @Override
    public void initData(Bundle savedInstanceState) {

    }

    @Override
    public int getLayoutId() {
        return 0;
    }

    @Override
    public Object newP() {
        return null;
    }

}
