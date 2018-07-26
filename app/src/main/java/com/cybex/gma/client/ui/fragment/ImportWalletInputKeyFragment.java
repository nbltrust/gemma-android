package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.cybex.gma.client.R;
import com.cybex.gma.client.event.BarcodeScanEvent;
import com.cybex.gma.client.manager.LoggerManager;
import com.cybex.gma.client.manager.UISkipMananger;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 *
 * 导入钱包输入私钥界面
 */

public class ImportWalletInputKeyFragment extends XFragment {

    @BindView(R.id.bt_start_input) Button btStartInput;
    @BindView(R.id.edt_show_priKey) MaterialEditText edtShowPrikey;
    Unbinder unbinder;

    @OnClick(R.id.bt_start_input)
    public void goConfigWallet(){
        start(ImportWalletConfigFragment.newInstance());
    }


    public static ImportWalletInputKeyFragment newInstance() {
        Bundle args = new Bundle();
        ImportWalletInputKeyFragment fragment = new ImportWalletInputKeyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public boolean useEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showPriKey(BarcodeScanEvent message){
        LoggerManager.d("received scan", message.getResult());
        edtShowPrikey.setText(message.getResult());
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(ImportWalletInputKeyFragment.this, rootView);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setNavibarTitle("导入钱包", true, false);

    }

    @Override
    protected void setNavibarTitle(String title, boolean isShowBack, boolean isOnBackFinishActivity) {
        super.setNavibarTitle(title, isShowBack, isOnBackFinishActivity);

        ImageView mCollectView = (ImageView)mTitleBar.addAction(new TitleBar.ImageAction(R.drawable.ic_notify_scan) {
            @Override
            public void performAction(View view) {
                UISkipMananger.launchBarcodeScan(getActivity());
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_import_wallet_input_key;
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
}
