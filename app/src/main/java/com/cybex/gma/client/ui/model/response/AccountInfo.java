package com.cybex.gma.client.ui.model.response;

/**
 * 链上服务器--->获取账户信息
 *
 * Created by wanglin on 2018/7/11.
 */
public class AccountInfo extends BaseOnchainModel {

    /**
     * 账户名
     */
    public String account_name;

    /**
     * 有多少的net cpu 和ram 相关资产信息
     */
    public AccountTotalResources total_resources;

    /**
     * //正在赎回 可能没有
     */
    public AccountRefoundRequest refund_request;


}
