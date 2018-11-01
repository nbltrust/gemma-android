package com.cybex.componentservice.config;


/**
 * 缓存参数相关配置
 */
public interface CacheConstants {

    String PubKey_Prefix = "PublicKey_";
    String PriKey_Prefix = "PriKey+Pass_";
    String DEFAULT_WALLETNAME_PREFIX = "EOS-WALLET-";
    String DEFAULT_BLUETOOTH_WALLET_PREFIX = "WOOKONG Bio";
    Integer IS_CURRENT_WALLET = 1;
    Integer NOT_CURRENT_WALLET = 0;
    int ALREADY_BACKUP = 1;
    int NOT_BACKUP = 0;

    Integer IS_CONFIRMED = 1;
    Integer NOT_CONFIRMED = 0;
    Integer CONFIRM_FAILED = -1;

    String GESTURE_PASSWORD = "GesturePassword";
    String KEY_OPEN_GESTURE = "is_open_gesture";
    String KEY_OPEN_FINGER_PRINT = "is_open_finger_print";

    int CURRENCY_CNY = 1;
    int CURRENCY_USD = 2;

    /**
     *  钱包类型 (0--新建助记词软件钱包 1-导入助记词类软钱包  2--硬件钱包  3-导入单个私钥类型的软钱包)
     */

    int WALLET_TYPE_MNE_CREATE = 0;
    int WALLET_TYPE_MNE_IMPORT = 1;
    int WALLET_TYPE_BLUETOOTH = 2;
    int WALLET_TYPE_PRIKEY_IMPORT = 3;

    int STATUS_BLUETOOTH_DISCONNCETED = 0;
    int STATUS_BLUETOOTH_CONNCETED = 1;

    int[] EOS_DERIVE_PATH = {0, 0x8000002C, 0x800000c2, 0x80000000, 0x00000000, 0x00000000};

    String BIO_CONNECT_STATUS = "isBioConnected";

}
