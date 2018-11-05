package com.cybex.eth.ui.model;

public class EthTransferRecord {

    public int action_seq;
    public String from;
    public String to;
    public String value;//交易金额
    public String memo;
    public String time;//交易时间
    public String hash;//交易哈希 txid
    public int block;//交易区块号
    /**
     * 交易状态：1：未确认 2：正在确认 3：已确认 4: 交易失败
     */
    public int status;
    public int last_pos;

}
