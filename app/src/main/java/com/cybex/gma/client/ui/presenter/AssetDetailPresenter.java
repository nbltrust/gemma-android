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
import com.cybex.gma.client.ui.model.response.GetEosTokensResult;
import com.cybex.gma.client.ui.model.response.GetEosTransactionResult;
import com.cybex.gma.client.ui.model.response.TransferHistory;
import com.cybex.gma.client.ui.model.response.TransferHistoryList;
import com.cybex.gma.client.ui.model.response.TransferHistoryListData;
import com.cybex.gma.client.ui.request.EOSConfigInfoRequest;
import com.cybex.gma.client.ui.request.GetCurrencyBalanceRequest;
import com.cybex.gma.client.ui.request.GetEosTokensRequest;
import com.cybex.gma.client.ui.request.GetEosTransactionRequest;
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
    private static String curLib = null;

    /**
     * 获取链上配置信息
     * 需要拿到head block num 和lib
     */
    public void getInfo(
            final String account_name,
            final int page,
            final int size,
            final String symbol,
            final String contract,
            final boolean isEos) {
        new EOSConfigInfoRequest(String.class)
                .getInfo(new StringCallback() {

                    @Override
                    public void onStart(Request<String, ? extends Request> request) {
                        LoggerManager.d(" get Info");
                        super.onStart(request);
                        if (getV() != null) {
                            super.onStart(request);
                            if (page == 1) {
                                getV().showLoading();
                            }
                        }

                    }

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
                                        curLib = lib_num;
//                                        LoggerManager.d("lib", lib_num);
//                                        LoggerManager.d("head_block_num", head_block_num);
                                        getV().setCurLib(lib_num);
                                        getV().setmRecyclerViewOnClick();
                                        requestHistory(account_name, page, size, symbol, contract, isEos);

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    }
                });
    }


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
//                        if (getV() != null) {
//                            super.onStart(request);
//                            if (page == 1) {
//                                getV().showLoading();
//                            }
//                        }
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
                                        historyList = clearListStatus(historyList);

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
                                            getV().showContent();

                                        }
                                    } else {

                                        getV().showBalance("0.0000");
                                        getV().showContent();

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

                                        }
                                    }
                                    getV().showContent();
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
        new GetEosTransactionRequest(GetEosTransactionResult.class, txId)
                .getTransaction(new JsonCallback<GetEosTransactionResult>() {
                    @Override
                    public void onSuccess(Response<GetEosTransactionResult> response) {
                        if (getV() != null) {
                            if (response != null && response.body() != null) {
                                String block_num = String.valueOf(response.body().getBlock_num());
                                updateBlockNum(block_num, txId);
                                updateStatus(block_num, curLib, txId);
                                getV().updateTransactionStatus();
                            }
                        }
                    }

                    @Override
                    public void onError(Response<GetEosTransactionResult> response) {
                        if (getV() != null) {
                            super.onError(response);
                            GemmaToastUtils.showShortToast(
                                    getV().getString(R.string.tip_cant_find_transaction));
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

            if (!Alerter.isShowing()) {
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
     * 将Block_Num字段更新进数据库中对应的Transaction
     */
    public void updateBlockNum(String block_num, String txId) {
        EosTransactionEntity curTransaction = DBManager.getInstance().getEosTransactionEntityDao()
                .getEosTransactionEntityByHash(txId);

        if (curTransaction != null) {
            curTransaction.setBlockNumber(block_num);
            DBManager.getInstance().getEosTransactionEntityDao().saveOrUpateEntity(curTransaction);
        }

    }

    /**
     * 通过比对更新状态字段进数据库中对应的Transaction
     */
    public void updateStatus(String block_num, String cur_lib, String txId) {
        EosTransactionEntity curTransaction = DBManager.getInstance().getEosTransactionEntityDao()
                .getEosTransactionEntityByHash(txId);

        if (curTransaction != null) {
            int status = getTransactionStatus(block_num, cur_lib);
            curTransaction.setStatus(status);
            DBManager.getInstance().getEosTransactionEntityDao().saveOrUpateEntity(curTransaction);
        }
    }

    /**
     * 通过比对确定该Transaction的状态
     */
    public int getTransactionStatus(String block_num, String cur_lib) {
        int block = Integer.valueOf(block_num);
        int lib = Integer.valueOf(cur_lib);

        if (block > lib) {
            //pending
            return ParamConstants.TRANSACTION_STATUS_PENDING;
        } else {
            //done
            return ParamConstants.TRANSACTION_STATUS_CONFIRMED;
        }
    }

    /**
     * 把对应dataList中所有Action所对应的Transaction实体存入数据库
     *
     * @param dataList
     */
    public void saveTransaction(List<TransferHistory> dataList) {
        for (TransferHistory curTransfer : dataList) {
            EosTransactionEntity curTransaction = DBManager.getInstance().getEosTransactionEntityDao()
                    .getEosTransactionEntityByHash(curTransfer.trx_id);

            if (curTransaction != null) {
                continue;
            }

            curTransaction = new EosTransactionEntity();
            curTransaction.setTransactionHash(curTransfer.trx_id);
            DBManager.getInstance().getEosTransactionEntityDao().saveOrUpateEntity(curTransaction);
        }
    }

    public List<TransferHistory> clearListStatus(List<TransferHistory> list){
        for (TransferHistory curTransfer : list){
            curTransfer.status = null;
        }
        return list;
    }

    /**
     * 更新Adapter的数据源
     */
    public List<TransferHistory> updateDataSource(List<TransferHistory> dataList) {
        List<TransferHistory> newDataList = new ArrayList<>();
        for (TransferHistory curTransfer : dataList) {
            EosTransactionEntity curTransaction = DBManager.getInstance().getEosTransactionEntityDao()
                    .getEosTransactionEntityByHash(curTransfer.trx_id);

            if (curTransaction != null) {
                //todo 根据不同类型设置status的值
                String currentEosName = DBManager.getInstance().getMultiWalletEntityDao().getCurrentMultiWalletEntity
                        ().getEosWalletEntities().get(0).getCurrentEosName();

                if(EmptyUtils.isNotEmpty(curTransaction.getStatus())){
                    int status = curTransaction.getStatus();
                    if (currentEosName != null) {

                        if (status == ParamConstants.TRANSACTION_STATUS_PENDING) {
                            //正在确认
                            if (curTransfer.sender.equals(currentEosName)) {
                                //转出 -
                                curTransfer.status = getV().getString(R.string.status_confirming);

                            } else {
                                //转入 +
                                curTransfer.status =  getV().getString(R.string.status_confirming);;
                            }
                        } else if (status == ParamConstants.TRANSACTION_STATUS_CONFIRMED) {
                            //已经确认
                            if (curTransfer.sender.equals(currentEosName)) {
                                //转出 -
                                curTransfer.status = getV().getString(R.string.status_sent);

                            } else {
                                //转入 +
                                curTransfer.status = getV().getString(R.string.status_accepted);
                            }
                        }else if (status == ParamConstants.TRANSACTION_STATUS_FAIL){
                            //已经失败
                            if (curTransfer.sender.equals(currentEosName)) {
                                //转出 -
                                curTransfer.status = getV().getString(R.string.status_send_fail);

                            } else {
                                //转入 +
                                curTransfer.status = getV().getString(R.string.status_accept_fail);
                            }
                        }

                    }

                }else {
                    //status为空
                    curTransfer.status = getV().getString(R.string.status_unknown);
                }


                newDataList.add(curTransfer);
            }

        }
        return newDataList;
    }

    /**
     * 拿当前显示数据中状态不确定的项进行查询
     */
    public void queryStatus(List<TransferHistory> dataList) {

        List<String> hashList = new ArrayList<>();
        for (TransferHistory curTransfer : dataList) {
            if (hashList.contains(curTransfer.trx_id)) { continue; }
            hashList.add(curTransfer.trx_id);
        }

        for (String curHash : hashList) {
            EosTransactionEntity curTransaction = DBManager.getInstance().getEosTransactionEntityDao()
                    .getEosTransactionEntityByHash(curHash);

            if (curTransaction != null) {
                if (curTransaction.getStatus() == null) {
                    //如果数据库中有Transaction且状态未确定
                    getTransaction(curHash);
                }
            }

        }
    }


}
