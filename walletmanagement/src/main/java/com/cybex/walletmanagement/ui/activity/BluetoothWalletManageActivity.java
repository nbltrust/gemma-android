package com.cybex.walletmanagement.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.allen.library.SuperTextView;
import com.cybex.componentservice.config.CacheConstants;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.componentservice.utils.AlertUtil;

import com.cybex.componentservice.utils.bluetooth.BlueToothWrapper;

import com.cybex.walletmanagement.R;
import com.cybex.walletmanagement.config.WalletManageConst;
import com.cybex.walletmanagement.event.ContextHandleEvent;

import com.cybex.walletmanagement.ui.fragment.BluetoothWalletManageFragment;
import com.extropies.common.MiddlewareInterface;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.hxlx.core.lib.utils.SPUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import me.framework.fragmentation.anim.DefaultHorizontalAnimator;
import me.framework.fragmentation.anim.FragmentAnimator;

/**
 * 蓝牙钱包管理页
 */
public class BluetoothWalletManageActivity extends XActivity {

    @Override
    public void bindUI(View view) {
        if (findFragment(BluetoothWalletManageFragment.class) == null) {
            loadRootFragment(R.id.fl_container_bluetooth_wallet_manage, BluetoothWalletManageFragment.newInstance());
        }

        //让布局向上移来显示软键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
    }

    @Override
    public int getLayoutId() {
        return R.layout.walletmanage_activity_bluetooth_wallet_manage;
    }

    @Override
    public Object newP() {
        return null;
    }


    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        // 设置横向(和安卓4.x动画相同)
        return new DefaultHorizontalAnimator();
    }

}
