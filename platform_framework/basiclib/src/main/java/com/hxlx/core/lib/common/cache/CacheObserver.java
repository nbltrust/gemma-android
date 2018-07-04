package com.hxlx.core.lib.common.cache;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 数据监控器
 * 当数据变化时候，回调所有监听器
 */
public class CacheObserver {

    public static CacheObserver getInstance() {
        return Holder.observer;
    }

    private static class Holder {
        private static CacheObserver observer = new CacheObserver();
    }

    private HashMap<String, List<IDataChangeListener>> mListenerMap = new HashMap<>();

    public synchronized void addObserver(String key, IDataChangeListener listener) {
        if (TextUtils.isEmpty(key) || listener == null) {
            return;
        }
        List<IDataChangeListener> list = mListenerMap.get(key);
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(listener);
        mListenerMap.put(key, list);
    }

    public synchronized void addObserver(String key, List<IDataChangeListener> listeners) {
        if (TextUtils.isEmpty(key) || listeners == null) {
            return;
        }
        List<IDataChangeListener> list = mListenerMap.get(key);
        if (list == null) {
            list = new ArrayList<>();
        }
        list.addAll(listeners);
        mListenerMap.put(key, list);
    }

    public synchronized void removeObserver(String key, IDataChangeListener listener) {
        if (TextUtils.isEmpty(key) || listener == null) {
            return;
        }
        List<IDataChangeListener> list = mListenerMap.get(key);
        if (list == null) {
            return;
        }
        list.remove(listener);
        mListenerMap.put(key, list);
    }

    public synchronized void removeObservers(String key) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        mListenerMap.remove(key);
    }

    public void notifyDataChange(String key, String value) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        List<IDataChangeListener> list = mListenerMap.get(key);
        if (list == null) {
            return;
        }
        int size = list.size();
        for (int i = 0; i < size; i++) {
            list.get(i).onDataChange(key, value);
        }
    }
}
