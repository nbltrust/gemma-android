package com.cybex.gma.client.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.cybex.gma.client.BuildConfig;
import com.cybex.gma.client.manager.LoggerManager;
import com.hxlx.core.lib.common.cache.CacheUtil;
import com.hxlx.core.lib.common.cache.CacheUtilConfig;


/**
 * 耗时操作的初始化
 * <p>
 * Created by wanglin on 2018/1/19.
 */

public class InitializeService extends IntentService {

    public static final int JOB_ID = 0x0801;
    private static final String ACTION_INIT = "initApplication";
    private static final String CHANNEL_ID_SERVICE = "InitializeService";
    private static final String CHANNEL_ID_NAME = "Initialize_Service";
    private static final int serviceID = 8002;
    private static final String TAG = "GEMMA_LOG";

    public InitializeService() {
        super("InitializeService");
    }

    /**
     * 启动调用
     */
    public static void start(Context context) {
        Intent intent = new Intent(context, InitializeService.class);
        intent.setAction(ACTION_INIT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID_SERVICE, CHANNEL_ID_NAME,
                    NotificationManager.IMPORTANCE_LOW);
            manager.createNotificationChannel(channel);
            Notification status = new Notification.Builder(this).setChannelId(CHANNEL_ID_SERVICE).build();
            startForeground(serviceID, status);
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_INIT.equals(action)) {
                initApplication();
            }
        }
    }

    /**
     * UI线程中执行
     */
    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    private void initApplication() {
        initCache();
        initLogger();

    }


    private void initLogger() {
        //初始化Logger日志打印
        LoggerManager.init(TAG, BuildConfig.DEBUG);
    }


    private void initCache() {
        CacheUtilConfig cc = CacheUtilConfig.builder(this)
                .allowMemoryCache(false)// 是否允许保存到内存
                .allowDes3(true)// 是否允许des3加密
                .build();
        CacheUtil.init(cc);// 初始化，必须调用
    }


}
