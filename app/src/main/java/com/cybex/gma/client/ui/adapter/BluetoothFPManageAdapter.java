package com.cybex.gma.client.ui.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.allen.library.SuperTextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cybex.gma.client.R;
import com.cybex.gma.client.ui.model.vo.BluetoothFPVO;
import com.cybex.gma.client.ui.model.vo.WalletVO;

import java.util.List;

public class BluetoothFPManageAdapter extends BaseQuickAdapter<BluetoothFPVO, BaseViewHolder> {

    public BluetoothFPManageAdapter(@Nullable List<BluetoothFPVO> data) {
        super(R.layout.item_bluetooth_fingerprint, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, BluetoothFPVO item) {
        SuperTextView fpCrad = helper.getView(R.id.superTextView_bluetooth_FPCard);
        fpCrad.setLeftString(item.getFingerprintName());
    }
}
