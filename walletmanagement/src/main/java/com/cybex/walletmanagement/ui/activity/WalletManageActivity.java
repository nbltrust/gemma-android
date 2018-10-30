package com.cybex.walletmanagement.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.cybex.base.view.LabelLayout;
import com.cybex.componentservice.config.BaseConst;
import com.cybex.componentservice.config.RouterConst;
import com.cybex.walletmanagement.R;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.siberiadante.customdialoglib.CustomDialog;

import me.framework.fragmentation.anim.DefaultHorizontalAnimator;
import me.framework.fragmentation.anim.FragmentAnimator;

@Route(path= RouterConst.PATH_TO_WALLET_MANAGE_PAGE)
public class WalletManageActivity extends XActivity {


    private LabelLayout labelCurrentWallet;
    private LabelLayout labelImportWallet;
    private LabelLayout labelCreateWallet;
    private RelativeLayout containerWookong;


    @Override
    public void bindUI(View view) {
        labelCurrentWallet = (LabelLayout) findViewById(R.id.label_current_wallet);
        labelImportWallet = (LabelLayout) findViewById(R.id.label_import_wallet);
        labelCreateWallet = (LabelLayout) findViewById(R.id.label_create_wallet);
        containerWookong = (RelativeLayout) findViewById(R.id.container_wookong);

        labelCurrentWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SelectCurrentWalletActivity.class);
                startActivity(intent);
            }
        });
        labelImportWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ImportWalletGuideActivity.class);
                startActivity(intent);
            }
        });
        labelCreateWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CreateMnemonicWalletActivity.class);
                startActivity(intent);
            }
        });
        setNavibarTitle(getResources().getString(R.string.walletmanage_manage_wallet_title), true);

    }

    @Override
    public void initData(Bundle savedInstanceState) {


    }

    @Override
    public int getLayoutId() {
        return R.layout.walletmanage_activity_wallet_manage;
    }

    @Override
    public Object newP() {
        return null;
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        // 设置横向(和安卓4.x动画相同)
        return new DefaultHorizontalAnimator();
    }

    /**
     * 显示请勿截图Dialog
     */
    private void showAlertDialog() {
        int[] listenedItems = {R.id.tv_understand};
        CustomDialog dialog = new CustomDialog(this,
                R.layout.walletmanage_dialog_no_screenshot_mne, listenedItems, false, Gravity.CENTER);
        dialog.setOnDialogItemClickListener(new CustomDialog.OnCustomDialogItemClickListener() {

            @Override
            public void OnCustomDialogItemClick(CustomDialog dialog, View view) {
                if(view.getId()==R.id.tv_understand){
                    dialog.cancel();
                }
            }
        });
        dialog.show();
    }
}
