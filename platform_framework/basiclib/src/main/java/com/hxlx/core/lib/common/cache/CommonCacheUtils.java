package com.hxlx.core.lib.common.cache;

import android.support.annotation.NonNull;


public class CommonCacheUtils {

    /**
     * 保存key和value到内存缓存和文件缓存
     *
     * @param key   保存的key
     * @param value 保存的value
     * @param <T>   对应的实体对象
     */
    public static <T> void saveCache(String key, @NonNull T value) {
        CacheUtil.put(key, value);
    }

    /**
     * 根据key获取对象
     * 先从内存缓存提取，取不到再从文件缓存中获取
     *
     * @param key      查找的key
     * @param classOfT 对应的实体对象
     * @param <T>      对应的实体对象
     * @return 实体对象
     */
    public static <T> T getCache(String key, Class<T> classOfT) {
        return CacheUtil.get(key, classOfT);
    }
}
