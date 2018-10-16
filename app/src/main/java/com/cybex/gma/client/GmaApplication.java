package com.cybex.gma.client;

import com.cybex.base.view.refresh.CommonRefreshLayout;
import com.cybex.componentservice.config.HttpConfig;
import com.cybex.componentservice.db.GemmaDatabase;
import com.hxlx.core.lib.base.BaseApplication;
import com.raizlabs.android.dbflow.config.DatabaseConfig;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowLog;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.runtime.DirectModelNotifier;

import me.framework.fragmentation.Fragmentation;
import me.framework.fragmentation.helper.ExceptionHandler;

/**
 * GMA application初始化配置
 */
public class GmaApplication extends BaseApplication {


    @Override
    public void onCreate() {
        super.onCreate();

        initFragmentation();
        initDBFlow();
        HttpConfig.init(this);
        initRefresh();
    }


    private void initRefresh() {
        CommonRefreshLayout.initRefresh();
    }

    private void initDBFlow() {
        FlowLog.setMinimumLoggingLevel(FlowLog.Level.D);

        /**
         FlowManager.init(new FlowConfig.Builder(this)
         .addDatabaseConfig(
         new DatabaseConfig.Builder(GemmaDatabase.class)
         .openHelper(new DatabaseConfig.OpenHelperCreator() {
        @Override public OpenHelper createHelper(
        DatabaseDefinition databaseDefinition,
        DatabaseHelperListener helperListener) {
        return new SQLCipherHelperImpl(databaseDefinition, helperListener);
        }
        })
         .build())
         .build());*/

        FlowManager.init(new FlowConfig.Builder(this)
                .addDatabaseConfig(new DatabaseConfig.Builder(GemmaDatabase.class)
                        .modelNotifier(DirectModelNotifier.get()).build())
                .build());
    }


    private void initFragmentation() {
        Fragmentation.builder()
                // 设置 栈视图 模式为 悬浮球模式 SHAKE: 摇一摇唤出 NONE：隐藏
                .stackViewMode(Fragmentation.NONE)
                // ture时，遇到异常："Can not perform this action after onSaveInstanceState!"时，会抛出
                // false时，不会抛出，会捕获，可以在handleException()里监听到
                .debug(false)
                // 线上环境时，可能会遇到上述异常，此时debug=false，不会抛出该异常（避免crash），会捕获
                .handleException(new ExceptionHandler() {
                    @Override
                    public void onException(Exception e) {
                    }
                }).install();
    }
}
