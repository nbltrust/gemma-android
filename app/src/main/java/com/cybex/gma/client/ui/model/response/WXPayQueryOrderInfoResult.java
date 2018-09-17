package com.cybex.gma.client.ui.model.response;

public class WXPayQueryOrderInfoResult {

    /**
     * code : 0
     * message : OK
     * result : {"notified":false,"_id":"5b9f216c585f7a489aa3c8c7","biz_type":"PAY","pay_state":"NOTPAY","status":"INIT","account":"123423452341","public_key":"EOS7zAEsW9TEjmFYFQe3T8xhytJfumit9PEp5QfgtBMfdgtA683ko","platform":"iOS","client_ip":"43.230.89.74","cpu":"0.1556","net":"0.1556","ram":"0.4068","rmb_price":"29.99","expiration":"1537156036702","create_at":"2018-09-17T03:37:16.705Z","update_at":"2018-09-17T03:40:59.275Z","__v":0,"prepay_id":"wx171140591407514cd2f76a8d3745628955"}
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
         * notified : false
         * _id : 5b9f216c585f7a489aa3c8c7
         * biz_type : PAY
         * pay_state : NOTPAY
         * status : INIT
         * account : 123423452341
         * public_key : EOS7zAEsW9TEjmFYFQe3T8xhytJfumit9PEp5QfgtBMfdgtA683ko
         * platform : iOS
         * client_ip : 43.230.89.74
         * cpu : 0.1556
         * net : 0.1556
         * ram : 0.4068
         * rmb_price : 29.99
         * expiration : 1537156036702
         * create_at : 2018-09-17T03:37:16.705Z
         * update_at : 2018-09-17T03:40:59.275Z
         * __v : 0
         * prepay_id : wx171140591407514cd2f76a8d3745628955
         */

        private boolean notified;
        private String _id;
        private String biz_type;
        private String pay_state;
        private String status;
        private String account;
        private String public_key;
        private String platform;
        private String client_ip;
        private String cpu;
        private String net;
        private String ram;
        private String rmb_price;
        private String expiration;
        private String create_at;
        private String update_at;
        private int __v;
        private String prepay_id;

        public boolean isNotified() { return notified;}

        public void setNotified(boolean notified) { this.notified = notified;}

        public String get_id() { return _id;}

        public void set_id(String _id) { this._id = _id;}

        public String getBiz_type() { return biz_type;}

        public void setBiz_type(String biz_type) { this.biz_type = biz_type;}

        public String getPay_state() { return pay_state;}

        public void setPay_state(String pay_state) { this.pay_state = pay_state;}

        public String getStatus() { return status;}

        public void setStatus(String status) { this.status = status;}

        public String getAccount() { return account;}

        public void setAccount(String account) { this.account = account;}

        public String getPublic_key() { return public_key;}

        public void setPublic_key(String public_key) { this.public_key = public_key;}

        public String getPlatform() { return platform;}

        public void setPlatform(String platform) { this.platform = platform;}

        public String getClient_ip() { return client_ip;}

        public void setClient_ip(String client_ip) { this.client_ip = client_ip;}

        public String getCpu() { return cpu;}

        public void setCpu(String cpu) { this.cpu = cpu;}

        public String getNet() { return net;}

        public void setNet(String net) { this.net = net;}

        public String getRam() { return ram;}

        public void setRam(String ram) { this.ram = ram;}

        public String getRmb_price() { return rmb_price;}

        public void setRmb_price(String rmb_price) { this.rmb_price = rmb_price;}

        public String getExpiration() { return expiration;}

        public void setExpiration(String expiration) { this.expiration = expiration;}

        public String getCreate_at() { return create_at;}

        public void setCreate_at(String create_at) { this.create_at = create_at;}

        public String getUpdate_at() { return update_at;}

        public void setUpdate_at(String update_at) { this.update_at = update_at;}

        public int get__v() { return __v;}

        public void set__v(int __v) { this.__v = __v;}

        public String getPrepay_id() { return prepay_id;}

        public void setPrepay_id(String prepay_id) { this.prepay_id = prepay_id;}
    }
}
