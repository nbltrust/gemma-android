package com.cybex.walletmanagement.ui.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cybex.walletmanagement.R;
import com.cybex.walletmanagement.ui.model.CoinTypeBean;

import java.util.List;

public class CoinTypeListAdapter extends BaseQuickAdapter<CoinTypeBean, BaseViewHolder> {

    public CoinTypeListAdapter(@Nullable List<CoinTypeBean> data) {
        super(R.layout.walletmanage_item_coin_type, data);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    protected void convert(BaseViewHolder helper, CoinTypeBean item) {
        TextView tvCointype = helper.getView(R.id.tv_cointype);
        ImageView ivCheckbox = helper.getView(R.id.iv_checkbox);
        tvCointype.setText(item.cointype.coinName);

        helper.addOnClickListener(R.id.rootview_cointype);

        if(item.isChecked){
            //如果该选项卡被选择
            ivCheckbox.setImageResource(R.drawable.ic_radio_button_selected);
        }else {
            //如果该选项卡未被选择
            ivCheckbox.setImageResource(R.drawable.ic_radio_button_unselected);
        }

    }
}
