package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.cybex.base.view.statusview.MultipleStatusView;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.ui.adapter.VoteNodeListAdapter;
import com.cybex.gma.client.ui.model.vo.VoteNodeVO;
import com.cybex.gma.client.ui.presenter.VotePresenter;
import com.hxlx.core.lib.mvp.lite.XFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class VoteFragment extends XFragment<VotePresenter> {

    @BindView(R.id.rv_list) RecyclerView mRecyclerView;
    @BindView(R.id.list_multiple_status_view) MultipleStatusView mStatusView;

    private VoteNodeListAdapter adapter;
    private List<VoteNodeVO> nodeVOList = new ArrayList<>();
    Unbinder unbinder;

    public static VoteFragment newInstance() {
        Bundle args = new Bundle();
        VoteFragment fragment = new VoteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(VoteFragment.this, rootView);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setNavibarTitle("投票", true, true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getActivity(), LinearLayoutManager
                .VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);
        getP().fetchBPDetail(ParamConstants.BP_NODE_NUMBERS);
        mRecyclerView.addOnItemTouchListener(new OnItemChildClickListener() {
            @Override
            public void onSimpleItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                VoteNodeVO curVoteNodeVO = nodeVOList.get(position);

                if (curVoteNodeVO.ischecked){
                    curVoteNodeVO.ischecked = false;
                }else{
                    curVoteNodeVO.ischecked = true;
                }

                adapter.notifyDataSetChanged();
            }
        });

    }

    public void initAdapterData(List<VoteNodeVO> voteNodeVOList){

        /*
        for (VoteNodeVO node : voteNodeVOList){
            nodeVOList.add(node);
        }
        */
        nodeVOList.addAll(voteNodeVOList);
        adapter = new VoteNodeListAdapter(nodeVOList);
        mRecyclerView.setAdapter(adapter);

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_vote;
    }

    @Override
    public VotePresenter newP() {
        return new VotePresenter();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
