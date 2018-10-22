package com.cybex.componentservice.manager;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.cybex.base.view.tablayout.CommonTabLayout;
import com.cybex.base.view.tablayout.SegmentTabLayout;
import com.cybex.base.view.tablayout.SlidingTabLayout;
import com.cybex.base.view.tablayout.listener.CustomTabEntity;
import com.cybex.base.view.tablayout.listener.OnTabSelectListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * TabLayout+ViewPager 使用管理类
 *
 * Created by wanglin on 2018/7/19.
 */
public class TabLayoutManager {

    private static volatile TabLayoutManager instance;

    private TabLayoutManager() {
    }

    public static TabLayoutManager getInstance() {
        if (instance == null) {
            synchronized (TabLayoutManager.class) {
                if (instance == null) {
                    instance = new TabLayoutManager();
                }
            }
        }
        return instance;
    }

    public void setSlidingTabData(
            Fragment fragment, SlidingTabLayout tabLayout, ViewPager viewPager,
            List<String> tittles, List<Fragment> fragments) {
        setSlidingTabData(fragment, tabLayout, viewPager, tittles, fragments, null);
    }

    public void setSlidingTabData(
            FragmentActivity activity, SlidingTabLayout tabLayout,
            ViewPager viewPager, List<String> tittles, List<Fragment> fragments) {
        setSlidingTabData(activity, tabLayout, viewPager, tittles, fragments, null);
    }

    /**
     * Fragment 里SlidingTabLayout 快速设置
     *
     * @param fragment
     * @param viewPager
     * @param tittles
     * @param fragments
     * @param tabLayout
     * @param listener
     */
    public void setSlidingTabData(
            Fragment fragment, SlidingTabLayout tabLayout, ViewPager viewPager,
            List<String> tittles, List<Fragment> fragments, OnTabSelectListener listener) {
        setViewPager(fragment, tabLayout, viewPager, tittles, fragments, listener);
        tabLayout.setViewPager(viewPager);
    }

    /**
     * FragmentActivity里SlidingTabLayout 快速设置
     *
     * @param activity
     * @param viewPager
     * @param tittles
     * @param fragments
     * @param tabLayout
     * @param listener
     */
    public void setSlidingTabData(
            FragmentActivity activity, SlidingTabLayout tabLayout, ViewPager viewPager,
            List<String> tittles, List<Fragment> fragments, OnTabSelectListener listener) {
        setViewPager(activity, tabLayout, viewPager, tittles, fragments, listener);
        tabLayout.setViewPager(viewPager);
    }

    public void setCommonTabData(
            Fragment fragment, final CommonTabLayout tabLayout, final ViewPager viewPager,
            ArrayList<CustomTabEntity> tabs, List<Fragment> fragments) {
        setCommonTabData(fragment, tabLayout, viewPager, tabs, fragments, null);
    }

    public void setCommonTabData(
            FragmentActivity activity, final CommonTabLayout tabLayout, final ViewPager viewPager,
            ArrayList<CustomTabEntity> tabs, List<Fragment> fragments) {
        setCommonTabData(activity, tabLayout, viewPager, tabs, fragments, null);
    }

    /**
     * 快速设置 Fragment CommonTabLayout
     *
     * @param fragment
     * @param viewPager
     * @param tabs
     * @param fragments
     * @param tabLayout
     * @param listener
     */
    public void setCommonTabData(
            Fragment fragment, final CommonTabLayout tabLayout, final ViewPager viewPager,
            ArrayList<CustomTabEntity> tabs, List<Fragment> fragments, final OnTabSelectListener listener) {
        setViewPager(fragment, tabLayout, viewPager, null, fragments, listener);
        tabLayout.setTabData(tabs);
    }

    /**
     * 快速设置CommonTabLayout
     *
     * @param activity
     * @param viewPager
     * @param tabs
     * @param fragments
     * @param tabLayout
     * @param listener
     */
    public void setCommonTabData(
            FragmentActivity activity, final CommonTabLayout tabLayout, final ViewPager viewPager,
            ArrayList<CustomTabEntity> tabs, List<Fragment> fragments, final OnTabSelectListener listener) {
        setViewPager(activity, tabLayout, viewPager, null, fragments, listener);
        tabLayout.setTabData(tabs);
    }

    public void setSegmentTabData(
            Fragment fragment, final SegmentTabLayout tabLayout, final ViewPager viewPager,
            String[] titles, List<Fragment> fragments) {
        setSegmentTabData(fragment, tabLayout, viewPager, titles, fragments, null);
    }

    public void setSegmentTabData(
            FragmentActivity activity, final SegmentTabLayout tabLayout, final ViewPager viewPager,
            String[] titles, List<Fragment> fragments) {
        setSegmentTabData(activity, tabLayout, viewPager, titles, fragments, null);
    }

    /**
     * 快速设置Fragment 里SegmentTabLayout
     *
     * @param fragment
     * @param tabLayout
     * @param viewPager
     * @param titles
     * @param fragments
     * @param listener
     */
    public void setSegmentTabData(
            Fragment fragment, final SegmentTabLayout tabLayout, final ViewPager viewPager,
            String[] titles, List<Fragment> fragments, final OnTabSelectListener listener) {
        setViewPager(fragment, tabLayout, viewPager, Arrays.asList(titles), fragments, listener);
        tabLayout.setTabData(titles);
    }

    /**
     * 快速设置 FragmentActivity 里SegmentTabLayout
     *
     * @param activity
     * @param tabLayout
     * @param viewPager
     * @param titles
     * @param fragments
     * @param listener
     */
    public void setSegmentTabData(
            FragmentActivity activity, final SegmentTabLayout tabLayout, final ViewPager viewPager,
            String[] titles, List<Fragment> fragments, final OnTabSelectListener listener) {
        setViewPager(activity, tabLayout, viewPager, null, fragments, listener);
        tabLayout.setTabData(titles);
    }

    /**
     * viewPager配合使用
     *
     * @param activity FragmentActivity或Fragment
     * @param viewPager
     * @param tittles
     * @param fragments
     * @param listener
     */
    private void setViewPager(
            Object activity, final Object tabLayout, final ViewPager viewPager,
            List<String> tittles, List<Fragment> fragments, final OnTabSelectListener listener) {

        if (activity == null) {
            return;
        }
        if (!(activity instanceof FragmentActivity)
                && !(activity instanceof Fragment)) {
            return;
        }
        if (!(tabLayout instanceof CommonTabLayout)
                && !(tabLayout instanceof SlidingTabLayout)
                && !(tabLayout instanceof SegmentTabLayout)) {
            return;
        }
        viewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
        viewPager.setAdapter(getFragmentAdapter(activity, tittles, fragments));
        viewPager.setOffscreenPageLimit(fragments.size());
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                if ((tabLayout instanceof CommonTabLayout)) {
                    ((CommonTabLayout) tabLayout).setCurrentTab(position);
                } else if ((tabLayout instanceof SegmentTabLayout)) {
                    ((SegmentTabLayout) tabLayout).setCurrentTab(position);
                }
                if (listener != null) { listener.onTabSelect(position); }
            }
        });
        if ((tabLayout instanceof CommonTabLayout)) {
            ((CommonTabLayout) tabLayout).setOnTabSelectListener(new OnTabSelectListener() {
                @Override
                public void onTabSelect(int position) {
                    viewPager.setCurrentItem(position);
                }

                @Override
                public void onTabReselect(int position) {
                    if (listener != null) { listener.onTabReselect(position); }
                }
            });
        } else if ((tabLayout instanceof SegmentTabLayout)) {
            ((SegmentTabLayout) tabLayout).setOnTabSelectListener(new OnTabSelectListener() {
                @Override
                public void onTabSelect(int position) {
                    viewPager.setCurrentItem(position);
                }

                @Override
                public void onTabReselect(int position) {
                    if (listener != null) { listener.onTabReselect(position); }
                }
            });
        }
    }

    private FragmentStatePagerAdapter getFragmentAdapter(
            Object activity, final List<String> tittles,
            @NonNull final List<Fragment> fragments) {
        FragmentManager manager = null;
        if (activity instanceof FragmentActivity) {
            manager = ((FragmentActivity) activity).getSupportFragmentManager();
        } else if (activity instanceof Fragment) {
            manager = ((Fragment) activity).getChildFragmentManager();
        }
        if (manager == null) {
            return null;
        }
        FragmentStatePagerAdapter fragmentPagerAdapter = new FragmentStatePagerAdapter(manager) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }


            @Override
            public CharSequence getPageTitle(int position) {
                if (tittles == null) {
                    return "";
                }
                return position < tittles.size() ? tittles.get(position) : "";
            }
        };
        return fragmentPagerAdapter;
    }

}
