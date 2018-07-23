package com.cybex.gma.client.ui;


/**
 * Jni调用
 *
 * Created by wanglin on 2018/7/12.
 */
public class JNIUtil {

    static {
        System.loadLibrary("eosJni");
    }

    private JNIUtil() {}


    /**
     * 获取公私钥对
     *
     * @return
     */
    public final static native String createKey();


    /**
     * 加密私钥获取密文
     *
     * @param password
     * @param priv_key
     * @return
     */
    public final static native String get_cypher(String password, String priv_key);

    /**
     * 根据密文和密码获取私钥
     *
     * @param cipher_keys
     * @param password
     * @return
     */
    public final static native String get_private_key(String cipher_keys, String password);

    /**
     * 创建获取abi需要的操作体
     *
     * @return
     */
    public final static native String create_abi_req(
            String code, String action, String from,
            String to, String quantity, String memo);


    /**
     * 获取Transaction交易体
     */
    public final static native String signTransaction(
            String priv_key_str, String contract,
            String senderstr, String recipientstr, String amountstr, String memo, String infostr, String abistr, long
            max_cpu_usage_ms, long max_net_usage_words, long tx_expiration);

    /**
     * 获取Transaction交易体
     *
     * @return
     */
    public final static native String signTransaction(String trxstr, String priv_key_str, String chain_id_str);
}
