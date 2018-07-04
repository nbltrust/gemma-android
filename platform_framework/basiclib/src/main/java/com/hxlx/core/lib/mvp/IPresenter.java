package com.hxlx.core.lib.mvp;

/**
 * P
 */
public interface IPresenter {

    void onStart();

    void onDestroy();

    <P extends IPresenter> void setView(Iv iview);


}
