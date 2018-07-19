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
}
