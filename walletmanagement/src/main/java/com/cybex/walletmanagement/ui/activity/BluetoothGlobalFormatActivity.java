package com.cybex.walletmanagement.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.cybex.componentservice.config.RouterConst;
import com.cybex.componentservice.event.DeviceConnectStatusUpdateEvent;
import com.cybex.componentservice.ui.activity.BluetoothBaseActivity;
import com.cybex.walletmanagement.R;
import com.cybex.walletmanagement.ui.fragment.BluetoothWalletDetailFragment;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import me.framework.fragmentation.anim.DefaultHorizontalAnimator;
import me.framework.fragmentation.anim.FragmentAnimator;

/**
 * 备份助记词引导页
 */
@Route(path = RouterConst.PATH_TO_FORMAT_PAGE)
public class BluetoothGlobalFormatActivity extends BluetoothBaseActivity {

    @Override
    public void bindUI(View view) {
//        Bundle  bd = getIntent().getExtras();
        if (findFragment(BluetoothWalletDetailFragment.class) == null) {
            loadRootFragment(R.id.fl_container_backup_mne_guide, BluetoothWalletDetailFragment.newInstance());
        }

        //让布局向上移来显示软键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
    }

    @Override
    public int getLayoutId() {
        return R.layout.eos_activity_bluetooth_backup_mne_guide;
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

    @Override
    public boolean useEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveDeviceConnectEvent(DeviceConnectStatusUpdateEvent event){
        fixDeviceDisconnectEvent(event);
    }
}
