package com.cybex.gma.client.ui.presenter;

import com.cybex.gma.client.R;
import com.cybex.gma.client.api.callback.JsonCallback;
import com.cybex.gma.client.manager.LoggerManager;
import com.cybex.gma.client.manager.UISkipMananger;
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
import com.cybex.gma.client.utils.AmountUtil;
import com.hxlx.core.lib.mvp.lite.XPresenter;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.GsonUtils;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import java.util.List;

public class BuySellRamPresenter extends XPresenter<BuySellRamFragment> {

    private static final int OPERATION_BUY_RAM = 1;
    private static final int OPERATION_SELL_RAM = 2;
    private static final String RAM_SCOPE = "eosio";
    private static final String RAM_CODE = "eosio";
    private static final String RAM_TABLE = "rammarket";
    private static final String VALUE_CONTRACT = "eosio";
    private static final String VALUE_COMPRESSION = "none";
    private static final String VALUE_ACTION_BUY_RAM = "buyram";
    private static final String VALUE_ACTION_SELL_RAM = "sellram";


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
                        getV().dissmisProgressDialog();

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
                            //跳转到收支记录
                            UISkipMananger.launchTransferRecord(getV().getActivity());
                        }

                    }
                });

    }

    /**
     * 获取当前链上ram市场信息
     *
     * @return List中的四个参数依次为base_balance, quote_balance,quote_weight, 1EOS对应的RAM价格
     */
    public void getRamMarketInfo() {
        //List<String> args = new ArrayList<>();
        //String price = "";
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
                                         public void onStart(Request<String, ? extends Request> request) {
                                             super.onStart(request);
                                             getV().showProgressDialog(getV().getResources().getString(R.string.loading_cur_ram_price));
                                         }

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
                                                     //args.add(bae_balance[0]);
                                                     //args.add(quote_balance[0]);
                                                     //args.add(quote_weight);
                                                     String ramRatio = AmountUtil.div(quote_balance[0], base_balance[0], 8);
                                                     String ramUnitPrice = AmountUtil.mul(ramRatio, quote_weight, 8);
                                                     String ramUnitPriceKB = AmountUtil.mul(ramUnitPrice, "1024", 4);
                                                     getV().setRamUnitPrice(ramUnitPriceKB);
                                                     //args.add(ramUnitPrice);
                                                 }
                                             } catch (Exception e) {
                                                 e.printStackTrace();
                                             }
                                             getV().dissmisProgressDialog();
                                         }

                                         @Override
                                         public void onError(Response<String> response) {
                                             LoggerManager.d("on Error");
                                             GemmaToastUtils.showLongToast(getV().getResources().getString(R.string
                                                     .load_cur_ram_price_fail));
                                             getV().dissmisProgressDialog();
                                         }
                                     }
                );
    }

}
