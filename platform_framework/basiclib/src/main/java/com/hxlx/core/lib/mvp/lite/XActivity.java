package com.hxlx.core.lib.mvp.lite;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.hxlx.core.lib.R;
import com.hxlx.core.lib.common.eventbus.BaseEvent;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.KeyboardUtils;
import com.hxlx.core.lib.utils.LanguageManager;
import com.hxlx.core.lib.utils.OSUtils;
import com.hxlx.core.lib.utils.SPUtils;
import com.hxlx.core.lib.utils.common.utils.AppManager;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.yanzhenjie.sofia.Sofia;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Field;
import java.util.Calendar;

import me.framework.fragmentation.ActivitySupport;
import me.jessyan.autosize.AutoSize;

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
    protected boolean isResume;


    @Nullable
    public <T extends View> T findView(int viewId) {
        return (T) findViewById(viewId);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        AutoSize.autoConvertDensityOfGlobal(this);
        if (useEventBus()) {
            EventBusProvider.register(this);
        }
        if (useArouter()) {
            ARouter.getInstance().inject(this);
        }

        if (getLayoutId() > 0) {
            setContentView(getLayoutId());

            if (isShowTitleBar()) {
                mTitleBar = findViewById(R.id.btn_navibar);
            }

            bindUI(null);
            bindEvent();
        }

        AppManager.getAppManager().addActivity(this);
        mContext = this;

        setImmersiveStyle();
        handleNotch();

        initData(savedInstanceState);
    }

    @Override
    protected void onStart() {
        AutoSize.autoConvertDensityOfGlobal(this);
        super.onStart();
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
        mTitleBar = findViewById(R.id.btn_navibar);
        mTitleBar.setTitle(title);
        mTitleBar.setTitleColor(R.color.black_title);
        mTitleBar.setBackgroundColor(getResources().getColor(R.color.ffffff_white_1000));
        mTitleBar.setTitleSize(18);
        mTitleBar.setTitleBold(true);
        mTitleBar.setImmersive(true);

        if (isShowBack) {
            mTitleBar.setLeftImageResource(R.drawable.ic_notify_back);
            mTitleBar.setLeftClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }


    /**
     * 处理刘海
     */

    public void handleNotch(){
        if (Build.VERSION.SDK_INT >= 28){
            WindowManager.LayoutParams lp = mContext.getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT;
            mContext.getWindow().setAttributes(lp);
        }
    }
    /**
     * 设置沉浸状态栏和透明导航栏
     */
    protected void setImmersiveStyle(){
        Sofia.with(mContext)
                .statusBarDarkFont()
                .statusBarBackgroundAlpha(0)
                .navigationBarBackgroundAlpha(0)
                .invasionStatusBar();

//        Android P 上的适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            //Android 9.0以上适配
            Sofia.with(mContext)
                    .invasionNavigationBar();
                try {
                    //statusBar透明
                    Class decorViewClazz = Class.forName("com.android.internal.policy.DecorView");
                    Field field = decorViewClazz.getDeclaredField("mSemiTransparentStatusBarColor");
                    field.setAccessible(true);
                    field.setInt(getWindow().getDecorView(), Color.TRANSPARENT);  //改为透明
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
    protected void onResume() {
        isResume=true;
        super.onResume();
        AutoSize.autoConvertDensityOfGlobal(this);
        getvDelegate().resume();
       // if (OSUtils.checkDeviceHasNavigationBar(this)) {
       //     OSUtils.solveNavigationBar(getWindow());
       // }

        LanguageManager.getInstance(this).setConfiguration();
    }


    @Override
    protected void onPause() {
        isResume=false;
        super.onPause();
        getvDelegate().pause();
    }

    @Override
    public boolean useEventBus() {
        return false;
    }

    public boolean useArouter() {
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
                    ImageView imageView = new ImageView(mContext);
                    imageView.setBackgroundResource(R.drawable.animation_loading_spin);
                    AnimationDrawable drawable = (AnimationDrawable) imageView.getBackground();
                    drawable.start();

                    kProgressHUD = KProgressHUD.create(mContext)
                            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                            .setCancellable(true)
                            .setCustomView(imageView)
                            .setBackgroundColor(getResources().getColor(R.color.white))
                            .setDimAmount(0.5f);

                }
                if(!TextUtils.isEmpty(prompt)){
                    kProgressHUD.setLabel(prompt, getResources().getColor(R.color.black_context));
                }else{
                    kProgressHUD.setLabel(null);
                }
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
