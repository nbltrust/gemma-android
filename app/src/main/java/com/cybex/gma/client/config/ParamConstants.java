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

    int BP_NODE_NUMBERS = 999;//投票时获取的bp节点数量

    boolean NODE_SELECTED = true;//节点被选择
    boolean NODE_NOT_SELECT = false;//节点未被选择

}
