package com.cybex.gma.client.ui.presenter;

import com.cybex.gma.client.db.entity.WalletEntity;
import com.cybex.gma.client.manager.DBManager;
import com.cybex.gma.client.manager.LoggerManager;
import com.cybex.gma.client.ui.fragment.TransferFragment;
import com.cybex.gma.client.ui.model.request.GetAccountReqParams;
import com.cybex.gma.client.ui.request.GetCurrencyBalanceRequest;
import com.hxlx.core.lib.mvp.lite.XPresenter;
import com.hxlx.core.lib.utils.GsonUtils;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * 转账presenter
 *
 * Created by wanglin on 2018/7/9.
 */
public class TransferPresenter extends XPresenter<TransferFragment> {

    public void requestAccountInfo() {
        WalletEntity entity = DBManager.getInstance().getWalletEntityDao().getCurrentWalletEntity();
        if (entity == null) { return; }

        String currentEOSName = entity.getCurrentEosName();
        GetAccountReqParams params = new GetAccountReqParams();
        params.setAccount_name(currentEOSName);
        String jsonParams = GsonUtils.objectToJson(params);

        new GetCurrencyBalanceRequest(String.class)
                .setJsonParams(jsonParams)
                .getCurrencyBalance(new StringCallback() {
                    @Override
                    public void onStart(Request<String, ? extends Request> request) {
                        super.onStart(request);
                        getV().showProgressDialog("加载中...");
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        getV().dissmisProgressDialog();
                    }

                    @Override
                    public void onSuccess(Response<String> response) {
                        getV().dissmisProgressDialog();

                        String jsonStr = response.body();
                        String banlance = "";
                        LoggerManager.d("json:" + jsonStr);
                        try {
                            JSONArray array = new JSONArray(jsonStr);
                            if (array != null && array.length() > 0) {
                                banlance = array.optString(0);
                                getV().showInitData(banlance);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
    }


}
