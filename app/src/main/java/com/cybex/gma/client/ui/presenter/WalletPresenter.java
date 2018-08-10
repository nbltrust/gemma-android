package com.cybex.gma.client.ui.presenter;

import com.cybex.gma.client.api.callback.JsonCallback;
import com.cybex.gma.client.db.dao.WalletEntityDao;
import com.cybex.gma.client.db.entity.WalletEntity;
import com.cybex.gma.client.manager.DBManager;
import com.cybex.gma.client.manager.LoggerManager;
import com.cybex.gma.client.ui.fragment.WalletFragment;
import com.cybex.gma.client.ui.model.request.GetCurrencyBalanceReqParams;
import com.cybex.gma.client.ui.model.response.AccountInfo;
import com.cybex.gma.client.ui.model.response.UnitPrice;
import com.cybex.gma.client.ui.model.vo.EOSNameVO;
import com.cybex.gma.client.ui.model.vo.HomeCombineDataVO;
import com.cybex.gma.client.ui.request.GetAccountinfoRequest;
import com.cybex.gma.client.ui.request.GetCurrencyBalanceRequest;
import com.cybex.gma.client.ui.request.UnitPriceRequest;
import com.hxlx.core.lib.mvp.lite.XPresenter;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.GsonUtils;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function3;
import io.reactivex.schedulers.Schedulers;

/**
 * 钱包Presenter
 *
 * Created by wanglin on 2018/7/9.
 */
public class WalletPresenter extends XPresenter<WalletFragment> {

    private static final String VALUE_CODE = "eosio.token";
    private static final String VALUE_CONTRACT = "eosio.token";
    private static final String VALUE_COMPRESSION = "none";
    private static final String VALUE_SYMBOL = "EOS";

    private static final String MAP_KEY_ACCOUNT_INFO = "account_info";
    private static final String MAP_KEY_UNIT_PRICE = "unit_price";
    private static final String MAP_KEY_BANLANCE = "key_banlance";

    /**
     * 切换eos账户
     */
    public List<EOSNameVO> getEOSNameVOList() {
        List<EOSNameVO> voList = new ArrayList<>();
        WalletEntity entity = DBManager.getInstance().getWalletEntityDao().getCurrentWalletEntity();
        if (entity != null) {
            List<String> eosNameList = GsonUtils.parseString2List(entity.getEosNameJson(), String.class);

            if (EmptyUtils.isNotEmpty(eosNameList) && eosNameList.size() > 1) {
                for (int i = 0; i < eosNameList.size(); i++) {
                    String eosName = eosNameList.get(i);
                    EOSNameVO vo = new EOSNameVO();
                    if (eosName.equals(entity.getCurrentEosName())) {
                        vo.isChecked = true;
                    } else {
                        vo.isChecked = false;
                    }

                    vo.setEosName(eosName);
                    voList.add(vo);
                }
            }
        }

        return voList;

    }


    public void saveNewEntity(String currentEOSName) {
        WalletEntityDao dao = DBManager.getInstance().getWalletEntityDao();
        WalletEntity entity = dao.getCurrentWalletEntity();
        if (entity != null) {
            entity.setCurrentEosName(currentEOSName);
            dao.saveOrUpateEntity(entity);
        }
    }


    /**
     * 获取首页聚合数据
     */
    public void requestHomeCombineDataVO() {
        Observable.combineLatest(getAccountObserver, unitPriceObserver, banlanceObserver,
                new Function3<AccountInfo, String, String, HomeCombineDataVO>() {
                    @Override
                    public HomeCombineDataVO apply(AccountInfo accountInfo, String unitPrice, String banlance) throws
                            Exception {

                        HomeCombineDataVO vo = new HomeCombineDataVO();
                        vo.setAccountInfo(accountInfo);
                        vo.setBanlance(banlance);
                        vo.setUnitPrice(unitPrice);

                        return vo;
                    }

                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<HomeCombineDataVO>() {
                    @Override
                    public void accept(HomeCombineDataVO vo) throws Exception {
                        getV().showMainInfo(vo);

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                    }
                });


    }


    Observable<AccountInfo> getAccountObserver =
            Observable.create(new ObservableOnSubscribe<AccountInfo>() {

                @Override
                public void subscribe(ObservableEmitter<AccountInfo> e) {
                    try {
                        new GetAccountinfoRequest(AccountInfo.class)
                                .getAccountInfo(new JsonCallback<AccountInfo>() {
                                    @Override
                                    public void onSuccess(Response<AccountInfo> response) {
                                        if (response != null && response.body() != null) {
                                            AccountInfo info = response.body();
                                            if (info != null) {
                                                e.onNext(info);
                                                e.onComplete();
                                            }
                                        }

                                    }
                                });
                    } catch (Throwable t) {
                        throw Exceptions.propagate(t);
                    }
                }
            }).subscribeOn(Schedulers.io());


    Observable<String> unitPriceObserver = Observable.create(new ObservableOnSubscribe<String>() {
        @Override
        public void subscribe(ObservableEmitter<String> emitter) {
            try {
                new UnitPriceRequest(UnitPrice.class)
                        .getUnitPriceRequest(new JsonCallback<UnitPrice>() {
                            @Override
                            public void onSuccess(Response<UnitPrice> response) {
                                if (response != null && response.body() != null) {
                                    UnitPrice unitPrice = response.body();
                                    List<UnitPrice.PricesBean> prices = unitPrice.getPrices();
                                    if (EmptyUtils.isNotEmpty(prices)) {
                                        for (int i = 0; i < prices.size(); i++) {
                                            UnitPrice.PricesBean bean = prices.get(i);
                                            if (bean != null && bean.getName().equals(VALUE_SYMBOL)) {
                                                double value = bean.getValue();

                                                emitter.onNext(Double.toString(value));
                                                emitter.onComplete();
                                            }

                                        }

                                    }

                                }

                            }
                        });
            } catch (Throwable t) {
                throw Exceptions.propagate(t);
            }


        }
    }).subscribeOn(Schedulers.io());

    Observable<String> banlanceObserver = Observable.create(new ObservableOnSubscribe<String>() {
        @Override
        public void subscribe(ObservableEmitter<String> emitter) {
            requestBanlanceInfo(new StringCallback() {

                String banlance = "0.0000";

                @Override
                public void onStart(Request<String, ? extends Request> request) {
                    super.onStart(request);
                }

                @Override
                public void onError(Response<String> response) {
                    super.onError(response);
                    getV().dissmisProgressDialog();
                    emitter.onNext(banlance);
                    emitter.onComplete();
                }

                @Override
                public void onSuccess(Response<String> response) {

                    try {

                        if (response != null) {
                            String jsonStr = response.body();
                            LoggerManager.d("response json:" + jsonStr);

                            JSONArray array = new JSONArray(jsonStr);
                            if (array != null && array.length() > 0) {
                                banlance = array.optString(0);
                            }

                            getV().showBanlance(banlance);

                            emitter.onNext(banlance);
                            emitter.onComplete();
                        }


                    } catch (Throwable t) {
                        throw Exceptions.propagate(t);
                    }

                }
            });


        }
    }).subscribeOn(Schedulers.io());


    public void requestBanlanceInfo(StringCallback callback) {
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
                .getCurrencyBalance(callback);
    }


}
