package com.cybex.eos.ui.model.response;

public class WXPayBillResult {

    /**
     * code : 0
     * message : OK
     * result : {"cpu":"0.1950","net":"0.1950","ram":"0.4168","rmbPrice":"29.99"}
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
         * cpu : 0.1950
         * net : 0.1950
         * ram : 0.4168
         * rmbPrice : 29.99
         */

        private String cpu;
        private String net;
        private String ram;
        private String rmbPrice;

        public String getCpu() { return cpu;}

        public void setCpu(String cpu) { this.cpu = cpu;}

        public String getNet() { return net;}

        public void setNet(String net) { this.net = net;}

        public String getRam() { return ram;}

        public void setRam(String ram) { this.ram = ram;}

        public String getRmbPrice() { return rmbPrice;}

        public void setRmbPrice(String rmbPrice) { this.rmbPrice = rmbPrice;}
    }
}
