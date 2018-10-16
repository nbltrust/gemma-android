package com.cybex.walletmanagement.applike;

import com.mrzhang.component.componentlib.applicationlike.IApplicationLike;
import com.mrzhang.component.componentlib.router.Router;

public class WalletManagementApplike implements IApplicationLike {

    Router router = Router.getInstance();

    @Override
    public void onCreate() {
//        router.addService(EosWalletService.class.getSimpleName(), new EosWalletServiceImpl());
    }

    @Override
    public void onStop() {
//        router.removeService(EosWalletService.class.getSimpleName());
    }
}

