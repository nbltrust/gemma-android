package com.hxlx.core.lib.common.cache;

import android.text.TextUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * Des3工具类
 */
public class Des3Util {
    public static final String DESEDE_CBC_PKCS5_PADDING = "desede/CBC/PKCS5Padding";
    private static String secretKey = "";//加密密钥
    private static String iv = "";//8位
    private static String encoding = "utf-8";

    /**
     * 初始化工具类
     *
     * @param secretKey 密钥
     * @param iv        向量
     */
    public static void init(String secretKey, String iv) {
        Des3Util.secretKey = secretKey;
        Des3Util.iv = iv;
    }

    /**
     * 加密
     *
     * @param plainText 要加密文字
     * @return 加密文字
     * @throws Exception
     */
    public static String encode(String plainText) throws Exception {
        if (TextUtils.isEmpty(secretKey) || TextUtils.isEmpty(iv))
            throw new NullPointerException("u should init first");
        SecretKey deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(secretKey.getBytes());
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        deskey = keyfactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance(DESEDE_CBC_PKCS5_PADDING);
        IvParameterSpec ips = new IvParameterSpec(iv.getBytes());
        cipher.init(1, deskey, ips);
        byte[] encryptData = cipher.doFinal(plainText.getBytes(encoding));
        return Base64Util.encode(encryptData);
    }

    /***
     *  解密
     * @param encryptText 要解密文字
     * @return 解密文字
     * @throws Exception
     */
    public static String decode(String encryptText) throws Exception {
        if (TextUtils.isEmpty(secretKey) || TextUtils.isEmpty(iv))
            throw new NullPointerException("u should init first");
        SecretKey deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(secretKey.getBytes());
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        deskey = keyfactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance(DESEDE_CBC_PKCS5_PADDING);
        IvParameterSpec ips = new IvParameterSpec(iv.getBytes());
        cipher.init(2, deskey, ips);
        byte[] decryptData = cipher.doFinal(Base64Util.decode(encryptText));
        return new String(decryptData, encoding);
    }
}