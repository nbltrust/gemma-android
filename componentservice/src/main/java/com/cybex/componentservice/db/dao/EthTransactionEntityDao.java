package com.cybex.componentservice.db.dao;


import com.cybex.componentservice.db.entity.EthTransactionEntity;

import java.util.List;

public interface EthTransactionEntityDao {

    void  deleteTransaction(EthTransactionEntity entity);

    EthTransactionEntity getTransactionEntity(String transactionHash);

    EthTransactionEntity getEthTransactionEntityByID(int id);

    List<EthTransactionEntity> getWalletEntityList();

    void saveOrUpateEntity(EthTransactionEntity entity);

}
