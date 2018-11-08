package com.cybex.gma.client.ui.request;

import com.cybex.componentservice.api.ApiMethod;
import com.cybex.componentservice.api.ApiPath;
import com.cybex.componentservice.api.callback.JsonCallback;
import com.cybex.componentservice.api.request.GMAHttpRequest;
import com.cybex.gma.client.ui.model.response.TransferHistoryListData;

/**
 * 账户交易历史
 *
 * Created by wanglin on 2018/7/13.
 */
public class TransferHistoryListRequest extends GMAHttpRequest<TransferHistoryListData> {

    public static final String TAG = "transfer_history";

    /**
     *
     * @param clazz 返回结果Bean
     * @param account_name Eos用户名
     * @param page 返回页数
     * @param size 返回交易数
     * @param symbol 代币名称
     * @param contract 代币合约
     */
    public TransferHistoryListRequest(Class clazz, String account_name, int page, int size, String symbol, String
            contract) {
        super(clazz);
        setMethod(ApiPath.HOST_CENTER_SERVER + ApiMethod.API_GET_TRANSACTION_HISTORY
                + "?account=" + account_name + "&"
                + "page=" + page + "&"
                + "size=" + size + "&"
                + "symbol=" + symbol + "&"
                + "contract=" + contract);
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
