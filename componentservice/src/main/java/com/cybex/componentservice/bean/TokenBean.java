package com.cybex.componentservice.bean;

public class TokenBean {

    /**
     * symbol : EOS
     * code : eosio.token
     * balance : 62.1213
     * logo_url : https://eosauthority.com/common/images/airdrops_EOS.png
     */

    private String symbol;
    private String code;
    private String balance;
    private String logo_url;

    public String getSymbol() { return symbol;}

    public void setSymbol(String symbol) { this.symbol = symbol;}

    public String getCode() { return code;}

    public void setCode(String code) { this.code = code;}

    public String getBalance() { return balance;}

    public void setBalance(String balance) { this.balance = balance;}

    public String getLogo_url() { return logo_url;}

    public void setLogo_url(String logo_url) { this.logo_url = logo_url;}


    public TokenBean(String name, String iconUrl) {
        this.symbol = name;
        this.logo_url = iconUrl;
    }
    public TokenBean() {


    }

}
