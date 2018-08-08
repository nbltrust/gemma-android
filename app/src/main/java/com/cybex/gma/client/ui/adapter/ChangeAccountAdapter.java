package com.cybex.gma.client.ui.adapter;

import android.support.annotation.Nullable;
import android.widget.CheckedTextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cybex.gma.client.R;
import com.cybex.gma.client.ui.model.vo.EOSNameVO;
import com.hxlx.core.lib.utils.EmptyUtils;

import java.util.List;

/**
 * Created by wanglin on 2018/8/7.
 */
public class ChangeAccountAdapter extends BaseQuickAdapter<EOSNameVO, BaseViewHolder> {


    public ChangeAccountAdapter(
            @Nullable List<EOSNameVO> data) {
        super(R.layout.item_change_account, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, EOSNameVO item) {
        CheckedTextView tv = helper.getView(R.id.tv_account_name);
        if (EmptyUtils.isNotEmpty(item)) {
            tv.setText(item.getEosName());
            tv.setChecked(item.isChecked);
        }

    }
}
