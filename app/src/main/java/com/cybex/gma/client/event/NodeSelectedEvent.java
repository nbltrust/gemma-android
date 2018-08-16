package com.cybex.gma.client.event;

import com.cybex.gma.client.ui.model.vo.VoteNodeVO;

import java.util.ArrayList;

/**
 * 投票时某一节点是否被选择事件
 */
public class NodeSelectedEvent {
    private ArrayList<VoteNodeVO> voteNodeVOList;

    public ArrayList<VoteNodeVO> getVoteNodeVOList() {
        return voteNodeVOList;
    }

    public void setVoteNodeVOList(ArrayList<VoteNodeVO> voteNodeVOList) {
        this.voteNodeVOList = voteNodeVOList;
    }
}
