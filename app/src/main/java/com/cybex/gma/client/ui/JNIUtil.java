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


    /**
     * 买ram transaction
     *
     * @param priv_key_str
     * @param contract
     * @param payer_str
     * @param infostr
     * @param abistr
     * @param max_cpu_usage_ms
     * @param max_net_usage_words
     * @param tx_expiration
     * @return
     */
    public final static native String signTransaction_buyram(
            String priv_key_str,
            String contract, String payer_str,
            String infostr, String abistr,
            long max_cpu_usage_ms,
            long max_net_usage_words,
            long tx_expiration);


    /**
     * 卖ram transaction
     *
     * @param priv_key_str
     * @param contract
     * @param account_str
     * @param infostr
     * @param abistr
     * @param max_cpu_usage_ms
     * @param max_net_usage_words
     * @param tx_expiration
     * @return
     */
    public final static native String signTransaction_sellram(
            String priv_key_str, String
            contract, String
            account_str,
            String infostr, String abistr,
            long max_cpu_usage_ms,
            long max_net_usage_words,
            long tx_expiration);


    /**
     * 买ram abi req
     *
     * @param code
     * @param action
     * @param payer
     * @param receiver
     * @param quant
     */
    public final static native String fincreate_abi_req_buyram(
            String code, String action, String payer,
            String receiver, String quant);



    /**
     * 卖ram abi req
     *
     * @param code
     * @param action
     * @param account
     * @param bytes
     * @return
     */
    public final static native String create_abi_req_sellram(
            String code, String action, String account,
            long bytes);


    /**
     * 抵押
     *
     * @param priv_key_str
     * @param contract
     * @param from_str
     * @param infostr
     * @param abistr
     * @param max_cpu_usage_ms
     * @param max_net_usage_words
     * @param tx_expiration
     * @return
     */
    public final static native String signTransaction_delegatebw(
            String priv_key_str, String contract,
            String from_str, String infostr,
            String abistr, long max_cpu_usage_ms,
            long max_net_usage_words, long tx_expiration);


    /**
     * 取消抵押
     *
     * @param priv_key_str
     * @param contract
     * @param from_str
     * @param infostr
     * @param abistr
     * @param max_cpu_usage_ms
     * @param max_net_usage_words
     * @param tx_expiration
     * @return
     */
    public final static native String signTransaction_undelegatebw(
            String priv_key_str, String contract, String from_str,
            String infostr, String abistr, long max_cpu_usage_ms,
            long max_net_usage_words, long tx_expiration);


    /**
     * 抵押 abi req
     *
     * @param code
     * @param action
     * @param from
     * @param receiver
     * @param stake_net_quantity
     * @param stake_cpu_quantity
     * @return
     */
    public final static native String create_abi_req_delegatebw(
            String code, String action,
            String from, String receiver,
            String stake_net_quantity, String stake_cpu_quantity);

    /**
     * 解除抵押 abi req
     *
     * @param code
     * @param action
     * @param from
     * @param receiver
     * @param unstake_net_quantity
     * @param unstake_cpu_quantity
     * @return
     */
    public final static native String create_abi_req_undelegatebw(
            String code, String action, String
            from, String receiver, String unstake_net_quantity,
            String unstake_cpu_quantity);


    /**
     * 给生产者投票
     *
     * @param priv_key_str
     * @param contract
     * @param voter_str
     * @param infostr
     * @param abistr
     * @param max_cpu_usage_ms
     * @param max_net_usage_words
     * @param tx_expiration
     * @return
     */
    public final static native String signTransaction_voteproducer(
            String priv_key_str, String contract,
            String voter_str, String infostr,
            String abistr, long max_cpu_usage_ms,
            long max_net_usage_words, long tx_expiration);


}
