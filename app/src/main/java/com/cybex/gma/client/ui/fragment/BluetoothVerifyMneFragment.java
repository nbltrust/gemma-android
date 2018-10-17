package com.cybex.gma.client.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cybex.componentservice.db.entity.WalletEntity;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.CacheConstants;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.event.AccountRegisterEvent;
import com.cybex.gma.client.event.ValidateResultEvent;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.activity.BluetoothCreatEosAccountActivity;
import com.cybex.gma.client.ui.activity.BluetoothPairActivity;
import com.cybex.gma.client.ui.activity.BluetoothScanResultDialogActivity;
import com.cybex.gma.client.ui.activity.BluetoothSettingFPActivity;
import com.cybex.gma.client.ui.model.vo.BluetoothAccountInfoVO;
import com.cybex.gma.client.ui.presenter.BluetoothVerifyPresenter;
import com.cybex.gma.client.utils.CollectionUtils;
import com.cybex.gma.client.utils.ConvertUtils;
import com.cybex.gma.client.utils.TSnackbarUtil;
import com.cybex.gma.client.utils.bluetooth.BlueToothWrapper;
import com.cybex.gma.client.widget.LabelsView;
import com.extropies.common.CommonUtility;
import com.extropies.common.MiddlewareInterface;
import com.hxlx.core.lib.common.async.TaskManager;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.common.utils.AppManager;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.trycatch.mysnackbar.Prompt;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 验证助记词页面
 */

public class BluetoothVerifyMneFragment extends XFragment<BluetoothVerifyPresenter> {

    Unbinder unbinder;
    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.view_click_to_show_mne) LabelsView viewClickToShowMne;
    @BindView(R.id.view_show_mne) LabelsView viewShowMne;
    @BindView(R.id.view_root) LinearLayout viewRoot;

    private boolean isInit;//是否为初始状态，用来调整上方显示区域大小

    List<String> selectedLabels = new ArrayList<>();//被点选的Label
    List<String> unSelectedLabels = new ArrayList<>();

    private List<String> answerLabels = new ArrayList<>();

    BlueToothWrapper.GenSeedMnesReturnValue values = null;

    long contextHandle = 0;

    BlueToothWrapper getAddressThread;
    BlueToothWrapper getCheckCodeThread;
    MainHandler mHandler;
    final int[] deriveEOSPaths =
            {0, 0x8000002C, 0x800000c2, 0x80000000, 0x00000000, 0x00000000};

    private String public_key = "";
    private String public_key_hex = "";
    private String public_key_sign = "";
    private String SN = "";
    private String SN_sign = "";
    private Bundle bd;
    private BluetoothAccountInfoVO infoVo;

    public static BluetoothVerifyMneFragment newInstance(Bundle bd) {
        BluetoothVerifyMneFragment fragment = new BluetoothVerifyMneFragment();
        fragment.setArguments(bd);
        return fragment;
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(BluetoothVerifyMneFragment.this, rootView);
        setNavibarTitle(getResources().getString(R.string.eos_title_verify_mne), true, true);
    }


    @Override
    public void initData(Bundle savedInstanceState) {
        isInit = true;
        initAboveLabelView();
        initBelowLabelView();

        bd = getArguments();
        if (bd != null) {
            values = bd.getParcelable(ParamConstants.KEY_GEEN_SEED);
            contextHandle = bd.getLong(ParamConstants.CONTEXT_HANDLE);
            infoVo = bd.getParcelable(ParamConstants.KEY_BLUETOOTH_ACCOUNT_INFO);

            if (values != null) {
                String[] mnes = values.getStrMneWord();
                List<String> tempLabels = new ArrayList<>();
                if (EmptyUtils.isNotEmpty(mnes)) {
                    for (int i = 0; i < mnes.length; i++) {
                        String word = mnes[i];
                        tempLabels.add(word);
                    }

                    answerLabels.addAll(tempLabels);

                    //打乱顺序
                    //Collections.shuffle(tempLabels);


                    viewShowMne.setLabels(tempLabels);
                    unSelectedLabels.addAll(tempLabels);
                }

            }
        }


        //上方助记词显示区域
        viewClickToShowMne.setOnLabelClickListener(new LabelsView.OnLabelClickListener() {
            @Override
            public void onLabelClick(TextView label, Object data, int position) {
                selectedLabels.remove(label.getText().toString());
                unSelectedLabels.add(label.getText().toString());
                updateAboveLabelView(selectedLabels);
                updateBottomLabelView(unSelectedLabels);
            }
        });
        //下方助记词显示区域
        viewShowMne.setOnLabelClickListener(new LabelsView.OnLabelClickListener() {
            @Override
            public void onLabelClick(TextView label, Object data, int position) {
                if (isInit) {
                    setAboveLabelView();
                }
                selectedLabels.add(label.getText().toString());
                unSelectedLabels.remove(label.getText().toString());
                updateAboveLabelView(selectedLabels);
                updateBottomLabelView(unSelectedLabels);
                isInit = false;

                doValidateResult();
            }
        });
    }


    /**
     * 执行验证助记词
     */
    private void doValidateResult() {
        if (EmptyUtils.isEmpty(unSelectedLabels)) {
            if (EmptyUtils.isNotEmpty(selectedLabels) && EmptyUtils.isNotEmpty(answerLabels)) {
                if (CollectionUtils.isEqualCollection(selectedLabels, answerLabels)) {
                    //app验证通过，发送给硬件验证，并且获取硬件返回的公钥
                    if (values != null) {
                        int[] checkedIndex = values.getCheckIndex();
                        int[] checkIndexCount = values.getCheckIndexCount();
                        if (EmptyUtils.isNotEmpty(checkedIndex)) {
                            StringBuffer sb = new StringBuffer();
                            for (int m = 0; m < checkIndexCount[0]; m++) {
                                String label = answerLabels.get(checkedIndex[m]);
                                sb.append(label);
                                if (m != (checkIndexCount[0] - 1)) {
                                    sb.append(" ");
                                }
                            }

                            TaskManager.runOnUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    String strDestMnes = sb.toString();
                                    int status = MiddlewareInterface.generateSeed_CheckMnes(contextHandle, 0,
                                            strDestMnes);

                                    if (status == MiddlewareInterface.PAEW_RET_SUCCESS) {
                                        LoggerManager.d("mne validate success...");


                                        TSnackbarUtil.showTip(viewRoot, getString(R.string.eos_mne_validate_success),
                                                Prompt.SUCCESS);

                                        doGetAddressLogic();

                                    } else if (status == -2147483642) {
                                        LoggerManager.d("device disconnected...");

                                        TSnackbarUtil.showTip(viewRoot, getString(R.string.eos_mne_validate_warn),
                                                Prompt.WARNING);

                                    } else {
                                        LoggerManager.d("mne validate error...");
                                        TSnackbarUtil.showTip(viewRoot, getString(R.string.eos_mne_validate_failed),
                                                Prompt.ERROR);
                                    }
                                }
                            });


                        }
                    }
                } else {
                    TSnackbarUtil.showTip(viewRoot, getString(R.string.eos_mne_validate_failed),
                            Prompt.ERROR);
                }
            }
        }

    }


    /**
     * 设置上方LabelView初始样式
     */
    public void initAboveLabelView() {
        viewClickToShowMne.setLabelBackgroundDrawable(getResources().getDrawable(R.drawable.shape_corner));
        viewClickToShowMne.setWordMargin(30);
        viewClickToShowMne.setLineMargin(40);
        viewClickToShowMne.setSelectType(LabelsView.SelectType.SINGLE);
        viewClickToShowMne.setLabelTextSize(42);
        viewClickToShowMne.setLabelTextColor(getResources().getColor(R.color.whiteTwo));
        viewClickToShowMne.setLabelTextPadding(40, 20, 40, 20);
    }

    /**
     * 设置上方LabelView正常显示样式
     */
    public void setAboveLabelView() {
        viewClickToShowMne.setLabelBackgroundDrawable(getResources().getDrawable(R.drawable.shape_corner_text));
        viewClickToShowMne.setWordMargin(20);
        viewClickToShowMne.setLineMargin(20);
        viewClickToShowMne.setSelectType(LabelsView.SelectType.SINGLE);
        viewClickToShowMne.setLabelTextSize(42);
        viewClickToShowMne.setLabelTextColor(Color.parseColor("#4169d1"));
        viewClickToShowMne.setLabelTextPadding(40, 20, 40, 20);
    }

    /**
     * 设置下方LabelView样式
     */
    public void initBelowLabelView() {
        viewShowMne.setLabelBackgroundDrawable(getResources().getDrawable(R.drawable.shape_corner_text_clicked));
        viewShowMne.setWordMargin(20);
        viewShowMne.setLineMargin(20);
        viewShowMne.setSelectType(LabelsView.SelectType.SINGLE);
        viewShowMne.setLabelTextSize(42);
        viewShowMne.setLabelTextColor(Color.WHITE);
        viewShowMne.setLabelTextPadding(40, 20, 40, 20);
    }

    /**
     * 更新上方LabelView的显示
     *
     * @param data
     */
    public void updateAboveLabelView(List<String> data) {
        viewClickToShowMne.setLabels(data);
    }

    /**
     * 更新下方LabelView的显示
     *
     * @param data
     */
    public void updateBottomLabelView(List<String> data) {
        viewShowMne.setLabels(data);
    }

    @Override
    public int getLayoutId() {
        return R.layout.eos_fragment_bluetooth_verify_mne;
    }

    @Override
    public BluetoothVerifyPresenter newP() {
        return new BluetoothVerifyPresenter();
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
     * 获取秘钥相关逻辑
     */
    private void doGetAddressLogic() {
        showProgressDialog(getString(R.string.eos_do_get_init_info));
        mHandler = new MainHandler();

        if ((getAddressThread == null) || (getAddressThread.getState() == Thread.State.TERMINATED)) {
            getAddressThread = new BlueToothWrapper(mHandler);
            getAddressThread.setGetAddressWrapper(contextHandle, 0, MiddlewareInterface.PAEW_COIN_TYPE_EOS,
                    deriveEOSPaths);
            getAddressThread.start();
        }
    }


    /**
     * 获取
     */
    private void doGetCheckcodeLogic() {
        if ((getCheckCodeThread == null) || (getCheckCodeThread.getState() == Thread.State.TERMINATED)) {
            getCheckCodeThread = new BlueToothWrapper(new MainHandler());
            getCheckCodeThread.setGetCheckCodeWrapper(contextHandle, 0);
            getCheckCodeThread.start();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceivePollevent(AccountRegisterEvent event) {
        if (EmptyUtils.isNotEmpty(event)) {
            getP().doAccountRegisterRequest(infoVo.getAccountName(),
                    SN, SN_sign, public_key, public_key_hex, public_key_sign, infoVo.getPassword(),
                    infoVo.getPasswordTip(), bd);
        }
    }

    /**
     * 时间戳比较验证状态判断
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onCreateStatusReceieved(ValidateResultEvent event){
        if (event != null){
            dissmisProgressDialog();
            if(event.isSuccess()){
                //创建成功
                //跳转到主页面
                AppManager.getAppManager().finishAllActivity();
                UISkipMananger.launchHome(getActivity());
            }else {
                //创建失败,删除当前钱包
                WalletEntity curWallet = DBManager.getInstance().getWalletEntityDao().getCurrentWalletEntity();
                DBManager.getInstance().getWalletEntityDao().deleteEntity(curWallet);
                List<WalletEntity> walletEntityList = DBManager.getInstance().getWalletEntityDao()
                        .getWalletEntityList();
                //更新列表中的第一个钱包为当前钱包并跳转
                WalletEntity newCurWallet = walletEntityList.get(0);
                newCurWallet.setIsCurrentWallet(CacheConstants.IS_CURRENT_WALLET);
                DBManager.getInstance().getWalletEntityDao().saveOrUpateEntity(newCurWallet);
                AppManager.getAppManager().finishActivity(BluetoothPairActivity.class);
                AppManager.getAppManager().finishActivity(BluetoothSettingFPActivity.class);
                AppManager.getAppManager().finishActivity(BluetoothScanResultDialogActivity.class);
                AppManager.getAppManager().finishActivity(BluetoothCreatEosAccountActivity.class);

                UISkipMananger.launchHome(getActivity());
            }
        }
    }

    @Override
    public boolean useEventBus() {
        return true;
    }

    class MainHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BlueToothWrapper.MSG_GET_ADDRESS_FINISH:

                    BlueToothWrapper.GetAddressReturnValue returnValue = (BlueToothWrapper.GetAddressReturnValue) msg.obj;
                    if (returnValue.getReturnValue() == MiddlewareInterface.PAEW_RET_SUCCESS) {
                        if (returnValue.getCoinType() == MiddlewareInterface.PAEW_COIN_TYPE_EOS) {
                            String eosAddress = returnValue.getAddress();
                            if (EmptyUtils.isNotEmpty(eosAddress)) {
                                String[] addressSpilt = eosAddress.split("####");
                                if (EmptyUtils.isNotEmpty(addressSpilt)) {
                                    public_key = addressSpilt[0];
                                    public_key_sign = addressSpilt[1];

                                    public_key_hex = ConvertUtils.str2HexStr(public_key);

                                    LoggerManager.d("public_key: " + public_key);
                                    LoggerManager.d("public_key_hex: " + public_key_hex);
                                    LoggerManager.d("publick_key_sign: " + public_key_sign);

                                    doGetCheckcodeLogic();
                                }
                            }

                        }
                    } else {
                        dissmisProgressDialog();
                    }
                    break;
                case BlueToothWrapper.MSG_GET_CHECK_CODE_FINISH:
                    dissmisProgressDialog();
                    BlueToothWrapper.GetCheckCodeReturnValue getCheckCodeReturnValue = (BlueToothWrapper.GetCheckCodeReturnValue) msg.obj;
                    if (getCheckCodeReturnValue.getReturnValue() == MiddlewareInterface.PAEW_RET_SUCCESS) {
                        byte[] checkedcode = getCheckCodeReturnValue.getCheckCode();
                        byte[] snbyte = ConvertUtils.subByte(checkedcode, 0, 16);
//                         byte[] snSignByte = ConvertUtils.subByte(checkedcode,17,64);

                        SN = CommonUtility.byte2hex(snbyte);
                        SN_sign = CommonUtility.byte2hex(checkedcode);
                        SN_sign = SN_sign.substring(32);
                        LoggerManager.d("SN: " + SN);
                        LoggerManager.d("SN_sign: " + SN_sign);

                        if (infoVo != null) {
                            EventBusProvider.post(new AccountRegisterEvent());
                        }

                    }
                    break;
                case BlueToothWrapper.MSG_DEVICE_DISCONNECTED:
                    dissmisProgressDialog();
                    TSnackbarUtil.showTip(viewRoot, getString(R.string.eos_mne_validate_warn),
                            Prompt.WARNING);
                    break;
                default:
                    break;
            }

        }
    }
}
