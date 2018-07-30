package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.allen.library.SuperTextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.cybex.gma.client.R;
import com.cybex.gma.client.db.entity.WalletEntity;
import com.cybex.gma.client.manager.DBManager;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.adapter.WalletManageListAdapter;
import com.cybex.gma.client.ui.model.vo.WalletVO;
import com.hxlx.core.lib.mvp.lite.XFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

/**
 * 管理钱包一级界面
 * 钱包主页面中点击右上角icon进入的界面
 */
public class ManageWalletFragment extends XFragment {

    @BindView(R.id.layout_wallet_number) LinearLayout layoutWalletNumber;
    @BindView(R.id.superTextView_importWallet) SuperTextView superTextViewImportWallet;
    @BindView(R.id.superTextView_createWallet) SuperTextView superTextViewCreateWallet;
    @BindView(R.id.scroll_wallet_manage) ScrollView scrollViewWalletManage;
    @BindView(R.id.recycler_wallet_manage) RecyclerView recyclerViewWalletManage;
    Unbinder unbinder;
    private WalletManageListAdapter adapter;

    public static ManageWalletFragment newInstance() {
        Bundle args = new Bundle();
        ManageWalletFragment fragment = new ManageWalletFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(ManageWalletFragment.this, rootView);
        OverScrollDecoratorHelper.setUpOverScroll(scrollViewWalletManage);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setNavibarTitle("钱包", true, true);
        superTextViewCreateWallet.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                UISkipMananger.launchCreateWallet(getActivity());
            }
        });

        superTextViewImportWallet.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                UISkipMananger.launchImportWallet(getActivity());
            }
        });

        setWalletListViewData();
    }


    @Override
    protected void setNavibarTitle(String title, boolean isShowBack) {
        super.setNavibarTitle(title, isShowBack);

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_manage_wallet;
    }

    @Override
    public Object newP() {
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateWalletList();
        //setWalletListViewData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    public void updateWalletList(){

        //从数据库中读取Wallet信息转换成WalletVO列表
        List<WalletEntity> walletEntityList = DBManager.getInstance().getWalletEntityDao().getWalletEntityList();
        List<WalletVO> walletVOList = new ArrayList<>();

        for (int i = 0; i < walletEntityList.size(); i++) {
            WalletVO curWalletVO = new WalletVO();
            curWalletVO.setWalletName(walletEntityList.get(i).getWalletName());
            walletVOList.add(curWalletVO);
        }
        adapter = new WalletManageListAdapter(walletVOList);
    }

    /**
     * 把钱包名称数据放入RecyclerView中显示出来
     */
    public void setWalletListViewData() {
        updateWalletList();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getActivity(), LinearLayoutManager
                .VERTICAL, false);
        recyclerViewWalletManage.setLayoutManager(layoutManager);
        recyclerViewWalletManage.setAdapter(adapter);
        recyclerViewWalletManage.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                //position与数据库表中的id对应，可直接根据position值来确定ID
                WalletEntity curWallet = DBManager.getInstance().getWalletEntityDao().getWalletEntityByID(position+1);
                Bundle bundle = new Bundle();
                bundle.putParcelable("curWallet", curWallet);
                start(WalletDetailFragment.newInstance(bundle));
            }
        });
    }

}
