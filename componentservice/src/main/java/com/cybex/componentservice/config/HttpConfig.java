package com.cybex.componentservice.config;

import android.app.Application;
import android.content.Context;

import com.cybex.componentservice.BuildConfig;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.SPCookieStore;
import com.lzy.okgo.https.HttpsUtils;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * 网络请求配置
 */
public class HttpConfig {

    public static final long DEFAULT_MILLISECONDS = 30 * 1000; // 默认时间
    public static final long REFRESH_TIME = 300;

    public static void init(Context mContext) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("Fantasy");
        // log打印级别，决定了log显示的详细程度
        loggingInterceptor.setPrintLevel(
                BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        // log颜色级别，决定了log在控制台显示的颜色
        loggingInterceptor.setColorLevel(Level.INFO);
        builder.addInterceptor(loggingInterceptor);

        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                // 设置 Cookie
//        String memberEncode = ShardPreUtils.getUserToken();
//        String cookieStr = "member_uskey_=" + memberEncode;
//        if (StringUtils.isNotEmpty(cookieStr)) {
//          return chain.proceed(chain.request().newBuilder().header("Cookie", cookieStr).build());
//        }
                return chain.proceed(chain.request());
            }
        });


        // 超时时间设置，默认20秒
        builder.readTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS); // 全局的读取超时时间
        builder.writeTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS); // 全局的写入超时时间
        builder.connectTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS); // 全局的连接超时时间
        // 自动管理cookie（或者叫session的保持），以下几种任选其一就行
        if (mContext.getResources().getBoolean(com.hxlx.core.lib.R.bool.cookie_enable)) {
            // builder.cookieJar(new CookieJarImpl(new SPCookieStore(app))); //
            // 使用sp保持cookie，如果cookie不过期，则一直有效
            // builder.cookieJar(new CookieJarImpl(new DBCookieStore(app)));
            // 使用数据库保持cookie，如果cookie不过期，则一直有效

            builder.cookieJar(new CookieJarImpl(new SPCookieStore(mContext)));
        }

        // https相关设置，以下几种方案根据需要自己设置
        // 方法二：自定义信任规则，校验服务端证书
        // HttpsUtils.SSLParams sslParams2 = HttpsUtils.getSslSocketFactory(new SafeTrustManager());
        // 方法三：使用预埋证书，校验服务端证书（自签名证书）
        // HttpsUtils.SSLParams sslParams3 =
        // HttpsUtils.getSslSocketFactory(getAssets().open("srca.cer"));
        // 方法四：使用bks证书和密码管理客户端证书（双向认证），使用预埋证书，校验服务端证书（自签名证书）
        // HttpsUtils.SSLParams sslParams4 = HttpsUtils.getSslSocketFactory(getAssets().open("xxx.bks"),
        // "123456", getAssets().open("yyy.cer"));
        // 方法一：信任所有证书,不安全有风险
        HttpsUtils.SSLParams sslParams1 = HttpsUtils.getSslSocketFactory();
        builder.sslSocketFactory(sslParams1.sSLSocketFactory, sslParams1.trustManager);

        // 其他统一的配置
        HttpHeaders headers = new HttpHeaders();// header不支持中文，不允许有特殊字符
        // headers.put("commonHeaderKey1", "commonHeaderValue1");
        // headers.put(API_HOST_KEY, API_HOST_VALUE);

        HttpParams params = new HttpParams();// param支持中文,直接传,不要自己编码

        // -------------------------------------------------------------------------------------//

        OkGo.getInstance().init((Application) mContext) // 必须调用初始化
                .setOkHttpClient(builder.build()) // 建议设置OkHttpClient，不设置将使用默认的
                .setCacheMode(CacheMode.NO_CACHE) // 全局统一缓存模式，默认不使用缓存，可以不传
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE) // 全局统一缓存时间，默认永不过期，可以不传
                .setRetryCount(3) // 全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
                .addCommonHeaders(headers) // 全局公共头
                .addCommonParams(params); // 全局公共参数
    }


}
