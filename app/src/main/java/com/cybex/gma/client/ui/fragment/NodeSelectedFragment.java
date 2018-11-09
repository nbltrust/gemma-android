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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 已选节点页面，由投票界面点击进入
 */
public class NodeSelectedFragment extends XFragment {


    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.recycler_node_selected) RecyclerView mRecyclerView;

    Unbinder unbinder;
    private final int EVENT_THIS_PAGE = 0;//本页面发送的事件
    private final int EVENT_DOWN = 1;//上级页面发送的事件
    private final int EVENT_UP = 2;//下级页面返回发送的事件
    private int nodeSelected = 0;//被选择的节点数
    private VoteSelectedNodeAdapter adapter;
    private List<VoteNodeVO> voteNodeVOArrayList = new ArrayList<>();//上级页面传过来的节点列表，用于显示
    private List<VoteNodeVO> actualSelectedNodes = new ArrayList<>();//真实被选中的节点列表，用于返回上级时做参数

    public static NodeSelectedFragment newInstance() {
        Bundle args = new Bundle();
        NodeSelectedFragment fragment = new NodeSelectedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Subscribe(threadMode = ThreadMode.POSTING, sticky = true)
    public void onNodeSelectedChanged(NodeSelectedEvent event) {
        if (EmptyUtils.isNotEmpty(event)) {
            switch (event.getEventType()){
                case EVENT_THIS_PAGE:
                    //当前页面发送的事件
                    //动态更新顶部状态栏的已选节点数
                    setNavibarTitle(String.format(getString(R.string.eos_nodes_selected_num),
                            String.valueOf(nodeSelected)), true, false);
                    break;
                case EVENT_DOWN:
                    //上级页面发送的事件
                    //从上级页面接收已选节点列表
                    nodeSelected = event.getVoteNodeVOList().size();
                    voteNodeVOArrayList.clear();
                    actualSelectedNodes.clear();
                    voteNodeVOArrayList.addAll(event.getVoteNodeVOList());
                    actualSelectedNodes.addAll(event.getVoteNodeVOList());
                    break;
                case EVENT_UP:
                    //此页面无下级页面发送的事件
                    break;
            }
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
        mTitleBar.setTitleColor(com.hxlx.core.lib.R.color.black_title);
        mTitleBar.setTitleBold(true);
        mTitleBar.setTitleSize(20);
        mTitleBar.setImmersive(true);
        if (isShowBack) {
            mTitleBar.setLeftImageResource(com.hxlx.core.lib.R.drawable.ic_notify_back);
            mTitleBar.setLeftClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //此事件用于向上级页面返回数据
                    NodeSelectedEvent event_up = new NodeSelectedEvent();
                    event_up.setVoteNodeVOList(actualSelectedNodes);
                    event_up.setEventType(EVENT_UP);
                    EventBusProvider.postSticky(event_up);
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
        setNavibarTitle(String.format(getString(R.string.eos_nodes_selected_num),
                String.valueOf(nodeSelected)), true, false);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getActivity(), LinearLayoutManager
                .VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        adapter = new VoteSelectedNodeAdapter(voteNodeVOArrayList);
        adapter.setHasStableIds(true);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                VoteNodeVO curVO = voteNodeVOArrayList.get(position);
                if (curVO.ischecked) {
                    //从点选到取消
                    curVO.ischecked = false;
                    actualSelectedNodes.remove(curVO);
                    nodeSelected = actualSelectedNodes.size();
                } else {
                    //点选
                    curVO.ischecked = true;
                    actualSelectedNodes.add(curVO);
                    nodeSelected =actualSelectedNodes.size();
                }
                //此事件用于更新本页面UI
                NodeSelectedEvent eventThisPage = new NodeSelectedEvent();
                eventThisPage.setEventType(EVENT_THIS_PAGE);
                EventBusProvider.postSticky(eventThisPage);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.eos_fragment_node_selected;
    }

    @Override
    public Object newP() {
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        NodeSelectedEvent event_up = new NodeSelectedEvent();
        event_up.setVoteNodeVOList(actualSelectedNodes);
        event_up.setEventType(EVENT_UP);
        EventBusProvider.postSticky(event_up);
        actualSelectedNodes.clear();
    }
}
