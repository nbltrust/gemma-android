package com.cybex.gma.client.config;


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

    int WALLET_TYPE_SOFT = 0;
    int WALLET_TYPE_BLUETOOTH = 1;

    int STATUS_BLUETOOTH_DISCONNCETED = 0;
    int STATUS_BLUETOOTH_CONNCETED = 1;


}
