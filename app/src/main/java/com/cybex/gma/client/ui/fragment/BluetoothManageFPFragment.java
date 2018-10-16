package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.allen.library.SuperTextView;
import com.cybex.gma.client.R;
import com.cybex.gma.client.ui.model.vo.BluetoothFPVO;
import com.hxlx.core.lib.mvp.lite.XFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class BluetoothManageFPFragment extends XFragment {

    Unbinder unbinder;
    @BindView(R.id.superTextView_change_fp_name) SuperTextView superTextViewChangeFpName;
    @BindView(R.id.bt_delete_fp) Button btDeleteFp;

    @OnClick({R.id.superTextView_change_fp_name, R.id.bt_delete_fp})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.superTextView_change_fp_name:
                break;
            case R.id.bt_delete_fp:
                break;
        }
    }
    
    public static BluetoothManageFPFragment newInstance(Bundle args) {
        BluetoothManageFPFragment fragment = new BluetoothManageFPFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(BluetoothManageFPFragment.this, rootView);

    }

    @Override
    public void initData(Bundle savedInstanceState) {
        if (getArguments() != null){
            String title_fp = getArguments().getString("fpName");
            setNavibarTitle(title_fp, true, false);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_bluetooth_manage_fp;
    }

    @Override
    public Object newP() {
        return null;
    }
}
