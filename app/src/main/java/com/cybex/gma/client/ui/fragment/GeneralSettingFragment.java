package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.view.View;

import com.allen.library.SuperTextView;
import com.cybex.componentservice.api.ApiPath;
import com.cybex.gma.client.R;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.LanguageManager;
import com.hxlx.core.lib.utils.OnChangeLanguageEvent;
import com.hxlx.core.lib.utils.SPUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 通用设置界面
 */
public class GeneralSettingFragment extends XFragment {

    private final int CURRENCY_CNY = 1;
    private final int CURRENCY_USD = 2;
    Unbinder unbinder;
    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.superTextView_change_language) SuperTextView superTextViewChangeLanguage;
    @BindView(R.id.superTextView_change_unit) SuperTextView superTextViewChangeUnit;
    @BindView(R.id.superTextView_change_node) SuperTextView superTextViewChangeNode;

    public static GeneralSettingFragment newInstance() {
        Bundle args = new Bundle();
        GeneralSettingFragment fragment = new GeneralSettingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);

    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setNavibarTitle(getResources().getString(R.string.general), true, true);
        //切换语言
        superTextViewChangeLanguage.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                start(LanguageSettingFragment.newInstance());

            }
        });
        //切换货币显示单位
        superTextViewChangeUnit.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                start(CurrencyUnitFragment.newInstance());
            }
        });
        //切换节点
        superTextViewChangeNode.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                start(ChangeNodeFragment.newInstance());
            }
        });


        this.showCurrencyUnit();
        this.showLanguage();
        this.showCurNode();
    }



    private void showLanguage() {
        int savedLanguageType = LanguageManager.getInstance(getContext()).getLanguageType();
        switch (savedLanguageType) {
            case LanguageManager.LanguageType.LANGUAGE_FOLLOW_SYSTEM:
                superTextViewChangeLanguage.setRightString(getString(R.string.follow_system));
                break;
            case LanguageManager.LanguageType.LANGUAGE_CHINESE_SIMPLIFIED:
                superTextViewChangeLanguage.setRightString(getString(R.string.simplified_C));
                break;
            case LanguageManager.LanguageType.LANGUAGE_EN:
                superTextViewChangeLanguage.setRightString(getString(R.string.english));
                break;
            default:
                break;
        }
    }

    private void showCurrencyUnit() {
        int savedCurrencyUnit = SPUtils.getInstance().getInt("currency_unit");
        switch (savedCurrencyUnit) {
            case CURRENCY_CNY:
                superTextViewChangeUnit.setRightString(getString(R.string.CNY));
                break;
            case CURRENCY_USD:
                superTextViewChangeUnit.setRightString(getString(R.string.USD));
                break;
            default:
                superTextViewChangeUnit.setRightString(getString(R.string.CNY));
                break;
        }
    }

    private void showCurNode(){
        String selectedNode = SPUtils.getInstance().getString("curNode");
            switch (selectedNode){
                case ApiPath.EOS_CYBEX:
                    superTextViewChangeNode.setRightString(getString(R.string.eos_cybex));
                    break;
                case ApiPath.EOS_CYPHER_GLASS:
                    superTextViewChangeNode.setRightString(getString(R.string.eos_cypher_glass));
                    break;
                case ApiPath.EOS_GRAVITY:
                    superTextViewChangeNode.setRightString(getString(R.string.eos_gravity));
                    break;
                case ApiPath.EOS_GREY_MASS:
                    superTextViewChangeNode.setRightString(getString(R.string.eos_greymass));
                    break;
                case ApiPath.EOS_HELLOEOS:
                    superTextViewChangeNode.setRightString(getString(R.string.hello_eos));
                    break;
                case ApiPath.EOS_HK_EOS:
                    superTextViewChangeNode.setRightString(getString(R.string.hk_eos));
                    break;
                case ApiPath.EOS_NEW_YORK:
                    superTextViewChangeNode.setRightString(getString(R.string.eos_newyork));
                    break;
                case ApiPath.EOS_START_EOS:
                    superTextViewChangeNode.setRightString(getString(R.string.start_eos));
                    break;
                case ApiPath.EOS_AISA:
                    superTextViewChangeNode.setRightString(getString(R.string.eos_asia));
                    break;
                case ApiPath.EOS_42:
                    superTextViewChangeNode.setRightString(getString(R.string.eos_42));
                    break;
                case ApiPath.EOS_TEST_BEIJING:
                    superTextViewChangeNode.setRightString(getString(R.string.eos_test_beijing));
                    break;
                default:
                    superTextViewChangeNode.setRightString(getString(R.string.eos_cybex));
        }
    }


    /**
     * 更改语言事件
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangeLanguageEvent(OnChangeLanguageEvent event) {
        if (event != null) {
            showLanguage();
        }

    }

    @Override
    public boolean useEventBus() {
        return true;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_general_setting;
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
