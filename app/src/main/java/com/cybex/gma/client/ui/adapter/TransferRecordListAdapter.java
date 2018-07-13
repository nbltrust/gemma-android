package com.cybex.gma.client.ui.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cybex.gma.client.R;
import com.cybex.gma.client.ui.model.response.TransferRecord;

import java.util.List;

/**
 * 交易记录Adapter
 *
 * Created by wanglin on 2018/7/12.
 */
public class TransferRecordListAdapter extends BaseQuickAdapter<TransferRecord, BaseViewHolder> {

    public TransferRecordListAdapter(@Nullable List<TransferRecord> data) {
        super(R.layout.item_transfer_record_list, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, TransferRecord item) {

    }
}