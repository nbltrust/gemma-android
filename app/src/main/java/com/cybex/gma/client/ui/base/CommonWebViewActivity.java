package com.cybex.gma.client.ui.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

/**
 * 公用WebViewActivity
 *
 * Created by wanglin on 2018/7/18.
 */
public class CommonWebViewActivity extends BaseWebviewActivity {


    private static boolean mIsShowTitle = true;

    public static void startWebView(Activity mActivity, String url, String title) {
        startWebView(mActivity, url, title, true);
    }


    private static void startWebView(Activity mActivity, String url, String title, boolean isShowTitle) {
        mIsShowTitle = isShowTitle;
        goWebView(mActivity, CommonWebViewActivity.class, url, title);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        if (mIsShowTitle) {
            isShowNavibar(View.VISIBLE);
        } else {
            isShowNavibar(View.GONE);
        }

    }

    @Override
    public Object newP() {
        return null;
    }

}
