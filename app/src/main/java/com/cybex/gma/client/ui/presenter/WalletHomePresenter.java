package com.cybex.gma.client.ui.presenter;

import android.content.Context;

import com.cybex.componentservice.api.callback.JsonCallback;
import com.cybex.componentservice.bean.TokenBean;
import com.cybex.componentservice.config.BaseConst;
import com.cybex.componentservice.db.entity.EosWalletEntity;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.componentservice.utils.AlertUtil;
import com.cybex.componentservice.utils.AmountUtil;
import com.cybex.gma.client.GmaApplication;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.HttpConst;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.event.CybexPriceEvent;
import com.cybex.gma.client.job.JobUtils;
import com.cybex.gma.client.job.TimeStampValidateJob;
import com.cybex.gma.client.ui.activity.WalletHomeActivity;
import com.cybex.gma.client.ui.model.request.GetAccountInfoReqParams;
import com.cybex.gma.client.ui.model.request.GetCurrencyBalanceReqParams;
import com.cybex.gma.client.ui.model.request.GetkeyAccountReqParams;
import com.cybex.gma.client.ui.model.response.AccountInfo;
import com.cybex.gma.client.ui.model.response.CheckActionStatusResult;
import com.cybex.gma.client.ui.model.response.GetEosTokensResult;
import com.cybex.gma.client.ui.model.response.GetKeyAccountsResult;
import com.cybex.gma.client.ui.model.response.UnitPrice;
import com.cybex.gma.client.ui.request.CheckActionStatusRequest;
import com.cybex.gma.client.ui.request.GetAccountinfoRequest;
import com.cybex.gma.client.ui.request.GetCurrencyBalanceRequest;
import com.cybex.gma.client.ui.request.GetEosTokensRequest;
import com.cybex.gma.client.ui.request.GetKeyAccountsRequest;
import com.cybex.gma.client.ui.request.UnitPriceRequest;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.mvp.lite.XPresenter;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.GsonUtils;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import org.json.JSONArray;

import java.util.List;

import io.hypertrack.smart_scheduler.Job;
import io.hypertrack.smart_scheduler.SmartScheduler;
import io.reactivex.exceptions.Exceptions;

public class WalletHomePresenter extends XPresenter<WalletHomeActivity> {

    private static final String VALUE_CODE = "eosio.token";
    private static final String VALUE_SYMBOL_EOS = "EOS";
    private static final String VALUE_SYMBOL_USDT = "USDT";

    /**
     * 根据公钥查询对应的eos账户
     *
     * @param public_key
     */
    public void getKeyAccounts(String public_key) {
        GetkeyAccountReqParams params = new GetkeyAccountReqParams();
        params.setPublic_key(public_key);
        String jsonParams = GsonUtils.objectToJson(params);

        new GetKeyAccountsRequest(GetKeyAccountsResult.class)
                .setJsonParams(jsonParams)
                .getKeyAccountsRequest(new JsonCallback<GetKeyAccountsResult>() {
                    @Override
                    public void onStart(Request<GetKeyAccountsResult, ? extends Request> request) {
                        if (getV() != null) {
                            super.onStart(request);
                            getV().showProgressDialog(getV().getString(R.string.eos_loading_account_info));
                        }
                    }

                    @Override
                    public void onSuccess(Response<GetKeyAccountsResult> response) {
                        if (getV() != null) {
                            if (response != null && response.body() != null
                                    && response.code() != HttpConst.SERVER_INTERNAL_ERR) {
                                LoggerManager.d("getKeyAccounts");
                                //找到此账号
                                //todo 理论上存在此公钥已经注册过其他账户的可能性

                                GetKeyAccountsResult result = response.body();
                                List<String> account_names = result.account_names;
                                if (account_names != null && account_names.size() > 0) {
                                    final String firstEosName = account_names.get(0);
                                    LoggerManager.d("firstEosname", firstEosName);

                                    int wallet_type = DBManager.getInstance().getMultiWalletEntityDao()
                                            .getCurrentMultiWalletEntity().getWalletType();

                                    //todo 添加其他钱包种类的处理逻辑
                                    //todo 更改为V2接口后查询进度方式改变
                                    if (wallet_type == BaseConst.WALLET_TYPE_BLUETOOTH) {
                                        updateBluetoothWallet(firstEosName, account_names);
                                        TimeStampValidateJob.executeValidateLogic(firstEosName, public_key);
                                    } else if (wallet_type == BaseConst.WALLET_TYPE_PRIKEY_IMPORT) {
                                        LoggerManager.d("case pri key import");
                                        TimeStampValidateJob.executeValidateLogic(firstEosName, public_key);
                                        updateEOSWallet(account_names);
                                    } else if (wallet_type == BaseConst.WALLET_TYPE_MNE_CREATE) {
                                        updateEOSWallet(account_names);
                                    } else if (wallet_type == BaseConst.WALLET_TYPE_MNE_IMPORT) {
                                        updateEOSWallet(account_names);
                                        TimeStampValidateJob.executeValidateLogic(firstEosName, public_key);
                                    }

                                    getV().updateEosCardView();
                                    String curEosName = getCurEosname();
                                    getEosTokens(curEosName);

                                } else {
                                    //todo account_names为空

                                    LoggerManager.d("account_names empty");
                                    int wallet_type = DBManager.getInstance().getMultiWalletEntityDao()
                                            .getCurrentMultiWalletEntity().getWalletType();

                                    if (wallet_type == BaseConst.WALLET_TYPE_BLUETOOTH) {

                                    } else if (wallet_type == BaseConst.WALLET_TYPE_PRIKEY_IMPORT) {

                                    } else if (wallet_type == BaseConst.WALLET_TYPE_MNE_CREATE) {

                                    } else if (wallet_type == BaseConst.WALLET_TYPE_MNE_IMPORT) {

                                    }
                                    getV().updateEosCardView();
                                    getV().dissmisProgressDialog();
                                }

                            } else if (response.body() != null && response.code() == HttpConst
                                    .SERVER_INTERNAL_ERR) {
                                //未找到此账号
                                LoggerManager.d("account not found");
                                GemmaToastUtils.showLongToast(getV().getString(R.string.eos_load_account_info_fail));
                                getV().dissmisProgressDialog();
                            }
                        }
                    }

                    @Override
                    public void onError(Response<GetKeyAccountsResult> response) {
                        //未找到此账号
                        if (getV() != null) {
                            super.onError(response);
                            getV().dissmisProgressDialog();
                        }
                    }
                });
    }

    /**
     * 从中心化服务器调取Tokens
     */
    private void getEosTokens(String account_name) {
        GetEosTokensRequest request = new GetEosTokensRequest(GetEosTokensResult.class, account_name)
                .getEosTokens(new JsonCallback<GetEosTokensResult>() {
                    @Override
                    public void onStart(Request<GetEosTokensResult, ? extends Request> request) {
                        super.onStart(request);
                    }

                    @Override
                    public void onSuccess(Response<GetEosTokensResult> data) {
                        if (getV() != null) {
                            LoggerManager.d("getEosTokens");
                            if (data != null) {
                                GetEosTokensResult response = data.body();
                                if (response.getResult() != null) {
                                    GetEosTokensResult.ResultBean resultBean = response.getResult();
                                    List<TokenBean> tokens = resultBean.getTokens();
                                    //更新UI
                                    if (tokens != null){
                                        getV().setEosTokens(tokens);
                                        getV().updateEosTokensUI(tokens);
                                    }else {
                                        GemmaToastUtils.showShortToast(getV().getString(R.string.eos_load_tokens_fail));
                                        getV().dissmisProgressDialog();
                                    }
                                    requestUnitPrice();
                                }else {
                                    GemmaToastUtils.showShortToast(getV().getString(R.string.eos_load_tokens_fail));
                                    getV().dissmisProgressDialog();
                                }
                            }else {
                                GemmaToastUtils.showShortToast(getV().getString(R.string.eos_load_tokens_fail));
                                getV().dissmisProgressDialog();
                            }
                        }
                    }

                    @Override
                    public void onError(Response<GetEosTokensResult> response) {
                        if (getV() != null) {
                            super.onError(response);
                            getV().dissmisProgressDialog();
                            GemmaToastUtils.showLongToast(getV().getString(R.string.eos_load_account_info_fail));
                        }
                    }
                });

    }

    /**
     * 从接口获取Cybex上主要币种的法币估值
     */
    private void requestUnitPrice() {
        try {
            new UnitPriceRequest(UnitPrice.class)
                    .getUnitPriceRequest(new JsonCallback<UnitPrice>() {
                        @Override
                        public void onStart(Request<UnitPrice, ? extends Request> request) {
                            if (getV() != null) {
                                super.onStart(request);
                            }
                        }

                        @Override
                        public void onError(Response<UnitPrice> response) {
                            if (getV() != null) {
                                super.onError(response);
                                getV().dissmisProgressDialog();
                                GemmaToastUtils.showLongToast(getV().getString(R.string.eos_load_account_info_fail));
                            }
                        }

                        @Override
                        public void onSuccess(Response<UnitPrice> response) {
                            if (response != null && response.body() != null) {
                                LoggerManager.d("requestUnitPrice");
                                UnitPrice unitPrice = response.body();
                                List<UnitPrice.PricesBean> prices = unitPrice.getPrices();
                                if (EmptyUtils.isNotEmpty(prices)) {
                                    String[] str = new String[2];
                                    for (int i = 0; i < prices.size(); i++) {
                                        UnitPrice.PricesBean bean = prices.get(i);
                                        if (bean != null) {
                                            if (bean.getName().equals(VALUE_SYMBOL_EOS)) {
                                                str[1] = String.valueOf(bean.getValue());
                                                CybexPriceEvent event = new CybexPriceEvent();
                                                event.setEosPrice(String.valueOf(bean.getValue()));
                                                EventBusProvider.postSticky(event);
                                            }
                                            if (bean.getName().equals(VALUE_SYMBOL_USDT)) {
                                                str[0] = String.valueOf(bean.getValue());
                                            }
                                        }
                                    }
                                    getAccount();
                                }
                            }
                        }
                    });
        } catch (Throwable t) {
            throw Exceptions.propagate(t);
        }
    }


    /**
     * 获取账户信息中是否有抵押资产
     * 如果有，需要把抵押资产也算进总资产估值中
     */
    private boolean isRequesting;

    public void getAccount() {
        if (isRequesting)return;
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
                        public void onStart(Request<AccountInfo, ? extends Request> request) {
                            isRequesting = true;
                            if (getV() != null) {
                                super.onStart(request);
                            }
                        }

                        @Override
                        public void onError(Response<AccountInfo> response) {
                            if (getV() != null) {
                                getV().dissmisProgressDialog();
                                GemmaToastUtils.showLongToast(getV().getString(R.string
                                        .eos_load_account_info_fail));
                            }
                            isRequesting = false;
                        }

                        @Override
                        public void onSuccess(Response<AccountInfo> response) {
                            if (getV() != null) {
                                LoggerManager.d("getAccount Success");
                                if (response != null && response.body() != null) {
                                    AccountInfo info = response.body();
                                    AccountInfo.SelfDelegatedBandwidthBean selfDelegatedBandwidth = info
                                            .getSelf_delegated_bandwidth();
                                    if (selfDelegatedBandwidth != null) {
                                        String totalDelegatedRes = AmountUtil.add(
                                                selfDelegatedBandwidth.getCpu_weightX().split(" ")[0],
                                                selfDelegatedBandwidth.getNet_weightX().split(" ")[0],
                                                4);
                                        getV().setDelegatedResourceQuantity(totalDelegatedRes);

                                    }
                                    requestBalanceInfo();
                                }
                                isRequesting = false;
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
    private void requestBalanceInfo() {
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
//                                                GemmaToastUtils.showLongToast(
//                                                        getV().getString(R.string.eos_loading_success));
                                                queryUnitPrice(balance);
//                                                getV().showEosBalance(balance);
                                            }
                                        } else {
                                            //可用余额为0，链上返回空
                                            LoggerManager.d("case 2");
                                            queryUnitPrice("0.0000");
//                                            getV().showEosBalance("0.0000");
//
                                        }
                                    } else {
                                        //LoggerManager.d("case 3");
//                                        getV().clearEosCardView();
//                                        GemmaToastUtils.showLongToast(
//                                                getV().getString(R.string.eos_load_account_info_fail));
                                    }
                                } else {
                                    //LoggerManager.d("case 4");
                                    getV().clearEosCardView();
                                    GemmaToastUtils.showLongToast(
                                            getV().getString(R.string.eos_load_account_info_fail));
                                }
                                //getV().dissmisProgressDialog();

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


    /**
     * 获取当前EOS用户名
     *
     * @return
     */
    private String getCurEosname() {
        MultiWalletEntity curWallet = DBManager.getInstance().getMultiWalletEntityDao().getCurrentMultiWalletEntity();
        if (curWallet != null && curWallet.getEosWalletEntities().size() > 0) {
            EosWalletEntity curEosWallet = curWallet.getEosWalletEntities().get(0);
            String curEosName = curEosWallet.getCurrentEosName();
            return curEosName;
        }
        return "Get curEosName Err";
    }

    /**
     * 更新
     */
    private void updateBluetoothWallet(String cur_eos_name, List<String> account_names) {
        MultiWalletEntity bluetoothMultiWalletEntity = DBManager.getInstance().getMultiWalletEntityDao()
                .getBluetoothWalletList().get(0);

        if (bluetoothMultiWalletEntity != null) {
            EosWalletEntity eosWalletEntity = bluetoothMultiWalletEntity.getEosWalletEntities().get(0);

            if (eosWalletEntity != null) {
                eosWalletEntity.setCurrentEosName(cur_eos_name);
                String eos_name_json = GsonUtils.objectToJson(account_names);
                eosWalletEntity.setEosNameJson(eos_name_json);

                List<EosWalletEntity> list = bluetoothMultiWalletEntity.getEosWalletEntities();
                list.remove(0);
                list.add(eosWalletEntity);

                DBManager.getInstance().getMultiWalletEntityDao().saveOrUpateEntitySync(bluetoothMultiWalletEntity);
            }
        }
    }

    /**
     * 更新EOS钱包状态
     */
    private void updateEOSWallet(List<String> account_names) {
        MultiWalletEntity curMultiWallet = DBManager.getInstance().getMultiWalletEntityDao()
                .getCurrentMultiWalletEntity();
        List<EosWalletEntity> eosList = curMultiWallet.getEosWalletEntities();

        if (eosList.size() > 0) {
            EosWalletEntity curEosWallet = eosList.get(0);
            if (curEosWallet != null) {
//                curEosWallet.setIsConfirmLib(ParamConstants.EOSACCOUNT_ACTIVATED);
                String curEosName = "";
                if (EmptyUtils.isEmpty(getCurEosname())) {
                    curEosName = account_names.get(0);
                } else {
                    curEosName = getCurEosname();
                }
                curEosWallet.setCurrentEosName(curEosName);
                String eosNameJson = GsonUtils.objectToJson(account_names);
                curEosWallet.setEosNameJson(eosNameJson);
                eosList.remove(0);
                eosList.add(curEosWallet);
                DBManager.getInstance().getMultiWalletEntityDao().saveOrUpateEntitySync(curMultiWallet);
            }
        }
    }

    /**
     * 访问Cybex获取当前EOS和USDT的价格
     * @param balance
     */
    public void queryUnitPrice(String balance){

        try {

            new UnitPriceRequest(UnitPrice.class)
                    .getUnitPriceRequest(new JsonCallback<UnitPrice>() {

                        @Override
                        public void onError(Response<UnitPrice> response) {
                            if (getV() != null){
                                super.onError(response);
                                getV().dissmisProgressDialog();
                                GemmaToastUtils.showLongToast(getV().getString(R.string.eos_load_account_info_fail));
                            }
                        }

                        @Override
                        public void onSuccess(Response<UnitPrice> response) {
                            if (getV() == null)return;
                            if (response != null && response.body() != null) {
                                UnitPrice unitPrice = response.body();
                                List<UnitPrice.PricesBean> prices = unitPrice.getPrices();
                                if (EmptyUtils.isNotEmpty(prices)) {

                                    CybexPriceEvent event = new CybexPriceEvent();
                                    String[] str = new String[2];
                                    for (int i = 0; i < prices.size(); i++) {
                                        UnitPrice.PricesBean bean = prices.get(i);

                                        if (bean != null){
                                            if (bean.getName().equals(VALUE_SYMBOL_EOS)){
                                                str[1] = String.valueOf(bean.getValue());
                                                event.setEosPrice(String.valueOf(bean.getValue()));
                                            }
                                            if (bean.getName().equals(VALUE_SYMBOL_USDT)){
                                                str[0] = String.valueOf(bean.getValue());
                                                event.setUsdtPrice(String.valueOf(bean.getValue()));
                                            }
                                        }

                                    }

                                    EventBusProvider.postSticky(event);
                                    getV().showEosBalance(balance);
                                    getV().dissmisProgressDialog();
                                    GemmaToastUtils.showLongToast(getV().getString(R.string.eos_loading_success));

                                }else {
                                    getV().dissmisProgressDialog();
                                    GemmaToastUtils.showLongToast(getV().getString(R.string.eos_load_account_info_fail));
                                }
                            }else {
                                getV().dissmisProgressDialog();
                                GemmaToastUtils.showLongToast(getV().getString(R.string.eos_load_account_info_fail));
                            }
                        }
                    });
        } catch (Throwable t) {
            throw Exceptions.propagate(t);
        }

    }

    /**
     * 检查抵押Action的状态
     * 当返回值为3（pending）已上链正在等待确认的时候重新执行之前的转账操作
     *
     * @param action_id
     */
    public void checkActionStatus(String action_id) {

        new CheckActionStatusRequest(CheckActionStatusResult.class, action_id)
                .checkActionStatus(new JsonCallback<CheckActionStatusResult>() {
                    @Override
                    public void onSuccess(Response<CheckActionStatusResult> response) {
                        if (getV() != null) {

                            MultiWalletEntity curWallet = DBManager.getInstance().getMultiWalletEntityDao()
                                    .getCurrentMultiWalletEntity();

                            if (curWallet != null){
                                if (response != null && response.body() != null) {
                                    CheckActionStatusResult result = response.body();
                                    CheckActionStatusResult.ResultBean resultBean = result.getResult();
                                    if (resultBean != null) {
                                        int status = resultBean.getStatus();
                                        LoggerManager.d("Action status", status);
                                        if (status == 3 || status == 2 || status == 1){
                                            //轮询
                                            startValidatePolling(action_id, 10000);
                                            int block_num = resultBean.getBlock_num();
                                            int lib = resultBean.getLast_irreversible_block();
                                            double progress = getProgressPrecentage(block_num, lib);
                                            getV().setmProgress((int)progress);

                                            getV().updateEosCardView();

                                        }
                                        else if (status == 4) {
                                            //已完成

                                            removePollingJob();
                                            getV().setActionId(null);
                                            updateEosAccountStatus(ParamConstants.EOSACCOUNT_ACTIVATED);
                                            getV().updateWallet(curWallet);

                                        } else {
                                            //失败

                                            removePollingJob();
                                            getV().setActionId(null);
                                            updateEosAccountStatus(ParamConstants.EOSACCOUNT_NOT_ACTIVATED);
                                            getV().updateWallet(curWallet);

                                        }
                                    }
                                }

                            }
                        }
                    }

                    @Override
                    public void onError(Response<CheckActionStatusResult> response) {
                        if (getV() != null) {
                            super.onError(response);
                            getV().dissmisProgressDialog();
                            removePollingJob();
                            updateEosAccountStatus(ParamConstants.EOSACCOUNT_NOT_ACTIVATED);
                            AlertUtil.showShortUrgeAlert(getV(), getV().getString(R.string.eos_chain_unstable));
                        }
                    }

                });
    }

    /**
     * 更新数据库中当前EOS账号的状态
     * @param status
     */
    private void updateEosAccountStatus(int status){
        MultiWalletEntity curWallet = DBManager.getInstance().getMultiWalletEntityDao().getCurrentMultiWalletEntity();
        if (curWallet != null && curWallet.getEosWalletEntities().size() > 0){
            EosWalletEntity curEosWallet = curWallet.getEosWalletEntities().get(0);
            curEosWallet.setIsConfirmLib(status);
            curEosWallet.save();
            curWallet.save();
        }
    }

    /**
     * 计算该Transaction确认中大致百分比
     *
     * @return
     */
    public double getProgressPrecentage(int block_num, int lib_num) {
        return Math.min(1 - Math.min((block_num - lib_num) / 325, 1), 0.99) * 100;
    }

    /**
     * 移除轮询
     */
    private void removePollingJob() {
        SmartScheduler smartScheduler = SmartScheduler.getInstance(GmaApplication.getAppContext());
        if (smartScheduler != null && smartScheduler.contains(ParamConstants.POLLING_JOB)) {
            smartScheduler.removeJob(ParamConstants.POLLING_JOB);
        }
    }

    /**
     * 开启一次比较时间戳验证轮询
     * 时间单位毫秒
     */
    public void startValidatePolling(String actionId, int intervalTime) {
        SmartScheduler smartScheduler = SmartScheduler.getInstance(GmaApplication.getAppContext());
        if (!smartScheduler.contains(ParamConstants.POLLING_JOB)) {
            SmartScheduler.JobScheduledCallback callback = new SmartScheduler.JobScheduledCallback() {
                @Override
                public void onJobScheduled(Context context, Job job) {
                    LoggerManager.d("validate polling executed");
                    checkActionStatus(actionId);
                }

            };

            Job job = JobUtils.createPeriodicHandlerJob(ParamConstants.POLLING_JOB, callback, intervalTime);
            smartScheduler.addJob(job);
        }

    }

}
