package com.cybex.walletmanagement.event;

import com.cybex.componentservice.bean.CoinType;

/**
 * 导入私钥时，选择导入的币种 事件
 */
public class SelectCoinTypeEvent {
    public CoinType cointype;

    public SelectCoinTypeEvent() {
    }

    public SelectCoinTypeEvent(CoinType cointype) {

        this.cointype = cointype;
    }
}
