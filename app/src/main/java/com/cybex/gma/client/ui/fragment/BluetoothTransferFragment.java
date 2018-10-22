package com.cybex.gma.client.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.cybex.componentservice.db.dao.WalletEntityDao;
import com.cybex.componentservice.db.entity.WalletEntity;
import com.cybex.gma.client.R;
import com.cybex.componentservice.config.CacheConstants;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.event.ChangeAccountEvent;
import com.cybex.gma.client.event.ContextHandleEvent;
import com.cybex.gma.client.event.TabSelectedEvent;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.gma.client.ui.JNIUtil;
import com.cybex.gma.client.ui.model.request.PushTransactionReqParams;
import com.cybex.gma.client.ui.model.vo.TransferTransactionVO;
import com.cybex.gma.client.ui.presenter.BluetoothTransferPresenter;
import com.cybex.gma.client.utils.bluetooth.BlueToothWrapper;
import com.cybex.gma.client.utils.listener.DecimalInputTextWatcher;
import com.extropies.common.CommonUtility;
import com.extropies.common.MiddlewareInterface;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.GsonUtils;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.siberiadante.customdialoglib.CustomFullDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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

/**
 * 蓝牙卡硬件钱包转账
 *
 * Created by wanglin on 2018/9/20.
 */
public class BluetoothTransferFragment extends XFragment<BluetoothTransferPresenter> {

    protected static ReentrantLock m_uiLock;

    public TransferTransactionVO getVo() {
        return vo;
    }

    public void setVo(TransferTransactionVO vo) {
        this.vo = vo;
    }

    private TransferTransactionVO vo;
    @BindView(R.id.icon_receiver)
    TextView iconReceiver;
    @BindView(R.id.view_divider)
    View viewDivider;
    @BindView(R.id.et_collection_account)
    EditText etCollectionAccount; //收款账户
    @BindView(R.id.tv_collection_account)
    TextView tvCollectionAmount;
    @BindView(R.id.tv_pay_account)
    TextView tvPayAccount; //付款账户
    @BindView(R.id.tv_banlance)
    TextView tvBanlance; //账户余额
    @BindView(R.id.et_amount)
    EditText etAmount; //支付金额
    @BindView(R.id.et_note)
    EditText etNote;  //备注
    @BindView(R.id.btn_transfer_nextStep)
    Button btnTransfer; //确认转账
    @BindView(R.id.root_scrollview)
    ScrollView rootScrollview;
    Unbinder unbinder;
    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.iv_transfer_account_clear) ImageView ivTransferAccountClear;
    @BindView(R.id.tv_title_pay_account) TextView tvTitlePayAccount;
    @BindView(R.id.tv_amount) TextView tvAmount;
    @BindView(R.id.iv_transfer_amount_clear) ImageView ivTransferAmountClear;
    @BindView(R.id.tv_note) TextView tvNote;
    @BindView(R.id.iv_transfer_memo_clear) ImageView ivTransferMemoClear;
    @BindView(R.id.imv_wookong_logo) ImageView imvWookongLogo;
    CustomFullDialog dialog = null;
    SerializeHandler mSerializeHandler;
    SignHandler mSignHandler;
    private String maxValue = "";
    private String currentEOSName = "";
    private String collectionAccount = "";
    private String amount = "";
    private String memo = "";
    private String chain_id = "";
    private String signArgStr = "";
    private static final String VALUE_COMPRESSION = "none";
    private long mContextHandle;
    private int mDevIndex = 0;
    private BlueToothWrapper serializedThread;
    private BlueToothWrapper signThread;
    private BlueToothWrapper connectThread;

    public static int m_authTypeResult = 0;
    public static int m_authTypeChoiceIndex = 0; //-1 means cancel
    public static byte m_authType;
    public static int m_getPINResult;
    public static String m_getPINString;
    private static final int m_minPINLen = 6;
    private static final int m_maxPINLen = 16;

    private static final String m_strDefaultPIN = "12345678";
    private static final String m_strDefaultNewPIN = "88888888";
    AlertDialog dlg = null;



    public static BluetoothTransferFragment newInstance(Bundle args) {
        BluetoothTransferFragment fragment = new BluetoothTransferFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public String getChain_id() {
        return chain_id;
    }

    public void setChain_id(String chain_id) {
        this.chain_id = chain_id;
    }

    @OnClick({R.id.iv_transfer_account_clear, R.id.iv_transfer_amount_clear, R.id.iv_transfer_memo_clear})
    public void onClearClicked(View v) {
        switch (v.getId()) {
            case R.id.iv_transfer_account_clear:
                etCollectionAccount.setText("");
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
        setNavibarTitle(getString(R.string.eos_title_transfer), false);
    }

    @Override
    public boolean useEventBus() {
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void showInitData(String banlance, String eosName) {
        currentEOSName = eosName;
        tvPayAccount.setText(currentEOSName);

        if (EmptyUtils.isEmpty(banlance)) {
            return;
        }

        String[] spiltBanlance = banlance.split(" ");
        if (EmptyUtils.isEmpty(spiltBanlance)) {
            return;
        }

        maxValue = spiltBanlance[0].trim();
        LoggerManager.d("maxValue", maxValue);
        tvBanlance.setText(getString(R.string.eos_show_remain_balance) + banlance);

        etAmount.addTextChangedListener(new DecimalInputTextWatcher(etAmount, DecimalInputTextWatcher
                .Type.decimal, 4, maxValue) {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                validateButton();
            }
        });

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

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onChangeAccountEvent(ChangeAccountEvent event) {
        if (EmptyUtils.isNotEmpty(event)) {
            LoggerManager.d("---changeAccount event---");
            getP().requestBanlanceInfo();
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onContextHandleRecieved(ContextHandleEvent event){
        mContextHandle = event.getContextHanle();
    }


    @Override
    public void initData(Bundle savedInstanceState) {
        //mContextHandle = getArguments().getLong("contextHandle");
        //LoggerManager.d("contextHandle at BluetoothTransferFragment", mContextHandle);
        vo = new TransferTransactionVO();
        m_uiLock = new ReentrantLock();
        m_uiLock.lock();
        WalletEntityDao dao = DBManager.getInstance().getWalletEntityDao();
        WalletEntity entity = dao.getCurrentWalletEntity();
        if (entity != null && getWalletType() == CacheConstants.WALLET_TYPE_BLUETOOTH) {
            imvWookongLogo.setVisibility(View.VISIBLE);
        } else {
            imvWookongLogo.setVisibility(View.GONE);
        }

        etCollectionAccount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (EmptyUtils.isEmpty(getCollectionAccount())) {
                        tvCollectionAmount.setText(getString(R.string.eos_title_receiver));
                        tvCollectionAmount.setTextColor(getResources().getColor(R.color.steel));
                    } else {
                        ivTransferAccountClear.setVisibility(View.VISIBLE);
                    }
                } else {
                    ivTransferAccountClear.setVisibility(View.GONE);
                    validateButton();
                    if (EmptyUtils.isEmpty(getCollectionAccount())) {
                        tvCollectionAmount.setText(getString(R.string.eos_title_receiver));
                        tvCollectionAmount.setTextColor(getResources().getColor(R.color.steel));
                    }
                    if (!isAccountNameValid() && EmptyUtils.isNotEmpty(
                            etCollectionAccount.getText().toString().trim())) {
                        //显示alert样式
                        tvCollectionAmount.setText(getString(R.string.eos_tip_account_name_err));
                        tvCollectionAmount.setTextColor(getResources().getColor(R.color.scarlet));
                    } else {
                        //显示默认样式
                        tvCollectionAmount.setText(getString(R.string.eos_title_receiver));
                        tvCollectionAmount.setTextColor(getResources().getColor(R.color.steel));
                    }
                }
            }
        });

        etCollectionAccount.addTextChangedListener(new TextWatcher() {
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
                    if (EmptyUtils.isNotEmpty(getNote())) {
                        ivTransferMemoClear.setVisibility(View.VISIBLE);
                    }
                } else {
                    ivTransferMemoClear.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    public int getLayoutId() {
        return R.layout.eos_fragment_transfer;
    }

    @Override
    public BluetoothTransferPresenter newP() {
        return new BluetoothTransferPresenter();
    }

    private void validateButton() {
        String collectionAccount = String.valueOf(etCollectionAccount.getText());
        String amount = String.valueOf(etAmount.getText());

        if (!EmptyUtils.isEmpty(collectionAccount) && !EmptyUtils.isEmpty(amount) && collectionAccount.length() ==
                ParamConstants.VALID_EOSNAME_LENGTH) {
            btnTransfer.setEnabled(true);

        } else {
            btnTransfer.setEnabled(false);

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onTabSelctedEvent(TabSelectedEvent event) {
        if (event != null && event.getPosition() == 1) {
            etCollectionAccount.setText("");
            etAmount.setText("");
            etNote.setText("");
            LoggerManager.d("tab transfer selected");
            getP().requestBanlanceInfo();
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

    @OnClick({R.id.btn_transfer_nextStep})
    public void onClickSubmitTransfer(View view) {
        String toAccount = String.valueOf(etCollectionAccount.getText());
        if (toAccount.equals(currentEOSName)) {
            GemmaToastUtils.showShortToast(getResources().getString(R.string.eos_tip_cant_transfer_to_yourself));
            return;
        }

        validateAmountValue();
        showConfirmTransferDialog();
    }

    public boolean isAccountNameValid() {
        String eosUsername = etCollectionAccount.getText().toString().trim();
        String regEx = "^[a-z1-5]{12}$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher((eosUsername));
        boolean res = matcher.matches();
        return res;
    }

    public String getCollectionAccount() {
        return etCollectionAccount.getText().toString().trim();
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

        CustomFullDialog dialog = new CustomFullDialog(getContext(),
                R.layout.eos_dialog_transfer_confirm, listenedItems, false, Gravity.BOTTOM);

        dialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
            @Override
            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {
                WalletEntity curWallet = DBManager.getInstance().getWalletEntityDao().getCurrentWalletEntity();
                if (curWallet != null){
                    int walletType = curWallet.getWalletType();
                    switch (view.getId()) {
                        case R.id.btn_close:
                            dialog.cancel();
                            break;
                        case R.id.btn_transfer_nextStep:
                            switch (walletType){
                                case CacheConstants.WALLET_TYPE_SOFT:
                                    showConfirmAuthoriDialog();
                                    break;
                                case CacheConstants.WALLET_TYPE_BLUETOOTH:
                                    //todo 测试用，之后要更改为弹出不同的dialog
                                    getP().executeBluetoothTransferLogic(currentEOSName, getCollectionAccount(),
                                            getAmount()+ " EOS", getNote());

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
        TextView tv_note = dialog.findViewById(R.id.tv_note); //备注
        TextView tv_payment_account = dialog.findViewById(R.id.tv_payment_account);//付款账户

        collectionAccount = String.valueOf(etCollectionAccount.getText());
        amount = String.valueOf(etAmount.getText()) + " EOS";

        if (EmptyUtils.isNotEmpty(collectionAccount)) {
            tv_payee.setText(collectionAccount);
        }

        if (EmptyUtils.isNotEmpty(amount)) {
            tv_amount.setText(amount);
        }

        if (EmptyUtils.isNotEmpty(currentEOSName)) {
            tv_payment_account.setText(currentEOSName);

            if (EmptyUtils.isEmpty(etNote.getText().toString().trim())) {
                memo = String.format(getString(R.string.eos_default_memo), currentEOSName);
            } else {
                memo = String.valueOf(etNote.getText());
            }
            tv_note.setText(memo);
            tv_note.setTextColor(getResources().getColor(R.color.darkSlateBlue));
        }
    }

    /**
     * 显示确认授权dialog
     */
    private void showConfirmAuthoriDialog() {
        int[] listenedItems = {R.id.imc_cancel, R.id.btn_confirm_authorization};
        dialog = new CustomFullDialog(getContext(),
                R.layout.eos_dialog_input_password_with_ic_mask, listenedItems, false, Gravity.BOTTOM);
        dialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
            @Override
            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.imc_cancel:
                        dialog.cancel();
                        showConfirmTransferDialog();
                        break;
                    case R.id.btn_confirm_authorization:
                        EditText etPasword = dialog.findViewById(R.id.et_password);
                        String pwd = String.valueOf(etPasword.getText());
                        if (EmptyUtils.isEmpty(pwd)) {
                            GemmaToastUtils.showLongToast(getResources().getString(R.string.eos_tip_please_input_pass));
                            return;
                        } else {
                            //获取当前账户的私钥
                            WalletEntity entity = DBManager.getInstance()
                                    .getWalletEntityDao()
                                    .getCurrentWalletEntity();
                            if (entity != null) {
                                String privateKey = JNIUtil.get_private_key(entity.getCypher(), pwd);

                                if ("wrong password".equals(privateKey)) {
                                    GemmaToastUtils.showShortToast(getResources().getString(R.string.eos_tip_wrong_password));
                                } else {
                                    //todo 密码正确，执行转账逻辑

                                    dialog.cancel();
                                }

                            } else {
                                GemmaToastUtils.showShortToast(getString(R.string.eos_transfer_error));
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        dialog.show();
        EditText etPasword = dialog.findViewById(R.id.et_password);
        etPasword.setHint(getString(R.string.eos_transfer_eosname_hint) + currentEOSName + getString(
                R.string.eos_transfer_eosname_hint_last));
    }

    public void clearData() {
        if (dialog != null) {
            dialog.cancel();
        }

        etCollectionAccount.setText("");
        etAmount.setText("");
        etNote.setText("");

        //刷新数据
        getP().requestBanlanceInfo();
    }

    /**
     * 返回当前钱包类型
     */
    public int getWalletType() {
        WalletEntity curWallet = DBManager.getInstance().getWalletEntityDao().getCurrentWalletEntity();
        if (curWallet != null) {
            switch (curWallet.getWalletType()) {
                case CacheConstants.WALLET_TYPE_BLUETOOTH:
                    //蓝牙钱包
                    return CacheConstants.WALLET_TYPE_BLUETOOTH;
                case CacheConstants.WALLET_TYPE_SOFT:
                    //软件钱包
                    return CacheConstants.WALLET_TYPE_SOFT;
            }
        }
        return CacheConstants.WALLET_TYPE_SOFT;
    }

    /**
     * 调用底层库进行序列化
     * @param jsonTxStr
     */
    public void startJsonSerialization(String jsonTxStr){
        mSerializeHandler = new SerializeHandler();
        if ((serializedThread == null) || (serializedThread.getState() == Thread.State.TERMINATED)) {
            serializedThread = new BlueToothWrapper(mSerializeHandler);
            serializedThread.setEOSTxSerializeWrapper(jsonTxStr);
            serializedThread.start();
        }

    }
    /**
     * 调用硬件进行签名
     */
    public void startSign( byte[] transaction){
        mSignHandler = new SignHandler();
        if ((signThread == null) || (signThread.getState() == Thread.State.TERMINATED)) {
            int[] derivePath = {0, 0x8000002C, 0x800000c2, 0x80000000, 0x00000000, 0x00000000};
            byte[] testTransaction = {(byte)0x74, (byte)0x09, (byte)0x70, (byte)0xd9, (byte)0xff, (byte)0x01, (byte)
                    0xb5, (byte)0x04, (byte)0x63, (byte)0x2f, (byte)0xed, (byte)0xe1, (byte)0xad, (byte)0xc3, (byte)0xdf, (byte)0xe5, (byte)0x59, (byte)0x90, (byte)0x41, (byte)0x5e, (byte)0x4f, (byte)0xde, (byte)0x01, (byte)0xe1, (byte)0xb8, (byte)0xf3, (byte)0x15, (byte)0xf8, (byte)0x13, (byte)0x6f, (byte)0x47, (byte)0x6c, (byte)0x14, (byte)0xc2, (byte)0x67, (byte)0x5b, (byte)0x01, (byte)0x24, (byte)0x5f, (byte)0x70, (byte)0x5d, (byte)0xd7, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0xa6, (byte)0x82, (byte)0x34, (byte)0x03, (byte)0xea, (byte)0x30, (byte)0x55, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x57, (byte)0x2d, (byte)0x3c, (byte)0xcd, (byte)0xcd, (byte)0x01, (byte)0x20, (byte)0x29, (byte)0xc2, (byte)0xca, (byte)0x55, (byte)0x7a, (byte)0x73, (byte)0x57, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xa8, (byte)0xed, (byte)0x32, (byte)0x32, (byte)0x21, (byte)0x20, (byte)0x29, (byte)0xc2, (byte)0xca, (byte)0x55, (byte)0x7a, (byte)0x73, (byte)0x57, (byte)0x90, (byte)0x55, (byte)0x8c, (byte)0x86, (byte)0x77, (byte)0x95, (byte)0x4c, (byte)0x3c, (byte)0x10, (byte)0x27, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x04, (byte)0x45, (byte)0x4f, (byte)0x53, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};
            signThread = new BlueToothWrapper(mSignHandler);
            signThread.setEOSSignWrapper(mContextHandle, mDevIndex, m_uiLock, derivePath, transaction);
            signThread.start();
        }
    }

    /**
     * 构建硬件能够识别的HEX字符串
     * 序列化结果前面加32字节chain_id，后面加32字节0
     * @param serializedStr
     */
    public byte[] buildSignStr(String serializedStr){
        //把数据转成HEX数组
        String prefix = chain_id.toUpperCase();//chain_id已经是HEX
        LoggerManager.d("prefix_hex", prefix);

        LoggerManager.d("serializedStr_hex", serializedStr);

        byte[] suffix_bytes = {
                (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};//32字节的0

        LoggerManager.d("suffix_hex", suffix_bytes);

        String hexString = prefix + serializedStr;

        byte[] hexBytes = hexToByte(hexString);
        LoggerManager.d("hexBytes", hexBytes);


        return byteMerger(hexBytes, suffix_bytes);
    }

    public static byte[] byteMerger(byte[] bt1, byte[] bt2){
        byte[] bt3 = new byte[bt1.length+bt2.length];
        System.arraycopy(bt1, 0, bt3, 0, bt1.length);
        System.arraycopy(bt2, 0, bt3, bt1.length, bt2.length);
        return bt3;
    }

    /**
     * hex转byte数组
     * @param hex
     * @return
     */
    public static byte[] hexToByte(String hex){
        int m = 0, n = 0;
        int byteLen = hex.length() / 2; // 每两个字符描述一个字节
        byte[] ret = new byte[byteLen];
        for (int i = 0; i < byteLen; i++) {
            m = i * 2 + 1;
            n = m + 1;
            int intVal = Integer.decode("0x" + hex.substring(i * 2, m) + hex.substring(m, n));
            ret[i] = Byte.valueOf((byte)intVal);
        }
        return ret;
    }


    /**
     * 处理序列化json和签名的handler
     */
    class SerializeHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BlueToothWrapper.MSG_EOS_SERIALIZE_START:

                    break;
                case BlueToothWrapper.MSG_EOS_SERIALIZE_FINISH:

                    BlueToothWrapper.EOSTxSerializeReturn returnValue = (BlueToothWrapper.EOSTxSerializeReturn)msg.obj;
                    if (returnValue.getReturnValue() == MiddlewareInterface.PAEW_RET_SUCCESS) {
                        //序列化成功
                        //LoggerManager.d("Serialize Result: " + CommonUtility.byte2hex(returnValue.getSerializeData()));
                        String serializedStr = CommonUtility.byte2hex(returnValue.getSerializeData());
                        byte[] builtStr = buildSignStr(serializedStr);
                        //把builtStr 送给设备签名
                        startSign( builtStr);
                    }

                    LoggerManager.d("Return Value: " + MiddlewareInterface.getReturnString(returnValue.getReturnValue()));

                    break;
                default:
                    break;
            }

        }
    }

    class SignHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BlueToothWrapper.MSG_EOS_SIGN_START:
                    //LoggerManager.d("MSG_EOS_SIGN_START");
                    break;
                case BlueToothWrapper.MSG_EOS_SIGN_FINISH:
                    LoggerManager.d("MSG_EOS_SIGN_FINISH");
                    BlueToothWrapper.SignReturnValue returnValue = (BlueToothWrapper.SignReturnValue)msg.obj;
                    LoggerManager.d("Return Value: " + MiddlewareInterface.getReturnString(returnValue
                            .getReturnValue()));

                    if (returnValue.getReturnValue() == MiddlewareInterface.PAEW_RET_SUCCESS) {
                        LoggerManager.d("Sign Success");

                        String strSignature = new String(returnValue.getSignature());
                        LoggerManager.d("\nSignature Value: " + strSignature);
                        List<String> signatures = new ArrayList<>();
                        signatures.add(strSignature);
                        vo.setSignatures(signatures);

                        if (vo != null) {
                            //构造PushTransaction 请求的json参数
                            PushTransactionReqParams reqParams = new PushTransactionReqParams();
                            reqParams.setTransaction(vo);
                            reqParams.setSignatures(vo.getSignatures());
                            reqParams.setCompression(VALUE_COMPRESSION);

                            String buildTransactionJson = GsonUtils.
                                    objectToJson(reqParams);
                            LoggerManager.d("buildTransactionJson:" + buildTransactionJson);

                            //执行Push Transaction 最后一步操作
                            getP().pushTransaction(buildTransactionJson);
                        }




                    }else {
                        LoggerManager.d("Sign Fail");
                    }



                    break;

                case BlueToothWrapper.MSG_GET_AUTH_TYPE:
                    //显示选择验证方式dialog
                    final String[] authTypeString = {"Sign by Finger Print", "Sign by PIN"};
                    final byte[] authTypes = {MiddlewareInterface.PAEW_SIGN_AUTH_TYPE_FP, MiddlewareInterface.PAEW_SIGN_AUTH_TYPE_PIN};
                    m_authTypeChoiceIndex = 0;

                    dlg = new AlertDialog.Builder(getActivity())
                            .setIcon(R.mipmap.app_icon)
                            .setTitle("Please Select Sign Type:")
                            .setSingleChoiceItems(authTypeString, m_authTypeChoiceIndex, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    m_authTypeChoiceIndex = which;
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    m_authTypeResult = MiddlewareInterface.PAEW_RET_DEV_OP_CANCEL;
                                    m_uiLock.unlock();
                                }
                            })
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    m_authType = authTypes[m_authTypeChoiceIndex];
                                    m_authTypeResult = MiddlewareInterface.PAEW_RET_SUCCESS;

                                    if (m_authType == MiddlewareInterface.PAEW_SIGN_AUTH_TYPE_PIN) {
                                        Message msg = mSignHandler.obtainMessage();
                                        msg.what = BlueToothWrapper.MSG_GET_USER_PIN;
                                        msg.sendToTarget();
                                    } else {
                                        m_uiLock.unlock();
                                    }
                                }
                            })
                            .setCancelable(false)
                            .create();
                    dlg.show();
                    break;
                case BlueToothWrapper.MSG_GET_USER_PIN:
                    View dlgView = getLayoutInflater().inflate(R.layout.eos_ui_dlg_verify_pin, null);
                    final EditText editPIN = dlgView.findViewById(R.id.edit_pin);
                    editPIN.setText(m_strDefaultPIN);
                    editPIN.selectAll();
                    dlg = new AlertDialog.Builder(getActivity())
                            .setIcon(R.mipmap.app_icon)
                            .setTitle("Please Input PIN:")
                            .setView(dlgView).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    m_getPINResult = MiddlewareInterface.PAEW_RET_DEV_OP_CANCEL;
                                    m_uiLock.unlock();
                                }
                            })
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int PINLength = editPIN.getText().toString().length();
                                    if ((PINLength < m_minPINLen) || (PINLength > m_maxPINLen)) {
                                        Toast toast = Toast.makeText(getContext(), "Please input PIN between " + m_minPINLen +
                                                " and " + m_maxPINLen, Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                        return;
                                    }

                                    m_getPINResult = MiddlewareInterface.PAEW_RET_SUCCESS;
                                    m_getPINString = editPIN.getText().toString();
                                    m_uiLock.unlock();
                                }
                            })
                            .setCancelable(false)
                            .create();
                    dlg.show();
                    break;
                default:
                    break;
            }

        }
    }
}
