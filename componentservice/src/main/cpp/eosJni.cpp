#include <jni.h>
#include <string>
#include "keosdlib.hpp"
//#include <iostream>
#include <android/log.h>
#define TAG "eos-jni" // 这个是自定义的LOG的标识
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG ,__VA_ARGS__) // 定义LOGD类型
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,TAG ,__VA_ARGS__) // 定义LOGI类型
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,TAG ,__VA_ARGS__) // 定义LOGW类型
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG ,__VA_ARGS__) // 定义LOGE类型
#define LOGF(...) __android_log_print(ANDROID_LOG_FATAL,TAG ,__VA_ARGS__) // 定义LOGF类型

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


extern "C"
JNIEXPORT jstring JNICALL
Java_com_cybex_gma_client_ui_JNIUtil_signTransaction_1tranfer(JNIEnv *env, jclass type, jstring priv_key_str_,
                                                              jstring contract_, jstring senderstr_, jstring infostr_,
                                                              jstring abistr_, jlong max_cpu_usage_ms,
                                                              jlong max_net_usage_words, jlong tx_expiration) {
    const char *priv_key_str = env->GetStringUTFChars(priv_key_str_, 0);
    const char *contract = env->GetStringUTFChars(contract_, 0);
    const char *senderstr = env->GetStringUTFChars(senderstr_, 0);
    const char *infostr = env->GetStringUTFChars(infostr_, 0);
    const char *abistr = env->GetStringUTFChars(abistr_, 0);

    env->ReleaseStringUTFChars(priv_key_str_, priv_key_str);
    env->ReleaseStringUTFChars(contract_, contract);
    env->ReleaseStringUTFChars(senderstr_, senderstr);
    env->ReleaseStringUTFChars(infostr_, infostr);
    env->ReleaseStringUTFChars(abistr_, abistr);

    std::string returnValue = k.signTransaction_tranfer(priv_key_str, contract, senderstr, infostr, abistr,
                                                        max_cpu_usage_ms, max_net_usage_words, tx_expiration);


    return env->NewStringUTF(returnValue.c_str());
}


extern "C"
JNIEXPORT jstring JNICALL
Java_com_cybex_gma_client_ui_JNIUtil_create_1abi_1req_1transfer(JNIEnv *env, jclass type, jstring code_,
                                                                jstring action_, jstring from_, jstring to_,
                                                                jstring quantity_, jstring memo_) {
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

    std::string returnValue;
    try{
        returnValue = k.create_abi_req_transfer(code, action, from, to, quantity, memo);
    } catch(...){
        returnValue = std::string(code) + "__" + action + "__" + from + "__" + to + "__" + quantity + "__" + memo;
    }


    return env->NewStringUTF(returnValue.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_cybex_gma_client_ui_JNIUtil_signTransaction_1buyram(JNIEnv *env, jclass type, jstring priv_key_str_,
                                                             jstring contract_, jstring payer_str_, jstring infostr_,
                                                             jstring abistr_, jlong max_cpu_usage_ms,
                                                             jlong max_net_usage_words, jlong tx_expiration) {
    const char *priv_key_str = env->GetStringUTFChars(priv_key_str_, 0);
    const char *contract = env->GetStringUTFChars(contract_, 0);
    const char *payer_str = env->GetStringUTFChars(payer_str_, 0);
    const char *infostr = env->GetStringUTFChars(infostr_, 0);
    const char *abistr = env->GetStringUTFChars(abistr_, 0);

    env->ReleaseStringUTFChars(priv_key_str_, priv_key_str);
    env->ReleaseStringUTFChars(contract_, contract);
    env->ReleaseStringUTFChars(payer_str_, payer_str);
    env->ReleaseStringUTFChars(infostr_, infostr);
    env->ReleaseStringUTFChars(abistr_, abistr);

    std::string returnValue = k.signTransaction_buyram
            (priv_key_str, contract,
             payer_str, infostr, abistr,
             max_cpu_usage_ms,
             max_net_usage_words,
             tx_expiration);

    return env->NewStringUTF(returnValue.c_str());
}


extern "C"
JNIEXPORT jstring JNICALL
Java_com_cybex_gma_client_ui_JNIUtil_signTransaction_1sellram(JNIEnv *env, jclass type, jstring priv_key_str_,
                                                              jstring contract_, jstring account_str_, jstring infostr_,
                                                              jstring abistr_, jlong max_cpu_usage_ms,
                                                              jlong max_net_usage_words, jlong tx_expiration) {
    const char *priv_key_str = env->GetStringUTFChars(priv_key_str_, 0);
    const char *contract = env->GetStringUTFChars(contract_, 0);
    const char *account_str = env->GetStringUTFChars(account_str_, 0);
    const char *infostr = env->GetStringUTFChars(infostr_, 0);
    const char *abistr = env->GetStringUTFChars(abistr_, 0);

    env->ReleaseStringUTFChars(priv_key_str_, priv_key_str);
    env->ReleaseStringUTFChars(contract_, contract);
    env->ReleaseStringUTFChars(account_str_, account_str);
    env->ReleaseStringUTFChars(infostr_, infostr);
    env->ReleaseStringUTFChars(abistr_, abistr);

    std::string returnValue = k.signTransaction_sellram
            (priv_key_str, contract,
             account_str, infostr,
             abistr, max_cpu_usage_ms,
             max_net_usage_words, tx_expiration);

    return env->NewStringUTF(returnValue.c_str());
}


extern "C"
JNIEXPORT jstring JNICALL
Java_com_cybex_gma_client_ui_JNIUtil_fincreate_1abi_1req_1buyram(JNIEnv *env, jclass type, jstring code_,
                                                                 jstring action_, jstring payer_, jstring receiver_,
                                                                 jstring quant_) {
    const char *code = env->GetStringUTFChars(code_, 0);
    const char *action = env->GetStringUTFChars(action_, 0);
    const char *payer = env->GetStringUTFChars(payer_, 0);
    const char *receiver = env->GetStringUTFChars(receiver_, 0);
    const char *quant = env->GetStringUTFChars(quant_, 0);

    env->ReleaseStringUTFChars(code_, code);
    env->ReleaseStringUTFChars(action_, action);
    env->ReleaseStringUTFChars(payer_, payer);
    env->ReleaseStringUTFChars(receiver_, receiver);
    env->ReleaseStringUTFChars(quant_, quant);

    std::string returnValue = k.create_abi_req_buyram(code, action, payer, receiver, quant);

    return env->NewStringUTF(returnValue.c_str());
}


extern "C"
JNIEXPORT jstring JNICALL
Java_com_cybex_gma_client_ui_JNIUtil_create_1abi_1req_1sellram(JNIEnv *env, jclass type,
                                                               jstring code_, jstring action_,
                                                               jstring account_, jlong bytes) {
    const char *code = env->GetStringUTFChars(code_, 0);
    const char *action = env->GetStringUTFChars(action_, 0);
    const char *account = env->GetStringUTFChars(account_, 0);

    env->ReleaseStringUTFChars(code_, code);
    env->ReleaseStringUTFChars(action_, action);
    env->ReleaseStringUTFChars(account_, account);

    std::string returnValue = k.create_abi_req_sellram(code, action, account, bytes);

    return env->NewStringUTF(returnValue.c_str());
}


extern "C"
JNIEXPORT jstring JNICALL
Java_com_cybex_gma_client_ui_JNIUtil_signTransaction_1delegatebw(JNIEnv *env, jclass type, jstring priv_key_str_,
                                                                 jstring contract_, jstring from_str_, jstring infostr_,
                                                                 jstring abistr_, jlong max_cpu_usage_ms,
                                                                 jlong max_net_usage_words, jlong tx_expiration) {
    const char *priv_key_str = env->GetStringUTFChars(priv_key_str_, 0);
    const char *contract = env->GetStringUTFChars(contract_, 0);
    const char *from_str = env->GetStringUTFChars(from_str_, 0);
    const char *infostr = env->GetStringUTFChars(infostr_, 0);
    const char *abistr = env->GetStringUTFChars(abistr_, 0);

    env->ReleaseStringUTFChars(priv_key_str_, priv_key_str);
    env->ReleaseStringUTFChars(contract_, contract);
    env->ReleaseStringUTFChars(from_str_, from_str);
    env->ReleaseStringUTFChars(infostr_, infostr);
    env->ReleaseStringUTFChars(abistr_, abistr);

    std::string returnValue = k.signTransaction_delegatebw(priv_key_str, contract,
                                                           from_str, infostr,
                                                           abistr, max_cpu_usage_ms, max_net_usage_words,
                                                           tx_expiration);

    return env->NewStringUTF(returnValue.c_str());
}


extern "C"
JNIEXPORT jstring JNICALL
Java_com_cybex_gma_client_ui_JNIUtil_signTransaction_1undelegatebw(JNIEnv *env, jclass type, jstring priv_key_str_,
                                                                   jstring contract_, jstring from_str_,
                                                                   jstring infostr_, jstring abistr_,
                                                                   jlong max_cpu_usage_ms, jlong max_net_usage_words,
                                                                   jlong tx_expiration) {
    const char *priv_key_str = env->GetStringUTFChars(priv_key_str_, 0);
    const char *contract = env->GetStringUTFChars(contract_, 0);
    const char *from_str = env->GetStringUTFChars(from_str_, 0);
    const char *infostr = env->GetStringUTFChars(infostr_, 0);
    const char *abistr = env->GetStringUTFChars(abistr_, 0);

    env->ReleaseStringUTFChars(priv_key_str_, priv_key_str);
    env->ReleaseStringUTFChars(contract_, contract);
    env->ReleaseStringUTFChars(from_str_, from_str);
    env->ReleaseStringUTFChars(infostr_, infostr);
    env->ReleaseStringUTFChars(abistr_, abistr);

    std::string returnValue = k.signTransaction_undelegatebw
            (priv_key_str, contract,
             from_str, infostr, abistr, max_cpu_usage_ms,
             max_net_usage_words, tx_expiration);

    return env->NewStringUTF(returnValue.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_cybex_gma_client_ui_JNIUtil_create_1abi_1req_1delegatebw(JNIEnv *env, jclass type, jstring code_,
                                                                  jstring action_, jstring from_, jstring receiver_,
                                                                  jstring stake_net_quantity_,
                                                                  jstring stake_cpu_quantity_) {
    const char *code = env->GetStringUTFChars(code_, 0);
    const char *action = env->GetStringUTFChars(action_, 0);
    const char *from = env->GetStringUTFChars(from_, 0);
    const char *receiver = env->GetStringUTFChars(receiver_, 0);
    const char *stake_net_quantity = env->GetStringUTFChars(stake_net_quantity_, 0);
    const char *stake_cpu_quantity = env->GetStringUTFChars(stake_cpu_quantity_, 0);

    env->ReleaseStringUTFChars(code_, code);
    env->ReleaseStringUTFChars(action_, action);
    env->ReleaseStringUTFChars(from_, from);
    env->ReleaseStringUTFChars(receiver_, receiver);
    env->ReleaseStringUTFChars(stake_net_quantity_, stake_net_quantity);
    env->ReleaseStringUTFChars(stake_cpu_quantity_, stake_cpu_quantity);


    std::string returnValue = k.create_abi_req_delegatebw(code, action, from, receiver,
                                                          stake_net_quantity, stake_cpu_quantity);

    return env->NewStringUTF(returnValue.c_str());
}


extern "C"
JNIEXPORT jstring JNICALL
Java_com_cybex_gma_client_ui_JNIUtil_create_1abi_1req_1undelegatebw(JNIEnv *env, jclass type, jstring code_,
                                                                    jstring action_, jstring from_, jstring receiver_,
                                                                    jstring unstake_net_quantity_,
                                                                    jstring unstake_cpu_quantity_) {
    const char *code = env->GetStringUTFChars(code_, 0);
    const char *action = env->GetStringUTFChars(action_, 0);
    const char *from = env->GetStringUTFChars(from_, 0);
    const char *receiver = env->GetStringUTFChars(receiver_, 0);
    const char *unstake_net_quantity = env->GetStringUTFChars(unstake_net_quantity_, 0);
    const char *unstake_cpu_quantity = env->GetStringUTFChars(unstake_cpu_quantity_, 0);

    env->ReleaseStringUTFChars(code_, code);
    env->ReleaseStringUTFChars(action_, action);
    env->ReleaseStringUTFChars(from_, from);
    env->ReleaseStringUTFChars(receiver_, receiver);
    env->ReleaseStringUTFChars(unstake_net_quantity_, unstake_net_quantity);
    env->ReleaseStringUTFChars(unstake_cpu_quantity_, unstake_cpu_quantity);

    std::string returnValue = k.create_abi_req_undelegatebw(code, action, from, receiver,
                                                            unstake_net_quantity, unstake_cpu_quantity);

    return env->NewStringUTF(returnValue.c_str());
}


extern "C"
JNIEXPORT jstring JNICALL
Java_com_cybex_gma_client_ui_JNIUtil_signTransaction_1voteproducer(JNIEnv *env, jclass type, jstring priv_key_str_,
                                                                   jstring contract_, jstring voter_str_,
                                                                   jstring infostr_, jstring abistr_,
                                                                   jlong max_cpu_usage_ms, jlong max_net_usage_words,
                                                                   jlong tx_expiration) {

    const char *priv_key_str = env->GetStringUTFChars(priv_key_str_,0);
    const char *contract = env->GetStringUTFChars(contract_,0);
    const char *voter_str = env->GetStringUTFChars(voter_str_, 0);
    const char *infostr = env->GetStringUTFChars(infostr_, 0);
    const char *abistr = env->GetStringUTFChars(abistr_, 0);

//    LOGE("########## E--priv_key_str = %s", priv_key_str);
//    LOGE("########## E--contract = %s", contract);
//    LOGE("########## E--voter_str = %s", voter_str);
//    LOGE("########## E--infostr = %s", infostr);
//    LOGE("######### E--abistr = %s", abistr);

    std::string returnValue = k.signTransaction_voteproducer
            (priv_key_str, contract,
             voter_str, infostr, abistr,
             max_cpu_usage_ms, max_net_usage_words, tx_expiration);

    env->ReleaseStringUTFChars(priv_key_str_, priv_key_str);
    env->ReleaseStringUTFChars(contract_, contract);
    env->ReleaseStringUTFChars(voter_str_, voter_str);
    env->ReleaseStringUTFChars(infostr_, infostr);
    env->ReleaseStringUTFChars(abistr_, abistr);

    return env->NewStringUTF(returnValue.c_str());
}