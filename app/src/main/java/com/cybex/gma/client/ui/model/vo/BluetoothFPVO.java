package com.cybex.gma.client.ui.model.vo;

public class BluetoothFPVO {

    public String getFingerprintName() {
        return fingerprintName;
    }

    public void setFingerprintName(String fingerpintName) {
        this.fingerprintName = fingerpintName;
    }

    public int getFingerprintIndex() {
        return fingerprintIndex;
    }

    public void setFingerprintIndex(int fingerprintIndex) {
        this.fingerprintIndex = fingerprintIndex;
    }

    private String fingerprintName;//指纹名称
    private int fingerprintIndex;//指纹index
}
