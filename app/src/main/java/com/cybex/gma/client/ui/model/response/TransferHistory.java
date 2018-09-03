package com.cybex.gma.client.ui.model.response;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 交易记录对象
 *
 * Created by wanglin on 2018/7/12.
 */
public class TransferHistory implements Parcelable {

    public int action_seq;
    public String from;
    public String to;
    public String value;//交易金额
    public String memo;
    public String time;//交易时间
    public String hash;//交易哈希 txid
    public int block;//交易区块号
    /**
     * 交易状态：1：未确认 2：正在确认 3：已确认 4: 交易失败
     */
    public int status;
    public int last_pos;

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.action_seq);
        dest.writeString(this.from);
        dest.writeString(this.to);
        dest.writeString(this.value);
        dest.writeString(this.memo);
        dest.writeString(this.time);
        dest.writeString(this.hash);
        dest.writeInt(this.block);
        dest.writeInt(this.status);
        dest.writeInt(this.last_pos);
    }

    public TransferHistory() {}

    protected TransferHistory(Parcel in) {
        this.action_seq = in.readInt();
        this.from = in.readString();
        this.to = in.readString();
        this.value = in.readString();
        this.memo = in.readString();
        this.time = in.readString();
        this.hash = in.readString();
        this.block = in.readInt();
        this.status = in.readInt();
        this.last_pos = in.readInt();
    }

    public static final Parcelable.Creator<TransferHistory> CREATOR = new Parcelable.Creator<TransferHistory>() {
        @Override
        public TransferHistory createFromParcel(Parcel source) {return new TransferHistory(source);}

        @Override
        public TransferHistory[] newArray(int size) {return new TransferHistory[size];}
    };
}
