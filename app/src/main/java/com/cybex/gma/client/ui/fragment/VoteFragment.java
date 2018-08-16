package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.cybex.base.view.statusview.MultipleStatusView;
import com.cybex.gma.client.R;
import com.cybex.gma.client.event.NodeSelectedEvent;
import com.cybex.gma.client.ui.adapter.VoteNodeListAdapter;
import com.cybex.gma.client.ui.model.vo.VoteNodeVO;
import com.cybex.gma.client.ui.presenter.VotePresenter;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 投票主页面
 */
public class VoteFragment extends XFragment<VotePresenter> {

    @BindView(R.id.rv_list) RecyclerView mRecyclerView;
    @BindView(R.id.list_multiple_status_view) MultipleStatusView mStatusView;
    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.tv_resource) TextView tvResource;
    @BindView(R.id.tv_vote_number) TextView tvVoteNumber;

    private VoteNodeListAdapter adapter;
    private List<VoteNodeVO> nodeVOList = new ArrayList<>();
    private ArrayList<VoteNodeVO> selectedNodes = new ArrayList<>();//已选择的节点
    Unbinder unbinder;

    @OnClick(R.id.tv_vote_number)
    public void goToSeeSelectedNodes(){
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("selected nodes", selectedNodes);
        start(NodeSelectedFragment.newInstance(bundle));
    }

    public static VoteFragment newInstance() {
        Bundle args = new Bundle();
        VoteFragment fragment = new VoteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Subscribe(threadMode = ThreadMode.POSTING, sticky = true)
    public void onNodeSelectChanged(NodeSelectedEvent event) {
        if (EmptyUtils.isNotEmpty(event)) {
            if (EmptyUtils.isNotEmpty(event.getVoteNodeVOList())){
                //如果是从已选节点页面返回
                selectedNodes.clear();
                selectedNodes.addAll(event.getVoteNodeVOList());
                int newSelectedNodesNum = selectedNodes.size();
                for (VoteNodeVO vo : selectedNodes){
                    if (!vo.ischecked)newSelectedNodesNum--;
                }
                tvVoteNumber.setText(String.format(getResources().getString(R.string.vote_num), String.valueOf(newSelectedNodesNum)));

                for (int i = 0; i < nodeVOList.size(); i++){
                    for (int j = 0; j < selectedNodes.size(); j++){
                        if (nodeVOList.get(i).getAccount().equals(selectedNodes.get(j).getAccount())){
                            //如果两个节点名称相同
                            nodeVOList.get(i).ischecked = selectedNodes.get(j).ischecked;
                        }
                    }
                }

                for (int k = 0; k < selectedNodes.size(); k++){
                    if (!selectedNodes.get(k).ischecked)selectedNodes.remove(k);//此选项卡在之前页面被取消选中
                }

            }else{
                //如果是从主页面进入
                tvVoteNumber.setText(String.format(getResources().getString(R.string.vote_num), String.valueOf(selectedNodes
                        .size())));
            }
           adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean useEventBus() {
        return true;
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(VoteFragment.this, rootView);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setNavibarTitle("投票", true, true);

        VoteNodeVO vo = new VoteNodeVO();
        vo.setAlias("@eoscannon");
        vo.setPercentage("0.5%");
        vo.setUrl("http://www.tuolian.com");
        vo.setAccount("EOS Cannon");

        VoteNodeVO vo2 = new VoteNodeVO();
        vo2.setAlias("@eosnewyork");
        vo2.setPercentage("0.24%");
        vo2.setUrl("http://www.tuolian.com");
        vo2.setAccount("EOS New York");

        nodeVOList.add(vo);
        nodeVOList.add(vo2);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getActivity(), LinearLayoutManager
                .VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);
        adapter = new VoteNodeListAdapter(nodeVOList);
        mRecyclerView.setAdapter(adapter);



        //getP().fetchBPDetail(ParamConstants.BP_NODE_NUMBERS);
        mRecyclerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                VoteNodeVO curVoteNodeVO = nodeVOList.get(position);

                if (curVoteNodeVO.ischecked) {
                    //从点选到取消
                    curVoteNodeVO.ischecked = false;
                    //从已选列表中删除
                    selectedNodes.remove(curVoteNodeVO);
                    EventBusProvider.postSticky(new NodeSelectedEvent());
                } else {
                    //点选
                    curVoteNodeVO.ischecked = true;
                    //向已选列表中添加
                    selectedNodes.add(curVoteNodeVO);
                    EventBusProvider.postSticky(new NodeSelectedEvent());
                }
                adapter.notifyDataSetChanged();
            }
        });

    }

    public void initAdapterData(List<VoteNodeVO> voteNodeVOList) {

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
