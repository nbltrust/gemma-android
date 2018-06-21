package com.example.yiran.gemma.Modules.Wallet.ImportWallet;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import com.example.yiran.gemma.Adapter.NavFragmentAdapter;
import com.example.yiran.gemma.R;
import com.example.yiran.gemma.Modules.Wallet.ImportWallet.ImportByMne.ImportByMneFragment;
import com.example.yiran.gemma.Modules.Wallet.ImportWallet.ImportByPrikey.ImportByPriKeyFragment;

import java.util.ArrayList;
import java.util.List;

public class ImportWalletActivity extends FragmentActivity {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_wallet);
        initView();
    }

    public void initView(){
        mViewPager = findViewById(R.id.viewpager_import_wallet);
        mTabLayout = findViewById(R.id.tabLayout_import_wallet);

        List<String> titles = new ArrayList<>();
        titles.add("助记词");
        titles.add("私钥");

        for (int i = 0; i < titles.size(); i++){
            mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(i)));
        }

        List<Fragment> fragments = new ArrayList<>();

        fragments.add(new ImportByMneFragment());
        fragments.add(new ImportByPriKeyFragment());

        NavFragmentAdapter navFragmentAdapter = new NavFragmentAdapter(getSupportFragmentManager(), fragments, titles);
        mViewPager.setAdapter(navFragmentAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

    }
}
