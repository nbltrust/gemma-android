package com.cybex.componentservice.db.dao;

import com.raizlabs.android.dbflow.list.FlowCursorList;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;

/**
 * 数据访问对象,抽取基本方法
 *
 * Created by wanglin on 2018/1/23.
 */

public abstract class BaseDao<T extends BaseModel> {

    protected void insert(T t) {

    }

    protected void batchInsert(List<T> entityList) {

    }

    protected void delete(T t) {

    }

    protected void update(T t) {

    }

    protected FlowCursorList<T> getAll() {
        return null;
    }

    protected T get(int id) {
        return null;
    }

}
