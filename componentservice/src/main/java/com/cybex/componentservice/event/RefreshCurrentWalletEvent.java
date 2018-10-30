package com.cybex.componentservice.event;

import com.cybex.componentservice.db.entity.MultiWalletEntity;

public class RefreshCurrentWalletEvent {
    private MultiWalletEntity currentWallet;

    public RefreshCurrentWalletEvent() {

    }

    public RefreshCurrentWalletEvent(MultiWalletEntity currentWallet) {

        this.currentWallet = currentWallet;
    }

    public MultiWalletEntity getCurrentWallet() {
        return currentWallet;
    }

    public void setCurrentWallet(MultiWalletEntity currentWallet) {
        this.currentWallet = currentWallet;
    }
}
