package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cybex.gma.client.R;
import com.cybex.gma.client.config.CacheConstants;
import com.cybex.gma.client.db.entity.WalletEntity;
import com.cybex.gma.client.manager.DBManager;
import com.cybex.gma.client.manager.UISkipMananger;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.siberiadante.customdialoglib.CustomFullDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 正式进入备份私钥界面前的引导页面
 */

public class BackUpPriKeyGuideFragment extends XFragment {

    @BindView(R.id.show_priKey) Button btShowPrikey;
    @BindView(R.id.et_password) EditText passInDialog;
    Unbinder unbinder;


    @OnClick(R.id.show_priKey)
    public void showPriKey() {
        showConfirmAuthoriDialog();
    }

    public static BackUpPriKeyGuideFragment newInstance() {
        Bundle args = new Bundle();
        BackUpPriKeyGuideFragment fragment = new BackUpPriKeyGuideFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(BackUpPriKeyGuideFragment.this, rootView);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setNavibarTitle("备份私钥", true, true);
    }


    @Override
    public int getLayoutId() {
        return R.layout.fragment_backup_guide;
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

    /**
     * 显示输入密码Dialog
     */
    private void showConfirmAuthoriDialog() {
        int[] listenedItems = {R.id.imc_cancel, R.id.btn_confirm_authorization};
        CustomFullDialog dialog = new CustomFullDialog(getContext(),
                R.layout.dialog_input_transfer_password, listenedItems, false, Gravity.BOTTOM);
        dialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
            @Override
            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.imc_cancel:
                        dialog.cancel();
                        break;
                    case R.id.btn_confirm_authorization:
                        /*
                        //获取底层方法需要的参数
                        final String inputPass = passInDialog.getText().toString().trim();
                        WalletEntity curWallet = getCurWallet();
                        final String cypher = curWallet.getCypher();
                        final String priKey = JNIUtil.get_private_key(cypher, inputPass);
                        //向BUNDLE中添加
                        Bundle bundle = new Bundle();
                        bundle.putString("prikey", priKey);
                        */

                        UISkipMananger.launchBackUpPrivateKey(getActivity());
                        break;
                    default:
                        break;
                }
            }
        });
        dialog.show();
    }

    private WalletEntity getCurWallet(){
        List<WalletEntity> walletEntityList = DBManager.getInstance().getMediaBeanDao().getWalletEntityList();
        for (WalletEntity walletEntity : walletEntityList){
            if (walletEntity.getIsCurrentWallet() == CacheConstants.IS_CURRENT_WALLET){
                return walletEntity;
            }
        }
        return null;
    }


}
