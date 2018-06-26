package com.example.yiran.gemma.Modules.Wallet.CreateWallet;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yiran.gemma.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreateWalletActivity extends AppCompatActivity {

    private static int barHeight;
    private static int backIconWidth;

    @BindView(R.id.tv_title)
    TextView mTitle;
    @BindView(R.id.iv_back)
    ImageView mBackIcon;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initToolBar();
        setContentView(R.layout.activity_create_wallet);
        ButterKnife.bind(this);
        initView();
    }


    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void initView(){
        backIconWidth = getViewWidth(mBackIcon);
        barHeight = getStatusBarHeight(this);
        mTitle.setPadding(-backIconWidth, barHeight,0,0);
        mTitle.setText("创建钱包");
        setMargins(mBackIcon,0,barHeight,0,0);
    }

    /**
     * 沉浸式状态栏初始化
     */
    public void initToolBar(){
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);
    }

    /**
     * 动态获取状态栏高度
     * @param mActivity
     * @return
     */

    public int getStatusBarHeight(Activity mActivity) {
        Context context = mActivity.getApplicationContext();
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 动态设置控件margins的方法
     * @param v
     * @param l 左边距
     * @param t 上边距
     * @param r
     * @param b
     */

    public static void setMargins(View v, int l, int t, int r, int b){
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    /**
     * 手动测量view的宽度
     * @param view
     * @return
     */
    public int getViewWidth(View view){
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        return view.getMeasuredWidth();
    }
}
