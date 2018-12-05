package com.cybex.componentservice.config;

import com.extropies.common.MiddlewareInterface;

public class BaseConst {
    public static final String KEY_BACKUP_MNEMONIC_TYPE="key_backup_mnemonic_type";
    public static final String KEY_COIN_TYPE="key_coin_type";
    public static final String KEY_MNEMONIC="key_mnemonic";
    public static final String KEY_PRI_KEY="key_pri_key";
    public static final String KEY_INIT_TYPE="key_init_type";
    public static final String KEY_WALLET_ENTITY="key_wallet_entity";
    public static final String KEY_WALLET_ENTITY_ID="key_wallet_entity_id";
    public static final String KEY_PASSWORD="key_password";



    public static final String MNEMONIC_PATH_ETH="m/44'/60'/0'/0/";
    public static final String MNEMONIC_PATH_EOS="m/44'/194'/0'/0/0/";


    //主页面跳转类型定义
    public static final int APP_HOME_INITTYPE_NONE=-1;
    public static final int APP_HOME_INITTYPE_TO_BACKUP_MNEMONIC_GUIDE=0;
    public static final int APP_HOME_INITTYPE_TO_ENROLL_FP=1;
    public static final int APP_HOME_INITTYPE_TO_INITI_PAGE=2;
    public static final int APP_HOME_INITTYPE_TO_INITI_PAGE_WOOKONG_PAIR=3;


    //INIT PAGE 跳转类型定义
    public static final int APP_INIT_INITTYPE_TO_WOOKONG_PAIE=1;

    /**
     * 钱包类型 (0--新建助记词软件钱包 1-导入助记词类软钱包  2--硬件钱包  3-导入单个私钥类型的软钱包)
     */
    public static final int WALLET_TYPE_MNE_CREATE = 0;
    public static final int WALLET_TYPE_MNE_IMPORT = 1;
    public static final int WALLET_TYPE_BLUETOOTH = 2;
    public static final int WALLET_TYPE_PRIKEY_IMPORT = 3;

    public static final String INITIAL_WALLET_NAME="WOOKONG Wallet";
    public static final String INITIAL_WALLET_NAME_PREFIX="WOOKONG Wallet ";
    public static final String INITIAL_WALLET_INDEX_KEY="initial_wallet_index_key";
    public static final int INITIAL_WALLET_INDEX=1;


    public static final String CN = "中文";
    public static final String EN = "English";

    public static final String BLUETOOTH_CONNECTION_STATE = "isBioConnected";



    //蓝牙设备
    public static final int DEVICE_LIFE_CYCLE_INVALID = MiddlewareInterface.PAEW_DEV_INFO_LIFECYCLE_INVALID;//INVALID
    public static final int DEVICE_LIFE_CYCLE_AGREE = MiddlewareInterface.PAEW_DEV_INFO_LIFECYCLE_AGREE;//production state
    public static final int DEVICE_LIFE_CYCLE_PRODUCE = MiddlewareInterface.PAEW_DEV_INFO_LIFECYCLE_PRODUCE ;//produce
    public static final int DEVICE_LIFE_CYCLE_USER = MiddlewareInterface.PAEW_DEV_INFO_LIFECYCLE_USER;//user

    public static final int DEVICE_PIN_STATE_INVALID = MiddlewareInterface.PAEW_DEV_INFO_PIN_INVALID_STATE;//PIN非法
    public static final int DEVICE_PIN_STATE_LOGOUT = MiddlewareInterface.PAEW_DEV_INFO_PIN_LOGOUT;//
    public static final int DEVICE_PIN_STATE_LOGIN = MiddlewareInterface.PAEW_DEV_INFO_PIN_LOGIN;//
    public static final int DEVICE_PIN_STATE_LOCKED = MiddlewareInterface.PAEW_DEV_INFO_PIN_LOCKED;//PIN锁定
    public static final int DEVICE_PIN_STATE_UNSET = MiddlewareInterface.PAEW_DEV_INFO_PIN_UNSET;//PIN未设置

    public static final int STATE_SET_PIN_NOT_INIT = 10;//已设置PIN未完成初始化
    public static final int STATE_INIT_DONE = 11;//已完成初始化

    public static final String PIN_STATUS = "isPINInit";





}
