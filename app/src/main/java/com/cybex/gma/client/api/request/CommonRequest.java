package com.cybex.gma.client.api.request;


import com.cybex.gma.client.api.callback.CustomConvert;
import com.cybex.gma.client.api.callback.CustomRequestCallback;
import com.cybex.gma.client.api.data.response.CustomData;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okrx2.adapter.ObservableResponse;

import java.util.HashMap;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wanglin on 2018/7/5.
 */
public class CommonRequest<T> {

    private Class clazz;

    /**
     * @param clazz 想要请求返回的Bean
     */
    public CommonRequest(Class clazz) {
        this.clazz = clazz;
    }


    /**
     * POST请求JSON参数
     *
     * @param path
     * @param input
     * @param callback
     */
    public void postJson(
            String path, String input, final
    CustomRequestCallback<T> callback) {
        OkGo.<CustomData<T>>post(path)
                .upJson(input)
                .converter(new CustomConvert<CustomData<T>>(clazz))
                .adapt(new ObservableResponse<CustomData<T>>())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) {
                        //做一些操作 showLoading();
                        if (callback != null) {
                            callback.onBeforeRequest(disposable);
                        }
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<CustomData<T>>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(@NonNull Response<CustomData<T>> response) {
                        if (callback != null) {
                            callback.onSuccess(response.body());
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        if (callback != null) {
                            callback.onError(e);
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (callback != null) {
                            callback.onComplete();
                        }
                    }
                });


    }


    /**
     * 非RX的Post请求方式
     *
     * @param tag
     * @param path
     * @param reqParams
     * @param callback
     */
    public void post(
            String tag, String path, HashMap<String, String> reqParams,
            final AbsCallback<T> callback) {
        OkGo.<T>post(path)
                .tag(tag).params(reqParams)
                .execute(callback);
    }


    /**
     * 非RX的Post json请求方式
     *
     * @param tag
     * @param path
     * @param jsonParams
     * @param callback
     */
    public void postJsonNoRx(
            String tag, String path, String jsonParams,
            final AbsCallback<T> callback) {
        OkGo.<T>post(path)
                .tag(tag).upJson(jsonParams)
                .execute(callback);
    }

    /**
     * 非RX的Post json请求方式
     *
     * @param tag
     * @param path
     * @param callback
     */
    public void getJsonNoRx(
            String tag, String path,
            final AbsCallback<T> callback) {

        OkGo.<T>get(path)
                .tag(tag)
                .execute(callback);
    }


    private static CompositeDisposable compositeDisposable;

    public static void addDisposable(Disposable disposable) {
        if (compositeDisposable == null) {
            compositeDisposable = new CompositeDisposable();
        }
        compositeDisposable.add(disposable);
    }

    public static void dispose() {
        if (compositeDisposable != null) { compositeDisposable.dispose(); }
    }
}