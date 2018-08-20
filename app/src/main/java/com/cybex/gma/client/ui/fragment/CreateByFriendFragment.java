package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.view.View;

import com.cybex.gma.client.R;
import com.hxlx.core.lib.mvp.lite.XFragment;

public class CreateByFriendFragment extends XFragment {

    public static CreateByFriendFragment newInstance() {
        Bundle args = new Bundle();
        CreateByFriendFragment fragment = new CreateByFriendFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void bindUI(View rootView) {
        
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setNavibarTitle(getResources().getString(R.string.title_invite_friend),
                true, true);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_create_by_friend;
    }

    @Override
    public Object newP() {
        return null;
    }
}
