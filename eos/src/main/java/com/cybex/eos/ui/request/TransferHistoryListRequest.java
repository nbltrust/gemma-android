package com.cybex.eos.ui.request;

import com.cybex.componentservice.api.ApiMethod;
import com.cybex.componentservice.api.ApiPath;
import com.cybex.componentservice.api.callback.JsonCallback;
import com.cybex.componentservice.api.request.GMAHttpRequest;
import com.cybex.eos.ui.model.response.TransferHistoryListData;


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
    public TransferHistoryListRequest(Class clazz, String account, int pos, int number) {
        super(clazz);
        setMethod(ApiPath.HOST_CENTER_SERVER + ApiMethod.API_GET_TRANSACTION_HISTORY + account + "/" + pos + "/" +
                number);
    }


    /*
    public TransferHistoryListRequest setJsonParams(String jsonParams) {
        super.setJsonParams(jsonParams);
        return this;
    }
    */

    public TransferHistoryListRequest getTransferHistory(JsonCallback<TransferHistoryListData> callback) {
        getJsonNoRxRequest(TAG, callback);
        //postJsonNoRxRequest(TAG, callback);

        return this;
    }
}
