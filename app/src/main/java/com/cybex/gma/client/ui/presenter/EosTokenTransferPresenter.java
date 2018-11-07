package com.cybex.gma.client.ui.presenter;

import com.cybex.componentservice.api.callback.JsonCallback;
import com.cybex.componentservice.config.CacheConstants;
import com.cybex.componentservice.db.entity.EosWalletEntity;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.JNIUtil;
import com.cybex.gma.client.ui.fragment.EosTokenTransferFragment;
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

public class EosTokenTransferPresenter extends XPresenter<EosTokenTransferFragment> {
    private static final String VALUE_CODE = "eosio.token";
    private static final String VALUE_ACTION = "transfer";
    private static final String VALUE_COMPRESSION = "none";
    private static final String VALUE_SYMBOL = "EOS";

//    public void requestBanlanceInfo() {
//        MultiWalletEntity entity = DBManager.getInstance().getMultiWalletEntityDao().getCurrentMultiWalletEntity();
//        EosWalletEntity eosEntity = entity.getEosWalletEntities().get(0);
//        if (entity == null && eosEntity != null) { return; }
//
//        String currentEOSName = eosEntity.getCurrentEosName();
//        GetCurrencyBalanceReqParams params = new GetCurrencyBalanceReqParams();
//        params.setAccount(currentEOSName);
//        params.setCode(VALUE_CODE);
//        params.setSymbol(VALUE_SYMBOL);
//        String jsonParams = GsonUtils.objectToJson(params);
//
//        new GetCurrencyBalanceRequest(String.class)
//                .setJsonParams(jsonParams)
//                .getCurrencyBalance(new StringCallback() {
//                    @Override
//                    public void onStart(Request<String, ? extends Request> request) {
//                        getV().showProgressDialog(
//                                getV().getResources().getString(R.string.eos_loading_pretransfer_info));
//                        super.onStart(request);
//                    }
//
//                    @Override
//                    public void onError(Response<String> response) {
//                        super.onError(response);
//                        if (EmptyUtils.isNotEmpty(getV())) {
//                            getV().dissmisProgressDialog();
//
//                            try {
//                                if (response.body() != null && response.getRawResponse().body() != null) {
//                                    String err_info_string = response.getRawResponse().body().string();
//                                    try {
//                                        JSONObject obj = new JSONObject(err_info_string);
//                                        JSONObject error = obj.optJSONObject("error");
//                                        String err_code = error.optString("code");
//                                        handleEosErrorCode(err_code);
//
//                                    } catch (JSONException ee) {
//                                        ee.printStackTrace();
//                                    }
//                                }
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onSuccess(Response<String> response) {
//                        if (getV() != null) {
//                            String jsonStr = response.body();
//                            String banlance = "0.0000";
//                            LoggerManager.d("json:" + jsonStr);
//                            try {
//                                JSONArray array = new JSONArray(jsonStr);
//                                if (array != null && array.length() > 0) {
//                                    banlance = array.optString(0);
//                                    getV().showInitData(banlance, currentEOSName);
//                                } else {
//                                    getV().showInitData(banlance, currentEOSName);
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                            getV().dissmisProgressDialog();
//                        }
//                    }
//                });
//    }

    /**
     * 执行软件钱包转账逻辑
     *
     * @param from
     * @param to
     * @param quantity
     * @param memo
     * @param privateKey
     */
    public void executeTransferLogic(String from, String to, String quantity, String memo, String privateKey, String
            tokenContract) {
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
                        if (EmptyUtils.isNotEmpty(getV())) {
                            getV().dissmisProgressDialog();
                            if (response != null && EmptyUtils.isNotEmpty(response.body())) {
                                String jsonStr = response.body();
                                LoggerManager.d("pushTransaction json:" + jsonStr);
                                //if (getV().getActivity() != null)getV().getActivity().finish();

                                UISkipMananger.launchEOSHome(getV().getActivity());
                                getV().clearData();
                            }
                        }
                    }
                });

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



}
