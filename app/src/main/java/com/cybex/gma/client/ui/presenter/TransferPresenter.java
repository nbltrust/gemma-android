package com.cybex.gma.client.ui.presenter;

import com.cybex.componentservice.api.callback.JsonCallback;
import com.cybex.componentservice.db.entity.WalletEntity;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.CacheConstants;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.JNIUtil;
import com.cybex.gma.client.ui.fragment.TransferFragment;
import com.cybex.gma.client.ui.model.request.GetCurrencyBalanceReqParams;
import com.cybex.gma.client.ui.model.request.PushTransactionReqParams;
import com.cybex.gma.client.ui.model.response.AbiJsonToBeanResult;

import com.cybex.gma.client.ui.model.vo.TransferTransactionTmpVO;
import com.cybex.gma.client.ui.model.vo.TransferTransactionVO;
import com.cybex.gma.client.ui.request.AbiJsonToBeanRequest;
import com.cybex.gma.client.ui.request.EOSConfigInfoRequest;
import com.cybex.gma.client.ui.request.GetCurrencyBalanceRequest;
import com.cybex.gma.client.ui.request.PushTransactionRequest;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * 转账presenter
 *
 * Created by wanglin on 2018/7/9.
 */
public class TransferPresenter extends XPresenter<TransferFragment> {

    private static final String VALUE_CODE = "eosio.token";
    private static final String VALUE_ACTION = "transfer";
    private static final String VALUE_CONTRACT = "eosio.token";
    private static final String VALUE_COMPRESSION = "none";
    private static final String VALUE_SYMBOL = "EOS";

    public void requestBanlanceInfo() {
        WalletEntity entity = DBManager.getInstance().getWalletEntityDao().getCurrentWalletEntity();
        if (entity == null) { return; }

        String currentEOSName = entity.getCurrentEosName();
        GetCurrencyBalanceReqParams params = new GetCurrencyBalanceReqParams();
        params.setAccount(currentEOSName);
        params.setCode(VALUE_CODE);
        params.setSymbol(VALUE_SYMBOL);
        String jsonParams = GsonUtils.objectToJson(params);

        new GetCurrencyBalanceRequest(String.class)
                .setJsonParams(jsonParams)
                .getCurrencyBalance(new StringCallback() {
                    @Override
                    public void onStart(Request<String, ? extends Request> request) {
                        getV().showProgressDialog(getV().getResources().getString(R.string.loading_pretransfer_info));
                        super.onStart(request);
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        if (EmptyUtils.isNotEmpty(getV())){
                            getV().dissmisProgressDialog();

                            try {
                                if (response.body() != null && response.getRawResponse().body() != null){
                                    String err_info_string = response.getRawResponse().body().string();
                                    try {
                                        JSONObject obj = new JSONObject(err_info_string);
                                        JSONObject error = obj.optJSONObject("error");
                                        String err_code = error.optString("code");
                                        handleEosErrorCode(err_code);

                                    }catch (JSONException ee){
                                        ee.printStackTrace();
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onSuccess(Response<String> response) {
                        String jsonStr = response.body();
                        String banlance = "0.0000";
                        LoggerManager.d("json:" + jsonStr);
                        try {
                            JSONArray array = new JSONArray(jsonStr);
                            if (array != null && array.length() > 0) {
                                banlance = array.optString(0);
                                getV().showInitData(banlance, currentEOSName);
                            } else {
                                getV().showInitData(banlance, currentEOSName);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        getV().dissmisProgressDialog();
                    }
                });
    }

    /**
     * 执行软件钱包转账逻辑
     *
     * @param from
     * @param to
     * @param quantity
     * @param memo
     * @param privateKey
     */
    public void executeTransferLogic(String from, String to, String quantity, String memo, String privateKey) {
        //通过c++获取 abi json操作体
        String abijson = JNIUtil.create_abi_req_transfer(VALUE_CODE, VALUE_ACTION,
                from, to, quantity, memo);

        //链上接口请求 abi_json_to_bin
        new AbiJsonToBeanRequest(AbiJsonToBeanResult.class)
                .setJsonParams(abijson)
                .getAbiJsonToBean(new JsonCallback<AbiJsonToBeanResult>() {
                    @Override
                    public void onStart(Request<AbiJsonToBeanResult, ? extends Request> request) {
                        super.onStart(request);
                        getV().showProgressDialog(getV().getString(R.string.transfer_trade_ing));
                    }

                    @Override
                    public void onError(Response<AbiJsonToBeanResult> response) {
                        super.onError(response);
                        if (EmptyUtils.isNotEmpty(getV())){
                            GemmaToastUtils.showShortToast(getV().getString(R.string.transfer_oprate_failed));
                            getV().dissmisProgressDialog();

                            try {
                                String err_info_string = response.getRawResponse().body().string();
                                try {
                                    JSONObject obj = new JSONObject(err_info_string);
                                    JSONObject error = obj.optJSONObject("error");
                                    String err_code = error.optString("code");
                                    handleEosErrorCode(err_code);

                                }catch (JSONException ee){
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

                            getInfo(from, privateKey, binargs);


                        } else {
                            GemmaToastUtils.showShortToast(getV().getString(R.string.transfer_oprate_failed));
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
    public void getInfo(String from, String privateKey, String abiStr) {
        new EOSConfigInfoRequest(String.class)
                .getInfo(new StringCallback() {

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                        if (EmptyUtils.isNotEmpty(getV())){
                            GemmaToastUtils.showShortToast(getV().getString(R.string.transfer_oprate_failed));
                            getV().dissmisProgressDialog();

                            try {
                                String err_info_string = response.getRawResponse().body().string();
                                try {
                                    JSONObject obj = new JSONObject(err_info_string);
                                    JSONObject error = obj.optJSONObject("error");
                                    String err_code = error.optString("code");
                                    handleEosErrorCode(err_code);

                                }catch (JSONException ee){
                                    ee.printStackTrace();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onSuccess(Response<String> response) {
                        if (getV() != null){
                            if (response != null && EmptyUtils.isNotEmpty(response.body())) {
                                //软钱包流程
                                String infostr = response.body();
                                LoggerManager.d("config info:" + infostr);
                                //C++库获取Transaction交易体
                                String transactionStr = JNIUtil.signTransaction_tranfer(privateKey,
                                        VALUE_CONTRACT, from, infostr, abiStr,
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
                                GemmaToastUtils.showShortToast(getV().getString(R.string.transfer_oprate_failed));
                                getV().dissmisProgressDialog();
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
    public void executeBluetoothTransferLogic(String from, String to, String quantity, String memo) {
        //通过c++获取 abi json操作体
        String abijson = JNIUtil.create_abi_req_transfer(VALUE_CODE, VALUE_ACTION,
                from, to, quantity, memo);

        //链上接口请求 abi_json_to_bin
        new AbiJsonToBeanRequest(AbiJsonToBeanResult.class)
                .setJsonParams(abijson)
                .getAbiJsonToBean(new JsonCallback<AbiJsonToBeanResult>() {
                    @Override
                    public void onStart(Request<AbiJsonToBeanResult, ? extends Request> request) {
                        super.onStart(request);
                        getV().showProgressDialog(getV().getString(R.string.transfer_trade_ing));
                    }

                    @Override
                    public void onError(Response<AbiJsonToBeanResult> response) {
                        super.onError(response);
                        if (EmptyUtils.isNotEmpty(getV())){
                            GemmaToastUtils.showShortToast(getV().getString(R.string.transfer_oprate_failed));
                            getV().dissmisProgressDialog();

                            try {
                                String err_info_string = response.getRawResponse().body().string();
                                try {
                                    JSONObject obj = new JSONObject(err_info_string);
                                    JSONObject error = obj.optJSONObject("error");
                                    String err_code = error.optString("code");
                                    handleEosErrorCode(err_code);

                                }catch (JSONException ee){
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

                            bluetoothGetInfo(from, binargs);

                        } else {
                            GemmaToastUtils.showShortToast(getV().getString(R.string.transfer_oprate_failed));
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
    public void bluetoothGetInfo(String from, String abiStr) {
        new EOSConfigInfoRequest(String.class)
                .getInfo(new StringCallback() {

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                        if (EmptyUtils.isNotEmpty(getV())){
                            GemmaToastUtils.showShortToast(getV().getString(R.string.transfer_oprate_failed));
                            getV().dissmisProgressDialog();

                            try {
                                String err_info_string = response.getRawResponse().body().string();
                                try {
                                    JSONObject obj = new JSONObject(err_info_string);
                                    JSONObject error = obj.optJSONObject("error");
                                    String err_code = error.optString("code");
                                    handleEosErrorCode(err_code);

                                }catch (JSONException ee){
                                    ee.printStackTrace();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onSuccess(Response<String> response) {
                        if (getV() != null){
                            if (response != null && EmptyUtils.isNotEmpty(response.body())) {
                                //蓝牙钱包流程
                                String infostr = response.body();
                                try {

                                    JSONObject obj = new JSONObject(infostr);
                                    final String chain_id = obj.optString("chain_id");
                                    LoggerManager.d("chain_id", chain_id);
                                    getV().setChain_id(chain_id);

                                }catch (JSONException e){
                                    e.printStackTrace();
                                }

                                LoggerManager.d("config info:" + infostr);
                                String[] keyPair = JNIUtil.createKey().split(";");
                                //随机生成一个无用私钥用于签名
                                String dumpPriKey = keyPair[1];
                                //C++库获取Transaction交易体
                                String transactionStr = JNIUtil.signTransaction_tranfer(dumpPriKey,
                                        VALUE_CONTRACT, from, infostr, abiStr,
                                        0,
                                        0,
                                        120);
                                LoggerManager.d("transactionJson:" + transactionStr);

                                TransferTransactionVO vo = GsonUtils.jsonToBean(transactionStr,
                                        TransferTransactionVO.class);
                                getV().setTransactionVO(vo);
                                if (vo != null){
                                    //转换临时VO，让硬件可以签名
                                    TransferTransactionTmpVO tmpVO = switchVO(vo);
                                    String tmpJson = GsonUtils.objectToJson(tmpVO);
                                    getV().startJsonSerialization(tmpJson);
                                }

                                getV().dissmisProgressDialog();
                            } else {
                                //错误
                                GemmaToastUtils.showShortToast(getV().getString(R.string.transfer_oprate_failed));
                                getV().dissmisProgressDialog();
                            }
                        }
                    }
                });
    }

    /**
     * 最后执行push transaction
     */
    public void pushTransaction(String jsonParams) {
        new PushTransactionRequest(String.class)
                .setJsonParams(jsonParams)
                .pushTransaction(new StringCallback() {
                    @Override
                    public void onStart(Request<String, ? extends Request> request) {
                        super.onStart(request);
                        if (getV() != null){
                            getV().showProgressDialog(getV().getString(R.string.transfer_trade_ing));
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        if (EmptyUtils.isNotEmpty(getV())){
                            getV().dissmisProgressDialog();

                            try {
                                String err_info_string = response.getRawResponse().body().string();
                                try {
                                    JSONObject obj = new JSONObject(err_info_string);
                                    JSONObject error = obj.optJSONObject("error");
                                    String err_code = error.optString("code");
                                    handleEosErrorCode(err_code);

                                }catch (JSONException ee){
                                    ee.printStackTrace();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onSuccess(Response<String> response) {
                        if (EmptyUtils.isNotEmpty(getV())){
                            getV().dissmisProgressDialog();
                            if (response != null && EmptyUtils.isNotEmpty(response.body())) {
                                String jsonStr = response.body();
                                LoggerManager.d("pushTransaction json:" + jsonStr);
                                //if (getV().getActivity() != null)getV().getActivity().finish();
                                UISkipMananger.launchTransferRecord(getV().getActivity());
                                getV().clearData();
                            }
                        }
                    }
                });

    }

    /**
     * 反射机制处理EOS错误码
     * @param err_code
     */
    private void handleEosErrorCode(String err_code){
        String code = ParamConstants.EOS_ERR_CODE_PREFIX + err_code;
        if (EmptyUtils.isNotEmpty(getV()) && EmptyUtils.isNotEmpty(getV().getActivity())){
            String package_name = getV().getActivity().getPackageName();
            int resId = getV().getResources().getIdentifier(code, "string", package_name);
            String err_info =  getV().getResources().getString(resId);

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
     * 转换VO类型
     * 目的是转换字段的基础数据类型以让硬件SDK可以处理
     * @param oldVO
     * @return
     */
    private TransferTransactionTmpVO switchVO(TransferTransactionVO oldVO){
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
     * @param bt1
     * @param bt2
     * @return
     */
    public byte[] byteMerger(byte[] bt1, byte[] bt2){
        byte[] bt3 = new byte[bt1.length+bt2.length];
        System.arraycopy(bt1, 0, bt3, 0, bt1.length);
        System.arraycopy(bt2, 0, bt3, bt1.length, bt2.length);
        return bt3;
    }

    /**
     * hex String转byte数组
     * @param hex
     * @return
     */
    public byte[] hexToByte(String hex){
        int m = 0, n = 0;
        int byteLen = hex.length() / 2; // 每两个字符描述一个字节
        byte[] ret = new byte[byteLen];
        for (int i = 0; i < byteLen; i++) {
            m = i * 2 + 1;
            n = m + 1;
            int intVal = Integer.decode("0x" + hex.substring(i * 2, m) + hex.substring(m, n));
            ret[i] = Byte.valueOf((byte)intVal);
        }
        return ret;
    }

    /**
     * 返回当前钱包类型
     */
    public int getWalletType() {
        WalletEntity curWallet = DBManager.getInstance().getWalletEntityDao().getCurrentWalletEntity();
        if (curWallet != null) {
            switch (curWallet.getWalletType()) {
                case CacheConstants.WALLET_TYPE_BLUETOOTH:
                    //蓝牙钱包
                    return CacheConstants.WALLET_TYPE_BLUETOOTH;
                case CacheConstants.WALLET_TYPE_SOFT:
                    //软件钱包
                    return CacheConstants.WALLET_TYPE_SOFT;
            }
        }
        return CacheConstants.WALLET_TYPE_SOFT;
    }

    /**
     * 构建硬件能够识别的HEX字符串
     * 序列化结果前面加32字节chain_id，后面加32字节0
     * @param serializedStr
     */
    public byte[] buildSignStr(String serializedStr, String chain_id){
        //把数据转成HEX数组
        String prefix = chain_id.toUpperCase();//chain_id已经是HEX
        LoggerManager.d("prefix_hex", prefix);

        LoggerManager.d("serializedStr_hex", serializedStr);

        byte[] suffix_bytes = {
                (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};//32字节的0

        LoggerManager.d("suffix_hex", suffix_bytes);

        String hexString = prefix + serializedStr;

        byte[] hexBytes = hexToByte(hexString);
        LoggerManager.d("hexBytes", hexBytes);

        return byteMerger(hexBytes, suffix_bytes);
    }

}