package com.cybex.gma.client.ui.presenter;

import com.cybex.componentservice.api.callback.JsonCallback;
import com.cybex.componentservice.bean.TokenBean;
import com.cybex.componentservice.db.entity.EosWalletEntity;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.HttpConst;
import com.cybex.gma.client.ui.activity.EosAssetDetailActivity;
import com.cybex.gma.client.ui.model.request.GetCurrencyBalanceReqParams;
import com.cybex.gma.client.ui.model.response.GetEosTokensResult;
import com.cybex.gma.client.ui.model.response.TransferHistory;
import com.cybex.gma.client.ui.model.response.TransferHistoryList;
import com.cybex.gma.client.ui.model.response.TransferHistoryListData;
import com.cybex.gma.client.ui.request.GetCurrencyBalanceRequest;
import com.cybex.gma.client.ui.request.GetEosTokensRequest;
import com.cybex.gma.client.ui.request.TransferHistoryListRequest;
import com.hxlx.core.lib.mvp.lite.XPresenter;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.GsonUtils;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.exceptions.Exceptions;

public class AssetDetailPresenter extends XPresenter<EosAssetDetailActivity> {

    private static final String VALUE_CODE_EOS = "eosio.token";
    private static final String VALUE_SYMBOL_EOS = "EOS";

    public void requestHistory(
            final String account_name,
            final int page,
            final int size,
            final String symbol,
            final String contract,
            final boolean isEos) {

        new TransferHistoryListRequest(TransferHistoryListData.class, account_name, page, size, symbol,
                contract)
                .getTransferHistory(new JsonCallback<TransferHistoryListData>() {
                    @Override
                    public void onStart(Request<TransferHistoryListData, ? extends Request> request) {
                        if (getV() != null) {
                            super.onStart(request);
                            if (page == 1) {
                                getV().showLoading();
                            }
                        }
                    }

                    @Override
                    public void onError(Response<TransferHistoryListData> response) {
                        if (getV() != null) {
                            super.onError(response);
                            getV().showError();
                        }
                    }

                    @Override
                    public void onSuccess(Response<TransferHistoryListData> response) {
                        if (getV() != null) {
                            if (response != null && response.body() != null) {
                                TransferHistoryListData data = response.body();
                                if (data != null && data.code == HttpConst.CODE_RESULT_SUCCESS) {
                                    if (data.result != null) {
                                        TransferHistoryList transferHistoryList = data.result;
                                        //int trace_count = transferHistoryList.trace_count;
                                        List<TransferHistory> historyList = transferHistoryList.trace_list;

                                        if (page == 1) {
                                            getV().refreshData(historyList);
                                        } else {
                                            if (historyList == null || historyList.size() == 0) {
                                                //没有更多数据
                                                getV().showEmptyOrFinish();
                                                GemmaToastUtils.showShortToast(getV().getString(R.string.no_more_data));
                                            } else {
                                                getV().loadMoreData(historyList);
                                            }
                                        }

                                        if (isEos){
                                            requestEosBalance();
                                        }else {
                                            requestTokenBalance(account_name, contract, symbol);
                                        }


                                    } else {
                                        getV().showEmptyOrFinish();

                                    }

                                } else {
                                    getV().showEmptyOrFinish();
                                }
                            } else {
                                getV().showEmptyOrFinish();
                            }
                        }

                    }
                });

        //return historyList;
    }

    /**
     * 重新组装数据
     *
     * @param lastPos
     * @param historyList
     * @return
     */
    private List<TransferHistory> buildNewData(int lastPos, List<TransferHistory> historyList) {
        List<TransferHistory> newList = new ArrayList<>();
//
//        if (EmptyUtils.isEmpty(historyList)) {
//            return newList;
//        }
//
//        for (int i = 0; i < historyList.size(); i++) {
//            TransferHistory history = historyList.get(i);
//            history.last_pos = lastPos;
//
//            newList.add(history);
//        }
//
        return newList;
    }

    public void requestEosBalance() {
        MultiWalletEntity entity = DBManager.getInstance().getMultiWalletEntityDao().getCurrentMultiWalletEntity();
        EosWalletEntity eosEntity = entity.getEosWalletEntities().get(0);
        if (eosEntity == null) { return; }

        String currentEOSName = eosEntity.getCurrentEosName();
        GetCurrencyBalanceReqParams params = new GetCurrencyBalanceReqParams();
        params.setAccount(currentEOSName);
        params.setCode(VALUE_CODE_EOS);
        params.setSymbol(VALUE_SYMBOL_EOS);
        String jsonParams = GsonUtils.objectToJson(params);

        new GetCurrencyBalanceRequest(String.class)
                .setJsonParams(jsonParams)
                .getCurrencyBalance(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {

                            if (response != null) {
                                String jsonStr = response.body();
                                //LoggerManager.d("response json:" + jsonStr);

                                JSONArray array = new JSONArray(jsonStr);
                                if (array.length() > 0) {
                                    String balance = array.optString(0);
                                    if (EmptyUtils.isNotEmpty(balance) && EmptyUtils.isNotEmpty(getV())) {
                                        getV().showBalance(balance);
                                        getV().showContent();
                                    }
                                }else {
                                    getV().showBalance("0.0000");
                                    getV().showContent();
                                }
                            }else{
                                getV().showEmptyOrFinish();
                            }


                        } catch (Throwable t) {
                            throw Exceptions.propagate(t);
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        if (getV() != null){
                            super.onError(response);
                            getV().showEmptyOrFinish();
                        }
                    }
                });
    }

    public void requestTokenBalance(String account_name, String token_contract, String token_symbol) {
        GetEosTokensRequest request = new GetEosTokensRequest(GetEosTokensResult.class, account_name)
                .getEosTokens(new JsonCallback<GetEosTokensResult>() {
                    @Override
                    public void onStart(Request<GetEosTokensResult, ? extends Request> request) {
                        super.onStart(request);
                    }

                    @Override
                    public void onSuccess(Response<GetEosTokensResult> data) {
                        if (getV() != null){
                            if (data != null){
                                GetEosTokensResult response = data.body();
                                if (response.getResult() != null){
                                    GetEosTokensResult.ResultBean resultBean = response.getResult();
                                    List<TokenBean> tokens = resultBean.getTokens();
                                    //更新UI
                                    for (TokenBean token : tokens){
                                        if (token.getSymbol().equals(token_symbol)
                                                && token.getCode().equals(token_contract)){
                                            String tokenBalance = String.valueOf(token.getBalance());
                                            getV().showBalance(tokenBalance);
                                            getV().showContent();
                                        }
                                    }
                                }else {
                                    getV().showEmptyOrFinish();
                                }
                            }else {
                                getV().showEmptyOrFinish();
                            }
                            GemmaToastUtils.showLongToast(getV().getString(R.string.eos_loading_success));
                            getV().dissmisProgressDialog();
                        }
                    }

                    @Override
                    public void onError(Response<GetEosTokensResult> response) {
                        if (getV() != null){
                            super.onError(response);
                            getV().showEmptyOrFinish();
                            GemmaToastUtils.showLongToast(getV().getString(R.string.eos_load_account_info_fail));
                        }
                    }
                });


    }
}
