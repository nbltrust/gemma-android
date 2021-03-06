//package com.cybex.gma.client.ui.fragment;
//
//import android.os.Build;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.view.View;
//import android.widget.FrameLayout;
//import android.widget.LinearLayout;
//
//import com.cybex.componentservice.db.dao.WalletEntityDao;
//import com.cybex.componentservice.db.entity.WalletEntity;
//import com.cybex.gma.client.R;
//import com.cybex.gma.client.event.TabSelectedEvent;
//import com.cybex.componentservice.manager.DBManager;
//import com.cybex.componentservice.manager.LoggerManager;
//import com.cybex.gma.client.ui.presenter.MainTabPresenter;
//import com.cybex.gma.client.widget.bottombar.BottomBar;
//import com.cybex.gma.client.widget.bottombar.BottomBarTab;
//import com.cybex.qrcode.core.QRCodeUtil;
//import com.hxlx.core.lib.common.eventbus.EventBusProvider;
//import com.hxlx.core.lib.mvp.lite.XFragment;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import butterknife.Unbinder;
//import me.framework.fragmentation.FragmentSupport;
//
///**
// * Created by wanglin on 2018/7/5.
// */
//public class MainTabFragment extends XFragment<MainTabPresenter> {
//
//    private static final int TAB_WALLET = 0;//钱包
//    private static final int TAB_TRANSFER = 1;//转账
//    private static final int TAB_MINE = 2;//我的
//
//    @BindView(R.id.fl_tab_container) FrameLayout flTabContainer;
//    @BindView(R.id.bottomBar) BottomBar bottomBar;
//    @BindView(R.id.view_bot_tab) LinearLayout viewBotTab;
//    Unbinder unbinder;
//
//    private FragmentSupport[] mFragments = new FragmentSupport[3];
//
//    private BottomBar mBottomBar;
//
//
//    public static MainTabFragment newInstance() {
//        Bundle args = new Bundle();
//        MainTabFragment fragment = new MainTabFragment();
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//    @Override
//    public void bindUI(View rootView) {
//        unbinder = ButterKnife.bind(this, rootView);
//        mBottomBar = rootView.findViewById(R.id.bottomBar);
//
//        mBottomBar
//                .addItem(new BottomBarTab(_mActivity, R.drawable.ic_wallet_normal, getString(R.string.eos_tab_wallet)))
//
//                .addItem(new BottomBarTab(_mActivity, R.drawable.ic_send_normal,
//                        getString(R.string.eos_tab_transfer)))
//                .addItem(new BottomBarTab(_mActivity, R.drawable.ic_me_normal, getString(R.string.eos_tab_mine)));
//
//
//        mBottomBar.setOnTabSelectedListener(new BottomBar.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(int position, int prePosition) {
//                showHideFragment(mFragments[position], mFragments[prePosition]);
//                TabSelectedEvent event_no_fresh = new TabSelectedEvent();
//                event_no_fresh.setPosition(position);
//                event_no_fresh.setRefresh(false);
//                EventBusProvider.postSticky(event_no_fresh);
//            }
//
//            @Override
//            public void onTabUnselected(int position) {
//
//            }
//
//            @Override
//            public void onTabReselected(int position) {
//                // 主要为了交互: 重选tab 如果列表不在顶部则移动到顶部,如果已经在顶部,则刷新
//            }
//        });
//
//
//    }
//
//    @Override
//    public void initData(Bundle savedInstanceState) {
//        TabSelectedEvent event_fresh = new TabSelectedEvent();
//        event_fresh.setPosition(0);
//        event_fresh.setRefresh(true);
//        EventBusProvider.postSticky(event_fresh);
//
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
//            if (getActivity() != null && getContext() != null) {
//                int height = QRCodeUtil.getStatusBarHeight(getContext());
//                LoggerManager.d("statusBarHeight", height);
//                viewBotTab.setPadding(0, height, 0, 0);
//            }
//        }
//    }
//
//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        FragmentSupport firstFragment = null;
//        WalletEntityDao dao = DBManager.getInstance().getWalletEntityDao();
//        WalletEntity entity = dao.getCurrentWalletEntity();
//        int type = 0;
//        if (entity != null) {
//            type = entity.getWalletType();
//            if (type == 1) {
//                firstFragment = findChildFragment(BluetoothWalletFragment.class);
//            } else {
//                firstFragment = findChildFragment(WalletFragment.class);
//            }
//
//        } else {
//            firstFragment = findChildFragment(WalletFragment.class);
//        }
//
//        if (firstFragment == null) {
//            if (type == 1) {
//                mFragments[TAB_WALLET] = BluetoothWalletFragment.newInstance();
//            } else {
//                mFragments[TAB_WALLET] = WalletFragment.newInstance();
//            }
//            mFragments[TAB_TRANSFER] = TransferFragment.newInstance();
//            mFragments[TAB_MINE] = SettingsFragment.newInstance();
//
//            loadMultipleRootFragment(R.id.fl_tab_container, TAB_WALLET,
//                    mFragments[TAB_WALLET],
//                    mFragments[TAB_TRANSFER],
//                    mFragments[TAB_MINE]);
//        } else {
//            mFragments[TAB_WALLET] = firstFragment;
//            mFragments[TAB_TRANSFER] = findChildFragment(TransferFragment.class);
//            mFragments[TAB_MINE] = findChildFragment(SettingsFragment.class);
//        }
//
//    }
//
//    @Override
//    public int getLayoutId() {
//        return R.layout.eos_fragment_main_tab;
//    }
//
//    @Override
//    public MainTabPresenter newP() {
//        return null;
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        unbinder.unbind();
//    }
//}
