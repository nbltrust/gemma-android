package com.cybex.gma.client.ui.presenter;

import com.cybex.componentservice.api.callback.JsonCallback;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.db.entity.WalletEntity;
import com.cybex.gma.client.manager.DBManager;
import com.cybex.gma.client.manager.LoggerManager;
import com.cybex.gma.client.ui.JNIUtil;
import com.cybex.gma.client.ui.fragment.VoteFragment;
import com.cybex.gma.client.ui.model.request.GetAccountInfoReqParams;
import com.cybex.gma.client.ui.model.request.PushTransactionReqParams;
import com.cybex.gma.client.ui.model.request.VoteAbiJsonToBinReqParams;
import com.cybex.gma.client.ui.model.response.AbiJsonToBeanResult;
import com.cybex.gma.client.ui.model.response.AccountInfo;
import com.cybex.gma.client.ui.model.response.FetchBPDetailsResult;
import com.cybex.gma.client.ui.model.vo.TransferTransactionVO;
import com.cybex.gma.client.ui.model.vo.VoteNodeVO;
import com.cybex.gma.client.ui.request.AbiJsonToBeanRequest;
import com.cybex.gma.client.ui.request.EOSConfigInfoRequest;
import com.cybex.gma.client.ui.request.FetchBPDetailRequest;
import com.cybex.gma.client.ui.request.GetAccountinfoRequest;
import com.cybex.gma.client.ui.request.PushTransactionRequest;
import com.cybex.gma.client.utils.AlertUtil;
import com.cybex.gma.client.utils.AmountUtil;
import com.hxlx.core.lib.mvp.lite.XPresenter;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.GsonUtils;
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

public class VotePresenter extends XPresenter<VoteFragment> {

    private static final String VALUE_CONTRACT = "eosio";
    private static final String VALUE_CODE = "eosio";
    private static final String VALUE_PROXY = "";
    private static final String VALUE_COMPRESSION = "none";
    private static final String VALUE_ACTION = "voteproducer";

    /**
     * 获取可投票的节点信息
     *
     * @param show_num
     */
    public void fetchBPDetail(int show_num) {
        List<VoteNodeVO> list = new ArrayList<>();
        //FetchBPDetailReqParams params = new FetchBPDetailReqParams();
        //params.setNumber(show_num);

        //String jsonParams = GsonUtils.objectToJson(params);

        new FetchBPDetailRequest(FetchBPDetailsResult.class, String.valueOf(ParamConstants.BP_NODE_NUMBERS))
                //.setJsonParams(jsonParams)
                .FetchBPDetailRequest(new JsonCallback<FetchBPDetailsResult>() {
                    @Override
                    public void onStart(Request<FetchBPDetailsResult, ? extends Request> request) {
                        getV().showLoading();
                    }

                    @Override
                    public void onSuccess(Response<FetchBPDetailsResult> response) {
                        LoggerManager.d(response);
                        if (response.body() != null && response.body().getResult() != null && response.body()
                                .getResult().getProducers() != null) {
                            List<FetchBPDetailsResult.ResultBean.ProducersBean> producers = response.body().getResult
                                    ().getProducers();
                            if (EmptyUtils.isNotEmpty(producers)) {
                                for (FetchBPDetailsResult.ResultBean.ProducersBean producer : producers) {
                                    VoteNodeVO curNodeVO = new VoteNodeVO();
                                    curNodeVO.setAccount(producer.getAccount());
                                    curNodeVO.setAlias(producer.getAlias());
                                    curNodeVO.setUrl(producer.getUrl());
                                    String percentage = AmountUtil.mul(String.valueOf(producer.getPercentage()), "100",
                                            2) + " %";
                                    curNodeVO.setPercentage(percentage);
                                    list.add(curNodeVO);
                                }
                                if (EmptyUtils.isNotEmpty(getV())) {
                                    getV().initAdapterData(list);
                                    getTotalDelegatedRes();
                                }
                            }
                        } else {
                            if (EmptyUtils.isNotEmpty(getV())) {
                                getV().showEmptyOrFinish();
                            }
                        }
                    }

                    @Override
                    public void onError(Response<FetchBPDetailsResult> response) {
                        super.onError(response);
                        if (EmptyUtils.isNotEmpty(getV())) {
                            GemmaToastUtils.showLongToast(
                                    getV().getResources().getString(R.string.load_node_info_fail));
                            getV().showError();
                            if (response.body() != null && response.getRawResponse().body() != null) {
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
                    }
                });
    }

    public void executeVoteLogic(String from, List<String> producers, String privateKey) {
        //组装abi_json_to_bin请求参数
        VoteAbiJsonToBinReqParams params = new VoteAbiJsonToBinReqParams();
        params.setCode(VALUE_CODE);
        params.setAction(VALUE_ACTION);

        VoteAbiJsonToBinReqParams.ArgsBean args = new VoteAbiJsonToBinReqParams.ArgsBean();
        args.setProxy(VALUE_PROXY);
        args.setVoter(from);
        args.setProducers(producers);
        params.setArgs(args);

        String jsonParams = GsonUtils.objectToJson(params);
        LoggerManager.d("abijson", jsonParams);
        //请求获取binargs
        new AbiJsonToBeanRequest(AbiJsonToBeanResult.class)
                .setJsonParams(jsonParams)
                .getAbiJsonToBean(new JsonCallback<AbiJsonToBeanResult>() {
                    @Override
                    public void onSuccess(Response<AbiJsonToBeanResult> response) {
                        if (response.body() != null) {
                            String abistr = response.body().binargs;
                            LoggerManager.d("binargs", abistr);

                            getInfo(from, privateKey, abistr);
                        }
                    }

                    @Override
                    public void onError(Response<AbiJsonToBeanResult> response) {
                        super.onError(response);
                        if (response.body() != null && response.getRawResponse().body() != null){
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
                });

    }

    public void getInfo(String from, String privateKey, String abiStr) {
        new EOSConfigInfoRequest(String.class)
                .getInfo(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null && EmptyUtils.isNotEmpty(response.body())) {
                            String infostr = response.body();
                            LoggerManager.d("config info:" + infostr);
                            //C++库获取Transaction交易体
                            String transactionStr = JNIUtil.signTransaction_voteproducer(privateKey, VALUE_CONTRACT,
                                    from, infostr, abiStr, 0, 0, 120);
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
                            GemmaToastUtils.showShortToast(getV().getString(R.string.operate_deal_failed));
                            if (EmptyUtils.isNotEmpty(getV())) {
                                getV().dissmisProgressDialog();
                            }
                        }
                    }

                    @Override
                    public void onError(Response<String> response){
                        if (getV() != null){
                            super.onError(response);
                            AlertUtil.showLongUrgeAlert(getV().getActivity(), getV().getString(R.string.operate_deal_failed));
                            GemmaToastUtils.showShortToast(getV().getString(R.string.operate_deal_failed));
                            if (EmptyUtils.isNotEmpty(getV())) {
                                getV().dissmisProgressDialog();
                                if (response.body() != null && response.getRawResponse().body() != null){
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
                        }

                    }
                });
    }

    /**
     * 执行push transaction
     */
    public void pushTransaction(String jsonParams) {
        new PushTransactionRequest(String.class)
                .setJsonParams(jsonParams)
                .pushTransaction(new StringCallback() {
                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                        if (EmptyUtils.isNotEmpty(getV())) {
                            AlertUtil.showLongUrgeAlert(getV().getActivity(), getV().getString(R.string.operate_deal_failed));
                            getV().dissmisProgressDialog();
                            if (response.body() != null && response.getRawResponse().body() != null){
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
                    }

                    @Override
                    public void onSuccess(Response<String> response) {
                        if (EmptyUtils.isNotEmpty(getV())) {
                            AlertUtil.showLongCommonAlert(getV().getActivity(),
                                    getV().getString(R.string.operate_deal_success));
                            getV().dissmisProgressDialog();
                            if (response != null && EmptyUtils.isNotEmpty(response.body())) {
                                String jsonStr = response.body();
                                //停留在投票页面，更新数据
                                LoggerManager.d("pushTransaction json:" + jsonStr);
                                fetchBPDetail(ParamConstants.BP_NODE_NUMBERS);
                            }
                        }
                    }
                });
    }

    /**
     * 获取已抵押的资源数
     */
    public void getTotalDelegatedRes() {
        WalletEntity curWallet = DBManager.getInstance().getWalletEntityDao().getCurrentWalletEntity();
        if (EmptyUtils.isNotEmpty(curWallet)) {
            final String eosName = curWallet.getCurrentEosName();
            GetAccountInfoReqParams params = new GetAccountInfoReqParams();
            params.setAccount_name(eosName);
            String jsonParams = GsonUtils.objectToJson(params);
            LoggerManager.d("jsonParams", jsonParams);

            new GetAccountinfoRequest(AccountInfo.class)
                    .setJsonParams(jsonParams)
                    .getAccountInfo(new JsonCallback<AccountInfo>() {
                        @Override
                        public void onStart(Request<AccountInfo, ? extends Request> request) {
                            super.onStart(request);
                        }

                        @Override
                        public void onSuccess(Response<AccountInfo> response) {
                            if (response != null && response.body() != null) {
                                AccountInfo info = response.body();
                                if (EmptyUtils.isNotEmpty(info)) {
                                    AccountInfo.SelfDelegatedBandwidthBean resource = info
                                            .getSelf_delegated_bandwidth();
                                    if (EmptyUtils.isNotEmpty(resource)) {
                                        //有抵押资源
                                        String delegated_cpu = resource.getCpu_weightX();
                                        String delegated_net = resource.getNet_weightX();

                                        String[] cpu_amount_arr = delegated_cpu.split(" ");
                                        String[] net_amount_arr = delegated_net.split(" ");

                                        String cpu_amount = cpu_amount_arr[0];
                                        String net_amount = net_amount_arr[0];

                                        String total_resource = AmountUtil.add(cpu_amount, net_amount, 4) + " EOS";
                                        if (EmptyUtils.isNotEmpty(getV())) {
                                            getV().hasDelegatedRes(true);
                                            getV().setTotalDelegatedResource(total_resource);
                                            getV().showContent();
                                        }

                                    } else {
                                        //该账号没有给自己抵押资源
                                        if (EmptyUtils.isNotEmpty(getV())) {
                                            getV().hasDelegatedRes(false);
                                            getV().showEmptyOrFinish();
                                            GemmaToastUtils.showLongToast(
                                                    getV().getResources().getString(R.string.not_enough_delegated_res));
                                        }
                                    }
                                }
                            }
                            getV().dissmisProgressDialog();
                        }

                        @Override
                        public void onError(Response<AccountInfo> response) {
                            if (EmptyUtils.isNotEmpty(getV())) {
                                getV().showError();
                                GemmaToastUtils.showLongToast(
                                        getV().getResources().getString(R.string.load_avail_res_fail));
                                if (response.body() != null && response.getRawResponse().body() != null){
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
                        }
                    });
        }
    }

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
