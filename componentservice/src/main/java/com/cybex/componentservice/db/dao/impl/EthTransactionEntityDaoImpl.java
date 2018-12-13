package com.cybex.componentservice.db.dao.impl;

import com.cybex.componentservice.db.GemmaDatabase;
import com.cybex.componentservice.db.dao.EthTransactionEntityDao;
import com.cybex.componentservice.db.entity.EthTransactionEntity;
import com.cybex.componentservice.db.entity.EthTransactionEntity_Table;
import com.cybex.componentservice.db.util.DBFlowUtil;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;

import java.util.List;

public class EthTransactionEntityDaoImpl implements EthTransactionEntityDao {
    @Override
    public void deleteTransaction(EthTransactionEntity entity) {
        if (EmptyUtils.isEmpty(entity)) { return; }

        entity.delete();
    }

    @Override
    public EthTransactionEntity getTransactionEntity(String transactionHash) {
        EthTransactionEntity entity = SQLite.select().from(EthTransactionEntity.class)
                .where(EthTransactionEntity_Table.transactionHash.eq(transactionHash))
                .querySingle();
        return entity;
    }

    @Override
    public EthTransactionEntity getEthTransactionEntityByID(int id) {
        EthTransactionEntity entity = SQLite.select().from(EthTransactionEntity.class)
                .where(EthTransactionEntity_Table.id.eq(id))
                .querySingle();
        return entity;
    }

    @Override
    public List<EthTransactionEntity> getWalletEntityList() {
        List<EthTransactionEntity> list =
                SQLite.select().from(EthTransactionEntity.class)
                        .queryList();

        return list;
    }

    @Override
    public void saveOrUpateEntity(EthTransactionEntity entity) {
        DBFlowUtil.execTransactionAsync(GemmaDatabase.class, new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                entity.save();
            }
        });
    }
}
