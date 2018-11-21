package com.cybex.componentservice.event;

public class WookongFpChangedNameEvent {

    private int walletID;
    private int index;
    private String fpName;

    public WookongFpChangedNameEvent(int walletID,int index, String fpName) {
        this.walletID = walletID;
        this.index = index;
        this.fpName = fpName;
    }

    public WookongFpChangedNameEvent() {
    }

    public int getWalletID() {
        return walletID;
    }

    public void setWalletID(int walletID) {
        this.walletID = walletID;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getFpName() {
        return fpName;
    }

    public void setFpName(String fpName) {
        this.fpName = fpName;
    }
}
