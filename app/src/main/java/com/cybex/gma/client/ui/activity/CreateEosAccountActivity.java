package com.cybex.gma.client.ui.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.cybex.gma.client.R;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.presenter.CreateEosAccountPresenter;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 普通多币种钱包创建EOS账户页面
 */
public class CreateEosAccountActivity extends XActivity<CreateEosAccountPresenter> implements Validator.ValidationListener {

    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @NotEmpty(messageResId = R.string.eos_name_not_empty)
    @BindView(R.id.edt_eos_name) EditText edtEosName;
    @BindView(R.id.iv_eos_name_clear) ImageView ivEosNameClear;
    @BindView(R.id.bt_next_step) Button btNextStep;
    private Bundle bd;
    Validator validator;

    @Override
    public void bindUI(View rootView) {
        ButterKnife.bind(this);
        setNavibarTitle(getString(R.string.eos_title_create_eos_account), true);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        validator = new Validator(this);
        validator.setValidationListener(this);
        bd = new Bundle();
        //删除icon
        ivEosNameClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtEosName.setText("");
            }
        });

        //下一步
        btNextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validator.validate();
            }
        });

        edtEosName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (EmptyUtils.isNotEmpty(s) && isUserNameValid()){
                    setClickable(btNextStep);
                }else {
                    setUnclickable(btNextStep);
                }
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_create_eos_account;
    }

    @Override
    public CreateEosAccountPresenter newP() {
        return new CreateEosAccountPresenter();
    }

    public String getEOSUsername(){
        return edtEosName.getText().toString().trim();
    }

    public boolean isUserNameValid() {
        String eosUsername = getEOSUsername();
        String regEx = "^[a-z1-5]{12}$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher((eosUsername));
        boolean res = matcher.matches();
        return res;
    }

    private void setClickable(Button button){
        button.setBackground(getDrawable(R.drawable.shape_corner_button));
    }

    private void setUnclickable(Button button){
        button.setBackground(getDrawable(R.drawable.shape_corner_button_unclickable));
    }


    @Override
    public void onValidationSucceeded() {
        if (isUserNameValid()){
            getP().verifyAccount(getEOSUsername());
        }else {
            GemmaToastUtils.showLongToast(getString(R.string.eos_invalid_eos_username));
        }

    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {

    }
}
