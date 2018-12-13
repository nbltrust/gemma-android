package com.cybex.gma.client.ui.model.request;

public class CreateNewEosAccountReqParams {

    /**
     * app_id : 1
     * goods_id : 1002
     * code : serial nubme
     * account_name : daizong12345
     * public_key : EOS6ft4SjHACJPRaRu1SVUUQg4cTM9VaEiqKUto1HdmoKSD3CgBwe
     * validation : {"SN":"serial nubmer","SN_sig":"snnnnnnn","public_key":"public_key","public_key_sig":"public_key_sig"}
     */

    private int app_id;
    private int goods_id;
    private String code;
    private String account_name;
    private String public_key;
    private ValidationBean validation;

    public int getApp_id() { return app_id;}

    public void setApp_id(int app_id) { this.app_id = app_id;}

    public int getGoods_id() { return goods_id;}

    public void setGoods_id(int goods_id) { this.goods_id = goods_id;}

    public String getCode() { return code;}

    public void setCode(String code) { this.code = code;}

    public String getAccount_name() { return account_name;}

    public void setAccount_name(String account_name) { this.account_name = account_name;}

    public String getPublic_key() { return public_key;}

    public void setPublic_key(String public_key) { this.public_key = public_key;}

    public ValidationBean getValidation() { return validation;}

    public void setValidation(ValidationBean validation) { this.validation = validation;}

    public static class ValidationBean {

        /**
         * SN : serial nubmer
         * SN_sig : snnnnnnn
         * public_key : public_key
         * public_key_sig : public_key_sig
         */

        private String SN;
        private String SN_sig;
        private String public_key;
        private String public_key_sig;

        public String getSN() { return SN;}

        public void setSN(String SN) { this.SN = SN;}

        public String getSN_sig() { return SN_sig;}

        public void setSN_sig(String SN_sig) { this.SN_sig = SN_sig;}

        public String getPublic_key() { return public_key;}

        public void setPublic_key(String public_key) { this.public_key = public_key;}

        public String getPublic_key_sig() { return public_key_sig;}

        public void setPublic_key_sig(String public_key_sig) { this.public_key_sig = public_key_sig;}
    }
}
