package com.cybex.eos.ui.model.vo;

public class BluetoothFPVO {

    public String getFingerprintName() {
        return fingerprintName;
    }

    public void setFingerprintName(String fingerpintName) {
        this.fingerprintName = fingerpintName;
    }

    public byte[] getFingerprintIndex() {
        return fingerprintIndex;
    }

    public void setFingerprintIndex(byte[] fingerprintIndex) {
        this.fingerprintIndex = fingerprintIndex;
    }

    private String fingerprintName;//指纹名称
    private byte[] fingerprintIndex;//指纹index
}
