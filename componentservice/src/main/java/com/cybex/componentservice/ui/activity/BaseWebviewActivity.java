package com.cybex.componentservice.ui.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.cybex.componentservice.R;
import com.hxlx.core.lib.mvp.lite.BasePresenter;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.just.agentweb.AgentWeb;

/**
 * App内快速实现WebView功能
 *
 * 1、调整WebView自适应屏幕代码属性{@link #initAgentWeb()}
 *
 * Created by wanglin on 2018/7/18.
 */
public abstract class BaseWebviewActivity<P extends BasePresenter> extends XActivity {

    protected ViewGroup mContainer;
    protected String mUrl = "";
    protected String mTitle = "";
    protected String mCurrentUrl;
    protected AlertDialog mAlertDialog;
    protected AgentWeb mAgentWeb;
    protected AgentWeb.CommonBuilder mAgentBuilder;
    public static final String KEY_URL = "url";
    public static final String KEY_TITLE = "title";

    protected void initAgentWeb() {
        mAgentBuilder = AgentWeb.with(this)//
                .setAgentWebParent(mContainer, new ViewGroup.LayoutParams(-1, -1))//
                .useDefaultIndicator(
                        getProgressColor() != -1 ? getProgressColor() : getResources().getColor(R.color.whiteTwo),
                        getProgressHeight())
                .setWebChromeClient(new WebChromeClient() {
                    @Override
                    public void onReceivedTitle(WebView view, String title) {
                        super.onReceivedTitle(view, title);
                        mCurrentUrl = view.getUrl();

                        if (TextUtils.isEmpty(mTitle)) {
                            mTitle = title;
                            setNavibarTitle(mTitle, true);
                        }
                    }
                })
                .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK);
        setAgentWeb(mAgentBuilder);
        mAgentWeb = mAgentBuilder
                .createAgentWeb()//
                .ready()
                .go(mUrl);
        WebSettings webSettings = mAgentWeb.getAgentWebSettings().getWebSettings();
        //设置webView自适应屏幕
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        setAgentWeb(mAgentWeb);
    }

    protected static void goWebView(
            Activity mActivity, Class<? extends BaseWebviewActivity> activity, String url, String
            title) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_URL, url);
        bundle.putString(KEY_TITLE, title);
        Intent intent = new Intent(mActivity, activity);
        intent.putExtras(bundle);
        mActivity.startActivity(intent);
    }


    protected void setAgentWeb(AgentWeb mAgentWeb) {

    }

    protected void setAgentWeb(AgentWeb.CommonBuilder mAgentBuilder) {

    }

    /**
     * 设置进度条颜色
     *
     * @return
     */
    @ColorInt
    protected int getProgressColor() {
        return -1;
    }

    /**
     * 设置进度条高度 注意此处最终AgentWeb会将其作为float 转dp2px
     *
     * @return
     */
    protected int getProgressHeight() {
        return 2;
    }


    @Override
    public void bindUI(View rootView) {
        Bundle bd = getIntent().getExtras();
        if (bd != null) {
            mUrl = getIntent().getStringExtra(KEY_URL);
            mTitle = getIntent().getStringExtra(KEY_TITLE);

            setNavibarTitle(mTitle, true);
            mCurrentUrl = mUrl;
        }

        mContainer = (ViewGroup) findView(R.id.lLayout_containerFastWeb);
        initAgentWeb();
    }

    @Override
    public void initData(Bundle savedInstanceState) {

    }

    protected void showDialog() {
        if (mAlertDialog == null) {
            mAlertDialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.fast_web_alert_title)
                    .setMessage(R.string.fast_web_alert_msg)
                    .setNegativeButton(R.string.fast_web_alert_left, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (mAlertDialog != null) { mAlertDialog.dismiss(); }
                        }
                    })
                    .setPositiveButton(R.string.fast_web_alert_right, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if (mAlertDialog != null) { mAlertDialog.dismiss(); }
                            mContext.finish();
                        }
                    }).create();
        }
        mAlertDialog.show();
        //show之后可获取对应Button对象设置文本颜色--show之前获取对象为null
        mAlertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.RED);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mAgentWeb.handleKeyEvent(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onBackPressedSupport() {
        if (mAgentWeb.back()) {
            return;
        }
        super.onBackPressedSupport();
    }

    @Override
    protected void onPause() {
        mAgentWeb.getWebLifeCycle().onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mAgentWeb.getWebLifeCycle().onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mAgentWeb.getWebLifeCycle().onDestroy();
        super.onDestroy();
    }


    @Override
    public int getLayoutId() {
        return R.layout.baseservice_activity_common_webview;
    }

}
