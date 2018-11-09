package com.cybex.gma.client.ui.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.allen.library.SuperTextView;
import com.cybex.base.view.tablayout.CommonTabLayout;
import com.cybex.base.view.tablayout.listener.CustomTabEntity;
import com.cybex.base.view.tablayout.listener.OnTabSelectListener;
import com.cybex.componentservice.db.entity.EosWalletEntity;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.ui.model.vo.ResourceInfoVO;
import com.cybex.gma.client.ui.model.vo.TabTitleBuyRamVO;
import com.cybex.gma.client.ui.model.vo.TabTitleSellRamVO;
import com.cybex.gma.client.ui.presenter.BuySellRamPresenter;
import com.cybex.componentservice.utils.AmountUtil;
import com.cybex.componentservice.utils.listener.DecimalInputTextWatcher;
import com.cybex.gma.client.utils.repeatclick.NoDoubleClick;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.common.utils.HashGenUtil;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.siberiadante.customdialoglib.CustomDialog;
import com.siberiadante.customdialoglib.CustomFullDialog;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import seed39.Seed39;


/**
 * 买卖RAM Fragment
 */
public class BuySellRamFragment extends XFragment<BuySellRamPresenter> {

    private final int OPERATION_BUY_RAM = 1;
    private final int OPERATION_SELL_RAM = 2;
    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.CTL_buy_sell_ram) CommonTabLayout mTab;
    @BindView(R.id.tv_eos_ram_amount) TextView tvEosRamAmount;
    @BindView(R.id.tv_available_eos_ram) TextView tvAvaEosRam;
    @BindView(R.id.edt_buy_ram) EditText edtBuyRam;
    @BindView(R.id.edt_sell_ram) EditText edtSellRam;
    @BindView(R.id.tv_ram_unitPrice) SuperTextView tvRamUnitPrice;
    @BindView(R.id.tv_approximately_amount) TextView tvApproximatelyAmount;
    @BindView(R.id.bt_buy_ram) Button btBuyRam;
    @BindView(R.id.bt_sell_ram) Button btSellRam;
    Unbinder unbinder;
    private ResourceInfoVO resourceInfoVO;
    private String kbPerEOS;
    private String eosPerKb;
    private int inputCount;
    private String maxValue;
    private String maxRamValue;

    private CustomFullDialog confirmDialog;
    private CustomFullDialog confirmAuthorDialog;

    public static BuySellRamFragment newInstance(Bundle args) {
        BuySellRamFragment fragment = new BuySellRamFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static void hideSoftKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(
                    Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @OnClick(R.id.bt_buy_ram)
    public void showBuyDialog() {
        if (!NoDoubleClick.isDoubleClick()) {
            showConfirmBuyRamDialog();
        }
    }

    @OnClick(R.id.bt_sell_ram)
    public void showSellDialog() {
        if (!NoDoubleClick.isDoubleClick()) {
            showConfirmSellRamDialog();
        }
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(BuySellRamFragment.this, rootView);
        setNavibarTitle(getResources().getString(R.string.eos_buy_sell_ram), true, true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        if (getArguments() != null) {
            String ramUnitPriceKB = getArguments().getString(ParamConstants.RAM_UNIT_PRICE);
            if (ramUnitPriceKB != null) {
                //显示ram价格
                tvRamUnitPrice.setRightString(String.format(getString(R.string.eos_amount_ram_unit_price),
                        ramUnitPriceKB));

                resourceInfoVO = getArguments().getParcelable("resourceInfo");
                if (resourceInfoVO != null) {
                    String balance = resourceInfoVO.getBanlance();
                    maxValue = balance.split(" ")[0];

                    String ramAvailable = calAvailableRam(resourceInfoVO);
                    maxRamValue = ramAvailable.split(" ")[0];

                    tvAvaEosRam.setText(String.format(getString(R.string.eos_amount_balance), balance));

                    inputCount = 0;
                    tvApproximatelyAmount.setVisibility(View.GONE);
                    setUnclickable(btSellRam);
                    setUnclickable(btBuyRam);
                    ArrayList<CustomTabEntity> list = new ArrayList<CustomTabEntity>();
                    list.add(new TabTitleBuyRamVO(getString(R.string.eos_tab_title_buy)));
                    list.add(new TabTitleSellRamVO(getString(R.string.eos_tab_title_sell)));
                    mTab.setTabData(list);
                    mTab.setCurrentTab(0);

                    mTab.setOnTabSelectListener(new OnTabSelectListener() {
                        @Override
                        public void onTabSelect(int position) {
                            if (position == 0) {
                                btBuyRam.setVisibility(View.VISIBLE);
                                btSellRam.setVisibility(View.GONE);
                                edtBuyRam.setVisibility(View.VISIBLE);
                                edtSellRam.setVisibility(View.GONE);
                                tvApproximatelyAmount.setVisibility(View.GONE);
                                edtBuyRam.setText("");
                                tvEosRamAmount.setText(getResources().getString(R.string.eos_title_eos_quantity));
                                if (EmptyUtils.isNotEmpty(resourceInfoVO)) {
                                    String available_eos = resourceInfoVO.getBanlance();
                                    tvAvaEosRam.setText(
                                            String.format(getResources().getString(R.string.eos_amount_available_eos),
                                                    available_eos));
                                }
                                //切换TAB时隐藏软键盘
                                if (EmptyUtils.isNotEmpty(getActivity())) { hideSoftKeyboard(getActivity()); }
                                //切换TAB时给maxValue更新值

                            } else if (position == 1) {
                                btBuyRam.setVisibility(View.GONE);
                                btSellRam.setVisibility(View.VISIBLE);
                                edtBuyRam.setVisibility(View.GONE);
                                edtSellRam.setVisibility(View.VISIBLE);
                                edtSellRam.setText("");
                                tvApproximatelyAmount.setVisibility(View.GONE);
                                tvEosRamAmount.setText(getResources().getString(R.string.eos_ram_quantity));
                                if (EmptyUtils.isNotEmpty(resourceInfoVO)) {
                                    String ramAvailable = calAvailableRam(resourceInfoVO);
                                    tvAvaEosRam.setText(
                                            String.format(getResources().getString(R.string.eos_amount_available_ram),
                                                    ramAvailable));
                                }

                                if (EmptyUtils.isNotEmpty(getActivity())) { hideSoftKeyboard(getActivity()); }

                            }
                        }

                        @Override
                        public void onTabReselect(int position) {

                        }
                    });

                    edtBuyRam.addTextChangedListener(new DecimalInputTextWatcher(edtBuyRam, DecimalInputTextWatcher
                            .Type.decimal, 4, maxValue) {
                        @Override
                        public void afterTextChanged(Editable s) {
                            super.afterTextChanged(s);
                            if (EmptyUtils.isNotEmpty(getEOSAmount()) && !getEOSAmount().equals(".")) {
                                setClickable(btBuyRam);
                                if (EmptyUtils.isNotEmpty(kbPerEOS)) {
                                    String amount =
                                            getResources().getString(R.string.eos_tip_quantity_approxi) + AmountUtil.mul(
                                                    kbPerEOS, getEOSAmount(), 4) + " KB";
                                    tvApproximatelyAmount.setText(amount);
                                    tvApproximatelyAmount.setVisibility(View.VISIBLE);
                                }
                            } else {
                                tvApproximatelyAmount.setVisibility(View.GONE);
                                setUnclickable(btBuyRam);
                            }

                        }
                    });

                    edtSellRam.addTextChangedListener(new DecimalInputTextWatcher(edtSellRam, DecimalInputTextWatcher
                            .Type.decimal, 4, maxRamValue) {
                        @Override
                        public void afterTextChanged(Editable s) {
                            super.afterTextChanged(s);
                            if (EmptyUtils.isNotEmpty(getRamAmount()) && !getRamAmount().equals(".")) {
                                setClickable(btSellRam);
                                if (EmptyUtils.isNotEmpty(kbPerEOS)) {
                                    String amount =
                                            getResources().getString(R.string.eos_tip_quantity_approxi) + AmountUtil.mul(
                                                    kbPerEOS, getEOSAmount(), 4) + " KB";
                                    tvApproximatelyAmount.setText(amount);
                                    tvApproximatelyAmount.setVisibility(View.VISIBLE);
                                }
                            } else {
                                tvApproximatelyAmount.setVisibility(View.GONE);
                                setUnclickable(btSellRam);
                            }
                        }
                    });
                }
            }

            }

    }

    @Override
    public int getLayoutId() {
        return R.layout.eos_fragment_buy_sell_ram;
    }

    @Override
    public BuySellRamPresenter newP() {
        return new BuySellRamPresenter();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public String getEOSAmount() {
        return edtBuyRam.getText().toString().trim();
    }

    public String getRamAmount() {
        return edtSellRam.getText().toString().trim();
    }

    public void setClickable(Button button) {
        button.setClickable(true);
        button.setBackground(getResources().getDrawable(R.drawable.eos_shape_corner_button));
    }

    public void setUnclickable(Button button) {
        button.setClickable(false);
        button.setBackground(getResources().getDrawable(R.drawable.shape_corner_button_unclickable));
    }

    public void setRamUnitPrice(String ramUnitPriceKB) {
        eosPerKb = ramUnitPriceKB;
        tvRamUnitPrice.setRightString(
                String.format(getResources().getString(R.string.eos_amount_ram_unit_price), ramUnitPriceKB));
        String ramTotalKB = AmountUtil.div(String.valueOf(resourceInfoVO.getRamTotal()), "1024", 2);
        String ramPrice = AmountUtil.mul(ramUnitPriceKB, ramTotalKB, 4);
        //superTextViewRamAmount.setRightString(String.format(getResources().getString(R.string.eos_amount_ram),
        //ramPrice));
        kbPerEOS = AmountUtil.div("1", ramUnitPriceKB, 8);
    }

    public String calAvailableRam(ResourceInfoVO vo) {
        String ramUsed = String.valueOf(vo.getRamUsed());
        String ramTotal = String.valueOf(vo.getRamTotal());
        String ramAvailable = AmountUtil.sub(ramTotal, ramUsed, 2);
        String ramAvailableKB = AmountUtil.div(ramAvailable, "1024", 2);
        return ramAvailableKB;
    }

    /**
     * 显示确认买入dialog
     */
    private void showConfirmBuyRamDialog() {
        int[] listenedItems = {R.id.btn_confirm_buy_ram, R.id.btn_close};
        confirmDialog = new CustomFullDialog(getContext(),
                R.layout.eos_dialog_confirm_buy, listenedItems, false, Gravity.BOTTOM);

        confirmDialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
            @Override
            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.btn_close:
                        dialog.cancel();
                        break;
                    case R.id.btn_confirm_buy_ram:
                        dialog.cancel();
                        showConfirmAuthorDialog(OPERATION_BUY_RAM);
                        break;
                    default:
                        break;
                }
            }
        });
        confirmDialog.show();
        TextView amount = confirmDialog.findViewById(R.id.tv_ram_amount);
        String showAmount = AmountUtil.round(getEOSAmount(), 4);
        amount.setText(showAmount);
        TextView memo = confirmDialog.findViewById(R.id.tv_explanation);
        memo.setText(getResources().getString(R.string.eos_tip_buy_ram));
    }

    /**
     * 显示确认卖出dialog
     */
    private void showConfirmSellRamDialog() {
        int[] listenedItems = {R.id.btn_confirm_sell_ram, R.id.btn_close};
        confirmDialog = new CustomFullDialog(getContext(),
                R.layout.eos_dialog_confirm_sell, listenedItems, false, Gravity.BOTTOM);
        confirmDialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
            @Override
            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.btn_close:
                        dialog.cancel();
                        break;
                    case R.id.btn_confirm_sell_ram:
                        dialog.cancel();
                        showConfirmAuthorDialog(OPERATION_SELL_RAM);
                        break;
                    default:
                        break;
                }
            }
        });
        confirmDialog.show();

        TextView amount = confirmDialog.findViewById(R.id.tv_ram_amount);
        String showRamAmount = getRamAmount();
        amount.setText(showRamAmount);
        TextView memo = confirmDialog.findViewById(R.id.tv_explanation);
        memo.setText(getResources().getString(R.string.eos_tip_sell_ram));
    }

    /**
     * 显示确认买入授权dialog
     */
    private void showConfirmAuthorDialog(int operation_type) {
        int[] listenedItems = {R.id.imc_cancel, R.id.btn_confirm_authorization};
        confirmAuthorDialog = new CustomFullDialog(getContext(),
                R.layout.eos_dialog_input_transfer_password, listenedItems, false, Gravity.BOTTOM);

        confirmAuthorDialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
            @Override
            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.imc_cancel:
                        switch (operation_type) {
                            case OPERATION_BUY_RAM:
                                showConfirmBuyRamDialog();
                                break;
                            case OPERATION_SELL_RAM:
                                showConfirmSellRamDialog();
                                break;
                        }
                        dialog.cancel();
                        break;
                    case R.id.btn_confirm_authorization:
                        MultiWalletEntity curWallet = DBManager.getInstance().getMultiWalletEntityDao().getCurrentMultiWalletEntity();
                        EosWalletEntity curEosWallet = curWallet.getEosWalletEntities().get(0);

                        switch (operation_type) {
                            case OPERATION_BUY_RAM:
                                //买入RAM操作
                                if (EmptyUtils.isNotEmpty(curWallet)) {
                                    EditText mPass = dialog.findViewById(R.id.et_password);
                                    ImageView iv_clear = dialog.findViewById(R.id.iv_password_clear);
                                    iv_clear.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mPass.setText("");
                                        }
                                    });

                                    //验证密码
                                    String inputPass = mPass.getText().toString().trim();
                                    final String cypher = curWallet.getCypher();
                                    final String hashPwd = HashGenUtil.generateHashFromText(inputPass, HashGenUtil
                                            .TYPE_SHA256);
                                    final String saved_pri_key = curEosWallet.getPrivateKey();
                                    if (EmptyUtils.isNotEmpty(inputPass)) {

                                        if (!cypher.equals(hashPwd)) {
                                            //密码错误
                                            inputCount++;
                                            iv_clear.setVisibility(View.VISIBLE);
                                            GemmaToastUtils.showLongToast(
                                                    getResources().getString(R.string.eos_tip_wrong_password));
                                            if (inputCount > 3) {
                                                dialog.cancel();
                                                showPasswordHintDialog(OPERATION_BUY_RAM);
                                            }
                                        } else {
                                            //密码正确
                                            final String key = Seed39.keyDecrypt(inputPass, saved_pri_key);
                                            final String curEOSName = curEosWallet.getCurrentEosName();
                                            String quantity = AmountUtil.add(getEOSAmount(), "0", 4) + " EOS";
                                            getP().executeBuyRamLogic(curEOSName, curEOSName, quantity, key);
                                            dialog.cancel();
                                        }
                                    } else {
                                        GemmaToastUtils.showLongToast(
                                                getResources().getString(R.string.eos_tip_please_input_pass));
                                    }

                                }
                                break;
                            //卖出RAM操作
                            case OPERATION_SELL_RAM:
                                if (EmptyUtils.isNotEmpty(curWallet)) {

                                    EditText mPass = dialog.findViewById(R.id.et_password);
                                    ImageView iv_clear = dialog.findViewById(R.id.iv_password_clear);
                                    iv_clear.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mPass.setText("");
                                        }
                                    });
                                    String inputPass = mPass.getText().toString().trim();
                                    final String cypher = curWallet.getCypher();
                                    final String hashPwd = HashGenUtil.generateHashFromText(inputPass, HashGenUtil
                                            .TYPE_SHA256);
                                    final String saved_pri_key = curEosWallet.getPrivateKey();

                                    if (EmptyUtils.isNotEmpty(inputPass)) {
                                        if (!cypher.equals(hashPwd)) {
                                            //密码错误
                                            inputCount++;
                                            iv_clear.setVisibility(View.VISIBLE);
                                            GemmaToastUtils.showLongToast(
                                                    getResources().getString(R.string.eos_tip_wrong_password));

                                            if (inputCount > 3) {
                                                dialog.cancel();
                                                showPasswordHintDialog(OPERATION_SELL_RAM);
                                            }
                                        } else {
                                            //密码正确
                                            final String key = Seed39.keyDecrypt(inputPass, saved_pri_key);
                                            final String curEOSName = curEosWallet.getCurrentEosName();
                                            String ramAmountInStr = AmountUtil.mul(String.valueOf(getRamAmount()),
                                                    "1024", 0);
                                            String bytesInStr = AmountUtil.round(ramAmountInStr, 0);
                                            long bytes = Long.parseLong(bytesInStr);
                                            LoggerManager.d("bytes", bytes);

                                            getP().executeSellRamLogic(curEOSName, bytes, key);
                                            dialog.cancel();
                                        }
                                    } else {
                                        GemmaToastUtils.showLongToast(
                                                getResources().getString(R.string.eos_tip_please_input_pass));
                                    }
                                }
                                break;
                            default:
                                LoggerManager.d("参数错误");
                        }
                        break;
                    default:
                        break;
                }
            }
        });

        confirmAuthorDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (confirmDialog != null && !confirmDialog.isShowing()){
                    confirmDialog.show();
                }
            }
        });
        confirmAuthorDialog.show();
        EditText inputPass = confirmAuthorDialog.findViewById(R.id.et_password);
        MultiWalletEntity curWallet = DBManager.getInstance().getMultiWalletEntityDao().getCurrentMultiWalletEntity();
        EosWalletEntity curEosWallet = curWallet.getEosWalletEntities().get(0);
        if (EmptyUtils.isNotEmpty(curWallet) && curEosWallet != null) {
            inputPass.setHint(String.format(getResources().getString(R.string.eos_input_pass_hint),
                    curEosWallet.getCurrentEosName()));
        }


    }

    /**
     * 显示密码提示Dialog
     */
    private void showPasswordHintDialog(int operation_type) {
        int[] listenedItems = {R.id.tv_i_understand};
        CustomDialog dialog = new CustomDialog(getContext(),
                R.layout.eos_dialog_password_hint, listenedItems, false, Gravity.CENTER);
        dialog.setOnDialogItemClickListener(new CustomDialog.OnCustomDialogItemClickListener() {

            @Override
            public void OnCustomDialogItemClick(CustomDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.tv_i_understand:
                        dialog.cancel();
                        showConfirmAuthorDialog(operation_type);
                        break;
                    default:
                        break;
                }
            }
        });
        dialog.show();

        TextView tv_pass_hint = dialog.findViewById(R.id.tv_password_hint_hint);
        MultiWalletEntity curWallet = DBManager.getInstance().getMultiWalletEntityDao().getCurrentMultiWalletEntity();
        EosWalletEntity curEosWallet = curWallet.getEosWalletEntities().get(0);
        if (EmptyUtils.isNotEmpty(curWallet) && curEosWallet != null) {
            String passHint = curWallet.getPasswordTip();
            String showInfo = getString(R.string.eos_tip_password_hint) + " : " + passHint;
            tv_pass_hint.setText(showInfo);
        }
    }

}
