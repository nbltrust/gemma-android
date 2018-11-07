package com.cybex.eth.ui.activity;

import android.content.Intent;
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

import com.cybex.base.view.LabelLayout;
import com.cybex.componentservice.bean.TokenBean;
import com.cybex.componentservice.config.CacheConstants;
import com.cybex.componentservice.db.dao.MultiWalletEntityDao;
import com.cybex.componentservice.db.entity.EosWalletEntity;
import com.cybex.componentservice.db.entity.EthWalletEntity;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.utils.FormatValidateUtils;
import com.cybex.componentservice.utils.PasswordValidateHelper;
import com.cybex.componentservice.utils.listener.DecimalInputTextWatcher;
import com.cybex.componentservice.widget.EditTextWithScrollView;
import com.cybex.eth.R;
import com.cybex.eth.event.GasSettingEvent;
import com.cybex.eth.ui.presenter.EthTransferPresenter;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.SPUtils;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.siberiadante.customdialoglib.CustomFullDialog;


import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;

import me.framework.fragmentation.anim.DefaultHorizontalAnimator;
import me.framework.fragmentation.anim.FragmentAnimator;


public class EthTransferActivity extends XActivity<EthTransferPresenter> {

    private ScrollView rootScrollview;
    private TextView tvTitleReceiver;
    private LinearLayout viewReceiverAccount;
    private EditText etReceiverAccount;
    private ImageView ivTransferAccountClear;
    private TextView tvTitlePayAccount;
    private RelativeLayout viewChangeAccount;
    private ImageView imvWookongLogo;
    private TextView tvPayAccount;
    private TextView tvAmount;
    private TextView tvBanlance;
    private EditText etAmount;
    private ImageView ivTransferAmountClear;
    private TextView tvNote;
    private EditTextWithScrollView etNote;
    private ImageView ivTransferNoteClear;
    private LabelLayout labelMine;
    private Button btnTransferNextStep;
    private EthWalletEntity currentEthWallet;
    private MultiWalletEntity currentWallet;


    private TokenBean tokenBean;
    private BigInteger gasPrice;
    private BigDecimal balance;
    private long gasLimit = 210000;

    @Override
    public void bindUI(View view) {
        setNavibarTitle(getString(R.string.transfer), true);

        rootScrollview = (ScrollView) findViewById(R.id.root_scrollview);
        tvTitleReceiver = (TextView) findViewById(R.id.tv_title_receiver);
        viewReceiverAccount = (LinearLayout) findViewById(R.id.view_receiver_account);
        etReceiverAccount = (EditText) findViewById(R.id.et_receiver_account);
        ivTransferAccountClear = (ImageView) findViewById(R.id.iv_transfer_account_clear);
        tvTitlePayAccount = (TextView) findViewById(R.id.tv_title_pay_account);
        viewChangeAccount = (RelativeLayout) findViewById(R.id.view_change_account);
        imvWookongLogo = (ImageView) findViewById(R.id.imv_wookong_logo);
        tvPayAccount = (TextView) findViewById(R.id.tv_pay_account);
        tvAmount = (TextView) findViewById(R.id.tv_amount);
        tvBanlance = (TextView) findViewById(R.id.tv_banlance);
        etAmount = (EditText) findViewById(R.id.et_amount);
        ivTransferAmountClear = (ImageView) findViewById(R.id.iv_transfer_amount_clear);
        tvNote = (TextView) findViewById(R.id.tv_note);
        etNote = (EditTextWithScrollView) findViewById(R.id.et_note);
        ivTransferNoteClear = (ImageView) findViewById(R.id.iv_transfer_note_clear);
        labelMine = (LabelLayout) findViewById(R.id.label_mine);
        btnTransferNextStep = (Button) findViewById(R.id.btn_transfer_nextStep);
        labelMine.setRightText("");

        etReceiverAccount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (EmptyUtils.isEmpty(getCollectionAccount())) {
                        tvTitleReceiver.setText(getString(R.string.eth_title_receiver));
                        tvTitleReceiver.setTextColor(getResources().getColor(R.color.black_context));
                    } else {
                        ivTransferAccountClear.setVisibility(View.VISIBLE);
                    }
                } else {
                    ivTransferAccountClear.setVisibility(View.GONE);
                    validateButton();
                    if (EmptyUtils.isEmpty(getCollectionAccount())) {
                        tvTitleReceiver.setText(getString(R.string.eth_title_receiver));
                        tvTitleReceiver.setTextColor(getResources().getColor(R.color.black_context));
                    }
                    if (!FormatValidateUtils.isEthAddressValid(getCollectionAccount())) {
                        //显示alert样式
                        tvTitleReceiver.setText(getString(R.string.eth_tip_account_name_err));
                        tvTitleReceiver.setTextColor(getResources().getColor(R.color.scarlet));
                    } else {
                        //显示默认样式
                        tvTitleReceiver.setText(getString(R.string.eth_title_receiver));
                        tvTitleReceiver.setTextColor(getResources().getColor(R.color.black_context));
                    }
                }
            }
        });



        etAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
//                    validateAmountValue();
                    validateButton();
                    ivTransferAmountClear.setVisibility(View.GONE);
                } else {
                    if (EmptyUtils.isNotEmpty(etAmount.getText().toString().trim())) {
                        ivTransferAmountClear.setVisibility(View.VISIBLE);
                    }
                }
            }
        });


        etNote.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (EmptyUtils.isNotEmpty(etNote.getText().toString().trim()))  {
                        ivTransferNoteClear.setVisibility(View.VISIBLE);
                    }
                } else {
                    ivTransferNoteClear.setVisibility(View.GONE);
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
                if (EmptyUtils.isNotEmpty(etReceiverAccount.getText().toString().trim())) {
                    ivTransferAccountClear.setVisibility(View.VISIBLE);
                } else {
                    ivTransferAccountClear.setVisibility(View.GONE);
                }
                validateButton();
            }
        });

        ivTransferAccountClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etReceiverAccount.setText("");
            }
        });

//        etAmount.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (EmptyUtils.isNotEmpty(etAmount.getText().toString().trim())) {
//                    ivTransferAmountClear.setVisibility(View.VISIBLE);
//                } else {
//                    ivTransferAmountClear.setVisibility(View.GONE);
//                }
//            }
//        });

        ivTransferAmountClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etAmount.setText("");
            }
        });


        etNote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (EmptyUtils.isNotEmpty(etNote.getText().toString().trim())) {
                    ivTransferNoteClear.setVisibility(View.VISIBLE);
                } else {
                    ivTransferNoteClear.setVisibility(View.GONE);
                }
            }
        });

        ivTransferNoteClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etNote.setText("");
            }
        });

    }

    private void validateButton() {
        String collectionAccount = String.valueOf(etReceiverAccount.getText());
        String amount = String.valueOf(etAmount.getText());

        if (FormatValidateUtils.isEthAddressValid(collectionAccount) && !EmptyUtils.isEmpty(amount)&&balance!=null&&gasPrice!=null ) {
            btnTransferNextStep.setEnabled(true);
        } else {
            btnTransferNextStep.setEnabled(false);
        }

    }

    public String getCollectionAccount() {
        return etReceiverAccount.getText().toString().trim();
    }

    @Override
    public void initData(Bundle savedInstanceState) {

        currentWallet = DBManager.getInstance().getMultiWalletEntityDao().getCurrentMultiWalletEntity();
        currentEthWallet = currentWallet.getEthWalletEntities().get(0);
        tvPayAccount.setText(currentEthWallet.getAddress());
        tvBanlance.setText("余额：");

        if (currentWallet != null && currentEthWallet != null && currentWallet.getWalletType() == CacheConstants.WALLET_TYPE_BLUETOOTH) {
            imvWookongLogo.setVisibility(View.VISIBLE);
        } else {
            imvWookongLogo.setVisibility(View.GONE);
        }

        btnTransferNextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkToTransfer();
            }
        });

        labelMine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,EthTransferMineSettingActivity.class));
            }
        });


        tokenBean = new TokenBean("eth2", "");

        getP().getBalance(tokenBean);
        getP().getGasPrice();


    }

    private void checkToTransfer() {
        if (balance == null) {
            GemmaToastUtils.showLongToast("账户余额加载失败，无法转账");
            return;
        }

        if (gasPrice == null) {
            GemmaToastUtils.showLongToast("矿工费用加载失败，无法转账");
            return;
        }

        final String receiveAccount = etReceiverAccount.getText().toString().trim();
        if (!FormatValidateUtils.isEthAddressValid(receiveAccount)) {
            GemmaToastUtils.showLongToast("收款地址格式错误");
            return;
        }

        final String sendAmount = etAmount.getText().toString().trim();
        try {
            Float.parseFloat(sendAmount);
            int index = sendAmount.indexOf(".");
            if (index != -1&&index!=sendAmount.length()-1) {
                int length = sendAmount.split("\\.")[1].length();
                if (length > 4) {
                    GemmaToastUtils.showLongToast("金额只支持小数点后4位");
                    return;
                }
            }
        } catch (NumberFormatException exception) {
            GemmaToastUtils.showLongToast("金额格式错误");
            return;
        }

        showConfirmTransferDialog();



    }

    private void doTransfer(){
        final String receiveAccount = etReceiverAccount.getText().toString().trim();
        final String sendAmount = etAmount.getText().toString().trim();
        final String note = etNote.getText().toString().trim();

        PasswordValidateHelper helper = new PasswordValidateHelper(currentWallet, context);
        helper.startValidatePassword(new PasswordValidateHelper.PasswordValidateCallback() {
            @Override
            public void onValidateSuccess(String password) {
//                getP().transfer("0x1efD7B83B2eB20A33a20432B6CEeDc8Abe2e8Fc1",sendAmount,gasPrice,gasLimit+"",tokenBean,password,note);
                getP().transfer(receiveAccount, sendAmount, gasPrice, gasLimit + "", tokenBean, password, note);


            }

            @Override
            public void onValidateFail(int failedCount) {

            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.eth_activity_transfer;
    }

    @Override
    public EthTransferPresenter newP() {
        return new EthTransferPresenter();
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        // 设置横向(和安卓4.x动画相同)
        return new DefaultHorizontalAnimator();
    }

    public void setBalance(String balance) {
        etAmount.addTextChangedListener(new DecimalInputTextWatcher(etAmount, DecimalInputTextWatcher
                .Type.decimal, 8, balance) {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                if (EmptyUtils.isNotEmpty(etAmount.getText().toString().trim())) {
                    ivTransferAmountClear.setVisibility(View.VISIBLE);
                } else {
                    ivTransferAmountClear.setVisibility(View.GONE);
                }
                validateButton();
            }
        });
        this.balance = new BigDecimal(balance);
        tvBanlance.setText("余额：" + balance + tokenBean.getSymbol());
    }

    public EthWalletEntity getCurrentEthWallet() {
        return currentEthWallet;
    }

    public void setGasPrice(BigInteger gasPrice) {
        if (this.gasPrice == null) {
            this.gasPrice = gasPrice;
        }
        updateCurrentGasUsed();
    }

    private void updateCurrentGasUsed() {
        BigInteger gasUsedWei = gasPrice.multiply(new BigInteger(gasLimit + ""));
        BigDecimal gasPrice = Convert.fromWei(gasUsedWei.toString(), Convert.Unit.ETHER);
        labelMine.setRightText(gasPrice.toPlainString() + " ETH");
    }

    @Override
    public boolean useEventBus() {
        return true;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGasSetting(GasSettingEvent event){
        gasPrice=new BigInteger(event.gasPrice+"");
        gasLimit=event.gas;
        updateCurrentGasUsed();
    }



    /**
     * 确认转账dialog
     */
    private void showConfirmTransferDialog() {
        int[] listenedItems = {R.id.btn_close, R.id.btn_transfer_nextStep};
        CustomFullDialog dialog = new CustomFullDialog(context,
                R.layout.eth_dialog_transfer_confirm, listenedItems, false, Gravity.BOTTOM);

        dialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
            @Override
            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {
                MultiWalletEntity curWallet = DBManager.getInstance()
                        .getMultiWalletEntityDao()
                        .getCurrentMultiWalletEntity();
                if (curWallet != null) {
                    int walletType = curWallet.getWalletType();
                        if(R.id.btn_close==view.getId()){
                            dialog.cancel();
                        }else if(R.id.btn_transfer_nextStep==view.getId()){
                            switch (walletType) {
                                case CacheConstants.WALLET_TYPE_MNE_IMPORT:
                                case CacheConstants.WALLET_TYPE_PRIKEY_IMPORT:
                                case CacheConstants.WALLET_TYPE_MNE_CREATE:
                                    dialog.cancel();
                                    doTransfer();
                                    break;
                                case CacheConstants.WALLET_TYPE_BLUETOOTH:
                                    //蓝牙钱包转账
//                                    int status = SPUtils.getInstance().getInt(CacheConstants.BIO_CONNECT_STATUS);
//                                    if (status == CacheConstants.STATUS_BLUETOOTH_DISCONNCETED) {
//                                        //蓝牙卡未连接
//                                        startConnect();
//                                    } else {
//                                        //蓝牙卡已连接
//                                        getP().executeBluetoothTransferLogic(currentEOSName, getCollectionAccount(),
//                                                getAmount() + " EOS", getNote());
//                                    }
                                    break;
                            }
                        }
                    }
                }
        });
        dialog.show();

        TextView tv_payee = dialog.findViewById(R.id.tv_payee); //收款人
        TextView tv_amount = dialog.findViewById(R.id.tv_amount); //金额
        TextView tv_amount_unit = dialog.findViewById(R.id.tv_amount_unit); //金额
        TextView tv_note = dialog.findViewById(R.id.tv_note); //备注
        TextView tv_payment_account = dialog.findViewById(R.id.tv_payment_account);//付款账户

        String collectionAccount = String.valueOf(etReceiverAccount.getText());
        String amount = String.valueOf(etAmount.getText());

        if (EmptyUtils.isNotEmpty(collectionAccount)) {
            tv_payee.setText(collectionAccount);
        }
        if (EmptyUtils.isNotEmpty(amount)) {
            tv_amount.setText(amount);
        }
        tv_amount_unit.setText(tokenBean.getSymbol());
        tv_payment_account.setText(currentEthWallet.getAddress());
        String memo = String.valueOf(etNote.getText());
        tv_note.setText(memo);
    }


}
