package com.cybex.gma.client.ui.model.response;

import java.util.List;
import com.cybex.componentservice.bean.TokenBean;

public class GetEosTokensResult {

    /**
     * code : 0
     * message : OK
     * result : {"tokens":[{"owner":"heytcmbqgage","contract":"eosadddddddd","symbol":"ADD","balance":683726.5889,"price":9.641854882799999E-4,"logo_url":""},{"owner":"heytcmbqgage","contract":"eosiochaince","symbol":"CET","balance":500000,"price":0.022334240124,"logo_url":"https://eosauthority.com/common/images/airdrops_CET.png"}]}
     */

    private int code;
    private String message;
    private ResultBean result;

    public int getCode() { return code;}

    public void setCode(int code) { this.code = code;}

    public String getMessage() { return message;}

    public void setMessage(String message) { this.message = message;}

    public ResultBean getResult() { return result;}

    public void setResult(ResultBean result) { this.result = result;}

    public static class ResultBean {

        private List<TokenBean> tokens;

        public List<TokenBean> getTokens() { return tokens;}

        public void setTokens(List<TokenBean> tokens) { this.tokens = tokens;}

//        public static class TokensBean {
//
//            /**
//             * owner : heytcmbqgage
//             * contract : eosadddddddd
//             * symbol : ADD
//             * balance : 683726.5889
//             * price : 9.641854882799999E-4
//             * logo_url :
//             */
//
//            private String owner;
//            private String contract;
//            private String symbol;
//            private double balance;
//            private double price;
//            private String logo_url;
//
//            public String getOwner() { return owner;}
//
//            public void setOwner(String owner) { this.owner = owner;}
//
//            public String getCode() { return contract;}
//
//            public void setCode(String contract) { this.contract = contract;}
//
//            public String getSymbol() { return symbol;}
//
//            public void setSymbol(String symbol) { this.symbol = symbol;}
//
//            public double getBalance() { return balance;}
//
//            public void setBalance(double balance) { this.balance = balance;}
//
//            public double getPrice() { return price;}
//
//            public void setPrice(double price) { this.price = price;}
//
//            public String getLogo_url() { return logo_url;}
//
//            public void setLogo_url(String logo_url) { this.logo_url = logo_url;}
//        }
    }
}
