package com.cybex.gma.client.config;

/**
 * 参数配置
 * <p>
 * Created by wanglin on 2018/1/9.
 */

public interface ParamConstants {

    int TYPE_APP_ID_CYBEX = 1;//cybex
    int TYPE_APP_ID_BEIJIN_TEAM = 2;//北京团队

    int ALARM_JOB = 1;
    int POLLING_JOB = 2;

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


    int EVENT_DOWN = 1;//事件从上级页面发送
    int EVENT_THIS_PAGE = 0;//事件在本页面发送
    int EVENT_UP = 2;//事件从下级页面发送


    String GESTURE_SKIP_TYPE = "gesture_skip_type";//手势设置类型
    int GESTURE_SKIP_TYPE_CHANGE = 0x1001;//修改手势密码
    int GESTURE_SKIP_TYPE_LOGIN_VERIFY = 0X1002;//登录验证手势密码
    int GESTURE_SKIP_TYPE_CLOSE =0X1003;//关闭手势密码

    int REQUEST_CODE_CHANGE_LANGUAGE = 1001;
    int CODE_CHANGE_RESULT = 1002;

    /**
     * 交易状态：1：未确认 2：正在确认 3：已确认 4: 交易失败
     */
    int STATUS_NOT_CONFIRMED = 1;
    int STATUS_CONFIRMING = 2;
    int STATUS_CONFIRMED = 3;
    int STATUS_FAIL = 4;

    /**
     *导入钱包：1，创建钱包：0
     */
    int OPERATION_CREATE = 0;
    int OPERATION_IMPORT = 1;
}
