package com.cybex.gma.client.ui.request;

import com.cybex.gma.client.api.ApiMethod;
import com.cybex.gma.client.api.ApiPath;
import com.cybex.gma.client.api.callback.JsonCallback;
import com.cybex.gma.client.api.request.GMAHttpRequest;
import com.cybex.gma.client.ui.model.response.TransferHistoryListData;

/**
 * 账户交易历史
 *
 * Created by wanglin on 2018/7/13.
 */
public class TransferHistoryListRequest extends GMAHttpRequest<TransferHistoryListData> {

    public static final String TAG = "transfer_history";

    /**
     * @param clazz 想要请求返回的Bean
     */
    public TransferHistoryListRequest(Class clazz) {
        super(clazz);
        setMethod(ApiPath.HOST_CENTER_SERVER + ApiMethod.API_GET_TRANSACTION_HISTORY);
    }


    public TransferHistoryListRequest setJsonParams(String jsonParams) {
        super.setJsonParams(jsonParams);
        return this;
    }

    public TransferHistoryListRequest getTransferHistory(JsonCallback<TransferHistoryListData> callback) {
        postJsonNoRxRequest(TAG, callback);

        return this;
    }
}
