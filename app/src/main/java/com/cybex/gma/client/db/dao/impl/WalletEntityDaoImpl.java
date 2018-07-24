package com.cybex.gma.client.db.dao.impl;

import com.cybex.gma.client.db.GemmaDatabase;
import com.cybex.gma.client.db.dao.WalletEntityDao;
import com.cybex.gma.client.db.entity.WalletEntity;
import com.cybex.gma.client.db.util.DBCallback;
import com.cybex.gma.client.db.util.DBFlowUtil;
import com.cybex.gma.client.db.util.OperationType;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;

import java.util.List;

/**
 * Created by wanglin on 2018/7/24.
 */
public class WalletEntityDaoImpl implements WalletEntityDao {

    @Override
    public List<WalletEntity> getWalletEntityList() {
        List<WalletEntity> list =
                SQLite.select().from(WalletEntity.class)
                        .queryList();

        return list;
    }

    @Override
    public void saveOrUpateMedia(WalletEntity entity) {
        DBFlowUtil.execTransactionAsync(GemmaDatabase.class, new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                entity.save();
            }
        });

    }

    @Override
    public void batchSaveMediaListSync(List<WalletEntity> list) {
        ITransaction transaction = DBFlowUtil.getTransaction(list, OperationType.TYPE_SAVE);
        DBFlowUtil.execTransactionSync(GemmaDatabase.class, transaction);
    }

    @Override
    public void batchSaveMediaListASync(List<WalletEntity> list, DBCallback callback) {
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
