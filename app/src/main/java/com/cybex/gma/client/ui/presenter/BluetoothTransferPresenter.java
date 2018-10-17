package com.cybex.gma.client.ui.presenter;

import com.cybex.componentservice.api.callback.JsonCallback;
import com.cybex.componentservice.db.entity.WalletEntity;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.JNIUtil;
import com.cybex.gma.client.ui.fragment.BluetoothTransferFragment;
import com.cybex.gma.client.ui.model.request.GetCurrencyBalanceReqParams;
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
 * Created by wanglin on 2018/9/21.
 */
public class BluetoothTransferPresenter extends XPresenter<BluetoothTransferFragment> {
    private static final String VALUE_CODE = "eosio.token";
    private static final String VALUE_ACTION = "transfer";
    private static final String VALUE_CONTRACT = "eosio.token";
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
                        getV().showProgressDialog(getV().getString(R.string.eos_tip_transfer_trade_ing));
                    }

                    @Override
                    public void onError(Response<AbiJsonToBeanResult> response) {
                        super.onError(response);
                        if (EmptyUtils.isNotEmpty(getV())){
                            GemmaToastUtils.showShortToast(getV().getString(R.string.eos_tip_transfer_oprate_failed));
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
    public void bluetoothGetInfo(String from, String abiStr) {
        new EOSConfigInfoRequest(String.class)
                .getInfo(new StringCallback() {

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                        if (EmptyUtils.isNotEmpty(getV())){
                            GemmaToastUtils.showShortToast(getV().getString(R.string.eos_tip_transfer_oprate_failed));
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
                                getV().setVo(vo);
                                if (vo != null){
                                    TransferTransactionTmpVO tmpVO = switchVO(vo);
                                    String tmpJson = GsonUtils.objectToJson(tmpVO);
                                    getV().startJsonSerialization(tmpJson);
                                    //todo 获取签名

                                }

                                getV().dissmisProgressDialog();
                            } else {
                                //错误
                                GemmaToastUtils.showShortToast(getV().getString(R.string.eos_tip_transfer_oprate_failed));
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
     * 转换VO类型
     * 目的是转换字段的基础数据类型以让硬件SDK可以处理
     * 具体处理为删除signatures字段，改变Ref_block_prefix的基础类型为字符串
     * @param oldVO
     * @return
     */
    private TransferTransactionTmpVO switchVO(TransferTransactionVO oldVO){
        TransferTransactionTmpVO newVO = new TransferTransactionTmpVO();

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

}
