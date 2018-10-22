package com.cybex.walletmanagement.event;

public class QrResultEvent {

    public boolean isMnemonicType;

    public String result;

    public QrResultEvent(boolean isMnemonicType, String result) {
        this.isMnemonicType = isMnemonicType;
        this.result = result;
    }

    public boolean isMnemonicType() {
        return isMnemonicType;
    }

    public void setMnemonicType(boolean mnemonicType) {
        isMnemonicType = mnemonicType;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
