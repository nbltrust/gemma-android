package com.cybex.gma.client.config;

/**
 * 参数配置
 * <p>
 * Created by wanglin on 2018/1/9.
 */

public interface ParamConstants {

    //微信支付常量
    String WXPAY_APPID = "wxae3306b1fdcca166";
    String WXPAY_PARTNER_ID = "1512677831";
    String PLATFORM_ANDROID = "ANDROID";
    String WXPAY_PACKAGE_VALUE = "Sign=WXPay";

    String SIGNEOS_MEMO_SUFFIX = "-EOS1234bb567aeaf0b681bfb99aa426ef9656e16889012b";

    int TYPE_APP_ID_CYBEX = 1;//cybex
    int TYPE_APP_ID_BEIJIN_TEAM = 2;//北京团队
    int TYPE_APP_ID_BLUETOOTH = 4;//蓝牙卡设备

    int APP_ID_GEMMA = 1;

    int ALARM_JOB = 1;
    int POLLING_JOB = 2;
    int BLUETOOTH_CONNECT_JOB = 3;

    int VALID_EOSNAME_LENGTH = 12;

    String KEY_CUR_TRANSFER = "curTransfer";
    String SAME_WALLET_NAME = "钱包名称不能与已有钱包名相同";
    String EMPTY_WALLET_NAME = "钱包名称不能为空！";
    String CHANGE_NAME_SUCCESS = "更改成功";

    float PROGRESS_ALERT = 85;//progress bar 中大于85%使用
    float PROGRESS_MAX = 100;
    float PROGRESS_MIN = 0;

    int BP_NODE_NUMBERS = 999;//投票时获取的bp节点数量

    boolean NODE_SELECTED = true;//节点被选择
    boolean NODE_NOT_SELECT = false;//节点未被选择


    String GESTURE_SKIP_TYPE = "gesture_skip_type";//手势设置类型
    int GESTURE_SKIP_TYPE_CHANGE = 0x1001;//修改手势密码
    int GESTURE_SKIP_TYPE_LOGIN_VERIFY = 0x1002;//登录验证手势密码
    int GESTURE_SKIP_TYPE_CLOSE = 0x1003;//关闭手势密码

    int REQUEST_CODE_CHANGE_LANGUAGE = 1001;
    int CODE_CHANGE_RESULT = 1002;

    /**
     * 交易状态：1：未确认 2：正在确认 3：已确认 4: 交易失败
     */
    int STATUS_NOT_CONFIRMED = 1;
    int STATUS_CONFIRMING = 2;
    int STATUS_CONFIRMED = 3;
    int STATUS_FAIL = 4;

    String CN = "中文";
    String EN = "English";

    String EOS_ERR_CODE_PREFIX = "eos_err_code_";

    /**
     * 微信支付状态判断
     */

    int WX_NOTPAY_INIT = 10;//未付款
    int WX_NOTPAY_CLOSED = 11;//超时未付款订单已关闭
    int WX_SUCCESS_DONE = 12;//付款成功
    int WX_SUCCESS_TOREFUND = 13;//支付成功但需要退款
    int WX_USERPAYING = 16;//用户正在付款

    int WX_REFUND = 14;
    int WX_CLOSED = 15;
    int WX_PAYERROR = 17;

    String CONTEXT_HANDLE = "contextHandle";

    String KEY_GEEN_SEED = "key_geen_seed";

    String KEY_BLUETOOTH_ACCOUNT_INFO = "bluetooth_account_info";

    String DEVICE_NAME = "deviceName";


    //指纹指令错误
    int FINGER_PRINT_COMMAND_ERROR = -2147483599;
    //指纹冗余
    int FINGER_REDUNDANT = -2147483598;
    //指纹录入成功
    int FINGER_GOOD = -2147483597;
    //指纹录入失败
    int FINGER_NOT = -2147483596;
    //指纹采集不全
    int FINGER_NOT_FULL = -2147483595;
    //指纹采集错误图片
    int FINGER_PRINT_BAND_IMAGE = -2147483594;
    //指纹录入成功
    int FINGER_SUCCESS = 0;

    String RAM_UNIT_PRICE = "ramUnitPrice";
    String EOS_ASSET_VALUE = "eosAssetsValue";
    String EOS_AMOUNT = "eosAmount";

    String EOS_TOKENS = "eosTokens";

    String EOS_USERNAME = "account_name";
    String EOS_PUBLIC_KEY = "eos_pub_key";

    String COIN_TYPE = "coinType";

    int COIN_TYPE_EOS = 5;
    int COIN_TYPE_TOKENS = 6;

    //EOS用户名状态
    int EOSNAME_NOT_ACTIVATED = 0;
    int EOSNAME_CONFIRMING = 1;
    int EOSNAME_ACTIVATED = 2;

    int TRANSFER_HISTORY_SIZE = 20;//每次请求拿20条数据

    String SYMBOL_EOS = "EOS";
    String CONTRACT_EOS = "eosio.token";

    String RESOURCE_VO = "resourceInfoVo";
    String EOS_TOKEN_TYPE = "eosTokenType";


}
