package com.cybex.componentservice.config;

public class BaseConst {
    public static final String KEY_BACKUP_MNEMONIC_TYPE="key_backup_mnemonic_type";
    public static final String KEY_COIN_TYPE="key_coin_type";
    public static final String KEY_MNEMONIC="key_mnemonic";
    public static final String KEY_PRI_KEY="key_pri_key";
    public static final String KEY_INIT_TYPE="key_init_type";
    public static final String KEY_WALLET_ENTITY="key_wallet_entity";
    public static final String KEY_PASSWORD="key_password";


    public static final String MNEMONIC_PATH_ETH="m/44'/60'/0'/0/";
    public static final String MNEMONIC_PATH_EOS="m/44'/194'/0'/0/";


    //主页面跳转类型定义
    public static final int APP_HOME_INITTYPE_NONE=-1;
    public static final int APP_HOME_INITTYPE_TO_BACKUP_MNEMONIC_GUIDE=0;

    /**
     * 钱包类型 (0--新建助记词软件钱包 1-导入助记词类软钱包  2--硬件钱包  3-导入单个私钥类型的软钱包)
     */
    public static final int WALLET_TYPE_MNE_CREATE = 0;
    public static final int WALLET_TYPE_MNE_IMPORT = 1;
    public static final int WALLET_TYPE_BLUETOOTH = 2;
    public static final int WALLET_TYPE_PRIKEY_IMPORT = 3;

    public static final String INITIAL_WALLET_NAME="Unnamed Wallet 1";





}
