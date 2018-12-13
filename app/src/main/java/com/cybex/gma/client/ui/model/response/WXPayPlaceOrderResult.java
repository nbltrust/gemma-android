package com.cybex.gma.client.ui.model.response;

public class WXPayPlaceOrderResult {


    /**
     * code : 0
     * message : OK
     * result : {"return_code":"SUCCESS","return_msg":"OK","appid":"wxae3306b1fdcca166","mch_id":"1512677831","nonce_str":"AYKt96FQ5n2vOSzo","sign":"5B9AD4E63D48E52DA6782BF3335384B6","result_code":"SUCCESS","prepay_id":"wx171140591407514cd2f76a8d3745628955","trade_type":"APP","action_id":"5c04e9aef2dffe6ecf804952"}
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
         * return_code : SUCCESS
         * return_msg : OK
         * appid : wxae3306b1fdcca166
         * mch_id : 1512677831
         * nonce_str : AYKt96FQ5n2vOSzo
         * sign : 5B9AD4E63D48E52DA6782BF3335384B6
         * result_code : SUCCESS
         * prepay_id : wx171140591407514cd2f76a8d3745628955
         * trade_type : APP
         * action_id : 5c04e9aef2dffe6ecf804952
         * timestamp : 1544684715
         */

        private String return_code;
        private String return_msg;
        private String appid;
        private String mch_id;
        private String nonce_str;
        private String sign;
        private String result_code;
        private String prepay_id;
        private String trade_type;
        private String action_id;

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        private String timestamp;

        public String getReturn_code() { return return_code;}

        public void setReturn_code(String return_code) { this.return_code = return_code;}

        public String getReturn_msg() { return return_msg;}

        public void setReturn_msg(String return_msg) { this.return_msg = return_msg;}

        public String getAppid() { return appid;}

        public void setAppid(String appid) { this.appid = appid;}

        public String getMch_id() { return mch_id;}

        public void setMch_id(String mch_id) { this.mch_id = mch_id;}

        public String getNonce_str() { return nonce_str;}

        public void setNonce_str(String nonce_str) { this.nonce_str = nonce_str;}

        public String getSign() { return sign;}

        public void setSign(String sign) { this.sign = sign;}

        public String getResult_code() { return result_code;}

        public void setResult_code(String result_code) { this.result_code = result_code;}

        public String getPrepay_id() { return prepay_id;}

        public void setPrepay_id(String prepay_id) { this.prepay_id = prepay_id;}

        public String getTrade_type() { return trade_type;}

        public void setTrade_type(String trade_type) { this.trade_type = trade_type;}

        public String getAction_id() { return action_id;}

        public void setAction_id(String action_id) { this.action_id = action_id;}
    }
}
