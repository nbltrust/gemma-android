package com.cybex.gma.client.ui.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.cybex.componentservice.api.ApiPath;
import com.cybex.componentservice.config.BaseConst;
import com.cybex.componentservice.config.CacheConstants;
import com.cybex.componentservice.config.RouterConst;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.event.DeviceConnectStatusUpdateEvent;
import com.cybex.componentservice.event.WookongInitialedEvent;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.DeviceOperationManager;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.componentservice.manager.PermissionManager;
import com.cybex.componentservice.ui.activity.BluetoothBaseActivity;
import com.cybex.componentservice.ui.activity.CommonWebViewActivity;
import com.cybex.componentservice.utils.listener.PermissionResultListener;
import com.cybex.gma.client.R;
import com.cybex.gma.client.ui.dialog.WookongScanDialog;
import com.cybex.gma.client.ui.presenter.BluetoothPairPresenter;

import com.hxlx.core.lib.utils.LanguageManager;
import com.yanzhenjie.permission.Permission;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.jessyan.autosize.AutoSize;

import static com.raizlabs.android.dbflow.config.FlowManager.getContext;

/**
 * 蓝牙配对窗口
 *
 * Created by wanglin on 2018/9/3.
 */
@Route(path = RouterConst.PATH_TO_BLUETOOTH_PAIR)
public class BluetoothPairActivity extends BluetoothBaseActivity<BluetoothPairPresenter> {


    @BindView(R.id.btn_start_scan) Button btnStartScan;
    private WookongScanDialog wookongScanDialog;

    @BindView(R.id.tv_more_wookong)
    TextView tvMoreWookong;


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

                final PermissionManager manager = PermissionManager.getInstance(BluetoothPairActivity.this);
                manager.requestPermission(new PermissionResultListener() {
                                              @Override
                                              public void onPermissionGranted() {
//                                                  UISkipMananger.startActivity(BluetoothPairActivity.this, BluetoothScanResultDialogActivity.class);
                                                  AutoSize.autoConvertDensityOfGlobal(BluetoothPairActivity.this);
                                                  wookongScanDialog = new WookongScanDialog(BluetoothPairActivity.this, false, false);
                                                  wookongScanDialog.show();
                                              }

                                              @Override
                                              public void onPermissionDenied(List<String> permissions) {
//                                                  GemmaToastUtils.showShortToast("");
                                              }
                                          },  Permission.ACCESS_COARSE_LOCATION, Manifest.permission.BLUETOOTH,Manifest.permission.BLUETOOTH_ADMIN);
            }
        });

        tvMoreWookong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonWebViewActivity.startWebView(context, ApiPath.URL_OF_BIO_HOME, getResources().getString(R
                        .string.bio_official_website));

            }
        });

    }

    @Override
    public int getLayoutId() {
        return R.layout.eos_activity_bluetooth_pair;
    }

    @Override
    protected void onDestroy() {
        if(wookongScanDialog!=null){
            wookongScanDialog.dismissAllDialog();
        }
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(wookongScanDialog==null||wookongScanDialog.isShowing()){
            return;
        }
        //判断有没有当前的连接设备,而且本地数据库并没有任何WOOKONG WALLET,则断开连接
        String currentDeviceName = DeviceOperationManager.getInstance().getCurrentDeviceName();
        LoggerManager.d("currentDeviceName="+currentDeviceName);
        if(!TextUtils.isEmpty(currentDeviceName)){
            List<MultiWalletEntity> bluetoothWalletList = DBManager.getInstance().getMultiWalletEntityDao().getBluetoothWalletList();
            LoggerManager.d("bluetoothWalletList.size()="+bluetoothWalletList.size());
            if(bluetoothWalletList.size()==0){
                DeviceOperationManager.getInstance().freeContext(this.toString(), currentDeviceName,
                        null);
                DeviceOperationManager.getInstance().clearDevice(currentDeviceName);
            }
        }
    }

    @Override
    public BluetoothPairPresenter newP() {
        return new BluetoothPairPresenter();
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
    public void onWookongInitial(WookongInitialedEvent event) {
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveDeviceConnectEvent(DeviceConnectStatusUpdateEvent event){
        fixDeviceDisconnectEvent(event);
    }
}
