package com.cybex.gma.client.db.util;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.FastStoreModelTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import static com.cybex.gma.client.db.util.OperationType.TYPE_DELETE;
import static com.cybex.gma.client.db.util.OperationType.TYPE_INSERT;
import static com.cybex.gma.client.db.util.OperationType.TYPE_SAVE;
import static com.cybex.gma.client.db.util.OperationType.TYPE_UPDATE;


/**
 * DBFlow 操作工具
 *
 * Created by wanglin on 2018/1/18.
 */

public class DBFlowUtil {


    public static <T> void save(Class clazz, T obj) {
        FlowManager.getModelAdapter(clazz).save(obj);
    }

    /**
     * 同步事务
     *
     * @param classDb
     * @param iTransaction
     */
    public static void execTransactionSync(Class classDb, ITransaction iTransaction) {
        FlowManager.getDatabase(classDb).executeTransaction(iTransaction);
    }

    /**
     * 异步事务
     *
     * @param classDb
     * @param iTransaction
     */
    public static void execTransactionAsync(Class classDb, ITransaction iTransaction) {
        FlowManager.getDatabase(classDb)
                .beginTransactionAsync(iTransaction) // add elements (can also handle multiple)
                .error(new Transaction.Error() {
                    @Override
                    public void onError(Transaction transaction, Throwable error) {}
                })
                .success(new Transaction.Success() {
                    @Override
                    public void onSuccess(Transaction transaction) {}
                })
                .build()
                .execute();
    }

    /**
     * 异步事务执行，带执行结果回调
     *
     * @param classDb
     * @param iTransaction
     * @param callback
     */
    public static void execTransactionAsync(
            Class classDb, ITransaction iTransaction,
            final DBCallback callback) {
        FlowManager.getDatabase(classDb)
                .beginTransactionAsync(iTransaction) // add elements (can also handle multiple)
                .error(new Transaction.Error() {
                    @Override
                    public void onError(Transaction transaction, Throwable error) {
                        if (null != callback) {
                            callback.onFailed(error);
                        }
                    }
                })
                .success(new Transaction.Success() {
                    @Override
                    public void onSuccess(Transaction transaction) {
                        if (null != callback) {
                            callback.onSucceed();
                        }
                    }
                })
                .build()
                .execute();
    }

    public static ITransaction getTransaction(
            List<? extends BaseModel> baseModels,
            @OperationType.TransactionType final int type) {
        ProcessModelTransaction processModelTransaction = new ProcessModelTransaction.Builder<>(
                new ProcessModelTransaction.ProcessModel<BaseModel>() {

                    @Override
                    public void processModel(BaseModel baseModel, DatabaseWrapper wrapper) {
                        switch (type) {
                            case TYPE_SAVE:
                                baseModel.save(wrapper);
                                break;
                            case TYPE_INSERT:
                                baseModel.insert(wrapper);
                                break;
                            case TYPE_UPDATE:
                                baseModel.update(wrapper);
                                break;
                            case TYPE_DELETE:
                                baseModel.delete(wrapper);
                                break;

                        }
                    }
                })
                .addAll(baseModels)
                .build();
        return processModelTransaction;
    }

    /**
     * 快速执行事务
     *
     * @param baseModels
     * @param type
     * @param <T>
     * @return
     */
    public static <T> ITransaction getFastStoreTransaction(
            List<? extends BaseModel> baseModels,
            @OperationType.TransactionType final int type) {
        Class<T> entityClass;
        Type genType = baseModels.get(0).getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        entityClass = (Class) params[0];

        ITransaction transaction = null;
        switch (type) {
            case TYPE_SAVE:
                transaction = FastStoreModelTransaction
                        .saveBuilder(FlowManager.getModelAdapter(entityClass))
                        .addAll((Collection<? extends T>) baseModels)
                        .build();
                break;
            case TYPE_INSERT:
                transaction = FastStoreModelTransaction
                        .insertBuilder(FlowManager.getModelAdapter(entityClass))
                        .addAll((Collection<? extends T>) baseModels)
                        .build();

                break;
            case TYPE_UPDATE:
                transaction = FastStoreModelTransaction
                        .updateBuilder(FlowManager.getModelAdapter(entityClass))
                        .addAll((Collection<? extends T>) baseModels)
                        .build();

                break;
            case TYPE_DELETE:
                transaction = FastStoreModelTransaction
                        .deleteBuilder(FlowManager.getModelAdapter(entityClass))
                        .addAll((Collection<? extends T>) baseModels)
                        .build();
                break;

        }

        return transaction;
    }
}
