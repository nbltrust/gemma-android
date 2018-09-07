package com.cybex.gma.client.api;

/**
 * 网络接口请求的方法。
 */
public interface ApiMethod {

    /**
     * 获取版本号接口
     */
    String API_CHECK_VER = "";

    /*----------------中心化服务器API Mehtod-----------------------*/

    /**
     * 注册
     */
    String API_REGISTER = "/api/v1/faucet/new";

    /**
     * 交易历史
     */
    String API_GET_TRANSACTION_HISTORY = "/api/v1/account/history/";

    /**
     * 验证账户名
     */
    String API_VERIFY_ACCOUNT = "/account/verify";

    /**
     * 获取投票节点信息
     */
    String API_FETCH_BP_DETAILS = "/api/v1/producer/";


    /*----------------链上服务器API Mehtod-----------------------*/

    /**
     * 获取账户信息
     */
    String API_GET_ACCOUNT_INFO = "/v1/chain/get_account";


    /**
     * 获取配置信息，提供C++库使用
     */
    String API_GET_INFO = "/v1/chain/get_info";


    /**
     * 将json序列化为二进制十六进制: 提供c++库转账接口用
     */
    String API_ABI_JSON_TO_BIN = "/v1/chain/abi_json_to_bin";

    /**
     * 转账，提供链上用
     */
    String API_PUSH_TRANSACTION = "/v1/chain/push_transaction";

    /**
     * 获取指定资产的余额
     */
    String API_GET_CUREENCY_BALANCE = "/v1/chain/get_currency_balance";

    /**
     * 根据公钥查询账户列表
     */
    String API_GET_KEY_ACCOUNTS = "/v1/history/get_key_accounts";

    /**
     *根据txId哈希值查询当前transaction信息
     * 创建钱包使用
     */
    String API_GET_TRANSACTION = "/v1/history/get_transaction";

    /**
     * 根据区块号获取当前区块信息
     */
    String API_GET_BLOCK = "/v1/chain/get_block";

    /**
     * 获取当前RAM market信息
     * 估算EOS与RAM间换算价格使用
     */
    String API_GET_RAM_MARKET = "/v1/chain/get_table_rows";




}
