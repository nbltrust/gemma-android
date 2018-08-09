package com.cybex.gma.client.ui.presenter;

import com.cybex.gma.client.api.callback.JsonCallback;
import com.cybex.gma.client.manager.LoggerManager;
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
import com.hxlx.core.lib.mvp.lite.XPresenter;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.GsonUtils;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import java.util.ArrayList;
import java.util.List;


public class BuySellRamPresenter extends XPresenter<BuySellRamFragment> {

    private static final int OPERATION_BUY_RAM = 1;
    private static final int OPERATION_SELL_RAM = 2;
    private static final int OPERATION_DELEGATE = 3;
    private static final int OPERATION_UNDELEGATE = 4;
    private static final String SCOPE = "eosio";
    private static final String CODE = "eosio";
    private static final String TABLE = "rammarket";
    private static final String VALUE_CODE = "eosio.token";
    private static final String VALUE_CONTRACT = "eosio.token";
    private static final String VALUE_COMPRESSION = "none";

    public void executeTransactionLogic(int operation_type, String from, String to, String quantity, String memo,
            String privateKey){
        String abijson = "";
        //通过C++获取abi操作体
        switch (operation_type){
            case OPERATION_BUY_RAM:
                abijson = "";
                break;
            case OPERATION_SELL_RAM:
                abijson = "";
                break;
            case OPERATION_DELEGATE:
                abijson = "";
                break;
            case OPERATION_UNDELEGATE:
                abijson = "";
                break;
        }

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

                            getInfo(from, privateKey, binargs);


                        } else {
                            GemmaToastUtils.showShortToast("操作失败");
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
                        GemmaToastUtils.showShortToast("操作失败");
                        getV().dissmisProgressDialog();
                    }

                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null && EmptyUtils.isNotEmpty(response.body())) {
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
                            GemmaToastUtils.showShortToast("操作失败");
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
                            //todo 页面刷新，什么数据该更新显示？

                        }

                    }
                });

    }

    /**
     * 获取当前链上ram市场信息
     * @return List中的三个参数依次为base_balance, quote_balance,quote_weight
     */
   public List<String> getRamMarketInfo(){
       List<String> args = new ArrayList<>();
       GetRamMarketReqParams params = new GetRamMarketReqParams();
       params.setScope(SCOPE);
       params.setCode(CODE);
       params.setTable(TABLE);
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
                           if (result != null){
                               List<RamMarketRows> rows = result.rows;
                               RamMarketBase base = rows.get(0).base;
                               RamMarketBase quote = rows.get(0).quote;

                               String base_balance = base.balance;
                               String quote_balance = quote.balance;
                               String quote_weight = quote.weight;

                               args.add(base_balance);
                               args.add(quote_balance);
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
     * 估算EOS和RAM之间的换算关系
     */
   public double calculateApproxiValue(List<String> args, double cardinalNum){
       double baseBalance = Double.parseDouble(args.get(0));
       double quoteBalance = Double.parseDouble(args.get(1));
       double quoteWeight = Double.parseDouble(args.get(2));

       double ramPrice = (quoteBalance/baseBalance) * quoteWeight;
       double price = ramPrice * cardinalNum;

       return price;
   }

}
