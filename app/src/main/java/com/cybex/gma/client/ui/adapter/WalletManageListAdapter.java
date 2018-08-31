package com.cybex.gma.client.ui.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cybex.gma.client.R;
import com.cybex.gma.client.ui.model.vo.WalletVO;

import java.util.List;

public class WalletManageListAdapter extends BaseQuickAdapter<WalletVO, BaseViewHolder> {

    public WalletManageListAdapter(@Nullable List<WalletVO> data) {
        super(R.layout.item_wallet_name, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, WalletVO item) {
        helper.addOnClickListener(R.id.layout_more);


        ImageView imvType = helper.getView(R.id.imv_wookong_logo);
        if (item.getWalletType() == 1) {
            imvType.setVisibility(View.VISIBLE);
        } else {
            imvType.setVisibility(View.GONE );
        }


        if (item.isSelected) {
            //默认被选中的选项卡
            helper.setText(R.id.tv_item_walletName, item.getWalletName());
            helper.setTextColor(R.id.tv_item_walletName, 0xffffffff);
            helper.setBackgroundRes(R.id.layout_item_wallet, R.drawable.shape_corner_button);
            //设置背景色为cornflowerBlue
        } else {
            helper.setText(R.id.tv_item_walletName, item.getWalletName());
            helper.setTextColor(R.id.tv_item_walletName, 0xff212c67);
            helper.setBackgroundRes(R.id.layout_item_wallet, R.drawable.shape_corner);
        }

    }
}
