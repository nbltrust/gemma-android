package com.cybex.gma.client.ui.adapter;

import android.support.annotation.Nullable;
import android.widget.TextView;

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
        TextView tv_account = helper.getView(R.id.tv_node_name);
        TextView tv_alias = helper.getView(R.id.tv_node_alias);
        TextView tv_url = helper.getView(R.id.tv_node_url);
        TextView tv_percentage = helper.getView(R.id.tv_node_percentage);
        TextView tv_checkbox = helper.getView(R.id.tv_node_checked);

        tv_account.setText(item.getAccount());
        tv_alias.setText(item.getAlias());
        tv_url.setText(item.getUrl());
        tv_percentage.setText(item.getPercentage());

        helper.addOnClickListener(R.id.view_is_node_checked);

        if(item.ischecked){
            //如果该选项卡被选择
            tv_checkbox.setBackgroundResource(R.drawable.ic_radio_button_selected);
        }else {
            //如果该选项卡未被选择
            tv_checkbox.setBackgroundResource(R.drawable.ic_radio_button_unselected);
        }
    }
}
