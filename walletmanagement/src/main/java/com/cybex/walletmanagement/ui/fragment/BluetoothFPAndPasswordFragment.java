package com.cybex.walletmanagement.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.allen.library.SuperTextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.cybex.componentservice.config.BaseConst;
import com.cybex.componentservice.config.RouterConst;
import com.cybex.componentservice.db.entity.FPEntity;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.db.entity.WalletEntity;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.DeviceOperationManager;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.componentservice.utils.bluetooth.BlueToothWrapper;
import com.cybex.walletmanagement.R;
import com.cybex.walletmanagement.event.ContextHandleEvent;
import com.cybex.walletmanagement.manager.UISkipMananger;
import com.cybex.walletmanagement.ui.activity.BluetoothChangePasswordActivity;
import com.cybex.walletmanagement.ui.adapter.BluetoothFPManageAdapter;
import com.cybex.walletmanagement.ui.model.vo.BluetoothFPVO;
import com.extropies.common.CommonUtility;
import com.extropies.common.MiddlewareInterface;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.GsonUtils;
import com.hxlx.core.lib.utils.SPUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class BluetoothFPAndPasswordFragment extends XFragment {

    TitleBar btnNavibar;
    RecyclerView recyclerFpManage;
    SuperTextView superTextViewBluetoothAddFP;
    LinearLayout layoutFpNumber;
    SuperTextView superTextViewBluetoothChangePass;
    ScrollView scrollWalletManage;
    private int currentID;
    private MultiWalletEntity curWallet;
    private long mContextHandle;
    private List<BluetoothFPVO> fingerprints;
    private BluetoothFPManageAdapter mAdapter;
    //    private BlueToothWrapper getFpListThread;
//    private ConnectHandler mConnectHandler;
    private MiddlewareInterface.FingerPrintID[] mFpList;
    private String password;

    public static BluetoothFPAndPasswordFragment newInstance(String password) {
        Bundle args = new Bundle();
        args.putString(BaseConst.KEY_PASSWORD,password);
        BluetoothFPAndPasswordFragment fragment = new BluetoothFPAndPasswordFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onContextHandleRecieved(ContextHandleEvent event) {
        mContextHandle = event.getContextHanle();
    }

    @Override
    public void bindUI(View rootView) {

        setNavibarTitle(getResources().getString(R.string.walletmanage_fp_and_password), true, true);

        btnNavibar = rootView.findViewById(R.id.btn_navibar);
        recyclerFpManage = rootView.findViewById(R.id.recycler_fp_manage);
        superTextViewBluetoothAddFP = rootView.findViewById(R.id.superTextView_bluetooth_addFP);
        layoutFpNumber = rootView.findViewById(R.id.layout_fp_number);
        superTextViewBluetoothChangePass = rootView.findViewById(R.id.superTextView_bluetooth_changePass);
        scrollWalletManage = rootView.findViewById(R.id.scroll_wallet_manage);
    }

    @Override
    public boolean useEventBus() {
        return true;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        password = arguments.getString(BaseConst.KEY_PASSWORD);

        curWallet = DBManager.getInstance().getMultiWalletEntityDao().getBluetoothWalletList().get(0);
        if (curWallet != null) {
            currentID = curWallet.getId();
        }

        fingerprints = new ArrayList<>();
        initFingerPrintsInfo();

        //修改密码
        superTextViewBluetoothChangePass.setOnSuperTextViewClickListener(
                new SuperTextView.OnSuperTextViewClickListener() {
                    @Override
                    public void onClickListener(SuperTextView superTextView) {
                        Intent intent = new Intent(getActivity(), BluetoothChangePasswordActivity.class);
                        intent.putExtra(BaseConst.KEY_PASSWORD, password);
                        getActivity().startActivity(intent);
                    }
                });

        //添加指纹
        superTextViewBluetoothAddFP.setOnSuperTextViewClickListener(
                new SuperTextView.OnSuperTextViewClickListener() {
                    @Override
                    public void onClickListener(SuperTextView superTextView) {
                        ARouter.getInstance().build(RouterConst.PATH_TO_WALLET_ENROOL_FP_PAGE).navigation();
                    }
                });
    }

    @Override
    public void onDestroy() {
        DeviceOperationManager.getInstance().clearCallback(this.toString());
        super.onDestroy();
    }

    @Override
    public int getLayoutId() {
        return R.layout.walletmanage_fragment_bluetooth_fp_and_password;
    }

    @Override
    public Object newP() {
        return null;
    }

    /**
     * 显示添加指纹选项卡
     */
    public void showAddFPCard() {
        superTextViewBluetoothAddFP.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏添加指纹选项卡
     */
    public void hideAddFPCard() {
        superTextViewBluetoothAddFP.setVisibility(View.GONE);
    }

    /**
     * 从蓝牙卡获取指纹列表并显示
     */
    public void initFingerPrintsInfo() {
//        mConnectHandler = new ConnectHandler();
//        if ((getFpListThread == null) || (getFpListThread.getState() == Thread.State.TERMINATED)) {
//            getFpListThread = new BlueToothWrapper(mConnectHandler);
//            getFpListThread.setGetFPListWrapper(mContextHandle, 0);
//            getFpListThread.start();
//        }

        showProgressDialog(getString(R.string.walletmanage_loading));
        DeviceOperationManager.getInstance().getFPList(this.toString(), curWallet.getBluetoothDeviceName(), new DeviceOperationManager.GetFPListCallback() {
            @Override
            public void onSuccess(BlueToothWrapper.GetFPListReturnValue returnValue) {
                //获取指纹信息成功
                MiddlewareInterface.FingerPrintID[] fpList = returnValue.getFPList();
                int fpCount = returnValue.getFPCount();
                if (fpCount >= 3) {
                    hideAddFPCard();
                } else {
                    showAddFPCard();
                }
                //展示指纹信息
                dissmisProgressDialog();
                mFpList = fpList;
                showFingerprintsInfo(fpCount, mFpList);

//                for (int i = 0; i < returnValue.getFPCount(); i++) {
//                    LoggerManager.d("FP Index: " + CommonUtility.byte2hex(fpList[i].data));
//                }
            }

            @Override
            public void onFail() {

            }
        });


    }

    /**
     * 从设备获取指纹后在recyclerView中展示出来
     */
    private void showFingerprintsInfo(int fpCount, MiddlewareInterface.FingerPrintID[] fpList) {
        List<FPEntity> fpEntities = curWallet.getFpEntities();
        for (int i = 0; i < fpCount; i++) {
            BluetoothFPVO bluetoothFPVO = new BluetoothFPVO();
            bluetoothFPVO.setFingerprintIndex(fpList[i].data);
            bluetoothFPVO.setFingerprintName(getString(R.string.walletmanage_fp_prefix) + String.valueOf(i + 1));
            for (int j = 0; j < fpEntities.size(); j++) {
                FPEntity fpEntity = fpEntities.get(j);
                if (fpEntity.getIndex()==fpList[i].data[0]){
                    bluetoothFPVO.setFingerprintName(fpEntity.getName());
                    break;
                }
            }
            fingerprints.add(bluetoothFPVO);
        }

//        String jsonList = GsonUtils.objectToJson(fingerprints);
//        SPUtils.getInstance().put("fingerPrints", jsonList);

        mAdapter = new BluetoothFPManageAdapter(fingerprints);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager
                .VERTICAL, false);
        recyclerFpManage.setLayoutManager(layoutManager);
        recyclerFpManage.setAdapter(mAdapter);

        recyclerFpManage.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                //每个指纹选项卡的点击事件
                Bundle bundle = new Bundle();
                BluetoothFPVO curFpVO = fingerprints.get(position);
//                bundle.putString("fpName", curFpVO.getFingerprintName());
                bundle.putByteArray("fpIndex", curFpVO.getFingerprintIndex());

                start(BluetoothManageFPFragment.newInstance(bundle));

            }
        });
    }

//    class ConnectHandler extends Handler {
//
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case BlueToothWrapper.MSG_GET_FP_LIST_START:
//                    showProgressDialog("Getting fingerprint info");
//                    //LoggerManager.d("MSG_GET_FP_LIST_START");
//                    break;
//                case BlueToothWrapper.MSG_GET_FP_LIST_FINISH:
//                    LoggerManager.d("MSG_GET_FP_LIST_FINISH");
//                    BlueToothWrapper.GetFPListReturnValue returnValue = (BlueToothWrapper.GetFPListReturnValue) msg.obj;
//                    if (returnValue.getReturnValue() == MiddlewareInterface.PAEW_RET_SUCCESS) {
//                        //获取指纹信息成功
//                        MiddlewareInterface.FingerPrintID[] fpList = returnValue.getFPList();
//                        int fpCount = returnValue.getFPCount();
//                        if (fpCount >= 3) {
//                            hideAddFPCard();
//                        } else {
//                            showAddFPCard();
//                        }
//                        //展示指纹信息
//                        dissmisProgressDialog();
//                        mFpList = fpList;
//                        showFingerprintsInfo(fpCount, mFpList);
//
//                        for (int i = 0; i < returnValue.getFPCount(); i++) {
//                            LoggerManager.d("FP Index: " + CommonUtility.byte2hex(fpList[i].data));
//                        }
//                    }
//                    //LoggerManager.d("Return Value: " + MiddlewareInterface.getReturnString(returnValue
//                    //.getReturnValue()));
//                    break;
//
//                default:
//                    break;
//            }
//        }
//    }

}
