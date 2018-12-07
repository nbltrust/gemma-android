package com.cybex.componentservice.db.dao;


import com.cybex.componentservice.db.entity.EosTransactionEntity;
import com.cybex.componentservice.db.entity.WalletEntity;
import com.cybex.componentservice.db.util.DBCallback;

import java.util.List;

public interface EosTransactionEntityDao {

    void  deleteTransaction(EosTransactionEntity entity);

    EosTransactionEntity getTransactionEntity(String transactionHash);

    EosTransactionEntity getEosTransactionEntityByID(int id);

    List<EosTransactionEntity> getEosTransactionEntityListByHash(String txId);

    List<EosTransactionEntity> getWalletEntityList();

    void saveOrUpateEntity(EosTransactionEntity entity);

}
