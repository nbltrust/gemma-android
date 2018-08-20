package com.cybex.gma.client.config;


/**
 * 缓存参数相关配置
 */
public interface CacheConstants {

    String PubKey_Prefix = "PublicKey_";
    String PriKey_Prefix = "PriKey+Pass_";
    String DEFAULT_WALLETNAME_PREFIX = "EOS-WALLET-";
    Integer IS_CURRENT_WALLET = 1;
    Integer NOT_CURRENT_WALLET = 0;
    int ALREADY_BACKUP = 1;
    int NOT_BACKUP = 0;

    Integer IS_CONFIRMED = 1;
    Integer NOT_CONFIRMED = 0;
    Integer CONFIRM_FAILED = -1;

    String GESTURE_PASSWORD = "GesturePassword";
    String KEY_OPEN_GESTURE = "is_open_gesture";
    String KEY_OPEN__FINGER_PRINT = "is_open_finger_print";


}
