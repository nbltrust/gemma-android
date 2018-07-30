package com.cybex.gma.client.ui.model.response;

/**
 * 交易记录对象
 *
 * Created by wanglin on 2018/7/12.
 */
public class TransferHistory {

    public int action_seq;
    public String from;
    public String to;
    public String value;
    public String memo;
    public String time;
    public String hash;
    public int block;
    /**
     * 交易状态：1：未确认 2：正在确认 3：已确认 4: 交易失败
     */
    public int status;
    public int last_pos;
}
