package com.cybex.gma.client.ui.model.vo;

public class NodeInfoVO {

    private String node_name;//节点名称
    private String alias;
    private String slogon;//宣传标语
    private String percentage;//得票比例
    private String ranking;//排名

    public String getNode_name() {
        return node_name;
    }

    public void setNode_name(String node_name) {
        this.node_name = node_name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public String getRanking() {
        return ranking;
    }

    public void setRanking(String ranking) {
        this.ranking = ranking;
    }

    public String getSlogon() {
        return slogon;
    }

    public void setSlogon(String slogon) {
        this.slogon = slogon;
    }
}
