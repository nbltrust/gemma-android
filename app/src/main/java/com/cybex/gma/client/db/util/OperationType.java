package com.cybex.gma.client.db.util;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 事务执行类型
 *
 * Created by wanglin on 2018/1/18.
 */

public class OperationType {


    /**
     * save 操作
     */
    public static final int TYPE_SAVE = 0;

    /**
     * insert 操作
     */
    public static final int TYPE_INSERT = 1;

    /**
     * update 操作
     */
    public static final int TYPE_UPDATE = 2;

    /**
     * delete 操作
     */
    public static final int TYPE_DELETE = 3;


    @IntDef(flag = true,value = {TYPE_SAVE,TYPE_INSERT,TYPE_UPDATE,TYPE_DELETE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TransactionType{}

}
