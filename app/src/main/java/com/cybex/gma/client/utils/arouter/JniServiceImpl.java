package com.cybex.gma.client.utils.arouter;

import com.cybex.componentservice.service.JniService;
import com.cybex.gma.client.ui.JNIUtil;

public class JniServiceImpl implements JniService{
    @Override
    public String createKey() {
        return JNIUtil.createKey();
    }

    @Override
    public String get_cypher(String password, String priv_key) {
        return JNIUtil.get_cypher(password,priv_key);
    }

    @Override
    public String get_private_key(String cipher_keys, String password) {
        return JNIUtil.get_private_key(cipher_keys, password);
    }

    @Override
    public String get_public_key(String priv_str) {
        return JNIUtil.get_public_key(priv_str);
    }

    @Override
    public String signTransaction_tranfer(String priv_key_str, String contract, String senderstr, String infostr, String abistr, long max_cpu_usage_ms, long max_net_usage_words, long tx_expiration) {
        return JNIUtil.signTransaction_tranfer(priv_key_str, contract, senderstr, infostr, abistr, max_cpu_usage_ms, max_net_usage_words, tx_expiration);
    }

    @Override
    public String create_abi_req_transfer(String code, String action, String from, String to, String quantity, String memo) {
        return JNIUtil.create_abi_req_transfer(code, action, from, to, quantity, memo);
    }

    @Override
    public String signTransaction_buyram(String priv_key_str, String contract, String payer_str, String infostr, String abistr, long max_cpu_usage_ms, long max_net_usage_words, long tx_expiration) {
        return JNIUtil.signTransaction_buyram(priv_key_str, contract, payer_str, infostr, abistr, max_cpu_usage_ms, max_net_usage_words, tx_expiration);
    }

    @Override
    public String signTransaction_sellram(String priv_key_str, String contract, String account_str, String infostr, String abistr, long max_cpu_usage_ms, long max_net_usage_words, long tx_expiration) {
        return JNIUtil.signTransaction_sellram(priv_key_str, contract, account_str, infostr, abistr, max_cpu_usage_ms, max_net_usage_words, tx_expiration);
    }

    @Override
    public String fincreate_abi_req_buyram(String code, String action, String payer, String receiver, String quant) {
        return JNIUtil.fincreate_abi_req_buyram(code, action, payer, receiver, quant);
    }

    @Override
    public String create_abi_req_sellram(String code, String action, String account, long bytes) {
        return JNIUtil.create_abi_req_sellram(code, action, account, bytes);
    }

    @Override
    public String signTransaction_delegatebw(String priv_key_str, String contract, String from_str, String infostr, String abistr, long max_cpu_usage_ms, long max_net_usage_words, long tx_expiration) {
        return JNIUtil.signTransaction_delegatebw(priv_key_str, contract, from_str, infostr, abistr, max_cpu_usage_ms, max_net_usage_words, tx_expiration);
    }

    @Override
    public String signTransaction_undelegatebw(String priv_key_str, String contract, String from_str, String infostr, String abistr, long max_cpu_usage_ms, long max_net_usage_words, long tx_expiration) {
        return JNIUtil.signTransaction_undelegatebw(priv_key_str, contract, from_str, infostr, abistr, max_cpu_usage_ms, max_net_usage_words, tx_expiration);
    }

    @Override
    public String create_abi_req_delegatebw(String code, String action, String from, String receiver, String stake_net_quantity, String stake_cpu_quantity) {
        return JNIUtil.create_abi_req_delegatebw(code, action, from, receiver, stake_net_quantity, stake_cpu_quantity);
    }

    @Override
    public String create_abi_req_undelegatebw(String code, String action, String from, String receiver, String unstake_net_quantity, String unstake_cpu_quantity) {
        return JNIUtil.create_abi_req_undelegatebw(code, action, from, receiver, unstake_net_quantity, unstake_cpu_quantity);
    }

    @Override
    public String signTransaction_voteproducer(String priv_key_str, String contract, String voter_str, String infostr, String abistr, long max_cpu_usage_ms, long max_net_usage_words, long tx_expiration) {
        return JNIUtil.signTransaction_voteproducer(priv_key_str, contract, voter_str, infostr, abistr, max_cpu_usage_ms, max_net_usage_words, tx_expiration);
    }
}
