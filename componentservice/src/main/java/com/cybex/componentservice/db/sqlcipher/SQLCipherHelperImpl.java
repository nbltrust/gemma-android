package com.cybex.componentservice.db.sqlcipher;

import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.sqlcipher.SQLCipherOpenHelper;
import com.raizlabs.android.dbflow.structure.database.DatabaseHelperListener;

/**
 * Created by wanglin on 2018/7/24.
 */
public class SQLCipherHelperImpl  extends SQLCipherOpenHelper {

    public SQLCipherHelperImpl(DatabaseDefinition databaseDefinition, DatabaseHelperListener listener) {
        super(databaseDefinition, listener);
    }

    @Override
    protected String getCipherSecret() {
        //TODO
        return "gemma-db-rules";
    }
}