package com.cybex.eos.applike;

import com.cybex.componentservice.service.EosWalletService;
import com.cybex.eos.serviceImpl.EosWalletServiceImpl;
import com.luojilab.component.componentlib.applicationlike.IApplicationLike;
import com.luojilab.component.componentlib.router.Router;
import com.luojilab.component.componentlib.router.ui.UIRouter;

public class EosApplike implements IApplicationLike {

    Router router = Router.getInstance();
    UIRouter uiRouter = UIRouter.getInstance();

    @Override
    public void onCreate() {
        uiRouter.registerUI("reader");
        router.addService(EosWalletService.class.getSimpleName(), new EosWalletServiceImpl());
    }

    @Override
    public void onStop() {
        uiRouter.unregisterUI("reader");
        router.removeService(EosWalletService.class.getSimpleName());
    }
}
