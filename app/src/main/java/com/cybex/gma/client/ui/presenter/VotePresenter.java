package com.cybex.gma.client.ui.presenter;

import com.cybex.gma.client.api.callback.JsonCallback;
import com.cybex.gma.client.manager.LoggerManager;
import com.cybex.gma.client.ui.fragment.VoteFragment;
import com.cybex.gma.client.ui.model.request.FetchBPDetailReqParams;
import com.cybex.gma.client.ui.model.response.FetchBPDetailsResult;
import com.cybex.gma.client.ui.model.vo.VoteNodeVO;
import com.cybex.gma.client.ui.request.FetchBPDetailRequest;
import com.cybex.gma.client.utils.AmountUtil;
import com.hxlx.core.lib.mvp.lite.XPresenter;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.GsonUtils;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import java.util.ArrayList;
import java.util.List;

public class VotePresenter extends XPresenter<VoteFragment> {

    public void fetchBPDetail(int show_num){
        List<VoteNodeVO> list = new ArrayList<>();
        FetchBPDetailReqParams params = new FetchBPDetailReqParams();
        params.setNumber(show_num);

        String jsonParams = GsonUtils.objectToJson(params);

        new FetchBPDetailRequest(FetchBPDetailsResult.class)
                .setJsonParams(jsonParams)
                .FetchBPDetailRequest(new JsonCallback<FetchBPDetailsResult>() {
                    @Override
                    public void onStart(Request<FetchBPDetailsResult, ? extends Request> request) {
                        getV().showProgressDialog("加载节点信息");
                    }

                    @Override
                    public void onSuccess(Response<FetchBPDetailsResult> response) {
                        if (response.body() != null){
                            List<FetchBPDetailsResult.ResultBean.ProducersBean> producers = response.body().getResult
                                    ().getProducers();
                            if (EmptyUtils.isNotEmpty(producers)){
                                for (FetchBPDetailsResult.ResultBean.ProducersBean producer : producers){
                                    VoteNodeVO curNodeVO = new VoteNodeVO();
                                    curNodeVO.setAccount(producer.getAccount());
                                    curNodeVO.setAlias(producer.getAlias());
                                    curNodeVO.setUrl(producer.getUrl());
                                    String percentage = AmountUtil.mul(String.valueOf(producer.getPercentage()), "100",
                                            2);
                                    curNodeVO.setPercentage(percentage);
                                    list.add(curNodeVO);
                                }

                                getV().initAdapterData(list);
                            }
                        }
                        getV().dissmisProgressDialog();
                    }

                    @Override
                    public void onError(Response<FetchBPDetailsResult> response) {
                        super.onError(response);
                        LoggerManager.d("fetchBP error");
                        getV().dissmisProgressDialog();
                    }
                });
    }
}
