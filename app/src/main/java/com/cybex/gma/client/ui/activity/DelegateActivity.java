package com.cybex.gma.client.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cybex.base.view.tablayout.CommonTabLayout;
import com.cybex.base.view.tablayout.listener.CustomTabEntity;
import com.cybex.base.view.tablayout.listener.OnTabSelectListener;
import com.cybex.componentservice.db.entity.WalletEntity;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.gma.client.R;
import com.cybex.gma.client.ui.JNIUtil;
import com.cybex.gma.client.ui.model.vo.ResourceInfoVO;
import com.cybex.gma.client.ui.model.vo.TabTitleDelegateVO;
import com.cybex.gma.client.ui.model.vo.TabTitleRefundVO;
import com.cybex.gma.client.ui.presenter.DelegatePresenter;
import com.cybex.componentservice.utils.AlertUtil;
import com.cybex.gma.client.utils.AmountUtil;
import com.cybex.componentservice.utils.SoftHideKeyBoardUtil;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.siberiadante.customdialoglib.CustomDialog;
import com.siberiadante.customdialoglib.CustomFullDialog;
import com.tapadoo.alerter.Alerter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import me.framework.fragmentation.anim.DefaultHorizontalAnimator;
import me.framework.fragmentation.anim.FragmentAnimator;

/**
 * 资源抵押页面容器Activity
 */

public class DelegateActivity extends XActivity<DelegatePresenter> {

    private final int OPERATION_DELEGATE = 1;
    private final int OPERATION_UNDELEGATE = 2;

    private final int STATUS_ZERO = 3;
    private final int STATUS_EXCEED = 4;
    private final int STATUS_VALID = 5;
    private final int STATUS_UNKNOW_ERR = 6;
    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.STL_delegate) CommonTabLayout mTab;
    @BindView(R.id.tv_delegate_cpu) TextView tvDelegateCpu;
    @BindView(R.id.edt_delegate_cpu) EditText edtDelegateCpu;
    @BindView(R.id.tv_delegate_net) TextView tvDelegateNet;
    @BindView(R.id.edt_delegate_net) EditText edtDelegateNet;
    @BindView(R.id.tv_undelegate_cpu) TextView tvUndelegateCpu;
    @BindView(R.id.edt_undelegate_cpu) EditText edtUndelegateCpu;
    @BindView(R.id.tv_undelegate_net) TextView tvUndelegateNet;
    @BindView(R.id.edt_undelegate_net) EditText edtUndelegateNet;
    @BindView(R.id.bt_delegate_cpu_net) Button btDelegateCpuNet;
    @BindView(R.id.bt_undelegate_cpu_net) Button btUndelegateCpuNet;
    @BindView(R.id.scroll_delegate) ScrollView scrollDelegate;
    @BindView(R.id.layout_tab_delegate) View tab_delegate;
    @BindView(R.id.layout_tab_undelegate) View tab_undelegate;
    @BindView(R.id.tv_balance_cpu) TextView tvBalanceCpu;
    @BindView(R.id.tv_balance_net) TextView tvBalanceNet;
    @BindView(R.id.tv_balance_cpu_undelegate) TextView tvBalanceCpuUndelegate;
    @BindView(R.id.tv_balance_net_undelegate) TextView tvBalanceNetUndelegate;

    private int inputCount;


    private ResourceInfoVO resourceInfoVO;

    @OnClick(R.id.bt_delegate_cpu_net)
    public void showDialog() {
        if (EmptyUtils.isNotEmpty(resourceInfoVO)) {
            Double balance = Double.parseDouble(resourceInfoVO.getBanlance().split(" ")[0]);
            int status = validateAmount(getDelegateCpu(), getDelegateNet(), balance);
            switch (status) {
                case STATUS_VALID:
                    showConfirmDelegateiDialog();
                    break;
                case STATUS_EXCEED:
                    //超过余额上限
                    AlertUtil.showShortUrgeAlert(this, getString(R.string.eos_tip_balance_not_enough));
                    break;
                case STATUS_ZERO:
                    //输入为零
                    AlertUtil.showShortUrgeAlert(this, getString(R.string.eos_tip_cant_input_zero));
                    break;
                case STATUS_UNKNOW_ERR:
                    AlertUtil.showShortUrgeAlert(this, getString(R.string.eos_unknow_err));
                    break;

            }
        }
    }

    @OnClick(R.id.bt_undelegate_cpu_net)
    public void showUnDialog() {
        showConfirmUndelegateiDialog();
    }

    @OnTextChanged(value = R.id.edt_delegate_cpu, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterDelegateCpuChanged(Editable s) {
        if ((EmptyUtils.isNotEmpty(getDelegateCpu()) || EmptyUtils.isNotEmpty(getDelegateNet()))
                && (!getDelegateCpu().equals(".") && !getDelegateNet().equals("."))) {
            setClickable(btDelegateCpuNet);
        } else {
            setUnclickable(btDelegateCpuNet);
        }

        String str = s.toString();
        int posDot = str.indexOf(".");
        if (posDot >= 0) {
            if (str.length() - posDot - 1 > 4) {
                s.delete(posDot + 5, posDot + 6);
                if (!Alerter.isShowing()) {
                    AlertUtil.showShortCommonAlert(this, getString(R.string.eos_tip_eos_amount_format_invalid));
                }
            }
        }
    }

    @OnTextChanged(value = R.id.edt_delegate_net, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterDelegateNetChanged(Editable s) {
        if ((EmptyUtils.isNotEmpty(getDelegateCpu()) || EmptyUtils.isNotEmpty(getDelegateNet()))
                && (!getDelegateNet().equals(".") && !getDelegateCpu().equals("."))) {
            setClickable(btDelegateCpuNet);
        } else {
            setUnclickable(btDelegateCpuNet);
        }

        String str = s.toString();
        int posDot = str.indexOf(".");
        if (posDot >= 0) {
            if (str.length() - posDot - 1 > 4) {
                s.delete(posDot + 5, posDot + 6);
                if (!Alerter.isShowing()) {
                    AlertUtil.showShortCommonAlert(this, getString(R.string.eos_tip_eos_amount_format_invalid));
                }
            }
        }
    }

    @OnTextChanged(value = R.id.edt_undelegate_cpu, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterUndelegateCpuChanged(Editable s) {
        if ((EmptyUtils.isNotEmpty(getUndelegateCpu()) || EmptyUtils.isNotEmpty(getunDelegateNet()))
                && !getUndelegateCpu().equals(".") && !getunDelegateNet().equals(".")) {
            setClickable(btUndelegateCpuNet);
        } else {
            setUnclickable(btUndelegateCpuNet);
        }

        String str = s.toString();
        int posDot = str.indexOf(".");
        if (posDot >= 0) {
            if (str.length() - posDot - 1 > 4) {
                s.delete(posDot + 5, posDot + 6);
                if (!Alerter.isShowing()) {
                    AlertUtil.showShortCommonAlert(this, getString(R.string.eos_tip_eos_amount_format_invalid));
                }
            }
        }
    }

    @OnTextChanged(value = R.id.edt_undelegate_net, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterundelegateNetChanged(Editable s) {
        if ((EmptyUtils.isNotEmpty(getunDelegateNet()) || EmptyUtils.isNotEmpty(getUndelegateCpu()))
                && !getunDelegateNet().equals(".") && !getUndelegateCpu().equals(".")) {
            setClickable(btUndelegateCpuNet);
        } else {
            setUnclickable(btUndelegateCpuNet);
        }

        String str = s.toString();
        int posDot = str.indexOf(".");
        if (posDot >= 0) {
            if (str.length() - posDot - 1 > 4) {
                s.delete(posDot + 5, posDot + 6);
                if (!Alerter.isShowing()) {
                    AlertUtil.showShortCommonAlert(this, getString(R.string.eos_tip_eos_amount_format_invalid));
                }
            }
        }
    }

    @Override
    public void bindUI(View view) {
        ButterKnife.bind(this);
        setNavibarTitle(getString(R.string.eos_title_delegate_undelegate), true);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        SoftHideKeyBoardUtil.assistActivity(this);

        inputCount = 0;
        setUnclickable(btUndelegateCpuNet);
        setUnclickable(btDelegateCpuNet);
        ArrayList<CustomTabEntity> list = new ArrayList<CustomTabEntity>();
        list.add(new TabTitleDelegateVO(getString(R.string.eos_tab_title_delegate)));
        list.add(new TabTitleRefundVO(getString(R.string.eos_tab_title_un_delegate)));
        mTab.setTabData(list);
        mTab.setCurrentTab(0);
        mTab.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                if (position == 0) {
                    showDelegateTab();
                } else if (position == 1) {
                    showRefundTab();
                }
            }

            @Override
            public void onTabReselect(int position) {

            }
        });

        resourceInfoVO = getIntent().getParcelableExtra("resourceInfo");
        String balance = resourceInfoVO.getBanlance();
        tvBalanceCpu.setText(String.format(getString(R.string.eos_amount_balance), balance));
        tvBalanceNet.setText(String.format(getString(R.string.eos_amount_balance), balance));
        tvBalanceCpuUndelegate.setText(String.format(getString(R.string.eos_amount_balance), balance));
        tvBalanceNetUndelegate.setText(String.format(getString(R.string.eos_amount_balance), balance));
    }

    @Override
    public int getLayoutId() {
        return R.layout.eos_activity_delegate;
    }

    @Override
    public DelegatePresenter newP() {
        return new DelegatePresenter();
    }


    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        // 设置横向(和安卓4.x动画相同)
        return new DefaultHorizontalAnimator();
    }


    private void showDelegateTab() {
        tab_delegate.setVisibility(View.VISIBLE);
        tab_undelegate.setVisibility(View.GONE);
        btDelegateCpuNet.setVisibility(View.VISIBLE);
        btUndelegateCpuNet.setVisibility(View.GONE);
        //隐藏软键盘
        if (EmptyUtils.isNotEmpty(this)) { hideSoftKeyboard(this); }
    }

    public static void hideSoftKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(
                    Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void showRefundTab() {
        tab_delegate.setVisibility(View.GONE);
        tab_undelegate.setVisibility(View.VISIBLE);
        btDelegateCpuNet.setVisibility(View.GONE);
        btUndelegateCpuNet.setVisibility(View.VISIBLE);
        if (EmptyUtils.isNotEmpty(this)) { hideSoftKeyboard(this); }
    }

    public String getDelegateCpu() {
        return edtDelegateCpu.getText().toString().trim();
    }

    public String getUndelegateCpu() {
        return edtUndelegateCpu.getText().toString().trim();
    }

    public String getDelegateNet() {
        return edtDelegateNet.getText().toString().trim();
    }

    public String getunDelegateNet() {
        return edtUndelegateNet.getText().toString().trim();
    }

    public void setClickable(Button button) {
        button.setClickable(true);
        button.setBackground(getResources().getDrawable(R.drawable.eos_shape_corner_button));
    }

    public void setUnclickable(Button button) {
        button.setClickable(false);
        button.setBackground(getResources().getDrawable(R.drawable.shape_corner_button_unclickable));
    }

    /**
     * 显示确认抵押dialog
     */
    private void showConfirmDelegateiDialog() {
        int[] listenedItems = {R.id.btn_delegate, R.id.btn_close};
        CustomFullDialog dialog = new CustomFullDialog(this,
                R.layout.eos_dialog_delegate_confirm, listenedItems, false, Gravity.BOTTOM);
        dialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
            @Override
            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.btn_close:
                        dialog.cancel();
                        break;
                    case R.id.btn_delegate:
                        dialog.cancel();
                        showConfirmAuthoriDialog(OPERATION_DELEGATE);
                        break;
                    default:
                        break;
                }
            }
        });
        dialog.show();

        //给dialog各个TextView设置值
        WalletEntity curWallet = DBManager.getInstance().getWalletEntityDao().getCurrentWalletEntity();
        if (EmptyUtils.isNotEmpty(curWallet)) {
            TextView tv_payee = dialog.findViewById(R.id.tv_payee);
            TextView tv_amount = dialog.findViewById(R.id.tv_amount);
            TextView tv_note = dialog.findViewById(R.id.tv_note);
            tv_payee.setText(curWallet.getCurrentEosName());
            try {
                String totalAmount;
                if (EmptyUtils.isNotEmpty(getDelegateCpu()) && EmptyUtils.isNotEmpty(getDelegateNet())) {
                    //抵押的CPU和NET输入都不为空
                    totalAmount = AmountUtil.add(getDelegateCpu(), getDelegateNet(), 4);
                    tv_note.setText(String.format(getResources().getString(R.string.eos_delegate_memo),
                            AmountUtil.round(getDelegateCpu(), 4),
                            AmountUtil.round(getDelegateNet(), 4)));
                } else if (EmptyUtils.isEmpty(getDelegateCpu()) && EmptyUtils.isNotEmpty(getDelegateNet())) {
                    //抵押的CPU输入为空,NET不为空
                    totalAmount = AmountUtil.round(getDelegateNet(), 4);
                    tv_note.setText(String.format(getResources().getString(R.string.eos_delegate_memo),
                            "0.0000",
                            AmountUtil.round(getDelegateNet(), 4)));
                } else if (EmptyUtils.isEmpty(getDelegateNet()) && EmptyUtils.isNotEmpty(getDelegateCpu())) {
                    //抵押的NET输入为空
                    totalAmount = AmountUtil.round(getDelegateCpu(), 4);
                    tv_note.setText(String.format(getResources().getString(R.string.eos_delegate_memo),
                            AmountUtil.round(getDelegateCpu(), 4),
                            "0.0000"));
                } else {
                    //两者输入都为空
                    totalAmount = "0.0000";
                }
                String showAmount = totalAmount + " EOS";
                tv_amount.setText(showAmount);

            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 显示确认解抵押dialog
     */
    private void showConfirmUndelegateiDialog() {
        int[] listenedItems = {R.id.btn_confirm_refund, R.id.btn_close};
        CustomFullDialog dialog = new CustomFullDialog(this,
                R.layout.eos_dialog_undelegate_confirm, listenedItems, false, Gravity.BOTTOM);
        dialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
            @Override
            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.btn_close:
                        dialog.cancel();
                        break;
                    case R.id.btn_confirm_refund:
                        dialog.cancel();
                        showConfirmAuthoriDialog(OPERATION_UNDELEGATE);
                        break;
                    default:
                        break;
                }
            }
        });
        dialog.show();

        //给dialog各个TextView设置值
        WalletEntity curWallet = DBManager.getInstance().getWalletEntityDao().getCurrentWalletEntity();
        if (EmptyUtils.isNotEmpty(curWallet)) {
            TextView tv_receiver = dialog.findViewById(R.id.tv_undelegate_account);
            TextView tv_fund_amount = dialog.findViewById(R.id.tv_undelegate_amount);
            TextView tv_memo = dialog.findViewById(R.id.tv_undelegate_memo);

            tv_receiver.setText(curWallet.getCurrentEosName());
            try {
                String totalAmount;
                if (EmptyUtils.isNotEmpty(getUndelegateCpu()) && EmptyUtils.isNotEmpty(getunDelegateNet())) {
                    //解除抵押CPU和NET输入都不为空
                    totalAmount = AmountUtil.add(getunDelegateNet(), getUndelegateCpu(), 4);
                    tv_memo.setText(String.format(getResources().getString(R.string.eos_unDelegate_memo),
                            AmountUtil.round(getUndelegateCpu(), 4),
                            AmountUtil.round(getunDelegateNet(), 4)));
                } else if (EmptyUtils.isEmpty(getUndelegateCpu()) && EmptyUtils.isNotEmpty(getunDelegateNet())) {
                    //解除抵押CPU输入为空, NET不为空
                    totalAmount = AmountUtil.round(getunDelegateNet(), 4);
                    tv_memo.setText(String.format(getResources().getString(R.string.eos_unDelegate_memo),
                            "0.0000",
                            AmountUtil.round(getunDelegateNet(), 4)));
                } else if (EmptyUtils.isEmpty(getunDelegateNet()) && EmptyUtils.isNotEmpty(getUndelegateCpu())) {
                    //解除抵押NET输入为空， CPU不为空
                    totalAmount = AmountUtil.round(getUndelegateCpu(), 4);
                    tv_memo.setText(String.format(getResources().getString(R.string.eos_unDelegate_memo),
                            AmountUtil.round(getUndelegateCpu(), 4),
                            "0.0000"));
                } else {
                    //两者都为空
                    totalAmount = "0.0000";
                }

                String showAmount = totalAmount + " EOS";
                tv_fund_amount.setText(showAmount);

            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 显示确认授权dialog
     */
    private void showConfirmAuthoriDialog(int operation_type) {
        int[] listenedItems = {R.id.imc_cancel, R.id.btn_confirm_authorization};
        CustomFullDialog dialog = new CustomFullDialog(this,
                R.layout.eos_dialog_input_transfer_password, listenedItems, false, Gravity.BOTTOM);
        dialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
            @Override
            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.imc_cancel:
                        dialog.cancel();
                        switch (operation_type) {
                            case OPERATION_DELEGATE:
                                showConfirmDelegateiDialog();
                                break;
                            case OPERATION_UNDELEGATE:
                                showConfirmUndelegateiDialog();
                                break;
                        }
                        break;
                    case R.id.btn_confirm_authorization:
                        WalletEntity curWallet = DBManager.getInstance().getWalletEntityDao().getCurrentWalletEntity();
                        switch (operation_type) {
                            case OPERATION_DELEGATE:
                                if (EmptyUtils.isNotEmpty(curWallet)) {
                                    //抵押操作
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
                                    if (EmptyUtils.isNotEmpty(inputPass)) {
                                        final String key = JNIUtil.get_private_key(cypher, inputPass);
                                        if (key.equals("wrong password")) {
                                            iv_clear.setVisibility(View.VISIBLE);
                                            GemmaToastUtils.showLongToast(
                                                    getResources().getString(R.string.eos_tip_wrong_password));

                                            inputCount++;
                                            if (inputCount > 3) {
                                                dialog.cancel();
                                                showPasswordHintDialog(OPERATION_DELEGATE);
                                            }
                                        } else {
                                            final String curEOSName = curWallet.getCurrentEosName();
                                            try {
                                                String cpu_quantity;
                                                String net_quantity;
                                                if (EmptyUtils.isNotEmpty(getDelegateCpu()) && EmptyUtils.isNotEmpty
                                                        (getDelegateNet())) {
                                                    //抵押CPU和NET输入都不为空
                                                    cpu_quantity = AmountUtil.round(getDelegateCpu(), 4);
                                                    net_quantity = AmountUtil.round(getDelegateNet(), 4);
                                                } else if (EmptyUtils.isEmpty(getDelegateCpu())) {
                                                    //抵押CPU输入为空
                                                    cpu_quantity = "0.0000";
                                                    net_quantity = AmountUtil.round(getDelegateNet(), 4);
                                                } else {
                                                    //抵押NET输入为空
                                                    cpu_quantity = AmountUtil.round(getDelegateCpu(), 4);
                                                    net_quantity = "0.0000";
                                                }

                                                String stake_net_quantity = net_quantity + " EOS";
                                                String stake_cpu_quantity = cpu_quantity + " EOS";
                                                getP().executeDelegateLogic(curEOSName, curEOSName, stake_net_quantity,
                                                        stake_cpu_quantity, key);

                                            } catch (NumberFormatException e) {
                                                e.printStackTrace();
                                            }
                                            dialog.cancel();
                                        }
                                    } else {
                                        GemmaToastUtils.showLongToast(
                                                getResources().getString(R.string.eos_tip_please_input_pass));
                                    }
                                }
                                break;
                            case OPERATION_UNDELEGATE:
                                if (EmptyUtils.isNotEmpty(curWallet)) {
                                    //解抵押操作
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
                                        final String cypher = curWallet.getCypher();
                                        final String key = JNIUtil.get_private_key(cypher, inputPass);
                                        if (key.equals("wrong password")) {
                                            iv_clear.setVisibility(View.VISIBLE);
                                            GemmaToastUtils.showLongToast(
                                                    getResources().getString(R.string.eos_tip_wrong_password));

                                            inputCount++;
                                            if (inputCount > 3) {
                                                dialog.cancel();
                                                showPasswordHintDialog(OPERATION_UNDELEGATE);
                                            }

                                        } else {
                                            final String curEOSName = curWallet.getCurrentEosName();
                                            try {
                                                String cpu_quantity;
                                                String net_quantity;
                                                if (EmptyUtils.isNotEmpty(getUndelegateCpu()) && EmptyUtils.isNotEmpty
                                                        (getunDelegateNet())) {
                                                    //解除抵押CPU和NET输入都不为空
                                                    cpu_quantity = AmountUtil.round(getUndelegateCpu(), 4);
                                                    net_quantity = AmountUtil.round(getunDelegateNet(), 4);
                                                } else if (EmptyUtils.isEmpty(getUndelegateCpu())) {
                                                    //解除抵押CPU输入为空
                                                    cpu_quantity = "0.0000";
                                                    net_quantity = AmountUtil.round(getunDelegateNet(), 4);
                                                } else {
                                                    //解除抵押NET输入为空
                                                    cpu_quantity = AmountUtil.round(getUndelegateCpu(), 4);
                                                    net_quantity = "0.0000";
                                                }

                                                String unstake_net_quantity = net_quantity + " EOS";
                                                String unstake_cpu_quantity = cpu_quantity + " EOS";

                                                getP().executeUndelegateLogic(curEOSName, curEOSName,
                                                        unstake_net_quantity,
                                                        unstake_cpu_quantity, key);
                                            } catch (NumberFormatException e) {
                                                e.printStackTrace();
                                            }
                                            dialog.cancel();
                                        }
                                    } else {
                                        GemmaToastUtils.showLongToast(
                                                getResources().getString(R.string.eos_tip_please_input_pass));
                                    }
                                }
                                break;
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        dialog.show();
        EditText inputPass = dialog.findViewById(R.id.et_password);
        WalletEntity curWallet = DBManager.getInstance().getWalletEntityDao().getCurrentWalletEntity();
        if (EmptyUtils.isNotEmpty(curWallet)) {
            inputPass.setHint(String.format(getResources().getString(R.string.eos_input_pass_hint),
                    curWallet.getCurrentEosName()));
        }
    }

    /**
     * 显示密码提示Dialog
     */
    private void showPasswordHintDialog(int operation_type) {
        int[] listenedItems = {R.id.tv_i_understand};
        CustomDialog dialog = new CustomDialog(this,
                R.layout.eos_dialog_password_hint, listenedItems, false, Gravity.CENTER);
        dialog.setOnDialogItemClickListener(new CustomDialog.OnCustomDialogItemClickListener() {

            @Override
            public void OnCustomDialogItemClick(CustomDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.tv_i_understand:
                        dialog.cancel();
                        showConfirmAuthoriDialog(operation_type);
                        break;
                    default:
                        break;
                }
            }
        });
        dialog.show();

        TextView tv_pass_hint = dialog.findViewById(R.id.tv_password_hint_hint);
        WalletEntity curWallet = DBManager.getInstance().getWalletEntityDao().getCurrentWalletEntity();
        if (EmptyUtils.isNotEmpty(curWallet)) {
            String passHint = curWallet.getPasswordTip();
            String showInfo = getString(R.string.eos_tip_password_hint) + " : " + passHint;
            tv_pass_hint.setText(showInfo);
        }
    }

    /**
     * cpu和net输入阈值判断
     *
     * @param cpuInput
     * @param netInput
     * @param maxValue
     * @return
     */
    public int validateAmount(String cpuInput, String netInput, Double maxValue) {
        String totalAmount;
        if (EmptyUtils.isNotEmpty(cpuInput) && EmptyUtils.isNotEmpty(netInput)) {
            //CPU和NET输入都不为空
            totalAmount = AmountUtil.add(cpuInput, netInput, 4);
        } else if (EmptyUtils.isEmpty(cpuInput) && EmptyUtils.isNotEmpty(netInput)) {
            //CPU输入空
            totalAmount = AmountUtil.round(netInput, 4);
        } else if (EmptyUtils.isEmpty(netInput) && EmptyUtils.isNotEmpty(cpuInput)) {
            //NET输入空
            totalAmount = AmountUtil.round(cpuInput, 4);
        } else {
            totalAmount = "0.0000";
        }

        try {
            Double sum = Double.parseDouble(totalAmount);
            if (sum <= maxValue && sum > 0) {
                return STATUS_VALID;
            } else if (sum > maxValue) {
                return STATUS_EXCEED;
            } else {
                return STATUS_ZERO;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return STATUS_UNKNOW_ERR;
    }
}
