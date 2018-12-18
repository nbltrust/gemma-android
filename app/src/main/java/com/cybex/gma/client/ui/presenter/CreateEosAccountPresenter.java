package com.cybex.gma.client.ui.presenter;

import android.os.Bundle;

import com.cybex.componentservice.api.callback.JsonCallback;
import com.cybex.componentservice.config.BaseConst;
import com.cybex.componentservice.config.CacheConstants;
import com.cybex.componentservice.db.entity.EosWalletEntity;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.DeviceOperationManager;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.componentservice.utils.AlertUtil;
import com.cybex.componentservice.utils.ConvertUtils;
import com.cybex.componentservice.utils.WookongConnectHelper;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.HttpConst;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.event.ActionIdEvent;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.activity.CreateEosAccountActivity;
import com.cybex.gma.client.ui.model.request.BluetoothCreateAccountReqParams;
import com.cybex.gma.client.ui.model.request.GetAccountReqParams;
import com.cybex.gma.client.ui.model.response.AccountInfo;
import com.cybex.gma.client.ui.model.response.UserRegisterResult;
import com.cybex.gma.client.ui.request.BluetoothAccountRegisterRequest;
import com.cybex.gma.client.ui.request.GetAccountinfoRequest;
import com.extropies.common.CommonUtility;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.mvp.lite.XPresenter;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.GsonUtils;
import com.hxlx.core.lib.utils.common.utils.AppManager;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import java.util.ArrayList;
import java.util.List;

public class CreateEosAccountPresenter extends XPresenter<CreateEosAccountActivity> {

    MultiWalletEntity curWallet;

    public void verifyAccount(String account_name) {
        GetAccountReqParams params = new GetAccountReqParams();
        params.setAccount_name(account_name);

        String jsonParams = GsonUtils.objectToJson(params);

        new GetAccountinfoRequest(AccountInfo.class)
                .setJsonParams(jsonParams)
                .getAccountInfo(new JsonCallback<AccountInfo>() {
                    @Override
                    public void onStart(Request<AccountInfo, ? extends Request> request) {
                        super.onStart(request);
                        getV().showProgressDialog(getV().getString(R.string.eos_verifying_account));
                    }

                    @Override
                    public void onSuccess(Response<AccountInfo> response) {
                        getV().dissmisProgressDialog();
                        if (response != null && response.body() != null) {
                            AccountInfo accountInfo = response.body();
                            String account_name_onChain = accountInfo.getAccount_name();
                            if (account_name.equals(account_name_onChain)) {
                                //找到该用户名，该用户名不可以使用
                                AlertUtil.showLongUrgeAlert(getV(), getV().getString(R.string.eos_name_used));
                            }
                        }
                    }

                    @Override
                    public void onError(Response<AccountInfo> response) {
                        getV().dissmisProgressDialog();
                        super.onError(response);
                        if (response.code() == HttpConst.SERVER_INTERNAL_ERR) {
                            //未找到该用户名，该用户名可以使用
                            LoggerManager.d("Verify Err");
                            curWallet = DBManager.getInstance().getMultiWalletEntityDao()
                                    .getCurrentMultiWalletEntity();
                            if (curWallet != null) {
                                int walletType = curWallet.getWalletType();
                                if (walletType == BaseConst.WALLET_TYPE_BLUETOOTH) {
                                    //蓝牙钱包
                                    LoggerManager.d("Type Bluetooth");
                                    String deviceName = DBManager.getInstance().getMultiWalletEntityDao()
                                            .getCurrentMultiWalletEntity().getBluetoothDeviceName();
                                    int status = DeviceOperationManager.getInstance().getDeviceConnectStatus
                                            (deviceName);

                                    if (status == CacheConstants.STATUS_BLUETOOTH_CONNCETED) {
                                        //已连接
                                        getDeviceInfoAndRegister();

                                    } else {
                                        //连接蓝牙卡

                                        if (curWallet != null) {
                                            WookongConnectHelper wookongConnectHelper = new WookongConnectHelper(
                                                    getV().toString(), curWallet, getV());
                                            wookongConnectHelper.startConnectDevice(
                                                    new WookongConnectHelper.ConnectWookongBioCallback() {
                                                        @Override
                                                        public void onConnectSuccess() {
                                                            getDeviceInfoAndRegister();
                                                        }

                                                        @Override
                                                        public void onConnectFail() {
                                                            AlertUtil.showShortUrgeAlert(getV(), getV().getString(R
                                                                    .string.wookong_connect_fail));
                                                            getV().dissmisProgressDialog();
                                                        }
                                                    });
                                        }
                                    }


                                } else {
                                    //软钱包跳转到激活界面
                                    EosWalletEntity curEosWallet = getCurEosWallet();
                                    if (curEosWallet != null) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("account_name", getV().getEOSUsername());
                                        UISkipMananger.launchChooseActivateMethod(getV(), bundle);
                                    }
                                }

                            } else {
                                LoggerManager.d("curWallet Empty");
                            }


                        } else {
                            GemmaToastUtils.showLongToast(getV().getString(R.string.eos_tip_check_network));
                        }

                    }
                });

    }

    public EosWalletEntity getCurEosWallet() {
        MultiWalletEntity curWallet = DBManager.getInstance().getMultiWalletEntityDao().getCurrentMultiWalletEntity();
        if (curWallet != null && curWallet.getEosWalletEntities().size() > 0) {
            return curWallet.getEosWalletEntities().get(0);
        }
        return null;
    }


    public void getDeviceInfoAndRegister() {

        getV().showProgressDialog(getV().getString(R.string.baseservice_wookong_bio_connecting));

        if (getV() != null) {
            String device_name = DeviceOperationManager.getInstance().getCurrentDeviceName();
            String account_name = getV().getEOSUsername();
            String TAG = getV().toString();

            //getEosAddress
            DeviceOperationManager.getInstance().getEosAddress(
                    TAG,
                    device_name,
                    new DeviceOperationManager.GetAddressCallback() {
                        @Override
                        public void onGetSuccess(String address) {

                            String[] addressSpilt = address.split("####");
                            if (EmptyUtils.isNotEmpty(addressSpilt)) {

                                String public_key = addressSpilt[0];
                                String public_key_sign = addressSpilt[1];
                                String public_key_hex = ConvertUtils.str2HexStr(public_key);

                                LoggerManager.d("public_key: " + public_key);
                                LoggerManager.d("public_key_hex: " + public_key_hex);
                                LoggerManager.d("publick_key_sign: " + public_key_sign);


                                //Check Code
                                DeviceOperationManager.getInstance().getCheckCode(TAG,
                                        device_name,
                                        new DeviceOperationManager.GetCheckCodeCallback() {
                                            @Override
                                            public void onCheckCodeSuccess(byte[] checkedCode) {
                                                if (checkedCode != null) {
                                                    byte[] snbyte = ConvertUtils.subByte(
                                                            checkedCode, 0, 16);

                                                    String SN = CommonUtility.byte2hex(snbyte);
                                                    String SN_sign = CommonUtility.byte2hex(
                                                            checkedCode);
                                                    SN_sign = SN_sign.substring(32);

                                                    doAccountRegisterRequest(account_name, SN,
                                                            SN_sign, public_key, public_key_hex,
                                                            public_key_sign);
                                                }

                                            }

                                            @Override
                                            public void onCheckCodeFail() {
                                                getV().dissmisProgressDialog();
                                                AlertUtil.showShortUrgeAlert(getV(), "SN 签名失败，请重新尝试");
                                                LoggerManager.d("getCheckCode Fail");
                                            }
                                        });
                            }


                        }

                        @Override
                        public void onGetFail() {
                            getV().dissmisProgressDialog();
                            AlertUtil.showShortUrgeAlert(getV(), "读取EOS公钥失败，请重新尝试");
                        }
                    }
            );
        }
    }


    /**
     * 蓝牙钱包账户创建请求
     *
     * @param account_name
     * @param SN
     * @param SN_sig
     * @param public_key
     * @param public_key_sig
     */
    public void doAccountRegisterRequest(
            String account_name,
            String SN, String SN_sig,
            String public_key,
            String publick_key_hex,
            String public_key_sig) {
        BluetoothCreateAccountReqParams params = new BluetoothCreateAccountReqParams();

        params.setApp_id(ParamConstants.TYPE_APP_ID_BLUETOOTH);
        params.setGoods_id(ParamConstants.CODE_TYPE_SN);
        params.setAccount_name(account_name);
        params.setPublic_key(public_key);
        params.setCode(SN);

        BluetoothCreateAccountReqParams.WookongValidation validation = new BluetoothCreateAccountReqParams
                .WookongValidation();
        validation.setSN(SN.toLowerCase());
        validation.setSN_sig(SN_sig.toLowerCase());
        validation.setPublic_key(publick_key_hex + "00");
        validation.setPublic_key_sig(public_key_sig);
        params.setValidation(validation);

        String jsonParams = GsonUtils.objectToJson(params);
        LoggerManager.d("Bluetooth Create Params", jsonParams);


        new BluetoothAccountRegisterRequest(String.class)
                .setJsonParams(jsonParams)
                .registerAccount(new JsonCallback<UserRegisterResult>() {
                    @Override
                    public void onSuccess(Response<UserRegisterResult> response) {
                        if (getV() != null) {
                            if (response != null && response.body() != null) {
                                UserRegisterResult.ResultBean resultBean = response.body().getResult();
                                if (resultBean != null) {
                                    String action_id = resultBean.getAction_id();
                                    LoggerManager.d("action_id", action_id);

                                    ActionIdEvent event = new ActionIdEvent();
                                    event.setAction_id(action_id);
                                    EventBusProvider.postSticky(event);

                                    updateCurBluetoothWallet(account_name);
                                    AppManager.getAppManager().finishActivity();
                                    UISkipMananger.launchHome(getV());
                                    getV().dissmisProgressDialog();
                                } else if (response.body().getCode() == HttpConst.INVCODE_USED) {
                                    AppManager.getAppManager().finishActivity();
                                    Bundle bundle = new Bundle();
                                    String account_name = getV().getEOSUsername();
                                    bundle.putString(ParamConstants.EOS_USERNAME, account_name);
                                    UISkipMananger.launchChooseActivateMethod(getV(), bundle);
                                }else {
                                    showOnErrorInfo(response.body().getCode());
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Response<UserRegisterResult> response) {
                        if (getV() != null) {
                            super.onError(response);
                            getV().dissmisProgressDialog();
                        }
                    }
                });
    }


    /**
     * 创建成功后将相关信息填入数据库
     */
    private void updateCurBluetoothWallet(String account_name) {

        List<MultiWalletEntity> bluetoothWalletList = DBManager.getInstance().getMultiWalletEntityDao()
                .getBluetoothWalletList();
        if (bluetoothWalletList != null && bluetoothWalletList.size() > 0) {
            MultiWalletEntity curBluetoothWallet = bluetoothWalletList.get(0);
            if (curBluetoothWallet.getEosWalletEntities().size() > 0) {
                EosWalletEntity curEosWallet = curBluetoothWallet.getEosWalletEntities().get(0);
                curEosWallet.setCurrentEosName(account_name);
                List<String> account_names = new ArrayList<>();
                account_names.add(account_name);
                String jsonEosName = GsonUtils.objectToJson(account_names);
                curEosWallet.setIsConfirmLib(ParamConstants.EOSACCOUNT_CONFIRMING);
                curEosWallet.setEosNameJson(jsonEosName);
                curEosWallet.save();
                curBluetoothWallet.save();

            }
        }
    }

    /**
     * 根据返回值不同Toast不同内容
     *
     * @param errorCode
     */
    public void showOnErrorInfo(int errorCode) {

        switch (errorCode) {
            case (HttpConst.INVCODE_USED):
                GemmaToastUtils.showLongToast(getV().getResources().getString(R.string.eos_sn_used));
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
            case (HttpConst.PUBLICKEY_HEX_NOT_MATCH):
                GemmaToastUtils.showLongToast(getV().getResources().getString(R.string.key_sig_not_match));
                break;
            case (HttpConst.VERIFY_SIG_FAIL):
                GemmaToastUtils.showLongToast(getV().getResources().getString(R.string.verify_sig_fail));
                break;
            default:
                GemmaToastUtils.showLongToast(getV().getResources().getString(R.string.eos_default_create_fail_info));
                break;
        }
    }

}
