package com.cybex.eos.ui.model.vo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wanglin on 2018/9/18.
 */
public class BluetoothAccountInfoVO implements Parcelable {

    private String password;

    private String passwordTip;

    private String accountName;


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordTip() {
        return passwordTip;
    }

    public void setPasswordTip(String passwordTip) {
        this.passwordTip = passwordTip;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.password);
        dest.writeString(this.passwordTip);
        dest.writeString(this.accountName);
    }

    public BluetoothAccountInfoVO() {}

    protected BluetoothAccountInfoVO(Parcel in) {
        this.password = in.readString();
        this.passwordTip = in.readString();
        this.accountName = in.readString();
    }

    public static final Creator<BluetoothAccountInfoVO> CREATOR = new Creator<BluetoothAccountInfoVO>() {
        @Override
        public BluetoothAccountInfoVO createFromParcel(Parcel source) {return new BluetoothAccountInfoVO(source);}

        @Override
        public BluetoothAccountInfoVO[] newArray(int size) {return new BluetoothAccountInfoVO[size];}
    };
}
