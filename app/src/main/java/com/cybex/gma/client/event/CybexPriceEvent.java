package com.cybex.gma.client.event;

/**
 * 从Cybex上获取的币种法币价值估值
 */
public class CybexPriceEvent {
    private String eosPrice;

    public String getEosPrice() {
        return eosPrice;
    }

    public void setEosPrice(String eosPrice) {
        this.eosPrice = eosPrice;
    }

    public String getUsdtPrice() {
        return usdtPrice;
    }

    public void setUsdtPrice(String usdtPrice) {
        this.usdtPrice = usdtPrice;
    }

    private String usdtPrice;
}
