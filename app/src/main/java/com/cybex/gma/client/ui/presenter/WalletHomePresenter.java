package com.cybex.gma.client.ui.presenter;

import com.cybex.componentservice.api.callback.JsonCallback;
import com.cybex.componentservice.config.BaseConst;
import com.cybex.componentservice.db.entity.EosWalletEntity;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.gma.client.config.HttpConst;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.ui.activity.WalletHomeActivity;
import com.cybex.gma.client.ui.model.request.GetkeyAccountReqParams;
import com.cybex.gma.client.ui.model.response.GetKeyAccountsResult;
import com.cybex.gma.client.ui.request.GetKeyAccountsRequest;
import com.hxlx.core.lib.mvp.lite.XPresenter;
import com.hxlx.core.lib.utils.GsonUtils;
import com.hxlx.core.lib.utils.android.logger.Log;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import java.util.List;
import java.util.logging.Logger;

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
                                final String curEOSName = account_names.get(0);

                                int wallet_type = DBManager.getInstance().getMultiWalletEntityDao()
                                        .getCurrentMultiWalletEntity().getWalletType();

                                //todo 添加其他钱包种类的处理逻辑
                                if (wallet_type == BaseConst.WALLET_TYPE_BLUETOOTH) {
                                    updateBluetoothWallet(curEOSName, account_names);
                                }else if (wallet_type == BaseConst.WALLET_TYPE_PRIKEY_IMPORT){
                                    //LoggerManager.d("case 3");
                                    updateEOSWallet(account_names);
                                }else if (wallet_type == BaseConst.WALLET_TYPE_MNE_CREATE){

                                }else if (wallet_type == BaseConst.WALLET_TYPE_MNE_IMPORT){
                                    updateEOSWallet(account_names);
                                }


                                getV().updateEosCardView();

                            } else if (response != null && response.body() != null && response.code() == HttpConst
                                    .SERVER_INTERNAL_ERR) {
                                //未找到此账号

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

        if (curMultiWallet != null && eosList.size()> 0) {
            EosWalletEntity curEosWallet = eosList.get(0);
            if (curEosWallet != null){
                curEosWallet.setIsConfirmLib(ParamConstants.EOSNAME_ACTIVATED);
                String curEosName = account_names.get(0);
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
