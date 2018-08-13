package com.cybex.gma.client.ui.model.response;

import com.google.gson.annotations.SerializedName;

/**
 * 链上服务器--->获取账户信息
 *
 * Created by wanglin on 2018/7/11.
 */
public class AccountInfo extends BaseOnchainModel {

    /**
     * 账户名
     */
    private String account_name;

    private String head_block_num;
    private int ram_quota;
    private int net_weight;
    private int cpu_weight;

    /**
     * 有多少的net cpu 和ram 相关资产信息
     */
    private AccountTotalResources total_resources;

    /**
     * //正在赎回 可能没有
     */
    private AccountRefoundRequest refund_request;

    private NetLimitBean net_limit;
    private CpuLimitBean cpu_limit;
    private int ram_usage;
    /**
     * self_delegated_bandwidth : {"from":"awesome14","to":"awesome14","net_weight":"100.0000 EOS","cpu_weight":"100.0000 EOS"}
     */

    private SelfDelegatedBandwidthBean self_delegated_bandwidth;

    public NetLimitBean getNet_limit() { return net_limit;}

    public void setNet_limit(NetLimitBean net_limit) { this.net_limit = net_limit;}

    public CpuLimitBean getCpu_limit() { return cpu_limit;}

    public void setCpu_limit(CpuLimitBean cpu_limit) { this.cpu_limit = cpu_limit;}

    public int getRam_usage() { return ram_usage;}

    public void setRam_usage(int ram_usage) { this.ram_usage = ram_usage;}

    public SelfDelegatedBandwidthBean getSelf_delegated_bandwidth() { return self_delegated_bandwidth;}

    public void setSelf_delegated_bandwidth(SelfDelegatedBandwidthBean self_delegated_bandwidth) { this.self_delegated_bandwidth = self_delegated_bandwidth;}

    public static class NetLimitBean {

        private int used;
        private int available;
        private int max;

        public int getUsed() { return used;}

        public void setUsed(int used) { this.used = used;}

        public int getAvailable() { return available;}

        public void setAvailable(int available) { this.available = available;}

        public int getMax() { return max;}

        public void setMax(int max) { this.max = max;}
    }

    public static class CpuLimitBean {

        private int used;
        private int available;
        private int max;

        public int getUsed() { return used;}

        public void setUsed(int used) { this.used = used;}

        public int getAvailable() { return available;}

        public void setAvailable(int available) { this.available = available;}

        public int getMax() { return max;}

        public void setMax(int max) { this.max = max;}
    }

    public static class SelfDelegatedBandwidthBean {

        private String from;
        private String to;
        @SerializedName("net_weight") private String net_weightX;
        @SerializedName("cpu_weight") private String cpu_weightX;

        public String getFrom() { return from;}

        public void setFrom(String from) { this.from = from;}

        public String getTo() { return to;}

        public void setTo(String to) { this.to = to;}

        public String getNet_weightX() { return net_weightX;}

        public void setNet_weightX(String net_weightX) { this.net_weightX = net_weightX;}

        public String getCpu_weightX() { return cpu_weightX;}

        public void setCpu_weightX(String cpu_weightX) { this.cpu_weightX = cpu_weightX;}
    }

    public String getAccount_name() {
        return account_name;
    }

    public void setAccount_name(String account_name) {
        this.account_name = account_name;
    }

    public String getHead_block_num() {
        return head_block_num;
    }

    public void setHead_block_num(String head_block_num) {
        this.head_block_num = head_block_num;
    }

    public AccountTotalResources getTotal_resources() {
        return total_resources;
    }

    public void setTotal_resources(AccountTotalResources total_resources) {
        this.total_resources = total_resources;
    }

    public int getRam_quota() {
        return ram_quota;
    }

    public void setRam_quota(int ram_quota) {
        this.ram_quota = ram_quota;
    }

    public int getNet_weight() {
        return net_weight;
    }

    public void setNet_weight(int net_weight) {
        this.net_weight = net_weight;
    }

    public int getCpu_weight() {
        return cpu_weight;
    }

    public void setCpu_weight(int cpu_weight) {
        this.cpu_weight = cpu_weight;
    }

    public AccountRefoundRequest getRefund_request() {
        return refund_request;
    }

    public void setRefund_request(AccountRefoundRequest refund_request) {
        this.refund_request = refund_request;
    }
}
