package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.cybex.gma.client.R;
import com.cybex.gma.client.event.NodeSelectedEvent;
import com.cybex.gma.client.ui.adapter.VoteSelectedNodeAdapter;
import com.cybex.gma.client.ui.model.vo.VoteNodeVO;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class NodeSelectedFragment extends XFragment {


    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.recycler_node_selected) RecyclerView mRecyclerView;

    Unbinder unbinder;
    private int nodeSelected;//被选择的节点数
    private VoteSelectedNodeAdapter adapter;
    private ArrayList<VoteNodeVO> voteNodeVOArrayList = new ArrayList<>();

    public static NodeSelectedFragment newInstance(Bundle args) {
        NodeSelectedFragment fragment = new NodeSelectedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onNodeSelectedChanged(NodeSelectedEvent event){
        if (EmptyUtils.isNotEmpty(event)){
            setNavibarTitle(String.format(getString(R.string.nodes_selected_num), String.valueOf(nodeSelected)), true, false);
        }
    }
    @Override
    public boolean useEventBus() {
        return true;
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
    }

    @Override
    protected void setNavibarTitle(String title, boolean isShowBack, boolean isOnBackFinishActivity) {
        mTitleBar = btnNavibar;
        mTitleBar.setTitle(title);
        mTitleBar.setTitleColor(com.hxlx.core.lib.R.color.ffffff_white_1000);
        mTitleBar.setTitleSize(20);
        mTitleBar.setImmersive(true);
        if (isShowBack) {
            mTitleBar.setLeftImageResource(com.hxlx.core.lib.R.drawable.ic_btn_back);
            mTitleBar.setLeftClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NodeSelectedEvent event = new NodeSelectedEvent();
                    event.setVoteNodeVOList(voteNodeVOArrayList);
                    EventBusProvider.postSticky(event);
                    if (isOnBackFinishActivity) {
                        getActivity().finish();
                    } else {
                        pop();
                    }
                }
            });
        }
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setNavibarTitle("已选节点(0/30)", true, false);
        if (getArguments() != null) {
            ArrayList<VoteNodeVO> voteNodeVOList = getArguments().getParcelableArrayList("selected nodes");
            if (EmptyUtils.isNotEmpty(voteNodeVOList)) {
                setNavibarTitle(String.format(getString(R.string.nodes_selected_num), String.valueOf(voteNodeVOList.size
                        ())), true, false);
                voteNodeVOArrayList.addAll(voteNodeVOList);
                adapter = new VoteSelectedNodeAdapter(voteNodeVOArrayList);
                nodeSelected = voteNodeVOList.size();
            }else{
                adapter = null;
            }
        }

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getActivity(), LinearLayoutManager
                .VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                VoteNodeVO curVO = voteNodeVOArrayList.get(position);
                if (curVO.ischecked) {
                    //从点选到取消
                    curVO.ischecked = false;
                    voteNodeVOArrayList.remove(curVO);
                    voteNodeVOArrayList.add(curVO);
                    nodeSelected--;
                    EventBusProvider.post(new NodeSelectedEvent());
                } else {
                    //点选
                    curVO.ischecked = true;
                    nodeSelected++;
                    EventBusProvider.post(new NodeSelectedEvent());
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_node_selected;
    }

    @Override
    public Object newP() {
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
