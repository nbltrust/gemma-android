package com.cybex.gma.client.ui.model;

/**
 * Created by wanglin on 2018/7/11.
 */
public class AccountInfo {

    private final int app_id = 1;
    private String mAccount_name;//EOS账户名称
    private String mInvitation_code;//6位邀请码
    private String mPublic_key;//公钥

    public AccountInfo(){

    }
    /*
    public AccountInfo(String account_name, String invitation_code, String public_key){
        this.mAccount_name = account_name;
        this.mInvitation_code = invitation_code;
        this.mPublic_key = public_key;
    }*/

    public void setmAccount_name(String mAccount_name) {
        this.mAccount_name = mAccount_name;
    }

    public void setmInvitation_code(String mInvitation_code) {
        this.mInvitation_code = mInvitation_code;
    }

    public void setmPublic_key(String mPublic_key) {
        this.mPublic_key = mPublic_key;
    }

    public String getmAccount_name() {
        return mAccount_name;
    }

    public String getmInvitation_code() {
        return mInvitation_code;
    }

    public String getmPublic_key() {
        return mPublic_key;
    }
}
