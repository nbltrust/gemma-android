package com.cybex.componentservice.event;

public class DeviceConnectStatusUpdateEvent {

    public static final int STATUS_BLUETOOTH_DISCONNCETED = 0;
    public static final int STATUS_BLUETOOTH_CONNCETED = 1;


    public int status;
    public String deviceName;
    public boolean manual=true;

    public DeviceConnectStatusUpdateEvent() {
    }

    public DeviceConnectStatusUpdateEvent(int status, String deviceName) {

        this.status = status;
        this.deviceName = deviceName;
    }

    public DeviceConnectStatusUpdateEvent(int status, String deviceName,boolean manual) {
        this.status = status;
        this.deviceName = deviceName;
        this.manual = manual;
    }
}
