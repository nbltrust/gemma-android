package com.cybex.gma.client.widget;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.OverScroller;


public class MyScrollView extends FrameLayout {

    final static String TAG = MyScrollView.class.getSimpleName();

    private final static int ANIMATED_SCROLL_GAP = 250;
    private long mLastScroll;

    private int maxOverScrollY;//回弹的最大距离
    //当滑动当顶部还继续滑动的时候 ScrollView滑动的系数，当为1的时候与手指的速度一样
    //小于1快与手指 大于1慢于手指
    private int mOverDyFactor = 4;

    private OverScroller mScroller;
    private VelocityTracker mVelocityTracker;
    private int mMaximumVelocity, mMinimumVelocity;
    private int mTouchSlop;
    private float mLastY;
    private boolean mDragging;

    /**
     * 保存数据的
     * todo
     */
    static class SaveState extends BaseSavedState {

        public SaveState(Parcelable superState) {
            super(superState);

        }
    }

    public MyScrollView(@NonNull Context context) {
        this(context, null);
    }

    public MyScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyScrollView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        android.widget.ScrollView s = new android.widget.ScrollView(this.getContext());
        setFocusable(true);
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        setWillNotDraw(false);

        mScroller = new OverScroller(context);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mMaximumVelocity = ViewConfiguration.get(context)
                .getScaledMaximumFlingVelocity();
        mMinimumVelocity = ViewConfiguration.get(context)
                .getScaledMinimumFlingVelocity();

        setVerticalScrollBarEnabled(true);
    }

    @Override
    public void addView(View child) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("只能有一个子View");
        }
        super.addView(child);
    }

    @Override
    public void addView(View child, int index) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("只能有一个子View");
        }
        super.addView(child, index);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("只能有一个子View");
        }
        super.addView(child, index, params);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("只能有一个子View");
        }
        super.addView(child, params);
    }

    @Override
    public void addView(View child, int width, int height) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("只能有一个子View");
        }
        super.addView(child, width, height);
    }

    public final void smoothScrollBy(int dx, int dy) {
        if (getChildCount() == 0) {
            return;
        }
        long duration = AnimationUtils.currentAnimationTimeMillis() - mLastScroll;
        if (duration > ANIMATED_SCROLL_GAP) {
            final int height = getHeight();
            final int bottom = getChildAt(0).getHeight();
            final int maxY = Math.max(0, bottom - height);
            final int scrollY = getScrollY();
            dy = Math.max(0, Math.min(scrollY + dy, maxY)) - scrollY;

            mScroller.startScroll(getScrollY(), scrollY, 0, dy);
            postInvalidateOnAnimation();
        } else {
            if (!mScroller.isFinished()) {
                mScroller.abortAnimation();
            }
            int scrollY = getScrollY() + dy;
            if (scrollY < 0) {
                scrollY = 0;
            } else if (scrollY > getScrollRange()) {
                scrollY = getScrollRange();
            }
            scrollTo(getScrollX() + dx, scrollY);
        }
        mLastScroll = AnimationUtils.currentAnimationTimeMillis();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        maxOverScrollY = getMeasuredHeight() / 2;
        if (getChildCount() > 0) {
            View child = getChildAt(0);
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();
            final int childMeasureWidth = getChildMeasureSpec(
                    widthMeasureSpec, lp.leftMargin + lp.rightMargin, lp.width);
            final int childMeasureHeight = MeasureSpec.makeMeasureSpec(
                    heightMeasureSpec, MeasureSpec.UNSPECIFIED);
            child.measure(childMeasureWidth, childMeasureHeight);

            Log.i(TAG, "child height = " + child.getMeasuredHeight());
        }
    }

    @Override
    protected void measureChild(View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
        super.measureChild(child, parentWidthMeasureSpec, parentHeightMeasureSpec);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDragging = false;
                mLastY = ev.getY();
                //计算点击的时候是不是滑动
                mScroller.computeScrollOffset();
                mDragging = !mScroller.isFinished();
                break;

            case MotionEvent.ACTION_MOVE:
                int deltaY = (int) Math.abs((int) ev.getY() - mLastY);
                if (deltaY > mTouchSlop) {
                    mDragging = true;
                }
                break;

            case MotionEvent.ACTION_UP:

                break;
        }
        return mDragging;
    }

    private int getChildHeight() {
        if (getChildCount() > 0) {
            return getChildAt(0).getBottom() - getChildAt(0).getTop();
        } else {
            return 0;
        }
    }

    private int getScrollRange() {
        if (getChildCount() > 0) {
            int childHeight = getChildHeight();
            LayoutParams lp = (LayoutParams) getChildAt(0).getLayoutParams();
            return childHeight + lp.topMargin + lp.bottomMargin - getHeight();
        }
        return 0;
    }

    //down事件的时候调用 初始化它
    private void initVelocityTrackerIfNotExists(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    //cancle或者Up事件的时候调用 销毁它
    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    private int mActivePointerId = -1;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        initVelocityTrackerIfNotExists(event);
        int action = event.getActionMasked();
        float y = event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mLastY = (int) event.getY();
                mActivePointerId = event.getPointerId(0);
                return true;
            case MotionEvent.ACTION_MOVE:
                int dy = (int) (mLastY - y);
                if (!mDragging && Math.abs(dy) > mTouchSlop) {
                    mDragging = true;
                }
                if (mDragging) {
                    //如果滑动超出边界了 将dy按系数取值
                    if (getScrollY() < 0 || getScrollY() > getScrollRange()) {
                        dy /= mOverDyFactor;
                    }
                    overScrollBy(0, dy, 0, getScrollY(), 0, getScrollRange(), 0, maxOverScrollY, true);
                }
                mLastY = y;
                break;
            case MotionEvent.ACTION_CANCEL:
                mDragging = false;
                recycleVelocityTracker();
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_UP:
                mDragging = false;
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int velocityY = (int) mVelocityTracker.getYVelocity(mActivePointerId);
                Log.i(TAG, "velocityY=" + velocityY + " mMinimumVelocity = " + mMinimumVelocity);
                if (getScrollY() > 0 && getScrollY() < getScrollRange() && Math.abs(velocityY) > mMinimumVelocity) {
                    fling(-velocityY);
                } else if (mScroller.springBack(0, getScrollY(), 0, 0, 0, getScrollRange())) {
                    postInvalidateOnAnimation();
                }
                recycleVelocityTracker();
                mActivePointerId = -1;
                break;
        }

        return super.onTouchEvent(event);
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        Log.e("onOverScrolled", "onOverScrolled clampedY = >>>>" + clampedY);
        Log.e("scrollY", ">>>>>>>scrollY = " + scrollY + " mScroller.isFinished() = " + mScroller.isFinished());
        if (!mScroller.isFinished()) {
//            int oldY = getScrollY();
//            setScrollY(scrollY);
//            onScrollChanged(0 , scrollY , 0,  oldY);
//            invalidate();
//            if (clampedY){
//                mScroller.springBack(0, getScrollY() , 0 , 0 ,0 , getScallRange());
//            }
        }
        scrollTo(scrollX, scrollY);
        awakenScrollBars();
    }

    @Override
    protected int computeVerticalScrollExtent() {
        return getHeight();
    }

    @Override
    protected int computeVerticalScrollOffset() {
        return Math.max(0, super.computeVerticalScrollOffset());
    }

    @Override
    protected int computeVerticalScrollRange() {
        final int count = getChildCount();
        final int contentHeight = getHeight() - 0 - 0;
        if (count == 0) {
            return contentHeight;
        }
        int scrollRange = getChildAt(0).getBottom();
        final int scrollY = getScrollY();
        final int overScrollBottom = Math.max(0, scrollRange - contentHeight);
        if (scrollY < 0) {
            scrollRange -= scrollY;
        } else if (scrollY > overScrollBottom) {
            scrollRange += scrollY - overScrollBottom;
        }
        return scrollRange;
    }

    public void fling(int velocityY) {
        mScroller.fling(0, getScrollY(), 0, velocityY, 0, 0, 0, getScrollRange(), 0, maxOverScrollY);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            overScrollBy(0, mScroller.getCurrY() - getScrollY(), 0, getScrollY(), 0, getScrollRange(), 0,
                    maxOverScrollY, false);
            if (!awakenScrollBars()) {
                postInvalidateOnAnimation();
            }
        }
    }
}

