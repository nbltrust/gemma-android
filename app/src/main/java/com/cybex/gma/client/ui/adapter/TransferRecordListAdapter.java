package com.cybex.gma.client.ui.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cybex.gma.client.R;
import com.cybex.gma.client.ui.model.response.TransferHistory;

import java.util.List;

/**
 * 交易记录Adapter
 *
 * Created by wanglin on 2018/7/12.
 */
public class  TransferRecordListAdapter extends BaseQuickAdapter<TransferHistory, BaseViewHolder> {
    String currentEosName = null;

    public TransferRecordListAdapter(@Nullable List<TransferHistory> data,String currentEosName) {
        super(R.layout.item_transfer_record_list, data);
        this.currentEosName = currentEosName;
    }

    @Override
    protected void convert(BaseViewHolder helper, TransferHistory item) {
        helper.setText(R.id.tv_transfer_account,item.to);
        helper.setText(R.id.tv_transfer_amount,item.value);
        helper.setText(R.id.tv_transfer_status,"转账中");
        helper.setText(R.id.tv_transfer_date,item.time);

    }
}
