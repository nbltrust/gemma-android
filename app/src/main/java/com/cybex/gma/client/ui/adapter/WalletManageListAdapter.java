package com.cybex.gma.client.ui.adapter;

import android.support.annotation.Nullable;

import com.allen.library.SuperTextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cybex.gma.client.R;
import com.cybex.gma.client.ui.model.vo.WalletVO;

import java.util.List;

public class WalletManageListAdapter extends BaseQuickAdapter<WalletVO, BaseViewHolder> {

    public WalletManageListAdapter(@Nullable List<WalletVO> data){
        super(R.layout.item_wallet_name, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, WalletVO item) {
        SuperTextView superTextView =  helper.getView(R.id.superTV_Item_WalletName);
        superTextView.setLeftString(item.getWalletName());

        helper.addOnClickListener(R.id.superTV_Item_WalletName);

    }
}
