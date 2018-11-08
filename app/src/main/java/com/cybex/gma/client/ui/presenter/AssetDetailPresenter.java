package com.cybex.gma.client.ui.presenter;

import com.cybex.componentservice.api.callback.JsonCallback;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.HttpConst;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.ui.activity.EosAssetDetailActivity;
import com.cybex.gma.client.ui.model.response.TransferHistory;
import com.cybex.gma.client.ui.model.response.TransferHistoryList;
import com.cybex.gma.client.ui.model.response.TransferHistoryListData;
import com.cybex.gma.client.ui.request.TransferHistoryListRequest;
import com.hxlx.core.lib.mvp.lite.XPresenter;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.GsonUtils;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import java.util.ArrayList;
import java.util.List;

public class AssetDetailPresenter extends XPresenter<EosAssetDetailActivity> {

    public void requestHistory(
            final String account_name,
            final int page,
            final int size,
            final String symbol,
            final String contract) {

        new TransferHistoryListRequest(TransferHistoryListData.class, account_name, page, ParamConstants.TRANSFER_HISTORY_SIZE, symbol,
                contract)
                .getTransferHistory(new JsonCallback<TransferHistoryListData>() {
                    @Override
                    public void onStart(Request<TransferHistoryListData, ? extends Request> request) {
                        if (getV() != null){
                            super.onStart(request);
                            if  ( page == 1) {
                                getV().showLoading();
                            }
                        }
                    }

                    @Override
                    public void onError(Response<TransferHistoryListData> response) {
                        if (getV() != null){
                            super.onError(response);
                            getV().showError();
                        }
                    }

                    @Override
                    public void onSuccess(Response<TransferHistoryListData> response) {
                        if (getV() != null){
                            if (response != null && response.body() != null) {
                                TransferHistoryListData data = response.body();
                                if (data != null && data.code == HttpConst.CODE_RESULT_SUCCESS) {
                                    if (data.result != null) {
                                        TransferHistoryList transferHistoryList = data.result;
                                        //int trace_count = transferHistoryList.trace_count;
                                        List<TransferHistory> historyList = transferHistoryList.trace_list;

                                        if (page == 1){
                                            getV().refreshData(historyList);
                                        }else {
                                            if (historyList == null){
                                                //没有更多数据
                                                getV().showEmptyOrFinish();
                                                GemmaToastUtils.showShortToast(getV().getString(R.string.no_more_data));
                                            }else {
                                                getV().loadMoreData(historyList);
                                            }
                                        }

                                        getV().showContent();

//                                        List<TransferHistory> newList = buildNewData(lastPos, historyList);
//                                        if (last_pos == HttpConst.ACTION_REFRESH) {
//                                            //刷新数据
//                                            getV().refreshData(newList);
//                                        } else {
//                                            //加载更多数据
//                                            if(EmptyUtils.isNotEmpty(newList)){
//                                                getV().loadMoreData(newList);
//                                            }else{
//                                                getV().showEmptyOrFinish();
//                                            }
//                                        }
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
}
