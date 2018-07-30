package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cybex.gma.client.R;
import com.cybex.gma.client.db.entity.WalletEntity;
import com.cybex.gma.client.manager.DBManager;
import com.cybex.gma.client.manager.LoggerManager;
import com.cybex.gma.client.ui.JNIUtil;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import cxy.com.validate.IValidateResult;
import cxy.com.validate.Validate;
import cxy.com.validate.ValidateAnimation;
import cxy.com.validate.annotation.Index;
import cxy.com.validate.annotation.MinLength;
import cxy.com.validate.annotation.Password1;
import cxy.com.validate.annotation.Password2;

public class ChangePasswordFragment extends XFragment {

    Unbinder unbinder;
    private WalletEntity curWallet;
    private String priKey;
    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.tv_set_new_pass) TextView tvSetNewPass;
    @Index(1)
    @Password1()
    @MinLength(length = 8, msg = "新密码请至少输入8位！")
    @BindView(R.id.edt_set_new_pass) EditText edtSetNewPass;
    @BindView(R.id.tv_repeat_new_pass) TextView tvRepeatNewPass;
    @Index(2)
    @Password2(msg = "两次输入的密码不匹配")
    @BindView(R.id.edt_repeat_new_pass) EditText edtRepeatNewPass;
    @BindView(R.id.tv_new_pass_hint) TextView tvNewPassHint;
    @BindView(R.id.edt_new_pass_hint) EditText edtNewPassHint;
    @BindView(R.id.btn_confirm_change_pass) Button btnConfirmChangePass;
    @BindView(R.id.scroll_change_pass) ScrollView scrollChangePass;

    @OnTextChanged(value = R.id.edt_set_new_pass, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onPasswordChanged(){
        if (isAllFilled()){
            //todo 设置Button为可点击
        }
    }

    @OnTextChanged(value = R.id.edt_repeat_new_pass, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onRepeatPassChanged(){
        if (isAllFilled()){
            //设置Button为可点击
        }
    }

    @OnClick(R.id.btn_confirm_change_pass)
    public void changePass(){
        Validate.check(getActivity(), new IValidateResult() {
            @Override
            public void onValidateSuccess() {
                final String newCypher = JNIUtil.get_cypher(getPassword(), priKey);
                curWallet.setCypher(newCypher);
                DBManager.getInstance().getWalletEntityDao().saveOrUpateMedia(curWallet);
                GemmaToastUtils.showLongToast("修改密码成功！");
            }

            @Override
            public void onValidateError(String msg, View view) {
                GemmaToastUtils.showLongToast(msg);
            }

            @Override
            public Animation onValidateErrorAnno() {
                return ValidateAnimation.horizontalTranslate();
            }
        });

    }

    public static ChangePasswordFragment newInstance(String priKey, int walletID) {
        Bundle args = new Bundle();
        args.putString("key", priKey);
        args.putInt("walletID", walletID);
        ChangePasswordFragment fragment = new ChangePasswordFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
        Validate.reg(this);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setNavibarTitle("修改密码", true, false);
        final int currentId = getArguments().getInt("walletID");
        LoggerManager.d("currentID", currentId);
        curWallet = DBManager.getInstance().getWalletEntityDao().getWalletEntityByID(currentId);
        priKey = getArguments().getString("key");
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_change_password;
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
        Validate.unreg(this);
        unbinder.unbind();
    }

    public String getPassword(){
        return edtSetNewPass.getText().toString().trim();
    }

    public String getRepeatPass(){
        return edtRepeatNewPass.getText().toString().trim();
    }

    public boolean isAllFilled(){
        if(EmptyUtils.isEmpty(getPassword()) ||
                EmptyUtils.isEmpty(getRepeatPass())){
            return false;
        }
        return true;
    }


}
