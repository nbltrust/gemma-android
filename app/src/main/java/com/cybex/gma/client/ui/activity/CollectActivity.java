package com.cybex.gma.client.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cybex.componentservice.db.entity.EosWalletEntity;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.utils.ClipboardUtils;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.ui.model.vo.EosTokenVO;
import com.cybex.qrcode.zxing.QRCodeEncoder;
import com.hxlx.core.lib.common.async.TaskManager;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 收款页面
 */
public class CollectActivity extends XActivity {

    @BindView(R.id.tv_cur_eosName) TextView tvCurEosName;
    @BindView(R.id.iv_eosName_QR) ImageView ivEosNameQR;
    @BindView(R.id.tv_copy_eosName) TextView tvCopyEosName;
    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.iv_token_logo_collect) ImageView ivTokenLogoCollect;
    @BindView(R.id.iv_token_name_collect) TextView ivTokenNameCollect;

    @Override
    public void bindUI(View rootView) {
        ButterKnife.bind(this);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setNavibarTitle(getString(R.string.collect), true);

        Bundle bd = getIntent().getExtras();
        if (bd != null) {//bd为空，则为EOS资产
            //token资产
            EosTokenVO curToken = bd.getParcelable(ParamConstants.EOS_TOKENS);
            if (curToken != null){
                //ivTokenLogoCollect.setImageResource(R.drawable.eos_ic_asset);
                ivTokenNameCollect.setText(curToken.getTokenSymbol());

                String tokenUrl = curToken.getLogo_url();
                if (tokenUrl == null || tokenUrl.equals("")){
                    ivTokenLogoCollect.setImageResource(R.drawable.ic_token_unknown);
                }else {
                    Glide.with(ivTokenLogoCollect.getContext())
                            .load(tokenUrl)
                            .into(ivTokenLogoCollect);
                }
            }
        }

        MultiWalletEntity walletEntity = DBManager.getInstance()
                .getMultiWalletEntityDao()
                .getCurrentMultiWalletEntity();
        EosWalletEntity eosWallet = walletEntity.getEosWalletEntities().get(0);
        if (eosWallet != null && walletEntity.getEosWalletEntities().size() > 0) {
            tvCurEosName.setText(eosWallet.getCurrentEosName());
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

    public String getCurEosName() {
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
