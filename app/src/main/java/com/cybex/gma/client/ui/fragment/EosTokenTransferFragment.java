package com.cybex.gma.client.ui.fragment;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cybex.componentservice.config.BaseConst;
import com.cybex.componentservice.config.CacheConstants;
import com.cybex.componentservice.db.dao.MultiWalletEntityDao;
import com.cybex.componentservice.db.entity.EosWalletEntity;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.DeviceOperationManager;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.componentservice.utils.AlertUtil;
import com.cybex.componentservice.utils.PasswordValidateHelper;
import com.cybex.componentservice.utils.WookongConnectHelper;
import com.cybex.componentservice.utils.bluetooth.BlueToothWrapper;
import com.cybex.componentservice.utils.listener.DecimalInputTextWatcher;
import com.cybex.componentservice.widget.EditTextWithScrollView;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.ui.model.request.PushTransactionReqParams;
import com.cybex.gma.client.ui.model.vo.EosTokenVO;
import com.cybex.gma.client.ui.model.vo.TransferTransactionVO;
import com.cybex.gma.client.ui.presenter.EosTokenTransferPresenter;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.GsonUtils;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.siberiadante.customdialoglib.CustomDialog;
import com.siberiadante.customdialoglib.CustomFullDialog;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import me.jessyan.autosize.AutoSize;
import seed39.Seed39;

public class EosTokenTransferFragment extends XFragment<EosTokenTransferPresenter> {

    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.tv_title_receiver) TextView tvTitleReceiver;
    @BindView(R.id.et_receiver_account) EditText etReceiverAccount;
    @BindView(R.id.iv_transfer_account_clear) ImageView ivTransferAccountClear;
    @BindView(R.id.view_receiver_account) LinearLayout viewReceiverAccount;
    @BindView(R.id.tv_title_pay_account) TextView tvTitlePayAccount;
    @BindView(R.id.imv_wookong_logo) ImageView imvWookongLogo;
    @BindView(R.id.tv_pay_account) TextView tvPayAccount;
    @BindView(R.id.imv_arrow_change_account) ImageView imvArrowChangeAccount;
    @BindView(R.id.view_change_account) RelativeLayout viewChangeAccount;
    @BindView(R.id.tv_amount) TextView tvAmount;
    @BindView(R.id.tv_banlance) TextView tvBanlance;
    @BindView(R.id.et_amount) EditText etAmount;
    @BindView(R.id.iv_transfer_amount_clear) ImageView ivTransferAmountClear;
    @BindView(R.id.tv_note) TextView tvNote;
    @BindView(R.id.et_note) EditTextWithScrollView etNote;
    @BindView(R.id.iv_transfer_memo_clear) ImageView ivTransferMemoClear;
    @BindView(R.id.btn_transfer_nextStep) Button btnTransferNextStep;
    @BindView(R.id.root_scrollview_token) ScrollView rootScrollview;
    String deviceName;
    Unbinder unbinder;
    CustomFullDialog dialog = null;
    EosTokenVO curToken;
    String TAG = this.toString();
    ReentrantLock uiLock = new ReentrantLock();

    public void setChain_id(String chain_id) {
        this.chain_id = chain_id;
    }

    private String chain_id = "";

    public void setTransactionVO(TransferTransactionVO transactionVO) {
        this.transactionVO = transactionVO;
    }

    private TransferTransactionVO transactionVO;

    private String maxValue = "";
    private String currentEOSName = "";
    private String collectionAccount = "";
    private String amount = "";
    private String memo = "";
    private MultiWalletEntity curWallet;

    private CustomFullDialog pinDialog;
    private CustomFullDialog verifyDialog;
    private int verifyFpCount;

    public static EosTokenTransferFragment newInstance(Bundle args) {
        EosTokenTransferFragment fragment = new EosTokenTransferFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @OnClick({R.id.iv_transfer_account_clear, R.id.iv_transfer_amount_clear, R.id.iv_transfer_memo_clear})
    public void onClearClicked(View v) {
        switch (v.getId()) {
            case R.id.iv_transfer_account_clear:
                etReceiverAccount.setText("");
                break;
            case R.id.iv_transfer_amount_clear:
                etAmount.setText("");
                break;
            case R.id.iv_transfer_memo_clear:
                etNote.setText("");
                break;
        }
    }

    @OnTextChanged(value = R.id.et_amount, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onAmountChanged() {
        if (EmptyUtils.isNotEmpty(getAmount())) {
            ivTransferAmountClear.setVisibility(View.VISIBLE);
        } else {
            ivTransferAmountClear.setVisibility(View.GONE);
        }
    }

    @OnTextChanged(value = R.id.et_note, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onNoteChanged() {
        if (EmptyUtils.isNotEmpty(getNote())) {
            ivTransferMemoClear.setVisibility(View.VISIBLE);
        } else {
            ivTransferMemoClear.setVisibility(View.GONE);
        }
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
        setNavibarTitle(getString(R.string.eos_title_transfer), true, true);
    }

    @Override
    public boolean useEventBus() {
        return true;
    }

    @Override
    public void onDestroyView() {
        DeviceOperationManager.getInstance().clearCallback(this.toString());
        clearData();
        dissmisProgressDialog();
        unbinder.unbind();
        super.onDestroyView();
    }

    public void showInitData(String balance, String eosName, String tokenName) {
        currentEOSName = eosName;
        tvPayAccount.setText(currentEOSName);

        if (EmptyUtils.isEmpty(balance)) {
            return;
        }

        String[] spiltBalance = balance.split(" ");
        if (EmptyUtils.isEmpty(spiltBalance)) {
            return;
        }


        maxValue = spiltBalance[0].trim();
        //LoggerManager.d("maxValue", maxValue);


        if (curToken != null) {
            tvBanlance.setText(getString(R.string.eos_show_remain_balance)
                    + balance
                    + " " + tokenName);
            int accuracy = 0;

            if (curToken.getAccurancy() > 0) {
                accuracy = curToken.getAccurancy();
                etAmount.addTextChangedListener(new DecimalInputTextWatcher(etAmount, DecimalInputTextWatcher
                        .Type.decimal, accuracy, maxValue) {
                    @Override
                    public void afterTextChanged(Editable s) {
                        super.afterTextChanged(s);
                        validateButton();
                    }
                });
            } else {
                etAmount.addTextChangedListener(new DecimalInputTextWatcher(etAmount, DecimalInputTextWatcher
                        .Type.integer, maxValue.length() + 1, maxValue) {
                    @Override
                    public void afterTextChanged(Editable s) {
                        super.afterTextChanged(s);
                        validateButton();
                    }
                });
            }


        }

        etAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateAmountValue();
                    validateButton();
                    ivTransferAmountClear.setVisibility(View.GONE);
                } else {
                    if (EmptyUtils.isNotEmpty(getAmount())) {
                        ivTransferAmountClear.setVisibility(View.VISIBLE);
                    }
                }
            }
        });


    }

    private void validateAmountValue() {
        if (EmptyUtils.isNotEmpty(getAmount())) {
            String text = String.valueOf(etAmount.getText());
            LoggerManager.d("input text:" + text);
            DecimalFormat df = new DecimalFormat("00.0000");
            df.setMaximumFractionDigits(4);
            df.setMinimumFractionDigits(4);
            String data = df.format(Double.parseDouble(text));
            etAmount.setText(data);
        }
    }

    @Override
    public void initData(Bundle savedInstanceState) {

        verifyFpCount = 0;
        etReceiverAccount.setText("");
        etAmount.setText("");
        etNote.setText("");

        curWallet = DBManager.getInstance().getMultiWalletEntityDao().getCurrentMultiWalletEntity();
        deviceName = getP().getBluetoothDeviceName();

        OverScrollDecoratorHelper.setUpOverScroll(rootScrollview);

        if (getArguments() != null) {
            curToken = getArguments().getParcelable(ParamConstants.EOS_TOKENS);
            if (curToken != null) {
                String balance = curToken.getQuantity();

                MultiWalletEntityDao dao = DBManager.getInstance().getMultiWalletEntityDao();
                MultiWalletEntity entity = dao.getCurrentMultiWalletEntity();
                EosWalletEntity eosEntity = entity.getEosWalletEntities().get(0);

                currentEOSName = eosEntity.getCurrentEosName();
                String tokenSymbol = curToken.getTokenSymbol();
                showInitData(balance, currentEOSName, tokenSymbol);

                String curAccuracyStr = " ";
                int curAccuracy = curToken.getAccurancy();
                if (curAccuracy > 0) {
                    if (curAccuracy == 1) {
                        curAccuracyStr = "一";
                    } else if (curAccuracy == 2) {
                        curAccuracyStr = "二";
                    } else if (curAccuracy == 3) {
                        curAccuracyStr = "三";
                    } else if (curAccuracy == 4) {
                        curAccuracyStr = "四";
                    } else if (curAccuracy == 5) {
                        curAccuracyStr = "五";
                    } else if (curAccuracy == 6) {
                        curAccuracyStr = "六";
                    } else if (curAccuracy == 7) {
                        curAccuracyStr = "七";
                    } else if (curAccuracy == 8) {
                        curAccuracyStr = "八";
                    } else if (curAccuracy == 9) {
                        curAccuracyStr = "九";
                    } else if (curAccuracy == 10) {
                        curAccuracyStr = "十";
                    } else if (curAccuracy == 11) {
                        curAccuracyStr = "十一";
                    } else if (curAccuracy == 12) {
                        curAccuracyStr = "十二";
                    } else if (curAccuracy == 13) {
                        curAccuracyStr = "十三";
                    } else if (curAccuracy == 14) {
                        curAccuracyStr = "十四";
                    } else if (curAccuracy == 15) {
                        curAccuracyStr = "十五";
                    } else if (curAccuracy == 16) {
                        curAccuracyStr = "十六";
                    } else if (curAccuracy == 17) {
                        curAccuracyStr = "十七";
                    } else if (curAccuracy == 18) {
                        curAccuracyStr = "十八";
                    } else {
                        curAccuracyStr = "四";
                    }
                    etAmount.setHint(String.format(getString(R.string.eos_token_tip_transfer), curAccuracyStr));
                } else {
                    etAmount.setHint(getString(R.string.eos_token_tip_transfer_no_decimal));
                }

                etReceiverAccount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            etReceiverAccount.setTypeface(Typeface.DEFAULT_BOLD);
                            if (EmptyUtils.isEmpty(getCollectionAccount())) {
                                tvTitleReceiver.setText(getString(R.string.eos_title_receiver));
                                tvTitleReceiver.setTextColor(getResources().getColor(R.color.black_title));
                            } else {
                                ivTransferAccountClear.setVisibility(View.VISIBLE);
                            }
                        } else {
                            etReceiverAccount.setTypeface(Typeface.DEFAULT);
                            ivTransferAccountClear.setVisibility(View.GONE);
                            validateButton();
                            if (EmptyUtils.isEmpty(getCollectionAccount())) {
                                tvTitleReceiver.setText(getString(R.string.eos_title_receiver));
                                tvTitleReceiver.setTextColor(getResources().getColor(R.color.black_title));
                            }
                            if (!isAccountNameValid() && EmptyUtils.isNotEmpty(
                                    etReceiverAccount.getText().toString().trim())) {
                                //显示alert样式
                                tvTitleReceiver.setText(getString(R.string.eos_tip_account_name_err));
                                tvTitleReceiver.setTextColor(getResources().getColor(R.color.scarlet));
                            } else {
                                //显示默认样式
                                tvTitleReceiver.setText(getString(R.string.eos_title_receiver));
                                tvTitleReceiver.setTextColor(getResources().getColor(R.color.black_title));
                            }
                        }
                    }
                });

                etReceiverAccount.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (EmptyUtils.isNotEmpty(getCollectionAccount())) {
                            ivTransferAccountClear.setVisibility(View.VISIBLE);
                        } else {
                            ivTransferAccountClear.setVisibility(View.GONE);
                        }
                        validateButton();
                    }
                });

                etNote.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            etNote.setTypeface(Typeface.DEFAULT_BOLD);
                            if (EmptyUtils.isNotEmpty(getNote())) {
                                ivTransferMemoClear.setVisibility(View.VISIBLE);
                            }
                        } else {
                            etNote.setTypeface(Typeface.DEFAULT);
                            ivTransferMemoClear.setVisibility(View.GONE);
                        }
                    }
                });

                etAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            etAmount.setTypeface(Typeface.DEFAULT_BOLD);
                        } else {
                            etAmount.setTypeface(Typeface.DEFAULT);
                        }
                    }
                });
            }
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.eos_fragment_token_transfer;
    }

    @Override
    public EosTokenTransferPresenter newP() {
        return new EosTokenTransferPresenter();
    }

    private void validateButton() {
        String collectionAccount = String.valueOf(etReceiverAccount.getText());
        String amount = String.valueOf(etAmount.getText());

        if (!EmptyUtils.isEmpty(collectionAccount) && !EmptyUtils.isEmpty(amount) && isAccountNameValid()) {
            btnTransferNextStep.setEnabled(true);
            btnTransferNextStep.setBackground(getActivity().getDrawable(R.drawable.shape_corner_button));

        } else {
            btnTransferNextStep.setEnabled(false);
            btnTransferNextStep.setBackground(getActivity().getDrawable(R.drawable.shape_corner_button_unclickable));

        }

    }

    @OnClick({R.id.et_note})
    public void onClickEvent(EditText v) {
        switch (v.getId()) {
            case R.id.et_note:
                break;
            default:
                break;
        }
    }

    /**
     * 转账操作入口
     *
     * @param view
     */
    @OnClick({R.id.btn_transfer_nextStep})
    public void onClickSubmitTransfer(View view) {
        String toAccount = String.valueOf(etReceiverAccount.getText());
        if (toAccount.equals(currentEOSName)) {
            GemmaToastUtils.showShortToast(getResources().getString(R.string.eos_tip_cant_transfer_to_yourself));
            return;
        }

        validateAmountValue();
        showConfirmTransferDialog();
    }

    public boolean isAccountNameValid() {
        String eosUsername = etReceiverAccount.getText().toString().trim();
        String regEx = "^[a-z1-5.]{1,13}$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher((eosUsername));
        boolean res = matcher.matches();
        return res;
    }

    public String getCollectionAccount() {
        return etReceiverAccount.getText().toString().trim();
    }

    public String getAmount() {
        return etAmount.getText().toString().trim();
    }

    public String getNote() {
        return etNote.getText().toString().trim();
    }

    /**
     * 确认转账dialog
     */
    private void showConfirmTransferDialog() {
        int[] listenedItems = {R.id.btn_close, R.id.btn_transfer_nextStep};

        if (getActivity() != null) { AutoSize.autoConvertDensityOfGlobal(getActivity()); }

        CustomFullDialog dialog = new CustomFullDialog(getContext(),
                R.layout.eos_dialog_transfer_confirm, listenedItems, false, Gravity.BOTTOM);

        dialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
            @Override
            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {
                MultiWalletEntity curWallet = DBManager.getInstance()
                        .getMultiWalletEntityDao()
                        .getCurrentMultiWalletEntity();
                if (curWallet != null) {
                    int walletType = curWallet.getWalletType();
                    switch (view.getId()) {
                        case R.id.btn_close:
                            dialog.cancel();
                            break;
                        case R.id.btn_transfer_nextStep:
                            if (walletType == BaseConst.WALLET_TYPE_BLUETOOTH) {
                                //蓝牙钱包
                                int connect_status = DeviceOperationManager.getInstance().getDeviceConnectStatus
                                        (deviceName);

                                if (connect_status == CacheConstants.STATUS_BLUETOOTH_CONNCETED) {
                                    //蓝牙卡已连接
                                    getP().executeBluetoothTransferLogic(currentEOSName, getCollectionAccount(),
                                            getAmount() + " EOS", getNote());
                                } else {
                                    //蓝牙卡未连接
                                    connectBio();
                                }
                            } else {
                                //软钱包
                                showConfirmAuthoriDialog();
                            }
                            break;
                        default:
                            break;
                    }
                }

            }
        });
        dialog.show();

        TextView tv_payee = dialog.findViewById(R.id.tv_payee); //收款人
        TextView tv_amount = dialog.findViewById(R.id.tv_amount); //金额
        TextView tv_token_symbol = dialog.findViewById(R.id.tv_eos_symbol);
        TextView tv_note = dialog.findViewById(R.id.tv_note); //备注
        TextView tv_payment_account = dialog.findViewById(R.id.tv_payment_account);//付款账户

        collectionAccount = String.valueOf(etReceiverAccount.getText());
        if (curToken != null) {
            amount = String.valueOf(etAmount.getText()) + " " + curToken.getTokenSymbol();
        }

        if (EmptyUtils.isNotEmpty(collectionAccount)) {
            tv_payee.setText(collectionAccount);
        }

        if (EmptyUtils.isNotEmpty(amount)) {
            tv_amount.setText(amount.split(" ")[0]);
            tv_token_symbol.setText(" " + amount.split(" ")[1]);
        }

        if (EmptyUtils.isNotEmpty(currentEOSName)) {
            tv_payment_account.setText(currentEOSName);

            if (EmptyUtils.isEmpty(etNote.getText().toString().trim())) {
                memo = String.format(getString(R.string.eos_default_memo), currentEOSName);
            } else {
                memo = String.valueOf(etNote.getText());
            }
            tv_note.setText(memo);
            tv_note.setTextColor(getResources().getColor(R.color.black_context));
        }
    }

    /**
     * 显示确认授权dialog
     */
    private void showConfirmAuthoriDialog() {

        MultiWalletEntity wallet = DBManager.getInstance().getMultiWalletEntityDao()
                .getCurrentMultiWalletEntity();
        PasswordValidateHelper passwordValidateHelper = new PasswordValidateHelper(wallet, context);
        passwordValidateHelper.startValidatePassword(
                new PasswordValidateHelper.PasswordValidateCallback() {
                    @Override
                    public void onValidateSuccess(String password) {
                        //密码正确，执行转账逻辑
                        if (curToken != null) {

                            //软钱包EOS及Tokens转账
                            String saved_pri_key = wallet.getEosWalletEntities().get(0).getPrivateKey();
                            String privateKey = Seed39.keyDecrypt(password, saved_pri_key);
                            String tokenContract = curToken.getTokenName();
                            int accuracy = curToken.getAccurancy() > 0 ? curToken.getAccurancy() : 0;

                            String tokenSymbol = curToken.getTokenSymbol();
                            getP().executeTokenTransferLogic(
                                    wallet.getEosWalletEntities().get(0).getCurrentEosName(),
                                    collectionAccount, amount, memo,
                                    privateKey, tokenContract, tokenSymbol, accuracy);

                        }
                    }

                    @Override
                    public void onValidateFail(int failedCount) {
                        LoggerManager.d("validate fail");
                        showPasswordHintDialog();
                    }
                });

    }

    /**
     * 显示密码提示Dialog
     */
    private void showPasswordHintDialog() {
        int[] listenedItems = {R.id.tv_i_understand};
        if (getActivity() != null) { AutoSize.autoConvertDensityOfGlobal(getActivity()); }

        CustomDialog dialog = new CustomDialog(getContext(),
                R.layout.eos_dialog_password_hint, listenedItems, false, Gravity.CENTER);
        dialog.setOnDialogItemClickListener(new CustomDialog.OnCustomDialogItemClickListener() {

            @Override
            public void OnCustomDialogItemClick(CustomDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.tv_i_understand:
                        dialog.cancel();
                        showConfirmAuthoriDialog();
                        break;
                    default:
                        break;
                }
            }
        });
        dialog.show();

        TextView tv_pass_hint = dialog.findViewById(R.id.tv_password_hint_hint);
        MultiWalletEntity curWallet = DBManager.getInstance().getMultiWalletEntityDao().getCurrentMultiWalletEntity();
        if (EmptyUtils.isNotEmpty(curWallet)) {
            String passHint = curWallet.getPasswordTip();
            String showInfo = getString(R.string.eos_tip_password_hint) + " : " + passHint;
            tv_pass_hint.setText(showInfo);
        }
    }

    public void clearData() {
        if (dialog != null) {
            dialog.cancel();
        }

        etReceiverAccount.setOnFocusChangeListener(null);
        etAmount.setOnFocusChangeListener(null);
        etNote.setOnFocusChangeListener(null);

        etReceiverAccount.setText("");
        etAmount.setText("");
        etNote.setText("");

        //刷新数据
        //getP().requestBanlanceInfo();
    }

    public EosTokenVO getCurToken() {
        return curToken;
    }

    /**
     * 连接指定设备
     */
    private void connectBio() {

        if (curWallet != null) {
            WookongConnectHelper wookongConnectHelper = new WookongConnectHelper(
                    EosTokenTransferFragment.this.toString(), curWallet, getActivity());
            wookongConnectHelper.startConnectDevice(new WookongConnectHelper.ConnectWookongBioCallback() {
                @Override
                public void onConnectSuccess() {
                    if (dialog != null) { dialog.cancel(); }
                    getP().executeBluetoothTransferLogic(currentEOSName, getCollectionAccount(),
                            getAmount() + " EOS", getNote());
                }

                @Override
                public void onConnectFail() {
                    if (dialog != null) { dialog.cancel(); }
                    showConnectBioFailDialog();
                }
            });
        }

    }


    /**
     * EOS Transaction字符串序列化
     */
    public void startEosSerialization(String jsonTxStr) {

        DeviceOperationManager.getInstance().jsonSerialization(TAG, jsonTxStr, deviceName,
                new DeviceOperationManager.JsonSerilizeCallback() {
                    @Override
                    public void onSerilizeStart() {

                    }

                    @Override
                    public void onSerilizeSuccess(String serializeResult) {
                        //把序列化之后的数据做处理
                        byte[] builtStr = getP().buildSignStr(serializeResult, chain_id);
                        //把builtStr 送给设备签名
                        startEosSign(builtStr);
                    }

                    @Override
                    public void onSerilizeFail() {
                        AlertUtil.showLongUrgeAlert(getActivity(), " Transaction Serialization Fail");
                    }
                });
    }


    public void startVerifyProcess() {

        DeviceOperationManager.getInstance().getFPList(TAG, deviceName,
                new DeviceOperationManager.GetFPListCallback() {
                    @Override
                    public void onSuccess(BlueToothWrapper.GetFPListReturnValue fpListReturnValue) {
                        //判断是否有指纹
                        if (fpListReturnValue.getFPCount() > 0) {
                            //有设置指纹
                            DeviceOperationManager.getInstance().startVerifyFP(TAG, deviceName,
                                    new DeviceOperationManager.DeviceVerifyFPCallback() {
                                        @Override
                                        public void onVerifyStart() {
                                            showVerifyFPDialog();
                                        }

                                        @Override
                                        public void onVerifySuccess() {
                                            //EOS Sign
                                            verifyDialog.cancel();
                                        }

                                        @Override
                                        public void onVerifyFailed() {
                                            verifyDialog.cancel();
                                        }

                                        @Override
                                        public void onVerifyCancelled() {
                                            verifyDialog.cancel();
                                        }
                                    });
                        } else {
                            //没有设置指纹
                            //PIN 验证
                            showConfirmPINDialog();
                        }
                    }

                    @Override
                    public void onFail() {
                        AlertUtil.showShortUrgeAlert(getActivity(), "读取指纹信息失败");
                    }
                });
    }

    /**
     * EOS Tranasaction 签名
     */
    private void startEosSign(byte[] transaction) {
//        showProgressDialog("正在Bio上签名交易");
        uiLock.lock();
        DeviceOperationManager.getInstance().signEosTransaction(TAG, deviceName, uiLock, transaction,
                new DeviceOperationManager.EosSignCallback() {

                    @Override
                    public void onEosSignStart() {
                        //startVerifyProcess();
                    }

                    @Override
                    public void onEosSignSuccess(String strSignature) {

                        uiLock.unlock();
                        List<String> signatures = new ArrayList<>();
                        strSignature = strSignature.substring(0, strSignature.length() - 1);
                        signatures.add(strSignature);
                        transactionVO.setSignatures(signatures);

                        if (transactionVO != null) {
                            //构造PushTransaction 请求的json参数
                            PushTransactionReqParams reqParams = new PushTransactionReqParams();
                            reqParams.setTransaction(transactionVO);
                            reqParams.setSignatures(transactionVO.getSignatures());
                            reqParams.setCompression("none");

                            String buildTransactionJson = GsonUtils.
                                    objectToJson(reqParams);
                            LoggerManager.d("buildTransactionJson:" + buildTransactionJson);

                            //执行Push Transaction 最后一步操作
                            dissmisProgressDialog();
                            getP().pushTransaction(buildTransactionJson);
                        }
                    }

                    @Override
                    public void onEosSignFail() {
                        uiLock.unlock();
                        AlertUtil.showShortUrgeAlert(getActivity(), "签名EOS交易失败");
                    }
                });
    }


    /**
     * 显示通过蓝牙卡验证指纹dialog
     */
    private void showVerifyFPDialog() {
        if (verifyDialog != null) { return; }
        int[] listenedItems = {R.id.imv_back};
        verifyDialog = new CustomFullDialog(getActivity(),
                R.layout.eos_dialog_transfer_bluetooth_finger_sure, listenedItems, false, Gravity.BOTTOM);
        verifyDialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
            @Override
            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.imv_back:
                        verifyDialog.cancel();
                        break;
                    default:
                        break;
                }
            }
        });
        verifyDialog.show();
    }

    /**
     * 显示连接蓝牙卡失败dialog
     */
    private void showConnectBioFailDialog() {
        int[] listenedItems = {R.id.imc_cancel, R.id.btn_reconnect};
        dialog = new CustomFullDialog(getContext(),
                R.layout.eos_dialog_transfer_bluetooth_connect_failed, listenedItems, false, Gravity.BOTTOM);
        dialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
            @Override
            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.imc_cancel:
                        dialog.cancel();
                        break;
                    case R.id.btn_reconnect:
                        //重新连接
                        dialog.cancel();
                        connectBio();
                        break;
                    default:
                        break;
                }
            }
        });
        dialog.show();
    }


    /**
     * 显示输入PIN以确认dialog
     */
    private void showConfirmPINDialog() {
        int[] listenedItems = {R.id.imc_cancel, R.id.btn_confirm_authorization};
        pinDialog = new CustomFullDialog(getActivity(),
                R.layout.eos_dialog_bluetooth_input_transfer_password, listenedItems, false, false, Gravity.BOTTOM);

        pinDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

            }
        });
        pinDialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
            @Override
            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.imc_cancel:
                        dialog.cancel();
                        break;
                    case R.id.btn_confirm_authorization:
                        TextView tv_password = dialog.findViewById(R.id.et_password);
                        String password = tv_password.getText().toString();
                        DeviceOperationManager.getInstance().verifyPin(TAG, deviceName, password,
                                new DeviceOperationManager.VerifyPinCallback() {
                                    @Override
                                    public void onVerifySuccess() {

                                    }

                                    @Override
                                    public void onPinLocked() {

                                    }

                                    @Override
                                    public void onVerifyFail() {

                                    }

                                });

                        break;
                    default:
                        break;
                }
            }
        });
        pinDialog.show();

    }
}
