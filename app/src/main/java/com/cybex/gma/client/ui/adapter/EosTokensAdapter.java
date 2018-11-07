package com.cybex.gma.client.ui.adapter;


import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cybex.gma.client.R;
import com.cybex.gma.client.ui.model.vo.EosTokenVO;

import java.util.List;

public class EosTokensAdapter extends BaseQuickAdapter<EosTokenVO, BaseViewHolder> {


    @Override
    protected void convert(BaseViewHolder helper, EosTokenVO item) {
        TextView tv_token_name = helper.getView(R.id.tv_token_name);
        TextView tv_token_quantity = helper.getView(R.id.tv_token_quantity);
        ImageView iv_token_logo = helper.getView(R.id.iv_token_logo);

        tv_token_name.setText(item.getTokenSymbol());
        tv_token_quantity.setText(String.valueOf(item.getQuantity()));
        //todo 用Glide加载指定url图片进入ImageView
        iv_token_logo.setImageResource(R.drawable.eos_ic_asset);

    }

    public EosTokensAdapter(@Nullable List<EosTokenVO> data) {
        super(R.layout.item_single_token, data);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
