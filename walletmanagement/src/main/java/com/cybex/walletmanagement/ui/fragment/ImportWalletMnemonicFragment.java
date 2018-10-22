package com.cybex.walletmanagement.ui.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

import com.cybex.walletmanagement.R;
import com.cybex.walletmanagement.event.BarcodeScanEvent;
import com.cybex.walletmanagement.event.QrResultEvent;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.EmptyUtils;
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
//        final String inputKey = edtShowMnemonic.getText().toString().trim();
//        final String key = JNIUtil.get_public_key(inputKey);

//        if (key.equals("invalid priv string!")) {
//            GemmaToastUtils.showLongToast(getResources().getString(R.string.eos_prikey_format_invalid));
//        } else {
//            start(ImportWalletConfigFragment.newInstance(inputKey));
//        }

        //check mnemonic
        final String mnemonic = edtShowMnemonic.getText().toString().trim();
        if(isMnemonicValid(mnemonic)){
//            start(ImportWalletConfigFragment.newInstance(inputKey));
        }else{
//            GemmaToastUtils.showLongToast(getResources().getString(R.string.eos_prikey_format_invalid));

        }
    }

    private boolean isMnemonicValid(String mnemonic) {
        return false;
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
