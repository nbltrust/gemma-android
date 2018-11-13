package com.cybex.walletmanagement.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.allen.library.SuperTextView;
import com.cybex.walletmanagement.R;
import com.hxlx.core.lib.mvp.lite.XFragment;


public class BluetoothManageFPFragment extends XFragment {

     SuperTextView superTextViewChangeFpName;
   Button btDeleteFp;
    
    public static BluetoothManageFPFragment newInstance(Bundle args) {
        BluetoothManageFPFragment fragment = new BluetoothManageFPFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void bindUI(View rootView) {
        if (getActivity()!=null){
            superTextViewChangeFpName = getActivity().findViewById(R.id.superTextView_change_fp_name);
            btDeleteFp = getActivity().findViewById(R.id.bt_delete_fp);


        }

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
        return R.layout.walletmanage_fragment_bluetooth_manage_fp;
    }

    @Override
    public Object newP() {
        return null;
    }
}
