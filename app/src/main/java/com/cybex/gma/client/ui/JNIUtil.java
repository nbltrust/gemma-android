package com.cybex.gma.client.ui;

/**
 * Jni调用
 *
 * Created by wanglin on 2018/7/12.
 */
public class JNIUtil {

    static {
        System.loadLibrary("libkeosdlib");
    }

    private JNIUtil() {}


    public final static native void createKey();

    public final static native String get_cypher(String password, String priv_key);

    public final static native String get_private_key(String cipher_keys, String password);
}
