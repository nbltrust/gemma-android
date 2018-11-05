package com.cybex.eth.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cybex.componentservice.db.entity.EthWalletEntity;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.utils.ClipboardUtils;
import com.cybex.componentservice.utils.SizeUtil;
import com.cybex.eth.R;
import com.cybex.qrcode.zxing.QRCodeEncoder;
import com.hxlx.core.lib.common.async.TaskManager;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;


/**
 * 收款页面
 */
public class EthCollectActivity extends XActivity {

    private TextView tvCurAddress;
    private ImageView ivAddressQR;
    private TextView tvCopyAddress;
    private TextView tvTokenName;
    private ImageView ivTokenLogo;

    @Override
    public void bindUI(View rootView) {
        ivAddressQR = (ImageView) findViewById(R.id.iv_account_QR);
        ivTokenLogo = (ImageView) findViewById(R.id.iv_token_logo);
        tvTokenName = (TextView) findViewById(R.id.tv_token_name);
        tvCurAddress = (TextView) findViewById(R.id.tv_cur_address);
        tvCopyAddress = (TextView) findViewById(R.id.tv_copy_address);

        //todo:update name and icon

    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setNavibarTitle(getString(R.string.collect), true);

        MultiWalletEntity walletEntity = DBManager.getInstance().getMultiWalletEntityDao().getCurrentMultiWalletEntity();
        EthWalletEntity ethWallet = walletEntity.getEthWalletEntities().get(0);
        if (walletEntity != null && ethWallet != null){
            tvCurAddress.setText(ethWallet.getAddress());
            showQRCode(ethWallet.getAddress());
        }

        tvCopyAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardUtils.copyText(context, getCurEosName());
                GemmaToastUtils.showLongToast(getString(R.string.eth_copy_success));
            }
        });
    }

    public String getCurEosName(){
        return tvCurAddress.getText().toString().trim();
    }

    public void showQRCode(final String address) {
        TaskManager.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                ivAddressQR.setImageBitmap(QRCodeEncoder.syncEncodeQRCode(address, SizeUtil.dp2px(173)));
            }
        });

    }

    @Override
    public int getLayoutId() {
        return R.layout.eth_activity_collect;
    }

    @Override
    public Object newP() {
        return null;
    }

}
