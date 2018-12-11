package com.cybex.componentservice.db.dao.impl;

import com.cybex.componentservice.db.GemmaDatabase;
import com.cybex.componentservice.db.dao.EosTransactionEntityDao;
import com.cybex.componentservice.db.entity.EosTransactionEntity;
import com.cybex.componentservice.db.entity.EosTransactionEntity_Table;
import com.cybex.componentservice.db.util.DBFlowUtil;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;

import java.util.ArrayList;
import java.util.List;

public class EosTransactionEntityDaoImpl implements EosTransactionEntityDao{
    @Override
    public void deleteTransaction(EosTransactionEntity entity) {
        if (EmptyUtils.isEmpty(entity)) { return; }

        entity.delete();
    }

    @Override
    public EosTransactionEntity getTransactionEntity(String transactionHash) {
        EosTransactionEntity entity = SQLite.select().from(EosTransactionEntity.class)
                .where(EosTransactionEntity_Table.transactionHash.eq(transactionHash))
                .querySingle();
        return entity;
    }

    @Override
    public EosTransactionEntity getEosTransactionEntityByID(int id) {
        EosTransactionEntity entity = SQLite.select().from(EosTransactionEntity.class)
                .where(EosTransactionEntity_Table.id.eq(id))
                .querySingle();
        return entity;
    }

    @Override
    public EosTransactionEntity getEosTransactionEntityByHash(String txId) {
        EosTransactionEntity entity =  SQLite.select().from(EosTransactionEntity.class)
                .where(EosTransactionEntity_Table.transactionHash.eq(txId))
                .querySingle();
        return entity;
    }

    @Override
    public List<EosTransactionEntity> getWalletEntityList() {
        List<EosTransactionEntity> list =
                SQLite.select().from(EosTransactionEntity.class)
                        .queryList();

        return list;
    }

    @Override
    public void saveOrUpateEntity(EosTransactionEntity entity) {
        DBFlowUtil.execTransactionAsync(GemmaDatabase.class, new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                entity.save();
            }
        });
    }
}
