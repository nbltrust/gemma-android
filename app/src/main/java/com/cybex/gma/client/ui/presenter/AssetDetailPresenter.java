package com.cybex.gma.client.ui.presenter;

import com.cybex.componentservice.api.callback.JsonCallback;
import com.cybex.componentservice.bean.TokenBean;
import com.cybex.componentservice.db.entity.EosTransactionEntity;
import com.cybex.componentservice.db.entity.EosWalletEntity;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.HttpConst;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.ui.activity.EosAssetDetailActivity;
import com.cybex.gma.client.ui.model.request.GetCurrencyBalanceReqParams;
import com.cybex.gma.client.ui.model.request.GetTransactionReqParams;
import com.cybex.gma.client.ui.model.response.GetEosTokensResult;
import com.cybex.gma.client.ui.model.response.TransferHistory;
import com.cybex.gma.client.ui.model.response.TransferHistoryList;
import com.cybex.gma.client.ui.model.response.TransferHistoryListData;
import com.cybex.gma.client.ui.request.EOSConfigInfoRequest;
import com.cybex.gma.client.ui.request.GetCurrencyBalanceRequest;
import com.cybex.gma.client.ui.request.GetEosTokensRequest;
import com.cybex.gma.client.ui.request.GetTransactionRequest;
import com.cybex.gma.client.ui.request.TransferHistoryListRequest;
import com.hxlx.core.lib.mvp.lite.XPresenter;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.GsonUtils;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.tapadoo.alerter.Alert;
import com.tapadoo.alerter.Alerter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
                        LoggerManager.d("request history");
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
                                                //循环请求获取当前Transaction的BlockNum
//                                                for (TransferHistory curTransfer : historyList){
//                                                    getTransaction(curTransfer.trx_id);
//                                                }
                                            }
                                        }

                                        if (isEos) {
                                            requestEosBalance();
                                        } else {
                                            requestTokenBalance(account_name, contract, symbol);
                                        }


                                    } else {
                                        getV().showEmptyOrFinish();

                                    }

                                } else {
                                    getV().showError();
                                }
                            } else {
                                getV().showEmptyOrFinish();
                            }
                        }

                    }
                });

        //return historyList;
    }


    private void requestEosBalance() {
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
                            if (getV() != null) {
                                if (response != null) {
                                    String jsonStr = response.body();
                                    //LoggerManager.d("response json:" + jsonStr);

                                    JSONArray array = new JSONArray(jsonStr);
                                    if (array.length() > 0) {
                                        String balance = array.optString(0);
                                        if (EmptyUtils.isNotEmpty(balance) && EmptyUtils.isNotEmpty(getV())) {

                                            getV().showBalance(balance);
                                            getInfo();

                                        }
                                    } else {

                                        getV().showBalance("0.0000");
                                        getInfo();

                                    }
                                } else {
                                    getV().showEmptyOrFinish();
                                }
                            }
                        } catch (Throwable t) {
                            throw Exceptions.propagate(t);
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        if (getV() != null) {
                            super.onError(response);
                            getV().showEmptyOrFinish();
                        }
                    }
                });
    }

    private void requestTokenBalance(String account_name, String token_contract, String token_symbol) {
        GetEosTokensRequest request = new GetEosTokensRequest(GetEosTokensResult.class, account_name)
                .getEosTokens(new JsonCallback<GetEosTokensResult>() {
                    @Override
                    public void onStart(Request<GetEosTokensResult, ? extends Request> request) {
                        super.onStart(request);
                    }

                    @Override
                    public void onSuccess(Response<GetEosTokensResult> data) {
                        if (getV() != null) {
                            if (data != null) {
                                GetEosTokensResult response = data.body();
                                if (response.getResult() != null) {
                                    GetEosTokensResult.ResultBean resultBean = response.getResult();
                                    List<TokenBean> tokens = resultBean.getTokens();
                                    //更新UI
                                    for (TokenBean token : tokens) {
                                        if (token.getSymbol().equals(token_symbol)
                                                && token.getCode().equals(token_contract)) {
                                            String tokenBalance = String.valueOf(token.getBalance());
                                            getV().showBalance(tokenBalance);
                                            getInfo();
//                                            getV().showContent();
                                        }
                                    }
                                } else {
                                    getV().showEmptyOrFinish();
                                }
                            } else {
                                getV().showEmptyOrFinish();
                            }
                            GemmaToastUtils.showLongToast(getV().getString(R.string.eos_loading_success));
                            getV().dissmisProgressDialog();
                        }
                    }

                    @Override
                    public void onError(Response<GetEosTokensResult> response) {
                        if (getV() != null) {
                            super.onError(response);
                            getV().showEmptyOrFinish();
                            GemmaToastUtils.showLongToast(getV().getString(R.string.eos_load_account_info_fail));
                        }
                    }
                });


    }


    /**
     * @return 返回block_num
     */

    public void getTransaction(String txId) {
        LoggerManager.d("Transaction id", txId);
        GetTransactionReqParams reqParams = new GetTransactionReqParams();
        reqParams.setid(txId);
        String jsonParams = GsonUtils.objectToJson(reqParams);
        new GetTransactionRequest(String.class)
                .setJsonParams(jsonParams)
                .getInfo(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null && response.body() != null) {
                            String infoJson = response.body();
                            try {
                                JSONObject obj = new JSONObject(infoJson);
                                if (obj != null) {
                                    String block_num = obj.optString("block_num");
                                    LoggerManager.d("block_num", block_num);
                                    //存入数据库

                                    EosTransactionEntity eosTransactionEntity = new DBManager()
                                            .getEosTransactionEntityDao().getEosTransactionEntityByHash(txId);
                                    eosTransactionEntity.setBlockNumber(block_num);
                                    getV().setTransactionStatus(eosTransactionEntity);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                        if (EmptyUtils.isNotEmpty(getV())) {
//                            GemmaToastUtils.showShortToast(getV().getString(R.string.operate_deal_failed));
//                            getV().dissmisProgressDialog();

                            try {
                                String err_info_string = response.getRawResponse().body().string();
                                try {
                                    JSONObject obj = new JSONObject(err_info_string);
                                    JSONObject error = obj.optJSONObject("error");
                                    String err_code = error.optString("code");
                                    handleEosErrorCode(err_code);

                                } catch (JSONException ee) {
                                    ee.printStackTrace();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

    }

    /**
     * 获取链上配置信息
     * 需要拿到head block num 和lib
     */
    public void getInfo() {
        new EOSConfigInfoRequest(String.class)
                .getInfo(new StringCallback() {

                    @Override
                    public void onError(Response<String> response) {
                        if (EmptyUtils.isNotEmpty(getV())) {
                            super.onError(response);
                            GemmaToastUtils.showShortToast(getV().getString(R.string.operate_deal_failed));
                            getV().dissmisProgressDialog();

                            try {
                                String err_info_string = response.getRawResponse().body().string();
                                try {
                                    JSONObject obj = new JSONObject(err_info_string);
                                    JSONObject error = obj.optJSONObject("error");
                                    String err_code = error.optString("code");
                                    handleEosErrorCode(err_code);

                                } catch (JSONException ee) {
                                    ee.printStackTrace();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onSuccess(Response<String> response) {
                        if (getV() != null) {
                            if (response != null && response.body() != null) {
                                String infoJson = response.body();
                                try {
                                    JSONObject obj = new JSONObject(infoJson);
                                    if (obj != null) {
//                                        String head_block_num = obj.optString("head_block_num");
                                        String lib_num = obj.optString("last_irreversible_block_num");
//                                        LoggerManager.d("lib", lib_num);
//                                        LoggerManager.d("head_block_num", head_block_num);
                                        getV().setCurLib(lib_num);
                                        getV().setmRecyclerViewOnClick();
                                        getV().showContent();

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    }
                });
    }


    /**
     * 反射机制处理EOS错误码
     *
     * @param err_code
     */
    private void handleEosErrorCode(String err_code) {
        String code = ParamConstants.EOS_ERR_CODE_PREFIX + err_code;
        if (EmptyUtils.isNotEmpty(getV()) && EmptyUtils.isNotEmpty(getV())) {
            String package_name = getV().getPackageName();
            int resId = getV().getResources().getIdentifier(code, "string", package_name);
            String err_info = getV().getResources().getString(resId);

            if (!Alerter.isShowing()){
                Alerter.create(getV())
                        .setText(err_info)
                        .setContentGravity(Alert.TEXT_ALIGNMENT_GRAVITY)
                        .showIcon(false)
                        .setDuration(3000)
                        .setBackgroundColorRes(R.color.scarlet)
                        .show();
            }
        }
    }


    /**
     * 计算该Transaction确认中大致百分比
     *
     * @return
     */
    public double getProgressPrecentage(String block_num, String lib_num) {
        int block = Integer.valueOf(block_num);
        int lib = Integer.valueOf(lib_num);
        return Math.min(1 - Math.min((block - lib) / 325, 1), 0.99) * 100;
    }

    /**
     * 重新组装数据
     * 将TransferHistoryList变为EosTransactionList
     *
     * @return
     */
    public List<EosTransactionEntity> convertTransactionData(List<TransferHistory> transferHistoryList) {
        List<EosTransactionEntity> entityList = new ArrayList<>();
        for (TransferHistory curTransfer : transferHistoryList) {
            EosTransactionEntity curTransaction = new EosTransactionEntity();
            curTransaction.setTransactionHash(curTransfer.trx_id);
            curTransaction.setDate(curTransfer.timestamp);
            curTransaction.setQuantity(curTransfer.quantity);
            curTransaction.setReceiver(curTransfer.receiver);
            curTransaction.setSender(curTransfer.sender);
            curTransaction.setTokenCode(curTransfer.code);
            curTransaction.setTokenSymbol(curTransfer.symbol);
            entityList.add(curTransaction);
        }
        return entityList;
    }

    /**
     * 将Transaction List存入数据库
     *
     * @param list
     */
    public void saveTransactionData(List<EosTransactionEntity> list) {
        for (EosTransactionEntity curTransaction : list) {
            DBManager.getInstance().getEosTransactionEntityDao().saveOrUpateEntity(curTransaction);
        }
    }

}
