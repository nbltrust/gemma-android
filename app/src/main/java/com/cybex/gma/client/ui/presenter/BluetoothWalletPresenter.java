package com.cybex.gma.client.ui.presenter;

import com.cybex.componentservice.api.callback.JsonCallback;
import com.cybex.componentservice.db.dao.WalletEntityDao;
import com.cybex.componentservice.db.entity.WalletEntity;
import com.cybex.gma.client.R;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.gma.client.ui.fragment.BluetoothWalletFragment;
import com.cybex.gma.client.ui.model.request.GetAccountInfoReqParams;
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
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
 * 蓝牙钱包Presenter
 *
 * Created by wanglin on 2018/7/9.
 */
public class BluetoothWalletPresenter extends XPresenter<BluetoothWalletFragment> {

    private static final String VALUE_CODE = "eosio.token";
    private static final String VALUE_CONTRACT = "eosio.token";
    private static final String VALUE_COMPRESSION = "none";
    private static final String VALUE_SYMBOL_EOS = "EOS";
    private static final String VALUE_SYMBOL_USDT = "USDT";

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
                    vo.isChecked = eosName.equals(entity.getCurrentEosName());

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
                new Function3<AccountInfo, String[], String, HomeCombineDataVO>() {
                    @Override
                    public HomeCombineDataVO apply(AccountInfo accountInfo, String[] unitPrice, String banlance) {

                        HomeCombineDataVO vo = new HomeCombineDataVO();
                        vo.setAccountInfo(accountInfo);
                        vo.setBanlance(banlance);
                        vo.setUnitPrice(unitPrice[1]);
                        vo.setUnitPriceUSDT(unitPrice[0]);

                        return vo;
                    }

                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<HomeCombineDataVO>() {
                    @Override
                    public void accept(HomeCombineDataVO vo) {
                        getV().showMainInfo(vo);
                        getV().dissmisProgressDialog();
                        GemmaToastUtils.showLongToast(getV().getString(R.string.loading_success));

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        getV().dissmisProgressDialog();
                    }
                });
    }


    Observable<AccountInfo> getAccountObserver =
            Observable.create(new ObservableOnSubscribe<AccountInfo>() {

                @Override
                public void subscribe(ObservableEmitter<AccountInfo> e) {
                    try {
                        WalletEntity entity = DBManager.getInstance().getWalletEntityDao().getCurrentWalletEntity();
                        if (entity == null) {
                            e.onComplete();
                            return;
                        }

                        String account_name = entity.getCurrentEosName();

                        GetAccountInfoReqParams params = new GetAccountInfoReqParams();
                        params.setAccount_name(account_name);

                        String jsonParams = GsonUtils.objectToJson(params);
                        new GetAccountinfoRequest(AccountInfo.class)
                                .setJsonParams(jsonParams)
                                .getAccountInfo(new JsonCallback<AccountInfo>() {
                                    @Override
                                    public void onStart(Request<AccountInfo, ? extends Request> request) {
                                        super.onStart(request);
                                        if (getV() != null){
                                            getV().showProgressDialog(getV().getResources().getString(R.string.loading_account_info));
                                        }

                                    }

                                    @Override
                                    public void onError(Response<AccountInfo> response) {
                                        if (getV() != null){
                                            getV().dissmisProgressDialog();
                                            GemmaToastUtils.showLongToast(getV().getString(R.string.load_account_info_fail));
                                        }
                                    }

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


    Observable<String[]> unitPriceObserver = Observable.create(new ObservableOnSubscribe<String[]>() {
        @Override
        public void subscribe(ObservableEmitter<String[]> emitter) {
            try {
                new UnitPriceRequest(UnitPrice.class)
                        .getUnitPriceRequest(new JsonCallback<UnitPrice>() {
                            @Override
                            public void onStart(Request<UnitPrice, ? extends Request> request) {
                                if (getV() != null){
                                    super.onStart(request);
                                    getV().showProgressDialog(getV().getResources().getString(R.string.loading_account_info));
                                }
                            }

                            @Override
                            public void onError(Response<UnitPrice> response) {
                                super.onError(response);
                                GemmaToastUtils.showLongToast(getV().getString(R.string.load_account_info_fail));
                            }

                            @Override
                            public void onSuccess(Response<UnitPrice> response) {
                                if (response != null && response.body() != null) {
                                    UnitPrice unitPrice = response.body();
                                    List<UnitPrice.PricesBean> prices = unitPrice.getPrices();
                                    if (EmptyUtils.isNotEmpty(prices)) {
                                        String[] str = new String[2];
                                        for (int i = 0; i < prices.size(); i++) {
                                            UnitPrice.PricesBean bean = prices.get(i);
                                            /*
                                            if (bean != null && bean.getName().equals(VALUE_SYMBOL_EOS)) {

                                                double value = bean.getValue();


                                                emitter.onNext(Double.toString(value));
                                                emitter.onComplete();
                                            }
                                            */

                                            if (bean != null){
                                                if (bean.getName().equals(VALUE_SYMBOL_EOS)){
                                                    str[1] = String.valueOf(bean.getValue());
                                                }
                                                if (bean.getName().equals(VALUE_SYMBOL_USDT)){
                                                    str[0] = String.valueOf(bean.getValue());
                                                }
                                            }

                                        }
                                        emitter.onNext(str);
                                        emitter.onComplete();

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
                    if (getV() != null){
                        super.onStart(request);
                        getV().showProgressDialog(getV().getResources().getString(R.string.loading_account_info));
                    }
                }

                @Override
                public void onError(Response<String> response) {
                    if (getV() != null){
                        super.onError(response);
                        getV().dissmisProgressDialog();
                        GemmaToastUtils.showLongToast(getV().getString(R.string.load_account_info_fail));
                        emitter.onNext(banlance);
                        emitter.onComplete();
                    }
                }

                @Override
                public void onSuccess(Response<String> response) {
                    if (getV() != null){
                        try {

                            if (response != null) {
                                String jsonStr = response.body();
                                LoggerManager.d("response json:" + jsonStr);

                                JSONArray array = new JSONArray(jsonStr);
                                if (array != null && array.length() > 0) {
                                    banlance = array.optString(0);
                                    getV().showBanlance(banlance);
                                }

                                emitter.onNext(banlance);
                                emitter.onComplete();
                            }


                        } catch (Throwable t) {
                            throw Exceptions.propagate(t);
                        }
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
        params.setSymbol(VALUE_SYMBOL_EOS);
        String jsonParams = GsonUtils.objectToJson(params);

        new GetCurrencyBalanceRequest(String.class)
                .setJsonParams(jsonParams)
                .getCurrencyBalance(callback);
    }


    /**
     * 计算时间差
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public String dateDistance(Date startDate, Date endDate) {
        String tip = "";

        if (startDate == null || endDate == null) {
            return null;
        }
        long timeLong = endDate.getTime() - startDate.getTime();
        if (timeLong < 0) {
            timeLong = 0;
        }


        Integer ss = 1000;
        Integer mi = ss * 60;
        Integer hh = mi * 60;
        Integer dd = hh * 24;

        Long day = timeLong / dd;
        Long hour = (timeLong - day * dd) / hh;

        if (timeLong < 60 * 60 * 24 * 1000) {
            if (hour < 10) {
                tip = getV().getString(R.string.tip_remaining_zero_zero_day_zero) + hour + getV().getString(R.string.unit_hours);
            } else {
                tip = getV().getString(R.string.tip_remaining_zero_zero_day) + hour + getV().getString(R.string.unit_hours);
            }
        } else if ((timeLong / 1000 / 60 / 60 / 24) < 3 && (timeLong / 1000 / 60 / 60 / 24) > 1) {
            if(hour>0){
                if(hour<10){
                    tip =getV().getString(R.string.tip_remain)+day+getV().getString(R.string.tip_day_zero) + hour +
                            getV().getString(R.string.unit_hours);
                }else{
                    tip = getV().getString(R.string.tip_remain)+day+getV().getString(R.string.tip_day) + hour + getV
                            ().getString(R.string.unit_hours);
                }

            }
        }

        return tip;
    }


    /**
     * 获得当前时间的时间差
     *
     * @param oldms 旧时间
     * @param format
     * @return
     */
    public String dateDistance2now(long oldms, String format) {
        SimpleDateFormat DateF = new SimpleDateFormat(format);
        try {
            Long time = new Long(oldms);
            String oldTime = DateF.format(time);
            Date oldDate = DateF.parse(oldTime);
            Date nowDate = Calendar.getInstance().getTime();
            return dateDistance(oldDate, nowDate);
        } catch (Exception e) {
        }
        return null;
    }

}
