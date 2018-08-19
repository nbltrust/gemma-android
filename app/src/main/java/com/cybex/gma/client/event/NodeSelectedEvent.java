package com.cybex.gma.client.event;

import com.cybex.gma.client.ui.model.vo.VoteNodeVO;

import java.util.List;

/**
 * 投票时某一节点是否被选择事件
 */
public class NodeSelectedEvent {
    private List<VoteNodeVO> voteNodeVOList;
    private int eventType;//判断是哪一种事件，0为本页面事件，1为上级页面发送事件， 2为下级页面发送事件

    public List<VoteNodeVO> getVoteNodeVOList() {
        return voteNodeVOList;
    }

    public void setVoteNodeVOList(List<VoteNodeVO> voteNodeVOList) {
        this.voteNodeVOList = voteNodeVOList;
    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }
}
