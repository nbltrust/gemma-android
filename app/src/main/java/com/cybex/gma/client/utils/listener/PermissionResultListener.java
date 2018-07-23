package com.cybex.gma.client.utils.listener;

import java.util.List;

/**
 * Android6.0 运行时权结果
 *
 * Created by wanglin on 2018/7/23.
 */
public interface PermissionResultListener {

    void onPermissionGranted();

    void onPermissionDenied(List<String> permissions);
}
