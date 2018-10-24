package com.cybex.walletmanagement.ui.model;

import com.cybex.componentservice.bean.CoinType;

import java.io.Serializable;

public class ImportWalletConfigBean implements Serializable{

    private int walletType;//导入的钱包类型
    private String mnemonic;//如果钱包类型为助记词类型，使用此字段
    private String priKey;//如果钱包类型为私钥类型，字段有效
    private CoinType coinType;//如果钱包类型为私钥类型，此字段用作保存导入的私钥种类

    public int getWalletType() {
        return walletType;
    }

    public void setWalletType(int walletType) {
        this.walletType = walletType;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    public String getPriKey() {
        return priKey;
    }

    public void setPriKey(String priKey) {
        this.priKey = priKey;
    }

    public CoinType getCoinType() {
        return coinType;
    }

    @Override
    public String toString() {
        return "ImportWalletConfigBean{" +
                "walletType=" + walletType +
                ", mnemonic='" + mnemonic + '\'' +
                ", priKey='" + priKey + '\'' +
                ", coinType=" + coinType +
                '}';
    }

    public void setCoinType(CoinType coinType) {
        this.coinType = coinType;
    }
}
