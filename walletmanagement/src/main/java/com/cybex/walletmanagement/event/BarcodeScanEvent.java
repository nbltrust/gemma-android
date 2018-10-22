package com.cybex.walletmanagement.event;

/**
 * 二维码扫描事件
 *
 * Created by wanglin on 2018/7/23.
 */
public class BarcodeScanEvent {

    private String result;

    public String getResult() {
        return result;
    }

    public BarcodeScanEvent(String result) {
        this.result = result;
    }
}
