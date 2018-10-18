package com.cybex.walletmanagement.ui.presenter;

import com.cybex.componentservice.config.CacheConstants;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.db.entity.WalletEntity;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.service.JniService;
import com.cybex.walletmanagement.ui.activity.CreateMnemonicWalletActivity;
import com.hxlx.core.lib.mvp.lite.XPresenter;
import com.hxlx.core.lib.utils.GsonUtils;
import com.mrzhang.component.componentlib.router.Router;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seed39.Seed39;

public class CreateMnemonicWalletPresenter extends XPresenter<CreateMnemonicWalletActivity> {

    @Override
    protected CreateMnemonicWalletActivity getV() {
        return super.getV();
    }

    /**
     * 调用底层方法生成公私钥对
     *
     * @return
     */
//    public String[] getKeypair() {
//        String[] keypair = JNIUtil.createKey().split(";");
//        return keypair;
//    }


    /**
     * 钱包命规则：todo
     * @return
     */

    public boolean isWalletNameValid() {
        String walletName = getV().getWalletName();

        //todo，check if walletname exist
        return true;

    }

    public boolean isPasswordMatch(){
        return getV().getPassword().equals(getV().getRepeatPassword());
    }

    /**
     * 调用DB Manager将钱包信息存入表中
     *
     * @param publicKey
     * @param privateKey
     * @param password
     * @param eosUsername
     * @param passwordTip
     */
    public void saveAccount(
            final String publicKey, final String privateKey, final String
            password, final String eosUsername, final String passwordTip, final String txId, final String invCode) {


//        WalletEntity walletEntity = new WalletEntity();
//        List<WalletEntity> walletEntityList = DBManager.getInstance().getWalletEntityDao().getWalletEntityList();
//        //获取当前数据库中已存入的钱包个数
//        int walletNum = walletEntityList.size();
//        int index = walletNum + 1;
//        //以默认钱包名称存入
//        walletEntity.setWalletName(CacheConstants.DEFAULT_WALLETNAME_PREFIX + String.valueOf(index));
//        //设置公钥
//        walletEntity.setPublicKey(publicKey);
//        //设置摘要
//        final String cypher = JNIUtil.get_cypher(password, privateKey);
//        walletEntity.setCypher(cypher);
//        //设置eosNameJson
//        List<String> account_names = new ArrayList<>();
//        account_names.add(eosUsername);
//        final String eosNameJson = GsonUtils.objectToJson(account_names);
//        walletEntity.setEosNameJson(eosNameJson);
//        //设置currentEosName，创建钱包步骤中可以直接设置，因为默认eosNameJson中只会有一个用户名字符串
//        walletEntity.setCurrentEosName(eosUsername);
//        //设置是否为当前钱包，默认新建钱包为当前钱包
//        walletEntity.setIsCurrentWallet(CacheConstants.IS_CURRENT_WALLET);
//        //设置密码提示
//        walletEntity.setPasswordTip(passwordTip);
//        walletEntity.setWalletType(0);
//        //设置为未备份
//        walletEntity.setIsBackUp(CacheConstants.NOT_BACKUP);
//        //设置被链上确认状态位未被确认
//        walletEntity.setIsConfirmLib(CacheConstants.NOT_CONFIRMED);
//        //设置当前Transaction的Hash值
//        walletEntity.setTxId(txId);
//        //设置邀请码
//        walletEntity.setInvCode(invCode);
//        //执行存入操作之前需要把其他钱包设置为非当前钱包
//        if (walletNum > 0) {
//            WalletEntity curWallet = DBManager.getInstance().getWalletEntityDao().getCurrentWalletEntity();
//            curWallet.setIsCurrentWallet(CacheConstants.NOT_CURRENT_WALLET);
//            DBManager.getInstance().getWalletEntityDao().saveOrUpateEntity(curWallet);
//        }
//        //最后执行存入操作，此前包此时为当前钱包
//        DBManager.getInstance().getWalletEntityDao().saveOrUpateEntity(walletEntity);
    }



    public void creatWallet(String walletName,String password,String passwordHint) {

        MultiWalletEntity multiWalletEntity = new MultiWalletEntity();




        multiWalletEntity.setWalletType(0);
        multiWalletEntity.setIsBackUp(CacheConstants.NOT_BACKUP);
        multiWalletEntity.setPasswordTip(passwordHint);


    }
}