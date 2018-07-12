package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.cybex.gma.client.R;
import com.cybex.gma.client.event.EventBusActivityScope;
import com.cybex.gma.client.event.TabSelectedEvent;
import com.cybex.gma.client.ui.presenter.MainTabPresenter;
import com.cybex.gma.client.widget.bottombar.BottomBar;
import com.cybex.gma.client.widget.bottombar.BottomBarTab;
import com.hxlx.core.lib.mvp.lite.XFragment;

import me.framework.fragmentation.FragmentSupport;

/**
 * Created by wanglin on 2018/7/5.
 */
public class MainTabFragment extends XFragment<MainTabPresenter> {

    private static final int TAB_WALLET = 0;//钱包
    private static final int TAB_TRANSFER = 1;//转账
    private static final int TAB_MINE = 2;//我的

    private FragmentSupport[] mFragments = new FragmentSupport[3];

    private BottomBar mBottomBar;


    public static MainTabFragment newInstance() {

        Bundle args = new Bundle();

        MainTabFragment fragment = new MainTabFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void bindUI(View rootView) {
        mBottomBar = (BottomBar) rootView.findViewById(R.id.bottomBar);

        mBottomBar
                .addItem(new BottomBarTab(_mActivity, R.drawable.ic_wallet_normal, getString(R.string.tab_wallet)))

                .addItem(new BottomBarTab(_mActivity, R.drawable.ic_send_normal,
                        getString(R.string.tab_transfer)))
                .addItem(new BottomBarTab(_mActivity, R.drawable.ic_me_normal, getString(R.string.tab_mine)));


        mBottomBar.setOnTabSelectedListener(new BottomBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position, int prePosition) {
                showHideFragment(mFragments[position], mFragments[prePosition]);

                if(position==1){

                }
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {
                // 主要为了交互: 重选tab 如果列表不在顶部则移动到顶部,如果已经在顶部,则刷新
                EventBusActivityScope.getDefault(_mActivity).post(new TabSelectedEvent(position));
            }
        });



    }

    @Override
    public void initData(Bundle savedInstanceState) {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentSupport firstFragment = findChildFragment(WalletFragment.class);
        if (firstFragment == null) {
            mFragments[TAB_WALLET] = WalletFragment.newInstance();
            mFragments[TAB_TRANSFER] = TransferFragment.newInstance();
            mFragments[TAB_MINE] = MineFragment.newInstance();

            loadMultipleRootFragment(R.id.fl_tab_container, TAB_WALLET,
                    mFragments[TAB_WALLET],
                    mFragments[TAB_TRANSFER],
                    mFragments[TAB_MINE]);
        } else {
            mFragments[TAB_WALLET] = firstFragment;
            mFragments[TAB_TRANSFER] = findChildFragment(TransferFragment.class);
            mFragments[TAB_MINE] = findChildFragment(MineFragment.class);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_main_tab;
    }

    @Override
    public MainTabPresenter newP() {
        return null;
    }
}
