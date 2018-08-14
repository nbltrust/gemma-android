package com.cybex.gma.client.ui.presenter;

import com.cybex.gma.client.api.callback.JsonCallback;
import com.cybex.gma.client.manager.LoggerManager;
import com.cybex.gma.client.ui.JNIUtil;
import com.cybex.gma.client.ui.fragment.DelegateFragment;
import com.cybex.gma.client.ui.model.request.PushTransactionReqParams;
import com.cybex.gma.client.ui.model.response.AbiJsonToBeanResult;
import com.cybex.gma.client.ui.model.vo.TransferTransactionVO;
import com.cybex.gma.client.ui.request.AbiJsonToBeanRequest;
import com.cybex.gma.client.ui.request.EOSConfigInfoRequest;
import com.cybex.gma.client.ui.request.PushTransactionRequest;
import com.hxlx.core.lib.mvp.lite.XPresenter;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.GsonUtils;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

public class DelegatePresenter extends XPresenter<DelegateFragment> {

    private static final int OPERATION_DELEGATE = 3;
    private static final int OPERATION_UNDELEGATE = 4;
    private static final String VALUE_CODE = "eosio";
    private static final String VALUE_CONTRACT = "eosio";
    private static final String VALUE_COMPRESSION = "none";
    private static final String VALUE_ACTION_DELEGATE = "delegatebw";
    private static final String VALUE_ACTION_UNDELEGATE = "undelegatebw";

    /**
     * 执行Delegate逻辑
     * @param from 付EOS的账号
     * @param to 收到资源的账号
     * @param stake_net_quantity
     * @param stake_cpu_quantity
     * @param privateKey
     */
    public void executeDelegateLogic(String from, String to, String stake_net_quantity, String stake_cpu_quantity,
            String privateKey){
        //通过C++获取abi操作体
        String abijson = JNIUtil.create_abi_req_delegatebw(VALUE_CODE, VALUE_ACTION_DELEGATE, from, to,
                stake_net_quantity, stake_cpu_quantity);

        //链上接口请求 abi_json_to_bin
        new AbiJsonToBeanRequest(AbiJsonToBeanResult.class)
                .setJsonParams(abijson)
                .getAbiJsonToBean(new JsonCallback<AbiJsonToBeanResult>() {
                    @Override
                    public void onStart(Request<AbiJsonToBeanResult, ? extends Request> request) {
                        super.onStart(request);
                        getV().showProgressDialog("操作处理中...");
                    }

                    @Override
                    public void onError(Response<AbiJsonToBeanResult> response) {
                        super.onError(response);
                        GemmaToastUtils.showShortToast("操作失败");
                        getV().dissmisProgressDialog();
                    }

                    @Override
                    public void onSuccess(Response<AbiJsonToBeanResult> response) {
                        if (response != null && response.body() != null) {

                            AbiJsonToBeanResult result = response.body();
                            String binargs = result.binargs;
                            LoggerManager.d("abiStr: " + binargs);

                            getInfo(OPERATION_DELEGATE ,from, privateKey, binargs);

                        } else {
                            GemmaToastUtils.showShortToast("操作失败");
                        }
                        getV().dissmisProgressDialog();

                    }
                });
    }

    public void executeUndelegateLogic(String from, String to, String unstake_net_quantity, String unstake_cpu_quantity,
            String privateKey){

        //通过C++获取abi操作体
        String abijson = JNIUtil.create_abi_req_undelegatebw(VALUE_CODE, VALUE_ACTION_UNDELEGATE, from, to,
                unstake_net_quantity, unstake_cpu_quantity);

        //链上接口请求 abi_json_to_bin
        new AbiJsonToBeanRequest(AbiJsonToBeanResult.class)
                .setJsonParams(abijson)
                .getAbiJsonToBean(new JsonCallback<AbiJsonToBeanResult>() {
                    @Override
                    public void onStart(Request<AbiJsonToBeanResult, ? extends Request> request) {
                        super.onStart(request);
                        getV().showProgressDialog("操作处理中...");
                    }

                    @Override
                    public void onError(Response<AbiJsonToBeanResult> response) {
                        super.onError(response);
                        GemmaToastUtils.showShortToast("操作失败");
                        getV().dissmisProgressDialog();
                    }

                    @Override
                    public void onSuccess(Response<AbiJsonToBeanResult> response) {
                        if (response != null && response.body() != null) {
                            AbiJsonToBeanResult result = response.body();
                            String binargs = result.binargs;
                            LoggerManager.d("abiStr: " + binargs);

                            getInfo(OPERATION_UNDELEGATE ,from, privateKey, binargs);


                        } else {
                            GemmaToastUtils.showShortToast("操作失败");
                        }
                        getV().dissmisProgressDialog();

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
    public void getInfo(int operation_type, String from, String privateKey, String abiStr) {
        new EOSConfigInfoRequest(String.class)
                .getInfo(new StringCallback() {

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        GemmaToastUtils.showShortToast("操作失败");
                        getV().dissmisProgressDialog();
                    }

                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null && EmptyUtils.isNotEmpty(response.body())) {
                            String infostr = response.body();
                            LoggerManager.d("config info:" + infostr);
                            //C++库获取Transaction交易体
                            String transactionStr = "";
                            switch (operation_type){
                                case OPERATION_DELEGATE:
                                    transactionStr = JNIUtil.signTransaction_delegatebw(privateKey, VALUE_CONTRACT,
                                            from, infostr, abiStr, 0,0,120);
                                    break;
                                case OPERATION_UNDELEGATE:
                                    transactionStr = JNIUtil.signTransaction_undelegatebw(privateKey, VALUE_CONTRACT,
                                            from, infostr, abiStr, 0,0,120);
                                    break;
                                default:
                                    LoggerManager.d("参数错误");
                            }

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
                            GemmaToastUtils.showShortToast("getInfo操作失败");
                            getV().dissmisProgressDialog();
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
                        getV().dissmisProgressDialog();
                    }

                    @Override
                    public void onSuccess(Response<String> response) {
                        getV().dissmisProgressDialog();
                        if (response != null && EmptyUtils.isNotEmpty(response.body())) {
                            String jsonStr = response.body();
                            LoggerManager.d("pushTransaction json:" + jsonStr);

                            GemmaToastUtils.showLongToast("操作成功");
                            //页面跳转至收支记录
                            //UISkipMananger.launchTransferRecord(getV().getActivity());

                        }

                    }
                });

    }

}
