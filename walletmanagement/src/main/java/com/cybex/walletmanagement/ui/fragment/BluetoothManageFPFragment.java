package com.cybex.walletmanagement.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.allen.library.SuperTextView;
import com.cybex.base.view.LabelLayout;
import com.cybex.componentservice.db.dao.MultiWalletEntityDao;
import com.cybex.componentservice.db.entity.FPEntity;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.event.WalletNameChangedEvent;
import com.cybex.componentservice.event.WookongFpChangedNameEvent;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.DeviceOperationManager;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.walletmanagement.R;
import com.cybex.walletmanagement.ui.model.vo.BluetoothFPVO;
import com.extropies.common.MiddlewareInterface;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class BluetoothManageFPFragment extends XFragment {

    LabelLayout superTextViewChangeFpName;
   Button btDeleteFp;

    private MultiWalletEntity multiWalletEntity ;
    private byte[] fpIndex;
//    private String title_fp;
    private FPEntity fpEntity;

    public static BluetoothManageFPFragment newInstance(Bundle args) {
        BluetoothManageFPFragment fragment = new BluetoothManageFPFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void bindUI(View rootView) {
        multiWalletEntity = DBManager.getInstance().getMultiWalletEntityDao().getBluetoothWalletList().get(0);
        superTextViewChangeFpName = rootView.findViewById(R.id.superTextView_change_fp_name);
        btDeleteFp = rootView.findViewById(R.id.bt_delete_fp);
        setNavibarTitle(getString(R.string.walletmanage_manage_fp), true, false);

        btDeleteFp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog("");

                MiddlewareInterface.FingerPrintID[] fingerPrintIDS = new MiddlewareInterface.FingerPrintID[1];
                fingerPrintIDS[0]=new MiddlewareInterface.FingerPrintID();
                fingerPrintIDS[0].data=fpIndex;
                DeviceOperationManager.getInstance().deleteFp(BluetoothManageFPFragment.this.toString(), multiWalletEntity.getBluetoothDeviceName(), fingerPrintIDS, new DeviceOperationManager.DeleteFPCallback() {
                    @Override
                    public void onSuccess() {
                        if(fpEntity !=null){
                            DBManager.getInstance().getMultiWalletEntityDao().deleteFpEntityAsync(fpEntity,null);
                        }
                        dissmisProgressDialog();
                        GemmaToastUtils.showLongToast(getString(R.string.walletmanage_fp_delete_success));
                        pop();
                    }

                    @Override
                    public void onFail() {
                        GemmaToastUtils.showLongToast(getString(R.string.walletmanage_fp_delete_fail));
                        dissmisProgressDialog();
                    }
                });

            }
        });


        superTextViewChangeFpName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
//                bundle.putString("fpName", curFpVO.getFingerprintName());
                bundle.putByteArray("fpIndex", fpIndex);
                start(BluetoothChangeFPNameFragment.newInstance(bundle));


            }
        });

    }

    @Override
    public void initData(Bundle savedInstanceState) {
        if (getArguments() != null){
//            title_fp = getArguments().getString("fpName");
            fpIndex = getArguments().getByteArray("fpIndex");
        }
        fpEntity = DBManager.getInstance().getMultiWalletEntityDao().getFpEntityByWalletIdAndIndex(multiWalletEntity.getId(), fpIndex[0]);

        if(fpEntity !=null){
            superTextViewChangeFpName.setRightText(fpEntity.getName());
        }else{
            superTextViewChangeFpName.setRightText(getString(R.string.walletmanage_fp_prefix)+(fpIndex[0]+1));
        }

    }

    @Override
    public boolean useEventBus() {
        return true;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changeFpName(WookongFpChangedNameEvent event) {
        if(event.getIndex()==fpIndex[0]+0){
            if(fpEntity !=null){
                fpEntity.setName(event.getFpName());
            }else{
                fpEntity = DBManager.getInstance().getMultiWalletEntityDao().getFpEntityByWalletIdAndIndex(multiWalletEntity.getId(), fpIndex[0]);
            }
            superTextViewChangeFpName.setRightText(event.getFpName());
        }
    }



    @Override
    public void onDestroy() {
        DeviceOperationManager.getInstance().clearCallback(this.toString());
        super.onDestroy();
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
