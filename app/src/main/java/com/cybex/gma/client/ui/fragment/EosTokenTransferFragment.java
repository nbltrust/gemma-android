package com.cybex.gma.client.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.cybex.componentservice.config.CacheConstants;
import com.cybex.componentservice.db.dao.MultiWalletEntityDao;
import com.cybex.componentservice.db.entity.EosWalletEntity;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.componentservice.utils.AlertUtil;
import com.cybex.componentservice.utils.PasswordValidateHelper;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.event.ChangeAccountEvent;
import com.cybex.gma.client.event.ContextHandleEvent;
import com.cybex.gma.client.event.DeviceInfoEvent;
import com.cybex.gma.client.ui.model.request.PushTransactionReqParams;
import com.cybex.gma.client.ui.model.vo.TransferTransactionVO;
import com.cybex.gma.client.ui.presenter.EosTokenTransferPresenter;
import com.cybex.gma.client.ui.presenter.TransferPresenter;
import com.cybex.gma.client.utils.bluetooth.BlueToothWrapper;
import com.cybex.gma.client.utils.listener.DecimalInputTextWatcher;
import com.cybex.gma.client.widget.EditTextWithScrollView;
import com.extropies.common.CommonUtility;
import com.extropies.common.MiddlewareInterface;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.GsonUtils;
import com.hxlx.core.lib.utils.SPUtils;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.siberiadante.customdialoglib.CustomDialog;
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
    @BindView(R.id.root_scrollview) ScrollView rootScrollview;
    Unbinder unbinder;
    CustomFullDialog dialog = null;

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
        super.onDestroyView();
        unbinder.unbind();
    }

    public void showInitData(String balance, String eosName) {
        currentEOSName = eosName;
        tvPayAccount.setText(currentEOSName);

        if (EmptyUtils.isEmpty(balance)) {
            return;
        }

        String[] spiltBanlance = balance.split(" ");
        if (EmptyUtils.isEmpty(spiltBanlance)) {
            return;
        }

        maxValue = spiltBanlance[0].trim();
        LoggerManager.d("maxValue", maxValue);
        tvBanlance.setText(getString(R.string.eos_show_remain_balance) + balance);

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


    @Override
    public void initData(Bundle savedInstanceState) {

        etReceiverAccount.setText("");
        etAmount.setText("");
        etNote.setText("");
        //getP().requestBanlanceInfo();

        MultiWalletEntityDao dao = DBManager.getInstance().getMultiWalletEntityDao();
        MultiWalletEntity entity = dao.getCurrentMultiWalletEntity();
        EosWalletEntity eosEntity = entity.getEosWalletEntities().get(0);

        etReceiverAccount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (EmptyUtils.isEmpty(getCollectionAccount())) {
                        tvTitleReceiver.setText(getString(R.string.eos_title_receiver));
                        tvTitleReceiver.setTextColor(getResources().getColor(R.color.steel));
                    } else {
                        ivTransferAccountClear.setVisibility(View.VISIBLE);
                    }
                } else {
                    ivTransferAccountClear.setVisibility(View.GONE);
                    validateButton();
                    if (EmptyUtils.isEmpty(getCollectionAccount())) {
                        tvTitleReceiver.setText(getString(R.string.eos_title_receiver));
                        tvTitleReceiver.setTextColor(getResources().getColor(R.color.steel));
                    }
                    if (!isAccountNameValid() && EmptyUtils.isNotEmpty(
                            etReceiverAccount.getText().toString().trim())) {
                        //显示alert样式
                        tvTitleReceiver.setText(getString(R.string.eos_tip_account_name_err));
                        tvTitleReceiver.setTextColor(getResources().getColor(R.color.scarlet));
                    } else {
                        //显示默认样式
                        tvTitleReceiver.setText(getString(R.string.eos_title_receiver));
                        tvTitleReceiver.setTextColor(getResources().getColor(R.color.steel));
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
        return R.layout.eos_fragment_token_transfer;
    }

    @Override
    public EosTokenTransferPresenter newP() {
        return new EosTokenTransferPresenter();
    }

    private void validateButton() {
        String collectionAccount = String.valueOf(etReceiverAccount.getText());
        String amount = String.valueOf(etAmount.getText());

        if (!EmptyUtils.isEmpty(collectionAccount) && !EmptyUtils.isEmpty(amount) && collectionAccount.length() ==
                ParamConstants.VALID_EOSNAME_LENGTH) {
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
        String regEx = "^[a-z1-5]{12}$";
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
                        String privateKey  = Seed39.keyDecrypt(password, saved_pri_key);
//                        getP().executeTransferLogic(wallet.getEosWalletEntities().get(0)
//                                        .getCurrentEosName(),
//                                collectionAccount, amount, memo, privateKey);
                        //dialog.cancel();
                    }

                    @Override
                    public void onValidateFail(int failedCount) {
                        LoggerManager.d("validate fail");
                        //showPasswordHintDialog();
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

        etReceiverAccount.setText("");
        etAmount.setText("");
        etNote.setText("");

        //刷新数据
        //getP().requestBanlanceInfo();
    }

}
