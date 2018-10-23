package com.cybex.eos.ui.model.request;

/**
 * Created by wanglin on 2018/8/1.
 */
public class GetCurrencyBalanceReqParams {

    private String code;    //合约
    private String account; //账户名
    private String symbol;  //资产

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
