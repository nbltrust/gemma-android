package com.cybex.walletmanagement.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

import com.alibaba.android.arouter.launcher.ARouter;
import com.cybex.base.view.LabelLayout;
import com.cybex.componentservice.bean.CoinType;
import com.cybex.componentservice.config.RouterConst;
import com.cybex.componentservice.db.entity.EosWalletEntity;
import com.cybex.componentservice.db.entity.EthWalletEntity;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.db.util.DBCallback;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.utils.PasswordValidateHelper;
import com.cybex.gma.client.ui.JNIUtil;
import com.cybex.walletmanagement.R;
import com.cybex.walletmanagement.config.WalletManageConst;
import com.cybex.walletmanagement.event.QrResultEvent;
import com.cybex.walletmanagement.event.SelectCoinTypeEvent;
import com.cybex.walletmanagement.event.SelectImportWhichWalletEvent;
import com.cybex.walletmanagement.ui.activity.ConfigNewWalletActivity;
import com.cybex.walletmanagement.ui.activity.SelectImportWhichWalletActivity;
import com.cybex.walletmanagement.ui.activity.SelectWalletCoinTypeActivity;
import com.cybex.walletmanagement.ui.model.ImportWalletConfigBean;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.common.utils.AppManager;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import seed39.Seed39;


/**
 * 导入钱包输入私钥界面
 */

public class ImportWalletPrivateKeyFragment extends XFragment {

    Button btnNext;
    MaterialEditText edtShowPrikey;
    LabelLayout labelCoinType;
    LabelLayout labelWhichWallet;
    private CoinType currentCoinType = CoinType.EOS;
    private MultiWalletEntity importWallet = null;
    private List<MultiWalletEntity> wallets;


    public static ImportWalletPrivateKeyFragment newInstance() {
        Bundle args = new Bundle();
        ImportWalletPrivateKeyFragment fragment = new ImportWalletPrivateKeyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public boolean useEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showPriKey(QrResultEvent message) {
        if (message.isMnemonicType) { return; }
        if (!EmptyUtils.isEmpty(message)) {
            edtShowPrikey.setText(message.getResult());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changeCoinType(SelectCoinTypeEvent message) {
        if (!EmptyUtils.isEmpty(message)) {
            labelCoinType.setRightText(message.cointype.coinName);
            currentCoinType = message.cointype;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changeImportWallet(SelectImportWhichWalletEvent message) {
        if (!EmptyUtils.isEmpty(message) && message.walletEntity != null) {
            labelWhichWallet.setRightText(message.walletEntity.getWalletName());
            importWallet = message.walletEntity;
        } else {
            labelWhichWallet.setRightText(getString(R.string.walletmanage_create_new_wallet));
            importWallet = null;
        }
    }

    @Override
    public void bindUI(View rootView) {
        edtShowPrikey = rootView.findViewById(R.id.edt_show_priKey);
        btnNext = rootView.findViewById(R.id.bt_next_step);
        labelCoinType = rootView.findViewById(R.id.label_coin_type);
        labelWhichWallet = rootView.findViewById(R.id.label_which_wallet);
        edtShowPrikey.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (EmptyUtils.isEmpty(edtShowPrikey.getText().toString().trim())) {
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
        labelCoinType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SelectWalletCoinTypeActivity.class);
                intent.putExtra(WalletManageConst.KEY_SELECT_COINTYPE, currentCoinType);
                startActivity(intent);
            }
        });
        labelWhichWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SelectImportWhichWalletActivity.class);
                intent.putExtra(WalletManageConst.KEY_SELECT_IMPORT_WHICH_WALLET, importWallet);
                startActivity(intent);
            }
        });

    }

    public void goConfigWallet() {
        final String priKey = edtShowPrikey.getText().toString().trim();
        //check priv key
        boolean isValid = true;
        if (currentCoinType == CoinType.EOS) {
            String publcKey = JNIUtil.get_public_key(priKey);
            if (publcKey.equals("invalid priv string!")) {
                //验证格式未通过
                isValid = false;
            }
        } else if (currentCoinType == CoinType.ETH) {
//            String seed = Seed39.seedByMnemonic("asdfas sdfsf");
//            LoggerManager.e("seed="+seed);
//
//            String publickey2 = Seed39.getEthereumPublicKeyFromPrivateKey("12323sadfasdf");
//            LoggerManager.e("publickey2="+publickey2);
//
//            isValid=false;

        }
        if (!isValid) {
            GemmaToastUtils.showShortToast(getString(R.string.walletmanage_import_pri_key_invalid));
            return;
        }

        //if import to exist wallet, need check password
        if (importWallet != null) {
            PasswordValidateHelper passwordValidateHelper = new PasswordValidateHelper(importWallet, context);
            passwordValidateHelper.setConfirmStr(getString(R.string.walletmanage_finish_import));
            if (currentCoinType == CoinType.EOS) {
                //check if exist eos wallet
                List<EosWalletEntity> eosWalletEntities = importWallet.getEosWalletEntities();
                if (eosWalletEntities.size() > 0) {
                    //show toast
                    GemmaToastUtils.showShortToast(getString(R.string.walletmanage_eos_account_exist));
                    return;
                }
                //check password
                passwordValidateHelper.startValidatePassword(new PasswordValidateHelper.PasswordValidateCallback() {
                    @Override
                    public void onValidateSuccess(String password) {

                        String eosPublic = JNIUtil.get_public_key(priKey);
                        EosWalletEntity eosWalletEntity = new EosWalletEntity();
                        eosWalletEntity.setPrivateKey(Seed39.keyEncrypt(password, priKey));
                        eosWalletEntity.setPublicKey(eosPublic);
                        eosWalletEntity.setIsBackUp(0);
                        eosWalletEntity.setMultiWalletEntity(importWallet);
                        importWallet.getEosWalletEntities().add(eosWalletEntity);

                        DBManager.getInstance()
                                .getMultiWalletEntityDao()
                                .saveOrUpateEntity(importWallet, new DBCallback() {
                                    @Override
                                    public void onSucceed() {
                                        GemmaToastUtils.showShortToast(getString(R.string.walletmanage_import_success));
                                        //if (getActivity() != null)getActivity().finish();
                                        AppManager.getAppManager().finishAllActivity();
                                        ARouter.getInstance().build(RouterConst.PATH_TO_WALLET_HOME)
                                                .navigation();
                                    }

                                    @Override
                                    public void onFailed(Throwable error) {
                                        GemmaToastUtils.showShortToast(getString(R.string.walletmanage_import_fail));
                                    }
                                });
                    }

                    @Override
                    public void onValidateFail(int failedCount) {

                    }
                });
            } else if (currentCoinType == CoinType.ETH) {
                //check if exist eth wallet
                List<EthWalletEntity> ethWalletEntities = importWallet.getEthWalletEntities();
                if (ethWalletEntities.size() > 0) {
                    //show toast
                    GemmaToastUtils.showShortToast(getString(R.string.walletmanage_eth_account_exist));
                    return;
                }
                //check password
                passwordValidateHelper.startValidatePassword(new PasswordValidateHelper.PasswordValidateCallback() {
                    @Override
                    public void onValidateSuccess(String password) {

                        String publicKey = Seed39.getEthereumPublicKeyFromPrivateKey(priKey);
                        String address = Seed39.getEthereumAddressFromPrivateKey(priKey);

                        EthWalletEntity ethWalletEntity = new EthWalletEntity();
                        ethWalletEntity.setAddress(address);
                        ethWalletEntity.setPrivateKey(Seed39.keyEncrypt(password, priKey));
                        ethWalletEntity.setPublicKey(publicKey);
                        ethWalletEntity.setBackUp(false);
                        ethWalletEntity.setMultiWalletEntity(importWallet);
                        importWallet.getEthWalletEntities().add(ethWalletEntity);

                        DBManager.getInstance()
                                .getMultiWalletEntityDao()
                                .saveOrUpateEntity(importWallet, new DBCallback() {
                                    @Override
                                    public void onSucceed() {
                                        GemmaToastUtils.showShortToast(getString(R.string.walletmanage_import_success));
                                        getActivity().finish();
                                    }

                                    @Override
                                    public void onFailed(Throwable error) {
                                        GemmaToastUtils.showShortToast(getString(R.string.walletmanage_import_fail));
                                    }
                                });
                    }

                    @Override
                    public void onValidateFail(int failedCount) {

                    }
                });
            }

            return;
        }
        //create new wallet
        //jump to config page
        ImportWalletConfigBean config = new ImportWalletConfigBean();
        config.setWalletType(MultiWalletEntity.WALLET_TYPE_PRI_KEY);
        config.setPriKey(priKey);
        config.setCoinType(currentCoinType);

        Intent intent = new Intent(getContext(), ConfigNewWalletActivity.class);
        intent.putExtra(WalletManageConst.KEY_IMPORT_WALLET_CONFIG, config);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setButtonUnclickable(btnNext);
        wallets = DBManager.getInstance()
                .getMultiWalletEntityDao()
                .getMultiWalletEntityListByWalletType(MultiWalletEntity.WALLET_TYPE_PRI_KEY);

        if (wallets.size() == 0) {
            labelWhichWallet.setVisibility(View.GONE);
        }

    }

    @Override
    public int getLayoutId() {
        return R.layout.walletmanage_fragment_import_wallet_pri_key;
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
