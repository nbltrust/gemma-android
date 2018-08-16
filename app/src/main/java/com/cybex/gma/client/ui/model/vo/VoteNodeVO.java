package com.cybex.gma.client.ui.model.vo;

/**
 * 投票节点VO对象
 *
 * Created by wanglin on 2018/8/14.
 */
public class VoteNodeVO {

    public boolean ischecked = false;//是否被选择

    private String account;//账户名
    private String alias;//别名
    private String votes;
    private String url;//地址
    private String percentage;//占票百分比
    private int ranking;//排名


    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getVotes() {
        return votes;
    }

    public void setVotes(String votes) {
        this.votes = votes;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }
}
