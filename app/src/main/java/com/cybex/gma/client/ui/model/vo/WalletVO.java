package com.cybex.gma.client.ui.model.vo;

public class WalletVO {

    private String walletName;

    private int walletType;

    public boolean isSelected = false;

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public String getWalletName() {
        return walletName;
    }

    public int getWalletType() {
        return walletType;
    }

    public void setWalletType(int walletType) {
        this.walletType = walletType;
    }
}
