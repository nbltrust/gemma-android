package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.cybex.gma.client.R;
import com.cybex.componentservice.config.CacheConstants;
import com.cybex.componentservice.db.entity.WalletEntity;
import com.cybex.gma.client.event.KeySendEvent;
import com.cybex.gma.client.event.WalletIDEvent;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.qrcode.zxing.QRCodeEncoder;
import com.hxlx.core.lib.common.async.TaskManager;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.EmptyUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 通过二维码备份私钥界面
 */
public class BackUpPriKeyQRFragment extends XFragment {

    @BindView(R.id.bt_key_saved) Button btKeySaved;
    private int walletID;
    private WalletEntity curWallet;
    private String priKey;
    Unbinder unbinder;
    @BindView(R.id.iv_fake_QR) ImageView ivFakeQR;
    @BindView(R.id.iv_real_QR) ImageView ivRealQR;
    @BindView(R.id.bt_show_QR) Button btShowQR;

    @OnClick(R.id.bt_show_QR)
    public void showQR() {
        showRealQR(priKey);
        btShowQR.setVisibility(View.GONE);
        btKeySaved.setVisibility(View.VISIBLE);

    }

    @OnClick(R.id.bt_key_saved)
    public void onExportFinish(){
        curWallet = DBManager.getInstance().getWalletEntityDao().getWalletEntityByID(walletID);
        if (EmptyUtils.isEmpty(curWallet)) {
            curWallet.setIsBackUp(CacheConstants.ALREADY_BACKUP);
            DBManager.getInstance().getWalletEntityDao().saveOrUpateEntity(curWallet);
        }
        Bundle bundle = new Bundle();
        bundle.putString("private_key", priKey);
        UISkipMananger.launchVerifyPriKey(getActivity(), bundle);
    }

    public static BackUpPriKeyQRFragment newInstance() {
        Bundle args = new Bundle();
        BackUpPriKeyQRFragment fragment = new BackUpPriKeyQRFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public boolean useEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void getKey(KeySendEvent keySendEvent) {
        priKey = keySendEvent.getKey();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void getID(WalletIDEvent message) {
        walletID = message.getWalletID();
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(BackUpPriKeyQRFragment.this, rootView);

    }

    @Override
    public void initData(Bundle savedInstanceState) {
        curWallet = DBManager.getInstance().getWalletEntityDao().getWalletEntityByID(walletID);
        if (!EmptyUtils.isEmpty(curWallet)) {
            curWallet.setIsBackUp(CacheConstants.ALREADY_BACKUP);
            DBManager.getInstance().getWalletEntityDao().saveOrUpateEntity(curWallet);
        }
    }

    @Override
    protected void setNavibarTitle(String title, boolean isShowBack) {
        super.setNavibarTitle(title, isShowBack);

    }

    @Override
    public int getLayoutId() {
        return R.layout.eos_fragment_backup_prikey_qr;
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

    public void showRealQR(String privateKey) {
        ivFakeQR.setVisibility(View.GONE);
        ivRealQR.setVisibility(View.VISIBLE);
        TaskManager.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                ivRealQR.setImageBitmap(QRCodeEncoder.syncEncodeQRCode(privateKey, 100));
            }
        });

    }
}
