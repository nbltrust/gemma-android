package com.cybex.gma.client.job;

import com.cybex.gma.client.api.callback.JsonCallback;
import com.cybex.gma.client.db.entity.WalletEntity;
import com.cybex.gma.client.manager.DBManager;
import com.cybex.gma.client.manager.LoggerManager;
import com.cybex.gma.client.ui.model.request.GetAccountInfoReqParams;
import com.cybex.gma.client.ui.model.request.GetBlockReqParams;
import com.cybex.gma.client.ui.model.request.GetTransactionReqParams;
import com.cybex.gma.client.ui.model.request.GetkeyAccountReqParams;
import com.cybex.gma.client.ui.model.response.AccountInfo;
import com.cybex.gma.client.ui.model.response.GetKeyAccountsResult;
import com.cybex.gma.client.ui.request.EOSConfigInfoRequest;
import com.cybex.gma.client.ui.request.GetAccountinfoRequest;
import com.cybex.gma.client.ui.request.GetBlockRequest;
import com.cybex.gma.client.ui.request.GetKeyAccountsRequest;
import com.cybex.gma.client.ui.request.GetTransactionRequest;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.GsonUtils;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * 通过比较时间戳方法验证账户是否存在的方法
 * 邀请好友创建钱包，导入钱包时使用
 *
 */
public class TimeStampValidateJob {
    /**
     * 调取RPC get_info接口查询当前链的配置信息
     * 获取head_block_num和lib返回
     * result[0]返回lib
     * result[1]返回head
     */
    public static void getConfigInfo(StringCallback callback) {
        new EOSConfigInfoRequest(String.class)
                .getInfo(callback);
    }


    /**
     * @return 返回block_num
     */

    public static void getTransaction(StringCallback callback) {
        GetTransactionReqParams reqParams = new GetTransactionReqParams();
        WalletEntity curWallet = DBManager.getInstance().getWalletEntityDao().getCurrentWalletEntity();
        if (EmptyUtils.isNotEmpty(curWallet)) {
            LoggerManager.d("txId", curWallet.getTxId());
            reqParams.setid(curWallet.getTxId());
            String jsonParams = GsonUtils.objectToJson(reqParams);
            new GetTransactionRequest(String.class)
                    .setJsonParams(jsonParams)
                    .getInfo(callback);
        }

    }

    /**
     * 根据公钥查询对应的eos账户
     * @param key
     */
    public List<String> getKeyAccounts(String key){
        List<String> account_Names = new ArrayList<>();
        GetkeyAccountReqParams params = new GetkeyAccountReqParams();
        params.setPublic_key(key);

        String jsonParams = GsonUtils.objectToJson(params);

        new GetKeyAccountsRequest(GetKeyAccountsResult.class)
                .setJsonParams(jsonParams)
                .getKeyAccountsRequest(new JsonCallback<GetKeyAccountsResult>() {
                    @Override
                    public void onSuccess(Response<GetKeyAccountsResult> response) {
                        if (EmptyUtils.isNotEmpty(response.body())){
                            for (String account : response.body().account_names){
                                account_Names.add(account);
                            }
                        }
                    }

                    @Override
                    public void onError(Response<GetKeyAccountsResult> response) {

                    }
                });
        return account_Names;
    }

    /**
     * 根据账户名查询账户信息
     * 需要在callback中解析并获取该账户的创建时间
     * @param account_name
     */
    public void getAccount(String account_name, JsonCallback<AccountInfo> callback){
        GetAccountInfoReqParams params = new GetAccountInfoReqParams();
        params.setAccount_name(account_name);

        String jsonParams = GsonUtils.objectToJson(params);

        new GetAccountinfoRequest(AccountInfo.class)
                .setJsonParams(jsonParams)
                .getAccountInfo(callback);
    }

    /**
     * 根据block_num查询当前块的信息
     * 需要在callback获取timestamp
     * @param block_num
     */
    public void getBlock(String block_num, StringCallback callback){

        GetBlockReqParams params = new GetBlockReqParams();
        params.setBlock_num_or_id(block_num);

        String jsonParams = GsonUtils.objectToJson(params);

        new GetBlockRequest(String.class)
                .setJsonParams(jsonParams)
                .getBlock(callback);
    }

    /**
     * 执行比较时间戳逻辑
     */
    private static void executeTimeStampLogic(){

    }
}
