package com.cybex.gma.client.ui.main.presenter;

import android.widget.EditText;
import android.widget.TextView;

import com.cybex.gma.client.ui.main.activity.CreateWalletActivity;
import com.example.yiran.gemma.Modules.Wallet.CreateWallet.CreateSuccessActivity;
import com.hxlx.core.lib.mvp.lite.XPresenter;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.android.logger.Log;

import java.lang.ref.WeakReference;

public class CreateWalletPresenter extends XPresenter<CreateWalletActivity> {

    private CreateWalletActivity curActivity = getV();
    @Override
    protected CreateWalletActivity getV() {
        return super.getV();
    }

    public void createWallet(){
        Log.d("create wallet", "createWallet executed");
    }

}
