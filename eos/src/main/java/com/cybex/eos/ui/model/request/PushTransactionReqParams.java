package com.cybex.eos.ui.model.request;



import com.cybex.eos.ui.model.vo.TransferTransactionVO;

import java.util.List;

/**
 * Created by wanglin on 2018/8/1.
 */
public class PushTransactionReqParams {

    private String compression;
    private TransferTransactionVO transaction;
    private List<String> signatures;


    public String getCompression() {
        return compression;
    }

    public void setCompression(String compression) {
        this.compression = compression;
    }

    public TransferTransactionVO getTransaction() {
        return transaction;
    }

    public void setTransaction(TransferTransactionVO transaction) {
        this.transaction = transaction;
    }

    public List<String> getSignatures() {
        return signatures;
    }

    public void setSignatures(List<String> signatures) {
        this.signatures = signatures;
    }
}
