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
Java_com_cybex_gma_client_ui_JNIUtil_create_1abi_1req(JNIEnv *env, jclass type, jstring code_, jstring action_,
                                                      jstring from_, jstring to_, jstring quantity_, jstring memo_) {
    const char *code = env->GetStringUTFChars(code_, 0);
    const char *action = env->GetStringUTFChars(action_, 0);
    const char *from = env->GetStringUTFChars(from_, 0);
    const char *to = env->GetStringUTFChars(to_, 0);
    const char *quantity = env->GetStringUTFChars(quantity_, 0);
    const char *memo = env->GetStringUTFChars(memo_, 0);

    env->ReleaseStringUTFChars(code_, code);
    env->ReleaseStringUTFChars(action_, action);
    env->ReleaseStringUTFChars(from_, from);
    env->ReleaseStringUTFChars(to_, to);
    env->ReleaseStringUTFChars(quantity_, quantity);
    env->ReleaseStringUTFChars(memo_, memo);

    std::string returnValue = k.create_abi_req(code, action, from, to, quantity, memo);

    return env->NewStringUTF(returnValue.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_cybex_gma_client_ui_JNIUtil_signTransaction__Ljava_lang_String_2Ljava_lang_String_2Ljava_lang_String_2Ljava_lang_String_2Ljava_lang_String_2Ljava_lang_String_2Ljava_lang_String_2Ljava_lang_String_2JJJ(
        JNIEnv *env, jclass type, jstring priv_key_str_, jstring contract_, jstring senderstr_, jstring recipientstr_,
        jstring amountstr_, jstring memo_, jstring infostr_, jstring abistr_, jlong max_cpu_usage_ms,
        jlong max_net_usage_words, jlong tx_expiration) {
    const char *priv_key_str = env->GetStringUTFChars(priv_key_str_, 0);
    const char *contract = env->GetStringUTFChars(contract_, 0);
    const char *senderstr = env->GetStringUTFChars(senderstr_, 0);
    const char *recipientstr = env->GetStringUTFChars(recipientstr_, 0);
    const char *amountstr = env->GetStringUTFChars(amountstr_, 0);
    const char *memo = env->GetStringUTFChars(memo_, 0);
    const char *infostr = env->GetStringUTFChars(infostr_, 0);
    const char *abistr = env->GetStringUTFChars(abistr_, 0);

    env->ReleaseStringUTFChars(priv_key_str_, priv_key_str);
    env->ReleaseStringUTFChars(contract_, contract);
    env->ReleaseStringUTFChars(senderstr_, senderstr);
    env->ReleaseStringUTFChars(recipientstr_, recipientstr);
    env->ReleaseStringUTFChars(amountstr_, amountstr);
    env->ReleaseStringUTFChars(memo_, memo);
    env->ReleaseStringUTFChars(infostr_, infostr);
    env->ReleaseStringUTFChars(abistr_, abistr);

    std::string returnValue = k.signTransaction(priv_key_str, contract, senderstr, recipientstr,
                              amountstr, memo, infostr, abistr,
                              max_cpu_usage_ms, max_net_usage_words, tx_expiration);

    return env->NewStringUTF(returnValue.c_str());
}


extern "C"
JNIEXPORT jstring JNICALL
Java_com_cybex_gma_client_ui_JNIUtil_signTransaction__Ljava_lang_String_2Ljava_lang_String_2Ljava_lang_String_2(
        JNIEnv *env, jclass type, jstring trxstr_, jstring priv_key_str_, jstring chain_id_str_) {
    const char *trxstr = env->GetStringUTFChars(trxstr_, 0);
    const char *priv_key_str = env->GetStringUTFChars(priv_key_str_, 0);
    const char *chain_id_str = env->GetStringUTFChars(chain_id_str_, 0);

    env->ReleaseStringUTFChars(trxstr_, trxstr);
    env->ReleaseStringUTFChars(priv_key_str_, priv_key_str);
    env->ReleaseStringUTFChars(chain_id_str_, chain_id_str);

    std::string returnValue = k.signTransaction(trxstr, priv_key_str, chain_id_str);

    return env->NewStringUTF(returnValue.c_str());
}