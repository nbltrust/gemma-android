package com.cybex.gma.client.ui.presenter;

import com.cybex.componentservice.api.callback.JsonCallback;
import com.cybex.gma.client.config.HttpConst;
import com.cybex.gma.client.ui.activity.WalletHomeActivity;
import com.cybex.gma.client.ui.model.request.GetkeyAccountReqParams;
import com.cybex.gma.client.ui.model.response.GetKeyAccountsResult;
import com.cybex.gma.client.ui.request.GetKeyAccountsRequest;
import com.hxlx.core.lib.mvp.lite.XPresenter;
import com.hxlx.core.lib.utils.GsonUtils;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

public class WalletHomePresenter extends XPresenter<WalletHomeActivity> {

    /**
     * 根据公钥查询对应的eos账户
     * @param public_key
     */
    public void getKeyAccounts(String public_key){
        GetkeyAccountReqParams params = new GetkeyAccountReqParams();
        params.setPublic_key(public_key);
        String jsonParams = GsonUtils.objectToJson(params);

        new GetKeyAccountsRequest(GetKeyAccountsResult.class)
                .setJsonParams(jsonParams)
                .getKeyAccountsRequest(new JsonCallback<GetKeyAccountsResult>() {
                    @Override
                    public void onStart(Request<GetKeyAccountsResult, ? extends Request> request) {
                        if (getV() != null){
                            super.onStart(request);
                            getV().showProgressDialog("正在核验EOS账户");
                        }
                    }

                    @Override
                    public void onSuccess(Response<GetKeyAccountsResult> response) {
                        if (getV() != null){
                            if (response != null && response.body() != null && response.code() != HttpConst.SERVER_INTERNAL_ERR){
                                //找到此账号
                            /*
                            GetKeyAccountsResult result = response.body();
                            List<String> account_names = result.account_names;
                            final String curEOSName = account_names.get(0);
                            */
                            getV().setEOSActivated(true);

                            }else if (response != null && response.body() != null && response.code() == HttpConst
                                    .SERVER_INTERNAL_ERR ){
                                //未找到此账号

                            }
                            getV().dissmisProgressDialog();
                        }

                    }

                    @Override
                    public void onError(Response<GetKeyAccountsResult> response) {
                        //未找到此账号
                        if (getV() != null){
                            super.onError(response);
                            getV().dissmisProgressDialog();
                        }
                    }
                });
    }
}
