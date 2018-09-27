package com.cybex.gma.client.ui.presenter;

import com.cybex.gma.client.R;
import com.cybex.gma.client.api.callback.JsonCallback;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.db.entity.WalletEntity;
import com.cybex.gma.client.manager.DBManager;
import com.cybex.gma.client.manager.LoggerManager;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.JNIUtil;
import com.cybex.gma.client.ui.fragment.BluetoothTransferFragment;
import com.cybex.gma.client.ui.fragment.TransferFragment;
import com.cybex.gma.client.ui.model.request.GetCurrencyBalanceReqParams;
import com.cybex.gma.client.ui.model.request.PushTransactionReqParams;
import com.cybex.gma.client.ui.model.response.AbiJsonToBeanResult;
import com.cybex.gma.client.ui.model.vo.BluetoothTransferTransactionVO;
import com.cybex.gma.client.ui.model.vo.TransferTransactionVO;
import com.cybex.gma.client.ui.request.AbiJsonToBeanRequest;
import com.cybex.gma.client.ui.request.EOSConfigInfoRequest;
import com.cybex.gma.client.ui.request.GetCurrencyBalanceRequest;
import com.cybex.gma.client.ui.request.PushTransactionRequest;
import com.hxlx.core.lib.mvp.lite.XPresenter;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.GsonUtils;
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
import java.util.List;

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
    private static final int WALLET_TYPE_SOFT = 0;
    private static final int WALLET_TYPE_BLUETOOTH = 1;

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
     * 执行硬件钱包转账逻辑
     *
     * @param from
     * @param to
     * @param quantity
     * @param memo
     * @param privateKey
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

                                if (vo != null){
                                    BluetoothTransferTransactionVO bluetoothVO = switchTransferVO(vo);

                                    String bluetoothTransactionStr = GsonUtils.objectToJson(bluetoothVO);
                                    LoggerManager.d("bluetoothTransactionJson:" + bluetoothTransactionStr);
                                    //todo Transaction转HEX
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
     * 最后执行push transaction
     */
    public void pushTransaction(String jsonParams) {
        new PushTransactionRequest(String.class)
                .setJsonParams(jsonParams)
                .pushTransaction(new StringCallback() {
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
                                UISkipMananger.launchTransferRecord(getV().getActivity());
                                getV().clearData();
                            }
                        }
                    }
                });

    }

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
     * 软钱包VO转换成硬件钱包VO
     * 实际操作为删除最后两个字段
     */
    public BluetoothTransferTransactionVO switchTransferVO(TransferTransactionVO softVO){
        BluetoothTransferTransactionVO bluetoothVO = new BluetoothTransferTransactionVO();

        //基础类型参数
        bluetoothVO.setExpiration(softVO.getExpiration());
        bluetoothVO.setRef_block_num(softVO.getRef_block_num());
        bluetoothVO.setRef_block_prefix(softVO.getRef_block_prefix());
        bluetoothVO.setMax_cpu_usage_ms(softVO.getMax_cpu_usage_ms());
        bluetoothVO.setMax_net_usage_words(softVO.getMax_net_usage_words());
        bluetoothVO.setDelay_sec(softVO.getDelay_sec());
        bluetoothVO.setContext_free_actions(softVO.getContext_free_actions());
        bluetoothVO.setTransaction_extensions(softVO.getTransaction_extensions());

        //ActionBean类型转换
        List<BluetoothTransferTransactionVO.ActionsBean> bluetoothActions = new ArrayList<>();
        BluetoothTransferTransactionVO.ActionsBean bluetoothAction = new BluetoothTransferTransactionVO.ActionsBean();
        List<TransferTransactionVO.ActionsBean> softActions = softVO.getActions();
        TransferTransactionVO.ActionsBean softAction = softActions.get(0);
        //todo List循环添加

        bluetoothAction.setName(softAction.getName());
        bluetoothAction.setAccount(softAction.getAccount());
        bluetoothAction.setData(softAction.getData());

        //AuthorizationBean类型转换
        List<BluetoothTransferTransactionVO.ActionsBean.AuthorizationBean> bluetoothAuthorizations = new ArrayList<>();
        List<TransferTransactionVO.ActionsBean.AuthorizationBean> softAuthorizations = softAction.getAuthorization();
        BluetoothTransferTransactionVO.ActionsBean.AuthorizationBean bluetoothAuthorization = new
                BluetoothTransferTransactionVO.ActionsBean.AuthorizationBean();
        TransferTransactionVO.ActionsBean.AuthorizationBean softAuthorization = softAuthorizations.get(0);
        //todo List循环添加
        bluetoothAuthorization.setActor(softAuthorization.getActor());
        bluetoothAuthorization.setPermission(softAuthorization.getPermission());

        bluetoothAction.setAuthorization(bluetoothAuthorizations);
        bluetoothActions.add(bluetoothAction);
        bluetoothVO.setActions(bluetoothActions);

        return bluetoothVO;
    }

}