package com.cybex.walletmanagement.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.utils.FormatValidateUtils;
import com.cybex.walletmanagement.R;
import com.cybex.walletmanagement.config.WalletManageConst;
import com.cybex.walletmanagement.event.BarcodeScanEvent;
import com.cybex.walletmanagement.event.QrResultEvent;
import com.cybex.walletmanagement.ui.activity.ConfigNewWalletActivity;
import com.cybex.walletmanagement.ui.model.ImportWalletConfigBean;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * 导入钱包输入私钥界面
 */

public class ImportWalletMnemonicFragment extends XFragment {

    Button btnNext;
    MaterialEditText edtShowMnemonic;

    public static ImportWalletMnemonicFragment newInstance() {
        Bundle args = new Bundle();
        ImportWalletMnemonicFragment fragment = new ImportWalletMnemonicFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public boolean useEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showMnemonic(QrResultEvent message) {
        if(!message.isMnemonicType)return;
        if (!EmptyUtils.isEmpty(message)) {
            edtShowMnemonic.setText(message.getResult());
        }
    }

    @Override
    public void bindUI(View rootView) {
        edtShowMnemonic = rootView.findViewById(R.id.edt_show_mnemonic);
        btnNext = rootView.findViewById(R.id.bt_next_step);
        edtShowMnemonic.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (EmptyUtils.isEmpty(edtShowMnemonic.getText().toString().trim())) {
                    setButtonUnclickable(btnNext);
                } else {
                    setButtonClickable(btnNext);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goConfigWallet();
            }
        });
    }

    public void goConfigWallet() {
        //check mnemonic
        final String mnemonic = edtShowMnemonic.getText().toString().trim();
        if(FormatValidateUtils.isMnemonicValid(mnemonic)){
            //jump to config page
            ImportWalletConfigBean config = new ImportWalletConfigBean();
            config.setWalletType(MultiWalletEntity.WALLET_TYPE_IMPORT_MNEMONIC);
            config.setMnemonic(mnemonic);

            Intent intent = new Intent(getContext(), ConfigNewWalletActivity.class);
            intent.putExtra(WalletManageConst.KEY_IMPORT_WALLET_CONFIG,config);
            startActivity(intent);
            getActivity().finish();
        }else{
            GemmaToastUtils.showShortToast(getResources().getString(R.string.walletmanage_import_mnemonic_invalid));

        }
    }



    @Override
    public void initData(Bundle savedInstanceState) {
        setButtonUnclickable(btnNext);
    }

    @Override
    public int getLayoutId() {
        return R.layout.walletmanage_fragment_import_wallet_mnemonic;
    }

    @Override
    public Object newP() {
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void setButtonUnclickable(Button button) {
        button.setClickable(false);
        button.setBackground(getResources().getDrawable(R.drawable.shape_corner_button_unclickable));
    }

    public void setButtonClickable(Button button) {
        button.setClickable(true);
        button.setBackground(getResources().getDrawable(R.drawable.shape_corner_button));
    }


}
