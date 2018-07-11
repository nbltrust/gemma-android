package com.cybex.gma.client.ui.main.fragment;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cybex.gma.client.R;
import com.cybex.gma.client.ui.main.presenter.TransferPresenter;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.siberiadante.customdialoglib.CustomFullDialog;

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

    @BindView(R.id.icon_receiver) TextView iconReceiver;
    @BindView(R.id.view_divider) View viewDivider;
    @BindView(R.id.et_collection_account) EditText etCollectionAccount;
    @BindView(R.id.tv_title_pay_account) TextView tvTitlePayAccount;
    @BindView(R.id.tv_pay_account) TextView tvPayAccount;
    @BindView(R.id.tv_banlance) TextView tvBanlance;
    @BindView(R.id.tv_amount) TextView tvAmount;
    @BindView(R.id.et_amount) EditText etAmount;
    @BindView(R.id.tv_note) TextView tvNote;
    @BindView(R.id.et_note) EditText etNote;
    @BindView(R.id.btn_transfer) Button btnTransfer;
    @BindView(R.id.root_scrollview) ScrollView rootScrollview;
    Unbinder unbinder;


    public static TransferFragment newInstance() {
        Bundle args = new Bundle();
        TransferFragment fragment = new TransferFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
        setNavibarTitle("转账", false);
    }


    @Override
    public void initData(Bundle savedInstanceState) {

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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


}
