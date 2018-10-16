package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cybex.componentservice.db.entity.WalletEntity;
import com.cybex.gma.client.R;
import com.cybex.gma.client.event.KeySendEvent;
import com.cybex.gma.client.event.WalletIDEvent;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.JNIUtil;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.siberiadante.customdialoglib.CustomDialog;
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
    private int inputCount;

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
        setNavibarTitle(getResources().getString(R.string.backup_prikey), true,
                true);
        inputCount = 0;
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
        inputCount = 0;
        unbinder.unbind();
    }

    /**
     * 显示输入密码Dialog
     */
    private void showConfirmAuthoriDialog() {

        int[] listenedItems = {R.id.imc_cancel, R.id.btn_confirm_authorization};
        CustomFullDialog dialog = new CustomFullDialog(getContext(),
                R.layout.dialog_input_password_with_ic_mask, listenedItems, false, Gravity.BOTTOM);
        dialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {

            @Override
            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.imc_cancel:
                        dialog.cancel();
                        break;
                    case R.id.btn_confirm_authorization:
                        EditText password = dialog.findViewById(R.id.et_password);
                        ImageView iv_clear = dialog.findViewById(R.id.iv_password_clear);
                        iv_clear.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                password.setText("");
                            }
                        });
                        final String inputPass = password.getText().toString().trim();
                        if (EmptyUtils.isEmpty(inputPass)){
                            GemmaToastUtils.showLongToast(getString(R.string.please_input_pass));
                            return;
                        }else{
                            if (!EmptyUtils.isEmpty(curWallet)){
                                final String cypher = curWallet.getCypher();
                                final String priKey = JNIUtil.get_private_key(cypher, inputPass);
                                //验证密码是否正确
                                if ("wrong password".equals(priKey)){
                                    inputCount++;

                                    //密码错误
                                    iv_clear.setVisibility(View.VISIBLE);
                                    GemmaToastUtils.showLongToast(getString(R.string.wrong_password));
                                    //如果输错3次以上，弹框提醒
                                    if (inputCount > 3){
                                        dialog.cancel();
                                        showPasswordHintDialog();
                                    }

                                }else {
                                    //密码正确
                                    EventBusProvider.postSticky(new KeySendEvent(priKey));
                                    dialog.cancel();
                                    UISkipMananger.launchBackUpPrivateKey(getActivity());
                                }
                            }
                        }

                        break;
                    default:
                        break;
                }
            }
        });
        dialog.show();
        EditText etPasword = dialog.findViewById(R.id.et_password);
        etPasword.setHint("请输入@" + curWallet.getCurrentEosName() + "的密码");
    }

    /**
     * 显示密码提示Dialog
     */
    private void showPasswordHintDialog() {
        int[] listenedItems = {R.id.tv_i_understand};
        CustomDialog dialog = new CustomDialog(getContext(),
                R.layout.dialog_password_hint, listenedItems, false, Gravity.CENTER);
        dialog.setOnDialogItemClickListener(new CustomDialog.OnCustomDialogItemClickListener() {

            @Override
            public void OnCustomDialogItemClick(CustomDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.tv_i_understand:
                        dialog.cancel();
                        showConfirmAuthoriDialog();
                        break;
                    default:
                        break;
                }
            }
        });
        dialog.show();

        TextView tv_pass_hint = dialog.findViewById(R.id.tv_password_hint_hint);
        if (EmptyUtils.isNotEmpty(curWallet)){
            String passHint = curWallet.getPasswordTip();
            String showInfo = getString(R.string.password_hint_info) + " : " + passHint;
            tv_pass_hint.setText(showInfo);
        }
    }

}
