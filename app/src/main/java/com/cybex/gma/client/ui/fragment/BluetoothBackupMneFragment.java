package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cybex.componentservice.manager.DeviceOperationManager;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.manager.WookongBioManager;
import com.cybex.gma.client.utils.bluetooth.BlueToothWrapper;
import com.cybex.base.view.flowlayout.FlowLayout;
import com.cybex.base.view.flowlayout.TagAdapter;
import com.cybex.base.view.flowlayout.TagFlowLayout;
import com.cybex.gma.client.widget.LabelsView;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.siberiadante.customdialoglib.CustomDialog;

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
    @BindView(R.id.id_flowlayout) TagFlowLayout mFlowLayout;
    @BindView(R.id.bt_copied_mne) Button btCopiedMne;

//    BlueToothWrapper generateSeedThread;
//    BluetoothHandler handler;
    Bundle bd = null;
//    long contextHandle = 0;

    private TagAdapter<String> mAdapter;


//    class BluetoothHandler extends Handler {
//
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case BlueToothWrapper.MSG_GENERATE_SEED_MNES_START:
//                case BlueToothWrapper.MSG_GENERATE_SEED_MNES_FINISH:
//                    BlueToothWrapper.GenSeedMnesReturnValue value = (BlueToothWrapper.GenSeedMnesReturnValue) msg.obj;
//                    if (EmptyUtils.isNotEmpty(value)) {
//                        String[] mnes = value.getStrMneWord();
//                        bd.putParcelable(ParamConstants.KEY_GEEN_SEED, value);
//
//                        mFlowLayout.setAdapter(mAdapter = new TagAdapter<String>(mnes) {
//
//                            @Override
//                            public View getView(FlowLayout parent, int position, String s) {
//                                TextView tv = (TextView) getActivity().getLayoutInflater().inflate(R.layout.eos_item_tag,
//                                        mFlowLayout, false);
//                                tv.setText(s);
//                                return tv;
//                            }
//                        });
//
//                    }
//
//                    break;
//                default:
//                    break;
//            }
//        }
//    }

    public static BluetoothBackupMneFragment newInstance(Bundle bd) {
        BluetoothBackupMneFragment fragment = new BluetoothBackupMneFragment();
        fragment.setArguments(bd);
        return fragment;
    }

    @OnClick(R.id.bt_copied_mne)
    public void goVerifyMne() {
        replaceFragment(BluetoothVerifyMneFragment.newInstance(bd),false);
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
        setNavibarTitle(getResources().getString(R.string.eos_backup_mne), true, true);
    }


    @Override
    public void initData(Bundle savedInstanceState) {
        bd = getArguments();
        if (bd != null) {
//            contextHandle = bd.getLong(ParamConstants.CONTEXT_HANDLE);
        }

//        handler = new BluetoothHandler();
//        WookongBioManager.getInstance().init(handler);
//        WookongBioManager.getInstance().startGenerateSeedGetMnes(contextHandle,0, 16);

        DeviceOperationManager.getInstance().generateMnemonic(this.toString(), DeviceOperationManager.getInstance().getCurrentDeviceName(), new DeviceOperationManager.GenerateMnemonicCallback() {

            @Override
            public void onGenerateSuccess(com.cybex.componentservice.utils.bluetooth.BlueToothWrapper.GenSeedMnesReturnValue mnemonic) {
                String[] mnes = mnemonic.getStrMneWord();
                bd.putParcelable(ParamConstants.KEY_GEEN_SEED, mnemonic);

                mFlowLayout.setAdapter(mAdapter = new TagAdapter<String>(mnes) {

                    @Override
                    public View getView(FlowLayout parent, int position, String s) {
                        TextView tv = (TextView) getActivity().getLayoutInflater().inflate(R.layout.eos_item_tag,
                                mFlowLayout, false);
                        tv.setText(s);
                        return tv;
                    }
                });
            }

            @Override
            public void onGenerateFail() {

            }
        });

        showAlertDialog();
    }

    @Override
    public int getLayoutId() {
        return R.layout.eos_fragment_backup_mne;
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
        DeviceOperationManager.getInstance().clearCallback(this.toString());
        super.onDestroyView();
        unbinder.unbind();
//        if (handler != null){
//            WookongBioManager.getInstance().freeThread();
//            WookongBioManager.getInstance().freeResource();
//        }
    }


    /**
     * 显示请勿截图Dialog
     */
    private void showAlertDialog() {
        int[] listenedItems = {R.id.tv_i_understand};
        CustomDialog dialog = new CustomDialog(getContext(),
                R.layout.eos_dialog_no_screenshot_mne, listenedItems, false, Gravity.CENTER);
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
