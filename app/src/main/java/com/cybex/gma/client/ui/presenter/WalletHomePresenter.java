package com.cybex.gma.client.ui.presenter;

import com.cybex.componentservice.api.callback.JsonCallback;
import com.cybex.componentservice.bean.TokenBean;
import com.cybex.componentservice.config.BaseConst;
import com.cybex.componentservice.db.entity.EosWalletEntity;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.componentservice.utils.AmountUtil;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.HttpConst;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.event.CybexPriceEvent;
import com.cybex.gma.client.ui.activity.WalletHomeActivity;
import com.cybex.gma.client.ui.model.request.GetAccountInfoReqParams;
import com.cybex.gma.client.ui.model.request.GetCurrencyBalanceReqParams;
import com.cybex.gma.client.ui.model.request.GetkeyAccountReqParams;
import com.cybex.gma.client.ui.model.response.AccountInfo;
import com.cybex.gma.client.ui.model.response.GetEosTokensResult;
import com.cybex.gma.client.ui.model.response.GetKeyAccountsResult;
import com.cybex.gma.client.ui.model.response.UnitPrice;
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
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import org.json.JSONArray;

import java.util.List;

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
                                if (account_names.size() > 0) {
                                    final String firstEosName = account_names.get(0);
                                    LoggerManager.d("firstEosname", firstEosName);

                                    int wallet_type = DBManager.getInstance().getMultiWalletEntityDao()
                                            .getCurrentMultiWalletEntity().getWalletType();

                                    //todo 添加其他钱包种类的处理逻辑
                                    if (wallet_type == BaseConst.WALLET_TYPE_BLUETOOTH) {
                                        updateBluetoothWallet(firstEosName, account_names);
                                    } else if (wallet_type == BaseConst.WALLET_TYPE_PRIKEY_IMPORT) {
                                        LoggerManager.d("case pri key import");
                                        updateEOSWallet(account_names);
                                    } else if (wallet_type == BaseConst.WALLET_TYPE_MNE_CREATE) {
                                        updateEOSWallet(account_names);
                                    } else if (wallet_type == BaseConst.WALLET_TYPE_MNE_IMPORT) {
                                        updateEOSWallet(account_names);
                                    }

                                    getV().updateEosCardView();
                                    String curEosName = getCurEosname();
                                    getEosTokens(curEosName);

                                } else {
                                    //todo account_names为空

                                    int wallet_type = DBManager.getInstance().getMultiWalletEntityDao()
                                            .getCurrentMultiWalletEntity().getWalletType();

                                    if (wallet_type == BaseConst.WALLET_TYPE_BLUETOOTH) {

                                    } else if (wallet_type == BaseConst.WALLET_TYPE_PRIKEY_IMPORT) {
                                        GemmaToastUtils.showLongToast(getV().getString(R.string.eos_load_account_info_fail));
                                    } else if (wallet_type == BaseConst.WALLET_TYPE_MNE_CREATE) {

                                    } else if (wallet_type == BaseConst.WALLET_TYPE_MNE_IMPORT) {

                                    }
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
                                    getV().setEosTokens(tokens);
                                    getV().updateEosTokensUI(tokens);

                                    requestUnitPrice();

                                }
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

                                            getAccount();
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




    /**
     * 获取账户信息中是否有抵押资产
     * 如果有，需要把抵押资产也算进总资产估值中
     */
    public void getAccount() {
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
                        }

                        @Override
                        public void onSuccess(Response<AccountInfo> response) {
                            if (getV() != null) {
                                LoggerManager.d("getAccount");
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
                                                GemmaToastUtils.showLongToast(
                                                        getV().getString(R.string.eos_loading_success));
                                                getV().showEosBalance(balance);
                                                //OkGo.getInstance().cancelAll();
                                            }
                                        }else {
                                            //可用余额为0，链上返回空
                                            LoggerManager.d("case 2");
                                            getV().showEosBalance("0.0000");
                                            GemmaToastUtils.showLongToast(
                                                    getV().getString(R.string.eos_loading_success));
                                        }
                                    } else {
                                        //LoggerManager.d("case 3");
//                                        getV().clearEosCardView();
//                                        GemmaToastUtils.showLongToast(
//                                                getV().getString(R.string.eos_load_account_info_fail));
                                    }
                                }else {
                                    //LoggerManager.d("case 4");
                                    getV().clearEosCardView();
                                    GemmaToastUtils.showLongToast(
                                            getV().getString(R.string.eos_load_account_info_fail));
                                }
                                getV().dissmisProgressDialog();
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
    public String getCurEosname() {
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
    public void updateBluetoothWallet(String cur_eos_name, List<String> account_names) {
        MultiWalletEntity bluetoothMultiWalletEntity = DBManager.getInstance().getMultiWalletEntityDao()
                .getBluetoothWalletList().get(0);

        if (bluetoothMultiWalletEntity != null) {
            EosWalletEntity eosWalletEntity = bluetoothMultiWalletEntity.getEosWalletEntities().get(0);

            if (eosWalletEntity != null) {
                eosWalletEntity.setIsConfirmLib(ParamConstants.EOSNAME_ACTIVATED);
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
    public void updateEOSWallet(List<String> account_names) {
        MultiWalletEntity curMultiWallet = DBManager.getInstance().getMultiWalletEntityDao()
                .getCurrentMultiWalletEntity();
        List<EosWalletEntity> eosList = curMultiWallet.getEosWalletEntities();

        if (eosList.size() > 0) {
            EosWalletEntity curEosWallet = eosList.get(0);
            if (curEosWallet != null) {
                curEosWallet.setIsConfirmLib(ParamConstants.EOSNAME_ACTIVATED);
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
}
