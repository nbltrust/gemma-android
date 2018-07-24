package com.cybex.gma.client.manager;

import com.cybex.gma.client.db.dao.WalletEntityDao;
import com.cybex.gma.client.db.dao.impl.WalletEntityDaoImpl;

/**
 * Created by wanglin on 2018/1/23.
 */
public class DBManager {

    private static volatile DBManager mInstance = null;

    public static DBManager getInstance() {
        DBManager tempInstance = mInstance;
        if (tempInstance == null) {
            synchronized (DBManager.class) {
                tempInstance = mInstance;
                if (tempInstance == null) {
                    tempInstance = new DBManager();
                    mInstance = tempInstance;
                }
            }
        }
        // 返回临时变量
        return tempInstance;
    }

    public WalletEntityDao getMediaBeanDao() {
        return new WalletEntityDaoImpl();
    }


}
