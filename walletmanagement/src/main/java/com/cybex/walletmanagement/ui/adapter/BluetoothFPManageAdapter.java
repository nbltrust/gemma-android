package com.cybex.walletmanagement.ui.adapter;

import android.support.annotation.Nullable;

import com.allen.library.SuperTextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cybex.walletmanagement.R;
import com.cybex.walletmanagement.ui.model.vo.BluetoothFPVO;


import java.util.List;

public class BluetoothFPManageAdapter extends BaseQuickAdapter<BluetoothFPVO, BaseViewHolder> {

    public BluetoothFPManageAdapter(@Nullable List<BluetoothFPVO> data) {
        super(R.layout.walletmanage_item_bluetooth_fingerprint, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, BluetoothFPVO item) {
        SuperTextView fpCrad = helper.getView(R.id.superTextView_bluetooth_FPCard);
        fpCrad.setLeftString(item.getFingerprintName());
    }
}
