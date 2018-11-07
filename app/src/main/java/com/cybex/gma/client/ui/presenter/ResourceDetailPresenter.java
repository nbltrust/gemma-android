package com.cybex.gma.client.ui.presenter;

import com.cybex.gma.client.R;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.gma.client.ui.activity.ResourceDetailActivity;
import com.cybex.gma.client.ui.model.request.GetRamMarketReqParams;
import com.cybex.gma.client.ui.model.response.GetRamMarketResult;
import com.cybex.gma.client.ui.model.response.RamMarketBase;
import com.cybex.gma.client.ui.model.response.RamMarketRows;
import com.cybex.gma.client.ui.request.GetRamMarketRequest;
import com.cybex.componentservice.utils.AmountUtil;
import com.hxlx.core.lib.mvp.lite.XPresenter;
import com.hxlx.core.lib.utils.GsonUtils;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import java.util.List;

public class ResourceDetailPresenter extends XPresenter<ResourceDetailActivity> {

    private static final String RAM_SCOPE = "eosio";
    private static final String RAM_CODE = "eosio";
    private static final String RAM_TABLE = "rammarket";

    /**
     * 获取当前链上ram市场信息
     *
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
                                         public void onStart(Request<String, ? extends Request> request) {
                                             super.onStart(request);
                                             getV().showProgressDialog(getV().getResources().getString(R.string.eos_loading_cur_ram_price));
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

                                                     String ramRatio = AmountUtil.div(quote_balance[0], base_balance[0], 8);
                                                     String ramUnitPrice = AmountUtil.mul(ramRatio, quote_weight, 8);
                                                     String ramUnitPriceKB = AmountUtil.mul(ramRatio, "1024",
                                                             4);//  eos per kb
                                                     getV().setRamUnitPrice(ramUnitPriceKB);
                                                     getV().setRamUnitPriceKB(ramUnitPriceKB);

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
                                                     .eos_load_cur_ram_price_fail));
                                             getV().dissmisProgressDialog();
                                         }
                                     }
                );
    }
}
