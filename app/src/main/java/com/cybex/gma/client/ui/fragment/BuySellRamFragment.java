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
import com.cybex.gma.client.ui.model.vo.TabTitleBuyRamVO;
import com.cybex.gma.client.ui.model.vo.TabTitleSellRamVO;
import com.cybex.gma.client.ui.presenter.BuySellRamPresenter;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.siberiadante.customdialoglib.CustomFullDialog;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;


/**
 * 买卖RAM Fragment
 */
public class BuySellRamFragment extends XFragment<BuySellRamPresenter> {

    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.superTextView_ram_amount) SuperTextView superTextViewRamAmount;
    @BindView(R.id.progressbar_ram) RoundCornerProgressBar progressbarRam;
    @BindView(R.id.superTextView_ram_status) SuperTextView superTextViewRamStatus;
    @BindView(R.id.CTL_buy_sell_ram) CommonTabLayout mTab;
    @BindView(R.id.edt_buy_sell_ram) EditText edtBuySellRam;
    @BindView(R.id.bt_buy_ram) Button btBuyRam;
    @BindView(R.id.bt_sell_ram) Button btSellRam;
    @BindView(R.id.tv_approximately_amount) TextView tvApproximatelyAmount;

    @OnClick(R.id.bt_buy_ram)
    public void showBuyDialog() {
        showConfirmBuyRamDialog();
    }

    @OnClick(R.id.bt_sell_ram)
    public void showSellDialog() {
        showConfirmSellRamDialog();
    }

    @OnTextChanged(value = R.id.edt_buy_sell_ram, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onChanged() {
        if (EmptyUtils.isNotEmpty(edtBuySellRam.getText().toString().trim())) {
            btBuyRam.setBackground(getResources().getDrawable(R.drawable.shape_corner_button));
            btSellRam.setBackground(getResources().getDrawable(R.drawable.shape_corner_button));
            tvApproximatelyAmount.setVisibility(View.VISIBLE);
        }else{
            tvApproximatelyAmount.setVisibility(View.GONE);
        }
    }

    public static BuySellRamFragment newInstance() {
        Bundle args = new Bundle();
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
        setNavibarTitle("买卖RAM", true, true);
        tvApproximatelyAmount.setVisibility(View.GONE);
        btBuyRam.setBackground(getResources().getDrawable(R.drawable.shape_corner_button_unclickable));
        btSellRam.setBackground(getResources().getDrawable(R.drawable.shape_corner_button_unclickable));

        ArrayList<CustomTabEntity> list = new ArrayList<CustomTabEntity>();
        list.add(new TabTitleBuyRamVO());
        list.add(new TabTitleSellRamVO());
        mTab.setTabData(list);
        mTab.setCurrentTab(0);

        mTab.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                if (position == 0) {
                    btBuyRam.setVisibility(View.VISIBLE);
                    btSellRam.setVisibility(View.GONE);
                } else if (position == 1) {
                    btBuyRam.setVisibility(View.GONE);
                    btSellRam.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
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
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public String getEOSAmount() {
        return edtBuySellRam.getText().toString().trim();
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
