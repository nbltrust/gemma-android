package com.cybex.gma.client.ui.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cybex.componentservice.db.entity.EosTransactionEntity;
import com.cybex.gma.client.R;

import com.hxlx.core.lib.utils.common.utils.DateUtil;

import java.util.Date;
import java.util.List;

public class TransferHistoryAdapter extends BaseQuickAdapter<EosTransactionEntity, BaseViewHolder> {

    String currentEosName = null;

    public TransferHistoryAdapter(@Nullable List<EosTransactionEntity> data, String currentEosName) {
        super(R.layout.eos_item_transfer_record_list, data);
        this.currentEosName = currentEosName;
    }

    @Override
    protected void convert(BaseViewHolder helper, EosTransactionEntity item) {

        TextView tv_status = helper.getView(R.id.tv_transfer_status);
        TextView tv_account = helper.getView(R.id.tv_transfer_account);
        TextView tv_quantity = helper.getView(R.id.tv_transfer_amount);

        ImageView iv_arrow = helper.getView(R.id.imv_arrow);
        TextView tv_date = helper.getView(R.id.tv_header_date);

        //time header
//        Date date = DateUtil.strToDate(item.getDate(), DateUtil.Format.EOS_DATE_FORMAT);
//        tv_date.setText(DateUtil.date2str(date,DateUtil.Format.TRANSFER_ITEM_DATE));
        int adapterPosition = helper.getAdapterPosition();
        if(adapterPosition==0){
            tv_date.setVisibility(View.VISIBLE);
        }else {
            List<EosTransactionEntity> data = getData();
            EosTransactionEntity lastItem = data.get(adapterPosition - 1);
            boolean sameDay = DateUtil.isSameDay(item.getDate(), lastItem.getDate(), DateUtil.Format.EOS_DATE_FORMAT);
            tv_date.setVisibility(sameDay?View.GONE:View.VISIBLE);
        }

        if (item.getStatus() == 1){
            //已确认
            if (item.getTransferType() == 1){
                //支出

            }else {
                //收入
            }
        }else if (item.getStatus() == 2){
            //确认中
            tv_status.setText(R.string.status_confirmed_ing);
        }else {
            //失败
            if (item.getTransferType() == 1){
                //支出
                tv_status.setText(R.string.status_send_fail);
            }else {
                //收入
                tv_status.setText(R.string.status_accept_fail);
            }

        }
    }

}
