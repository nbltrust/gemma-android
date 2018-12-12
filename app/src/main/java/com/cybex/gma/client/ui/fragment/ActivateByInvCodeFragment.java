package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cybex.componentservice.db.entity.EosWalletEntity;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.db.entity.WalletEntity;
import com.cybex.componentservice.db.util.DBCallback;
import com.cybex.gma.client.R;
import com.cybex.componentservice.config.CacheConstants;
import com.cybex.gma.client.config.HttpConst;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.event.KeySendEvent;
import com.cybex.gma.client.event.ValidateResultEvent;
import com.cybex.gma.client.event.WalletIDEvent;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.activity.ActivateAccountMethodActivity;
import com.cybex.gma.client.ui.activity.CreateEosAccountActivity;
import com.cybex.gma.client.ui.presenter.ActivateByInvCodePresenter;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.common.utils.AppManager;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
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
    private String invCode;

    Unbinder unbinder;

    @OnClick(R.id.bt_activate)
    public void goActivate(){
        getP().createAccount(account_name, public_key, getInvCode());
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
            account_name = getArguments().getString(ParamConstants.EOS_USERNAME);
            EosWalletEntity curEosWallet = getCurEosWallet();
            if (curEosWallet != null){
                public_key = curEosWallet.getPublicKey();
            }
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
     * 获取当前EOS钱包
     * @return
     */
    private EosWalletEntity getCurEosWallet(){
        EosWalletEntity curEosWallet = new EosWalletEntity();
        MultiWalletEntity multiWalletEntity = DBManager.getInstance().getMultiWalletEntityDao()
                .getCurrentMultiWalletEntity();

        if (multiWalletEntity != null){
            curEosWallet = multiWalletEntity.getEosWalletEntities().get(0);
        }

        return curEosWallet;
    }


}
