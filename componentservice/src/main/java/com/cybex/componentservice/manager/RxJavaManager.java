package com.cybex.componentservice.manager;

import com.cybex.componentservice.utils.rxjava.TransformerHelper;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.observers.DefaultObserver;

/**
 * RxJava使用管理类
 *
 * Created by wanglin on 2018/7/19.
 */
public class RxJavaManager {

    public interface TimerListener {

        void timeEnd();
    }

    private static volatile RxJavaManager instance;

    private RxJavaManager() {
    }

    public static RxJavaManager getInstance() {
        if (instance == null) {
            synchronized (RxJavaManager.class) {
                if (instance == null) {
                    instance = new RxJavaManager();
                }
            }
        }
        return instance;
    }

    public <T> Observable<T> getDelayObservable(T value, long delay, TimeUnit unit) {
        return Observable.just(value)
                .delay(delay, unit)
                .compose(TransformerHelper.switchSchedulers());
    }

    public <T> Observable<T> getDelayObservable(T value, long delay) {
        return getDelayObservable(value, delay, TimeUnit.SECONDS);
    }

    public Observable<String> setTimer(long delayTime, final TimerListener listener) {
        Observable<String> observable = getDelayObservable("", delayTime, TimeUnit.MILLISECONDS);
        observable.subscribe(new DefaultObserver<String>() {
            @Override
            public void onNext(String entity) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                if (listener != null) {
                    listener.timeEnd();
                }
            }
        });
        return observable;
    }
}
