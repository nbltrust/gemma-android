package com.cybex.walletmanagement.ui.fragment;

import android.os.Bundle;
import android.util.Size;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.cybex.componentservice.config.BaseConst;
import com.cybex.componentservice.config.CacheConstants;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.utils.SizeUtil;
import com.cybex.qrcode.zxing.QRCodeEncoder;
import com.cybex.walletmanagement.R;
import com.hxlx.core.lib.common.async.TaskManager;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.EmptyUtils;

/**
 * 通过二维码备份私钥界面
 */
public class BackUpPriKeyQRFragment extends XFragment {

    Button btKeySaved;
    private MultiWalletEntity curWallet;
    private String priKey;
    ImageView ivFakeQR;
    ImageView ivRealQR;
    Button btShowQR;

    public static BackUpPriKeyQRFragment newInstance(String priKey, MultiWalletEntity walletEntity) {
        Bundle args = new Bundle();
        args.putString(BaseConst.KEY_PRI_KEY, priKey);
        args.putParcelable(BaseConst.KEY_WALLET_ENTITY, walletEntity);
        BackUpPriKeyQRFragment fragment = new BackUpPriKeyQRFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void bindUI(View rootView) {
        btKeySaved = rootView.findViewById(R.id.bt_key_saved);
        ivFakeQR =  rootView.findViewById(R.id.iv_fake_QR);
        ivRealQR =  rootView.findViewById(R.id.iv_real_QR);
        btShowQR =  rootView.findViewById(R.id.bt_show_QR);

        btKeySaved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EmptyUtils.isEmpty(curWallet)) {
                    curWallet.setIsBackUp(CacheConstants.ALREADY_BACKUP);
                    DBManager.getInstance().getMultiWalletEntityDao().saveOrUpateEntity(curWallet,null);
                }
                getActivity().finish();
            }
        });

        btShowQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRealQR(priKey);
                btShowQR.setVisibility(View.GONE);
                btKeySaved.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        priKey = getArguments().getString(BaseConst.KEY_PRI_KEY);
        curWallet = getArguments().getParcelable(BaseConst.KEY_WALLET_ENTITY);
    }

    @Override
    protected void setNavibarTitle(String title, boolean isShowBack) {
        super.setNavibarTitle(title, isShowBack);

    }

    @Override
    public int getLayoutId() {
        return R.layout.walletmanage_fragment_backup_prikey_qr;
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
    }

    public void showRealQR(final String privateKey) {
        ivFakeQR.setVisibility(View.GONE);
        ivRealQR.setVisibility(View.VISIBLE);
        TaskManager.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                ivRealQR.setImageBitmap(QRCodeEncoder.syncEncodeQRCode(privateKey, SizeUtil.dp2px(173)));
            }
        });

    }
}
