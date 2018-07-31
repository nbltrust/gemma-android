package com.cybex.gma.client.ui.presenter;

import com.cybex.gma.client.api.callback.JsonCallback;
import com.cybex.gma.client.config.HttpConst;
import com.cybex.gma.client.ui.fragment.TransferRecordListFragment;
import com.cybex.gma.client.ui.model.request.TransferRecordReqParams;
import com.cybex.gma.client.ui.model.response.TransferHistory;
import com.cybex.gma.client.ui.model.response.TransferHistoryList;
import com.cybex.gma.client.ui.model.response.TransferHistoryListData;
import com.cybex.gma.client.ui.request.TransferHistoryListRequest;
import com.hxlx.core.lib.mvp.lite.XPresenter;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.GsonUtils;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanglin on 2018/7/11.
 */
public class TransferRecordListPresenter extends XPresenter<TransferRecordListFragment> {

    public TransferHistoryList requestHistory(final String account_name, final int last_pos, boolean isFirstLoad) {
        TransferHistoryList historyList = null;

        TransferRecordReqParams params = new TransferRecordReqParams();
        params.account_name = account_name;
        params.last_pos = last_pos;
        params.show_num = HttpConst.PAGE_NUM;
        String json = GsonUtils.objectToJson(params);

        new TransferHistoryListRequest(TransferHistoryListData.class)
                .setJsonParams(json)
                .getTransferHistory(new JsonCallback<TransferHistoryListData>() {
                    @Override
                    public void onStart(Request<TransferHistoryListData, ? extends Request> request) {
                        super.onStart(request);
                        if(isFirstLoad){
                            getV().showLoading();
                        }

                    }

                    @Override
                    public void onError(Response<TransferHistoryListData> response) {
                        super.onError(response);
                        getV().showError();
                    }

                    @Override
                    public void onSuccess(Response<TransferHistoryListData> response) {
                        getV().setFirstLoad(false);
                        if (response != null && response.body() != null) {
                            TransferHistoryListData data = response.body();
                            if (data != null && data.code == HttpConst.CODE_RESULT_SUCCESS) {
                                if (data != null && data.result != null) {
                                    TransferHistoryList transferHistoryList = data.result;
                                    int lastPos = transferHistoryList.last_pos;
                                    List<TransferHistory> historyList = transferHistoryList.transactions;
                                    List<TransferHistory> newList = buildNewData(lastPos, historyList);
                                    getV().showContent();
                                    if (last_pos == HttpConst.ACTION_REFRESH) {
                                        //刷新数据
                                        getV().refreshData(newList);
                                    } else {
                                        //加载更多数据
                                        getV().loadMoreData(newList);
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
                });


        /**
         new TransferHistoryListRequest(TransferHistoryList.class)
         .setJsonParams(json)
         .postJson(new CustomRequestCallback<TransferHistoryList>() {
        @Override public void onBeforeRequest(@NonNull Disposable disposable) {
        getV().showLoading();
        }

        @Override public void onSuccess(@NonNull CustomData<TransferHistoryList> data) {
        if (data != null && data.result != null) {
        TransferHistoryList transferHistoryList = data.result;
        int lastPos = transferHistoryList.last_pos;
        List<TransferHistory> historyList = transferHistoryList.transactions;
        List<TransferHistory> newList = buildNewData(lastPos, historyList);
        getV().showContent();
        if (last_pos == HttpConst.ACTION_REFRESH) {
        //刷新数据
        getV().refreshData(newList);
        } else {
        //加载更多数据
        getV().loadMoreData(newList);
        }
        } else {
        getV().showEmptyOrFinish();

        }
        }

        @Override public void onError(@NonNull Throwable e) {
        getV().showError();

        }


        @Override public void onComplete() {

        }
        }); */


        return historyList;
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

        if (EmptyUtils.isEmpty(historyList)) {
            return newList;
        }

        for (int i = 0; i < historyList.size(); i++) {
            TransferHistory history = historyList.get(i);
            history.last_pos = lastPos;

            newList.add(history);
        }

        return newList;
    }

}
