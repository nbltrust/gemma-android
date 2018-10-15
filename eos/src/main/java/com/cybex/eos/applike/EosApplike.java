package com.cybex.eos.applike;

import com.cybex.componentservice.service.EosWalletService;
import com.cybex.eos.serviceImpl.EosWalletServiceImpl;
import com.mrzhang.component.componentlib.applicationlike.IApplicationLike;
import com.mrzhang.component.componentlib.router.Router;

public class EosApplike implements IApplicationLike {

    Router router = Router.getInstance();

    @Override
    public void onCreate() {
        router.addService(EosWalletService.class.getSimpleName(), new EosWalletServiceImpl());
    }

    @Override
    public void onStop() {
        router.removeService(EosWalletService.class.getSimpleName());
    }
}
