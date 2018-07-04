package com.hxlx.core.lib.utils.network.support25;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkRequest;
import android.os.Build;
import android.support.annotation.RequiresApi;

/**
 * 网络环境更新回调
 */
public class NetWorkNotify {

    private Context mContext;
    private static NetWorkNotify mInstance;

    private NetWorkNotify(Context context) {
        this.mContext = context;
    }


    public static NetWorkNotify getInstance(Context context) {
        if (mInstance == null) {
            synchronized (NetWorkNotify.class) {
                if (mInstance == null) {
                    mInstance = new NetWorkNotify(context);
                }
            }
        }

        return mInstance;
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setNetNotifyForNougat(ConnectivityManager.NetworkCallback callback) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        connectivityManager.requestNetwork(new NetworkRequest.Builder().build(), callback);
    }
}
