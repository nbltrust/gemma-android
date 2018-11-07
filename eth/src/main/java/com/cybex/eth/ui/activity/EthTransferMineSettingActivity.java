package com.cybex.eth.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cybex.base.view.LabelLayout;
import com.cybex.componentservice.bean.TokenBean;
import com.cybex.componentservice.db.entity.EthWalletEntity;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.utils.FormatValidateUtils;
import com.cybex.componentservice.utils.PasswordValidateHelper;
import com.cybex.componentservice.widget.EditTextWithScrollView;
import com.cybex.eth.R;
import com.cybex.eth.event.GasSettingEvent;
import com.cybex.eth.ui.presenter.EthTransferPresenter;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;

import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;

import me.framework.fragmentation.anim.DefaultHorizontalAnimator;
import me.framework.fragmentation.anim.FragmentAnimator;


public class EthTransferMineSettingActivity extends XActivity {

    private TextView tvGas;
    private LinearLayout viewGas;
    private EditText etGas;
    private ImageView ivGasClear;
    private TextView tvGasprice;
    private EditText etGasprice;
    private ImageView ivGaspriceClear;


    @Override
    public void bindUI(View view) {
        setNavibarTitle(getString(R.string.eth_mine_setting_title), true);

        tvGas = (TextView) findViewById(R.id.tv_gas);
        viewGas = (LinearLayout) findViewById(R.id.view_gas);
        etGas = (EditText) findViewById(R.id.et_gas);
        ivGasClear = (ImageView) findViewById(R.id.iv_gas_clear);
        tvGasprice = (TextView) findViewById(R.id.tv_gasprice);
        etGasprice = (EditText) findViewById(R.id.et_gasprice);
        ivGaspriceClear = (ImageView) findViewById(R.id.iv_gasprice_clear);

        mTitleBar.addAction(new TitleBar.TextAction(getString(R.string.save)) {
            @Override
            public void performAction(View view) {

                try {
                    final String gas = etGas.getText().toString().trim();

                    long formatGas = Math.round(Double.parseDouble(gas));

                    final String gasPrice = etGasprice.getText().toString().trim();

                    long formatGasPrice = Math.round(Double.parseDouble(gasPrice));

                    if(formatGas>0&&formatGasPrice>0){

                        GasSettingEvent gasSettingEvent = new GasSettingEvent(formatGas, formatGasPrice);
                        EventBusProvider.post(gasSettingEvent);
                        finish();
                    }else{
                        GemmaToastUtils.showLongToast("数值不符合要求");
                    }
                }catch (NumberFormatException e){
                    GemmaToastUtils.showLongToast("数值不符合要求");
                }
            }
        });
    }

    @Override
    public void initData(Bundle savedInstanceState) {

        etGas.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (EmptyUtils.isNotEmpty(etGas.getText().toString().trim())) {
                    ivGasClear.setVisibility(View.VISIBLE);
                } else {
                    ivGasClear.setVisibility(View.GONE);
                }
            }
        });

        etGasprice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (EmptyUtils.isNotEmpty(etGasprice.getText().toString().trim())) {
                    ivGaspriceClear.setVisibility(View.VISIBLE);
                } else {
                    ivGaspriceClear.setVisibility(View.GONE);
                }
            }
        });

        ivGasClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etGas.setText("");
            }
        });

        ivGaspriceClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etGasprice.setText("");
            }
        });
    }



    @Override
    public int getLayoutId() {
        return R.layout.eth_activity_transfer_mine_setting;
    }

    @Override
    public Object newP() {
        return null;
    }


    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        // 设置横向(和安卓4.x动画相同)
        return new DefaultHorizontalAnimator();
    }


}
