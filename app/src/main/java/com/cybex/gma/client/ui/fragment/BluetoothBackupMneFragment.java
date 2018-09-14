package com.cybex.gma.client.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;

import com.cybex.gma.client.R;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.utils.bluetooth.BlueToothWrapper;
import com.cybex.gma.client.widget.LabelsView;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.siberiadante.customdialoglib.CustomDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 备份助记词页面
 */
public class BluetoothBackupMneFragment extends XFragment {

    Unbinder unbinder;
    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.view_labels) LabelsView viewShowMne;
    @BindView(R.id.bt_copied_mne) Button btCopiedMne;

    BlueToothWrapper generateSeedThread;
    BluetoothHandler handler;
    Bundle bd = null;
    long contextHandle = 0;

    class BluetoothHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BlueToothWrapper.MSG_GENERATE_SEED_MNES_START:
                case BlueToothWrapper.MSG_GENERATE_SEED_MNES_FINISH:
                    BlueToothWrapper.GenSeedMnesReturnValue value = (BlueToothWrapper.GenSeedMnesReturnValue) msg.obj;
                    if (EmptyUtils.isNotEmpty(value)) {
                        String[] mnes = value.getStrMneWord();
                        bd.putParcelable(ParamConstants.KEY_GEEN_SEED, value);

                        List<String> labels = new ArrayList<>();
                        if (EmptyUtils.isNotEmpty(mnes)) {
                            for (int i = 0; i < mnes.length; i++) {
                                String word = mnes[i];
                                labels.add(word);
                            }
                        }

                        viewShowMne.setLabels(labels);
//                        viewShowMne.setLabelBackgroundDrawable(
//                                getResources().getDrawable(R.drawable.selector_label_bg));
                        viewShowMne.setWordMargin(60);
                        viewShowMne.setLineMargin(20);
                        viewShowMne.setSelectType(LabelsView.SelectType.NONE);
                        viewShowMne.setLabelTextSize(48);
                        viewShowMne.setLabelTextColor(Color.parseColor("#4169d1"));
                        //viewShowMne.setLabelTextPadding(40, 20, 40, 20);
                    }

                    break;
                default:
                    break;
            }
        }
    }

    public static BluetoothBackupMneFragment newInstance(Bundle bd) {
        BluetoothBackupMneFragment fragment = new BluetoothBackupMneFragment();
        fragment.setArguments(bd);
        return fragment;
    }

    @OnClick(R.id.bt_copied_mne)
    public void goVerifyMne() {
        start(BluetoothVerifyMneFragment.newInstance(bd));
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
        setNavibarTitle(getResources().getString(R.string.backup_mne), true, false);
    }


    @Override
    public void initData(Bundle savedInstanceState) {
        bd = getArguments();
        if (bd != null) {
            contextHandle = bd.getLong(ParamConstants.CONTEXT_HANDLE);
        }

        handler = new BluetoothHandler();
        generateSeedThread = new BlueToothWrapper(handler);
        generateSeedThread.setGenerateSeedGetMnesWrapper(contextHandle, 0, 32);
        generateSeedThread.start();

        showAlertDialog();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_backup_mne;
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

    /**
     * 显示请勿截图Dialog
     */
    private void showAlertDialog() {
        int[] listenedItems = {R.id.tv_i_understand};
        CustomDialog dialog = new CustomDialog(getContext(),
                R.layout.dialog_no_screenshot_mne, listenedItems, false, Gravity.CENTER);
        dialog.setOnDialogItemClickListener(new CustomDialog.OnCustomDialogItemClickListener() {

            @Override
            public void OnCustomDialogItemClick(CustomDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.tv_i_understand:
                        dialog.cancel();
                        break;
                    default:
                        break;
                }
            }
        });
        dialog.show();
    }
}
