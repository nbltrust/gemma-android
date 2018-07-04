package com.hxlx.core.lib.mvp.lite;

import android.os.Bundle;
import android.view.View;

/**
 * Created by linwang on 2018/4/7.
 */

public interface BaseView<P> {
  void bindUI(View rootView);

  void bindEvent();

  void initData(Bundle savedInstanceState);

  int getOptionsMenuId();

  int getLayoutId();

  boolean useEventBus();

  P newP();
}
