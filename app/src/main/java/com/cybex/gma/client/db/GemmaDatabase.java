package com.cybex.gma.client.db;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by wanglin on 2018/7/24.
 */
@Database(name = GemmaDatabase.DB_NAME, version = GemmaDatabase.DB_VERSION, foreignKeyConstraintsEnforced = true)
public class GemmaDatabase {

    public static final String DB_NAME = "gemma_database";

    public static final int DB_VERSION = 1;

}
