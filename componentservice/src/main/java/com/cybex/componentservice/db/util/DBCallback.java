package com.cybex.componentservice.db.util;

/**
 * Created by wanglin on 2018/1/18.
 */

public interface DBCallback {

    void onSucceed();

    void onFailed(Throwable error);

}
