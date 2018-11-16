package com.cybex.gma.client.ui.activity;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.alibaba.android.arouter.launcher.ARouter;
import com.cybex.componentservice.bean.TransferTestBean;
import com.cybex.gma.client.R;
import com.cybex.gma.client.service.InitializeService;
import com.cybex.gma.client.ui.presenter.SpalashActPresenter;
import com.hxlx.core.lib.mvp.lite.XActivity;

import me.jessyan.autosize.AutoSize;

/**
 * Created by wanglin on 2018/8/5.
 */
public class SpalashActivity extends XActivity<SpalashActPresenter> {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // 隐藏标题栏
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        if (!isTaskRoot()) {
            finish();
            return;
        }

        InitializeService.start(this);
    }


    @Override
    public void bindUI(View rootView) {
        //to fix SizeUtil , Resources.getSystem().getDisplayMetrics()
        AutoSize.autoConvertDensityOfGlobal(this);
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        displayMetrics.density = this.getResources().getDisplayMetrics().density;
        displayMetrics.densityDpi = this.getResources().getDisplayMetrics().densityDpi;

    }

    @Override
    protected void setImmersiveStyle() {

    }

    @Override
    public void initData(Bundle savedInstanceState) {
        skipMain();
    }

    private void skipMain() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //demo
//                Router router= Router.getInstance();
//                EosWalletService service = (EosWalletService) router.getService(EosWalletService.class.getSimpleName());
//                Toast.makeText(context, "balance="+service.getEosBalance(0), Toast.LENGTH_SHORT).show();
//
                getP().goToNext();
            }
        }, 2000);
    }

    @Override
    public int getLayoutId() {
        return R.layout.eos_activity_spalash;
    }

    @Override
    public SpalashActPresenter newP() {
        return new SpalashActPresenter();
    }
}
