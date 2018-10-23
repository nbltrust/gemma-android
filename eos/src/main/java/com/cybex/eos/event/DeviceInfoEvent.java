package com.cybex.eos.event;

public class DeviceInfoEvent {

    public String getEosPublicKey() {
        return eosPublicKey;
    }

    public void setEosPublicKey(String publicKey) {
        this.eosPublicKey = publicKey;
    }

    private String eosPublicKey;//EOS public key
}
