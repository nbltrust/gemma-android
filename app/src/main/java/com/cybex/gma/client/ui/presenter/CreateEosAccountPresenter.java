package com.cybex.gma.client.ui.presenter;

import android.os.Bundle;

import com.cybex.componentservice.api.callback.JsonCallback;
import com.cybex.componentservice.db.entity.EosWalletEntity;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.utils.AlertUtil;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.HttpConst;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.activity.CreateEosAccountActivity;
import com.cybex.gma.client.ui.model.request.GetAccountReqParams;
import com.cybex.gma.client.ui.model.response.AccountInfo;
import com.cybex.gma.client.ui.request.GetAccountinfoRequest;
import com.hxlx.core.lib.mvp.lite.XPresenter;
import com.hxlx.core.lib.utils.GsonUtils;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

public class CreateEosAccountPresenter extends XPresenter<CreateEosAccountActivity> {

    public void verifyAccount(String account_name){
        GetAccountReqParams params = new GetAccountReqParams();
        params.setAccount_name(account_name);

        String jsonParams = GsonUtils.objectToJson(params);

        new GetAccountinfoRequest(AccountInfo.class)
                .setJsonParams(jsonParams)
                .getAccountInfo(new JsonCallback<AccountInfo>() {
                    @Override
                    public void onStart(Request<AccountInfo, ? extends Request> request) {
                        super.onStart(request);
                        getV().showProgressDialog(getV().getString(R.string.eos_verifying_account));
                    }

                    @Override
                    public void onSuccess(Response<AccountInfo> response) {
                        getV().dissmisProgressDialog();
                        if (response != null && response.body() != null){
                            AccountInfo accountInfo = response.body();
                            String account_name_onChain = accountInfo.getAccount_name();
                            if (account_name.equals(account_name_onChain)){
                                //找到该用户名，该用户名不可以使用
                                AlertUtil.showLongUrgeAlert(getV(), getV().getString(R.string.eos_name_used));
                            }
                        }
                    }

                    @Override
                    public void onError(Response<AccountInfo> response) {
                        super.onError(response);
                        if (response.code() == HttpConst.SERVER_INTERNAL_ERR){
                            //未找到该用户名，该用户名可以使用
                            EosWalletEntity curEosWallet = getCurEosWallet();
                            if (curEosWallet != null){
                                Bundle bundle = new Bundle();
                                bundle.putString("account_name", getV().getEOSUsername());
                                UISkipMananger.launchChooseActivateMethod(getV(), bundle);
                            }

                        }else {
                            GemmaToastUtils.showLongToast(getV().getString(R.string.eos_tip_check_network));
                        }
                        getV().dissmisProgressDialog();
                    }
                });

    }

    public EosWalletEntity getCurEosWallet(){
        MultiWalletEntity curWallet = DBManager.getInstance().getMultiWalletEntityDao().getCurrentMultiWalletEntity();
        if (curWallet != null && curWallet.getEosWalletEntities().size() > 0){
            return curWallet.getEosWalletEntities().get(0);
        }
        return null;
    }

}
