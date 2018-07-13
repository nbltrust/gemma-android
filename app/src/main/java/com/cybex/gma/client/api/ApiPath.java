package com.cybex.gma.client.api;


import com.cybex.gma.client.BuildConfig;

/**
 * 接口请求地址全部配置在这里
 * <p>
 * Created by wanglin on 2018/7/05
 */

public class ApiPath {

    //测试环境
    public static final String HOST_TEST = "";
    //开发环境
    public static final String HOST_DEV = "";
    //线上环境
    public static final String HOST_ONLINE = "https://api.xxx.com";
    public final static String[] HOST = {HOST_TEST, HOST_DEV, HOST_ONLINE};
    public static String REST_URI_HOST = HOST[BuildConfig.API_PATH];


    /**
     * ------------EOS 项目情况配置---------------
     */
    //中心化服务器host
    public static final String HOST_CENTER_SERVER = "http://139.196.73.117:3001";
    //链上服务器host
    public static final String HOST_ON_CHAIN = "http://139.196.73.117:9000";

    /**------------EOS 项目情况配置---------------*/
}
