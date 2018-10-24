package com.cybex.walletmanagement.event;

import com.cybex.componentservice.bean.CoinType;
import com.cybex.componentservice.db.entity.MultiWalletEntity;

/**
 * 导入私钥时，选择导入的哪个钱包 事件
 */
public class SelectImportWhichWalletEvent {
    public MultiWalletEntity walletEntity;

    public SelectImportWhichWalletEvent() {
    }

    public SelectImportWhichWalletEvent(MultiWalletEntity walletEntity) {

        this.walletEntity = walletEntity;
    }
}
