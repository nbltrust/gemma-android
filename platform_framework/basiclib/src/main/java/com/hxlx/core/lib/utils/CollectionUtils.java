package com.hxlx.core.lib.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;


public class CollectionUtils {
    public static final int INVALID_INDEX = -1;

    /**
     * replace all elements from start with elements in source.
     * Some adapter use this method to append newPart to originData from position.
     */
    public static <T> List<T> replaceFromPosition(List<T> origin, List<T> newPart, int pos) {
        if (origin == null || origin.isEmpty()) {
            return newPart;
        }
        if (newPart == null || newPart.isEmpty()) {
            return origin;
        }
        if (pos > origin.size()) {// append to end of list
            pos = origin.size();
        }
        List result = new ArrayList(origin);// avoid operation about origin list
        int newSize = pos + newPart.size();
        result.addAll(pos, newPart);
        while (result.size() > newSize) {
            result.remove(result.size() - 1); // remove last until size is right.
        }
        return result;
    }

    /**
     * append the newPart to the origin from pos.
     */
    public static <T> List<T> appendFromPosition(List<T> origin, List<T> newPart, int pos) {
        if (origin == null || origin.isEmpty()) {
            List<T> result = new ArrayList<T>();
            if (newPart != null) {
                result.addAll(newPart); // clone
            }
            return result;
        }
        if (newPart == null || newPart.isEmpty()) {
            return origin;
        }
        if (pos > origin.size()) {// append to end of list
            pos = origin.size();
        }
        List<T> result = new ArrayList<T>(origin);// avoid operation about origin list
        result.addAll(pos, newPart);
        return result;
    }

    public static <T> boolean isEmpty(Collection<T> list) {
        return list == null || list.isEmpty();
    }

    public static <K, V> boolean isEmpty(Map<K, V> map) {
        return map == null || isEmpty(map.keySet());
    }

    public static <T> T getFirstElement(List<T> list) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    public static <T> T getElementAtPosition(List<T> list, int position) {
        if (list == null || position < 0 || list.size() <= position) {
            return null;
        }
        return list.get(position);
    }

    public static <T> int getCount(List<T> list) {
        return list == null ? 0 : list.size();
    }

    @NonNull
    public static <NEW, OLD> List<NEW> transform(List<OLD> oldModels,
                                                 DataTransformer<NEW, OLD> transformer) {
        return transform(oldModels, transformer, null);
    }

    @NonNull
    public static <NEW, OLD> List<NEW> transform(List<OLD> oldModels,
                                                 @NonNull DataTransformer<NEW, OLD> transformer,
                                                 @Nullable DataExtraTransformer<NEW, OLD> extraTransformer) {
        List<NEW> newModels = new ArrayList<>();
        if (oldModels == null) {
            return newModels;
        }
        for (OLD oldData : oldModels) {
            NEW newData = transformer.apply(oldData);
            if (extraTransformer != null) {
                newData = extraTransformer.apply(newData, oldData);
            }
            newModels.add(newData);
        }
        return newModels;
    }

    public interface DataExtraTransformer<NEW, OLD> {
        @NonNull
        NEW apply(@NonNull NEW newModel, @NonNull OLD oldModel);
    }

    public interface DataTransformer<NEW, OLD> {
        @NonNull
        NEW apply(@NonNull OLD oldModel);
    }
}
