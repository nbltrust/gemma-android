package com.cybex.gma.client.ui.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cybex.componentservice.db.entity.EosTransactionEntity;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.ParamConstants;
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
        tv_date.setTextColor(mContext.getResources().getColor(R.color.black_context));

        //time header
        Date date = DateUtil.strToDate(item.getDate(), DateUtil.Format.EOS_DATE_FORMAT);
        tv_date.setText(DateUtil.date2str(date,DateUtil.Format.TRANSFER_ITEM_DATE));
        int adapterPosition = helper.getAdapterPosition();
        if (adapterPosition == 0) {
            tv_date.setVisibility(View.VISIBLE);
        } else {
            List<EosTransactionEntity> data = getData();
            EosTransactionEntity lastItem = data.get(adapterPosition - 1);
            boolean sameDay = DateUtil.isSameDay(item.getDate(), lastItem.getDate(), DateUtil.Format.EOS_DATE_FORMAT);
            tv_date.setVisibility(sameDay ? View.GONE : View.VISIBLE);
        }


        if (currentEosName != null) {
            if (item.getStatus() != null) {
                if (item.getStatus() == ParamConstants.TRANSACTION_STATUS_CONFIRMED) {
                    //已确认

                    if (item.getSender().equals(currentEosName) || adapterPosition % 2 == 0) {
                        //支出
                        tv_account.setText(
                                mContext.getResources().getString(R.string.transfer_to) + item.getReceiver());
                        tv_status.setText(R.string.status_sent);
                        iv_arrow.setImageResource(R.drawable.ic_tab_pay);
                        tv_quantity.setText("-" + item.getQuantity() + " " + item.getTokenSymbol());
                        tv_quantity.setTextColor(mContext.getResources().getColor(R.color.scarlet));

                    } else {
                        //收入
                        tv_account.setText(
                                mContext.getResources().getString(R.string.transfer_from) + item.getSender());
                        tv_status.setText(R.string.status_accepted);
                        iv_arrow.setImageResource(R.drawable.ic_tab_income);
                        tv_quantity.setText("+" + item.getQuantity() + " " + item.getTokenSymbol());
                        tv_quantity.setTextColor(mContext.getResources().getColor(R.color.success_color));
                    }
                } else if (item.getStatus() ==ParamConstants.TRANSACTION_STATUS_FAIL) {
                    //失败
                    if (item.getSender().equals(currentEosName) || adapterPosition % 2 == 0) {
                        //支出
                        tv_status.setText(R.string.status_send_fail);
                        tv_account.setText(
                                mContext.getResources().getString(R.string.transfer_to) + item.getReceiver());
                        iv_arrow.setImageResource(R.drawable.ic_tab_pay);
                        tv_quantity.setText("-" + item.getQuantity() + " " + item.getTokenSymbol());
                    } else {
                        //收入
                        tv_status.setText(R.string.status_accept_fail);
                        tv_account.setText(
                                mContext.getResources().getString(R.string.transfer_from) + item.getSender());
                        iv_arrow.setImageResource(R.drawable.ic_tab_income);
                        tv_quantity.setText("+" + item.getQuantity() + " " + item.getTokenSymbol());
                    }
                } else {
                    //确认中

                    if (item.getSender().equals(currentEosName) || adapterPosition % 2 == 0) {
                        //支出
                        tv_account.setText(
                                mContext.getResources().getString(R.string.transfer_to) + item.getReceiver());
                        iv_arrow.setImageResource(R.drawable.ic_tab_pay);
                        tv_quantity.setText("-" + item.getQuantity() + " " + item.getTokenSymbol());
                        tv_quantity.setTextColor(mContext.getResources().getColor(R.color.scarlet));
                    } else {
                        //收入
                        tv_account.setText(
                                mContext.getResources().getString(R.string.transfer_from) + item.getSender());
                        iv_arrow.setImageResource(R.drawable.ic_tab_income);
                        tv_quantity.setText("+" + item.getQuantity() + " " + item.getTokenSymbol());
                        tv_quantity.setTextColor(mContext.getResources().getColor(R.color.success_color));
                    }
                    tv_status.setText(R.string.status_confirmed_ing);
                }
            }else {
                //getStatus 为空，可能某个异步获取blockNum请求失败
                tv_status.setText(R.string.status_unknown);
                if (item.getSender().equals(currentEosName) || adapterPosition % 2 == 0) {
                    //支出
                    tv_account.setText(
                            mContext.getResources().getString(R.string.transfer_to) + item.getReceiver());
                    iv_arrow.setImageResource(R.drawable.ic_tab_pay);
                    tv_quantity.setText("-" + item.getQuantity() + " " + item.getTokenSymbol());
                    tv_quantity.setTextColor(mContext.getResources().getColor(R.color.scarlet));
                } else {
                    //收入
                    tv_account.setText(
                            mContext.getResources().getString(R.string.transfer_from) + item.getSender());
                    iv_arrow.setImageResource(R.drawable.ic_tab_income);
                    tv_quantity.setText("+" + item.getQuantity() + " " + item.getTokenSymbol());
                    tv_quantity.setTextColor(mContext.getResources().getColor(R.color.success_color));
                }
            }
        }
    }


}
