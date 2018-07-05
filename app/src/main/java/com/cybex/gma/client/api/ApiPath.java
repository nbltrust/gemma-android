package com.cybex.gma.client.api;


import com.cybex.gma.client.BuildConfig;

/**
 * 接口请求地址全部配置在这里
 * <p>
 * Created by wanglin on 2018/7/05
 */

public class ApiPath {

    // host
    private static final String HOST_OFFICIAL = "https://api.xxx.com";
    private static final String HOST_DEV = "";
    private static final String HOST_TEST = "";
    private final static String[] HOST={HOST_OFFICIAL,HOST_DEV,HOST_TEST};

    public static String REST_URI_HOST = HOST[BuildConfig.API_PATH];
}
