package com.example.yiran.gemma.Adapter;

import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

public class NavFragmentAdapter extends FragmentStatePagerAdapter {

    //private static final int Count = 3;
    private List<String> mTitle;
    private List<android.support.v4.app.Fragment> mFragments;

    public NavFragmentAdapter(android.support.v4.app.FragmentManager fm, List<android.support.v4.app.Fragment> fragments, List<String> titles){
        super(fm);
        mFragments = fragments;
        mTitle = titles;
    }

    @Override
    public android.support.v4.app.Fragment getItem(int position){
        return mFragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position){
        return mTitle.get(position);
    }

    @Override
    public int getCount(){
        return mTitle.size();
    }

}
