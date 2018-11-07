package com.cybex.eth.event;

public class GasSettingEvent {

    public long gas;
    public long gasPrice;

    public GasSettingEvent() {
    }

    public GasSettingEvent(long gas, long gasPrice) {

        this.gas = gas;
        this.gasPrice = gasPrice;
    }
}
