package com.cybex.componentservice.event;

public class DeviceConnectStatusUpdateEvent {

    public int status;
    public String deviceName;

    public DeviceConnectStatusUpdateEvent() {
    }

    public DeviceConnectStatusUpdateEvent(int status, String deviceName) {

        this.status = status;
        this.deviceName = deviceName;
    }
}
