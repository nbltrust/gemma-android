package com.cybex.gma.client.ui.fragment;

import android.app.Activity;
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
import com.cybex.base.view.progress.RoundCornerProgressBar;
import com.cybex.base.view.tablayout.CommonTabLayout;
import com.cybex.base.view.tablayout.listener.CustomTabEntity;
import com.cybex.base.view.tablayout.listener.OnTabSelectListener;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.db.entity.WalletEntity;
import com.cybex.gma.client.manager.DBManager;
import com.cybex.gma.client.manager.LoggerManager;
import com.cybex.gma.client.ui.JNIUtil;
import com.cybex.gma.client.ui.model.vo.ResourceInfoVO;
import com.cybex.gma.client.ui.model.vo.TabTitleBuyRamVO;
import com.cybex.gma.client.ui.model.vo.TabTitleSellRamVO;
import com.cybex.gma.client.ui.presenter.BuySellRamPresenter;
import com.cybex.gma.client.utils.AmountUtil;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.siberiadante.customdialoglib.CustomDialog;
import com.siberiadante.customdialoglib.CustomFullDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;


/**
 * 买卖RAM Fragment
 */
public class BuySellRamFragment extends XFragment<BuySellRamPresenter> {

    private static final String RAM_SCOPE = "eosio";
    private static final String RAM_CODE = "eosio";
    private static final String RAM_TABLE = "rammarket";
    private List<String> ramMarketStaus;
    private final int OPERATION_BUY_RAM = 1;
    private final int OPERATION_SELL_RAM = 2;
    private ResourceInfoVO resourceInfoVO;
    private String kbPerEOS;
    private String eosPerKb;
    private int inputCount;
    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.superTextView_ram_amount) SuperTextView superTextViewRamAmount;
    @BindView(R.id.progressbar_ram) RoundCornerProgressBar progressbarRam;
    @BindView(R.id.superTextView_ram_status) SuperTextView superTextViewRamStatus;
    @BindView(R.id.CTL_buy_sell_ram) CommonTabLayout mTab;
    @BindView(R.id.edt_buy_ram) EditText edtBuyRam;
    @BindView(R.id.edt_sell_ram) EditText edtSellRam;
    @BindView(R.id.bt_buy_ram) Button btBuyRam;
    @BindView(R.id.bt_sell_ram) Button btSellRam;
    @BindView(R.id.tv_approximately_amount) TextView tvApproximatelyAmount;
    @BindView(R.id.tv_eos_ram_amount) TextView tvEosRamAmount;
    @BindView(R.id.tv_available_eos_ram) TextView tvAvaEosRam;
    @BindView(R.id.tv_ram_unitPrice) SuperTextView tvRamUnitPrice;

    @OnClick(R.id.bt_buy_ram)
    public void showBuyDialog() {
        showConfirmBuyRamDialog();
    }

    @OnClick(R.id.bt_sell_ram)
    public void showSellDialog() {
        showConfirmSellRamDialog();
    }

    @OnTextChanged(value = R.id.edt_buy_ram, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onChanged(Editable s) {
        if (EmptyUtils.isNotEmpty(getEOSAmount())) {
            setClickable(btBuyRam);
            if (EmptyUtils.isNotEmpty(kbPerEOS)) {
                String amount =
                        getResources().getString(R.string.approxy_amount) + AmountUtil.mul(kbPerEOS, getEOSAmount(), 4)
                                + " KB";
                tvApproximatelyAmount.setText(amount);
                tvApproximatelyAmount.setVisibility(View.VISIBLE);
            }
        } else {
            tvApproximatelyAmount.setVisibility(View.GONE);
            setUnclickable(btBuyRam);
        }
    }

    @OnTextChanged(value = R.id.edt_sell_ram, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onSellChanged(Editable s) {
        if (EmptyUtils.isNotEmpty(getRamAmount()) && EmptyUtils.isNotEmpty(eosPerKb)) {
            setClickable(btSellRam);
            if (EmptyUtils.isNotEmpty(eosPerKb)) {
                String amount =
                        getResources().getString(R.string.approxy_amount) + AmountUtil.mul(eosPerKb, getRamAmount(),
                                4) + " EOS";
                tvApproximatelyAmount.setVisibility(View.VISIBLE);
                tvApproximatelyAmount.setText(amount);
            }
        } else {
            setUnclickable(btSellRam);
            tvApproximatelyAmount.setVisibility(View.GONE);
        }

    }

    public static BuySellRamFragment newInstance(Bundle args) {
        BuySellRamFragment fragment = new BuySellRamFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(BuySellRamFragment.this, rootView);
    }

    Unbinder unbinder;

    @Override
    public void initData(Bundle savedInstanceState) {
        setNavibarTitle(getResources().getString(R.string.buy_sell_ram), true, true);
        inputCount = 0;
        tvApproximatelyAmount.setVisibility(View.GONE);
        setUnclickable(btSellRam);
        setUnclickable(btBuyRam);
        ArrayList<CustomTabEntity> list = new ArrayList<CustomTabEntity>();
        list.add(new TabTitleBuyRamVO(getString(R.string.tab_title_buy)));
        list.add(new TabTitleSellRamVO(getString(R.string.tab_title_sell)));
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
                    tvEosRamAmount.setText(getResources().getString(R.string.transfer_eos_amount));
                    if (EmptyUtils.isNotEmpty(resourceInfoVO)) {
                        String available_eos = resourceInfoVO.getBanlance();
                        tvAvaEosRam.setText(
                                String.format(getResources().getString(R.string.available_eos), available_eos));
                    }
                    //切换TAB时隐藏软键盘
                    if (EmptyUtils.isNotEmpty(getActivity()))hideSoftKeyboard(getActivity());

                } else if (position == 1) {
                    btBuyRam.setVisibility(View.GONE);
                    btSellRam.setVisibility(View.VISIBLE);
                    edtBuyRam.setVisibility(View.GONE);
                    edtSellRam.setVisibility(View.VISIBLE);
                    edtSellRam.setText("");
                    tvApproximatelyAmount.setVisibility(View.GONE);
                    tvEosRamAmount.setText(getResources().getString(R.string.transfer_ram_amount));
                    if (EmptyUtils.isNotEmpty(resourceInfoVO)) {
                        String ramAvailable = calAvailableRam(resourceInfoVO);
                        tvAvaEosRam.setText(String.format(getResources().getString(R.string.available_ram),
                                ramAvailable));
                    }

                    if (EmptyUtils.isNotEmpty(getActivity()))hideSoftKeyboard(getActivity());
                }
            }

            @Override
            public void onTabReselect(int position) {

            }
        });

        if (getArguments() != null) {
            resourceInfoVO = getArguments().getParcelable("ramInfo");
            if (EmptyUtils.isNotEmpty(resourceInfoVO)) {
                String ramUsed = AmountUtil.round(String.valueOf(resourceInfoVO.getRamUsed()), 4);
                String ramUsedKB = AmountUtil.div(ramUsed, "1024", 2);
                superTextViewRamStatus.setLeftString(getResources().getString(R.string.used) + ramUsedKB + " KB");
                String ramTotal = AmountUtil.round(String.valueOf(resourceInfoVO.getRamTotal()), 4);
                String ramTotalKB = AmountUtil.div(ramTotal, "1024", 2);
                superTextViewRamStatus.setRightString(
                        getResources().getString(R.string.total_amount) + ramTotalKB + " KB");
                tvAvaEosRam.setText(String.format(getResources().getString(R.string.available_eos),
                        String.valueOf(resourceInfoVO.getBanlance())));
                initProgressBar(resourceInfoVO.getRamProgress());
            }
        }
        getP().getRamMarketInfo();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_buy_sell_ram;
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public String getEOSAmount() {
        return edtBuyRam.getText().toString().trim();
    }

    public String getRamAmount() {
        return edtSellRam.getText().toString().trim();
    }

    public void setClickable(Button button) {
        button.setClickable(true);
        button.setBackground(getResources().getDrawable(R.drawable.shape_corner_button));
    }

    public void setUnclickable(Button button) {
        button.setClickable(false);
        button.setBackground(getResources().getDrawable(R.drawable.shape_corner_button_unclickable));
    }

    public void setRamUnitPrice(String ramUnitPriceKB) {
        eosPerKb = ramUnitPriceKB;
        tvRamUnitPrice.setRightString(String.format(getResources().getString(R.string.ram_unit_price), ramUnitPriceKB));
        String ramTotalKB = AmountUtil.div(String.valueOf(resourceInfoVO.getRamTotal()), "1024", 2);
        String ramPrice = AmountUtil.mul(ramUnitPriceKB, ramTotalKB, 4);
        superTextViewRamAmount.setRightString(
                String.format(getResources().getString(R.string.eos_ram_amount), ramPrice));
        kbPerEOS = AmountUtil.div("1", ramUnitPriceKB, 8);
    }

    public String calAvailableRam(ResourceInfoVO vo) {
        String ramUsed = String.valueOf(vo.getRamUsed());
        String ramTotal = String.valueOf(vo.getRamTotal());
        String ramAvailable = AmountUtil.sub(ramTotal, ramUsed, 2);
        String ramAvailableKB = AmountUtil.div(ramAvailable, "1024", 2);
        return ramAvailableKB;
    }

    public static void hideSoftKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 初始化Progress样式
     * 85%progress以上要用红色显示
     *
     * @param progress
     */
    public void initProgressBar(float progress) {
        RoundCornerProgressBar progressBar = progressbarRam;
        if (progress >= ParamConstants.PROGRESS_ALERT) {
            //显示值>=85%
            progressBar.setProgressColor(getResources().getColor(R.color.scarlet));
            progressBar.setProgress(progress);
            superTextViewRamStatus.setLeftIcon(getResources().getDrawable(R.drawable.ic_dot_red));
        } else {
            progressBar.setProgressColor(getResources().getColor(R.color.dark_sky_blue));
            progressBar.setProgress(progress);
            superTextViewRamStatus.setLeftIcon(getResources().getDrawable(R.drawable.ic_dot_blue));
        }
    }

    /**
     * 显示确认买入dialog
     */
    private void showConfirmBuyRamDialog() {
        int[] listenedItems = {R.id.btn_confirm_buy_ram, R.id.btn_close};
        CustomFullDialog dialog = new CustomFullDialog(getContext(),
                R.layout.dialog_confirm_buy, listenedItems, false, Gravity.BOTTOM);

        dialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
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
        dialog.show();
        TextView amount = dialog.findViewById(R.id.tv_ram_amount);
        String showAmount = getEOSAmount() + " EOS";
        amount.setText(showAmount);
        TextView memo = dialog.findViewById(R.id.tv_explanation);
        memo.setText(getResources().getString(R.string.buy_ram));
    }

    /**
     * 显示确认卖出dialog
     */
    private void showConfirmSellRamDialog() {
        int[] listenedItems = {R.id.btn_confirm_sell_ram, R.id.btn_close};
        CustomFullDialog dialog = new CustomFullDialog(getContext(),
                R.layout.dialog_confirm_sell, listenedItems, false, Gravity.BOTTOM);
        dialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
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
        dialog.show();

        TextView amount = dialog.findViewById(R.id.tv_ram_amount);
        String showRamAmount = getRamAmount() + " KB";
        amount.setText(showRamAmount);
        TextView memo = dialog.findViewById(R.id.tv_explanation);
        memo.setText(getResources().getString(R.string.sell_ram));
    }

    /**
     * 显示确认买入授权dialog
     */
    private void showConfirmAuthorDialog(int operation_type) {
        int[] listenedItems = {R.id.imc_cancel, R.id.btn_confirm_authorization};
        CustomFullDialog dialog = new CustomFullDialog(getContext(),
                R.layout.dialog_input_transfer_password, listenedItems, false, Gravity.BOTTOM);

        dialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
            @Override
            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.imc_cancel:
                        dialog.cancel();
                        break;
                    case R.id.btn_confirm_authorization:
                        WalletEntity curWallet = DBManager.getInstance().getWalletEntityDao().getCurrentWalletEntity();

                        switch (operation_type) {
                            case OPERATION_BUY_RAM:
                                //买入RAM操作
                                if (EmptyUtils.isNotEmpty(curWallet)) {
                                    final String cypher = curWallet.getCypher();
                                    EditText mPass = dialog.findViewById(R.id.et_password);
                                    ImageView iv_clear = dialog.findViewById(R.id.iv_password_clear);
                                    iv_clear.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mPass.setText("");
                                        }
                                    });
                                    String inputPass = mPass.getText().toString().trim();
                                    if (EmptyUtils.isNotEmpty(inputPass)) {
                                        final String key = JNIUtil.get_private_key(cypher, inputPass);
                                        if (key.equals("wrong password")) {
                                            inputCount++;
                                            iv_clear.setVisibility(View.VISIBLE);
                                            GemmaToastUtils.showLongToast(
                                                    getResources().getString(R.string.wrong_password));
                                            if (inputCount > 3){
                                                dialog.cancel();
                                                showPasswordHintDialog(OPERATION_BUY_RAM);
                                            }
                                        } else {
                                            final String curEOSName = curWallet.getCurrentEosName();
                                            String quantity = AmountUtil.add(getEOSAmount(), "0", 4) + " EOS";
                                            getP().executeBuyRamLogic(curEOSName, curEOSName, quantity, key);
                                            dialog.cancel();
                                        }
                                    } else {
                                        GemmaToastUtils.showLongToast(
                                                getResources().getString(R.string.please_input_pass));
                                    }

                                }
                                break;
                            //卖出RAM操作
                            case OPERATION_SELL_RAM:
                                if (EmptyUtils.isNotEmpty(curWallet)) {
                                    final String cypher = curWallet.getCypher();
                                    EditText mPass = dialog.findViewById(R.id.et_password);
                                    ImageView iv_clear = dialog.findViewById(R.id.iv_password_clear);
                                    iv_clear.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mPass.setText("");
                                        }
                                    });
                                    String inputPass = mPass.getText().toString().trim();
                                    if (EmptyUtils.isNotEmpty(inputPass)) {
                                        final String key = JNIUtil.get_private_key(cypher, inputPass);
                                        if (key.equals("wrong password")) {
                                            inputCount++;
                                            iv_clear.setVisibility(View.VISIBLE);
                                            GemmaToastUtils.showLongToast(
                                                    getResources().getString(R.string.wrong_password));

                                            if (inputCount > 3){
                                                dialog.cancel();
                                                showPasswordHintDialog(OPERATION_SELL_RAM);
                                            }
                                        } else {
                                            final String curEOSName = curWallet.getCurrentEosName();
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
                                                getResources().getString(R.string.please_input_pass));
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
        dialog.show();
    }

    /**
     * 显示密码提示Dialog
     */
    private void showPasswordHintDialog(int operation_type) {
        int[] listenedItems = {R.id.tv_i_understand};
        CustomDialog dialog = new CustomDialog(getContext(),
                R.layout.dialog_password_hint, listenedItems, false, Gravity.CENTER);
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
        WalletEntity curWallet = DBManager.getInstance().getWalletEntityDao().getCurrentWalletEntity();
        if (EmptyUtils.isNotEmpty(curWallet)){
            String passHint = curWallet.getPasswordTip();
            String showInfo = getString(R.string.password_hint_info) + " : " + passHint;
            tv_pass_hint.setText(showInfo);
        }
    }


}
