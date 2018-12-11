package com.cybex.gma.client.ui.model.request;

public class DelegateReqParams {

    private int app_id;
    private int goods_id;
    private String code;
    private String account_name;

    public DelegateValidation getValidation() {
        return validation;
    }

    public void setValidation(DelegateValidation validation) {
        this.validation = validation;
    }

    private DelegateValidation validation;

    public int getApp_id() {
        return app_id;
    }

    public void setApp_id(int app_id) {
        this.app_id = app_id;
    }

    public int getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(int goods_id) {
        this.goods_id = goods_id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAccount_name() {
        return account_name;
    }

    public void setAccount_name(String account_name) {
        this.account_name = account_name;
    }

    public static class DelegateValidation {
        /**
         * wookong的SN
         */
        private String SN;
        /**
         * SN签名
         */
        private String SN_sig;

        public String getSN() {
            return SN;
        }

        public void setSN(String SN) {
            this.SN = SN;
        }

        public String getSN_sig() {
            return SN_sig;
        }

        public void setSN_sig(String SN_sig) {
            this.SN_sig = SN_sig;
        }
    }

}
