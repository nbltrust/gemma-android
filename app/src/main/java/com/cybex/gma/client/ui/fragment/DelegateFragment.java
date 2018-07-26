package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.allen.library.SuperTextView;
import com.cybex.base.view.progress.RoundCornerProgressBar;
import com.cybex.base.view.tablayout.CommonTabLayout;
import com.cybex.base.view.tablayout.listener.CustomTabEntity;
import com.cybex.base.view.tablayout.listener.OnTabSelectListener;
import com.cybex.gma.client.R;
import com.cybex.gma.client.ui.model.vo.TabTitleDelegateVO;
import com.cybex.gma.client.ui.model.vo.TabTitleRefundVO;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.siberiadante.customdialoglib.CustomFullDialog;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 资源抵押界面
 */

public class DelegateFragment extends XFragment {

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
    @BindView(R.id.tv_refund_cpu) TextView tvRefundCpu;
    @BindView(R.id.edt_refund_cpu) EditText edtRefundCpu;
    @BindView(R.id.tv_refund_net) TextView tvRefundNet;
    @BindView(R.id.edt_refund_net) EditText edtRefundNet;
    @BindView(R.id.bt_refund_cpu_net) Button btRefundCpuNet;
    @BindView(R.id.layout_tab_delegate) View tab_delegate;
    @BindView(R.id.layout_tab_refund) View tab_refund;


    @OnClick(R.id.bt_delegate_cpu_net)
    public void showDialog() {
        showConfirmDelegateiDialog();
    }

    public static DelegateFragment newInstance() {
        Bundle args = new Bundle();
        DelegateFragment fragment = new DelegateFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
    }


    @Override
    public void initData(Bundle savedInstanceState) {
        setNavibarTitle("资源管理", true, true);

        ArrayList<CustomTabEntity> list = new ArrayList<CustomTabEntity>();
        list.add(new TabTitleDelegateVO());
        list.add(new TabTitleRefundVO());
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

    private void showDelegateTab(){
        tab_delegate.setVisibility(View.VISIBLE);
        tab_refund.setVisibility(View.GONE);
        btDelegateCpuNet.setVisibility(View.VISIBLE);
        btRefundCpuNet.setVisibility(View.GONE);

    }

    private void showRefundTab(){
        tab_delegate.setVisibility(View.GONE);
        tab_refund.setVisibility(View.VISIBLE);
        btDelegateCpuNet.setVisibility(View.GONE);
        btRefundCpuNet.setVisibility(View.VISIBLE);
    }

    /**
     * 显示确认质押dialog
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
                        showConfirmAuthoriDialog();
                        break;
                    default:
                        break;
                }
            }
        });
        dialog.show();
    }

    /**
     * 显示确认授权dialog
     */
    private void showConfirmAuthoriDialog() {
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
                        break;
                    default:
                        break;
                }
            }
        });
        dialog.show();
    }
}
