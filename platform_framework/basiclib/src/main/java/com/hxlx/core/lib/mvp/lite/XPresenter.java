package com.hxlx.core.lib.mvp.lite;

import java.lang.ref.WeakReference;

/**
 * Created by linwang on 2018/4/7.
 */
public class XPresenter<V extends BaseView> implements BasePresenter<V> {

  private WeakReference<V> weakReference;

  @Override
  public void attachV(V view) {
    weakReference = new WeakReference<V>(view);
  }

  @Override
  public void detachV() {
    if (weakReference != null) {
      weakReference.clear();
      weakReference = null;
    }

  }

  protected V getV() {
    return weakReference != null ? weakReference.get() : null;
  }

}
