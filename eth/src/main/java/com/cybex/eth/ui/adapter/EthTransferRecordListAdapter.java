package com.cybex.eth.ui.adapter;

import android.graphics.Color;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cybex.eth.R;
import com.cybex.eth.ui.model.EthTransferRecord;
import com.hxlx.core.lib.utils.common.utils.DateUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;
import java.util.List;

/**
 * eth交易记录Adapter
 */
public class EthTransferRecordListAdapter extends BaseQuickAdapter<EthTransferRecord, BaseViewHolder> {

    /**
     * 1：未确认 2：正在确认 3：已确认 4: 交易失败
     */
    String currentAccountName = null;
    public static final int UN_CONFIRMED = 1;
    public static final int ING_CONFIRMED = 2;
    public static final int HAS_CONFIRMED = 3;
    public static final int TRANSACTION_FAILED = 4;

    @IntDef({UN_CONFIRMED, ING_CONFIRMED, HAS_CONFIRMED, TRANSACTION_FAILED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TradeStatus { }


    private String getCurrentStatus(@TradeStatus int STATUS) {
        String statusResult = "";
        switch (STATUS) {
            case UN_CONFIRMED:
                statusResult = mContext.getString(R.string.status_un_confirmed);
                break;
            case ING_CONFIRMED:
                statusResult = mContext.getString(R.string.status_confirmed_ing);
                break;
            case HAS_CONFIRMED:
                statusResult = mContext.getString(R.string.status_confirmed_ok);
                break;
            case TRANSACTION_FAILED:
                statusResult = mContext.getString(R.string.status_trade_failed);
                break;
            default:
                break;
        }

        return statusResult;

    }


    public EthTransferRecordListAdapter(@Nullable List<EthTransferRecord> data, String currentEosName) {
        super(R.layout.eth_item_transfer_record_list, data);
        this.currentAccountName = currentEosName;
    }

    @Override
    protected void convert(BaseViewHolder helper, EthTransferRecord item) {
        if (item != null) {

            //time header
            TextView tvHeaderDate = helper.getView(R.id.tv_header_date);
            Date date = DateUtil.strToDate(item.time, DateUtil.Format.EOS_DATE_FORMAT);
            tvHeaderDate.setText(DateUtil.date2str(date,DateUtil.Format.TRANSFER_ITEM_DATE));
            int adapterPosition = helper.getAdapterPosition();
            if(adapterPosition==0){
                tvHeaderDate.setVisibility(View.VISIBLE);
            }else {
                List<EthTransferRecord> data = getData();
                EthTransferRecord lastItem = data.get(adapterPosition - 1);
                boolean sameDay = DateUtil.isSameDay(item.time, lastItem.time, DateUtil.Format.EOS_DATE_FORMAT);
                tvHeaderDate.setVisibility(sameDay?View.GONE:View.VISIBLE);
            }

            ImageView iconArrow = helper.getView(R.id.imv_arrow);
            String account = "";
            if (!TextUtils.isEmpty(currentAccountName)) {
                if (item.from.equals(currentAccountName)||adapterPosition%2==0) {
                    //转出 -
                    account = item.to;
                    iconArrow.setImageResource(R.drawable.ic_tab_pay);
                    helper.setText(R.id.tv_transfer_amount, "-" + item.value);
                    helper.setTextColor(R.id.tv_transfer_amount, Color.parseColor("#ff3b30"));
                    helper.setText(R.id.tv_transfer_account,
                            mContext.getResources()
                                    .getString(R.string.transfer_to) + account);

                } else {
                    //转入 +
                    account = item.from;
                    iconArrow.setImageResource(R.drawable.ic_tab_income);
                    helper.setText(R.id.tv_transfer_amount, "+" + item.value);
                    helper.setTextColor(R.id.tv_transfer_amount, Color.parseColor("#4cd964"));

                    helper.setText(R.id.tv_transfer_account,
                            mContext.getResources()
                                    .getString(R.string.transfer_from) + account);
                }
            }
            helper.setText(R.id.tv_transfer_status, getCurrentStatus(item.status));
        }


    }
}
