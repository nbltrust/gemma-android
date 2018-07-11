package com.cybex.gma.client.ui.main.presenter;

import com.cybex.gma.client.ui.UISkipMananger;
import com.cybex.gma.client.ui.main.activity.CreateWalletActivity;
import com.hxlx.core.lib.mvp.lite.XPresenter;

public class CreateWalletPresenter extends XPresenter<CreateWalletActivity> {

    private CreateWalletActivity curActivity = getV();
    private final UISkipMananger uiSkipMananger = new UISkipMananger();
    @Override
    protected CreateWalletActivity getV() {
        return super.getV();
    }


}
