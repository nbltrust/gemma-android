package com.cybex.eos.ui.model.response;

/**
 * 总资产信息
 *
 * Created by wanglin on 2018/7/13.
 */
public class AccountTotalResources {

    private String owner;
    private String net_weight;
    private String cpu_weight;
    private int ram_bytes;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getNet_weight() {
        return net_weight;
    }

    public void setNet_weight(String net_weight) {
        this.net_weight = net_weight;
    }

    public String getCpu_weight() {
        return cpu_weight;
    }

    public void setCpu_weight(String cpu_weight) {
        this.cpu_weight = cpu_weight;
    }

    public int getRam_bytes() {
        return ram_bytes;
    }

    public void setRam_bytes(int ram_bytes) {
        this.ram_bytes = ram_bytes;
    }
}
