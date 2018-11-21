package com.cybex.componentservice.event;


public class BluetoothChangePinEvent {


    private int walletId;
    private String psw;

    public BluetoothChangePinEvent() {

    }

    public BluetoothChangePinEvent(int walletId, String psw) {
        this.walletId = walletId;
        this.psw = psw;
    }


    public int getWalletId() {
        return walletId;
    }

    public void setWalletId(int walletId) {
        this.walletId = walletId;
    }

    public String getPsw() {
        return psw;
    }

    public void setPsw(String psw) {
        this.psw = psw;
    }

}
