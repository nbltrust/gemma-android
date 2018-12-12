package com.cybex.walletmanagement.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.cybex.componentservice.db.entity.FPEntity;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.db.util.DBCallback;
import com.cybex.componentservice.event.WalletNameChangedEvent;
import com.cybex.componentservice.event.WookongFpChangedNameEvent;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.DeviceOperationManager;
import com.cybex.walletmanagement.R;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;

public class BluetoothChangeFPNameFragment extends XFragment {


    TitleBar btnNavibar;
    EditText editTextSetFPName;
    ImageView clearFpName;

    private MultiWalletEntity multiWalletEntity ;
    private FPEntity fpEntity;
    private String originFPName;

    private byte[] fpIndex;

    public static BluetoothChangeFPNameFragment newInstance(Bundle args) {
        BluetoothChangeFPNameFragment fragment = new BluetoothChangeFPNameFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void bindUI(View rootView) {
        multiWalletEntity = DBManager.getInstance().getMultiWalletEntityDao().getBluetoothWalletList().get(0);

        setNavibarTitle(getResources().getString(R.string.walletmanage_change_fp_name), true, false);
        btnNavibar = rootView.findViewById(R.id.btn_navibar);
        editTextSetFPName = rootView.findViewById(R.id.editText_setFPName);
        clearFpName = rootView.findViewById(R.id.clear_fp_name);

        clearFpName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextSetFPName.setText("");
            }
        });

        mTitleBar.setActionTextColor(Color.parseColor("#2D2D2D"));
        mTitleBar.addAction(new TitleBar.TextAction(getString(R.string.save)) {
            @Override
            public void performAction(View view) {
                String name = editTextSetFPName.getText().toString().trim();
                if (TextUtils.isEmpty(name)){
                    GemmaToastUtils.showLongToast(getString(R.string.walletmanage_fpname_notnull));
                    return;
                }
                if (!TextUtils.isEmpty(originFPName)&&originFPName.equals(name)){
                    GemmaToastUtils.showLongToast(getString(R.string.walletmanage_fpname_nochange));
                    return;
                }

                FPEntity tempFp = DBManager.getInstance().getMultiWalletEntityDao().getFpEntityListByWalletIdAndName(multiWalletEntity.getId(), name);
                if(tempFp !=null){
                    GemmaToastUtils.showLongToast(getString(R.string.walletmanage_fpname_have));
                    return;
                }

                if(fpEntity !=null){
                    fpEntity.setName(name);
                    fpEntity.save();
                }else{
                    FPEntity fpEntity = new FPEntity();
                    fpEntity.setName(name);
                    fpEntity.setIndex(fpIndex[0]+0);
                    fpEntity.setMultiWalletEntity(multiWalletEntity);
                    fpEntity.save();
                }
                EventBusProvider.post(new WookongFpChangedNameEvent(multiWalletEntity.getId(),fpIndex[0]+0,name));
                GemmaToastUtils.showLongToast(getString(R.string.walletmanage_fpname_change_success));
                pop();
            }
        });

    }

    @Override
    public void initData(Bundle savedInstanceState) {
        if (getArguments() != null) {
            fpIndex = getArguments().getByteArray("fpIndex");
        }
        fpEntity = DBManager.getInstance().getMultiWalletEntityDao().getFpEntityByWalletIdAndIndex(multiWalletEntity.getId(), fpIndex[0]);

        if(fpEntity !=null){
            originFPName=fpEntity.getName();
            editTextSetFPName.setText(fpEntity.getName());
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
