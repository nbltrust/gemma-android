package com.cybex.gma.client.ui.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.ToxicBakery.viewpager.transforms.CubeOutTransformer;
import com.ToxicBakery.viewpager.transforms.FlipHorizontalTransformer;
import com.ToxicBakery.viewpager.transforms.ForegroundToBackgroundTransformer;
import com.ToxicBakery.viewpager.transforms.RotateUpTransformer;
import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.cybex.componentservice.config.BaseConst;
import com.cybex.componentservice.config.RouterConst;
import com.cybex.componentservice.event.CloseInitialPageEvent;
import com.cybex.componentservice.event.WookongInitialedEvent;
import com.cybex.gma.client.R;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.adapter.InitPageAdapter;
import com.cybex.gma.client.utils.repeatclick.NoDoubleClick;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.lwj.widget.viewpagerindicator.ViewPagerIndicator;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.jessyan.autosize.AutoSize;


@Route(path = RouterConst.PATH_TO_INIT)
public class InitialActivity extends XActivity {

    @Autowired(name = BaseConst.KEY_INIT_TYPE)
    int initType = BaseConst.APP_HOME_INITTYPE_NONE;

    InitPageAdapter mAdapter;
    @BindView(R.id.viewPager_init) ViewPager viewPagerInit;
    @BindView(R.id.viewPager_indicator) ViewPagerIndicator viewPagerIndicator;
    @BindView(R.id.iv_wookong_logo_dark) ImageView ivWookongLogoDark;
    @BindView(R.id.bt_wookongbio) ConstraintLayout btWookongbio;

    private List<ImageView> mImageList;

    @BindView(R.id.bt_create_new) Button btCreateNew;
    @BindView(R.id.bt_import) Button btImport;

    @OnClick(R.id.bt_create_new)
    public void createWallet() {
        if (!NoDoubleClick.isDoubleClick()) {
            ARouter.getInstance().build(RouterConst.PATH_TO_CREATE_MNEMONIC_PAGE)
                    .navigation();
        }
    }

    @OnClick(R.id.bt_import)
    public void importWallet() {
        if (!NoDoubleClick.isDoubleClick()) {
            ARouter.getInstance().build(RouterConst.PATH_TO_IMPORT_WALLET_GUIDE_PAGE)
                    .navigation();
        }
    }

    @OnClick(R.id.bt_wookongbio)
    public void withWookong() {
        Bundle bd = new Bundle();
        UISkipMananger.skipBluetoothPaireActivity(InitialActivity.this, bd);
    }


    @Override
    public void bindUI(View rootView) {
        ButterKnife.bind(this);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        AutoSize.autoConvertDensityOfGlobal(InitialActivity.this);
        if (initType == BaseConst.APP_INIT_INITTYPE_TO_WOOKONG_PAIE) {
            Bundle bd = new Bundle();
            UISkipMananger.skipBluetoothPaireActivity(InitialActivity.this, bd);
        }

        int[] imageResIDs = {
                R.drawable.img_page_1,
                R.drawable.img_page_2,
                R.drawable.img_page_3,
        };

        mImageList = new ArrayList<>();
        ImageView iv;

        for (int i = 0; i < imageResIDs.length; i++) {
            iv = new ImageView(this);
            iv.setBackgroundResource(imageResIDs[i]);
            mImageList.add(iv);
        }

        mAdapter = new InitPageAdapter(mImageList, viewPagerInit);
        viewPagerInit.setAdapter(mAdapter);
        viewPagerIndicator.setViewPager(viewPagerInit);
        viewPagerInit.setPageTransformer(true,  new CubeOutTransformer());

    }


    @Override
    public int getLayoutId() {
        return R.layout.eos_activity_initial;
    }

    @Override
    public Object newP() {
        return null;
    }

    public void setNavigationBarStatusBarTranslucent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(Color.TRANSPARENT);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //Android 7.0以上适配
                try {
                    Class decorViewClazz = Class.forName("com.android.internal.policy.DecorView");
                    Field field = decorViewClazz.getDeclaredField("mSemiTransparentStatusBarColor");
                    field.setAccessible(true);
                    field.setInt(getWindow().getDecorView(), Color.TRANSPARENT);  //改为透明
                } catch (Exception e) {}
            }
        }
    }

    @Override
    public boolean useArouter() {
        return true;
    }

    @Override
    public boolean useEventBus() {
        return true;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void closeSelf(CloseInitialPageEvent event) {
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWookongInitial(WookongInitialedEvent event) {
        finish();
    }


}
