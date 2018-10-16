package com.cybex.gma.client.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
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
import com.cybex.componentservice.db.entity.WalletEntity;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.gma.client.ui.JNIUtil;
import com.cybex.gma.client.ui.model.vo.ResourceInfoVO;
import com.cybex.gma.client.ui.model.vo.TabTitleDelegateVO;
import com.cybex.gma.client.ui.model.vo.TabTitleRefundVO;
import com.cybex.gma.client.utils.AmountUtil;
import com.cybex.gma.client.utils.repeatclick.NoDoubleClick;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.siberiadante.customdialoglib.CustomDialog;
import com.siberiadante.customdialoglib.CustomFullDialog;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;

/**
 * 资源抵押界面
 */

public class DelegateFragment extends XFragment {

    private final int OPERATION_DELEGATE = 1;
    private final int OPERATION_UNDELEGATE = 2;
    private int inputCount;
    Unbinder unbinder;
    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.superTextView_cpu_amount) SuperTextView superTextViewCpuAmount;
    @BindView(R.id.progressbar_cpu) RoundCornerProgressBar progressbarCpu;
    @BindView(R.id.superTextView_cpu_status) SuperTextView superTextViewCpuStatus;
    @BindView(R.id.superTextView_net_amount) SuperTextView superTextViewNetAmount;
    @BindView(R.id.progressbar_net) RoundCornerProgressBar progressbarNet;
    @BindView(R.id.superTextView_net_status) SuperTextView superTextViewNetStatus;
    @BindView(R.id.tv_remain_balance) TextView tvRemainBalance;
    @BindView(R.id.bt_delegate_cpu_net) Button btDelegateCpuNet;
    @BindView(R.id.STL_delegate) CommonTabLayout mTab;
    @BindView(R.id.tv_delegate_cpu) TextView tvDelegateCpu;
    @BindView(R.id.edt_delegate_cpu) EditText edtDelegateCpu;
    @BindView(R.id.tv_delegate_net) TextView tvDelegateNet;
    @BindView(R.id.edt_delegate_net) EditText edtDelegateNet;
    @BindView(R.id.tv_undelegate_cpu) TextView tvundelegateCpu;
    @BindView(R.id.edt_undelegate_cpu) EditText edtundelegateCpu;
    @BindView(R.id.tv_undelegate_net) TextView tvundelegateNet;
    @BindView(R.id.edt_undelegate_net) EditText edtundelegateNet;
    @BindView(R.id.bt_undelegate_cpu_net) Button btundelegateCpuNet;
    @BindView(R.id.layout_tab_delegate) View tab_delegate;
    @BindView(R.id.layout_tab_undelegate) View tab_undelegate;

    @OnClick(R.id.bt_delegate_cpu_net)
    public void showDialog() {
        if (!NoDoubleClick.isDoubleClick())showConfirmDelegateiDialog();
    }

    @OnClick(R.id.bt_undelegate_cpu_net)
    public void showUnDialog(){
        if (!NoDoubleClick.isDoubleClick())showConfirmUndelegateiDialog();
    }

    public static DelegateFragment newInstance(Bundle args) {
        DelegateFragment fragment = new DelegateFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @OnTextChanged(value = R.id.edt_delegate_cpu, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterDelegateCpuChanged(){
        if ((EmptyUtils.isNotEmpty(getDelegateCpu()) || EmptyUtils.isNotEmpty(getDelegateNet()))
                && (!getDelegateCpu().equals(".") && !getDelegateNet().equals("."))){
            setClickable(btDelegateCpuNet);
        }else{
            setUnclickable(btDelegateCpuNet);
        }
    }

    @OnTextChanged(value = R.id.edt_delegate_net, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterDelegateNetChanged(){
        if ((EmptyUtils.isNotEmpty(getDelegateCpu()) || EmptyUtils.isNotEmpty(getDelegateNet()))
                && (!getDelegateNet().equals(".") && !getDelegateCpu().equals("."))) {
            setClickable(btDelegateCpuNet);
        }else{
            setUnclickable(btDelegateCpuNet);
        }
    }

    @OnTextChanged(value = R.id.edt_undelegate_cpu, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterUndelegateCpuChanged(){
        if ((EmptyUtils.isNotEmpty(getUndelegateCpu()) || EmptyUtils.isNotEmpty(getunDelegateNet()))
                && !getUndelegateCpu().equals(".") && !getunDelegateNet().equals(".")){
            setClickable(btundelegateCpuNet);
        }else{
            setUnclickable(btundelegateCpuNet);
        }
    }

    @OnTextChanged(value = R.id.edt_undelegate_net, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterundelegateNetChanged(){
        if ((EmptyUtils.isNotEmpty(getunDelegateNet()) || EmptyUtils.isNotEmpty(getUndelegateCpu()))
                && !getunDelegateNet().equals(".") && !getUndelegateCpu().equals(".")){
            setClickable(btundelegateCpuNet);
        }else{
            setUnclickable(btundelegateCpuNet);
        }
    }
    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
    }


    @Override
    public void initData(Bundle savedInstanceState) {
        setNavibarTitle(getResources().getString(R.string.title_delegate), true, true);
        inputCount = 0;
        setUnclickable(btundelegateCpuNet);
        setUnclickable(btDelegateCpuNet);
        ArrayList<CustomTabEntity> list = new ArrayList<CustomTabEntity>();
        list.add(new TabTitleDelegateVO(getString(R.string.tab_title_delegate)));
        list.add(new TabTitleRefundVO(getString(R.string.tab_title_un_delegate)));
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

        if (getArguments()!= null){
            ResourceInfoVO resourceInfoVO = getArguments().getParcelable("resourceInfo");
            if (EmptyUtils.isNotEmpty(resourceInfoVO)){
                String balance = getResources().getString(R.string.remain_balance_pure) + resourceInfoVO.getBanlance();
                tvRemainBalance.setText(balance);
                //cpu总量及已用显示
                String cpuUsed = AmountUtil.div(String.valueOf(resourceInfoVO.getCpuUsed()), "1000", 2);
                String cpuTotal = AmountUtil.div(String.valueOf(resourceInfoVO.getCpuTotal()), "1000", 2);
                String cpuWeight = AmountUtil.round(String.valueOf(resourceInfoVO.getCpuWeight()), 4);
                String cpuAmount = AmountUtil.div(cpuWeight, "10000", 4);
                superTextViewCpuStatus.setLeftString(String.format(getResources().getString(R.string.cpu_available),
                        cpuUsed));
                superTextViewCpuStatus.setRightString(String.format(getResources().getString(R.string.cpu_total), cpuTotal));
                superTextViewCpuAmount.setRightString(cpuAmount + " EOS");
                //CPU Progress
                initCPUProgressBar(resourceInfoVO.getCpuProgress());
                //NET 总量及已用显示
                String netUsed = AmountUtil.div(String.valueOf(resourceInfoVO.getNetUsed()), "1024", 2);
                String netTotal = AmountUtil.div(String.valueOf(resourceInfoVO.getNetTotal()), "1024", 2);
                String netWeight = AmountUtil.round(String.valueOf(resourceInfoVO.getNetWeight()), 4);
                String netAmount = AmountUtil.div(netWeight, "10000", 4);
                superTextViewNetStatus.setLeftString(String.format(getResources().getString(R.string.net_available),
                        netUsed));
                superTextViewNetStatus.setRightString(String.format(getResources().getString(R.string.net_total),
                        netTotal));
                superTextViewNetAmount.setRightString(netAmount + " EOS");
                //Net Progress
                initNETProgressBar(resourceInfoVO.getNetProgress());
            }
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_delegate;
    }

    @Override
    public Object newP() {
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * 初始化CPU ProgressBar样式
     * 85%progress以上要用红色显示
     * @param progress
     */
    public void initCPUProgressBar(float progress){
        RoundCornerProgressBar progressBar = progressbarCpu;

        if (progress >= ParamConstants.PROGRESS_MIN){
            if (progress >= ParamConstants.PROGRESS_ALERT && progress <= ParamConstants.PROGRESS_MAX){
                //显示值在85% ～ 100%之间
                progressBar.setProgressColor(getResources().getColor(R.color.scarlet));
                progressBar.setProgress(progress);
                superTextViewCpuStatus.setLeftIcon(getResources().getDrawable(R.drawable.ic_dot_red));
            }else if (progress >= ParamConstants.PROGRESS_MAX){
                //progress > 100%
                progressBar.setProgressColor(getResources().getColor(R.color.scarlet));
                progressBar.setProgress(ParamConstants.PROGRESS_MAX);
                superTextViewCpuStatus.setLeftIcon(getResources().getDrawable(R.drawable.ic_dot_red));
            }else {
                //progress 0 ~ 85%
                progressBar.setProgressColor(getResources().getColor(R.color.dark_sky_blue));
                progressBar.setProgress(progress);
                superTextViewCpuStatus.setLeftIcon(getResources().getDrawable(R.drawable.ic_dot_blue));
            }
        }else {
            //progress < 0
            progressBar.setProgressColor(getResources().getColor(R.color.dark_sky_blue));
            progressBar.setProgress(ParamConstants.PROGRESS_MIN);
            superTextViewCpuStatus.setLeftIcon(getResources().getDrawable(R.drawable.ic_dot_blue));
        }
    }

    /**
     * 初始化NET ProgressBar样式
     * 85%progress以上要用红色显示
     * @param progress
     */
    public void initNETProgressBar(float progress){
        RoundCornerProgressBar progressBar = progressbarNet;

        if (progress >= ParamConstants.PROGRESS_MIN){
            if (progress >= ParamConstants.PROGRESS_ALERT && progress <= ParamConstants.PROGRESS_MAX){
                //显示值在85% ～ 100%之间
                progressBar.setProgressColor(getResources().getColor(R.color.scarlet));
                progressBar.setProgress(progress);
                superTextViewNetStatus.setLeftIcon(getResources().getDrawable(R.drawable.ic_dot_red));
            }else if (progress >= ParamConstants.PROGRESS_MAX){
                //progress > 100%
                progressBar.setProgressColor(getResources().getColor(R.color.scarlet));
                progressBar.setProgress(ParamConstants.PROGRESS_MAX);
                superTextViewNetStatus.setLeftIcon(getResources().getDrawable(R.drawable.ic_dot_red));
            }else {
                //progress 0 ~ 85%
                progressBar.setProgressColor(getResources().getColor(R.color.dark_sky_blue));
                progressBar.setProgress(progress);
                superTextViewNetStatus.setLeftIcon(getResources().getDrawable(R.drawable.ic_dot_blue));
            }
        }else {
            //progress < 0
            progressBar.setProgressColor(getResources().getColor(R.color.dark_sky_blue));
            progressBar.setProgress(ParamConstants.PROGRESS_MIN);
            superTextViewNetStatus.setLeftIcon(getResources().getDrawable(R.drawable.ic_dot_blue));
        }
    }

    private void showDelegateTab(){
        tab_delegate.setVisibility(View.VISIBLE);
        tab_undelegate.setVisibility(View.GONE);
        btDelegateCpuNet.setVisibility(View.VISIBLE);
        btundelegateCpuNet.setVisibility(View.GONE);
        //隐藏软键盘
        if (EmptyUtils.isNotEmpty(getActivity()))hideSoftKeyboard(getActivity());
    }

    public static void hideSoftKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void showRefundTab(){
        tab_delegate.setVisibility(View.GONE);
        tab_undelegate.setVisibility(View.VISIBLE);
        btDelegateCpuNet.setVisibility(View.GONE);
        btundelegateCpuNet.setVisibility(View.VISIBLE);
       if (EmptyUtils.isNotEmpty(getActivity()))hideSoftKeyboard(getActivity());
    }

    public String getDelegateCpu(){
        return edtDelegateCpu.getText().toString().trim();
    }

    public String getUndelegateCpu(){
        return edtundelegateCpu.getText().toString().trim();
    }

    public String getDelegateNet(){
        return edtDelegateNet.getText().toString().trim();
    }
    public String getunDelegateNet(){
        return edtundelegateNet.getText().toString().trim();
    }

    public void setClickable(Button button){
        button.setClickable(true);
        button.setBackground(getResources().getDrawable(R.drawable.shape_corner_button));
    }

    public void setUnclickable(Button button){
        button.setClickable(false);
        button.setBackground(getResources().getDrawable(R.drawable.shape_corner_button_unclickable));
    }

    /**
     * 显示确认抵押dialog
     */
    private void showConfirmDelegateiDialog() {
        int[] listenedItems = {R.id.btn_delegate, R.id.btn_close};
        CustomFullDialog dialog = new CustomFullDialog(getContext(),
                R.layout.dialog_delegate_confirm, listenedItems, false, Gravity.BOTTOM);
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
        if (EmptyUtils.isNotEmpty(curWallet)){
            TextView tv_payee = dialog.findViewById(R.id.tv_payee);
            TextView tv_amount = dialog.findViewById(R.id.tv_amount);
            TextView tv_note = dialog.findViewById(R.id.tv_note);
            tv_payee.setText(curWallet.getCurrentEosName());
            String totalAmount = AmountUtil.add(getDelegateCpu(), getDelegateNet(), 4);
            String showAmount = totalAmount + " EOS";
            tv_amount.setText(showAmount);
            tv_note.setText(String.format(getResources().getString(R.string.delegate_memo), getDelegateCpu(), getDelegateNet()));
        }

    }

    /**
     * 显示确认解抵押dialog
     */
    private void showConfirmUndelegateiDialog() {
        int[] listenedItems = {R.id.btn_confirm_refund, R.id.btn_close};
        CustomFullDialog dialog = new CustomFullDialog(getContext(),
                R.layout.dialog_undelegate_confirm, listenedItems, false, Gravity.BOTTOM);
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
        if (EmptyUtils.isNotEmpty(curWallet)){
            TextView tv_receiver = dialog.findViewById(R.id.tv_undelegate_account);
            TextView tv_fund_amount = dialog.findViewById(R.id.tv_undelegate_amount);
            TextView tv_memo = dialog.findViewById(R.id.tv_undelegate_memo);

            tv_receiver.setText(curWallet.getCurrentEosName());
            String total_amount = AmountUtil.add(getunDelegateNet(), getUndelegateCpu(), 4) + " EOS";
            tv_fund_amount.setText(total_amount);
            tv_memo.setText(String.format(getResources().getString(R.string.unDelegate_memo), getUndelegateCpu(), getunDelegateNet
                    ()));
        }
    }

    /**
     * 显示确认授权dialog
     */
    private void showConfirmAuthoriDialog(int operation_type) {
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
                        switch (operation_type){
                            case OPERATION_DELEGATE:
                                if (EmptyUtils.isNotEmpty(curWallet)){
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
                                    if (EmptyUtils.isNotEmpty(inputPass)){
                                        final String key = JNIUtil.get_private_key(cypher, inputPass);
                                        if (key.equals("wrong password")){
                                            iv_clear.setVisibility(View.VISIBLE);
                                            GemmaToastUtils.showLongToast(getResources().getString(R.string.wrong_password));

                                            inputCount++;
                                            if (inputCount > 3){
                                                dialog.cancel();
                                                showPasswordHintDialog(OPERATION_DELEGATE);
                                            }
                                        }else{
                                            final String curEOSName = curWallet.getCurrentEosName();
                                            try {
                                                String stake_net_quantity = AmountUtil.add(getDelegateNet(), "0", 4) + " EOS";
                                                String stake_cpu_quantity = AmountUtil.add(getDelegateCpu(), "0", 4) + " EOS";
                                            }catch (NumberFormatException e){
                                                e.printStackTrace();
                                            }
                                            //getP().executeDelegateLogic(curEOSName, curEOSName, stake_net_quantity,
                                             //       stake_cpu_quantity, key);
                                            dialog.cancel();
                                        }
                                    }else{
                                        GemmaToastUtils.showLongToast(getResources().getString(R.string.please_input_pass));
                                    }
                                }
                                break;
                            case OPERATION_UNDELEGATE:
                                if (EmptyUtils.isNotEmpty(curWallet)){
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
                                    if (EmptyUtils.isNotEmpty(inputPass)){
                                        final String cypher = curWallet.getCypher();
                                        final String key = JNIUtil.get_private_key(cypher, inputPass);
                                        if (key.equals("wrong password")){
                                            iv_clear.setVisibility(View.VISIBLE);
                                            GemmaToastUtils.showLongToast(getResources().getString(R.string.wrong_password));

                                            inputCount++;
                                            if (inputCount > 3){
                                                dialog.cancel();
                                                showPasswordHintDialog(OPERATION_UNDELEGATE);
                                            }

                                        }else{
                                            final String curEOSName = curWallet.getCurrentEosName();
                                            try {
                                                String unstake_net_quantity = AmountUtil.add(getunDelegateNet(), "0", 4) + " EOS";
                                                String unstake_cpu_quantity = AmountUtil.add(getUndelegateCpu(), "0", 4) + " EOS";
                                            }catch (NumberFormatException e){
                                                e.printStackTrace();
                                            }

                                            //getP().executeUndelegateLogic(curEOSName, curEOSName,
                                            // unstake_net_quantity,
                                             //       unstake_cpu_quantity, key);
                                            dialog.cancel();
                                        }
                                    }else{
                                        GemmaToastUtils.showLongToast(getResources().getString(R.string.please_input_pass));
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
        if (EmptyUtils.isNotEmpty(curWallet)){
            String passHint = curWallet.getPasswordTip();
            String showInfo = getString(R.string.password_hint_info) + " : " + passHint;
            tv_pass_hint.setText(showInfo);
        }
    }
}
