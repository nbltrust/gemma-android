package com.cybex.gma.client.ui.model.vo;

import android.os.Parcel;
import android.os.Parcelable;

public class EosTokenVO implements Parcelable {

    private String logo_url;
    private String tokenName;//contract
    private String quantity;
    private String tokenSymbol;//Symbol
    private int accuracy;


    public int getAccurancy() {
        return accuracy;
    }

    public void setAccurancy(int accurancy) {
        this.accuracy = accurancy;
    }


    public String getTokenSymbol() {
        return tokenSymbol;
    }

    public void setTokenSymbol(String tokenSymbol) {
        this.tokenSymbol = tokenSymbol;
    }

    public String getLogo_url() {
        return logo_url;
    }

    public void setLogo_url(String logo_url) {
        this.logo_url = logo_url;
    }

    public String getTokenName() {
        return tokenName;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.logo_url);
        dest.writeString(this.tokenName);
        dest.writeString(this.quantity);
        dest.writeString(this.tokenSymbol);
        dest.writeInt(this.accuracy);
    }

    public EosTokenVO() {}

    protected EosTokenVO(Parcel in) {
        this.logo_url = in.readString();
        this.tokenName = in.readString();
        this.quantity = in.readString();
        this.tokenSymbol = in.readString();
        this.accuracy = in.readInt();
    }

    public static final Parcelable.Creator<EosTokenVO> CREATOR = new Parcelable.Creator<EosTokenVO>() {
        @Override
        public EosTokenVO createFromParcel(Parcel source) {return new EosTokenVO(source);}

        @Override
        public EosTokenVO[] newArray(int size) {return new EosTokenVO[size];}
    };
}
