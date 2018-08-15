package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.cybex.base.view.statusview.MultipleStatusView;
import com.cybex.gma.client.R;
import com.hxlx.core.lib.mvp.lite.XFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class VoteFragment extends XFragment {

    @BindView(R.id.rv_list) RecyclerView mRecyclerView;
    @BindView(R.id.list_multiple_status_view) MultipleStatusView mStatusView;

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
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_vote;
    }

    @Override
    public Object newP() {
        return null;
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
