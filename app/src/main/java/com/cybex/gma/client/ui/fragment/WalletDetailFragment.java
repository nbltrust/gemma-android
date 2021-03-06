package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.allen.library.SuperTextView;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.gma.client.R;
import com.cybex.componentservice.db.entity.WalletEntity;
import com.cybex.gma.client.event.WalletIDEvent;
import com.cybex.componentservice.event.WalletNameChangedEvent;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.JNIUtil;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.common.utils.HashGenUtil;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.siberiadante.customdialoglib.CustomDialog;
import com.siberiadante.customdialoglib.CustomFullDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import seed39.Seed39;


/**
 * 单独钱包详情管理页面
 * 在管理钱包一级界面中点击钱包名称进入的界面
 */
public class WalletDetailFragment extends XFragment {

    private WalletEntity curWallet;
    private Integer currentID;
    private String thisWalletName;
    private int inputCount;

    @BindView(R.id.layout_wallet_briefInfo) ConstraintLayout layoutWalletBriefInfo;
    @BindView(R.id.superTextView_exportPriKey) SuperTextView superTextViewExportPriKey;
    @BindView(R.id.superTextView_changePass) SuperTextView superTextViewChangePass;
    @BindView(R.id.scroll_wallet_detail) ScrollView scrollViewWalletDetail;
    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.tv_walletName_in_detailPage) TextView tvWalletNameInDetailPage;
    @BindView(R.id.eosAddress_in_detailPage) TextView tvPublicKey;
    @BindView(R.id.iv_arrow_in_detailPage) ImageView ivArrowInDetailPage;
    Unbinder unbinder;

    private final int requestCodeDown = 2;
    private final int requestCodeUp = 1;

    @OnClick(R.id.layout_wallet_briefInfo)
    public void goChangeWalletName() {
        //修改钱包名
        //start(ChangeWalletNameFragment.newInstance(curWallet.getId()));
        startForResult(ChangeWalletNameFragment.newInstance(curWallet.getId()), requestCodeDown);
    }

    public static WalletDetailFragment newInstance(Bundle args) {
        WalletDetailFragment fragment = new WalletDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
        OverScrollDecoratorHelper.setUpOverScroll(scrollViewWalletDetail);
    }

    @Override
    public boolean useEventBus() {
        return true;
    }

    @Override
    protected void setNavibarTitle(String title, boolean isShowBack, boolean isOnBackFinishActivity) {
        mTitleBar = btnNavibar;
        mTitleBar.setTitle(title);
        mTitleBar.setTitleColor(com.hxlx.core.lib.R.color.ffffff_white_1000);
        mTitleBar.setTitleSize(20);
        mTitleBar.setImmersive(true);
        if (isShowBack) {
            mTitleBar.setLeftImageResource(com.hxlx.core.lib.R.drawable.ic_btn_back);
            mTitleBar.setLeftClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isOnBackFinishActivity) {
                        getActivity().finish();
                    } else {
                        popChild();
                        pop();
                    }
                }
            });
        }
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setNavibarTitle(getResources().getString(R.string.eos_title_manage_wallet), true, false);
        inputCount = 0;
        if (getArguments() != null){
            curWallet = getArguments().getParcelable("curWallet");
            if (!EmptyUtils.isEmpty(curWallet)){
                currentID = curWallet.getId();
                //显示当前钱包名称
                final String walletName = curWallet.getWalletName();
                tvWalletNameInDetailPage.setText(walletName);
                //显示当前钱包公钥
                final String pubKey = curWallet.getPublicKey();
                tvPublicKey.setText(pubKey);

                //设置钱包ID和钱包名为回传参数
                Bundle bundle = new Bundle();
                bundle.putInt("walletID", currentID);
                bundle.putString("walletName", curWallet.getWalletName());
                setFragmentResult(requestCodeUp, bundle);
            }
        }

        //导出私钥点击事件
        superTextViewExportPriKey.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                EventBusProvider.postSticky(new WalletIDEvent(curWallet.getId()));
                UISkipMananger.launchBakupGuide(getActivity());
            }
        });
        //修改密码点击事件
        superTextViewChangePass.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                showConfirmAuthoriDialog();
            }
        });

    }

    @Override
    public int getLayoutId() {
        return R.layout.eos_fragment_wallet_detail;
    }

    @Override
    public Object newP() {
        return null;
    }

    @Override
    public void onDestroyView() {
        WalletNameChangedEvent event = new WalletNameChangedEvent();
        event.setWalletID(currentID);
        event.setWalletName(tvWalletNameInDetailPage.getText().toString().trim());
        EventBusProvider.postSticky(event);
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == 3){
            WalletEntity walletEntity = data.getParcelable("curWallet");
            if (EmptyUtils.isNotEmpty(walletEntity))
            tvWalletNameInDetailPage.setText(walletEntity.getWalletName());
        }
    }

    /**
     * 显示确认授权dialog
     */
    private void showConfirmAuthoriDialog() {
        int[] listenedItems = {R.id.imc_cancel, R.id.btn_confirm_authorization};
        CustomFullDialog dialog = new CustomFullDialog(getContext(),
                R.layout.eos_dialog_input_origin_password, listenedItems, false, Gravity.BOTTOM);
        dialog.setOnDialogItemClickListener(new CustomFullDialog.OnCustomDialogItemClickListener() {
            @Override
            public void OnCustomDialogItemClick(CustomFullDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.imc_cancel:
                        dialog.cancel();
                        break;
                    case R.id.btn_confirm_authorization:
//                        EditText edtPassword = dialog.findViewById(R.id.et_password);
//                        ImageView iv_clear = dialog.findViewById(R.id.iv_password_clear);
//                        iv_clear.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                edtPassword.setText("");
//                            }
//                        });
//                        //检查密码是否正确
//                        final String inputPass = edtPassword.getText().toString().trim();
//                        //重新获取curWallet，为确保修改密码过后验证的是新的密码
//                        MultiWalletEntity walletEntity = DBManager.getInstance().getMultiWalletEntityDao().getMultiWalletEntityByID
//                                (currentID);
//                        if (!EmptyUtils.isEmpty(walletEntity)){
//                            final String cypher = walletEntity.getCypher();
//                            final String hashPwd = HashGenUtil.generateHashFromText(inputPass, HashGenUtil
//                                    .TYPE_SHA256);
//
//                            if (cypher.equals(hashPwd)){
//                                //验证通过
//                                String priKey = Seed39.keyDecrypt(inputPass, )
//                                start(ChangePasswordFragment.newInstance(priKey,currentID));
//                                dialog.cancel();
//                            }else {
//                                inputCount++;
//                                iv_clear.setVisibility(View.VISIBLE);
//                                GemmaToastUtils.showLongToast(getResources().getString(R.string.eos_tip_wrong_password));
//                                if ( inputCount > 3 ){
//                                    dialog.cancel();
//                                    showPasswordHintDialog();
//                                }
//                            }
//                        }

                        break;
                    default:
                        break;
                }
            }
        });
        dialog.show();
        dialog.show();
        EditText etPasword = dialog.findViewById(R.id.et_password);
        etPasword.setHint("请输入@" + curWallet.getCurrentEosName() + "的密码");
    }

    /**
     * 显示密码提示Dialog
     */
    private void showPasswordHintDialog() {
        int[] listenedItems = {R.id.tv_i_understand};
        CustomDialog dialog = new CustomDialog(getContext(),
                R.layout.eos_dialog_password_hint, listenedItems, false, Gravity.CENTER);
        dialog.setOnDialogItemClickListener(new CustomDialog.OnCustomDialogItemClickListener() {

            @Override
            public void OnCustomDialogItemClick(CustomDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.tv_i_understand:
                        dialog.cancel();
                        showConfirmAuthoriDialog();
                        break;
                    default:
                        break;
                }
            }
        });
        dialog.show();

        TextView tv_pass_hint = dialog.findViewById(R.id.tv_password_hint_hint);
        if (EmptyUtils.isNotEmpty(curWallet)){
            String passHint = curWallet.getPasswordTip();
            String showInfo = getString(R.string.eos_tip_password_hint) + " : " + passHint;
            tv_pass_hint.setText(showInfo);
        }
    }
}
