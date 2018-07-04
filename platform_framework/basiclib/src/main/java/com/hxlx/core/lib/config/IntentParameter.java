package com.hxlx.core.lib.config;

/**
 * ntent传值使用的KEY,可以统一声明在这里
 */
public interface IntentParameter {

    String TAG = IntentParameter.class.getSimpleName();

    interface extra {
        String WEB_LOADING_URL = TAG + "web_loading_url";
        String WEB_LOADING_METHOD = TAG + "web_loading_method";
    }

    interface extras {
        String WEB_OPEN_INFO = TAG + "WEB_OPEN_INFO";
    }


    interface rquestCode {

    }


    interface resultCode {

    }

}
