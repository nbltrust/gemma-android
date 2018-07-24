#include <jni.h>
#include <string>
#include "keosdlib.hpp"
//#include <iostream>

keosdlib_api k;
extern "C" JNIEXPORT jstring



JNICALL
Java_com_cybex_gma_client_ui_JNIUtil_createKey(JNIEnv *env, jclass type) {

    std::pair<std::string, std::string> res = k.createKey("K1");

    //std::cout << res.first << "\n" << res.second << std::endl;
    return env->NewStringUTF((res.first + ";" + res.second).c_str());

}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_cybex_gma_client_ui_JNIUtil_get_1cypher(JNIEnv *env, jclass type, jstring password_, jstring priv_key_) {
    const char *password = env->GetStringUTFChars(password_, 0);
    const char *priv_key = env->GetStringUTFChars(priv_key_, 0);

    env->ReleaseStringUTFChars(password_, password);
    env->ReleaseStringUTFChars(priv_key_, priv_key);

    std::string returnValue = k.get_cypher(password, priv_key);

    return env->NewStringUTF(returnValue.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_cybex_gma_client_ui_JNIUtil_get_1private_1key(JNIEnv *env, jclass type, jstring cipher_keys_,
                                                       jstring password_) {
    const char *cipher_keys = env->GetStringUTFChars(cipher_keys_, 0);
    const char *password = env->GetStringUTFChars(password_, 0);

    env->ReleaseStringUTFChars(cipher_keys_, cipher_keys);
    env->ReleaseStringUTFChars(password_, password);

    std::string returnValue = k.get_private_key(cipher_keys, password);

    return env->NewStringUTF(returnValue.c_str());
}


extern "C"
JNIEXPORT jstring JNICALL
Java_com_cybex_gma_client_ui_JNIUtil_get_1public_1key(JNIEnv *env, jclass type, jstring priv_str_) {
    const char *priv_str = env->GetStringUTFChars(priv_str_, 0);

    env->ReleaseStringUTFChars(priv_str_, priv_str);

    std::string returnValue = k.get_public_key(priv_str);


    return env->NewStringUTF(returnValue.c_str());
}