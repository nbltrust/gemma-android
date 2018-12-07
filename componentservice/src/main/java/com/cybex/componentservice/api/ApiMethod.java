package com.cybex.componentservice.api;

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
    String API_REGISTER = "/api/v1/account/new";

    /**
     * 交易历史
     */
    String API_GET_TRANSACTION_HISTORY = "/api/v1/account/history";

    /**
     * 验证账户名
     */
    String API_VERIFY_ACCOUNT = "/account/verify";

    /**
     * 获取投票节点信息
     */
    String API_FETCH_BP_DETAILS = "/api/v1/producer/";

    /**
     * 蓝牙用户注册接口
     */
    String API_BLUETOOTH_REGISTER_ACCOUNT = "/api/v1/account/new";

    /**
     * 获取EOS糖果接口
     */
    String API_GET_EOS_TOKENS = "/api/v1/account/tokens/";


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
     * 根据txId哈希值查询当前transaction信息
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

    /**
     * 微信支付初始化订单
     */
    String WXPAY_INITIAL_ORDER = "/api/v1/pay/order";

    /**
     * 微信支付订单查询
     */
    String WXPAY_QUERY_ORDER_INFO = "/api/v1/pay/order/";

    /**
     * 微信支付查询创建账户法币支付账单明细
     */
    String WXPAY_PAY_BILL = "/api/v1/pay/bill";

    /**
     * 微信支付支付下单接口
     */
    String WXPAY_PLACE_ORDER = "/api/v1/pay/order/";

    String EOS_SPARK_API_GET_TRANSACTION = "/api?action=get_transaction_detail_info&apikey=2d0b90f1d4b59d5b24369762608cf681&module=transaction&trx_id=";
}
