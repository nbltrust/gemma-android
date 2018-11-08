package com.cybex.gma.client.ui.model.response;

import java.util.List;

/**
 * 交易历史列表对象
 *
 * Created by wanglin on 2018/7/13.
 */
public class TransferHistoryList {

    //public int last_pos;

    public int trace_count;//账户对应该代币总共多少action

    public List<TransferHistory> trace_list;

}
