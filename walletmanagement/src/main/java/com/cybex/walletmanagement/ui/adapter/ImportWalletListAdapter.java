package com.cybex.walletmanagement.ui.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.walletmanagement.R;
import com.cybex.walletmanagement.ui.model.CoinTypeBean;

import java.util.List;

public class ImportWalletListAdapter extends BaseQuickAdapter<MultiWalletEntity, BaseViewHolder> {

    public ImportWalletListAdapter(@Nullable List<MultiWalletEntity> data) {
        super(R.layout.walletmanage_item_import_which_wallet, data);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiWalletEntity item) {
        TextView tvWalletName = helper.getView(R.id.tv_wallet_name);
        ImageView ivCheckbox = helper.getView(R.id.iv_checkbox);
        tvWalletName.setText(item.getWalletName());

        helper.addOnClickListener(R.id.rootview_wallet);

        if(item.isChecked){
            //如果该选项卡被选择
            ivCheckbox.setImageResource(R.drawable.ic_radio_button_selected);
        }else {
            //如果该选项卡未被选择
            ivCheckbox.setImageResource(R.drawable.ic_radio_button_unselected);
        }

    }
}
