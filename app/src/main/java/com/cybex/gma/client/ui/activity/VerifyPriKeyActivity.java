package com.cybex.gma.client.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.cybex.gma.client.R;
import com.cybex.gma.client.manager.PermissionManager;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.JNIUtil;
import com.cybex.gma.client.ui.fragment.ImportWalletConfigFragment;
import com.cybex.gma.client.utils.listener.PermissionResultListener;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.hxlx.core.lib.utils.common.utils.AppManager;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VerifyPriKeyActivity extends XActivity {

    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.edt_input_priKey) MaterialEditText edtInputPriKey;
    @BindView(R.id.bt_verify_input) Button btVerifyInput;

    @OnClick(R.id.bt_verify_input)
    public void onVerifyFinish(){
        final String inputKey = edtInputPriKey.getText().toString().trim();
        final String key = JNIUtil.get_public_key(inputKey);

        if (key.equals("invalid priv string!")) {
            //验证未通过
            GemmaToastUtils.showLongToast(getResources().getString(R.string.prikey_format_invalid));
        } else {
            //验证通过
            AppManager.getAppManager().finishActivity();
            AppManager.getAppManager().finishActivity(BackUpPrivatekeyActivity.class);
            AppManager.getAppManager().finishActivity(BackUpWalletGuideActivity.class);
            UISkipMananger.launchHomeSingle(this);
        }
    }

    @Override
    public void bindUI(View rootView) {
        ButterKnife.bind(this);
        setNavibarTitle(getString(R.string.backup_prikey), true);
    }

    @Override
    public void initData(Bundle savedInstanceState) {

    }

    @Override
    protected void setNavibarTitle(String title, boolean isShowBack) {
        super.setNavibarTitle(title, isShowBack);

        ImageView mCollectView = (ImageView) mTitleBar.addAction(new TitleBar.ImageAction(R.drawable.ic_notify_scan) {
            @Override
            public void performAction(View view) {
                skipBarcodeScan();
            }
        });
    }

    private void skipBarcodeScan() {
        PermissionManager manager = PermissionManager.getInstance(this);
        manager.requestPermission(new PermissionResultListener() {
                                      @Override
                                      public void onPermissionGranted() {
                                          UISkipMananger.launchBarcodeScan(mContext);
                                      }

                                      @Override
                                      public void onPermissionDenied(List<String> permissions) {
                                          GemmaToastUtils.showShortToast(getResources().getString(R.string.set_camera_permission));
                                          if (AndPermission.hasAlwaysDeniedPermission(mContext, permissions)) {
                                              manager.showSettingDialog(mContext, permissions);
                                          }

                                      }
                                  }, Permission.CAMERA
                , Permission.READ_EXTERNAL_STORAGE);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_verify_prikey;
    }

    @Override
    public Object newP() {
        return null;
    }

}