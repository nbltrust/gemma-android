package com.hxlx.core.lib.mvp.lite;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;

import com.hxlx.core.lib.R;
import com.hxlx.core.lib.common.eventbus.BaseEvent;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.OSUtils;
import com.hxlx.core.lib.utils.common.utils.AppManager;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.yanzhenjie.sofia.Sofia;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import me.framework.fragmentation.ActivitySupport;

/**
 * Created by linwang on 2018/4/7.
 */
public abstract class XActivity<P extends BasePresenter> extends ActivitySupport
        implements
        BaseView<P> {

    public TitleBar mTitle;
    protected Activity context;
    protected KProgressHUD kProgressHUD;
    private VDelegate vDelegate;
    private P p;
    protected TitleBar mTitleBar;
    protected Activity mContext;


    @Nullable
    public <T extends View> T findView(int viewId) {
        return (T) findViewById(viewId);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        if (getLayoutId() > 0) {
            setContentView(getLayoutId());

            if (isShowTitleBar()) {
                mTitleBar = findViewById(R.id.btn_navibar);
            }

            bindUI(null);
            bindEvent();
        }
        initData(savedInstanceState);

        AppManager.getAppManager().addActivity(this);
        mContext = this;

    }


    /**
     * 设置沉侵式状态栏
     *
     * @param colorResId
     */
    protected void setStatusBarColor(int colorResId) {
        Sofia.with(this).statusBarDarkFont()
                .navigationBarBackground(ContextCompat.getColor(this, R.color.navigation_bar_black_color))
                .statusBarBackground(ContextCompat.getColor(this, colorResId));
    }

    protected void setNavibarTitle(String title, boolean isShowBack) {
        if (EmptyUtils.isEmpty(title)) { return; }

        mTitleBar.setTitle(title);
        mTitleBar.setTitleColor(R.color.ffffff_white_1000);
        mTitleBar.setTitleSize(20);
        mTitleBar.setImmersive(true);
        if (isShowBack) {
            mTitleBar.setLeftImageResource(R.drawable.ic_btn_back);
            mTitleBar.setLeftClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    /**
     * 是否因此TitleBar
     *
     * @param visibility
     */
    protected void isShowNavibar(int visibility) {
        if (null != mTitleBar) { mTitleBar.setVisibility(visibility); }

    }

    /**
     * 设置沉侵式状态栏
     **/
    @SuppressLint("NewApi")
    protected void setStatusBarRes(int statusColorResId) {
        Sofia.with(this).statusBarDarkFont()
                .statusBarBackground(getDrawable(statusColorResId));
    }


    public <T extends View> T $(@IdRes int resId) {
        return (T) super.findViewById(resId);
    }

    /**
     * 以无参数的模式启动Activity。
     *
     * @param activityClass
     */
    public void readyGo(Class<? extends Activity> activityClass) {
        startActivity(getLocalIntent(activityClass, null));
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
    }

    /**
     * 以绑定参数的模式启动Activity。
     *
     * @param activityClass
     */
    public void readyGo(Class<? extends Activity> activityClass, Bundle bd) {
        startActivity(getLocalIntent(activityClass, bd));
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
    }

    /**
     * 获取当前程序中的本地目标
     *
     * @param localIntent
     * @return
     */
    public Intent getLocalIntent(Class<? extends Context> localIntent, Bundle bd) {
        Intent intent = new Intent(this, localIntent);
        if (null != bd) {
            intent.putExtras(bd);
        }

        return intent;
    }

    public abstract void bindUI(View rootView);

    protected VDelegate getvDelegate() {
        if (vDelegate == null) {
            vDelegate = VDelegateBase.create(context);
        }
        return vDelegate;
    }

    protected P getP() {
        if (p == null) {
            p = newP();
            if (p != null) {
                p.attachV(this);
            }
        }
        return p;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (useEventBus()) {
            EventBusProvider.register(this);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        getvDelegate().resume();

        if (OSUtils.checkDeviceHasNavigationBar(this)) {
            OSUtils.solveNavigationBar(getWindow());
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        getvDelegate().pause();
    }

    @Override
    public boolean useEventBus() {
        return false;
    }

    public boolean isShowTitleBar() {
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return super.dispatchTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (useEventBus()) {
            EventBusProvider.unregister(this);
        }
        if (getP() != null) {
            getP().detachV();
        }
        getvDelegate().destory();
        p = null;
        vDelegate = null;
        AppManager.getAppManager().finishActivity(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (getOptionsMenuId() > 0) {
            getMenuInflater().inflate(getOptionsMenuId(), menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public int getOptionsMenuId() {
        return 0;
    }

    @Override
    public void bindEvent() {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBusCome(BaseEvent event) {
        if (event != null) {
            receiveEvent(event);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onStickyEventBusCome(BaseEvent event) {
        if (event != null) {
            receiveStickyEvent(event);
        }
    }

    /**
     * 接收到分发到事件
     *
     * @param event 事件
     */
    protected void receiveEvent(BaseEvent event) {

    }

    /**
     * 接受到分发的粘性事件
     *
     * @param event 粘性事件
     */
    protected void receiveStickyEvent(BaseEvent event) {

    }


    public void showProgressDialog(final String prompt) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (kProgressHUD == null) {
                    kProgressHUD = KProgressHUD.create(XActivity.this)
                            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                            .setCancellable(true)
                            .setAnimationSpeed(2)
                            .setDimAmount(0.5f);
                }
                kProgressHUD.setLabel(prompt);
                kProgressHUD.show();
            }
        });
    }


    public void dissmisProgressDialog() {
        if (kProgressHUD != null && kProgressHUD.isShowing()) {
            kProgressHUD.dismiss();
        }
    }

}
