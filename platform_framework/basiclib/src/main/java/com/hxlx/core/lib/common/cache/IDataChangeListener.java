package com.hxlx.core.lib.common.cache;

/**
 * 数据变化监听
 */
public interface IDataChangeListener {
    /**
     * 数据变化
     */
    void onDataChange(String key, String value);
}
