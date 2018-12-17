package com.cybex.gma.client.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.cybex.componentservice.config.BaseConst;
import com.cybex.componentservice.config.CacheConstants;
import com.cybex.componentservice.config.RouterConst;
import com.cybex.componentservice.db.entity.EosWalletEntity;
import com.cybex.componentservice.db.entity.EthWalletEntity;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.db.util.DBCallback;
import com.cybex.componentservice.event.WookongInitialedEvent;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.DeviceOperationManager;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.componentservice.manager.PermissionManager;
import com.cybex.componentservice.utils.FormatValidateUtils;
import com.cybex.componentservice.utils.listener.PermissionResultListener;
import com.cybex.gma.client.BuildConfig;
import com.cybex.gma.client.R;
import com.cybex.gma.client.event.BarcodeScanEvent;
import com.cybex.gma.client.ui.JNIUtil;
import com.cybex.gma.client.ui.activity.BarcodeScanActivity;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import seed39.Seed39;

public class BluetoothImportMneFragment extends XFragment {

    Unbinder unbinder;
    Button btnNext;
    MaterialEditText edtShowMnemonic;


    public static BluetoothImportMneFragment newInstance() {
        Bundle args = new Bundle();
        BluetoothImportMneFragment fragment = new BluetoothImportMneFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
        setNavibarTitle(getResources().getString(R.string.eos_import_wallet), true, true);
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

        ImageView mCollectView = (ImageView) mTitleBar.addAction(new TitleBar.ImageAction(R.drawable.ic_notify_scan) {
            @Override
            public void performAction(View view) {
                skipBarcodeScan();
            }
        });
    }

    @Override
    public boolean useEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveBarcodeMessage(BarcodeScanEvent message) {
        if (!EmptyUtils.isEmpty(message)) {
            edtShowMnemonic.setText(message.getResult());
        }
    }


    private void skipBarcodeScan() {
        final PermissionManager manager = PermissionManager.getInstance(getActivity());
        manager.requestPermission(new PermissionResultListener() {
                                      @Override
                                      public void onPermissionGranted() {
                                          startActivity(new Intent(getContext(),BarcodeScanActivity.class));
                                      }

                                      @Override
                                      public void onPermissionDenied(List<String> permissions) {
                                          GemmaToastUtils.showShortToast(getResources().getString(R.string.walletmanage_set_camera_permission));
                                          if (AndPermission.hasAlwaysDeniedPermission(getActivity(), permissions)) {
                                              manager.showSettingDialog(getContext(), permissions);
                                          }

                                      }
                                  }, Permission.CAMERA
                , Permission.READ_EXTERNAL_STORAGE);
    }

    public void goConfigWallet() {
        //check mnemonic
        final String mnemonic = edtShowMnemonic.getText().toString().trim();
        if(FormatValidateUtils.isMnemonicValid(mnemonic)){
            showProgressDialog("");
            //go wookong check
            DeviceOperationManager.getInstance().importMnemonics(this.toString(), DeviceOperationManager.getInstance().getCurrentDeviceName(),
                    mnemonic, new DeviceOperationManager.ImportMnemonicCallback() {
                        @Override
                        public void onImportSuccess() {
//                            dissmisProgressDialog();
                            initWookongBioWallet(mnemonic);
                        }

                        @Override
                        public void onImportFailed() {
                            dissmisProgressDialog();
                            GemmaToastUtils.showShortToast(getString(R.string.eos_mnenonic_import_fail));
                        }
                    }

            );
        }else{
            GemmaToastUtils.showShortToast(getString(R.string.eos_mnenonic_invalid));
        }
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setButtonUnclickable(btnNext);
    }

    @Override
    public int getLayoutId() {
        return R.layout.eos_fragment_bluetooth_import_mne;
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
        DeviceOperationManager.getInstance().clearCallback(this.toString());
        super.onDestroyView();
        unbinder.unbind();
    }

    public void setButtonUnclickable(Button button) {
        button.setClickable(false);
        button.setBackground(getResources().getDrawable(R.drawable.shape_corner_button_unclickable));
    }

    public void setButtonClickable(Button button) {
        button.setClickable(true);
        button.setBackground(getResources().getDrawable(R.drawable.shape_corner_button));
    }

    private void initWookongBioWallet(final String mnemonics) {
        showProgressDialog("");
        Disposable subscribe = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> emitter) throws Exception {


                MultiWalletEntity multiWalletEntity = new MultiWalletEntity();

                multiWalletEntity.setWalletName("WOOKONG Bio");
                multiWalletEntity.setWalletType(MultiWalletEntity.WALLET_TYPE_HARDWARE);
                multiWalletEntity.setIsBackUp(CacheConstants.ALREADY_BACKUP);
                multiWalletEntity.setPasswordTip(DeviceOperationManager.getInstance().getCurrentDeviceInitPswHint());
                multiWalletEntity.setIsCurrentWallet(1);
                multiWalletEntity.setBluetoothDeviceName(DeviceOperationManager.getInstance().getCurrentDeviceName());

                //testcode
                final String mnemonic = mnemonics;
//                String encryptMnemonic = Seed39.keyEncrypt(password, mnemonic);
//                multiWalletEntity.setMnemonic(encryptMnemonic);

                String seed = Seed39.seedByMnemonic(mnemonic);
                String privKey = Seed39.deriveRaw(seed, BaseConst.MNEMONIC_PATH_ETH);
                String publicKey = Seed39.getEthereumPublicKeyFromPrivateKey(privKey);
                String address = Seed39.getEthereumAddressFromPrivateKey(privKey);
                String eosPriv = Seed39.deriveWIF(seed, BaseConst.MNEMONIC_PATH_EOS, false);
//                String eosPrivCompress = Seed39.deriveWIF(seed, "m/44'/194'/0'/0/", true);
                LoggerManager.d(" mnemonic=" + mnemonic);
                LoggerManager.d(" seed=" + seed);
                LoggerManager.d(" privKey=" + privKey);
                LoggerManager.d(" publicKey=" + publicKey);
                LoggerManager.d(" address=" + address);
                LoggerManager.d(" eosPriv=" + eosPriv);
//                LoggerManager.d(" eosPrivCompress="+eosPrivCompress);

                EthWalletEntity ethWalletEntity = new EthWalletEntity();
                ethWalletEntity.setAddress(address);
//                ethWalletEntity.setPrivateKey(Seed39.keyEncrypt(password, privKey));
                ethWalletEntity.setPublicKey(publicKey);
                ethWalletEntity.setBackUp(true);
                ethWalletEntity.setMultiWalletEntity(multiWalletEntity);

                List<EthWalletEntity> ethWalletEntities = new ArrayList<>();
                ethWalletEntities.add(ethWalletEntity);
                multiWalletEntity.setEthWalletEntities(ethWalletEntities);

                String eosPublic = JNIUtil.get_public_key(eosPriv);
                LoggerManager.d(" eosPublic=" + eosPublic);
                EosWalletEntity eosWalletEntity = new EosWalletEntity();
//                eosWalletEntity.setPrivateKey(Seed39.keyEncrypt(password, eosPriv));
                eosWalletEntity.setPublicKey(eosPublic);
                eosWalletEntity.setIsBackUp(1);
                eosWalletEntity.setMultiWalletEntity(multiWalletEntity);
                List<EosWalletEntity> eosWalletEntities = new ArrayList<>();
                eosWalletEntities.add(eosWalletEntity);
                multiWalletEntity.setEosWalletEntities(eosWalletEntities);

//                LoggerManager.d("descrypt mnemonic=" + Seed39.keyDecrypt(password, encryptMnemonic));

                List<MultiWalletEntity> multiWalletEntityList = DBManager.getInstance().getMultiWalletEntityDao().getMultiWalletEntityList();
                int walletCount = multiWalletEntityList.size();
                if (walletCount > 0) {
                    MultiWalletEntity currentMultiWalletEntity = DBManager.getInstance().getMultiWalletEntityDao().getCurrentMultiWalletEntity();
                    if (currentMultiWalletEntity != null) {
                        currentMultiWalletEntity.setIsCurrentWallet(CacheConstants.NOT_CURRENT_WALLET);
                        currentMultiWalletEntity.save();
                    }
                }
                DBManager.getInstance().getMultiWalletEntityDao().saveOrUpateEntity(multiWalletEntity, new DBCallback() {
                    @Override
                    public void onSucceed() {
                        if (BuildConfig.DEBUG) {
                            List<MultiWalletEntity> list =
                                    SQLite.select().from(MultiWalletEntity.class)
                                            .queryList();
                            LoggerManager.d("list=" + list);
                        }
                        emitter.onNext(mnemonic);
                        emitter.onComplete();
                    }

                    @Override
                    public void onFailed(Throwable error) {
                        emitter.onError(error);
                        emitter.onComplete();
                    }
                });
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindUntilEvent(FragmentEvent.DESTROY))
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String mnemonic) {
                        dissmisProgressDialog();
//                        ARouter.getInstance().build(RouterConst.PATH_TO_WALLET_ENROOL_FP_PAGE)
//                                .withInt(BaseConst.KEY_INIT_TYPE, 0)
//                                .withFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                                .navigation();

                        ARouter.getInstance().build(RouterConst.PATH_TO_WALLET_HOME)
                                .withInt(BaseConst.KEY_INIT_TYPE, BaseConst.APP_HOME_INITTYPE_TO_ENROLL_FP)
//                                .withFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                .navigation();
                        if(DBManager.getInstance().getMultiWalletEntityDao().getBluetoothWalletList().size()==1){
                            EventBusProvider.post(new WookongInitialedEvent());
                        }else{

                        }

                        getActivity().finish();
                        //                                        doGetAddressLogic();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        dissmisProgressDialog();
                        GemmaToastUtils.showLongToast(getString(R.string.wookong_init_fail));
                        getActivity().finish();
                    }
                });

    }

}
