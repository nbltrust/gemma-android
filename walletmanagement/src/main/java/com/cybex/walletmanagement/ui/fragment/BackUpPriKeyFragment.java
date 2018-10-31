package com.cybex.walletmanagement.ui.fragment;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cybex.componentservice.config.BaseConst;
import com.cybex.componentservice.config.CacheConstants;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.db.entity.WalletEntity;
import com.cybex.componentservice.manager.DBManager;

import com.cybex.componentservice.utils.ClipboardUtils;
import com.cybex.walletmanagement.R;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;

/**
 * 明文备份私钥页面
 */
public class BackUpPriKeyFragment extends XFragment {

    TextView tvShowPriKeyArea;
    Button btClickToCopy;
    Button btCopiedPriKey;
    private String key;
    private MultiWalletEntity curWallet;


    @Override
    public void bindUI(View rootView) {
        btCopiedPriKey = (Button) rootView.findViewById(R.id.bt_copied_priKey);
        btClickToCopy = (Button) rootView.findViewById(R.id.bt_click_to_copy);
        tvShowPriKeyArea = (TextView) rootView.findViewById(R.id.tv_show_priKey_area);

        btClickToCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String curPriKey = tvShowPriKeyArea.getText().toString().trim();
                if (getActivity() != null) {
                    ClipboardUtils.copyText(getActivity(), key);
                }
                GemmaToastUtils.showLongToast(getResources().getString(R.string.walletmanage_tip_prikey_copied));
            }
        });

        btCopiedPriKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!EmptyUtils.isEmpty(curWallet)) {
                    curWallet.setIsBackUp(CacheConstants.ALREADY_BACKUP);
                    DBManager.getInstance().getMultiWalletEntityDao().saveOrUpateEntity(curWallet, null);
                }
                getActivity().finish();
            }
        });
    }

    public static BackUpPriKeyFragment newInstance(String priKey, MultiWalletEntity walletEntity) {
        Bundle args = new Bundle();
        args.putString(BaseConst.KEY_PRI_KEY, priKey);
        args.putParcelable(BaseConst.KEY_WALLET_ENTITY, walletEntity);
        BackUpPriKeyFragment fragment = new BackUpPriKeyFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void initData(Bundle savedInstanceState) {
        key = getArguments().getString(BaseConst.KEY_PRI_KEY);
        curWallet = getArguments().getParcelable(BaseConst.KEY_WALLET_ENTITY);
        tvShowPriKeyArea.setText(formatKey(key));
    }

    @Override
    protected void setNavibarTitle(String title, boolean isShowBack) {
        super.setNavibarTitle(title, isShowBack);

    }

    @Override
    public int getLayoutId() {
        return R.layout.walletmanage_fragment_backup_prikey;
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

    public String formatKey(String key) {
        String res = "";
        for (int i = 0; i < key.length(); i++) {
            if (i + 4 < key.length()) {
                res += key.substring(i, i + 4) + " ";
            } else {
                res += key.substring(i, key.length());
            }
            i += 3;
        }
        return res;
    }

}
