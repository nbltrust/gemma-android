package com.cybex.gma.client.ui.presenter;

import android.os.Bundle;

import com.cybex.componentservice.api.callback.JsonCallback;
import com.cybex.componentservice.config.CacheConstants;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.DeviceOperationManager;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.componentservice.utils.AlertUtil;
import com.cybex.componentservice.utils.AmountUtil;
import com.cybex.componentservice.utils.ConvertUtils;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.JNIUtil;
import com.cybex.gma.client.ui.activity.EosAssetDetailActivity;
import com.cybex.gma.client.ui.fragment.EosTokenTransferFragment;
import com.cybex.gma.client.ui.model.request.DelegateReqParams;
import com.cybex.gma.client.ui.model.request.PushTransactionReqParams;
import com.cybex.gma.client.ui.model.response.AbiJsonToBeanResult;
import com.cybex.gma.client.ui.model.response.CheckGoodsCodeResult;
import com.cybex.gma.client.ui.model.response.DelegateReqResult;
import com.cybex.gma.client.ui.model.vo.TransferTransactionTmpVO;
import com.cybex.gma.client.ui.model.vo.TransferTransactionVO;
import com.cybex.gma.client.ui.request.AbiJsonToBeanRequest;
import com.cybex.gma.client.ui.request.CheckGoodsCodeRequest;
import com.cybex.gma.client.ui.request.DelegateRequest;
import com.cybex.gma.client.ui.request.EOSConfigInfoRequest;
import com.cybex.gma.client.ui.request.PushTransactionRequest;
import com.extropies.common.CommonUtility;
import com.hxlx.core.lib.mvp.lite.XPresenter;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.GsonUtils;
import com.hxlx.core.lib.utils.common.utils.AppManager;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.tapadoo.alerter.Alert;
import com.tapadoo.alerter.Alerter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EosTokenTransferPresenter extends XPresenter<EosTokenTransferFragment> {

    private static final String VALUE_ACTION = "transfer";
    private static final String VALUE_COMPRESSION = "none";
    private static final String VALUE_SYMBOL = "EOS";
    private static final String VALUE_CODE = "eosio.token";
    private static final String VALUE_CONTRACT = "eosio.token";

    private String savedJsonParams;

    /**
     * 执行EOS Token转账逻辑
     *
     * @param from
     * @param to
     * @param quantity
     * @param memo
     * @param privateKey
     * @param accuracy Token小数点后精度
     */
    public void executeTokenTransferLogic(
            String from,
            String to,
            String quantity,
            String memo,
            String privateKey,
            String tokenContract,
            String tokenSymbol,
            int accuracy) {
        //通过c++获取 abi json操作体

        String format_quantity = AmountUtil.round(quantity.split(" ")[0], accuracy) + " " + tokenSymbol;
        String abijson = JNIUtil.create_abi_req_transfer(VALUE_CODE, VALUE_ACTION,
                from, to, format_quantity, memo);

        //链上接口请求 abi_json_to_bin
        new AbiJsonToBeanRequest(AbiJsonToBeanResult.class)
                .setJsonParams(abijson)
                .getAbiJsonToBean(new JsonCallback<AbiJsonToBeanResult>() {
                    @Override
                    public void onStart(Request<AbiJsonToBeanResult, ? extends Request> request) {
                        super.onStart(request);
                        getV().showProgressDialog(getV().getString(R.string.eos_tip_transfer_trade_ing));
                    }

                    @Override
                    public void onError(Response<AbiJsonToBeanResult> response) {
                        super.onError(response);
                        if (EmptyUtils.isNotEmpty(getV())) {
                            GemmaToastUtils.showShortToast(getV().getString(R.string.eos_tip_transfer_oprate_failed));
                            getV().dissmisProgressDialog();

                            try {
                                String err_info_string = response.getRawResponse().body().string();
                                try {
                                    JSONObject obj = new JSONObject(err_info_string);
                                    JSONObject error = obj.optJSONObject("error");
                                    String err_code = error.optString("code");
                                    handleEosErrorCode(err_code);

                                } catch (JSONException ee) {
                                    ee.printStackTrace();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onSuccess(Response<AbiJsonToBeanResult> response) {
                        if (response != null && response.body() != null) {
                            AbiJsonToBeanResult result = response.body();
                            String binargs = result.binargs;
                            LoggerManager.d("abiStr: " + binargs);

                            getInfo(from, privateKey, binargs, tokenContract);

                        } else {
                            GemmaToastUtils.showShortToast(getV().getString(R.string.eos_tip_transfer_oprate_failed));
                        }

                    }
                });
    }

    /**
     * 获取配置信息成功后，再到C++库获取交易体
     *
     * @param from
     * @param privateKey
     * @param abiStr
     */
    public void getInfo(String from, String privateKey, String abiStr, String tokenContract) {
        new EOSConfigInfoRequest(String.class)
                .getInfo(new StringCallback() {

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                        if (EmptyUtils.isNotEmpty(getV())) {
                            GemmaToastUtils.showShortToast(getV().getString(R.string.eos_tip_transfer_oprate_failed));
                            getV().dissmisProgressDialog();

                            try {
                                String err_info_string = response.getRawResponse().body().string();
                                try {
                                    JSONObject obj = new JSONObject(err_info_string);
                                    JSONObject error = obj.optJSONObject("error");
                                    String err_code = error.optString("code");
                                    handleEosErrorCode(err_code);

                                } catch (JSONException ee) {
                                    ee.printStackTrace();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onSuccess(Response<String> response) {
                        if (getV() != null) {
                            if (response != null && EmptyUtils.isNotEmpty(response.body())) {
                                //软钱包流程
                                String infostr = response.body();
                                LoggerManager.d("config info:" + infostr);
                                //C++库获取Transaction交易体
                                String transactionStr = JNIUtil.signTransaction_tranfer(privateKey,
                                        tokenContract, from, infostr, abiStr,
                                        0,
                                        0,
                                        120);
                                LoggerManager.d("transactionJson:" + transactionStr);

                                TransferTransactionVO vo = GsonUtils.jsonToBean(transactionStr,
                                        TransferTransactionVO.class);

                                if (vo != null) {
                                    //构造PushTransaction 请求的json参数
                                    PushTransactionReqParams reqParams = new PushTransactionReqParams();
                                    reqParams.setTransaction(vo);
                                    reqParams.setSignatures(vo.getSignatures());
                                    reqParams.setCompression(VALUE_COMPRESSION);

                                    String buildTransactionJson = GsonUtils.
                                            objectToJson(reqParams);
                                    LoggerManager.d("buildTransactionJson:" + buildTransactionJson);

                                    //执行Push Transaction 最后一步操作
                                    pushTransaction(buildTransactionJson);
                                }

                            } else {
                                //错误
                                if (getV() != null) {
                                    GemmaToastUtils.showShortToast(
                                            getV().getString(R.string.eos_tip_transfer_oprate_failed));
                                    getV().dissmisProgressDialog();
                                }
                            }
                        }
                    }
                });
    }

    /**
     * 最后执行push transaction
     */
    public void pushTransaction(String jsonParams) {
        savedJsonParams = jsonParams;
        new PushTransactionRequest(String.class)
                .setJsonParams(jsonParams)
                .pushTransaction(new StringCallback() {
                    @Override
                    public void onStart(Request<String, ? extends Request> request) {
                        super.onStart(request);
                        if (getV() != null) {
                            getV().showProgressDialog(getV().getString(R.string.eos_tip_transfer_trade_ing));
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        if (EmptyUtils.isNotEmpty(getV())) {

                            try {
                                String err_info_string = response.getRawResponse().body().string();
                                try {
                                    JSONObject obj = new JSONObject(err_info_string);
                                    JSONObject error = obj.optJSONObject("error");
                                    String err_code = error.optString("code");

                                    if (err_code.equals("3080004") || err_code.equals("3080005")) {
                                        //CPU不足
                                        //从卡获取SN
                                        MultiWalletEntity curWallet = DBManager.getInstance()
                                                .getMultiWalletEntityDao().getCurrentMultiWalletEntity();

                                        if (curWallet != null) {
                                            String TAG = getV().toString();
                                            String deviceName = curWallet.getBluetoothDeviceName();

                                            DeviceOperationManager.getInstance().getCheckCode(TAG, deviceName,
                                                    new DeviceOperationManager.GetCheckCodeCallback() {
                                                        @Override
                                                        public void onCheckCodeSuccess(byte[] checkCode) {
                                                            byte[] snbyte = ConvertUtils.subByte(
                                                                    checkCode, 0, 16);

                                                            String SN = CommonUtility.byte2hex(snbyte);
                                                            String SN_sign = CommonUtility.byte2hex(
                                                                    checkCode);
                                                            SN_sign = SN_sign.substring(32);

                                                            LoggerManager.d("SN : ", SN);

                                                            curWallet.setSerialNumber(SN);
                                                            curWallet.save();

                                                            //获取到，查询SN对应的权益
                                                            checkGoodsCode(SN, SN_sign);

                                                        }

                                                        @Override
                                                        public void onCheckCodeFail() {
                                                            if (getV() != null) {
                                                                LoggerManager.d("onCheckCodeFail");
                                                                getV().dissmisProgressDialog();
                                                                AlertUtil.showShortUrgeAlert(getV().getActivity(),
                                                                        getV().getString(R.string.get_sn_fail));
                                                            }
                                                        }
                                                    });


                                        }

                                    } else {
                                        //其他错误
                                        getV().dissmisProgressDialog();
                                        handleEosErrorCode(err_code);
                                    }

                                } catch (JSONException ee) {
                                    ee.printStackTrace();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onSuccess(Response<String> response) {
                        if (EmptyUtils.isNotEmpty(getV())) {
                            getV().dissmisProgressDialog();
                            if (response != null && EmptyUtils.isNotEmpty(response.body())) {
                                String jsonStr = response.body();
                                LoggerManager.d("pushTransaction json:" + jsonStr);

                                Bundle bundle = new Bundle();
                                bundle.putParcelable(ParamConstants.EOS_TOKENS, getV().getCurToken());

                                AppManager.getAppManager().finishActivity();
                                AppManager.getAppManager().finishActivity(EosAssetDetailActivity.class);
                                UISkipMananger.launchAssetDetail(getV().getActivity(), bundle);
                                getV().clearData();
                            }
                        }
                    }
                });

    }

    /**
     * 执行硬件钱包转账逻辑
     *
     * @param from
     * @param to
     * @param quantity
     * @param memo
     */
    public void executeBluetoothTransferLogic(
            String from,
            String to,
            String quantity,
            String memo,
            String tokenContract,
            String tokenSymbol,
            int accuracy) {
        //通过c++获取 abi json操作体
        String format_quantity = AmountUtil.round(quantity.split(" ")[0], accuracy) + " " + tokenSymbol;
        String abijson = JNIUtil.create_abi_req_transfer(VALUE_CODE, VALUE_ACTION,
                from, to, format_quantity, memo);

        //链上接口请求 abi_json_to_bin
        new AbiJsonToBeanRequest(AbiJsonToBeanResult.class)
                .setJsonParams(abijson)
                .getAbiJsonToBean(new JsonCallback<AbiJsonToBeanResult>() {
                    @Override
                    public void onStart(Request<AbiJsonToBeanResult, ? extends Request> request) {
                        super.onStart(request);
                        getV().showProgressDialog(getV().getString(R.string.eos_tip_transfer_trade_ing));
                    }

                    @Override
                    public void onError(Response<AbiJsonToBeanResult> response) {
                        super.onError(response);
                        if (EmptyUtils.isNotEmpty(getV())) {
                            GemmaToastUtils.showShortToast(getV().getString(R.string.eos_tip_transfer_oprate_failed));
                            getV().dissmisProgressDialog();

                            try {
                                String err_info_string = response.getRawResponse().body().string();
                                try {
                                    JSONObject obj = new JSONObject(err_info_string);
                                    JSONObject error = obj.optJSONObject("error");
                                    String err_code = error.optString("code");
                                    handleEosErrorCode(err_code);

                                } catch (JSONException ee) {
                                    ee.printStackTrace();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onSuccess(Response<AbiJsonToBeanResult> response) {
                        if (response != null && response.body() != null) {
                            AbiJsonToBeanResult result = response.body();
                            String binargs = result.binargs;
                            LoggerManager.d("abiStr: " + binargs);

                            bluetoothGetInfo(from, binargs, tokenContract);

                        } else {
                            GemmaToastUtils.showShortToast(getV().getString(R.string.eos_tip_transfer_oprate_failed));
                        }

                    }
                });
    }

    /**
     * 获取配置信息成功后，再到C++库获取交易体
     *
     * @param from
     * @param abiStr
     */
    public void bluetoothGetInfo(String from, String abiStr, String tokenContract) {
        new EOSConfigInfoRequest(String.class)
                .getInfo(new StringCallback() {

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                        if (EmptyUtils.isNotEmpty(getV())) {
                            GemmaToastUtils.showShortToast(getV().getString(R.string.eos_tip_transfer_oprate_failed));
                            getV().dissmisProgressDialog();

                            try {
                                String err_info_string = response.getRawResponse().body().string();
                                try {
                                    JSONObject obj = new JSONObject(err_info_string);
                                    JSONObject error = obj.optJSONObject("error");
                                    String err_code = error.optString("code");
                                    handleEosErrorCode(err_code);

                                } catch (JSONException ee) {
                                    ee.printStackTrace();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onSuccess(Response<String> response) {
                        if (getV() != null) {
                            if (response != null && EmptyUtils.isNotEmpty(response.body())) {
                                //蓝牙钱包流程
                                String infostr = response.body();
                                try {

                                    JSONObject obj = new JSONObject(infostr);
                                    final String chain_id = obj.optString("chain_id");
                                    LoggerManager.d("chain_id", chain_id);
                                    getV().setChain_id(chain_id);


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                LoggerManager.d("config info:" + infostr);
                                String[] keyPair = JNIUtil.createKey().split(";");
                                //随机生成一个无用私钥用于签名
                                String dumpPriKey = keyPair[1];
                                //C++库获取Transaction交易体
                                String transactionStr = JNIUtil.signTransaction_tranfer(dumpPriKey,
                                        tokenContract, from, infostr, abiStr,
                                        0,
                                        0,
                                        120);
                                LoggerManager.d("transactionJson:" + transactionStr);

                                TransferTransactionVO vo = GsonUtils.jsonToBean(transactionStr,
                                        TransferTransactionVO.class);
                                getV().setTransactionVO(vo);
                                if (vo != null) {
                                    //转换临时VO，让硬件可以签名
                                    TransferTransactionTmpVO tmpVO = switchVO(vo);
                                    String tmpJson = GsonUtils.objectToJson(tmpVO);
                                    getV().startEosSerialization(tmpJson);
                                }

//                                getV().dissmisProgressDialog();
                            } else {
                                //错误
                                if (getV() != null) {
                                    GemmaToastUtils.showShortToast(
                                            getV().getString(R.string.eos_tip_transfer_oprate_failed));
                                    getV().dissmisProgressDialog();
                                }

                            }
                        }
                    }
                });
    }

    public void checkGoodsCode(String SN, String SN_Sig) {
        new CheckGoodsCodeRequest(CheckGoodsCodeResult.class, SN)
                .checkGoodsCode(new JsonCallback<CheckGoodsCodeResult>() {

                    @Override
                    public void onStart(Request<CheckGoodsCodeResult, ? extends Request> request) {
                        super.onStart(request);
                    }

                    @Override
                    public void onSuccess(Response<CheckGoodsCodeResult> response) {
                        if (getV() != null) {
                            if (response != null && response.body() != null) {
                                CheckGoodsCodeResult result = response.body();
                                CheckGoodsCodeResult.ResultBean resultBean = result.getResult();
                                if (resultBean != null) {
                                    CheckGoodsCodeResult.ResultBean.RightsBean rightsBean = resultBean.getRights();
                                    if (rightsBean != null) {

                                        CheckGoodsCodeResult.ResultBean.RightsBean.DelegationBean delegationBean = rightsBean
                                                .getDelegation();
                                        if (delegationBean != null) {

                                            if (delegationBean.getActions() != null && delegationBean.getActions()
                                                    .size() > 0) {
                                                //有抵押权益
                                                String account_name = DBManager.getInstance().getMultiWalletEntityDao
                                                        ().getCurrentMultiWalletEntity().getEosWalletEntities().get
                                                        (0).getCurrentEosName();

                                                doDelegate(account_name, SN, SN_Sig);

                                            } else {
                                                //无抵押权益
                                                getV().dissmisProgressDialog();
                                                AlertUtil.showShortUrgeAlert(getV().getActivity(),
                                                        getV().getString(R.string.cpu_insufficient));

                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Response<CheckGoodsCodeResult> response) {
                        if (getV() != null) {
                            super.onError(response);
                            AlertUtil.showShortUrgeAlert(getV().getActivity(),
                                    getV().getString(R.string.eos_chain_unstable));
                        }

                    }
                });
    }

    public void doDelegate(String account_name, String SN, String SN_Sig) {
        DelegateReqParams params = new DelegateReqParams();
        params.setApp_id(1);
        params.setGoods_id(1001);
        params.setCode("serialNumber");
        params.setAccount_name(account_name);

        DelegateReqParams.DelegateValidation validation = new DelegateReqParams.DelegateValidation();
        validation.setSN(SN);
        validation.setSN_sig(SN_Sig);

        params.setValidation(validation);

        String jsonParams = GsonUtils.objectToJson(params);

        new DelegateRequest(DelegateReqResult.class)
                .setJsonParams(jsonParams)
                .delegate(new JsonCallback<DelegateReqResult>() {
                    @Override
                    public void onSuccess(Response<DelegateReqResult> response) {
                        if (getV() != null) {
                            if (response != null && response.body() != null) {
                                DelegateReqResult result = response.body();
                                DelegateReqResult.ResultBean resultBean = result.getResult();
                                if (resultBean != null) {
                                    String action_id = resultBean.getAction_id();
                                    LoggerManager.d("action_id : ", action_id);

                                    //抵押成功
                                    pushTransaction(savedJsonParams);
                                }

                            } else {
                                getV().dissmisProgressDialog();
                                AlertUtil.showShortUrgeAlert(getV().getActivity(),
                                        getV().getString(R.string.eos_chain_unstable));
                            }
                        }
                    }

                    @Override
                    public void onError(Response<DelegateReqResult> response) {
                        if (getV() != null) {
                            AlertUtil.showShortUrgeAlert(getV().getActivity(),
                                    getV().getString(R.string.cpu_insufficient));
                        }
                    }
                });

    }


    /**
     * 转换VO类型
     * 目的是转换字段的基础数据类型以让硬件SDK可以处理
     *
     * @param oldVO
     * @return
     */
    private TransferTransactionTmpVO switchVO(TransferTransactionVO oldVO) {
        TransferTransactionTmpVO newVO = new TransferTransactionTmpVO();
        /*
        for (TransferTransactionVO.ActionsBean action : oldVO.getActions()){
            action.setAccount("eosio");
            action.setName("newaccount");
        }
        */
        newVO.setActions(oldVO.getActions());
        newVO.setContext_free_actions(oldVO.getContext_free_actions());
        newVO.setContext_free_data(new ArrayList<>());
        newVO.setDelay_sec(oldVO.getDelay_sec());
        newVO.setExpiration(oldVO.getExpiration());
        newVO.setMax_cpu_usage_ms(oldVO.getMax_cpu_usage_ms());
        newVO.setMax_net_usage_words(oldVO.getMax_net_usage_words());
        newVO.setRef_block_num(oldVO.getRef_block_num());
        newVO.setSignatures(new ArrayList<>());
        newVO.setTransaction_extensions(oldVO.getTransaction_extensions());
        newVO.setRef_block_prefix(String.valueOf(oldVO.getRef_block_prefix()));

        return newVO;
    }

    /**
     * 合并两字节数组
     *
     * @param bt1
     * @param bt2
     * @return
     */
    public byte[] byteMerger(byte[] bt1, byte[] bt2) {
        byte[] bt3 = new byte[bt1.length + bt2.length];
        System.arraycopy(bt1, 0, bt3, 0, bt1.length);
        System.arraycopy(bt2, 0, bt3, bt1.length, bt2.length);
        return bt3;
    }

    /**
     * hex String转byte数组
     *
     * @param hex
     * @return
     */
    public byte[] hexToByte(String hex) {
        int m = 0, n = 0;
        int byteLen = hex.length() / 2; // 每两个字符描述一个字节
        byte[] ret = new byte[byteLen];
        for (int i = 0; i < byteLen; i++) {
            m = i * 2 + 1;
            n = m + 1;
            int intVal = Integer.decode("0x" + hex.substring(i * 2, m) + hex.substring(m, n));
            ret[i] = Byte.valueOf((byte) intVal);
        }
        return ret;
    }

    /**
     * 返回当前钱包类型
     */
    public int getWalletType() {
        MultiWalletEntity curWallet = DBManager.getInstance().getMultiWalletEntityDao().getCurrentMultiWalletEntity();
        if (curWallet != null) {
            switch (curWallet.getWalletType()) {
                case CacheConstants.WALLET_TYPE_BLUETOOTH:
                    //蓝牙钱包
                    return CacheConstants.WALLET_TYPE_BLUETOOTH;
                case CacheConstants.WALLET_TYPE_MNE_CREATE:
                    //软件钱包
                    return CacheConstants.WALLET_TYPE_MNE_CREATE;
            }
        }
        return CacheConstants.WALLET_TYPE_MNE_CREATE;
    }

    /**
     * 构建硬件能够识别的HEX字符串
     * 序列化结果前面加32字节chain_id，后面加32字节0
     *
     * @param serializedStr
     */
    public byte[] buildSignStr(String serializedStr, String chain_id) {
        //把数据转成HEX数组
        String prefix = chain_id.toUpperCase();//chain_id已经是HEX
        LoggerManager.d("prefix_hex", prefix);

        LoggerManager.d("serializedStr_hex", serializedStr);

        byte[] suffix_bytes = {
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};//32字节的0

        LoggerManager.d("suffix_hex", suffix_bytes);

        String hexString = prefix + serializedStr;

        byte[] hexBytes = hexToByte(hexString);
        LoggerManager.d("hexBytes", hexBytes);

        return byteMerger(hexBytes, suffix_bytes);
    }


    /**
     * 反射机制处理EOS错误码
     *
     * @param err_code
     */
    private void handleEosErrorCode(String err_code) {
        String code = ParamConstants.EOS_ERR_CODE_PREFIX + err_code;
        if (EmptyUtils.isNotEmpty(getV()) && EmptyUtils.isNotEmpty(getV().getActivity())) {
            String package_name = getV().getActivity().getPackageName();
            int resId = getV().getResources().getIdentifier(code, "string", package_name);
            String err_info = getV().getResources().getString(resId);

            Alerter.create(getV().getActivity())
                    .setText(err_info)
                    .setContentGravity(Alert.TEXT_ALIGNMENT_GRAVITY)
                    .showIcon(false)
                    .setDuration(3000)
                    .setBackgroundColorRes(R.color.scarlet)
                    .show();
        }
    }

    /**
     * 获取当前蓝牙钱包对应的设备名称
     *
     * @return
     */
    public String getBluetoothDeviceName() {

        List<MultiWalletEntity> bluetoothWalletList = DBManager.getInstance().getMultiWalletEntityDao()
                .getBluetoothWalletList();

        if (bluetoothWalletList != null && bluetoothWalletList.size() > 0) {
            return bluetoothWalletList.get(0).getBluetoothDeviceName();
        }

        return "list empty err";

    }

    public boolean isBioMemoValid() {
        if (getV() != null) {
            String memo = getV().getNote().toString();
//            String regEx = "^[A-Za-z0-9\\p{P}]{0,15}$";
            String regEx = "^[A-Za-z0-9!@#$%^&*().,_+=><?\\s]{0,15}$";
            Pattern pattern = Pattern.compile(regEx);
            Matcher matcher = pattern.matcher((memo));
            boolean res = matcher.matches();
            //LoggerManager.d("regex", res);
            return res;
        }
        return false;
    }


}
