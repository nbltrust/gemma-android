package com.cybex.componentservice.event;

import com.cybex.componentservice.db.entity.MultiWalletEntity;

/**
 * 刷新钱包密码的数据
 */
public class RefreshWalletPswEvent {
    private MultiWalletEntity currentWallet;



    public RefreshWalletPswEvent() {

    }

    public RefreshWalletPswEvent(MultiWalletEntity currentWallet) {

        this.currentWallet = currentWallet;
    }

    public MultiWalletEntity getCurrentWallet() {
        return currentWallet;
    }

    public void setCurrentWallet(MultiWalletEntity currentWallet) {
        this.currentWallet = currentWallet;
    }

}
