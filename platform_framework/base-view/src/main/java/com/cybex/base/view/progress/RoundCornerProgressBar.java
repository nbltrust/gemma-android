package com.cybex.base.view.progress;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.cybex.base.view.progress.common.BaseRoundCornerProgressBar;
import com.cybex.gma.framework.view.R;

/**
 * 圆角Progress Bar
 *
 * 用法如下:
 *
 *  <com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar
         android:layout_width="320dp"
         android:layout_height="4dp"
         app:rcBackgroundColor="#c6cade"
         app:rcBackgroundPadding="0dp"
         app:rcMax="100"
         app:rcProgress="90"
         app:rcProgressColor="#d0021b"
         app:rcRadius="10dp" />
 *
 *
 */
public class RoundCornerProgressBar extends BaseRoundCornerProgressBar {

    public RoundCornerProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundCornerProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public int initLayout() {
        return R.layout.layout_round_corner_progress_bar;
    }

    @Override
    protected void initStyleable(Context context, AttributeSet attrs) {

    }

    @Override
    protected void initView() {

    }

    @SuppressWarnings("deprecation")
    @Override
    protected void drawProgress(
            LinearLayout layoutProgress, float max, float progress, float totalWidth,
            int radius, int padding, int colorProgress, boolean isReverse) {
        GradientDrawable backgroundDrawable = createGradientDrawable(colorProgress);
        int newRadius = radius - (padding / 2);
        backgroundDrawable.setCornerRadii(
                new float[] {newRadius, newRadius, newRadius, newRadius, newRadius, newRadius, newRadius, newRadius});
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            layoutProgress.setBackground(backgroundDrawable);
        } else {
            layoutProgress.setBackgroundDrawable(backgroundDrawable);
        }

        float ratio = max / progress;
        int progressWidth = (int) ((totalWidth - (padding * 2)) / ratio);
        ViewGroup.LayoutParams progressParams = layoutProgress.getLayoutParams();
        progressParams.width = progressWidth;
        layoutProgress.setLayoutParams(progressParams);
    }

    @Override
    protected void onViewDraw() {

    }

}
