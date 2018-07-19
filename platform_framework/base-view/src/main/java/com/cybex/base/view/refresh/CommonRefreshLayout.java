package com.cybex.base.view.refresh;

import android.content.Context;
import android.util.AttributeSet;

import com.cybex.base.view.refresh.footer.CommonRefreshFooter;
import com.cybex.base.view.refresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreater;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreater;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

/**
 * Refresh刷新样式定制
 */
public class CommonRefreshLayout extends SmartRefreshLayout {

    private Context context;

    public CommonRefreshLayout(Context context) {
        this(context, null);
    }

    public CommonRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommonRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initViews();
    }

    /**
     * 初始化，必须调用
     */
    public static void initRefresh() {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreater(new DefaultRefreshHeaderCreater() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                layout.setDragRate(0.5f);
                layout.setHeaderMaxDragRate(2.0f);
                //微博样式
//                return new CommonRefreshHeader(context);
                //官方样式
                return new MaterialHeader(context);
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreater(new DefaultRefreshFooterCreater() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                return new CommonRefreshFooter(context);
            }
        });
    }

    private void initViews() {
        //设置是否监听列表在滚动到底部时触发加载事件
        setEnableAutoLoadmore(false);
        //在内容不满一页的时候，是否可以上拉加载更多
        setEnableLoadmoreWhenContentNotFull(true);
        //是否启用下拉刷新（默认启用）
        setEnableRefresh(true);
        //设置是否启用上啦加载更多（默认启用）
        setEnableLoadmore(true);
        //设置是否开启在刷新时候禁止操作内容视图
        setDisableContentWhenRefresh(true);
        //设置是否开启在加载时候禁止操作内容视图
        setDisableContentWhenLoading(true);
        //关闭内容视图拖动效果
        setEnableHeaderTranslationContent(false);

    }
}
