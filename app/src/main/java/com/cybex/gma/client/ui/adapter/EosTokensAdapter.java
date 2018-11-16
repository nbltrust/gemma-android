package com.cybex.gma.client.ui.adapter;


import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cybex.componentservice.utils.AmountUtil;
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
        String quantity = String.valueOf(item.getQuantity());
        tv_token_quantity.setText(quantity);

        String url = item.getLogo_url();
        if (url == null || url.equals("")){
            iv_token_logo.setImageResource(R.drawable.ic_token_unknown);
        }else {
            Glide.with(iv_token_logo.getContext())
                    .load(url)
                    .into(iv_token_logo);
        }

    }

    public EosTokensAdapter(@Nullable List<EosTokenVO> data) {
        super(R.layout.item_single_token, data);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
