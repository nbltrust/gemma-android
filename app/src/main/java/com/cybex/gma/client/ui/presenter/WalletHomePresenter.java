package com.cybex.gma.client.ui.presenter;

import com.cybex.componentservice.api.callback.JsonCallback;
import com.cybex.componentservice.bean.TokenBean;
import com.cybex.componentservice.config.BaseConst;
import com.cybex.componentservice.db.entity.EosWalletEntity;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.HttpConst;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.ui.activity.WalletHomeActivity;
import com.cybex.gma.client.ui.model.request.GetkeyAccountReqParams;
import com.cybex.gma.client.ui.model.response.GetEosTokensResult;
import com.cybex.gma.client.ui.model.response.GetKeyAccountsResult;
import com.cybex.gma.client.ui.request.GetEosTokensRequest;
import com.cybex.gma.client.ui.request.GetKeyAccountsRequest;
import com.hxlx.core.lib.mvp.lite.XPresenter;
import com.hxlx.core.lib.utils.GsonUtils;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import java.util.List;

public class WalletHomePresenter extends XPresenter<WalletHomeActivity> {

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
                            getV().showProgressDialog("正在核验EOS账户");
                        }
                    }

                    @Override
                    public void onSuccess(Response<GetKeyAccountsResult> response) {
                        if (getV() != null) {
                            if (response != null && response.body() != null
                                    && response.code() != HttpConst.SERVER_INTERNAL_ERR) {
                                //找到此账号
                                //todo 理论上存在此公钥已经注册过其他账户的可能性

                                GetKeyAccountsResult result = response.body();
                                List<String> account_names = result.account_names;
                                if (account_names.size() > 0) {
                                    final String firstEosName = account_names.get(0);

                                    int wallet_type = DBManager.getInstance().getMultiWalletEntityDao()
                                            .getCurrentMultiWalletEntity().getWalletType();

                                    //todo 添加其他钱包种类的处理逻辑
                                    if (wallet_type == BaseConst.WALLET_TYPE_BLUETOOTH) {
                                        updateBluetoothWallet(firstEosName, account_names);
                                    } else if (wallet_type == BaseConst.WALLET_TYPE_PRIKEY_IMPORT) {
                                        //LoggerManager.d("case 3");
                                        updateEOSWallet(account_names);
                                    } else if (wallet_type == BaseConst.WALLET_TYPE_MNE_CREATE) {

                                    } else if (wallet_type == BaseConst.WALLET_TYPE_MNE_IMPORT) {
                                        updateEOSWallet(account_names);
                                    }

                                    getV().updateEosCardView();
                                    //
                                    String curEosName = getCurEosname();
                                    getEosTokens(curEosName);

                                } else{
                                    //todo account_names为空
                                    GemmaToastUtils.showLongToast(getV().getString(R.string.eos_load_account_info_fail));
                                }

                            }else if (response.body() != null && response.code() == HttpConst
                                    .SERVER_INTERNAL_ERR) {
                                //未找到此账号
                                GemmaToastUtils.showLongToast(getV().getString(R.string.eos_load_account_info_fail));
                            }

                            getV().dissmisProgressDialog();
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

        if (curMultiWallet != null && eosList.size() > 0) {
            EosWalletEntity curEosWallet = eosList.get(0);
            if (curEosWallet != null) {
                curEosWallet.setIsConfirmLib(ParamConstants.EOSNAME_ACTIVATED);
                String curEosName = getCurEosname();
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
                                    getV().setEosTokens(tokens);
                                    getV().updateEosTokensUI(tokens);


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

    public String getCurEosname(){
        MultiWalletEntity curWallet = DBManager.getInstance().getMultiWalletEntityDao().getCurrentMultiWalletEntity();
        if (curWallet != null && curWallet.getEosWalletEntities().size() > 0){
            EosWalletEntity curEosWallet = curWallet.getEosWalletEntities().get(0);
            String curEosName = curEosWallet.getCurrentEosName();
            return curEosName;
        }
     return "Get curEosName Err";
    }
}
