package com.cybex.walletmanagement.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.allen.library.SuperTextView;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.DeviceOperationManager;
import com.cybex.walletmanagement.R;
import com.extropies.common.MiddlewareInterface;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;


public class BluetoothManageFPFragment extends XFragment {

   SuperTextView superTextViewChangeFpName;
   Button btDeleteFp;

    private MultiWalletEntity multiWalletEntity ;
    private byte[] fpIndex;
    private String title_fp;

    public static BluetoothManageFPFragment newInstance(Bundle args) {
        BluetoothManageFPFragment fragment = new BluetoothManageFPFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void bindUI(View rootView) {
        multiWalletEntity = DBManager.getInstance().getMultiWalletEntityDao().getBluetoothWalletList().get(0);

        if (getActivity()!=null){
            superTextViewChangeFpName = getActivity().findViewById(R.id.superTextView_change_fp_name);
            btDeleteFp = getActivity().findViewById(R.id.bt_delete_fp);
        }
        setNavibarTitle(getString(R.string.walletmanage_manage_fp), true, false);

        btDeleteFp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MiddlewareInterface.FingerPrintID[] fingerPrintIDS = new MiddlewareInterface.FingerPrintID[1];
                fingerPrintIDS[0]=new MiddlewareInterface.FingerPrintID();
                fingerPrintIDS[0].data=fpIndex;
                DeviceOperationManager.getInstance().deleteFp(BluetoothManageFPFragment.this.toString(), multiWalletEntity.getBluetoothDeviceName(), fingerPrintIDS, new DeviceOperationManager.DeleteFPCallback() {
                    @Override
                    public void onSuccess() {
                        GemmaToastUtils.showLongToast(getString(R.string.walletmanage_fp_delete_success));
                        pop();
                    }

                    @Override
                    public void onFail() {
                        GemmaToastUtils.showLongToast(getString(R.string.walletmanage_fp_delete_fail));

                    }
                });

            }
        });


        superTextViewChangeFpName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                



            }
        });

    }

    @Override
    public void initData(Bundle savedInstanceState) {
        if (getArguments() != null){
            title_fp = getArguments().getString("fpName");
            fpIndex = getArguments().getByteArray("fpIndex");
            superTextViewChangeFpName.setRightString(title_fp);
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
