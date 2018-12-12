package com.hxlx.core.lib.mvp.lite;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hxlx.core.lib.R;
import com.hxlx.core.lib.common.eventbus.BaseEvent;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.yanzhenjie.sofia.Sofia;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import me.framework.fragmentation.FragmentSupport;
import me.jessyan.autosize.AutoSize;
import me.jessyan.autosize.internal.CustomAdapt;

/**
 * Created by linwang on 2018/4/7.
 */
public abstract class XFragment<P extends BasePresenter> extends FragmentSupport
        implements
        BaseView<P> ,CustomAdapt{

    private VDelegate vDelegate;
    private P p;
    protected Activity context;
    private View rootView;
    protected LayoutInflater layoutInflater;
    protected KProgressHUD kProgressHUD;
    protected TitleBar mTitleBar;


    public <T extends View> T $(View layoutView, @IdRes int resId) {
        return (T) layoutView.findViewById(resId);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (useEventBus()) {
            EventBusProvider.register(this);
        }
        if(getActivity()!=null){
            AutoSize.autoConvertDensityOfGlobal(getActivity());
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        layoutInflater = inflater;
        if (rootView == null && getLayoutId() > 0) {
            rootView = inflater.inflate(getLayoutId(), null);
            bindUI(rootView);
        } else {
            ViewGroup viewGroup = (ViewGroup) rootView.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(rootView);
            }
        }
        setImmersiveStyle();
        return rootView;
    }

    @Override
    public void onStart() {
        if(getActivity()!=null){
            AutoSize.autoConvertDensityOfGlobal(getActivity());
        }
        super.onStart();
    }

    @Override
    public void onResume() {
        if(getActivity()!=null){
            AutoSize.autoConvertDensityOfGlobal(getActivity());
        }
        super.onResume();
    }

    protected void setNavibarTitle(final String title, final boolean isShowBack) {
        mTitleBar = rootView.findViewById(R.id.btn_navibar);
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
                    pop();
                }
            });
        }
    }


    /**
     * @param title 标题
     * @param isShowBack 是否显示返回键
     * @param isOnBackFinishActivity 是否关闭Fragment,Activity
     */
    protected void setNavibarTitle(final String title, final boolean isShowBack, final boolean isOnBackFinishActivity) {

        mTitleBar = rootView.findViewById(R.id.btn_navibar);
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
                    if (isOnBackFinishActivity) {
                        getActivity().finish();
                    } else {
                        pop();
                    }
                }
            });
        }
    }

    /**
     * 设置沉浸状态栏和透明导航栏
     */
    @SuppressLint("ObsoleteSdkInt")
    protected void setImmersiveStyle(){
        //Android P 上的适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            //Android 9.0以上适配

        }else {
            Sofia.with(context)
                    .statusBarBackgroundAlpha(0)
                    .navigationBarBackgroundAlpha(0)
                    .invasionStatusBar();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bindEvent();
        initData(savedInstanceState);
    }

    @Override
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
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.context = (Activity) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        context = null;
    }

    @Override
    public boolean useEventBus() {
        return false;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (useEventBus()) {
            EventBusProvider.unregister(this);
        }
        if (getP() != null) {
            getP().detachV();
        }
        getvDelegate().destory();

        p = null;
        vDelegate = null;
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


    @Override
    public int getOptionsMenuId() {
        return 0;
    }

    @Override
    public void bindEvent() {

    }

    /**
     * 以无参数的模式启动Activity。
     *
     * @param activityClass
     */
    public void readyGo(Class<? extends Activity> activityClass) {
        getActivity().startActivity(getLocalIntent(activityClass, null));
        getActivity().overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
    }

    /**
     * 以绑定参数的模式启动Activity。
     *
     * @param activityClass
     */
    public void readyGo(Class<? extends Activity> activityClass, Bundle bd) {
        getActivity().startActivity(getLocalIntent(activityClass, bd));
        getActivity().overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
    }

    /**
     * 获取当前程序中的本地目标
     *
     * @param localIntent
     * @return
     */
    public Intent getLocalIntent(Class<? extends Context> localIntent, Bundle bd) {
        Intent intent = new Intent(getActivity(), localIntent);
        if (null != bd) {
            intent.putExtras(bd);
        }

        return intent;
    }

    public void showProgressDialog(final String prompt) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (kProgressHUD == null) {
                        ImageView imageView = new ImageView(getActivity());
                        imageView.setBackgroundResource(R.drawable.animation_loading_spin);
                        AnimationDrawable drawable = (AnimationDrawable) imageView.getBackground();
                        drawable.start();

                        kProgressHUD = KProgressHUD.create(getActivity())
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
    }

    public void dissmisProgressDialog() {
        if (kProgressHUD != null && kProgressHUD.isShowing()) {
            kProgressHUD.dismiss();
        }
    }

    @Override
    public boolean isBaseOnWidth() {
        return true;
    }

    @Override
    public float getSizeInDp() {
        return 360;
    }
}
