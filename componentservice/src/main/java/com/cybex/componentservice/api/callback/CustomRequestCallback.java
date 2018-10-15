package com.cybex.componentservice.api.callback;

import android.support.annotation.NonNull;


import com.cybex.componentservice.api.data.response.CustomData;

import io.reactivex.disposables.Disposable;

/**
 * Created by wanglin on 2018/7/5.
 */
public interface CustomRequestCallback<T> {

    void onBeforeRequest(@NonNull Disposable disposable);

    void onSuccess(@NonNull CustomData<T> result);

    void onError(@NonNull Throwable e);

    void onComplete();
}
