package com.hxlx.core.lib.mvp.lite;

/**
 * Created by linwang on 2018/4/7.
 */
public interface BasePresenter<V> {
  void attachV(V view);

  void detachV();
}
