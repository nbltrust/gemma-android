package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cybex.gma.client.R;
import com.hxlx.core.lib.mvp.lite.XFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 备份私钥页面
 */

public class BackUpPriKeyFragment extends XFragment {


    @BindView(R.id.tv_show_priKey_area) TextView textViewShowPriKey;
    @BindView(R.id.bt_copy_priKey) Button buttonCopyPrikey;
    Unbinder unbinder;

    public static BackUpPriKeyFragment newInstance() {
        Bundle args = new Bundle();
        BackUpPriKeyFragment fragment = new BackUpPriKeyFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(BackUpPriKeyFragment.this, rootView);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        
    }

    @Override
    protected void setNavibarTitle(String title, boolean isShowBack) {
        super.setNavibarTitle(title, isShowBack);

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_backup_prikey;
    }

    @Override
    public Object newP() {
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
