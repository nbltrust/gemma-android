package com.cybex.base.view.refresh.header;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cybex.base.view.refresh.view.RefreshProgressWheel;
import com.cybex.gma.framework.view.R;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;


public class CommonRefreshHeader extends LinearLayout implements RefreshHeader {

    private Context context;

    private ImageView ivRefresh;
    private TextView tvRefresh;
    private RefreshProgressWheel progressRefresh;

    public CommonRefreshHeader(Context context) {
        this(context, null);
    }

    public CommonRefreshHeader(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommonRefreshHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
    }

    //初始化View
    private void initView() {
        //绑定View
        View.inflate(context, R.layout.layout_refresh_header, this);
        ivRefresh = findViewById(R.id.ivRefreshArrow);
        tvRefresh = findViewById(R.id.tvRefreshHeader);
        progressRefresh = findViewById(R.id.progressRefresh);
    }

    @Override
    public void onPullingDown(float percent, int offset, int headerHeight, int extendHeight) {
    }

    @Override
    public void onReleasing(float percent, int offset, int headerHeight, int extendHeight) {
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
        ivRefresh.setVisibility(View.GONE);
        progressRefresh.setVisibility(View.GONE);
        tvRefresh.setText(getResources().getString(R.string.finish_refresh));
        return 500;//延迟500毫秒之后再弹回
    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }

    @Override
    public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {
        switch (newState) {
            case None:
            case PullDownToRefresh:
                tvRefresh.setText(getResources().getString(R.string.pull_to_refresh));
                progressRefresh.setVisibility(GONE);
                ivRefresh.setVisibility(VISIBLE);
                ivRefresh.animate().rotation(0);
                break;
            case Refreshing:
                tvRefresh.setText(getResources().getString(R.string.loading));
                progressRefresh.setVisibility(VISIBLE);
                ivRefresh.setVisibility(GONE);
                break;
            case ReleaseToRefresh:
                tvRefresh.setText(getResources().getString(R.string.unleash_refresh));
                ivRefresh.animate().rotation(180);
                break;
        }
    }
}
