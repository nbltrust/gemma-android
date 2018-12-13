package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import com.cybex.componentservice.api.ApiPath;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.manager.UISkipMananger;
import com.hxlx.core.lib.utils.LanguageManager;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.common.utils.AppManager;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.cybex.gma.client.config.ParamConstants.CN;
import static com.cybex.gma.client.config.ParamConstants.EN;

/**
 * 更改APP显示语言界面
 */

public class LanguageSettingFragment extends XFragment {

    Unbinder unbinder;
//    @BindView(R.id.radioButton_followsystem) RadioButton radioButtonFollowSystem;
    @BindView(R.id.radioButton_simC) RadioButton radioButtonSimC;
    @BindView(R.id.radioButton_EN) RadioButton radioButtonEN;

    private int savedLanguageType;

    public static LanguageSettingFragment newInstance() {
        Bundle args = new Bundle();
        LanguageSettingFragment fragment = new LanguageSettingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setNavibarTitle(getResources().getString(R.string.language), true, false);

        savedLanguageType = LanguageManager.getInstance(getContext()).getLanguageType();
        this.showCheckedLanguage();

        mTitleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop();
                setFragmentResult(ParamConstants.CODE_CHANGE_RESULT,null);
            }
        });
    }


    @OnClick({R.id.radioButton_simC, R.id.radioButton_EN})
    public void setLanguageListener(View v) {
        int selectedLanguage = -1;
        switch (v.getId()) {
//            case R.id.radioButton_followsystem:
//                selectedLanguage = LanguageManager.LanguageType.LANGUAGE_FOLLOW_SYSTEM;
//                break;
            case R.id.radioButton_simC:
                selectedLanguage = LanguageManager.LanguageType.LANGUAGE_CHINESE_SIMPLIFIED;
                break;
            case R.id.radioButton_EN:
                selectedLanguage = LanguageManager.LanguageType.LANGUAGE_EN;
                break;
            default:
                break;
        }

        LanguageManager.getInstance(getContext()).updateLanguage(selectedLanguage);
        AppManager.getAppManager().finishAllActivity();
        UISkipMananger.launchHomeSingle(getActivity());

    }

    private void showCheckedLanguage() {
        switch (savedLanguageType) {
//            case LanguageManager.LanguageType.LANGUAGE_FOLLOW_SYSTEM:
//                //跟随系统
//                showCheckedLogic(true, false, false);
//                break;
            case LanguageManager.LanguageType.LANGUAGE_CHINESE_SIMPLIFIED:
                //简体中文
                showCheckedLogic(false, true, false);
                break;
            case LanguageManager.LanguageType.LANGUAGE_EN:
                //英文
                showCheckedLogic(false, false, true);

                break;
            default:
                Locale systemLanguageType = LanguageManager.getInstance(getContext()).getSysLocale();
                switch (systemLanguageType.getDisplayLanguage()){
                    case CN:
                        showCheckedLogic(false, true, false);
                        break;
                    default:
                        //英文
                        showCheckedLogic(false, false, true);
                        break;
                }
                break;
        }

    }


    /**
     * 显示选中逻辑
     *
     * @param followSystem
     * @param chineseSimolified
     * @param languageEn
     * @return
     */
    private void showCheckedLogic(boolean followSystem, boolean chineseSimolified, boolean languageEn) {
//        radioButtonFollowSystem.setChecked(followSystem);
        radioButtonSimC.setChecked(chineseSimolified);
        radioButtonEN.setChecked(languageEn);
    }


    @Override
    public int getLayoutId() {
        return R.layout.eos_fragment_language_setting;
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


}
