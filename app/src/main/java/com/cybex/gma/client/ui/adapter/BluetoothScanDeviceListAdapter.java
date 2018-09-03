package com.cybex.gma.client.ui.adapter;

import android.support.annotation.Nullable;
import android.widget.CheckedTextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cybex.gma.client.R;
import com.hxlx.core.lib.utils.EmptyUtils;

import java.util.List;

/**
 * Created by wanglin on 2018/9/3.
 */
public class BluetoothScanDeviceListAdapter extends BaseQuickAdapter<String, BaseViewHolder> {


    public BluetoothScanDeviceListAdapter(@Nullable List<String> data) {
        super(R.layout.item_bluetooth_scan_device_name, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        CheckedTextView tv = helper.getView(R.id.tv_account_name);
        if (EmptyUtils.isNotEmpty(item)) {
            tv.setText(item);
        }

    }
}
