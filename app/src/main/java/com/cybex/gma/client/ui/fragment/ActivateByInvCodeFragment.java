package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cybex.componentservice.db.entity.WalletEntity;
import com.cybex.gma.client.R;
import com.cybex.componentservice.config.CacheConstants;
import com.cybex.gma.client.event.KeySendEvent;
import com.cybex.gma.client.event.ValidateResultEvent;
import com.cybex.gma.client.event.WalletIDEvent;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.presenter.ActivateByInvCodePresenter;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.common.utils.AppManager;
import com.siberiadante.customdialoglib.CustomDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ActivateByInvCodeFragment extends XFragment<ActivateByInvCodePresenter> {

    @BindView(R.id.bt_activate) Button btActivate;
    @BindView(R.id.edt_invCode) EditText edtInvCode;
    private String account_name;
    private String public_key;
    private String private_key;
    private String password;
    private String passwordTip;
    private String invCode;

    Unbinder unbinder;

    @OnClick(R.id.bt_activate)
    public void goActivate(){
        getP().createAccount(account_name, private_key, public_key, password, invCode, passwordTip);
    }

    /**
     * 时间戳比较验证状态判断
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onCreateStatusReceieved(ValidateResultEvent event){
        if (event != null){
            dissmisProgressDialog();
            if(event.isSuccess()){//创建成功
                //跳转到备份私钥页面
                WalletEntity curWallet = DBManager.getInstance().getWalletEntityDao().getCurrentWalletEntity();
                if (curWallet != null && getArguments() != null){
                    final String private_key = getArguments().getString("private_key");
                    KeySendEvent keySendEvent = new KeySendEvent(private_key);
                    EventBusProvider.postSticky(keySendEvent);
                    WalletIDEvent walletIDEvent = new WalletIDEvent(curWallet.getId());
                    EventBusProvider.postSticky(walletIDEvent);
                    UISkipMananger.launchBakupGuide(getActivity());
                }

            }else {
                //创建失败,弹框，删除当前钱包，判断是否还有钱包，跳转
                WalletEntity curWallet = DBManager.getInstance().getWalletEntityDao().getCurrentWalletEntity();
                DBManager.getInstance().getWalletEntityDao().deleteEntity(curWallet);
                List<WalletEntity> walletEntityList = DBManager.getInstance().getWalletEntityDao()
                        .getWalletEntityList();
                showFailDialog(EmptyUtils.isEmpty(walletEntityList));
            }

        }
    }

    public static ActivateByInvCodeFragment newInstance(Bundle args) {
        ActivateByInvCodeFragment fragment = new ActivateByInvCodeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        if (getArguments() != null){
            account_name = getArguments().getString("account_name");
            public_key = getArguments().getString("public_key");
            private_key = getArguments().getString("private_key");
            password = getArguments().getString("password");
            passwordTip = getArguments().getString("passwordTip");
            invCode = getInvCode();
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.eos_fragment_activate_by_invcode;
    }

    @Override
    public ActivateByInvCodePresenter newP() {
        return new ActivateByInvCodePresenter();
    }

    public String getInvCode(){
        return edtInvCode.getText().toString().trim();
    }

    /**
     * 显示创建失败Dialog
     */
    private void showFailDialog(boolean isWalletListEmpty) {
        int[] listenedItems = {R.id.tv_i_understand};
        CustomDialog dialog = new CustomDialog(getContext(),
                R.layout.eos_dialog_create_fail, listenedItems, false, Gravity.CENTER);
        dialog.setOnDialogItemClickListener(new CustomDialog.OnCustomDialogItemClickListener() {

            @Override
            public void OnCustomDialogItemClick(CustomDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.tv_i_understand:
                        if (isWalletListEmpty){
                            //如果没有钱包了
                            UISkipMananger.launchGuide(getActivity());
                        }else {
                            //如果有钱包
                            //更新当前钱包为最后一个钱包，跳转主页面
                            List<WalletEntity> walletList = DBManager.getInstance().getWalletEntityDao().getWalletEntityList();
                            WalletEntity newCurWallet = walletList.get(walletList.size() - 1);
                            newCurWallet.setIsCurrentWallet(CacheConstants.IS_CURRENT_WALLET);
                            DBManager.getInstance().getWalletEntityDao().saveOrUpateEntity(newCurWallet);
                            AppManager.getAppManager().finishAllActivity();
                            UISkipMananger.launchHomeSingle(getActivity());
                        }

                        dialog.cancel();
                        break;
                    default:
                        break;
                }
            }
        });
        dialog.show();
    }
}
