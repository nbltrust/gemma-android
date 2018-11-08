package com.cybex.gma.client.ui.model.response;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 交易记录对象
 *
 */
public class TransferHistory implements Parcelable {

    public String trx_id;//交易哈希 txid
    public String timestamp;//交易时间戳
    public String receiver;
    public String sender;
    public String code;//代币合约
    public String quantity;//交易金额
    public String memo;
    public String symbol;//代币名称
    public String status;

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.trx_id);
        dest.writeString(this.timestamp);
        dest.writeString(this.receiver);
        dest.writeString(this.sender);
        dest.writeString(this.code);
        dest.writeString(this.quantity);
        dest.writeString(this.memo);
        dest.writeString(this.symbol);
        dest.writeString(this.status);
    }

    public TransferHistory() {}

    protected TransferHistory(Parcel in) {
        this.trx_id = in.readString();
        this.timestamp = in.readString();
        this.receiver = in.readString();
        this.sender = in.readString();
        this.code = in.readString();
        this.quantity = in.readString();
        this.memo = in.readString();
        this.symbol = in.readString();
        this.status = in.readString();
    }

    public static final Parcelable.Creator<TransferHistory> CREATOR = new Parcelable.Creator<TransferHistory>() {
        @Override
        public TransferHistory createFromParcel(Parcel source) {return new TransferHistory(source);}

        @Override
        public TransferHistory[] newArray(int size) {return new TransferHistory[size];}
    };
}
