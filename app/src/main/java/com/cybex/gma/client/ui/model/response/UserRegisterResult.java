package com.cybex.gma.client.ui.model.response;

/**
 * Created by wanglin on 2018/7/31.
 */
public class UserRegisterResult {


    /**
     * code : 0
     * message : OK
     * result : {"action_id":"5c04e9aef2dffe6ecf804952"}
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
         * action_id : 5c04e9aef2dffe6ecf804952
         */

        private String action_id;

        public String getAction_id() { return action_id;}

        public void setAction_id(String action_id) { this.action_id = action_id;}
    }
}
