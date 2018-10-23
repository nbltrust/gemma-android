package com.cybex.walletmanagement.ui.model;


import com.cybex.componentservice.bean.CoinType;

public class CoinTypeBean {

    public CoinType cointype;

    public boolean isChecked;

    public CoinTypeBean() {
    }

    public CoinTypeBean(CoinType cointype, boolean isChecked) {

        this.cointype = cointype;
        this.isChecked = isChecked;
    }
}
