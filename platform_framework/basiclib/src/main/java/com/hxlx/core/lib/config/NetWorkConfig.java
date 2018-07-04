package com.hxlx.core.lib.config;

import android.Manifest;
import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.hxlx.core.lib.utils.network.support25.NetWorkNotify;
import com.hxlx.core.lib.utils.android.PermissionUtil;
import com.hxlx.core.lib.utils.android.SysUtils;

import static com.hxlx.core.lib.base.BaseApplication.getAppContext;

/**
 * 网络配置
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class NetWorkConfig {


    final static ConnectivityManager.NetworkCallback callback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(Network network) {
            //就会连接该网络，回调 通过Network获取相关信息
        }

        // to Override ...
    };

    public static void initNetNotify(Activity activity) {

        //   网络监听
        if (SysUtils.hasNougat()) {


                PermissionUtil.requestPermission(new PermissionUtil.RequestPermission() {

                    @Override
                    public void onRequestPermissionSuccess() {

                        NetWorkNotify.getInstance(getAppContext()).setNetNotifyForNougat(callback);


                    }

                    @Override
                    public void onRequestPermissionFailure() {

                    }
                }, new RxPermissions(activity), null, Manifest.permission.CHANGE_NETWORK_STATE, Manifest.permission.WRITE_SETTINGS);

        }
    }
}
