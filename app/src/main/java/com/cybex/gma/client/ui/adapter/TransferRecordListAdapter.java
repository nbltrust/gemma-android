package com.cybex.gma.client.ui.adapter;

import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cybex.gma.client.R;
import com.cybex.gma.client.ui.model.response.TransferHistory;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * 交易记录Adapter
 *
 * Created by wanglin on 2018/7/12.
 */
public class TransferRecordListAdapter extends BaseQuickAdapter<TransferHistory, BaseViewHolder> {

    /**
     * 1：未确认 2：正在确认 3：已确认 4: 交易失败
     */
    String currentEosName = null;
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


    public TransferRecordListAdapter(@Nullable List<TransferHistory> data, String currentEosName) {
        super(R.layout.eos_item_transfer_record_list, data);
        this.currentEosName = currentEosName;
    }

    @Override
    protected void convert(BaseViewHolder helper, TransferHistory item) {
        if (item != null) {
            ImageView iconArrow = helper.getView(R.id.imv_arrow);
            String account = "";
            if (!TextUtils.isEmpty(currentEosName)) {
                if (item.from.equals(currentEosName)) {
                    //转出 -
                    account = item.to;
                    iconArrow.setImageResource(R.drawable.icon_arrow_up);
                    helper.setText(R.id.tv_transfer_amount, "-" + item.value);

                } else {
                    //转入 +
                    account = item.from;
                    iconArrow.setImageResource(R.drawable.icon_arrow_down);
                    helper.setText(R.id.tv_transfer_amount, "+" + item.value);
                }


                helper.setText(R.id.tv_transfer_account,
                        mContext.getResources()
                                .getString(R.string.eos_at_char) + account);
            }


            helper.setText(R.id.tv_transfer_status, getCurrentStatus(item.status));
            helper.setText(R.id.tv_transfer_date, item.time);
        }


    }
}
