package com.cybex.gma.client.ui.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CheckedTextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cybex.gma.client.R;
import com.cybex.gma.client.ui.model.vo.BluetoothDeviceVO;
import com.cybex.gma.client.widget.underline.UnderlineTextView;
import com.github.ybq.android.spinkit.SpinKitView;
import com.hxlx.core.lib.utils.EmptyUtils;

import java.util.List;

/**
 * Created by wanglin on 2018/9/3.
 */
public class BluetoothScanDeviceListAdapter extends BaseQuickAdapter<BluetoothDeviceVO, BaseViewHolder> {


    public BluetoothScanDeviceListAdapter(@Nullable List<BluetoothDeviceVO> data) {
        super(R.layout.item_bluetooth_scan_device_name, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, BluetoothDeviceVO item) {
        CheckedTextView tv = helper.getView(R.id.tv_device_name);
        SpinKitView vSpit = helper.getView(R.id.view_progress);
        UnderlineTextView vStatus = helper.getView(R.id.tv_status);
        helper.addOnClickListener(R.id.view_child_item);


        if (EmptyUtils.isNotEmpty(item)) {
            tv.setText(item.deviceName);

            if (EmptyUtils.isEmpty(item.status)) {
                vStatus.setVisibility(View.GONE);
            } else {
                vStatus.setVisibility(View.VISIBLE);
            }

            if (item.isShowProgress) {
                vSpit.setVisibility(View.VISIBLE);
            } else {
                vSpit.setVisibility(View.GONE);
            }
        }

    }
}
