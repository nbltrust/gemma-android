package com.cybex.componentservice.event;

public class WalletNameChangedEvent {

    private int walletID;
    private String walletName;

    public WalletNameChangedEvent(int walletID, String walletName) {
        this.walletID = walletID;
        this.walletName = walletName;
    }

    public WalletNameChangedEvent() {
    }

    public int getWalletID() {
        return walletID;
    }

    public void setWalletID(int walletID) {
        this.walletID = walletID;
    }

    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }
}
