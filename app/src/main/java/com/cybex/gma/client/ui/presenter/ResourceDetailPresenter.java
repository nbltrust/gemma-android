package com.cybex.gma.client.ui.presenter;

import com.cybex.componentservice.api.callback.JsonCallback;
import com.cybex.componentservice.db.entity.EosWalletEntity;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.componentservice.utils.AmountUtil;
import com.cybex.gma.client.R;
import com.cybex.gma.client.ui.activity.ResourceDetailActivity;
import com.cybex.gma.client.ui.model.request.GetAccountInfoReqParams;
import com.cybex.gma.client.ui.model.request.GetCurrencyBalanceReqParams;
import com.cybex.gma.client.ui.model.request.GetRamMarketReqParams;
import com.cybex.gma.client.ui.model.response.AccountInfo;
import com.cybex.gma.client.ui.model.response.GetRamMarketResult;
import com.cybex.gma.client.ui.model.response.RamMarketBase;
import com.cybex.gma.client.ui.model.response.RamMarketRows;
import com.cybex.gma.client.ui.model.vo.ResourceInfoVO;
import com.cybex.gma.client.ui.request.GetAccountinfoRequest;
import com.cybex.gma.client.ui.request.GetCurrencyBalanceRequest;
import com.cybex.gma.client.ui.request.GetRamMarketRequest;
import com.hxlx.core.lib.mvp.lite.XPresenter;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.GsonUtils;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import org.json.JSONArray;

import java.util.List;

import io.reactivex.exceptions.Exceptions;

public class ResourceDetailPresenter extends XPresenter<ResourceDetailActivity> {

    private static final String RAM_SCOPE = "eosio";
    private static final String RAM_CODE = "eosio";
    private static final String RAM_TABLE = "rammarket";
    private static final String VALUE_CODE = "eosio.token";
    private static final String VALUE_SYMBOL_EOS = "EOS";

    /**
     * 获取当前链上ram市场信息
     */
    public void getRamMarketInfo() {

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
                                             if (getV() != null) {
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

                                                         String ramRatio = AmountUtil.div(quote_balance[0],
                                                                 base_balance[0], 8);
                                                         String ramUnitPriceKB = AmountUtil.mul(ramRatio, "1024",
                                                                 4);//  eos per kb
                                                         getV().setRamUnitPriceKB(ramUnitPriceKB);
                                                         getV().setRamUnitPrice(ramUnitPriceKB);

                                                     }
                                                 } catch (Exception e) {
                                                     e.printStackTrace();
                                                 }

                                                 getV().dissmisProgressDialog();
                                             }
                                         }

                                         @Override
                                         public void onError(Response<String> response) {
                                             LoggerManager.d("on Error");
                                             if (getV() != null) {
                                                 GemmaToastUtils.showLongToast(getV().getResources().getString(R.string
                                                         .eos_load_cur_ram_price_fail));
                                                 getV().dissmisProgressDialog();
                                             }
                                         }
                                     }
                );
    }

    public void getAccountInfo(String balance) {
        try {
            MultiWalletEntity entity = DBManager.getInstance().getMultiWalletEntityDao().getCurrentMultiWalletEntity();
            EosWalletEntity eosEntity = entity.getEosWalletEntities().get(0);

            String account_name = eosEntity.getCurrentEosName();

            GetAccountInfoReqParams params = new GetAccountInfoReqParams();
            params.setAccount_name(account_name);

            String jsonParams = GsonUtils.objectToJson(params);
            new GetAccountinfoRequest(AccountInfo.class)
                    .setJsonParams(jsonParams)
                    .getAccountInfo(new JsonCallback<AccountInfo>() {

                        @Override
                        public void onError(Response<AccountInfo> response) {
                            if (getV() != null) {
                                getV().dissmisProgressDialog();
                                GemmaToastUtils.showLongToast(getV().getString(R.string
                                        .eos_load_account_info_fail));
                            }
                        }

                        @Override
                        public void onSuccess(Response<AccountInfo> response) {
                            if (getV() != null) {
                                if (response != null && response.body() != null) {
                                    getRamMarketInfo();
                                    AccountInfo info = response.body();
                                    ResourceInfoVO resourceInfoVO = getResourceInfo(info, balance);
                                    getV().showResourceInfo(resourceInfoVO);

                                }

                            }
                        }
                    });
        } catch (Throwable t) {
            throw Exceptions.propagate(t);
        }
    }

    /**
     * 获取当前EOS余额
     */
    public void requestBalanceInfo() {
        MultiWalletEntity entity = DBManager.getInstance().getMultiWalletEntityDao().getCurrentMultiWalletEntity();
        EosWalletEntity eosEntity = entity.getEosWalletEntities().get(0);
        if (eosEntity == null) { return; }

        String currentEOSName = eosEntity.getCurrentEosName();
        GetCurrencyBalanceReqParams params = new GetCurrencyBalanceReqParams();
        params.setAccount(currentEOSName);
        params.setCode(VALUE_CODE);
        params.setSymbol(VALUE_SYMBOL_EOS);
        String jsonParams = GsonUtils.objectToJson(params);

        new GetCurrencyBalanceRequest(String.class)
                .setJsonParams(jsonParams)
                .getCurrencyBalance(new StringCallback() {


                    @Override
                    public void onStart(Request<String, ? extends Request> request) {
                        if (getV() != null) {
                            super.onStart(request);
                            getV().showProgressDialog(
                                    getV().getResources().getString(R.string.eos_loading_account_info));
                        }

                    }

                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            if (getV() != null) {
                                if (response != null) {
                                    String jsonStr = response.body();
                                    LoggerManager.d("response json balance:" + jsonStr);
                                    if (response.body() != null) {
                                        JSONArray array = new JSONArray(jsonStr);
                                        if (array.length() > 0) {
                                            String balance = array.optString(0);
                                            if (EmptyUtils.isNotEmpty(balance)) {
                                                getAccountInfo(balance);
                                            }
                                        }else {
                                            //可用余额为0，链上返回空
                                           getAccountInfo("0.0000");
                                        }
                                    }
                                }else {
                                    GemmaToastUtils.showLongToast(
                                            getV().getString(R.string.eos_load_account_info_fail));
                                }
                            }

                        } catch (Throwable t) {
                            throw Exceptions.propagate(t);
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        if (getV() != null) {
                            getV().dissmisProgressDialog();
                        }
                    }
                });
    }


    private ResourceInfoVO getResourceInfo(AccountInfo info, String balance) {
        ResourceInfoVO resourceInfoVO = new ResourceInfoVO();
        if (info != null) {
            //CPU使用进度
            AccountInfo.CpuLimitBean cpuLimitBean = info.getCpu_limit();
            int cpuUsed = 0;
            int cpuTotal = 0;
            if (cpuLimitBean != null) {
                cpuUsed = cpuLimitBean.getUsed();
                cpuTotal = cpuLimitBean.getMax();
            }
            double i = cpuUsed / (double) cpuTotal * 100;
            int cpuProgress = (int) Math.ceil(i);

            //NET使用进度
            AccountInfo.NetLimitBean netLimitBean = info.getNet_limit();
            int netUsed = 0;
            int netTotal = 0;
            if (netLimitBean != null) {
                netUsed = netLimitBean.getUsed();
                netTotal = netLimitBean.getMax();
            }
            double j = netUsed / (double) netTotal * 100;
            int netProgress = (int) Math.ceil(j);

            //ram使用进度
            int ramTotal = info.getRam_quota();
            int ramUsed = info.getRam_usage();
            double k = ramUsed / (double) ramTotal * 100;
            int ramProgress = (int) Math.ceil(k);


            resourceInfoVO = new ResourceInfoVO();
            resourceInfoVO.setCpuProgress(cpuProgress);
            resourceInfoVO.setCpuUsed(cpuUsed);
            resourceInfoVO.setCpuTotal(cpuTotal);
            resourceInfoVO.setNetTotal(netTotal);
            resourceInfoVO.setNetProgress(netProgress);
            resourceInfoVO.setNetUsed(netUsed);
            resourceInfoVO.setRamUsed(ramUsed);
            resourceInfoVO.setRamProgress(ramProgress);
            resourceInfoVO.setRamTotal(ramTotal);
            resourceInfoVO.setCpuWeight(info.getCpu_weight());
            resourceInfoVO.setNetWeight(info.getNet_weight());
            resourceInfoVO.setBanlance(balance);
        }
        return resourceInfoVO;
    }
}
