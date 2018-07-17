package com.cybex.gma.client;

import com.cybex.gma.client.config.HttpConfig;
import com.cybex.gma.client.service.InitializeService;
import com.hxlx.core.lib.base.BaseApplication;

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
        InitializeService.start(this);
        HttpConfig.init(this);
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
