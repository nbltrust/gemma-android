package com.cybex.componentservice.db.dao.impl;


import com.cybex.componentservice.db.GemmaDatabase;
import com.cybex.componentservice.db.dao.MultiWalletEntityDao;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.db.entity.MultiWalletEntity_Table;
import com.cybex.componentservice.db.util.DBCallback;
import com.cybex.componentservice.db.util.DBFlowUtil;
import com.cybex.componentservice.db.util.OperationType;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;

import java.util.List;

public class MultiWalletEntityDaoImpl implements MultiWalletEntityDao {

    @Override
    public void deleteEntity(MultiWalletEntity entity) {
        if (EmptyUtils.isEmpty(entity)) { return; }

        entity.delete();
    }

    @Override
    public MultiWalletEntity getCurrentMultiWalletEntity() {
        MultiWalletEntity entity = SQLite.select().from(MultiWalletEntity.class)
                .where(MultiWalletEntity_Table.isCurrentWallet.eq(1))
                .querySingle();
        return entity;
    }

    @Override
    public MultiWalletEntity getMultiWalletEntity(String walletName) {
        MultiWalletEntity entity = SQLite.select().from(MultiWalletEntity.class)
                .where(MultiWalletEntity_Table.walletName.eq(walletName))
                .querySingle();
        return entity;
    }

    @Override
    public MultiWalletEntity getMultiWalletEntityByID(int id) {
        MultiWalletEntity entity = SQLite.select().from(MultiWalletEntity.class)
                .where(MultiWalletEntity_Table.id.eq(id))
                .querySingle();
        return entity;
    }

    @Override
    public List<MultiWalletEntity> getMultiWalletEntityList() {
        List<MultiWalletEntity> list =
                SQLite.select().from(MultiWalletEntity.class)
                        .queryList();

        return list;
    }

    @Override
    public List<MultiWalletEntity> getBluetoothWalletList() {
        List<MultiWalletEntity> list =
                SQLite.select().from(MultiWalletEntity.class)
                        .where(MultiWalletEntity_Table.walletType.eq(1))
                        .queryList();

        return list;
    }

    @Override
    public void saveOrUpateEntity(final MultiWalletEntity entity) {
        DBFlowUtil.execTransactionAsync(GemmaDatabase.class, new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                entity.save();
            }
        });

    }

    @Override
    public void batchSaveEntityListSync(List<MultiWalletEntity> list) {
        ITransaction transaction = DBFlowUtil.getTransaction(list, OperationType.TYPE_SAVE);
        DBFlowUtil.execTransactionSync(GemmaDatabase.class, transaction);
    }

    @Override
    public void batchSaveEntityListASync(List<MultiWalletEntity> list, DBCallback callback) {
        if (list == null || list.size() == 0) {
            if (null != callback) {
                callback.onFailed(new Throwable("object is null"));
            }
            return;
        }
        ITransaction transaction = DBFlowUtil.getTransaction(list, OperationType.TYPE_SAVE);
        DBFlowUtil.execTransactionAsync(GemmaDatabase.class, transaction, callback);
    }
}
