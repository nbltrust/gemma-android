package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cybex.gma.client.R;
import com.cybex.gma.client.config.CacheConstants;
import com.cybex.gma.client.db.entity.WalletEntity;
import com.cybex.gma.client.event.KeySendEvent;
import com.cybex.gma.client.event.WalletIDEvent;
import com.cybex.gma.client.manager.DBManager;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.activity.BackUpWalletGuideActivity;
import com.cybex.gma.client.utils.ClipboardUtils;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.common.utils.AppManager;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.siberiadante.customdialoglib.CustomDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 明文备份私钥页面
 */

public class BackUpPriKeyFragment extends XFragment {

    @BindView(R.id.tv_show_priKey_area) TextView tvShowPriKeyArea;
    @BindView(R.id.bt_click_to_copy) Button btClickToCopy;
    @BindView(R.id.bt_copied_priKey) Button btCopiedPriKey;
    private String key;
    private int walletID;
    private WalletEntity curWallet;

    Unbinder unbinder;

    @OnClick(R.id.bt_click_to_copy)
    public void copyPrikeyToClipboard() {
        String curPriKey = tvShowPriKeyArea.getText().toString().trim();
        if (getActivity() != null) {
            ClipboardUtils.copyText(getActivity(), curPriKey);
        }
        GemmaToastUtils.showLongToast(getResources().getString(R.string.prikey_copied));
    }

    @OnClick(R.id.bt_copied_priKey)
    public void onFinishExport(){
        curWallet = DBManager.getInstance().getWalletEntityDao().getWalletEntityByID(walletID);
        if (!EmptyUtils.isEmpty(curWallet)) {
            curWallet.setIsBackUp(CacheConstants.ALREADY_BACKUP);
            DBManager.getInstance().getWalletEntityDao().saveOrUpateEntity(curWallet);
        }
        UISkipMananger.launchVerifyPriKey(getActivity());
    }

    public static BackUpPriKeyFragment newInstance() {
        Bundle args = new Bundle();
        BackUpPriKeyFragment fragment = new BackUpPriKeyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public boolean useEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void getKey(KeySendEvent message) {
        key = message.getKey();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void getID(WalletIDEvent message) {
        walletID = message.getWalletID();
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(BackUpPriKeyFragment.this, rootView);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        showAlertDialog();
        tvShowPriKeyArea.setText(formatKey(key));
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
        return R.layout.fragment_backup_prikey;
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

    public String formatKey(String key){
        String res = "";
        for (int i = 0; i < key.length(); i++){
            if (i+4 < key.length()){
                res += key.substring(i, i+4) + " ";
            }else{
                res += key.substring(i, key.length());
            }
            i += 3;
        }
        return res;
    }

    /**
     * 显示请勿截图Dialog
     */
    private void showAlertDialog() {
        int[] listenedItems = {R.id.tv_i_understand};
        CustomDialog dialog = new CustomDialog(getContext(),
                R.layout.dialog_no_screenshot, listenedItems, false, Gravity.CENTER);
        dialog.setOnDialogItemClickListener(new CustomDialog.OnCustomDialogItemClickListener() {

            @Override
            public void OnCustomDialogItemClick(CustomDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.tv_i_understand:
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
