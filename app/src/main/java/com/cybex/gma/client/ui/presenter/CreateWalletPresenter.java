package com.cybex.gma.client.ui.presenter;

import com.cybex.gma.client.ui.activity.CreateWalletActivity;
import com.cybex.gma.client.ui.model.AccountInfo;
import com.cybex.gma.client.ui.request.GetAccountinfoRequest;
import com.hxlx.core.lib.common.cache.CacheUtil;
import com.hxlx.core.lib.mvp.lite.XPresenter;

public class CreateWalletPresenter extends XPresenter<CreateWalletActivity> {

    private String[] keyPair = new String[2];//公私钥对
    private String pubKey;//公钥
    private String priKey;//私钥
    private String invCode;
    private AccountInfo newAccount;

    private CreateWalletActivity curActivity = getV();
    private GetAccountinfoRequest getAccountinfoRequest;
    private CacheUtil cacheUtil = new CacheUtil();

    @Override
    protected CreateWalletActivity getV() {
        return super.getV();
    }


    /**
     * 获取邀请码
     */
    public String getInvCode(){
        String invCode = "";
        return invCode;
    }

    /**
     * 生成(获取)公私钥对方法
     * 调用本地方法获得公私钥对字符串，并做类型转换
     */
    public String[] getPubandPriKey(){



        return keyPair;
    }

    /**
     * 储存公私钥方法
     * 公钥直接存储，私钥与密码组成字符串调用本地方法加密后存储
     */

    public void storeKeyPair(){
        pubKey = keyPair[0];
        priKey = keyPair[1];



    }

}
