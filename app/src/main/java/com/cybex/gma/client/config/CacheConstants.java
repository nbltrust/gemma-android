package com.cybex.gma.client.config;


/**
 * 缓存参数相关配置
 */
public interface CacheConstants {

    final String PubKey_Prefix = "PublicKey_";
    final String PriKey_Prefix = "PriKey+Pass_";

    final String DEFAULT_WALLETNAME_PREFIX = "EOS-WALLET-";
    final String DEFALUT_WALLETNAME = "EOS-WALLET";

    final Integer IS_CURRENT_WALLET = 1;
    final Integer NOT_CURRENT_WALLET = 0;

    final int ALREADY_BACKUP = 1;
    final int NOT_BACKUP = 0;

}
