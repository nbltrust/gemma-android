package com.cybex.componentservice.event;

import com.cybex.componentservice.db.entity.MultiWalletEntity;

/**
 * 刷新某个钱包的数据
 */
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