package com.cybex.gma.client.ui.model.response;

import com.google.gson.annotations.SerializedName;

public class WXPayPlaceOrderResult {

    /**
     * code : 0
     * message : OK
     * result : {"appid":"wxae3306b1fdcca166","prepayid":"wx171140591407514cd2f76a8d3745628955","partnerid":"partnerid","mch_id":"1512677831","nonceStr":"AYKt96FQ5n2vOSzo","package":"Sign=WXPay","timestamp":1544684715,"sign":"5B9AD4E63D48E52DA6782BF3335384B6","action_id":"5c04e9aef2dffe6ecf804952 "}
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
         * prepayid : wx171140591407514cd2f76a8d3745628955
         * partnerid : partnerid
         * mch_id : 1512677831
         * nonceStr : AYKt96FQ5n2vOSzo
         * package : Sign=WXPay
         * timestamp : 1544684715
         * sign : 5B9AD4E63D48E52DA6782BF3335384B6
         * action_id : 5c04e9aef2dffe6ecf804952
         */

        private String appid;
        private String prepayid;
        private String partnerid;
        private String mch_id;
        private String nonceStr;
        @SerializedName("package") private String packageX;
        private int timestamp;
        private String sign;
        private String action_id;

        public String getAppid() { return appid;}

        public void setAppid(String appid) { this.appid = appid;}

        public String getPrepayid() { return prepayid;}

        public void setPrepayid(String prepayid) { this.prepayid = prepayid;}

        public String getPartnerid() { return partnerid;}

        public void setPartnerid(String partnerid) { this.partnerid = partnerid;}

        public String getMch_id() { return mch_id;}

        public void setMch_id(String mch_id) { this.mch_id = mch_id;}

        public String getNonceStr() { return nonceStr;}

        public void setNonceStr(String nonceStr) { this.nonceStr = nonceStr;}

        public String getPackageX() { return packageX;}

        public void setPackageX(String packageX) { this.packageX = packageX;}

        public int getTimestamp() { return timestamp;}

        public void setTimestamp(int timestamp) { this.timestamp = timestamp;}

        public String getSign() { return sign;}

        public void setSign(String sign) { this.sign = sign;}

        public String getAction_id() { return action_id;}

        public void setAction_id(String action_id) { this.action_id = action_id;}
    }
}
