package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.allen.library.SuperTextView;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.componentservice.db.entity.WalletEntity;
import com.cybex.gma.client.event.ContextHandleEvent;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.gma.client.manager.LoggerManager;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.JNIUtil;
import com.cybex.gma.client.ui.adapter.BluetoothFPManageAdapter;
import com.cybex.gma.client.ui.model.vo.BluetoothFPVO;
import com.cybex.gma.client.utils.bluetooth.BlueToothWrapper;
import com.extropies.common.CommonUtility;
import com.extropies.common.MiddlewareInterface;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.siberiadante.customdialoglib.CustomDialog;
import com.siberiadante.customdialoglib.CustomFullDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class BluetoothFPAndPasswordFragment extends XFragment {

    Unbinder unbinder;
    private int inputCount;
    private int currentID;
    private WalletEntity curWallet;
    private long mContextHandle;
    private List<BluetoothFPVO> fingerprints;
    private BluetoothFPManageAdapter mAdapter;
    private BlueToothWrapper getFpListThread;
    private ConnectHandler mConnectHandler;
    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.recycler_fp_manage) RecyclerView recyclerFpManage;
    @BindView(R.id.superTextView_bluetooth_addFP) SuperTextView superTextViewBluetoothAddFP;
    @BindView(R.id.layout_fp_number) LinearLayout layoutFpNumber;
    @BindView(R.id.superTextView_bluetooth_changePass) SuperTextView superTextViewBluetoothChangePass;
    @BindView(R.id.scroll_wallet_manage) ScrollView scrollWalletManage;

    public static BluetoothFPAndPasswordFragment newInstance() {
        Bundle args = new Bundle();
        BluetoothFPAndPasswordFragment fragment = new BluetoothFPAndPasswordFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @OnClick({R.id.superTextView_bluetooth_changePass})
    public void goChangePassword(){
        showConfirmAuthorDialog();
    }

    @OnClick(R.id.superTextView_bluetooth_addFP)
    public void goAddFingerPrint(){
        Bundle bundle = new Bundle();
        bundle.putLong(ParamConstants.CONTEXT_HANDLE, mContextHandle);
        UISkipMananger.skipBluetoothSettingFPActivity(getActivity(), bundle);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onContextHandleRecieved(ContextHandleEvent event){
        mContextHandle = event.getContextHanle();
    }

    @Override
    public boolean useEventBus() {
        return true;
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(BluetoothFPAndPasswordFragment.this, rootView);
        setNavibarTitle(getResources().getString(R.string.fp_and_password), true, true);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        curWallet = DBManager.getInstance().getWalletEntityDao().getBluetoothWalletList().get(0);
        if (curWallet != null){
            currentID = curWallet.getId();
        }

        fingerprints = new ArrayList<>();
        initFingerPrintsInfo();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_bluetooth_fp_and_password;
    }

    @Override
    public Object newP() {
        return null;
    }

    /**
     * 从蓝牙卡获取指纹列表并显示
     */
    public void initFingerPrintsInfo(){
        mConnectHandler = new ConnectHandler();
        if ((getFpListThread == null) || (getFpListThread.getState() == Thread.State.TERMINATED))
        {
            getFpListThread = new BlueToothWrapper(mConnectHandler);
            getFpListThread.setGetFPListWrapper(mContextHandle, 0);
            getFpListThread.start();
        }
    }

    /**
     * 显示确认授权dialog
     */
    private void showConfirmAuthorDialog() {
        int[] listenedItems = {R.id.imc_cancel, R.id.btn_confirm_authorization};
        CustomFullDialog dialog = new CustomFullDialog(getContext(),
                R.layout.dialog_input_origin_password, listenedItems, false, Gravity.BOTTOM);
        dialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
            @Override
            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.imc_cancel:
                        dialog.cancel();
                        break;
                    case R.id.btn_confirm_authorization:
                        EditText edtPassword = dialog.findViewById(R.id.et_password);
                        ImageView iv_clear = dialog.findViewById(R.id.iv_password_clear);
                        iv_clear.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                edtPassword.setText("");
                            }
                        });
                        //检查密码是否正确
                        final String inputPass = edtPassword.getText().toString().trim();
                        //重新获取curWallet，为确保修改密码过后验证的是新的密码
                        WalletEntity walletEntity = DBManager.getInstance().getWalletEntityDao().getWalletEntityByID
                                (currentID);
                        if (!EmptyUtils.isEmpty(walletEntity)){
                            final String cypher = walletEntity.getCypher();
                            final String priKey = JNIUtil.get_private_key(cypher, inputPass);
                            final String generatedCypher = JNIUtil.get_cypher(inputPass, priKey);
                            if (cypher.equals(generatedCypher)){
                                //验证通过
                                start(ChangePasswordFragment.newInstance(priKey,currentID));
                                dialog.cancel();
                            }else {
                                inputCount++;
                                iv_clear.setVisibility(View.VISIBLE);
                                GemmaToastUtils.showLongToast(getResources().getString(R.string.wrong_password));
                                if ( inputCount > 3 ){
                                    dialog.cancel();
                                    showPasswordHintDialog();
                                }
                            }
                        }

                        break;
                    default:
                        break;
                }
            }
        });
        dialog.show();
        dialog.show();
        EditText etPasword = dialog.findViewById(R.id.et_password);
        etPasword.setHint("请输入@" + curWallet.getCurrentEosName() + "的密码");
    }

    /**
     * 显示密码提示Dialog
     */
    private void showPasswordHintDialog() {
        int[] listenedItems = {R.id.tv_i_understand};
        CustomDialog dialog = new CustomDialog(getContext(),
                R.layout.dialog_password_hint, listenedItems, false, Gravity.CENTER);
        dialog.setOnDialogItemClickListener(new CustomDialog.OnCustomDialogItemClickListener() {

            @Override
            public void OnCustomDialogItemClick(CustomDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.tv_i_understand:
                        dialog.cancel();
                        showConfirmAuthorDialog();
                        break;
                    default:
                        break;
                }
            }
        });
        dialog.show();

        TextView tv_pass_hint = dialog.findViewById(R.id.tv_password_hint_hint);
        if (EmptyUtils.isNotEmpty(curWallet)){
            String passHint = curWallet.getPasswordTip();
            String showInfo = getString(R.string.password_hint_info) + " : " + passHint;
            tv_pass_hint.setText(showInfo);
        }
    }

    class ConnectHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BlueToothWrapper.MSG_GET_FP_LIST_START:
                    //LoggerManager.d("MSG_GET_FP_LIST_START");
                    break;
                case BlueToothWrapper.MSG_GET_FP_LIST_FINISH:
                    LoggerManager.d("MSG_GET_FP_LIST_FINISH");
                    BlueToothWrapper.GetFPListReturnValue returnValue = (BlueToothWrapper.GetFPListReturnValue) msg.obj;
                    if (returnValue.getReturnValue() == MiddlewareInterface.PAEW_RET_SUCCESS) {
                        //获取指纹信息成功
                        MiddlewareInterface.FingerPrintID[] fpList = returnValue.getFPList();
                        int fpCount = returnValue.getFPCount();
                        for (int i = 0; i < fpCount; i++){
                            BluetoothFPVO bluetoothFPVO = new BluetoothFPVO();
                            bluetoothFPVO.setFingerprintIndex(fpList[i].data);
                            bluetoothFPVO.setFingerprintName("Fingerprint" + String.valueOf(i+1));
                            fingerprints.add(bluetoothFPVO);
                        }

                        mAdapter = new BluetoothFPManageAdapter(fingerprints);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager
                                .VERTICAL, false);
                        recyclerFpManage.setLayoutManager(layoutManager);
                        recyclerFpManage.setAdapter(mAdapter);






                        for (int i = 0; i < returnValue.getFPCount(); i++) {
                            LoggerManager.d("FP Index: " + CommonUtility.byte2hex(fpList[i].data));
                        }
                    }
                    //LoggerManager.d("Return Value: " + MiddlewareInterface.getReturnString(returnValue
                       //.getReturnValue()));
                    break;

                default:
                    break;
            }
        }
    }

}
