package com.hxlx.core.lib.mvp;

import android.support.annotation.Nullable;

import com.hxlx.core.lib.utils.android.logger.Log;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 反射获取泛型参数
 */


public class TypeUtil {

    public static <T> T getT(Object o, int i) {
        try {
            return ((Class<T>) ((ParameterizedType) (o.getClass().getGenericSuperclass())).getActualTypeArguments()[i])
                    .newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassCastException e) {
            // Log.e(e,"TypeUtil 没有对应的presenter");
        }
        return null;
    }


    public static ParameterizedType type(final Class raw, final Type... args) {
        return new ParameterizedType() {
            public Type getRawType() {
                return raw;
            }

            public Type[] getActualTypeArguments() {
                return args;
            }

            public Type getOwnerType() {
                return null;
            }
        };
    }

    @Nullable
    public static <T> Class<T> getTypeClass(Object object) {
        Class<T> entityClass = null;
        try {
            entityClass = (Class<T>) ((ParameterizedType) object.getClass()
                    .getGenericSuperclass()).getActualTypeArguments()[0];
        } catch (Exception err) {
            Log.printStackTrace(err);
        }

        return entityClass;
    }
}
