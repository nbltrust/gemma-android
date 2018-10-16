package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.cybex.gma.client.R;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BluetoothChangeFPNameFragment extends XFragment {

    Unbinder unbinder;
    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.editText_setFPName) EditText editTextSetFPName;
    @BindView(R.id.clear_fp_name) ImageView clearFpName;

    public static BluetoothChangeFPNameFragment newInstance(Bundle args) {
        BluetoothChangeFPNameFragment fragment = new BluetoothChangeFPNameFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(BluetoothChangeFPNameFragment.this, rootView);
        setNavibarTitle(getResources().getString(R.string.change_fp_name), true, false);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        if (getArguments() != null){
            String fpName = getArguments().getString("fpName");
            editTextSetFPName.setText(fpName);
        }

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_bluetooth_change_fp_name;
    }

    @Override
    public Object newP() {
        return null;
    }
}
