package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cybex.gma.client.R;
import com.cybex.gma.client.db.entity.WalletEntity;
import com.cybex.gma.client.event.KeySendEvent;
import com.cybex.gma.client.event.WalletIDEvent;
import com.cybex.gma.client.manager.DBManager;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.JNIUtil;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.siberiadante.customdialoglib.CustomFullDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 正式进入备份私钥界面前的引导页面
 */

public class BackUpPriKeyGuideFragment extends XFragment {
    @BindView(R.id.show_priKey) Button btShowPrikey;
    @BindView(R.id.testTV) TextView testTV;
    Unbinder unbinder;
    private Integer walletID;
    private WalletEntity curWallet;

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
    public boolean useEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void setID(WalletIDEvent message){
        if (!EmptyUtils.isEmpty(message)){
            walletID = message.getWalletID();
            if (walletID != null){
                curWallet = DBManager.getInstance().getWalletEntityDao().getWalletEntityByID(walletID);
            }
        }

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
                        EditText password = dialog.findViewById(R.id.et_password);
                        final String inputPass = password.getText().toString().trim();
                        if (!EmptyUtils.isEmpty(curWallet)){
                            final String cypher = curWallet.getCypher();
                            final String priKey = JNIUtil.get_private_key(cypher, inputPass);
                            //验证密码是否正确
                            if (JNIUtil.get_cypher(inputPass, priKey).equals(cypher)){
                                //密码正确
                                EventBusProvider.postSticky(new KeySendEvent(priKey));
                                //Bundle bundle = new Bundle();
                               // bundle.putString("key", priKey);
                                dialog.cancel();
                                UISkipMananger.launchBackUpPrivateKey(getActivity());
                            }else {
                                //密码错误
                                GemmaToastUtils.showLongToast("密码错误请重新输入！");
                            }
                        }

                        break;
                    default:
                        break;
                }
            }
        });
        dialog.show();
    }

}
