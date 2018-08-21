package com.cybex.base.view.refresh.footer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cybex.base.view.refresh.view.RefreshProgressWheel;
import com.cybex.gma.framework.view.R;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;


public class CommonRefreshFooter extends LinearLayout implements RefreshFooter {

    private static final String TAG = "CommonRefreshFooter";
    private Context context;
    protected boolean mLoadmoreFinished = false;

    private TextView tvRefresh;
    private RefreshProgressWheel progressRefresh;

    public CommonRefreshFooter(Context context) {
        this(context, null);
    }

    public CommonRefreshFooter(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommonRefreshFooter(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
    }

    //初始化View
    private void initView() {
        //绑定View
        View.inflate(context, R.layout.layout_refresh_footer, this);
        tvRefresh = (TextView) findViewById(R.id.tvRefreshFooter);
        progressRefresh = (RefreshProgressWheel) findViewById(R.id.progressRefresh);
    }

    @Override
    public void onPullingUp(float percent, int offset, int footerHeight, int extendHeight) {

    }

    @Override
    public void onPullReleasing(float percent, int offset, int footerHeight, int extendHeight) {

    }

    @Override
    public boolean setLoadmoreFinished(boolean finished) {
        if (mLoadmoreFinished != finished) {
            mLoadmoreFinished = finished;
            if (mLoadmoreFinished) {
                tvRefresh.setText(getResources().getString(R.string.no_more_data));
                progressRefresh.setVisibility(View.GONE);
            } else {
                tvRefresh.setText(getResources().getString(R.string.load_more));
            }
        }
        return true;
    }

    @NonNull
    @Override
    public View getView() {
        return this;
    }

    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Translate;
    }

    @Override
    public void setPrimaryColors(int... colors) {

    }

    @Override
    public void onInitialized(RefreshKernel kernel, int height, int extendHeight) {

    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {

    }

    @Override
    public void onStartAnimator(RefreshLayout layout, int height, int extendHeight) {

    }

    @Override
    public int onFinish(RefreshLayout layout, boolean success) {
        if (!mLoadmoreFinished) {
            tvRefresh.setText(getResources().getString(R.string.finish_loading));
            progressRefresh.setVisibility(View.GONE);
        }
        return 500;
    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }

    @Override
    public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {
        if (!mLoadmoreFinished) {
            switch (newState) {
                case None:
                case PullToUpLoad:
                    tvRefresh.setText(getResources().getString(R.string.load_more));
                    progressRefresh.setVisibility(View.GONE);
                    break;
                case ReleaseToLoad:
                    tvRefresh.setText(getResources().getString(R.string.unleash_to_refresh));
                    progressRefresh.setVisibility(View.GONE);
                    break;
                case Loading:
                    tvRefresh.setText(getResources().getString(R.string.loading));
                    progressRefresh.setVisibility(View.VISIBLE);
                    break;
                case LoadFinish:
                    tvRefresh.setText(getResources().getString(R.string.finish_loading));
                    progressRefresh.setVisibility(View.GONE);
                    break;
            }
        }
    }
}
