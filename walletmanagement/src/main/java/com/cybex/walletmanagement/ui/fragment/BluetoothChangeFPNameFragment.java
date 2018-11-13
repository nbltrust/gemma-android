package com.cybex.walletmanagement.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.cybex.walletmanagement.R;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;

public class BluetoothChangeFPNameFragment extends XFragment {


    TitleBar btnNavibar;
    EditText editTextSetFPName;
    ImageView clearFpName;

    public static BluetoothChangeFPNameFragment newInstance(Bundle args) {
        BluetoothChangeFPNameFragment fragment = new BluetoothChangeFPNameFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void bindUI(View rootView) {
        setNavibarTitle(getResources().getString(R.string.walletmanage_change_fp_name), true, false);
        if (getActivity() != null) {
            btnNavibar = getActivity().findViewById(R.id.btn_navibar);
            editTextSetFPName = getActivity().findViewById(R.id.editText_setFPName);
            clearFpName = getActivity().findViewById(R.id.clear_fp_name);
        }


    }

    @Override
    public void initData(Bundle savedInstanceState) {
        if (getArguments() != null) {
            String fpName = getArguments().getString("fpName");
            editTextSetFPName.setText(fpName);
        }

    }

    @Override
    public int getLayoutId() {
        return R.layout.walletmanage_fragment_bluetooth_change_fp_name;
    }

    @Override
    public Object newP() {
        return null;
    }
}
