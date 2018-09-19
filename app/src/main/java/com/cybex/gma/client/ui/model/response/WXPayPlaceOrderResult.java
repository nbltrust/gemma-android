package com.cybex.gma.client.ui.model.response;

import com.google.gson.annotations.SerializedName;

public class WXPayPlaceOrderResult {


    /**
     * code : 0
     * message : OK
     * result : {"appid":"wxae3306b1fdcca166","partnerid":"1512677831","prepayid":"wx19160420814351755a57a8503360190394","nonceStr":"dc994fad456941472a4ccd91d1800331","package":"Sign=WXPay","timestamp":1537344260,"sign":"DF83C10264F9B40C0BA468C4046FFEE5"}
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

        /**
         * appid : wxae3306b1fdcca166
         * partnerid : 1512677831
         * prepayid : wx19160420814351755a57a8503360190394
         * nonceStr : dc994fad456941472a4ccd91d1800331
         * package : Sign=WXPay
         * timestamp : 1537344260
         * sign : DF83C10264F9B40C0BA468C4046FFEE5
         */

        private String appid;
        private String partnerid;
        private String prepayid;
        private String nonceStr;
        @SerializedName("package") private String packageX;
        private int timestamp;
        private String sign;

        public String getAppid() { return appid;}

        public void setAppid(String appid) { this.appid = appid;}

        public String getPartnerid() { return partnerid;}

        public void setPartnerid(String partnerid) { this.partnerid = partnerid;}

        public String getPrepayid() { return prepayid;}

        public void setPrepayid(String prepayid) { this.prepayid = prepayid;}

        public String getNonceStr() { return nonceStr;}

        public void setNonceStr(String nonceStr) { this.nonceStr = nonceStr;}

        public String getPackageX() { return packageX;}

        public void setPackageX(String packageX) { this.packageX = packageX;}

        public int getTimestamp() { return timestamp;}

        public void setTimestamp(int timestamp) { this.timestamp = timestamp;}

        public String getSign() { return sign;}

        public void setSign(String sign) { this.sign = sign;}
    }
}
