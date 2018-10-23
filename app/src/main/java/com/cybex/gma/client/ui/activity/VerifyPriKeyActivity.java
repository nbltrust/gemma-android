package com.cybex.gma.client.ui.activity;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.cybex.gma.client.R;
import com.cybex.gma.client.event.BarcodeScanEvent;
import com.cybex.componentservice.manager.PermissionManager;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.JNIUtil;
import com.cybex.componentservice.utils.listener.PermissionResultListener;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.common.utils.AppManager;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class VerifyPriKeyActivity extends XActivity {

    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.edt_input_priKey) EditText edtInputPriKey;
    @BindView(R.id.bt_verify_input) Button btVerifyInput;
    private String private_key;

    @OnTextChanged(value = R.id.edt_input_priKey, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onInputChanged(Editable s){
        if (EmptyUtils.isNotEmpty(s.toString())){
            btVerifyInput.setClickable(true);
            btVerifyInput.setBackground(getDrawable(R.drawable.eos_shape_corner_button));
        }else {
            btVerifyInput.setClickable(false);
            btVerifyInput.setBackground(getDrawable(R.drawable.shape_corner_button_unclickable));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void showPriKey(BarcodeScanEvent message) {
        if (!EmptyUtils.isEmpty(message)) {
            edtInputPriKey.setText(message.getResult());
        }
    }

    @OnClick(R.id.bt_verify_input)
    public void onVerifyFinish(){
        final String inputKey = edtInputPriKey.getText().toString().trim();
        final String key = JNIUtil.get_public_key(inputKey);

        if (key.equals("invalid priv string!")) {
            //验证格式未通过
            GemmaToastUtils.showLongToast(getResources().getString(R.string.eos_prikey_format_invalid));
        } else if (private_key.equals(inputKey)){
            //验证通过
            AppManager.getAppManager().finishAllActivity();
            UISkipMananger.launchHomeSingle(this);
        }else {
            //格式通过，但私钥不匹配
            GemmaToastUtils.showLongToast("Invalid key");
        }
    }

    @Override
    public void bindUI(View rootView) {
        ButterKnife.bind(this);
        setNavibarTitle(getString(R.string.eos_verify_priKey), true);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        btVerifyInput.setClickable(false);
        btVerifyInput.setBackground(getDrawable(R.drawable.shape_corner_button_unclickable));
        private_key = "";
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            private_key = bundle.getString("private_key");
        }
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
                                          GemmaToastUtils.showShortToast(getResources().getString(R.string.eos_set_camera_permission));
                                          if (AndPermission.hasAlwaysDeniedPermission(mContext, permissions)) {
                                              manager.showSettingDialog(mContext, permissions);
                                          }

                                      }
                                  }, Permission.CAMERA
                , Permission.READ_EXTERNAL_STORAGE);
    }

    @Override
    public int getLayoutId() {
        return R.layout.eos_activity_verify_prikey;
    }

    @Override
    public Object newP() {
        return null;
    }

    @Override
    public boolean useEventBus() {
        return true;
    }
}