package com.cybex.gma.client.ui.fragment;

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

import com.cybex.componentservice.db.dao.MultiWalletEntityDao;
import com.cybex.componentservice.db.entity.EosWalletEntity;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.componentservice.utils.AmountUtil;
import com.cybex.componentservice.utils.PasswordValidateHelper;
import com.cybex.componentservice.utils.listener.DecimalInputTextWatcher;
import com.cybex.componentservice.widget.EditTextWithScrollView;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.ui.model.vo.EosTokenVO;
import com.cybex.gma.client.ui.model.vo.TransferTransactionVO;
import com.cybex.gma.client.ui.presenter.EosTokenTransferPresenter;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.siberiadante.customdialoglib.CustomDialog;
import com.siberiadante.customdialoglib.CustomFullDialog;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
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
    Unbinder unbinder;
    CustomFullDialog dialog = null;



    EosTokenVO curToken;

    private TransferTransactionVO transactionVO;
    private String maxValue = "";
    private String currentEOSName = "";
    private String collectionAccount = "";
    private String amount = "";
    private String memo = "";

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
        clearData();
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


        if (curToken != null){
            tvBanlance.setText(getString(R.string.eos_show_remain_balance)
                    + balance
                    + " " + tokenName);
            int accuracy = 0;

            if (curToken.getAccurancy() > 0){
               accuracy = curToken.getAccurancy();
                etAmount.addTextChangedListener(new DecimalInputTextWatcher(etAmount, DecimalInputTextWatcher
                        .Type.decimal, accuracy, maxValue) {
                    @Override
                    public void afterTextChanged(Editable s) {
                        super.afterTextChanged(s);
                        validateButton();
                    }
                });
            }else {
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

        etReceiverAccount.setText("");
        etAmount.setText("");
        etNote.setText("");

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
                int curAccuracy  = curToken.getAccurancy();
                if (curAccuracy > 0){
                    if (curAccuracy == 1){
                        curAccuracyStr = "一";
                    }else if (curAccuracy == 2){
                        curAccuracyStr = "二";
                    }else if (curAccuracy == 3){
                        curAccuracyStr = "三";
                    }else if (curAccuracy == 4){
                        curAccuracyStr = "四";
                    }else if (curAccuracy == 5){
                        curAccuracyStr = "五";
                    }else if (curAccuracy == 6){
                        curAccuracyStr = "六";
                    }else if (curAccuracy == 7){
                        curAccuracyStr = "七";
                    }else if (curAccuracy == 8){
                        curAccuracyStr = "八";
                    }else if (curAccuracy == 9){
                        curAccuracyStr = "九";
                    }else if (curAccuracy == 10){
                        curAccuracyStr = "十";
                    }else if (curAccuracy == 11){
                        curAccuracyStr = "十一";
                    }else if (curAccuracy == 12){
                        curAccuracyStr = "十二";
                    }else if (curAccuracy == 13){
                        curAccuracyStr = "十三";
                    }else if (curAccuracy == 14){
                        curAccuracyStr = "十四";
                    }else if (curAccuracy == 15){
                        curAccuracyStr = "十五";
                    }else if (curAccuracy == 16){
                        curAccuracyStr = "十六";
                    }else if (curAccuracy == 17){
                        curAccuracyStr = "十七";
                    }else if (curAccuracy == 18){
                        curAccuracyStr = "十八";
                    }else {
                        curAccuracyStr = "四";
                    }
                    etAmount.setHint(String.format(getString(R.string.eos_token_tip_transfer), curAccuracyStr));
                }else {
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
                        if (hasFocus){
                            etAmount.setTypeface(Typeface.DEFAULT_BOLD);
                        }else {
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

    public void setTransactionVO(TransferTransactionVO transactionVO) {
        this.transactionVO = transactionVO;
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
                            showConfirmAuthoriDialog();
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

        collectionAccount = String.valueOf(etReceiverAccount.getText());
        if (curToken != null){
            amount = String.valueOf(etAmount.getText()) + " " + curToken.getTokenSymbol();
        }

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
                        String saved_pri_key = wallet.getEosWalletEntities().get(0).getPrivateKey();
                        String privateKey = Seed39.keyDecrypt(password, saved_pri_key);
                        String tokenContract = curToken.getTokenName();
                        int accuracy = curToken.getAccurancy();
                        String tokenSymbol = curToken.getTokenSymbol();
                        getP().executeTokenTransferLogic(
                                wallet.getEosWalletEntities().get(0).getCurrentEosName(),
                                collectionAccount, amount, memo,
                                privateKey, tokenContract,tokenSymbol, accuracy);
                        //dialog.cancel();
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

}
