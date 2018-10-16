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
import com.cybex.gma.client.config.CacheConstants;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.event.ChangeAccountEvent;
import com.cybex.gma.client.event.ContextHandleEvent;
import com.cybex.gma.client.event.DeviceInfoEvent;
import com.cybex.gma.client.event.TabSelectedEvent;
import com.cybex.gma.client.manager.DBManager;
import com.cybex.gma.client.manager.LoggerManager;
import com.cybex.gma.client.ui.JNIUtil;
import com.cybex.gma.client.ui.model.request.PushTransactionReqParams;
import com.cybex.gma.client.ui.model.vo.TransferTransactionVO;
import com.cybex.gma.client.ui.presenter.TransferPresenter;
import com.cybex.gma.client.utils.AlertUtil;
import com.cybex.gma.client.utils.bluetooth.BlueToothWrapper;
import com.cybex.gma.client.utils.listener.DecimalInputTextWatcher;
import com.extropies.common.CommonUtility;
import com.extropies.common.MiddlewareInterface;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.GsonUtils;
import com.hxlx.core.lib.utils.SPUtils;
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
 * 转账Fragment
 *
 * Created by wanglin on 2018/7/9.
 */
public class TransferFragment extends XFragment<TransferPresenter> {

    private static final int m_minPINLen = 6;
    private static final int m_maxPINLen = 16;
    private static final String m_strDefaultPIN = "12345678";
    private static final String m_strDefaultNewPIN = "88888888";
    public static int m_authTypeResult = 0;
    public static int m_authTypeChoiceIndex = 0; //-1 means cancel
    public static byte m_authType;
    public static int m_getPINResult;
    public static String m_getPINString;
    protected static ReentrantLock m_uiLock;
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
    @BindView(R.id.btn_transfer)
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
    ConnectHandler mConnectHandler;
    SignHandler mSignHandler;
    AlertDialog dlg = null;
    private TransferTransactionVO transactionVO;
    private String maxValue = "";
    private String currentEOSName = "";
    private String collectionAccount = "";
    private String amount = "";
    private String memo = "";
    private String chain_id = "";
    private String deviceName = "WOOKONG BIO####E7:D8:54:5C:33:82";
    private long mContextHandle = 0;
    private int mDevIndex = 0;
    private BlueToothWrapper serializedThread;
    private BlueToothWrapper signThread;
    private BlueToothWrapper connectThread;
    private BlueToothWrapper getAddressThread;

    public static TransferFragment newInstance() {
        Bundle args = new Bundle();
        TransferFragment fragment = new TransferFragment();
        fragment.setArguments(args);
        return fragment;
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
        setNavibarTitle(getString(R.string.title_transfer), false);
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
        tvBanlance.setText(getString(R.string.show_remain_balance) + banlance);

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
        m_uiLock = new ReentrantLock();
        m_uiLock.lock();
        WalletEntityDao dao = DBManager.getInstance().getWalletEntityDao();
        WalletEntity entity = dao.getCurrentWalletEntity();
        if (entity != null && getP().getWalletType() == CacheConstants.WALLET_TYPE_BLUETOOTH) {
            imvWookongLogo.setVisibility(View.VISIBLE);
        } else {
            imvWookongLogo.setVisibility(View.GONE);
        }

        etCollectionAccount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (EmptyUtils.isEmpty(getCollectionAccount())) {
                        tvCollectionAmount.setText(getString(R.string.receiver));
                        tvCollectionAmount.setTextColor(getResources().getColor(R.color.steel));
                    } else {
                        ivTransferAccountClear.setVisibility(View.VISIBLE);
                    }
                } else {
                    ivTransferAccountClear.setVisibility(View.GONE);
                    validateButton();
                    if (EmptyUtils.isEmpty(getCollectionAccount())) {
                        tvCollectionAmount.setText(getString(R.string.receiver));
                        tvCollectionAmount.setTextColor(getResources().getColor(R.color.steel));
                    }
                    if (!isAccountNameValid() && EmptyUtils.isNotEmpty(
                            etCollectionAccount.getText().toString().trim())) {
                        //显示alert样式
                        tvCollectionAmount.setText(getString(R.string.account_name_err));
                        tvCollectionAmount.setTextColor(getResources().getColor(R.color.scarlet));
                    } else {
                        //显示默认样式
                        tvCollectionAmount.setText(getString(R.string.receiver));
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
        return R.layout.fragment_transfer;
    }

    @Override
    public TransferPresenter newP() {
        return new TransferPresenter();
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

    @OnClick({R.id.btn_transfer})
    public void onClickSubmitTransfer(View view) {
        String toAccount = String.valueOf(etCollectionAccount.getText());
        if (toAccount.equals(currentEOSName)) {
            GemmaToastUtils.showShortToast(getResources().getString(R.string.cant_transfer_to_yourself));
            return;
        }

        validateAmountValue();
        showConfirmTransferDialog();
    }

    public void setTransactionVO(TransferTransactionVO transactionVO) {
        this.transactionVO = transactionVO;
    }

    public void setChain_id(String chain_id) {
        this.chain_id = chain_id;
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
        int[] listenedItems = {R.id.btn_close, R.id.btn_transfer};

        CustomFullDialog dialog = new CustomFullDialog(getContext(),
                R.layout.dialog_transfer_confirm, listenedItems, false, Gravity.BOTTOM);

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
                        case R.id.btn_transfer:
                            switch (walletType){
                                case CacheConstants.WALLET_TYPE_SOFT:
                                    //软钱包转账
                                    showConfirmAuthoriDialog();
                                    break;
                                case CacheConstants.WALLET_TYPE_BLUETOOTH:
                                    //蓝牙钱包转账
                                    int status = SPUtils.getInstance().getInt(CacheConstants.BIO_CONNECT_STATUS);
                                    if (status == CacheConstants.STATUS_BLUETOOTH_DISCONNCETED){
                                        //蓝牙卡未连接
                                        startConnect();
                                    }else {
                                        //蓝牙卡已连接
                                        getP().executeBluetoothTransferLogic(currentEOSName, getCollectionAccount(),
                                                getAmount()+ " EOS", getNote());
                                    }
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
                memo = String.format(getString(R.string.default_memo), currentEOSName);
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
                R.layout.dialog_input_password_with_ic_mask, listenedItems, false, Gravity.BOTTOM);
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
                            GemmaToastUtils.showLongToast(getResources().getString(R.string.please_input_pass));
                            return;
                        } else {
                            //获取当前账户的私钥
                            WalletEntity entity = DBManager.getInstance()
                                    .getWalletEntityDao()
                                    .getCurrentWalletEntity();
                            if (entity != null) {
                                String privateKey = JNIUtil.get_private_key(entity.getCypher(), pwd);

                                if ("wrong password".equals(privateKey)) {
                                    GemmaToastUtils.showShortToast(getResources().getString(R.string.wrong_password));
                                } else {
                                    //密码正确，执行转账逻辑
                                    getP().executeTransferLogic(entity.getCurrentEosName(),
                                            collectionAccount, amount, memo, privateKey);
                                    dialog.cancel();
                                }

                            } else {
                                GemmaToastUtils.showShortToast(getString(R.string.transfer_error));
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
        etPasword.setHint(getString(R.string.transfer_eosname_hint) + currentEOSName + getString(
                R.string.transfer_eosname_hint_last));
    }

    /**
     * 显示连接蓝牙dialog
     */
    private void showConnectBioDialog() {
        int[] listenedItems = {R.id.imc_cancel};
        dialog = new CustomFullDialog(getContext(),
                R.layout.dialog_transfer_bluetooth_connect_ing, listenedItems, false, Gravity.BOTTOM);
        dialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
            @Override
            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.imc_cancel:
                        dialog.cancel();
                        break;
                    default:
                        break;
                }
            }
        });
        dialog.show();
    }

    /**
     * 显示通过蓝牙卡验证指纹dialog
     */
    private void showVerifyFPDialog() {
        int[] listenedItems = {R.id.imv_back};
        dialog = new CustomFullDialog(getContext(),
                R.layout.dialog_transfer_bluetooth_finger_sure, listenedItems, false, Gravity.BOTTOM);
        dialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
            @Override
            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.imv_back:
                        dialog.cancel();
                        break;
                    default:
                        break;
                }
            }
        });
        dialog.show();
    }

    /**
     * 显示连接蓝牙卡失败dialog
     */
    private void showConnectBioFailDialog() {
        int[] listenedItems = {R.id.imc_cancel, R.id.btn_reconnect};
        dialog = new CustomFullDialog(getContext(),
                R.layout.dialog_transfer_bluetooth_connect_failed, listenedItems, false, Gravity.BOTTOM);
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
                        startConnect();
                        break;
                    default:
                        break;
                }
            }
        });
        dialog.show();
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
     * 连接蓝牙卡
     */
    public void startConnect(){
        if (mConnectHandler == null)mConnectHandler = new ConnectHandler();
        if ((connectThread == null) || (connectThread.getState() == Thread.State.TERMINATED)) {
            connectThread = new BlueToothWrapper(mConnectHandler);
            connectThread.setInitContextWithDevNameWrapper(getActivity(),
                    deviceName);
            connectThread.start();
        }
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
            int[] derivePath = {0, 0x8000002C, 0x800000c2, 0x80000000, 0x00000000, 0x00000000};//EOS币种的衍生路径
            byte[] testTransaction = {(byte)0x74, (byte)0x09, (byte)0x70, (byte)0xd9, (byte)0xff, (byte)0x01, (byte)
                    0xb5, (byte)0x04, (byte)0x63, (byte)0x2f, (byte)0xed, (byte)0xe1, (byte)0xad, (byte)0xc3, (byte)0xdf, (byte)0xe5, (byte)0x59, (byte)0x90, (byte)0x41, (byte)0x5e, (byte)0x4f, (byte)0xde, (byte)0x01, (byte)0xe1, (byte)0xb8, (byte)0xf3, (byte)0x15, (byte)0xf8, (byte)0x13, (byte)0x6f, (byte)0x47, (byte)0x6c, (byte)0x14, (byte)0xc2, (byte)0x67, (byte)0x5b, (byte)0x01, (byte)0x24, (byte)0x5f, (byte)0x70, (byte)0x5d, (byte)0xd7, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0xa6, (byte)0x82, (byte)0x34, (byte)0x03, (byte)0xea, (byte)0x30, (byte)0x55, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x57, (byte)0x2d, (byte)0x3c, (byte)0xcd, (byte)0xcd, (byte)0x01, (byte)0x20, (byte)0x29, (byte)0xc2, (byte)0xca, (byte)0x55, (byte)0x7a, (byte)0x73, (byte)0x57, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xa8, (byte)0xed, (byte)0x32, (byte)0x32, (byte)0x21, (byte)0x20, (byte)0x29, (byte)0xc2, (byte)0xca, (byte)0x55, (byte)0x7a, (byte)0x73, (byte)0x57, (byte)0x90, (byte)0x55, (byte)0x8c, (byte)0x86, (byte)0x77, (byte)0x95, (byte)0x4c, (byte)0x3c, (byte)0x10, (byte)0x27, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x04, (byte)0x45, (byte)0x4f, (byte)0x53, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};
            signThread = new BlueToothWrapper(mSignHandler);
            signThread.setEOSSignWrapper(mContextHandle, mDevIndex, m_uiLock, derivePath, transaction);
            signThread.start();
        }
    }

    /**
     * 获取EOS地址（公钥）
     */
    public void getEosAddress(){
        //showProgressDialog("Getting Device Information");
        if ((getAddressThread == null) || (getAddressThread.getState() == Thread.State.TERMINATED))
        {
            getAddressThread = new BlueToothWrapper(mConnectHandler);
            getAddressThread.setGetAddressWrapper(mContextHandle, 0, MiddlewareInterface.PAEW_COIN_TYPE_EOS,
                    CacheConstants.EOS_DERIVE_PATH);
            getAddressThread.start();
        }
    }

    /**
     * 处理序列化json的handler
     */
    class SerializeHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BlueToothWrapper.MSG_EOS_SERIALIZE_START:
                    showProgressDialog("Serializing...");
                    break;
                case BlueToothWrapper.MSG_EOS_SERIALIZE_FINISH:

                    BlueToothWrapper.EOSTxSerializeReturn returnValue = (BlueToothWrapper.EOSTxSerializeReturn)msg.obj;
                    if (returnValue.getReturnValue() == MiddlewareInterface.PAEW_RET_SUCCESS) {
                        //序列化成功
                        String serializedStr = CommonUtility.byte2hex(returnValue.getSerializeData());
                        //把序列化之后的数据做处理
                        byte[] builtStr = getP().buildSignStr(serializedStr, chain_id);
                        //把builtStr 送给设备签名
                        startSign(builtStr);
                    }
                    LoggerManager.d("Return Value: " + MiddlewareInterface.getReturnString(returnValue.getReturnValue()));
                    dissmisProgressDialog();
                    break;
                default:
                    break;
            }

        }
    }

    /**
     * 处理签名的
     */
    class SignHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BlueToothWrapper.MSG_EOS_SIGN_START:
                    LoggerManager.d("MSG_EOS_SIGN_START");
                    break;
                case BlueToothWrapper.MSG_EOS_SIGN_FINISH:
                    if (dialog != null && dialog.isShowing())dialog.cancel();
                    LoggerManager.d("MSG_EOS_SIGN_FINISH");
                    BlueToothWrapper.SignReturnValue returnValueSign = (BlueToothWrapper.SignReturnValue)msg.obj;
                    LoggerManager.d("Return Value: " + MiddlewareInterface.getReturnString(returnValueSign
                            .getReturnValue()));

                    if (returnValueSign.getReturnValue() == MiddlewareInterface.PAEW_RET_SUCCESS) {
                        //签名成功
                        LoggerManager.d("Sign Success");

                        String strSignature = new String(returnValueSign.getSignature());
                        LoggerManager.d("\nSignature Value: " + strSignature);
                        List<String> signatures = new ArrayList<>();
                        strSignature = strSignature.substring(0,strSignature.length() - 1);
                        signatures.add(strSignature);
                        transactionVO.setSignatures(signatures);


                        if (transactionVO != null) {
                            //构造PushTransaction 请求的json参数
                            PushTransactionReqParams reqParams = new PushTransactionReqParams();
                            reqParams.setTransaction(transactionVO);
                            reqParams.setSignatures(transactionVO.getSignatures());
                            final String VALUE_COMPRESSION = "none";
                            reqParams.setCompression(VALUE_COMPRESSION);

                            String buildTransactionJson = GsonUtils.
                                    objectToJson(reqParams);
                            LoggerManager.d("buildTransactionJson:" + buildTransactionJson);

                            //执行Push Transaction 最后一步操作
                            getP().pushTransaction(buildTransactionJson);
                        }

                    }else {
                        //签名失败
                        AlertUtil.showLongUrgeAlert(getActivity(), getString(R.string.bio_sign_fail));
                        LoggerManager.d("Sign Fail");
                    }

                    break;

                case BlueToothWrapper.MSG_GET_AUTH_TYPE:
                    //dialog.cancel();
                    //showVerifyFPDialog();

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
                                    dlg.dismiss();
                                    showVerifyFPDialog();
                                }
                            })
                            .setCancelable(false)
                            .create();
                    dlg.show();

                    break;
                case BlueToothWrapper.MSG_GET_USER_PIN:

                    View dlgView = getLayoutInflater().inflate(R.layout.ui_dlg_verify_pin, null);
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

    class ConnectHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BlueToothWrapper.MSG_INIT_CONTEXT_START:
                    LoggerManager.d("MSG_INIT_CONTEXT_START");
                    if (dialog != null && dialog.isShowing()){
                        dialog.cancel();
                    }
                    showConnectBioDialog();
                    break;
                case BlueToothWrapper.MSG_INIT_CONTEXT_FINISH:
                    //连接完成
                    LoggerManager.d("MSG_INIT_CONTEXT_FINISH");
                    BlueToothWrapper.InitContextReturnValue returnValueConnect = (BlueToothWrapper
                            .InitContextReturnValue) msg.obj;
                    if ((returnValueConnect != null) && (returnValueConnect.getReturnValue() == MiddlewareInterface.PAEW_RET_SUCCESS)) {
                        //连接成功
                        if (dialog != null && dialog.isShowing()){
                            dialog.cancel();
                        }
                        SPUtils.getInstance().put(CacheConstants.BIO_CONNECT_STATUS, CacheConstants.STATUS_BLUETOOTH_CONNCETED);

                        mContextHandle = returnValueConnect.getContextHandle();

                        ContextHandleEvent event = new ContextHandleEvent();
                        event.setContextHanle(mContextHandle);
                        EventBusProvider.postSticky(event);

                        getEosAddress();

                    } else {
                        //连接超时或失败
                        if (dialog != null && dialog.isShowing()){
                            dialog.cancel();
                        }
                        showConnectBioFailDialog();
                        SPUtils.getInstance().put(CacheConstants.BIO_CONNECT_STATUS, CacheConstants.STATUS_BLUETOOTH_DISCONNCETED);
                    }

                    connectThread.interrupt();
                    break;
                case BlueToothWrapper.MSG_VERIFYFP_START:
                    break;
                case BlueToothWrapper.MSG_GET_ADDRESS_START:
                    break;
                case BlueToothWrapper.MSG_GET_ADDRESS_FINISH:
                    //获取EOS地址（公钥）结束
                    BlueToothWrapper.GetAddressReturnValue returnValueAddress = (BlueToothWrapper
                            .GetAddressReturnValue) msg.obj;
                    if (returnValueAddress.getReturnValue() == MiddlewareInterface.PAEW_RET_SUCCESS) {
                        if (returnValueAddress.getCoinType() == MiddlewareInterface.PAEW_COIN_TYPE_EOS) {
                            //LoggerManager.d("EOS Address: " + returnValueAddress.getAddress());
                            String[] strArr = returnValueAddress.getAddress().split("####");
                            String publicKey = strArr[0];

                            DeviceInfoEvent event = new DeviceInfoEvent();
                            event.setEosPublicKey(publicKey);
                            EventBusProvider.postSticky(event);

                            getP().executeBluetoothTransferLogic(currentEOSName, getCollectionAccount(),
                                    getAmount()+ " EOS", getNote());
                        }
                    }else {

                    }
                    dissmisProgressDialog();

                    break;
                default:
                    break;
            }
        }
    }
}
