package com.cybex.gma.client.ui.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cybex.componentservice.db.entity.EosTransactionEntity;
import com.cybex.gma.client.R;
import com.cybex.gma.client.ui.model.response.TransferHistory;
import com.cybex.gma.client.ui.model.vo.BluetoothFPVO;

import java.util.List;

public class TransferHistoryAdapter extends BaseQuickAdapter<EosTransactionEntity, BaseViewHolder> {

    String currentEosName = null;

    public TransferHistoryAdapter(@Nullable List<EosTransactionEntity> data, String currentEosName) {
        super(R.layout.eos_item_transfer_record_list, data);
        this.currentEosName = currentEosName;
    }

    @Override
    protected void convert(BaseViewHolder helper, EosTransactionEntity item) {

    }

}
