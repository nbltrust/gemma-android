package com.cybex.componentservice.bean;


public enum CoinType {
    EOS("EOS"),ETH("ETH");

    public String coinName;

    CoinType(String coinName) {
        this.coinName = coinName;
    }
}
