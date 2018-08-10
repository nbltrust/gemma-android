package com.cybex.gma.client.ui.model.vo;

import com.cybex.gma.client.ui.model.response.AccountInfo;

/**
 * 首页聚合数据Data
 *
 * Created by wanglin on 2018/8/10.
 */
public class HomeCombineDataVO {

    private AccountInfo accountInfo;
    private String unitPrice;
    private String banlance;

    public AccountInfo getAccountInfo() {
        return accountInfo;
    }

    public void setAccountInfo(AccountInfo accountInfo) {
        this.accountInfo = accountInfo;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getBanlance() {
        return banlance;
    }

    public void setBanlance(String banlance) {
        this.banlance = banlance;
    }
}
