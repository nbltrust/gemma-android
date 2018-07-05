package com.cybex.gma.client.api.request;


import com.cybex.gma.client.api.ApiPath;
import com.cybex.gma.client.api.callback.CustomConvert;
import com.cybex.gma.client.api.callback.CustomRequestCallback;
import com.cybex.gma.client.api.data.response.CustomData;
import com.hxlx.core.lib.utils.android.logger.Log;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okrx2.adapter.ObservableResponse;

import org.json.JSONObject;

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
     * get 请求网络
     *
     * @param method 请求的api的path
     * @param input 请求参数
     * @param callback 成功回调
     */
    public void getRequest(String method, HashMap<String, String> input, final CustomRequestCallback<T> callback) {

        OkGo.<CustomData<T>>get(ApiPath.REST_URI_HOST + method)
                .params(input, false)
                .cacheKey(String.format("%s%s%s", ApiPath.REST_URI_HOST, method,
                        input != null ? input.toString() : ""))              //这里完全同OkGo的配置一样
                .cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)
                .converter(new CustomConvert<CustomData<T>>(clazz) {
                })
                .adapt(new ObservableResponse<CustomData<T>>()).subscribeOn(Schedulers.io())//
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        //做一些操作 showLoading();
                        if (callback != null) {
                            callback.onBeforeRequest(disposable);
                        }
                    }
                }).observeOn(AndroidSchedulers.mainThread())//
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
                    public void onError(@NonNull Throwable e) { //请求错误
                        Log.printStackTrace(e);

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
     * <p> 定制的POST请求传参，根据具体API设计</p>
     *
     * @param method 请求的api的path
     * @param callback 成功回调
     */
    public void postRequest(String method, HashMap<String, String> reqParams, final CustomRequestCallback<T> callback) {
        OkGo.<CustomData<T>>post(ApiPath.REST_URI_HOST + method) // 使用常量配置
                .params(reqParams)
                .converter(new CustomConvert<CustomData<T>>(clazz))
                .adapt(new ObservableResponse<CustomData<T>>())//
                .subscribeOn(Schedulers.io())//
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        //做一些操作 showLoading();
                        if (callback != null) {
                            callback.onBeforeRequest(disposable);
                        }
                    }
                }).observeOn(AndroidSchedulers.mainThread())//
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
                    public void onError(@NonNull Throwable e) { //请求错误
                        Log.printStackTrace(e);

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
     * POST请求JSON参数
     *
     * @param method 请求的api的path
     * @param input 请求参数
     * @param callback 成功回调
     */
    public void postJson(
            String method, JSONObject input, HashMap<String, String> reqParams, final
    CustomRequestCallback<T> callback) {
        OkGo.<CustomData<T>>post(ApiPath.REST_URI_HOST + method)/* 测试使用 */
                /* .headers("key", "value")*//* TODO header 传值*/
                .cacheKey(ApiPath.REST_URI_HOST + method + (input != null ? input.toString() :
                        "")) /* TODO 缓存Key 可以删除*/
                .cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)/* TODO  缓存策略 可以删除 */
                .params(reqParams)
                .upJson(input)/* TODO  json 参数*/
                .converter(new CustomConvert<CustomData<T>>(clazz))/* TODO 返回数据转换，根据各自的需求自定义*/
                .adapt(new ObservableResponse<CustomData<T>>())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
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
                        Log.printStackTrace(e);

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
     * @param method
     * @param reqParams
     * @param callback
     */
    public void post(
            String tag, String method, HashMap<String, String> reqParams,
            final AbsCallback<T> callback) {
        OkGo.<T>post(ApiPath.REST_URI_HOST + method)
                .tag(tag).params(reqParams)
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