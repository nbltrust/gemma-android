package com.cybex.gma.client.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cybex.componentservice.db.entity.WalletEntity;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.utils.ClipboardUtils;
import com.cybex.gma.client.R;
import com.cybex.qrcode.zxing.QRCodeEncoder;
import com.hxlx.core.lib.common.async.TaskManager;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 收款页面
 */
public class CollectActivity extends XActivity {

    @BindView(R.id.tv_cur_eosName) TextView tvCurEosName;
    @BindView(R.id.iv_eosName_QR) ImageView ivEosNameQR;
    @BindView(R.id.tv_copy_eosName) TextView tvCopyEosName;

    @Override
    public void bindUI(View rootView) {
        ButterKnife.bind(this);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setNavibarTitle(getString(R.string.collect), true);
        WalletEntity walletEntity = DBManager.getInstance().getWalletEntityDao().getCurrentWalletEntity();
        if (walletEntity != null){
            tvCurEosName.setText(walletEntity.getCurrentEosName());
            showQRCode(getCurEosName());
        }

        tvCopyEosName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardUtils.copyText(CollectActivity.this, getCurEosName());
                GemmaToastUtils.showLongToast(getString(R.string.eos_copy_success));
            }
        });
    }

    public String getCurEosName(){
        return tvCurEosName.getText().toString().trim();
    }

    public void showQRCode(String eosName) {
        TaskManager.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                ivEosNameQR.setImageBitmap(QRCodeEncoder.syncEncodeQRCode(eosName, 100));
            }
        });

    }

    @Override
    public int getLayoutId() {
        return R.layout.eos_activity_collect;
    }

    @Override
    public Object newP() {
        return null;
    }

}
