package com.cybex.eos.ui.model.response;

/**
 * 获得C++库需要的配置信息
 *
 * Created by wanglin on 2018/7/13.
 */
public class EOSConfigInfo {
    public String server_version;
    public String chain_id;
    public int head_block_num;
    public int last_irreversible_block_num;
    public String last_irreversible_block_id;
    public String head_block_id;
    public String head_block_time;
    public String head_block_producer;
    public int virtual_block_cpu_limit;
    public int virtual_block_net_limit;
    public int block_cpu_limit;
    public int block_net_limit;
}
