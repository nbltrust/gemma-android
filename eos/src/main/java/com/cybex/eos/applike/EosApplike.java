package com.cybex.eos.applike;

import com.cybex.componentservice.service.EosWalletService;
import com.cybex.eos.compouirouter.EosUIRouer;
import com.cybex.eos.serviceImpl.EosWalletServiceImpl;
import com.mrzhang.component.componentlib.applicationlike.IApplicationLike;
import com.mrzhang.component.componentlib.router.Router;
import com.mrzhang.component.componentlib.router.ui.UIRouter;
//import com.luojilab.component.componentlib.applicationlike.IApplicationLike;
//import com.luojilab.component.componentlib.router.Router;
//import com.luojilab.component.componentlib.router.ui.UIRouter;

public class EosApplike implements IApplicationLike {

    Router router = Router.getInstance();
    UIRouter uiRouter = UIRouter.getInstance();
    EosUIRouer eosUIRouer=EosUIRouer.getInstance();

    @Override
    public void onCreate() {
        uiRouter.registerUI(eosUIRouer);
        router.addService(EosWalletService.class.getSimpleName(), new EosWalletServiceImpl());
    }

    @Override
    public void onStop() {
        uiRouter.unregisterUI(eosUIRouer);
        router.removeService(EosWalletService.class.getSimpleName());
    }
}
