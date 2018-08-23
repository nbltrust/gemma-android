package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cybex.gma.client.R;
import com.cybex.gma.client.db.entity.WalletEntity;
import com.cybex.gma.client.event.ChangeAccountEvent;
import com.cybex.gma.client.event.TabSelectedEvent;
import com.cybex.gma.client.manager.DBManager;
import com.cybex.gma.client.manager.LoggerManager;
import com.cybex.gma.client.ui.JNIUtil;
import com.cybex.gma.client.ui.presenter.TransferPresenter;
import com.cybex.gma.client.utils.listener.DecimalInputTextWatcher;
import com.cybex.gma.client.widget.MyScrollView;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.siberiadante.customdialoglib.CustomFullDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 转账Fragment
 *
 * Created by wanglin on 2018/7/9.
 */
public class TransferFragment extends XFragment<TransferPresenter> {

    @BindView(R.id.icon_receiver)
    TextView iconReceiver;
    @BindView(R.id.view_divider)
    View viewDivider;
    @BindView(R.id.et_collection_account)
    EditText etCollectionAccount; //收款账户
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
    MyScrollView rootScrollview;
    Unbinder unbinder;

    private String maxValue = "";
    private String currentEOSName = "";

    private String collectionAccount = "";
    private String amount = "";
    private String memo = "";

    public static TransferFragment newInstance() {
        Bundle args = new Bundle();
        TransferFragment fragment = new TransferFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
        setNavibarTitle(getString(R.string.title_transfer), false);
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
                }
            }
        });

    }


    private void validateAmountValue() {
        String text = String.valueOf(etAmount.getText());
        if (EmptyUtils.isNotEmpty(text)) {
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

    @Override
    public void initData(Bundle savedInstanceState) {
        etCollectionAccount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateButton();
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
                validateButton();
            }
        });

    }


    private void validateButton() {
        String collectionAccount = String.valueOf(etCollectionAccount.getText());
        String amount = String.valueOf(etAmount.getText());

        if (!EmptyUtils.isEmpty(collectionAccount) && !EmptyUtils.isEmpty(amount)) {
            btnTransfer.setEnabled(true);
        } else {
            btnTransfer.setEnabled(false);
        }

    }

    @Override
    public boolean useEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onTabSelctedEvent(TabSelectedEvent event) {
        if (event != null && event.getPosition() == 1) {
            LoggerManager.d("tab transfer selected");
            getP().requestBanlanceInfo();
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_transfer;
    }

    @Override
    public TransferPresenter newP() {
        return new TransferPresenter();
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
                switch (view.getId()) {
                    case R.id.btn_close:
                        dialog.cancel();
                        break;
                    case R.id.btn_transfer:
                        dialog.cancel();
                        showConfirmAuthoriDialog();
                        break;
                    default:
                        break;
                }
            }
        });
        dialog.show();

        TextView tv_payee = dialog.findViewById(R.id.tv_payee); //收款人
        TextView tv_amount = dialog.findViewById(R.id.tv_amount); //金额
        TextView tv_note = dialog.findViewById(R.id.tv_note); //备注
        TextView tv_payment_account = dialog.findViewById(R.id.tv_payment_account);//付款账户


        collectionAccount = String.valueOf(etCollectionAccount.getText());
        amount = String.valueOf(etAmount.getText());
        memo = String.valueOf(etNote.getText());

        if (EmptyUtils.isNotEmpty(collectionAccount)) {
            tv_payee.setText(collectionAccount);
        }

        if (EmptyUtils.isNotEmpty(amount)) {
            tv_amount.setText(amount);
        }

        if (EmptyUtils.isNotEmpty(memo)) {
            tv_note.setText(memo);
        }

        if (EmptyUtils.isNotEmpty(currentEOSName)) {
            tv_payment_account.setText(currentEOSName);
        }


    }


    CustomFullDialog dialog = null;

    /**
     * 显示确认授权dialog
     */
    private void showConfirmAuthoriDialog() {
        int[] listenedItems = {R.id.imc_cancel, R.id.btn_confirm_authorization};
        dialog = new CustomFullDialog(getContext(),
                R.layout.dialog_input_transfer_password, listenedItems, false, Gravity.BOTTOM);
        dialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
            @Override
            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.imc_cancel:
                        dialog.cancel();
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
                                            collectionAccount, amount + " " + "EOS", memo, privateKey);
                                    dialog.cancel();
                                }

                            } else {
                                GemmaToastUtils.showShortToast("当前账户转账出现异常");
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
        etPasword.setHint("请输入@" + currentEOSName + "的密码");
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        etCollectionAccount.setText("");
        etAmount.setText("");
        etNote.setText("");
        unbinder.unbind();
    }
}
