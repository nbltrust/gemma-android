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
     * 根据私钥获取公钥
     *
     * @param priv_str
     * @return
     */
    public final static native String get_public_key(String priv_str);


    /**
     * 转账
     *
     * @param priv_key_str
     * @param contract
     * @param senderstr
     * @param infostr
     * @param abistr
     * @param max_cpu_usage_ms
     * @param max_net_usage_words
     * @param tx_expiration
     * @return
     */
    public final static native String signTransaction_tranfer(
            String priv_key_str,
            String contract, String senderstr, String infostr, String abistr,
            long max_cpu_usage_ms,
            long max_net_usage_words,
            long tx_expiration);

    /**
     * 创建转账 abi req
     *
     * @param code
     * @param action
     * @param from
     * @param to
     * @param quantity
     * @param memo
     * @return
     */
    public final static native String create_abi_req_transfer(
            String code,
            String action, String from, String to, String quantity, String memo);


}
