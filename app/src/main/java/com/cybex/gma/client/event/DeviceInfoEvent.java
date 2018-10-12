package com.cybex.gma.client.event;

public class DeviceInfoEvent {

    public String getEosPublicKey() {
        return eosPublicKey;
    }

    public void setEosPublicKey(String publicKey) {
        this.eosPublicKey = publicKey;
    }

    private String eosPublicKey;//EOS public key
}
