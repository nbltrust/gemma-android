package com.cybex.gma.client.ui.presenter;

import com.cybex.gma.client.ui.fragment.WalletFragment;
import com.cybex.gma.client.utils.encryptation.EncryptationManager;
import com.hxlx.core.lib.mvp.lite.XPresenter;

/**
 * 钱包Presenter
 *
 * Created by wanglin on 2018/7/9.
 */
public class WalletPresenter extends XPresenter<WalletFragment>{
  public void generatePotrait(String eosName){
      String hash = EncryptationManager.getEncrypt().encryptSHA256(eosName);

  }

}
