//package com.cybex.gma.client.ui.fragment;
//
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.View;
//import android.widget.LinearLayout;
//import android.widget.ScrollView;
//
//import com.allen.library.SuperTextView;
//import com.chad.library.adapter.base.BaseQuickAdapter;
//import com.chad.library.adapter.base.listener.OnItemClickListener;
//import com.cybex.componentservice.db.entity.MultiWalletEntity;
//import com.cybex.gma.client.R;
//import com.cybex.gma.client.config.ParamConstants;
//import com.cybex.componentservice.db.entity.WalletEntity;
//import com.cybex.gma.client.event.ContextHandleEvent;
//import com.cybex.componentservice.manager.DBManager;
//import com.cybex.componentservice.manager.LoggerManager;
//import com.cybex.gma.client.manager.UISkipMananger;
//import com.cybex.gma.client.ui.adapter.BluetoothFPManageAdapter;
//import com.cybex.gma.client.ui.model.vo.BluetoothFPVO;
//import com.cybex.gma.client.utils.bluetooth.BlueToothWrapper;
//import com.extropies.common.CommonUtility;
//import com.extropies.common.MiddlewareInterface;
//import com.hxlx.core.lib.mvp.lite.XFragment;
//import com.hxlx.core.lib.utils.GsonUtils;
//import com.hxlx.core.lib.utils.SPUtils;
//import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
//
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import butterknife.OnClick;
//import butterknife.Unbinder;
//
//public class BluetoothFPAndPasswordFragment extends XFragment {
//
//    Unbinder unbinder;
//    private int currentID;
//    private MultiWalletEntity curWallet;
//    private long mContextHandle;
//    private List<BluetoothFPVO> fingerprints;
//    private BluetoothFPManageAdapter mAdapter;
//    private BlueToothWrapper getFpListThread;
//    private ConnectHandler mConnectHandler;
//    private MiddlewareInterface.FingerPrintID[] mFpList;
//    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
//    @BindView(R.id.recycler_fp_manage) RecyclerView recyclerFpManage;
//    @BindView(R.id.superTextView_bluetooth_addFP) SuperTextView superTextViewBluetoothAddFP;
//    @BindView(R.id.layout_fp_number) LinearLayout layoutFpNumber;
//    @BindView(R.id.superTextView_bluetooth_changePass) SuperTextView superTextViewBluetoothChangePass;
//    @BindView(R.id.scroll_wallet_manage) ScrollView scrollWalletManage;
//
//    public static BluetoothFPAndPasswordFragment newInstance() {
//        Bundle args = new Bundle();
//        BluetoothFPAndPasswordFragment fragment = new BluetoothFPAndPasswordFragment();
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @OnClick({R.id.superTextView_bluetooth_changePass})
//    public void goChangePassword(){
//
//    }
//
//    @OnClick(R.id.superTextView_bluetooth_addFP)
//    public void goAddFingerPrint(){
//        Bundle bundle = new Bundle();
//        bundle.putLong(ParamConstants.CONTEXT_HANDLE, mContextHandle);
//        UISkipMananger.skipBluetoothSettingFPActivity(getActivity(), bundle);
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
//    public void onContextHandleRecieved(ContextHandleEvent event){
//        mContextHandle = event.getContextHanle();
//    }
//
//    @Override
//    public boolean useEventBus() {
//        return true;
//    }
//
//    @Override
//    public void bindUI(View rootView) {
//        unbinder = ButterKnife.bind(BluetoothFPAndPasswordFragment.this, rootView);
//        setNavibarTitle(getResources().getString(R.string.eos_fp_and_password), true, true);
//    }
//
//    @Override
//    public void initData(Bundle savedInstanceState) {
//        curWallet = DBManager.getInstance().getMultiWalletEntityDao().getBluetoothWalletList().get(0);
//        if (curWallet != null){
//            currentID = curWallet.getId();
//        }
//
//        fingerprints = new ArrayList<>();
//        initFingerPrintsInfo();
//    }
//
//    @Override
//    public int getLayoutId() {
//        return R.layout.eos_fragment_bluetooth_fp_and_password;
//    }
//
//    @Override
//    public Object newP() {
//        return null;
//    }
//
//    /**
//     * 显示添加指纹选项卡
//     */
//    public void showAddFPCard(){
//        superTextViewBluetoothAddFP.setVisibility(View.VISIBLE);
//    }
//
//    /**
//     * 隐藏添加指纹选项卡
//     */
//    public void hideAddFPCard(){
//        superTextViewBluetoothAddFP.setVisibility(View.GONE);
//    }
//
//    /**
//     * 从蓝牙卡获取指纹列表并显示
//     */
//    public void initFingerPrintsInfo(){
//        mConnectHandler = new ConnectHandler();
//        if ((getFpListThread == null) || (getFpListThread.getState() == Thread.State.TERMINATED))
//        {
//            getFpListThread = new BlueToothWrapper(mConnectHandler);
//            getFpListThread.setGetFPListWrapper(mContextHandle, 0);
//            getFpListThread.start();
//        }
//    }
//
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
//                        if (fpCount >= 3){
//                            hideAddFPCard();
//                        }else{
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
//                       //.getReturnValue()));
//                    break;
//
//                default:
//                    break;
//            }
//        }
//    }
//
//    /**
//     *从设备获取指纹后在recyclerView中展示出来
//     */
//    private void showFingerprintsInfo(int fpCount,MiddlewareInterface.FingerPrintID[] fpList){
//        for (int i = 0; i < fpCount; i++){
//            BluetoothFPVO bluetoothFPVO = new BluetoothFPVO();
//            bluetoothFPVO.setFingerprintIndex(fpList[i].data);
//            bluetoothFPVO.setFingerprintName(getString(R.string.eos_fingerprint) + String.valueOf(i+1));
//            fingerprints.add(bluetoothFPVO);
//        }
//
//        String jsonList = GsonUtils.objectToJson(fingerprints);
//        SPUtils.getInstance().put("fingerPrints", jsonList);
//
//        mAdapter = new BluetoothFPManageAdapter(fingerprints);
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager
//                .VERTICAL, false);
//        recyclerFpManage.setLayoutManager(layoutManager);
//        recyclerFpManage.setAdapter(mAdapter);
//
//        recyclerFpManage.addOnItemTouchListener(new OnItemClickListener() {
//            @Override
//            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
//                //每个指纹选项卡的点击事件
//                Bundle bundle = new Bundle();
//                BluetoothFPVO curFpVO = fingerprints.get(position);
//                bundle.putString("fpName", curFpVO.getFingerprintName());
//                bundle.putByteArray("fpIndex", curFpVO.getFingerprintIndex());
//
//                start(BluetoothManageFPFragment.newInstance(bundle));
//
//            }
//        });
//    }
//
//}
