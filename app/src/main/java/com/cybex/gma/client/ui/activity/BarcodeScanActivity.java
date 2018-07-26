package com.cybex.gma.client.ui.activity;

import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageView;

import com.cybex.gma.client.R;
import com.cybex.gma.client.event.BarcodeScanEvent;
import com.cybex.gma.client.manager.LoggerManager;
import com.cybex.gma.client.manager.PermissionManager;
import com.cybex.gma.client.utils.SoundPoolHelper;
import com.cybex.gma.client.utils.listener.PermissionResultListener;
import com.cybex.qrcode.core.QRCodeView;
import com.cybex.qrcode.zxing.ZXingView;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 二维码扫描窗口
 *
 * Created by wanglin on 2018/7/23.
 */
public class BarcodeScanActivity extends XActivity implements QRCodeView.Delegate {

    @BindView(R.id.view_barcode_zxing) ZXingView mZXingView;
    @BindView(R.id.btn_back) ImageView btnBack;

    private SoundPoolHelper soundPoolHelper;
    private static final int REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY = 666;

    @Override
    public void bindUI(View rootView) {
        ButterKnife.bind(this);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        soundPoolHelper = new SoundPoolHelper().loadDefault(this);
        mZXingView.setDelegate(this);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_barcode_scan;
    }

    @Override
    public Object newP() {
        return null;
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    @Override
    public boolean useEventBus() {
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        PermissionManager manager = PermissionManager.getInstance(this);
        manager.requestPermission(new PermissionResultListener() {
                                      @Override
                                      public void onPermissionGranted() {
                                          openCameraScan();
                                      }

                                      @Override
                                      public void onPermissionDenied(List<String> permissions) {
                                          GemmaToastUtils.showShortToast("请设置相机相关权限");
                                          if (AndPermission.hasAlwaysDeniedPermission(mContext, permissions)) {
                                              manager.showSettingDialog(mContext, permissions);
                                          }

                                      }
                                  }, Permission.CAMERA
                , Permission.READ_EXTERNAL_STORAGE);
    }


    private void openCameraScan() {
        mZXingView.startCamera(); // 打开后置摄像头开始预览，但是并未开始识别
        mZXingView.startSpotAndShowRect(); // 显示扫描框，并且延迟0.5秒后开始识别
    }

    @Override
    protected void onStop() {
        mZXingView.stopCamera(); // 关闭摄像头预览，并且隐藏扫描框
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundPoolHelper.release();
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        LoggerManager.d("barcode scan result:" + result);
        // 发出声音和震动提示
        vibrate();
        soundPoolHelper.playDefault();
        //发送 扫描结果event
        EventBusProvider.post(new BarcodeScanEvent(result));
        mZXingView.startSpot(); // 延迟0.5秒后开始识别
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        LoggerManager.d("barcode scan error");

    }


}
