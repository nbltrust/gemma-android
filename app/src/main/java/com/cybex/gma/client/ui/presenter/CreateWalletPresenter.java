package com.cybex.gma.client.ui.presenter;

import android.support.annotation.NonNull;

import com.cybex.gma.client.api.callback.CustomRequestCallback;
import com.cybex.gma.client.api.data.response.CustomData;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.ui.UISkipMananger;
import com.cybex.gma.client.ui.activity.CreateWalletActivity;
import com.cybex.gma.client.ui.activity.MainTabActivity;
import com.cybex.gma.client.ui.model.request.UserRegisterReqParams;
import com.cybex.gma.client.ui.request.UserRegisterRequest;
import com.hxlx.core.lib.mvp.lite.XPresenter;
import com.hxlx.core.lib.utils.GsonUtils;
import com.hxlx.core.lib.utils.android.logger.Log;

import io.reactivex.disposables.Disposable;

public class CreateWalletPresenter extends XPresenter<CreateWalletActivity> {

    @Override
    protected CreateWalletActivity getV() {
        return super.getV();
    }


    public void createAccount(String accountname, String invitationCode, String publicKey) {
        UserRegisterReqParams params = new UserRegisterReqParams();
        params.setApp_id(ParamConstants.TYPE_APP_ID_CYBEX);
        params.setAccount_name(accountname);
        params.setInvitation_code(invitationCode);
        params.setPublic_key(publicKey);

        String json = GsonUtils.objectToJson(params);

        new UserRegisterRequest(CustomData.class)
                .setRegisterJsonParams(json)
                .postJson(new CustomRequestCallback<CustomData>() {
                    @Override
                    public void onBeforeRequest(@NonNull Disposable disposable) {
                        getV().showProgressDialog("正在创建...");
                    }

                    @Override
                    public void onSuccess(@NonNull CustomData<CustomData> result) {
                        getV().dissmisProgressDialog();
                        Log.d("createAccount", "onSuccess");

                        if(result.code == 0){
                            UISkipMananger.launchIntent(getV(), MainTabActivity.class);
                        }else{
                            getV().showOnErrorInfo();
                        }

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        getV().dissmisProgressDialog();

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }


}
