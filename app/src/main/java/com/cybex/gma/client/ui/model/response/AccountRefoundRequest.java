package com.cybex.gma.client.ui.model.response;

/**
 * Created by wanglin on 2018/7/13.
 */
public class AccountRefoundRequest {

    public String owner;
    /**
     * 上一次请求时间 需要加上72小时跟本地时间对比
     */
    public String request_time;
    public String net_amount;
    public String cpu_amount;

}



