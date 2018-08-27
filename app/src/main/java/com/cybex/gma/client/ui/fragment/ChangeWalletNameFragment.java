package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.cybex.gma.client.R;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.db.entity.WalletEntity;
import com.cybex.gma.client.manager.DBManager;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;

/**
 * 修改钱包名页面
 */
public class ChangeWalletNameFragment extends XFragment {

    @BindView(R.id.editText_setWalletName) EditText setWalletName;
    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.clear_wallet_name) ImageView clearWalletName;
    Unbinder unbinder;
    private int textChangedCount;//TextChanged执行次数
    private WalletEntity curWallet;
    private final int requestCode = 2;

    @OnTextChanged(value = R.id.editText_setWalletName, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onTextChanged() {
        showSaveIcon(textChangedCount);
    }

    @OnClick(R.id.clear_wallet_name)
    public void setClearWalletName() {
        setWalletName.setText("");
    }

    public static ChangeWalletNameFragment newInstance(int walletID) {
        Bundle args = new Bundle();
        args.putInt("walletID", walletID);
        ChangeWalletNameFragment fragment = new ChangeWalletNameFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setNavibarTitle(getResources().getString(R.string.manage_wallet), true);
        textChangedCount = 0;
        mTitleBar.setActionTextColor(getResources().getColor(R.color.whiteTwo));
        mTitleBar.setActionTextSize(18);
        if (getArguments() != null) {
            int currentID = getArguments().getInt("walletID");
            curWallet = DBManager.getInstance().getWalletEntityDao().getWalletEntityByID(currentID);
            if (EmptyUtils.isNotEmpty(curWallet)) {
                setWalletName.setText(curWallet.getWalletName());
                //showSaveIcon();
            }
        }
        setWalletName.setFocusable(true);
        setWalletName.setFocusableInTouchMode(true);
        setWalletName.requestFocus();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_change_walletname;
    }

    @Override
    public Object newP() {
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public String getWalletName() {
        return setWalletName.getText().toString().trim();
    }

    public boolean isWalletNameExist(String walletName) {
        List<WalletEntity> list = DBManager.getInstance().getWalletEntityDao().getWalletEntityList();
        for (WalletEntity walletEntity : list) {
            if (walletEntity.getWalletName().equals(walletName)) {
                return true;
            }
        }
        return false;
    }

    private void showSaveIcon(int count) {
        if (count == 1){
            //只在第二次OnTextChanged的时候显示出来Icon
            mTitleBar.addAction(new TitleBar.TextAction(getString(R.string.save)) {
                @Override
                public void performAction(View view) {

                    if (isWalletNameExist(getWalletName())) {
                        //同样的钱包名
                        GemmaToastUtils.showLongToast(ParamConstants.SAME_WALLET_NAME);
                    } else if (EmptyUtils.isNotEmpty(getWalletName())) {
                        //允许修改，保存新钱包名
                        final String name = getWalletName();
                        curWallet.setWalletName(name);
                        DBManager.getInstance().getWalletEntityDao().saveOrUpateEntity(curWallet);
                        GemmaToastUtils.showLongToast(ParamConstants.CHANGE_NAME_SUCCESS);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("curWallet", curWallet);
                        //start(WalletDetailFragment.newInstance(bundle));
                        setFragmentResult(requestCode, bundle);
                        pop();
                    } else {
                        //钱包名为空
                        GemmaToastUtils.showLongToast(ParamConstants.EMPTY_WALLET_NAME);
                    }
                }
            });
        }
        textChangedCount++;
    }

}
