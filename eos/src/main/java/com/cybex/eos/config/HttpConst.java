package com.cybex.eos.config;

/**
 * Http使用的常量
 */
public interface HttpConst {

    int CODE_RESULT_SUCCESS = 0;
    String RESULT_SUCCESS = "ok";
    int INVCODE_USED = 10002;
    int INVCODE_NOTEXIST = 10003;
    int EOSNAME_USED = 10004;
    int EOSNAME_INVALID = 10005;
    int EOSNAME_LENGTH_INVALID = 10006;
    int PARAMETERS_INVALID = 10007;
    int PUBLICKEY_INVALID = 10008;
    int INVALID_PARAMETER = 10013;
    int BALANCE_NOT_ENOUGH = 20001;
    int CREATE_ACCOUNT_FAIL = 20002;

    String KEY_ACCOUNT_NAME = "account_name";
    String KEY_SHOW_NUM = "show_num";
    String KEY_LAST_POST = "last_pos";
    int PAGE_NUM = 10;
    int ACTION_REFRESH = -1;//列表刷新操作

    int SERVER_INTERNAL_ERR = 500;

    int WXPAY_SUCCESS = 0;//微信支付成功
    int WXPAY_ERROR = -1;//微信支付错误
    int WXPAY_CANCEL = -2;//微信支付用户取消

    String WXPAY_STATE_NOTPAY = "NOTPAY";
    String WXPAY_STATE_SUCCESS = "SUCCESS";
    String WXPAY_STATE_REFUND = "REFUND";
    String WXPAY_STATUS_TOREFUND = "REFUND";
    String WXPAY_STATE_CLOSED = "CLOSED";
    String WXPAY_STATE_USERPAYING = "USERPAYING";
    String WXPAY_STATE_PAYERROR = "PAYERROR";

    String WXPAY_STATUS_INIT = "INIT";
    String WXPAY_STATUS_CLOSED = "CLOSED";
    String WXPAY_STATUS_DONE = "DONE";


}
