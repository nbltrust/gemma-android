package com.cybex.gma.client.ui.presenter;

import com.cybex.componentservice.api.callback.JsonCallback;
import com.cybex.gma.client.R;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.gma.client.ui.JNIUtil;
import com.cybex.gma.client.ui.fragment.BuySellRamFragment;
import com.cybex.gma.client.ui.model.request.GetRamMarketReqParams;
import com.cybex.gma.client.ui.model.request.PushTransactionReqParams;
import com.cybex.gma.client.ui.model.response.AbiJsonToBeanResult;
import com.cybex.gma.client.ui.model.response.GetRamMarketResult;
import com.cybex.gma.client.ui.model.response.RamMarketBase;
import com.cybex.gma.client.ui.model.response.RamMarketRows;
import com.cybex.gma.client.ui.model.vo.TransferTransactionVO;
import com.cybex.gma.client.ui.request.AbiJsonToBeanRequest;
import com.cybex.gma.client.ui.request.EOSConfigInfoRequest;
import com.cybex.gma.client.ui.request.GetRamMarketRequest;
import com.cybex.gma.client.ui.request.PushTransactionRequest;
import com.cybex.componentservice.utils.AmountUtil;
import com.hxlx.core.lib.mvp.lite.XPresenter;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.GsonUtils;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import java.util.ArrayList;
import java.util.List;


public class PushTransactionPresenter extends XPresenter<BuySellRamFragment> {

    private static final int OPERATION_BUY_RAM = 1;
    private static final int OPERATION_SELL_RAM = 2;
    private static final int OPERATION_DELEGATE = 3;
    private static final int OPERATION_UNDELEGATE = 4;
    private static final String RAM_SCOPE = "eosio";
    private static final String RAM_CODE = "eosio";
    private static final String RAM_TABLE = "rammarket";
    private static final String VALUE_CODE = "eosio";
    private static final String VALUE_CONTRACT = "eosio";
    private static final String VALUE_COMPRESSION = "none";
    private static final String VALUE_ACTION_BUY_RAM = "buyram";
    private static final String VALUE_ACTION_SELL_RAM = "sellram";
    private static final String VALUE_ACTION_DELEGATE = "delegatebw";
    private static final String VALUE_ACTION_UNDELEGATE = "undelegatebw";
    private static final String UNUSED_STRING = "";

    /**
     * 执行Delegate逻辑
     *
     * @param from 付EOS的账号
     * @param to 收到资源的账号
     * @param stake_net_quantity
     * @param stake_cpu_quantity
     * @param privateKey
     */
    public void executeDelegateLogic(
            String from, String to, String stake_net_quantity, String stake_cpu_quantity,
            String privateKey) {
        //通过C++获取abi操作体
        String abijson = JNIUtil.create_abi_req_delegatebw(RAM_CODE, VALUE_ACTION_DELEGATE, from, to,
                stake_net_quantity, stake_cpu_quantity);

        //链上接口请求 abi_json_to_bin
        new AbiJsonToBeanRequest(AbiJsonToBeanResult.class)
                .setJsonParams(abijson)
                .getAbiJsonToBean(new JsonCallback<AbiJsonToBeanResult>() {
                    @Override
                    public void onStart(Request<AbiJsonToBeanResult, ? extends Request> request) {
                        super.onStart(request);
                        getV().showProgressDialog(getV().getString(R.string.operate_deal_ing));
                    }

                    @Override
                    public void onError(Response<AbiJsonToBeanResult> response) {
                        super.onError(response);
                        GemmaToastUtils.showShortToast(getV().getString(R.string.operate_deal_failed));
                        getV().dissmisProgressDialog();
                    }

                    @Override
                    public void onSuccess(Response<AbiJsonToBeanResult> response) {
                        if (response != null && response.body() != null) {
                            AbiJsonToBeanResult result = response.body();
                            String binargs = result.binargs;
                            LoggerManager.d("abiStr: " + binargs);

                            getInfo(OPERATION_DELEGATE, from, privateKey, binargs);


                        } else {
                            GemmaToastUtils.showShortToast(getV().getString(R.string.operate_deal_failed));

                        }

                    }
                });
    }

    public void executeUndelegateLogic(
            String from, String to, String unstake_net_quantity, String unstake_cpu_quantity,
            String privateKey) {

        //通过C++获取abi操作体
        String abijson = JNIUtil.create_abi_req_undelegatebw(RAM_CODE, VALUE_ACTION_UNDELEGATE, from, to,
                unstake_net_quantity, unstake_cpu_quantity);

        //链上接口请求 abi_json_to_bin
        new AbiJsonToBeanRequest(AbiJsonToBeanResult.class)
                .setJsonParams(abijson)
                .getAbiJsonToBean(new JsonCallback<AbiJsonToBeanResult>() {
                    @Override
                    public void onStart(Request<AbiJsonToBeanResult, ? extends Request> request) {
                        super.onStart(request);
                        getV().showProgressDialog(getV().getString(R.string.operate_deal_ing));
                    }

                    @Override
                    public void onError(Response<AbiJsonToBeanResult> response) {
                        super.onError(response);
                        GemmaToastUtils.showShortToast(getV().getString(R.string.operate_deal_failed));
                        getV().dissmisProgressDialog();
                    }

                    @Override
                    public void onSuccess(Response<AbiJsonToBeanResult> response) {
                        if (response != null && response.body() != null) {
                            AbiJsonToBeanResult result = response.body();
                            String binargs = result.binargs;
                            LoggerManager.d("abiStr: " + binargs);

                            getInfo(OPERATION_UNDELEGATE, from, privateKey, binargs);


                        } else {
                            GemmaToastUtils.showShortToast(getV().getString(R.string.operate_deal_failed));
                        }

                    }
                });

    }


    public void executeBuyRamLogic(
            String from, String to, String quantity,
            String privateKey) {

        //通过C++获取abi操作体
        String abijson = JNIUtil.fincreate_abi_req_buyram(RAM_CODE, VALUE_ACTION_BUY_RAM, from, to, quantity);
        LoggerManager.d("abijson_BuyRam", abijson);
        //链上接口请求 abi_json_to_bin
        new AbiJsonToBeanRequest(AbiJsonToBeanResult.class)
                .setJsonParams(abijson)
                .getAbiJsonToBean(new JsonCallback<AbiJsonToBeanResult>() {
                    @Override
                    public void onStart(Request<AbiJsonToBeanResult, ? extends Request> request) {
                        super.onStart(request);
                        getV().showProgressDialog(getV().getString(R.string.operate_deal_ing));
                    }

                    @Override
                    public void onError(Response<AbiJsonToBeanResult> response) {
                        super.onError(response);
                        GemmaToastUtils.showShortToast(getV().getString(R.string.operate_deal_failed));
                        LoggerManager.d(response.code());
                        getV().dissmisProgressDialog();
                    }

                    @Override
                    public void onSuccess(Response<AbiJsonToBeanResult> response) {
                        if (response != null && response.body() != null) {
                            AbiJsonToBeanResult result = response.body();
                            String binargs = result.binargs;
                            LoggerManager.d("abiStr: " + binargs);

                            getInfo(OPERATION_BUY_RAM, from, privateKey, binargs);


                        } else {
                            GemmaToastUtils.showShortToast(getV().getString(R.string.operate_deal_failed));
                        }

                    }
                });

    }

    /**
     * @param
     * @param account 谁收到卖出RAM对应的EOS，这里应该设置为本账号
     * @param bytes
     * @param privateKey
     */
    public void executeSellRamLogic(String account, long bytes, String privateKey) {

        //通过C++获取abi操作体
        String abijson = JNIUtil.create_abi_req_sellram(RAM_CODE, VALUE_ACTION_SELL_RAM, account, bytes);

        //链上接口请求 abi_json_to_bin
        new AbiJsonToBeanRequest(AbiJsonToBeanResult.class)
                .setJsonParams(abijson)
                .getAbiJsonToBean(new JsonCallback<AbiJsonToBeanResult>() {
                    @Override
                    public void onStart(Request<AbiJsonToBeanResult, ? extends Request> request) {
                        super.onStart(request);
                        getV().showProgressDialog(getV().getString(R.string.operate_deal_ing));
                    }

                    @Override
                    public void onError(Response<AbiJsonToBeanResult> response) {
                        super.onError(response);
                        GemmaToastUtils.showShortToast(getV().getString(R.string.operate_deal_failed));
                        getV().dissmisProgressDialog();
                    }

                    @Override
                    public void onSuccess(Response<AbiJsonToBeanResult> response) {
                        if (response != null && response.body() != null) {
                            AbiJsonToBeanResult result = response.body();
                            String binargs = result.binargs;
                            LoggerManager.d("abiStr: " + binargs);

                            getInfo(OPERATION_SELL_RAM, account, privateKey, binargs);


                        } else {
                            GemmaToastUtils.showShortToast(getV().getString(R.string.operate_deal_failed));
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
    public void getInfo(int operation_type, String from, String privateKey, String abiStr) {
        new EOSConfigInfoRequest(String.class)
                .getInfo(new StringCallback() {

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        GemmaToastUtils.showShortToast(getV().getString(R.string.operate_deal_failed));
                        getV().dissmisProgressDialog();
                    }

                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null && EmptyUtils.isNotEmpty(response.body())) {
                            String infostr = response.body();
                            LoggerManager.d("config info:" + infostr);
                            //C++库获取Transaction交易体
                            String transactionStr = "";
                            switch (operation_type) {
                                case OPERATION_DELEGATE:
                                    transactionStr = JNIUtil.signTransaction_delegatebw(privateKey, VALUE_CONTRACT,
                                            from, infostr, abiStr, 0, 0, 120);
                                    break;
                                case OPERATION_UNDELEGATE:
                                    transactionStr = JNIUtil.signTransaction_undelegatebw(privateKey, VALUE_CONTRACT,
                                            from, infostr, abiStr, 0, 0, 120);
                                    break;
                                case OPERATION_BUY_RAM:
                                    transactionStr = JNIUtil.signTransaction_buyram(privateKey, VALUE_CONTRACT,
                                            from, infostr, abiStr, 0, 0, 120);
                                    break;
                                case OPERATION_SELL_RAM:
                                    transactionStr = JNIUtil.signTransaction_sellram(privateKey, VALUE_CONTRACT,
                                            from, infostr, abiStr, 0, 0, 120);
                                    break;
                                default:
                                    break;
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
                            GemmaToastUtils.showShortToast(getV().getString(R.string.operate_deal_failed));
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

                            GemmaToastUtils.showLongToast(getV().getString(R.string.operate_deal_success));
                            //todo 页面刷新，什么数据该更新显示？

                        }

                    }
                });

    }

    /**
     * 获取当前链上ram市场信息
     *
     * @return List中的三个参数依次为base_balance, quote_balance,quote_weight
     */
    public List<String> getRamMarketInfo() {
        List<String> args = new ArrayList<>();
        GetRamMarketReqParams params = new GetRamMarketReqParams();
        params.setScope(RAM_SCOPE);
        params.setCode(RAM_CODE);
        params.setTable(RAM_TABLE);
        params.setJson(true);

        String jsonParams = GsonUtils.objectToJson(params);

        new GetRamMarketRequest(String.class)
                .setJsonParams(jsonParams)
                .getRamMarketRequest(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String infoJson = response.body();
                        LoggerManager.d("ram market info:" + infoJson);
                        try {
                            GetRamMarketResult result = GsonUtils.jsonToBean(infoJson, GetRamMarketResult.class);
                            if (result != null) {
                                List<RamMarketRows> rows = result.rows;
                                RamMarketBase base = rows.get(0).base;
                                RamMarketBase quote = rows.get(0).quote;

                                String[] base_balance = base.balance.split(" ");
                                String[] quote_balance = quote.balance.split(" ");
                                String quote_weight = quote.weight;

                                args.add(base_balance[0]);
                                args.add(quote_balance[0]);
                                args.add(quote_weight);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        LoggerManager.d("on Error");
                    }
                });

        return args;
    }

    /**
     * 输入EOS数额得RAM数量估值
     */
    public String calEos2Ram(List<String> args, String eosNum) {

        String baseBalance = args.get(0);
        String quoteBalance = args.get(1);
        String quoteWeight = args.get(2);

        String ramRatio = AmountUtil.div(quoteBalance, baseBalance, 10);
        String ramUnitPrice = AmountUtil.mul(ramRatio, quoteWeight, 10);
        String ramPrice = AmountUtil.mul(ramUnitPrice, eosNum, 10);
        return ramPrice;
    }

    /**
     * 输入RAM数额得EOS估值
     */
    public String calRam2Eos(List<String> args, String ramAmount) {

        String baseBalance = args.get(0);
        String quoteBalance = args.get(1);
        String quoteWeight = args.get(2);


        return "";
    }

}
