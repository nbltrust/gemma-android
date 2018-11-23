package com.cybex.gma.client.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cybex.gma.client.R;
import com.hxlx.core.lib.mvp.lite.XFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 导入蓝牙钱包引导页
 */
public class BluetoothImportWalletGuideFragment extends XFragment {

    Unbinder unbinder;
    @BindView(R.id.bt_start_import)
    public Button btStartImport;


    public static BluetoothImportWalletGuideFragment newInstance() {
        Bundle args = new Bundle();
        BluetoothImportWalletGuideFragment fragment = new BluetoothImportWalletGuideFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);

    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setNavibarTitle(getResources().getString(R.string.eos_import_wallet), true, true);
        btStartImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(BluetoothImportMneFragment.newInstance(),false);
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.eos_fragment_bluetooth_import_wallet_guide;
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
