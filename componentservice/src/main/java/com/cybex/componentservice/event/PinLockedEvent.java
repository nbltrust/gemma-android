package com.cybex.componentservice.event;

public class PinLockedEvent {


    public String deviceName;

    public PinLockedEvent(String deviceName) {
        this.deviceName=deviceName;
    }

    public PinLockedEvent() {
    }
}
