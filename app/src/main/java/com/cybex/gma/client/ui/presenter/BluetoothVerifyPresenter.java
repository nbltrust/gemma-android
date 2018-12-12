package com.cybex.gma.client.ui.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.cybex.componentservice.api.callback.CustomRequestCallback;
import com.cybex.componentservice.api.data.response.CustomData;
import com.cybex.componentservice.config.BaseConst;
import com.cybex.componentservice.config.CacheConstants;
import com.cybex.componentservice.db.entity.EosWalletEntity;
import com.cybex.componentservice.db.entity.EthWalletEntity;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.db.entity.WalletEntity;
import com.cybex.componentservice.db.util.DBCallback;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.HttpConst;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.job.TimeStampValidateJob;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.JNIUtil;
import com.cybex.gma.client.ui.fragment.BluetoothVerifyMneFragment;
import com.cybex.gma.client.ui.model.request.BluetoothCreateAccountReqParams;
import com.cybex.gma.client.ui.model.response.UserRegisterResult;
import com.cybex.gma.client.ui.request.BluetoothAccountRegisterRequest;
import com.hxlx.core.lib.mvp.lite.XPresenter;
import com.hxlx.core.lib.utils.GsonUtils;
import com.hxlx.core.lib.utils.common.utils.AppManager;
import com.hxlx.core.lib.utils.common.utils.HashGenUtil;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import seed39.Seed39;

/**
 * Created by wanglin on 2018/9/18.
 */
public class BluetoothVerifyPresenter extends XPresenter<BluetoothVerifyMneFragment> {


    /**
     * 蓝牙钱包账户创建请求
     *
     * @param account_name
     * @param SN
     * @param SN_sig
     * @param public_key
     * @param public_key_sig
     */
//    public void doAccountRegisterRequest(
//            String account_name,
//            String SN, String SN_sig,
//            String public_key,
//            String publick_key_hex,
//            String public_key_sig,
//            String password,
//            String password_tip, Bundle bd) {
//        BluetoothCreateAccountReqParams params = new BluetoothCreateAccountReqParams();
//
//        params.setApp_id(ParamConstants.TYPE_APP_ID_BLUETOOTH);
//        params.setAccount_name(account_name);
//        params.setPublic_key(public_key);
//
//        BluetoothCreateAccountReqParams.WookongValidation validation = new BluetoothCreateAccountReqParams
//                .WookongValidation();
//        validation.setSN(SN.toLowerCase());
//        validation.setSN_sig(SN_sig.toLowerCase());
//        validation.setPublic_key(publick_key_hex + "00");
//        validation.setPublic_key_sig(public_key_sig);
//        params.setValidation(validation);
//
//        String json = GsonUtils.objectToJson(params);
//        LoggerManager.d("Bluetooth Create Params", json);
//
//        new BluetoothAccountRegisterRequest(UserRegisterResult.class)
//                .setJsonParams(json)
//                .postJson(new CustomRequestCallback<UserRegisterResult>() {
//                    @Override
//                    public void onBeforeRequest(@NonNull Disposable disposable) {
//                        getV().showProgressDialog("正在创建账户...");
//                    }
//
//                    @Override
//                    public void onSuccess(@NonNull CustomData<UserRegisterResult> data) {
//                        if (getV() != null) {
//                            getV().dissmisProgressDialog();
//
//                            if (data.code == HttpConst.CODE_RESULT_SUCCESS) {
//                                UserRegisterResult registerResult = data.result;
//                                if (registerResult != null) {
//                                    String txId = registerResult.txId;
//                                    saveAccount(public_key, public_key_sig, password, account_name, password_tip,
//                                     txId, SN);
//                                    TimeStampValidateJob.executedCreateLogic(account_name, public_key);
//                                    AppManager.getAppManager().finishAllActivity();
//                                    UISkipMananger.skipBluetoothSettingFPActivity(getV().getActivity(), bd);
//                                }
//                            } else {
//                                showOnErrorInfo(data.code);
//                            }
//                        }
//
//
//                    }
//
//                    @Override
//                    public void onError(@NonNull Throwable e) {
//                        getV().dissmisProgressDialog();
//
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        getV().dissmisProgressDialog();
//                    }
//                });
//
//
//    }


    /**
     * 调用DB Manager将钱包信息存入表中
     *
     * @param publicKey
     * @param publickey_sign
     * @param password
     * @param eosUsername
     * @param passwordTip
     */

    //todo 更改存储逻辑
    public void saveAccount(
            final String publicKey, final String publickey_sign, final String
            password, final String eosUsername, final String passwordTip, final String txId, final String SN) {


        WalletEntity walletEntity = new WalletEntity();
        List<WalletEntity> walletEntityList = DBManager.getInstance().getWalletEntityDao().getWalletEntityList();
        //获取当前数据库中已存入的钱包个数
        int walletNum = walletEntityList.size();
        int index = walletNum + 1;
        //以默认钱包名称存入
        walletEntity.setWalletName(CacheConstants.DEFAULT_BLUETOOTH_WALLET_PREFIX + String.valueOf(index));
        //设置公钥
        walletEntity.setPublicKey(publicKey);
        //设置摘要
        walletEntity.setCypher(publickey_sign);
        //设置eosNameJson
        List<String> account_names = new ArrayList<>();
        account_names.add(eosUsername);
        final String eosNameJson = GsonUtils.objectToJson(account_names);
        walletEntity.setEosNameJson(eosNameJson);
        //设置currentEosName，创建钱包步骤中可以直接设置，因为默认eosNameJson中只会有一个用户名字符串
        walletEntity.setCurrentEosName(eosUsername);
        //设置是否为当前钱包，默认新建钱包为当前钱包
        walletEntity.setIsCurrentWallet(CacheConstants.IS_CURRENT_WALLET);
        //设置密码提示
        walletEntity.setPasswordTip(passwordTip);
        //设置为未备份
        walletEntity.setIsBackUp(CacheConstants.NOT_BACKUP);
        //设置被链上确认状态位未被确认
       // walletEntity.setIsConfirmLib(CacheConstants.NOT_CONFIRMED);
        //蓝牙设备类型
        walletEntity.setWalletType(CacheConstants.WALLET_TYPE_BLUETOOTH);
        //设置当前Transaction的Hash值
        walletEntity.setTxId(txId);
        //设置邀请码
        walletEntity.setInvCode(SN);
        //执行存入操作之前需要把其他钱包设置为非当前钱包
        if (walletNum > 0) {
            WalletEntity curWallet = DBManager.getInstance().getWalletEntityDao().getCurrentWalletEntity();
            curWallet.setIsCurrentWallet(CacheConstants.NOT_CURRENT_WALLET);
            DBManager.getInstance().getWalletEntityDao().saveOrUpateEntity(curWallet);
        }
        //最后执行存入操作，此前包此时为当前钱包
        DBManager.getInstance().getWalletEntityDao().saveOrUpateEntity(walletEntity);
    }





    /**
     * 根据返回值不同Toast不同内容
     *
     * @param errorCode
     */
    public void showOnErrorInfo(int errorCode) {

        switch (errorCode) {
            case (HttpConst.INVCODE_USED):
                GemmaToastUtils.showLongToast(getV().getResources().getString(R.string.eos_invCode_used));
                break;
            case (HttpConst.INVCODE_NOTEXIST):
                GemmaToastUtils.showLongToast(getV().getResources().getString(R.string.eos_invCode_not_exist));
                break;
            case (HttpConst.EOSNAME_USED):
                GemmaToastUtils.showLongToast(getV().getResources().getString(R.string.eos_name_used));
                break;
            case (HttpConst.EOSNAME_INVALID):
                GemmaToastUtils.showLongToast(getV().getResources().getString(R.string.eos_name_invalid));
                break;
            case (HttpConst.EOSNAME_LENGTH_INVALID):
                GemmaToastUtils.showLongToast(getV().getResources().getString(R.string.eos_name_len_invalid));
                break;
            case (HttpConst.PARAMETERS_INVALID):
                GemmaToastUtils.showLongToast(getV().getResources().getString(R.string.eos_params_invalid));
                break;
            case (HttpConst.PUBLICKEY_INVALID):
                GemmaToastUtils.showLongToast(getV().getResources().getString(R.string.eos_pubKey_invalid));
                break;
            case (HttpConst.BALANCE_NOT_ENOUGH):
                GemmaToastUtils.showLongToast(getV().getResources().getString(R.string.eos_no_balance));
                break;
            case (HttpConst.CREATE_ACCOUNT_FAIL):
                GemmaToastUtils.showLongToast(getV().getResources().getString(R.string.eos_default_create_fail_info));
                break;
            default:
                GemmaToastUtils.showLongToast(getV().getResources().getString(R.string.eos_default_create_fail_info));
                break;
        }


    }


}
