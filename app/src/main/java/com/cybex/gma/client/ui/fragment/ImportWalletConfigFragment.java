package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cybex.gma.client.R;
import com.cybex.gma.client.api.callback.JsonCallback;
import com.cybex.gma.client.config.CacheConstants;
import com.cybex.gma.client.db.entity.WalletEntity;
import com.cybex.gma.client.manager.DBManager;
import com.cybex.gma.client.manager.LoggerManager;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.JNIUtil;
import com.cybex.gma.client.ui.model.request.GetkeyAccountReqParams;
import com.cybex.gma.client.ui.model.response.GetKeyAccountsReuslt;
import com.cybex.gma.client.ui.request.GetKeyAccountsRequest;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.GsonUtils;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.lzy.okgo.model.Response;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cxy.com.validate.IValidateResult;
import cxy.com.validate.Validate;
import cxy.com.validate.ValidateAnimation;
import cxy.com.validate.annotation.Index;
import cxy.com.validate.annotation.MinLength;
import cxy.com.validate.annotation.NotNull;
import cxy.com.validate.annotation.Password1;
import cxy.com.validate.annotation.Password2;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

/**
 * 配置钱包界面
 *
 */

public class ImportWalletConfigFragment extends XFragment {


    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.tv_set_pass) TextView tvSetPass;

    @Index(1)
    @NotNull(msg = "密码不能为空")
    @MinLength(length = 8, msg = "密码请至少输入8位！")
    @Password1()
    @BindView(R.id.edt_set_pass) EditText edtSetPass;

    @BindView(R.id.tv_repeat_pass) TextView tvRepeatPass;

    @Index(2)
    @NotNull(msg = "重复密码不能为空")
    @Password2(msg = "两次密码不一致")
    @BindView(R.id.edt_repeat_pass) EditText edtRepeatPass;

    @BindView(R.id.tv_pass_hint) TextView tvPassHint;
    @BindView(R.id.edt_pass_hint) EditText edtPassHint;
    @BindView(R.id.checkbox_config) CheckBox checkboxConfig;
    @BindView(R.id.service_agreement_config) TextView serviceAgreementConfig;
    @BindView(R.id.layout_checkBox) LinearLayout layoutCheckBox;
    @BindView(R.id.btn_complete_import) Button btnCompleteImport;
    @BindView(R.id.scroll_wallet_config) ScrollView scrollViewWalletConfig;

    Unbinder unbinder;

    @OnClick(R.id.btn_complete_import)
    public void checkValidation(){
        //先检查表单
        Validate.check(this, new IValidateResult() {
            @Override
            public void onValidateSuccess() {
                if (checkboxConfig.isChecked()){//再检查checkBox
                    final String priKey = getArguments().getString("priKey");
                    LoggerManager.d("priKey", priKey);
                    saveTestWallet(priKey, getPassword(), getPassHint());
                    UISkipMananger.launchHome(getActivity());
                }
            }

            @Override
            public void onValidateError(String msg, View view) {
                EditText editText = (EditText) view;
                editText.setHint(msg);
                GemmaToastUtils.showLongToast(msg);
                editText.setHintTextColor(getResources().getColor(R.color.scarlet));
            }

            @Override
            public Animation onValidateErrorAnno() {
                return ValidateAnimation.horizontalTranslate();
            }
        });
    }

    public static ImportWalletConfigFragment newInstance(String privateKey) {
        Bundle args = new Bundle();
        args.putString("priKey", privateKey);
        ImportWalletConfigFragment fragment = new ImportWalletConfigFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
        Validate.reg(this);
        OverScrollDecoratorHelper.setUpOverScroll(scrollViewWalletConfig);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setNavibarTitle("配置钱包", true, false);
        final String priKey = getArguments().getString("priKey");
        LoggerManager.d("priKey", priKey);
        checkboxConfig.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    if (isAllFilled()){
                        setButtonClickableStyle();
                    }else{
                        setButtonUnClickableStyle();
                    }
                }else{
                    setButtonUnClickableStyle();
                }
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_import_wallet_config;
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

    public boolean isAllFilled(){
        if (EmptyUtils.isEmpty(edtSetPass.getText().toString().trim())
                || EmptyUtils.isEmpty(edtRepeatPass.getText().toString().trim())){
            return false;
        }
        return true;
    }

    public void setButtonClickableStyle(){
        btnCompleteImport.setBackground(getResources().getDrawable(R.drawable.shape_corner_button));

    }

    public void setButtonUnClickableStyle(){
        btnCompleteImport.setBackground(getResources().getDrawable(R.drawable.shape_corner_button_unclickable));

    }

    public void saveTestWallet(final String privateKey, final String password, final String passwordTips ){

        WalletEntity walletEntity = new WalletEntity();
        List<WalletEntity> walletEntityList = DBManager.getInstance().getMediaBeanDao().getWalletEntityList();
        //获取当前数据库中已存入的钱包个数
        int walletNum = walletEntityList.size();
        int index = walletNum + 1;
        //以默认钱包名称存入
        walletEntity.setWalletName(CacheConstants.DEFAULT_WALLETNAME_PREFIX + String.valueOf(index));
        walletEntity.setEosName("default-eos-name");
        final String cypher = JNIUtil.get_cypher(password, privateKey);
        walletEntity.setPrivateKey(cypher);//设置摘要
        walletEntity.setIsCurrentWallet(CacheConstants.IS_CURRENT_WALLET);//设置是否为当前钱包，默认新建钱包为当前钱包
        walletEntity.setIsBackUp(CacheConstants.NOT_BACKUP);//设置为未备份
        walletEntity.setPasswordTip(passwordTips);//设置密码提示
        //执行存入操作之前需要把其他钱包设置为非当前钱包
        if (walletNum > 0){

            for (WalletEntity curWallet : walletEntityList){
                curWallet.setIsCurrentWallet(CacheConstants.NOT_CURRENT_WALLET);
                DBManager.getInstance().getMediaBeanDao().saveOrUpateMedia(curWallet);
            }
        }
        //最后执行存入操作，此前包此时为当前钱包
        DBManager.getInstance().getMediaBeanDao().saveOrUpateMedia(walletEntity);
    }


    public void saveConfigedWallet(final String privateKey, final String password, final String passwordTips ){
        //根据私钥算出公钥和摘要
        WalletEntity curWallet = new WalletEntity();
        final String cypher = JNIUtil.get_cypher(password, privateKey);
        curWallet.setCypher(cypher);
        final String pubKey = JNIUtil.get_public_key(privateKey);
        curWallet.setPublicKey(pubKey);
        curWallet.setPasswordTip(passwordTips);
        //todo 用公钥上链获取eosUsername,这里用同步方法(显示progress bar，如果上链查询eosUsername失败，提示导入失败)




        GetkeyAccountReqParams getkeyAccountReqParams = new GetkeyAccountReqParams();
        getkeyAccountReqParams.setPublic_key(pubKey);
        String json = GsonUtils.objectToJson(getkeyAccountReqParams);
        //todo json数据如何设置？
        GetKeyAccountsRequest request = new GetKeyAccountsRequest(GetKeyAccountsReuslt.class);
        request.setJsonParams(json);
        request.getkEYAccountS(new JsonCallback<GetKeyAccountsReuslt>() {
            @Override
            public void onSuccess(Response<GetKeyAccountsReuslt> response) {

            }
        });

        final String eosName = "";
        curWallet.setEosName(eosName);




        curWallet.setIsCurrentWallet(CacheConstants.IS_CURRENT_WALLET);
        //更新其他WALLET为非当前WALLET,再把构建好的WALLET存入数据库
        List<WalletEntity> walletEntityList = DBManager.getInstance().getMediaBeanDao().getWalletEntityList();
        for (WalletEntity walletEntity : walletEntityList){
            walletEntity.setIsCurrentWallet(CacheConstants.NOT_CURRENT_WALLET);
        }
        DBManager.getInstance().getMediaBeanDao().saveOrUpateMedia(curWallet);
    }

    public String getPassword(){
        return edtSetPass.getText().toString().trim();
    }

    public String getPassHint(){
        return edtPassHint.getText().toString().trim();
    }

}
