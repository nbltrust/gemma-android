package com.cybex.gma.client.ui.model.vo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 投票节点VO对象
 *
 * Created by wanglin on 2018/8/14.
 */
public class VoteNodeVO implements Parcelable {

    public boolean ischecked = false;//是否被选择

    private String account;//账户名
    private String alias;//别名
    private String votes;
    private String url;//地址
    private String percentage;//占票百分比
    private int ranking;//排名


    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getVotes() {
        return votes;
    }

    public void setVotes(String votes) {
        this.votes = votes;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.ischecked ? (byte) 1 : (byte) 0);
        dest.writeString(this.account);
        dest.writeString(this.alias);
        dest.writeString(this.votes);
        dest.writeString(this.url);
        dest.writeString(this.percentage);
        dest.writeInt(this.ranking);
    }

    public VoteNodeVO() {}

    protected VoteNodeVO(Parcel in) {
        this.ischecked = in.readByte() != 0;
        this.account = in.readString();
        this.alias = in.readString();
        this.votes = in.readString();
        this.url = in.readString();
        this.percentage = in.readString();
        this.ranking = in.readInt();
    }

    public static final Parcelable.Creator<VoteNodeVO> CREATOR = new Parcelable.Creator<VoteNodeVO>() {
        @Override
        public VoteNodeVO createFromParcel(Parcel source) {return new VoteNodeVO(source);}

        @Override
        public VoteNodeVO[] newArray(int size) {return new VoteNodeVO[size];}
    };
}
