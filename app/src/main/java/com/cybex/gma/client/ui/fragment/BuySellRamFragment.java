package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.text.Editable;
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
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.db.entity.WalletEntity;
import com.cybex.gma.client.manager.DBManager;
import com.cybex.gma.client.manager.LoggerManager;
import com.cybex.gma.client.ui.JNIUtil;
import com.cybex.gma.client.ui.model.vo.TabTitleBuyRamVO;
import com.cybex.gma.client.ui.model.vo.TabTitleSellRamVO;
import com.cybex.gma.client.ui.presenter.BuySellRamPresenter;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
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

    private List<String> ramMarketStaus;
    private final int OPERATION_BUY_RAM = 1;
    private final int OPERATION_SELL_RAM = 2;
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

    private final String testPrikey = "5KhjpbahW1ahQHi5GeW8baTwFx3n7W249gEp8xRHMJ45AVGeT58";
    private final String testEosName = "test1";
    private final String testQuantity = "0.0100 EOS";


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
        if (EmptyUtils.isNotEmpty(getEOSAmount())){
            setClickable(btBuyRam);
            //String amount = getP().calculateApproxiValue(ramMarketStaus, getEOSAmount());
            tvApproximatelyAmount.setText("≈ " + "0.0010" + " KB");
            tvApproximatelyAmount.setVisibility(View.VISIBLE);
        }else{
            tvApproximatelyAmount.setVisibility(View.GONE);
            setUnclickable(btBuyRam);
        }
    }

    @OnTextChanged(value = R.id.edt_sell_ram, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onSellChanged(Editable s){
        if (EmptyUtils.isNotEmpty(getRamAmount())){
            setClickable(btSellRam);

        }else{
            setUnclickable(btSellRam);
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
        setUnclickable(btSellRam);
        setUnclickable(btBuyRam);
        ramMarketStaus = getP().getRamMarketInfo();

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
                    edtBuyRam.setVisibility(View.VISIBLE);
                    edtSellRam.setVisibility(View.GONE);
                    tvEosRamAmount.setText(getResources().getString(R.string.transfer_eos_amount));

                    //tvAvaEosRam.setText(String.format(getResources().getString(R.string.available_eos), ));
                } else if (position == 1) {
                    btBuyRam.setVisibility(View.GONE);
                    btSellRam.setVisibility(View.VISIBLE);
                    edtBuyRam.setVisibility(View.GONE);
                    edtSellRam.setVisibility(View.VISIBLE);
                    tvEosRamAmount.setText(getResources().getString(R.string.transfer_ram_amount));
                    //tvAvaEosRam.setText(String.format(getResources().getString(R.string.available_ram), ));
                }
            }

            @Override
            public void onTabReselect(int position) {

            }
        });

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
        ramMarketStaus = getP().getRamMarketInfo();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public String getEOSAmount() {
        return edtBuyRam.getText().toString().trim();
    }
    public String getRamAmount(){
        return edtSellRam.getText().toString().trim();
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
     * 初始化Progress样式
     * 85%progress以上要用红色显示
     * @param progress
     */
    public void initProgressBar(float progress){
        RoundCornerProgressBar progressBar = progressbarRam;
        if (progress >= ParamConstants.PROGRESS_ALERT){
            //显示值>=85%
            progressBar.setProgressColor(getResources().getColor(R.color.scarlet));
            progressBar.setProgress(progress);
            superTextViewRamStatus.setLeftIcon(getResources().getDrawable(R.drawable.ic_dot_red));
        }else{
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
                        switch (operation_type){
                            case OPERATION_BUY_RAM:
                                //买入RAM操作
                                if (EmptyUtils.isNotEmpty(curWallet)){
                                    final String cypher = curWallet.getCypher();
                                    EditText mPass = dialog.findViewById(R.id.et_password);
                                    String inputPass = mPass.getText().toString().trim();
                                    final String key = JNIUtil.get_private_key(cypher, inputPass);
                                    final String curEOSName = curWallet.getCurrentEosName();
                                    String quantity = getEOSAmount() + " EOS";
                                    //getP().executeBuyRamLogic(curEOSName, curEOSName, quantity, key);
                                }
                                break;
                                //卖出RAM操作
                            case OPERATION_SELL_RAM:
                                if (EmptyUtils.isNotEmpty(curWallet)){
                                    final String cypher = curWallet.getCypher();
                                    EditText mPass = dialog.findViewById(R.id.et_password);
                                    String inputPass = mPass.getText().toString().trim();
                                    final String key = JNIUtil.get_private_key(cypher, inputPass);
                                    final String curEOSName = curWallet.getCurrentEosName();
                                    long ramAmount = Long.parseLong(getRamAmount());
                                    long bytes = ramAmount * 1024;
                                    //getP().executeSellRamLogic(curEOSName, bytes,key);
                                }
                                break;
                            default:
                                LoggerManager.d("参数错误");
                        }
                        //getP().executeBuyRamLogic(testEosName, testEosName, testQuantity, testPrikey);
                        break;
                    default:
                        break;
                }
            }
        });
        dialog.show();
    }



}
