package com.cybex.gma.client.ui.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cybex.gma.client.R;
import com.cybex.gma.client.ui.model.vo.VoteNodeVO;

import java.util.List;

/**
 * 投票界面列表adapter
 *
 * Created by wanglin on 2018/8/14.
 */
public class VoteNodeListAdapter extends BaseQuickAdapter<VoteNodeVO, BaseViewHolder> {

    public VoteNodeListAdapter(@Nullable List<VoteNodeVO> data) {
        super(R.layout.item_node_info, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, VoteNodeVO item) {

    }
}
