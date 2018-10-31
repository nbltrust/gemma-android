package com.cybex.componentservice.event;

import com.cybex.componentservice.db.entity.MultiWalletEntity;

/**
 * 更换当前已选的钱包
 */
public class ChangeSelectedWalletEvent {
    private MultiWalletEntity currentWallet;

    public ChangeSelectedWalletEvent() {

    }

    public ChangeSelectedWalletEvent(MultiWalletEntity currentWallet) {

        this.currentWallet = currentWallet;
    }

    public MultiWalletEntity getCurrentWallet() {
        return currentWallet;
    }

    public void setCurrentWallet(MultiWalletEntity currentWallet) {
        this.currentWallet = currentWallet;
    }

}
