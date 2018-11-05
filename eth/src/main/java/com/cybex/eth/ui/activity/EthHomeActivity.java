package com.cybex.eth.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.cybex.componentservice.bean.TokenBean;
import com.cybex.componentservice.config.RouterConst;
import com.cybex.componentservice.db.entity.EthWalletEntity;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.ui.adapter.TokenIconAdapter;
import com.cybex.componentservice.utils.ClipboardUtils;
import com.cybex.componentservice.utils.SizeUtil;
import com.cybex.componentservice.widget.EthCardView;
import com.cybex.eth.R;
import com.cybex.eth.ui.adapter.EthTokenAdapter;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;

import java.util.ArrayList;

import me.framework.fragmentation.anim.DefaultHorizontalAnimator;
import me.framework.fragmentation.anim.FragmentAnimator;


@Route(path = RouterConst.PATH_TO_ETH_HOME)
public class EthHomeActivity extends XActivity {


    private RecyclerView rvTokens;
    private EthCardView ethCardView;
    private TextView tvCurrentAccount;
    private ImageView ivBack;
    private ImageView ivCopy;
    private MultiWalletEntity currentWallet;
    private EthWalletEntity currentEthWallet;


    @Override
    public void bindUI(View view) {
        getWindow().getDecorView().setPaddingRelative(0, SizeUtil.getStatusBarHeight(),0,0);
        ivBack = findViewById(R.id.iv_back);
        ivCopy = findViewById(R.id.iv_copy);
        tvCurrentAccount = findViewById(R.id.tv_current_account);
        rvTokens = findViewById(R.id.recycler_token_list);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ivCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardUtils.copyText(context, getAccountName());
                GemmaToastUtils.showLongToast(getString(R.string.eth_copy_success));
            }
        });


        currentWallet = DBManager.getInstance().getMultiWalletEntityDao().getCurrentMultiWalletEntity();
        currentEthWallet = currentWallet.getEthWalletEntities().get(0);
        tvCurrentAccount.setText(currentEthWallet.getAddress());

        ArrayList<TokenBean>  tokenBeans= new ArrayList<>();
        tokenBeans.add(new TokenBean("BAT",""));
        tokenBeans.add(new TokenBean("SNT",""));
        EthTokenAdapter ethTokenAdapter = new EthTokenAdapter(tokenBeans);
        View header = LayoutInflater.from(context).inflate(R.layout.eth_header_eth_home, null);

        ethCardView = header.findViewById(R.id.eth_card);
        ethCardView.setTokenListVisibility(false);
        ethCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,EthAssetDetailActivity.class));
            }
        });

        ethTokenAdapter.addHeaderView(header);
        ethTokenAdapter.setEnableLoadMore(false);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager
                .VERTICAL, false);
        rvTokens.setLayoutManager(layoutManager);

        rvTokens.setAdapter(ethTokenAdapter);
    }

    @Override
    public void initData(Bundle savedInstanceState) {


    }

    @Override
    public int getLayoutId() {
        return R.layout.eth_activity_home;
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

    public String getAccountName() {
        return currentEthWallet.getAddress();
    }
}
