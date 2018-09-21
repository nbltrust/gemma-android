package com.cybex.gma.client.ui.model.request;

/**
 * 用户注册请求的参数
 *
 * Created by wanglin on 2018/7/12.
 */
public class UserRegisterReqParams {

    /**
     * 访问方ID，1:Gemma 2:Wookong solo 3: Wookong 4.蓝牙卡
     */
    private int app_id;

    /**
     * 用户名
     */
    private String account_name;

    /**
     * 邀请码
     */
    private String invitation_code;

    /**
     * 公钥
     */
    private String public_key;
    /**
     * hash值
     */
    private String txId;


    public int getApp_id() {
        return app_id;
    }

    public void setApp_id(int app_id) {
        this.app_id = app_id;
    }

    public String getAccount_name() {
        return account_name;
    }

    public void setAccount_name(String account_name) {
        this.account_name = account_name;
    }

    public String getInvitation_code() {
        return invitation_code;
    }

    public void setInvitation_code(String invitation_code) {
        this.invitation_code = invitation_code;
    }

    public String getPublic_key() {
        return public_key;
    }

    public void setPublic_key(String public_key) {
        this.public_key = public_key;
    }

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }
}
