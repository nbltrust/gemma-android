package com.cybex.walletmanagement.ui.adapter;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

import com.allen.library.SuperTextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cybex.walletmanagement.R;
import com.cybex.walletmanagement.ui.model.vo.BluetoothFPVO;


import java.util.List;

import me.jessyan.autosize.AutoSize;

public class BluetoothFPManageAdapter extends BaseQuickAdapter<BluetoothFPVO, BaseViewHolder> {

    private Activity activity;

    public BluetoothFPManageAdapter(@Nullable List<BluetoothFPVO> data, Activity activity) {
        super(R.layout.walletmanage_item_bluetooth_fingerprint, data);
        this.activity=activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, BluetoothFPVO item) {
        SuperTextView fpCrad = helper.getView(R.id.superTextView_bluetooth_FPCard);
        fpCrad.setLeftString(item.getFingerprintName());
    }


    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if(activity!=null){
            AutoSize.autoConvertDensityOfGlobal(activity);
        }
        super.onBindViewHolder(holder, position);
    }
}
