package com.example.yiran.gemma.Home;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.cybex.gma.client.R;
import com.example.yiran.gemma.Adapter.NavFragmentAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends FragmentActivity {

    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initViewPager();
    }


    private void initViewPager(){
        mViewPager = findViewById(R.id.viewpager);
        mTabLayout = findViewById(R.id.tabs);

        List<String> titles = new ArrayList<>();
        titles.add("Wallet");
        titles.add("Send");
        titles.add("Me");

        for (int i = 0; i < titles.size(); i++){
            mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(i)));
        }

        List<Fragment> fragments = new ArrayList<>();

        fragments.add(new HomeFragment());
        fragments.add(new com.example.yiran.gemma.Modules.Transfer.TransferFragment());
        fragments.add(new com.example.yiran.gemma.Modules.Me.MeFragment());

        NavFragmentAdapter navFragmentAdapter = new NavFragmentAdapter(getSupportFragmentManager(), fragments, titles);
        mViewPager.setAdapter(navFragmentAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

    }



    @Override
    protected void onResume(){
        super.onResume();
    }


    @Override
    protected void onDestroy(){
        super.onDestroy();
    }


}
