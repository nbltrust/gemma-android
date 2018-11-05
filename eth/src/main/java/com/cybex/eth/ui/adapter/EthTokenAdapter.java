package com.cybex.eth.ui.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cybex.componentservice.bean.TokenBean;
import com.cybex.eth.R;

import java.util.List;

public class EthTokenAdapter extends BaseQuickAdapter<TokenBean, BaseViewHolder> {

    public EthTokenAdapter(
            @Nullable List<TokenBean> data) {
        super(R.layout.eth_item_token, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, TokenBean item) {
        ImageView ivTokenLogo = helper.getView(R.id.iv_token_logo);
        TextView tvTokenName = helper.getView(R.id.tv_token_name);
        TextView tvTokenAmount = helper.getView(R.id.tv_token_amount);
        tvTokenName.setText(item.getName());

    }
}
