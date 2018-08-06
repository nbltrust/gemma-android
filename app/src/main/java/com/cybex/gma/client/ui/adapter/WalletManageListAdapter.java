package com.cybex.gma.client.ui.adapter;

import android.support.annotation.Nullable;
import android.widget.TextView;

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
        //SuperTextView superTextView =  helper.getView(R.id.superTV_Item_WalletName);
        //superTextView.setLeftString(item.getWalletName());
        TextView textView = helper.getView(R.id.tv_item_walletName);
        //textView.setText(item.getWalletName());

        if (item.isSelected){
            //默认被选中的选项卡
            textView.setText("selected");
            //textView.setBackgroundColor(0x426bd4);
            //设置背景色为cornflowerBlue
        }else{
            textView.setText(item.getWalletName());
            //textView.setBackgroundColor(0xffffff);
        }

    }
}
