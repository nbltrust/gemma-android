package com.cybex.componentservice.bean;

public class TokenBean {

    /**
     * owner : rngsbrngsb22
     * contract : ebtc
     * symbol : EBTC
     * balance : 50
     * price : 0.00383902743934
     * total_value : 0.191951371967
     * logo_url :
     */

    private String owner;
    private String contract;
    private String symbol;
    private int balance;
    private double price;
    private double total_value;
    private String logo_url;

    public TokenBean(String name, String iconUrl) {
        this.contract = name;
        this.logo_url = iconUrl;
    }
    public TokenBean() {
        logo_url = "http://img3.imgtn.bdimg.com/it/u=3853852840,331334549&fm=26&gp=0.jpg";
    }


    public String getOwner() { return owner;}

    public void setOwner(String owner) { this.owner = owner;}

    public String getContract() { return contract;}

    public void setContract(String contract) { this.contract = contract;}

    public String getSymbol() { return symbol;}

    public void setSymbol(String symbol) { this.symbol = symbol;}

    public int getBalance() { return balance;}

    public void setBalance(int balance) { this.balance = balance;}

    public double getPrice() { return price;}

    public void setPrice(double price) { this.price = price;}

    public double getTotal_value() { return total_value;}

    public void setTotal_value(double total_value) { this.total_value = total_value;}

    public String getLogo_url() { return logo_url;}

    public void setLogo_url(String logo_url) { this.logo_url = logo_url;}

//    private String name;
//
//    private String iconUrl="http://img3.imgtn.bdimg.com/it/u=3853852840,331334549&fm=26&gp=0.jpg";
//
//    public TokenBean(String name, String iconUrl) {
//        this.name = name;
//        this.iconUrl = iconUrl;
//    }
//
//    public TokenBean() {
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getIconUrl() {
//        return iconUrl;
//    }
//
//    public void setIconUrl(String iconUrl) {
//        this.iconUrl = iconUrl;
//    }
}
