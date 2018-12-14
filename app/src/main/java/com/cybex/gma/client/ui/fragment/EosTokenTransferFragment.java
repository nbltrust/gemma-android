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
import com.cybex.componentservice.utils.listener.DecimalInputTextWatcher;
import com.cybex.componentservice.widget.CustomFullWithAlertDialog;
import com.cybex.componentservice.widget.EditTextWithScrollView;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.ui.model.request.PushTransactionReqParams;
import com.cybex.gma.client.ui.model.vo.EosTokenVO;
import com.cybex.gma.client.ui.model.vo.TransferTransactionVO;
import com.cybex.gma.client.ui.presenter.EosTokenTransferPresenter;
import com.extropies.common.MiddlewareInterface;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.GsonUtils;
import com.hxlx.core.lib.utils.KeyboardUtils;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.siberiadante.customdialoglib.CustomDialog;
import com.siberiadante.customdialoglib.CustomFullDialog;
import com.tapadoo.alerter.Alerter;

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
    boolean isAbort = false;//abort是否在执行中
    private String TAG = this.toString();
    private TransferTransactionVO transactionVO;
    private String chain_id = "";
    private String maxValue = "";
    private String currentEOSName = "";
    private String collectionAccount = "";
    private String amount = "";
    private String memo = "";
    private MultiWalletEntity curWallet;
    private CustomFullDialog pinDialog;
    private CustomFullWithAlertDialog verifyDialog;
    private CustomFullDialog powerPressDialog;
    private int verifyFpCount;

    public static EosTokenTransferFragment newInstance(Bundle args) {
        EosTokenTransferFragment fragment = new EosTokenTransferFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public String getTAG() {
        return TAG;
    }

    public void setTAG(String TAG) {
        this.TAG = TAG;
    }

    public void setChain_id(String chain_id) {
        this.chain_id = chain_id;
    }

    public void setTransactionVO(TransferTransactionVO transactionVO) {
        this.transactionVO = transactionVO;
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
        clearData();
        dissmisProgressDialog();
        if (getActivity() != null) { Alerter.clearCurrent(getActivity()); }
        unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        if (!isAbort){
            DeviceOperationManager.getInstance().abortSign(TAG, deviceName,
                    new DeviceOperationManager.AbortSignCallback() {
                        @Override
                        public void onAbortSignSuccess() {

                        }

                        @Override
                        public void onAbortSignFail() {

                        }
                    });
        }
        DeviceOperationManager.getInstance().clearCallback(TAG);
        super.onDestroy();
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
        if (curWallet != null) {
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
                                KeyboardUtils.showKeyBoard(getActivity(), etReceiverAccount);

                                if (EmptyUtils.isEmpty(getCollectionAccount())) {
                                    tvTitleReceiver.setText(getString(R.string.eos_title_receiver));
                                    if (isAdded()) {
                                        tvTitleReceiver.setTextColor(getResources().getColor(R.color.black_title));
                                    }
                                } else {
                                    ivTransferAccountClear.setVisibility(View.VISIBLE);
                                }
                            } else {
                                KeyboardUtils.hideSoftInput(getActivity(), etReceiverAccount);
                                //etReceiverAccount.setTypeface(Typeface.DEFAULT);
                                ivTransferAccountClear.setVisibility(View.GONE);
                                validateButton();
                                if (EmptyUtils.isEmpty(getCollectionAccount())) {
                                    tvTitleReceiver.setText(getString(R.string.eos_title_receiver));
                                    if (isAdded()) {
                                        tvTitleReceiver.setTextColor(getResources().getColor(R.color.black_title));
                                    }
                                }
                                if (!isAccountNameValid() && EmptyUtils.isNotEmpty(
                                        etReceiverAccount.getText().toString().trim())) {
                                    //显示alert样式
                                    tvTitleReceiver.setText(getString(R.string.eos_tip_account_name_err));
                                    if (isAdded()) {
                                        tvTitleReceiver.setTextColor(getResources().getColor(R.color.scarlet));
                                    }
                                } else {
                                    //显示默认样式
                                    tvTitleReceiver.setText(getString(R.string.eos_title_receiver));
                                    if (isAdded()) {
                                        tvTitleReceiver.setTextColor(getResources().getColor(R.color.black_title));
                                    }
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
                                KeyboardUtils.showKeyBoard(getActivity(), etNote);
                                if (EmptyUtils.isNotEmpty(getNote())) {
                                    ivTransferMemoClear.setVisibility(View.VISIBLE);
                                }
                            } else {
                                KeyboardUtils.hideSoftInput(getActivity(), etNote);
                                //etNote.setTypeface(Typeface.DEFAULT);
                                ivTransferMemoClear.setVisibility(View.GONE);
                            }
                        }
                    });

                    if (curWallet.getWalletType() == MultiWalletEntity.WALLET_TYPE_HARDWARE) {
                        etNote.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                if (getActivity() != null && !getP().isBioMemoValid()) {

                                }
                            }
                        });

                    }

                    etAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (hasFocus) {
                                etAmount.setTypeface(Typeface.DEFAULT_BOLD);
                                KeyboardUtils.showKeyBoard(getActivity(), etAmount);
                            } else {
                                KeyboardUtils.hideSoftInput(getActivity(), etAmount);
                                //etAmount.setTypeface(Typeface.DEFAULT);
                            }
                        }
                    });
                }
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

        if (!EmptyUtils.isEmpty(collectionAccount)
                && !EmptyUtils.isEmpty(amount)
                && isAccountNameValid()
                && Float.valueOf(getAmount()) > 0) {
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
        if (curWallet != null) {
            if (curWallet.getWalletType() == MultiWalletEntity.WALLET_TYPE_HARDWARE) {
                //蓝牙钱包
                if (getP().isBioMemoValid()) {
                    String toAccount = String.valueOf(etReceiverAccount.getText());
                    if (toAccount.equals(currentEOSName)) {
                        if (isAdded()) {
                            GemmaToastUtils.showShortToast(getResources().getString(R.string
                                    .eos_tip_cant_transfer_to_yourself));
                        }
                        return;
                    }

                    validateAmountValue();
                    showConfirmTransferDialog();
                } else {
                    AlertUtil.showShortUrgeAlert(getActivity(), getString(R.string.tip_bio_memo_invalid));
                }
            } else {
                //软钱包
                String toAccount = String.valueOf(etReceiverAccount.getText());
                if (toAccount.equals(currentEOSName) && isAdded()) {
                    GemmaToastUtils.showShortToast(
                            getResources().getString(R.string.eos_tip_cant_transfer_to_yourself));
                    return;
                }

                validateAmountValue();
                showConfirmTransferDialog();
            }
        }
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
                                    if (curToken != null){
                                        int accuracy = curToken.getAccurancy() > 0 ? curToken.getAccurancy() : 0;
                                        getP().executeBluetoothTransferLogic(
                                                currentEOSName,
                                                getCollectionAccount(),
                                                getAmount(),
                                                memo,
                                                curToken.getTokenName(),
                                                curToken.getTokenSymbol(),
                                                accuracy);
                                    }
                                } else {
                                    //蓝牙卡未连接
                                    connectBio();
                                }
                            } else {
                                //软钱包
                                showConfirmAuthoriDialog();
                            }
                            dialog.cancel();
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
                if (curWallet.getWalletType() == MultiWalletEntity.WALLET_TYPE_HARDWARE) {
                    memo = "http://wooko.ng";
                } else {
                    memo = String.format(getString(R.string.eos_default_memo), currentEOSName);
                }

            } else {
                memo = String.valueOf(etNote.getText());
            }
            tv_note.setText(memo);
            if (isAdded()) { tv_note.setTextColor(getResources().getColor(R.color.black_context)); }
        }
    }

    /**
     * 显示确认授权dialog
     */
    private void showConfirmAuthoriDialog() {

        MultiWalletEntity wallet = DBManager.getInstance().getMultiWalletEntityDao()
                .getCurrentMultiWalletEntity();
        PasswordValidateHelper passwordValidateHelper = new PasswordValidateHelper(wallet, context);
        passwordValidateHelper.setIsLastStep(true);
        passwordValidateHelper.setDialogCallback(new PasswordValidateHelper.PasswordDialogCallback() {
            @Override
            public void onDialogCancel() {
                showConfirmTransferDialog();
            }
        });
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
//                        showPasswordHintDialog();
                    }
                });

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
        Alerter.hide();
        if (getActivity() != null) {
            Alerter.clearCurrent(getActivity());
        }

        //刷新数据
        //getP().requestBanlanceInfo();
    }

    public EosTokenVO getCurToken() {
        return curToken;
    }

    public void setCurToken(EosTokenVO curToken) {
        this.curToken = curToken;
    }

    /**
     * 连接指定设备
     */
    private void connectBio() {

        if (curWallet != null) {
            WookongConnectHelper wookongConnectHelper = new WookongConnectHelper(
                    TAG, curWallet, getActivity());
            wookongConnectHelper.startConnectDevice(new WookongConnectHelper.ConnectWookongBioCallback() {
                @Override
                public void onConnectSuccess() {
                    if (curToken != null){
                        if (dialog != null) { dialog.cancel();}
                        int accuracy = curToken.getAccurancy() > 0 ? curToken.getAccurancy() : 0;
                        getP().executeBluetoothTransferLogic(currentEOSName, getCollectionAccount(),
                                getAmount(), memo, curToken.getTokenName(), curToken.getTokenSymbol(), accuracy);
                    }
                }

                @Override
                public void onConnectFail() {
                    if (dialog != null) { dialog.cancel(); }
                    //showConnectBioFailDialog();
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


    /**
     * EOS Tranasaction 签名
     */
    private void startEosSign(byte[] transaction) {
        //先Set Tx
        DeviceOperationManager.getInstance().setTx(TAG, deviceName, transaction,
                new DeviceOperationManager.SetTxCallback() {
                    @Override
                    public void onSetTxStart() {

                    }

                    @Override
                    public void onSetTxSuccess() {
                        dissmisProgressDialog();
                        DeviceOperationManager.getInstance().getSignResult(TAG, deviceName,
                                MiddlewareInterface.PAEW_SIGN_AUTH_TYPE_FP,
                                new DeviceOperationManager.SetGetSignResultCallback() {
                                    @Override
                                    public void onGetSignResultStart() {

                                    }

                                    @Override
                                    public void onGetSignResultSuccess(String strSignature) {

                                        //LoggerManager.d("Sign Success str Sig = " + strSignature);
                                        buildTransaction(strSignature);
                                        if (verifyDialog != null && verifyDialog.isShowing()) {
                                            verifyDialog.cancel();
                                        }
                                    }

                                    @Override
                                    public void onGetSignResultFail(int status) {

                                        if (verifyDialog != null && verifyDialog.isShowing()) {
                                            verifyDialog.cancel();
                                        }

                                        if (status == BaseConst.STATUS_NO_VERIFY_COUNT) {
                                            //没有指纹录入错误，调用PIN验证
                                            showConfirmPINDialog();
                                        } else {
                                            //其他错误
                                            AlertUtil.showShortUrgeAlert(getActivity(),
                                                    getString(R.string.tip_fp_sign_fail));
                                        }

                                    }

                                    @Override
                                    public void onGetSignResultUpdate(int statusCode) {

                                        int fpCount = 0;

                                        if (statusCode == MiddlewareInterface.PAEW_RET_DEV_STATE_INVALID) {
                                            if (verifyDialog != null && verifyDialog.isShowing()) {
                                                verifyDialog.dismiss();
                                            }

                                            if (powerPressDialog != null && powerPressDialog.isShowing()) {
                                                powerPressDialog.dismiss();
                                            }

                                        } else if (statusCode == MiddlewareInterface.PAEW_RET_DEV_WAITING) {
                                            if (verifyDialog == null || !verifyDialog.isShowing()) {
                                                showVerifyFPDialog();
                                            }
                                        } else if (statusCode == MiddlewareInterface.PAEW_RET_DEV_FP_COMMON_ERROR) {
                                            //单次指纹验证失败
                                            fpCount++;
                                            if (fpCount <= 3){
                                                verifyDialog.showShortUrgeAlert(getString(R.string.fp_error));
                                            }else {
                                                //切换验证方式至PIN
                                                DeviceOperationManager.getInstance().switchSignType(TAG, deviceName,
                                                        new DeviceOperationManager.SwitchSignCallback() {
                                                            @Override
                                                            public void onSwitchSignSuccess() {
                                                                if (verifyDialog != null && verifyDialog.isShowing()) {
                                                                    verifyDialog.dismiss();
                                                                }
                                                                showConfirmPINDialog();
                                                            }

                                                            @Override
                                                            public void onSwitchSignFail() {
                                                                if (verifyDialog != null && verifyDialog.isShowing()) {
                                                                    verifyDialog.dismiss();
                                                                }
                                                                GemmaToastUtils.showShortToast(getString(R.string.switch_sign_fail));
                                                            }
                                                        });
                                            }
                                        }
                                    }
                                }
                        );


                    }

                    @Override
                    public void onSetTxFail() {
                        dissmisProgressDialog();
                        GemmaToastUtils.showShortToast(getString(R.string.tip_set_tx_fail));
                    }
                });
    }

    /**
     * 构建PushTransaction需要的参数并执行PushTransaction
     * @param strSignature
     */
    public void buildTransaction(String strSignature) {
        showProgressDialog(getString(R.string.eos_tip_transfer_trade_ing));
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
            getP().pushTransaction(buildTransactionJson);
        }
    }


    /**
     * 显示通过蓝牙卡验证指纹dialog
     */
    private void showVerifyFPDialog() {
        if (verifyDialog != null && verifyDialog.isShowing()) {
            return;
        }

        int[] listenedItems = {R.id.imv_back};
        verifyDialog = new CustomFullWithAlertDialog(getActivity(),
                R.layout.eos_dialog_transfer_bluetooth_finger_sure, listenedItems, false, Gravity.BOTTOM);

        verifyDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (!isAbort){
                    DeviceOperationManager.getInstance().abortSign(TAG, deviceName,
                            new DeviceOperationManager.AbortSignCallback() {
                                @Override
                                public void onAbortSignSuccess() {
                                    isAbort = false;
                                }

                                @Override
                                public void onAbortSignFail() {
                                    isAbort = false;
                                }
                            });
                }
                isAbort = true;
            }
        });



        verifyDialog.setOnDialogItemClickListener(new CustomFullWithAlertDialog.OnCustomDialogItemClickListener() {

            @Override
            public void OnCustomDialogItemClick(CustomFullWithAlertDialog dialog, View view) {
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
     * 显示按键确认dialog
     */
    private void showPowerConfirmTransferDialog() {
        if (powerPressDialog != null && powerPressDialog.isShowing()) {
            return;
        }
        int[] listenedItems = {R.id.imv_back};
        powerPressDialog = new CustomFullDialog(getActivity(),
                R.layout.dialog_bluetooth_transfer_power_confirm, listenedItems, false, Gravity.BOTTOM);

        powerPressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                //abort sign
                DeviceOperationManager.getInstance().abortSign(TAG, deviceName,
                        new DeviceOperationManager.AbortSignCallback() {
                            @Override
                            public void onAbortSignSuccess() {

                            }

                            @Override
                            public void onAbortSignFail() {

                            }
                        });
            }
        });


        powerPressDialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
            @Override
            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.imv_back:
                        powerPressDialog.cancel();

                        break;
                    default:
                        break;
                }
            }
        });
        powerPressDialog.show();
    }


    /**
     * 显示连接蓝牙卡失败dialog
     */
    private void showConnectBioFailDialog() {
        int[] listenedItems = {R.id.imv_back, R.id.btn_reconnect};
        dialog = new CustomFullDialog(getContext(),
                R.layout.eos_dialog_transfer_bluetooth_connect_failed, listenedItems, false, Gravity.BOTTOM);
        dialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
            @Override
            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.imv_back:
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
        if (getActivity() != null) {
            AutoSize.autoConvertDensityOfGlobal(getActivity());
            pinDialog = new CustomFullDialog(getActivity(),
                    R.layout.eos_dialog_bluetooth_input_transfer_password, listenedItems, false, false, Gravity.BOTTOM);

            pinDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    DeviceOperationManager.getInstance().abortSign(TAG, deviceName,
                            new DeviceOperationManager.AbortSignCallback() {
                                @Override
                                public void onAbortSignSuccess() {

                                }

                                @Override
                                public void onAbortSignFail() {

                                }
                            });
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
                            if (password.equals("")) {
                                GemmaToastUtils.showShortToast(getString(R.string.eos_tip_please_input_pass));
                                return;
                            }
                            showProgressDialog(getString(R.string.eos_tip_transfer_trade_ing));

                            DeviceOperationManager.getInstance().verifySignPin(TAG, deviceName, password,
                                    new DeviceOperationManager.VerifySignPinCallback() {
                                        @Override
                                        public void onVerifySuccess() {
                                            pinDialog.dismiss();
                                            DeviceOperationManager.getInstance().getSignResult(TAG, deviceName,
                                                    MiddlewareInterface.PAEW_SIGN_AUTH_TYPE_PIN,
                                                    new DeviceOperationManager.SetGetSignResultCallback() {
                                                        @Override
                                                        public void onGetSignResultStart() {
                                                            dissmisProgressDialog();
                                                            pinDialog.dismiss();
                                                        }

                                                        @Override
                                                        public void onGetSignResultSuccess(String strSignature) {
                                                            buildTransaction(strSignature);
                                                            powerPressDialog.dismiss();
                                                        }

                                                        @Override
                                                        public void onGetSignResultFail(int status) {
                                                            AlertUtil.showShortUrgeAlert(getActivity(), getString(R
                                                                    .string.tip_pin_verify_fail));
                                                            powerPressDialog.dismiss();
                                                        }

                                                        @Override
                                                        public void onGetSignResultUpdate(int statusCode) {
                                                            if (statusCode
                                                                    == MiddlewareInterface.PAEW_RET_DEV_STATE_INVALID) {
                                                                if (powerPressDialog != null
                                                                        && powerPressDialog.isShowing()) {
                                                                    powerPressDialog.dismiss();
                                                                }

                                                            } else if (statusCode
                                                                    == MiddlewareInterface.PAEW_RET_DEV_WAITING) {
                                                                if (powerPressDialog == null
                                                                        || !powerPressDialog.isShowing()) {
                                                                    showPowerConfirmTransferDialog();
                                                                }
                                                            }
                                                        }
                                                    });
                                        }

                                        @Override
                                        public void onVerifyFail() {
                                            dissmisProgressDialog();
                                            GemmaToastUtils.showLongToast(getString(R
                                                    .string.baseservice_pass_validate_ip_wrong_password));
                                        }

                                        @Override
                                        public void onVerifyOvertime() {
                                            dissmisProgressDialog();
                                            pinDialog.dismiss();
                                            if (getActivity() != null){
                                                AlertUtil.showLongUrgeAlert(getActivity(), getString(R.string.input_psw_overtime));
                                            }

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
}
