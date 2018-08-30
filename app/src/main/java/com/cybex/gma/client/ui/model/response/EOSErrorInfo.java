package com.cybex.gma.client.ui.model.response;

import java.util.List;

public class EOSErrorInfo {

    /**
     * code : 500
     * message : Internal Service Error
     * error : {"code":3080001,"name":"ram_usage_exceeded","what":"Account using more than allotted RAM usage","details":[]}
     */

    private int code;
    private String message;
    private ErrorBean error;

    public int getCode() { return code;}

    public void setCode(int code) { this.code = code;}

    public String getMessage() { return message;}

    public void setMessage(String message) { this.message = message;}

    public ErrorBean getError() { return error;}

    public void setError(ErrorBean error) { this.error = error;}

    public static class ErrorBean {

        /**
         * code : 3080001
         * name : ram_usage_exceeded
         * what : Account using more than allotted RAM usage
         * details : []
         */

        private int code;
        private String name;
        private String what;
        private List<?> details;

        public int getCode() { return code;}

        public void setCode(int code) { this.code = code;}

        public String getName() { return name;}

        public void setName(String name) { this.name = name;}

        public String getWhat() { return what;}

        public void setWhat(String what) { this.what = what;}

        public List<?> getDetails() { return details;}

        public void setDetails(List<?> details) { this.details = details;}
    }
}
