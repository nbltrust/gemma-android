package com.cybex.gma.client.ui.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.cybex.componentservice.config.CacheConstants;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.event.ContextHandleEvent;
import com.cybex.gma.client.job.BluetoothConnectKeepJob;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.model.vo.BluetoothDeviceVO;
import com.cybex.gma.client.ui.presenter.BluetoothPairPresenter;
import com.cybex.gma.client.utils.bluetooth.BlueToothWrapper;
import com.extropies.common.MiddlewareInterface;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.SPUtils;
import com.siberiadante.customdialoglib.CustomFullDialog;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 蓝牙配对窗口
 *
 * Created by wanglin on 2018/9/3.
 */
@Route(path = "/bluetooth/pair")
public class BluetoothPairActivity extends XActivity<BluetoothPairPresenter> {


    @BindView(R.id.btn_start_scan) Button btnStartScan;

    @Override
    public void bindUI(View rootView) {
        ButterKnife.bind(this);
        setNavibarTitle(getString(R.string.eos_title_paire_bluetooth), true);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        //配对点击事件
        btnStartScan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                UISkipMananger.startActivity(BluetoothPairActivity.this, BluetoothScanResultDialogActivity.class);
            }
        });

    }

    @Override
    public int getLayoutId() {
        return R.layout.eos_activity_bluetooth_pair;
    }

    @Override
    public BluetoothPairPresenter newP() {
        return new BluetoothPairPresenter();
    }

    @Override
    public boolean useArouter() {
        return true;
    }
}
