package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.cybex.gma.client.R;
import com.cybex.gma.client.event.BarcodeScanEvent;
import com.cybex.componentservice.manager.PermissionManager;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.JNIUtil;
import com.cybex.componentservice.utils.listener.PermissionResultListener;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.EmptyUtils;
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
import butterknife.Unbinder;

/**
 * 导入钱包输入私钥界面
 */

public class ImportWalletInputKeyFragment extends XFragment {

    @BindView(R.id.bt_start_input) Button btStartInput;
    @BindView(R.id.edt_show_priKey) MaterialEditText edtShowPrikey;
    Unbinder unbinder;

    @OnClick(R.id.bt_start_input)
    public void goConfigWallet() {
        final String inputKey = edtShowPrikey.getText().toString().trim();
        final String key = JNIUtil.get_public_key(inputKey);

        if (key.equals("invalid priv string!")) {
            GemmaToastUtils.showLongToast(getResources().getString(R.string.prikey_format_invalid));
        } else {
            start(ImportWalletConfigFragment.newInstance(inputKey));
        }
    }


    public static ImportWalletInputKeyFragment newInstance() {
        Bundle args = new Bundle();
        ImportWalletInputKeyFragment fragment = new ImportWalletInputKeyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @OnTextChanged(value = R.id.edt_show_priKey, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onTextChanged(Editable s) {
        if (EmptyUtils.isEmpty(edtShowPrikey.getText().toString().trim())) {
            setButtonUnclickable(btStartInput);
        } else {
            setButtonClickable(btStartInput);
        }
    }

    @Override
    public boolean useEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showPriKey(BarcodeScanEvent message) {
        if (!EmptyUtils.isEmpty(message)) {
            edtShowPrikey.setText(message.getResult());
        }
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(ImportWalletInputKeyFragment.this, rootView);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setButtonUnclickable(btStartInput);
        setNavibarTitle(getResources().getString(R.string.import_wallet), true, false);
    }

    @Override
    protected void setNavibarTitle(String title, boolean isShowBack, boolean isOnBackFinishActivity) {
        super.setNavibarTitle(title, isShowBack, isOnBackFinishActivity);

        ImageView mCollectView = (ImageView) mTitleBar.addAction(new TitleBar.ImageAction(R.drawable.ic_notify_scan) {
            @Override
            public void performAction(View view) {
                skipBarcodeScan();
            }
        });
    }

    private void skipBarcodeScan() {
        PermissionManager manager = PermissionManager.getInstance(getActivity());
        manager.requestPermission(new PermissionResultListener() {
                                      @Override
                                      public void onPermissionGranted() {
                                          UISkipMananger.launchBarcodeScan(getActivity());
                                      }

                                      @Override
                                      public void onPermissionDenied(List<String> permissions) {
                                          GemmaToastUtils.showShortToast(getResources().getString(R.string.set_camera_permission));
                                          if (AndPermission.hasAlwaysDeniedPermission(getActivity(), permissions)) {
                                              manager.showSettingDialog(getContext(), permissions);
                                          }

                                      }
                                  }, Permission.CAMERA
                , Permission.READ_EXTERNAL_STORAGE);
    }

    @Override
    public int getLayoutId() {
        return R.layout.eos_fragment_import_wallet_input_key;
    }

    @Override
    public Object newP() {
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void setButtonUnclickable(Button button) {
        button.setClickable(false);
        button.setBackground(getResources().getDrawable(R.drawable.shape_corner_button_unclickable));
    }

    public void setButtonClickable(Button button) {
        button.setClickable(true);
        button.setBackground(getResources().getDrawable(R.drawable.shape_corner_button));
    }


}
