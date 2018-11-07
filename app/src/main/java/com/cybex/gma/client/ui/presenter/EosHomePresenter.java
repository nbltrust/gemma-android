package com.cybex.gma.client.ui.presenter;

import com.cybex.componentservice.api.callback.JsonCallback;
import com.cybex.componentservice.bean.TokenBean;
import com.cybex.componentservice.db.dao.MultiWalletEntityDao;
import com.cybex.componentservice.db.dao.WalletEntityDao;
import com.cybex.componentservice.db.entity.EosWalletEntity;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.db.entity.WalletEntity;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.gma.client.R;
import com.cybex.gma.client.ui.activity.EosHomeActivity;
import com.cybex.gma.client.ui.model.request.GetAccountInfoReqParams;
import com.cybex.gma.client.ui.model.request.GetCurrencyBalanceReqParams;
import com.cybex.gma.client.ui.model.response.AccountInfo;
import com.cybex.gma.client.ui.model.response.GetEosTokensResult;
import com.cybex.gma.client.ui.model.response.UnitPrice;
import com.cybex.gma.client.ui.model.vo.EOSNameVO;
import com.cybex.gma.client.ui.model.vo.EosTokenVO;
import com.cybex.gma.client.ui.model.vo.HomeCombineDataVO;
import com.cybex.gma.client.ui.request.GetAccountinfoRequest;
import com.cybex.gma.client.ui.request.GetCurrencyBalanceRequest;
import com.cybex.gma.client.ui.request.GetEosTokensRequest;
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
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function3;
import io.reactivex.schedulers.Schedulers;

public class EosHomePresenter extends XPresenter<EosHomeActivity> {

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
        MultiWalletEntity entity = DBManager.getInstance().getMultiWalletEntityDao().getCurrentMultiWalletEntity();
        EosWalletEntity eosEntity = entity.getEosWalletEntities().get(0);
        if (entity.getEosWalletEntities().size() > 0 && eosEntity != null) {
            List<String> eosNameList = GsonUtils.parseString2List(eosEntity.getEosNameJson(), String.class);

            if (EmptyUtils.isNotEmpty(eosNameList) && eosNameList.size() > 1) {
                for (int i = 0; i < eosNameList.size(); i++) {
                    String eosName = eosNameList.get(i);
                    EOSNameVO vo = new EOSNameVO();
                    vo.isChecked = eosName.equals(eosEntity.getCurrentEosName());

                    vo.setEosName(eosName);
                    voList.add(vo);
                }
            }
        }

        return voList;

    }


    public void saveNewEntity(String currentEOSName) {
        MultiWalletEntityDao dao = DBManager.getInstance().getMultiWalletEntityDao();
        MultiWalletEntity entity = dao.getCurrentMultiWalletEntity();
        if (entity.getEosWalletEntities().size() > 0){
            EosWalletEntity eosEntity = entity.getEosWalletEntities().get(0);
            if (eosEntity != null) {
                eosEntity.setCurrentEosName(currentEOSName);
                List<EosWalletEntity> list = entity.getEosWalletEntities();
                list.remove(0);
                list.add(eosEntity);
                dao.saveOrUpateEntitySync(entity);
            }
        }
    }


    /**
     * 从中心化服务器调取Tokens
     */
    public void getEosTokens(String account_name){
        GetEosTokensRequest request = new GetEosTokensRequest(GetEosTokensResult.class, account_name)
                .getEosTokens(new JsonCallback<GetEosTokensResult>() {
                    @Override
                    public void onStart(Request<GetEosTokensResult, ? extends Request> request) {
                        super.onStart(request);
                    }

                    @Override
                    public void onSuccess(Response<GetEosTokensResult> data) {
                        if (getV() != null){
                            if (data != null){
                                GetEosTokensResult response = data.body();
                                if (response.getResult() != null){
                                    GetEosTokensResult.ResultBean resultBean = response.getResult();
                                    List<TokenBean> tokens = resultBean.getTokens();
                                    //更新UI
                                    List<EosTokenVO> tokenVOList = converTokenBeanToVO(tokens);
                                    getV().showTokens(tokenVOList);
                                }
                            }
                            GemmaToastUtils.showLongToast(getV().getString(R.string.eos_loading_success));
                            getV().dissmisProgressDialog();
                        }
                    }

                    @Override
                    public void onError(Response<GetEosTokensResult> response) {
                        if (getV() != null){
                            super.onError(response);
                            getV().dissmisProgressDialog();
                            GemmaToastUtils.showLongToast(getV().getString(R.string.eos_load_account_info_fail));
                        }
                    }
                });

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
                        if (EmptyUtils.isNotEmpty(getV())){
                            getV().showMainInfo(vo);
                            //getV().dissmisProgressDialog();
                            //GemmaToastUtils.showLongToast(getV().getString(R.string.eos_loading_success));
                            getEosTokens(vo.getAccountInfo().getAccount_name());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        if (EmptyUtils.isNotEmpty(getV())){
                            getV().dissmisProgressDialog();
                            GemmaToastUtils.showLongToast(getV().getString(R.string.eos_load_account_info_fail));
                        }
                    }
                });
    }


    Observable<AccountInfo> getAccountObserver =
            Observable.create(new ObservableOnSubscribe<AccountInfo>() {

                @Override
                public void subscribe(ObservableEmitter<AccountInfo> e) {
                    try {
                        MultiWalletEntity entity = DBManager.getInstance().getMultiWalletEntityDao().getCurrentMultiWalletEntity();
                        EosWalletEntity eosEntity = entity.getEosWalletEntities().get(0);
                        if (entity == null) {
                            e.onComplete();
                            return;
                        }

                        String account_name = eosEntity.getCurrentEosName();

                        GetAccountInfoReqParams params = new GetAccountInfoReqParams();
                        params.setAccount_name(account_name);

                        String jsonParams = GsonUtils.objectToJson(params);
                        new GetAccountinfoRequest(AccountInfo.class)
                                .setJsonParams(jsonParams)
                                .getAccountInfo(new JsonCallback<AccountInfo>() {
                                    @Override
                                    public void onStart(Request<AccountInfo, ? extends Request> request) {
                                        if (getV() != null){
                                            super.onStart(request);
                                            getV().showProgressDialog(getV().getResources().getString(R.string.eos_loading_account_info));
                                        }
                                    }

                                    @Override
                                    public void onError(Response<AccountInfo> response) {
                                        if (getV() != null){
                                            getV().dissmisProgressDialog();
                                            GemmaToastUtils.showLongToast(getV().getString(R.string
                                                    .eos_load_account_info_fail));
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
                                    getV().showProgressDialog(getV().getResources().getString(R.string.eos_loading_account_info));
                                }
                            }

                            @Override
                            public void onError(Response<UnitPrice> response) {
                                if (getV() != null){
                                    super.onError(response);
                                    GemmaToastUtils.showLongToast(getV().getString(R.string.eos_load_account_info_fail));
                                }
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
                    super.onStart(request);
                    if (getV() != null){
                        getV().showProgressDialog(getV().getResources().getString(R.string.eos_loading_account_info));
                    }
                }

                @Override
                public void onError(Response<String> response) {
                    if (getV() != null){
                        super.onError(response);
                        getV().dissmisProgressDialog();
                        GemmaToastUtils.showLongToast(getV().getString(R.string.eos_load_account_info_fail));
                        emitter.onNext(banlance);
                        emitter.onComplete();
                    }
                }

                @Override
                public void onSuccess(Response<String> response) {

                    try {

                        if (response != null) {
                            String jsonStr = response.body();
                            //LoggerManager.d("response json:" + jsonStr);

                            JSONArray array = new JSONArray(jsonStr);
                            if (array != null && array.length() > 0) {
                                banlance = array.optString(0);
                                if (EmptyUtils.isNotEmpty(banlance) && EmptyUtils.isNotEmpty(getV())){
                                    getV().showBanlance(banlance);
                                }
                            }

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
        MultiWalletEntity entity = DBManager.getInstance().getMultiWalletEntityDao().getCurrentMultiWalletEntity();
        EosWalletEntity eosEntity = entity.getEosWalletEntities().get(0);
        if (eosEntity == null || entity == null) { return; }

        String currentEOSName = eosEntity.getCurrentEosName();
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
                tip = getV().getString(R.string.eos_tip_remaining_zero_zero_day_zero) + hour + getV().getString(R.string.eos_unit_hours);
            } else {
                tip = getV().getString(R.string.eos_tip_remaining_zero_zero_day) + hour + getV().getString(R.string.eos_unit_hours);
            }
        } else if ((timeLong / 1000 / 60 / 60 / 24) < 3 && (timeLong / 1000 / 60 / 60 / 24) > 1) {
            if(hour>0){
                if(hour<10){
                    tip =getV().getString(R.string.eos_tip_remain)+day+getV().getString(R.string.eos_tip_day_zero) + hour +
                            getV().getString(R.string.eos_unit_hours);
                }else{
                    tip = getV().getString(R.string.eos_tip_remain)+day+getV().getString(R.string.eos_tip_day) + hour + getV
                            ().getString(R.string.eos_unit_hours);
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
        SimpleDateFormat DateF = new SimpleDateFormat(format, Locale.getDefault());
        try {
            Long time = new Long(oldms);
            String oldTime = DateF.format(time);
            Date oldDate = DateF.parse(oldTime);
            Date nowDate = Calendar.getInstance().getTime();
            return dateDistance(nowDate, oldDate);
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 将List<TokenBean> 转化成为 List<EosTokensVO>
     */
    public List<EosTokenVO> converTokenBeanToVO(List<TokenBean> tokenBeanList){
        List<EosTokenVO> voList = new ArrayList<>();
        for (TokenBean curToken : tokenBeanList){
            EosTokenVO curTokenVO = new EosTokenVO();
            curTokenVO.setLogo_url(curToken.getLogo_url());
            curTokenVO.setQuantity(curToken.getBalance());
            curTokenVO.setTokenName(curToken.getContract());
            curTokenVO.setTokenSymbol(curToken.getSymbol());
            voList.add(curTokenVO);
        }
        return voList;
    }

}
