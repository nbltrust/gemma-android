package com.cybex.gma.client.ui.adapter;

import android.support.annotation.Nullable;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cybex.gma.client.R;
import com.cybex.gma.client.ui.model.vo.VoteNodeVO;

import java.util.List;

public class VoteSelectedNodeAdapter extends BaseQuickAdapter<VoteNodeVO,BaseViewHolder> {

    public VoteSelectedNodeAdapter(@Nullable List<VoteNodeVO> data) {
        super(R.layout.eos_item_selected_node, data);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    protected void convert(BaseViewHolder helper, VoteNodeVO item) {
        TextView tv_node_select_name = helper.getView(R.id.tv_node_selected_name);
        TextView tv_node_select_alais = helper.getView(R.id.tv_node_selected_alias);
        TextView tv_node_select_check = helper.getView(R.id.tv_node_selected_checked);

        tv_node_select_name.setText(item.getAccount());
        tv_node_select_alais.setText(item.getAlias());

        helper.addOnClickListener(R.id.view_is_node_checked);

        if(item.ischecked){
            //如果该选项卡被选择
            tv_node_select_check.setBackgroundResource(R.drawable.ic_radio_button_selected);
        }else {
            //如果该选项卡未被选择
            tv_node_select_check.setBackgroundResource(R.drawable.ic_radio_button_unselected);
        }
    }
}
