package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.cybex.gma.client.R;
import com.cybex.gma.client.db.entity.WalletEntity;
import com.cybex.gma.client.manager.DBManager;
import com.cybex.gma.client.manager.UISkipMananger;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 修改钱包名页面
 */
public class ChangeWalletNameFragment extends XFragment {

    @BindView(R.id.editText_setWalletName) MaterialEditText setWalletName;
    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.clear_wallet_name) ImageView clearWalletName;
    Unbinder unbinder;
    private WalletEntity curWallet;

    @OnClick(R.id.clear_wallet_name)
    public void setClearWalletName(){
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
        setNavibarTitle("管理钱包", true);
        mTitleBar.setActionTextColor(getResources().getColor(R.color.whiteTwo));
        mTitleBar.setActionTextSize(18);
        int currentID = getArguments().getInt("walletID");
        curWallet = DBManager.getInstance().getWalletEntityDao().getWalletEntityByID(currentID);
        if (EmptyUtils.isNotEmpty(curWallet)){
            setWalletName.setHint(curWallet.getWalletName());
            mTitleBar.addAction(new TitleBar.TextAction("保存") {
                @Override
                public void performAction(View view) {
                    //todo 保存钱包名
                    List<WalletEntity> list = DBManager.getInstance().getWalletEntityDao().getWalletEntityList();
                    if (EmptyUtils.isNotEmpty(list)){
                        for (WalletEntity walletEntity : list){
                            if (setWalletName.getText().toString().trim().equals(walletEntity.getWalletName())){
                                GemmaToastUtils.showLongToast("钱包名称不能与已有钱包名相同");
                            }else if (EmptyUtils.isNotEmpty(setWalletName.getText().toString().trim())){
                                final String name = getWalletName();
                                curWallet.setWalletName(name);
                                DBManager.getInstance().getWalletEntityDao().saveOrUpateMedia(curWallet);
                                GemmaToastUtils.showLongToast("更改成功");
                                UISkipMananger.launchHome(getActivity());
                            }else{
                                GemmaToastUtils.showLongToast("钱包名称不能为空！");
                            }

                        }
                    }
                }
            });
        }
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

    public String getWalletName(){
        return setWalletName.getText().toString().trim();
    }

}
