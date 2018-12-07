package com.cybex.walletmanagement.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cybex.componentservice.config.BaseConst;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.db.util.DBCallback;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.utils.AlertUtil;
import com.cybex.componentservice.utils.CollectionUtils;
import com.cybex.componentservice.utils.SizeUtil;
import com.cybex.walletmanagement.R;
import com.cybex.componentservice.widget.LabelsView;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.common.utils.HandlerUtil;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.framework.fragmentation.anim.DefaultHorizontalAnimator;
import me.framework.fragmentation.anim.FragmentAnimator;

public class MnemonicVerifyActivity extends XActivity {


    TitleBar btnNavibar;
    LabelsView viewClickToShowMne;
    LabelsView viewShowMne;
    LinearLayout viewRoot;

    private boolean isInit;//是否为初始状态，用来调整上方显示区域大小

    private List<String> selectedLabels = new ArrayList<>();//被点选的Label
    private List<String> unSelectedLabels = new ArrayList<>();

    private List<String> answerLabels = new ArrayList<>();

    private String[] mnes;

    private boolean isOver;
    private MultiWalletEntity multiWalletEntity;

    @Override
    public void bindUI(View view) {
        btnNavibar = findViewById(R.id.btn_navibar);
        viewClickToShowMne = findViewById(R.id.view_click_to_show_mne);
        viewShowMne = findViewById(R.id.view_show_mne);
        viewRoot = findViewById(R.id.view_root);
        setNavibarTitle(getResources().getString(R.string.walletmanage_title_verify_mne), true);

    }

    @Override
    public void initData(Bundle savedInstanceState) {
        isInit = true;
        initAboveLabelView();
//        setAboveLabelView();
        initBelowLabelView();

        if (getIntent() == null) {
            return;
        }
        mnes = getIntent().getStringArrayExtra(BaseConst.KEY_MNEMONIC);
        multiWalletEntity = getIntent().getParcelableExtra(BaseConst.KEY_WALLET_ENTITY);
        List<String> tempLabels = new ArrayList<>();
        if (EmptyUtils.isNotEmpty(mnes)) {
            for (int i = 0; i < mnes.length; i++) {
                String word = mnes[i];
                tempLabels.add(word);
            }

            answerLabels.addAll(tempLabels);

            //打乱顺序
            Collections.shuffle(tempLabels);


            viewShowMne.setLabels(tempLabels);
            unSelectedLabels.addAll(tempLabels);
        }


        //上方助记词显示区域
        viewClickToShowMne.setOnLabelClickListener(new LabelsView.OnLabelClickListener() {
            @Override
            public void onLabelClick(TextView label, Object data, int position) {
                if(isOver)return;
                String remove = selectedLabels.remove(position);
                unSelectedLabels.add(remove);
                if(selectedLabels.size()==0){
                    initAboveLabelView();
                    isInit=true;
                }
                updateAboveLabelView(selectedLabels);
                updateBottomLabelView(unSelectedLabels);
            }
        });
        //下方助记词显示区域
        viewShowMne.setOnLabelClickListener(new LabelsView.OnLabelClickListener() {
            @Override
            public void onLabelClick(TextView label, Object data, int position) {
                if(isOver)return;
                if (isInit) {
                    setAboveLabelView();
                }
                selectedLabels.add(label.getText().toString());
                unSelectedLabels.remove(position);
                updateAboveLabelView(selectedLabels);
                updateBottomLabelView(unSelectedLabels);
                isInit = false;

                doValidateResult();
            }
        });
    }


    private void resetMnemonicsUI(){

        List<String> tempLabels = new ArrayList<>();
        tempLabels.addAll(answerLabels);
        //打乱顺序
        Collections.shuffle(tempLabels);
        viewShowMne.setLabels(tempLabels);
        unSelectedLabels.clear();
        unSelectedLabels.addAll(tempLabels);
        selectedLabels.clear();
        isInit=true;
        initAboveLabelView();
        updateAboveLabelView(selectedLabels);
        updateBottomLabelView(unSelectedLabels);
    }

    /**
     * 执行验证助记词
     */
    private void doValidateResult() {
        if (EmptyUtils.isEmpty(unSelectedLabels)) {
            if (EmptyUtils.isNotEmpty(selectedLabels) && EmptyUtils.isNotEmpty(answerLabels)) {
                if (CollectionUtils.isEqualListWithSequence(selectedLabels, answerLabels)) {
                    AlertUtil.showShortSuccessAlert(context,getString(R.string.walletmanage_mnemonic_validate_success));
//                    ARouter.getInstance().build(RouterConst.PATH_TO_WALLET_HOME)
//                            .navigation();
//                    finish();
                    MultiWalletEntity entityByID = DBManager.getInstance().getMultiWalletEntityDao().getMultiWalletEntityByID(multiWalletEntity.getId());
                    entityByID.setIsBackUp(1);
                    DBManager.getInstance().getMultiWalletEntityDao().saveOrUpateEntity(entityByID, new DBCallback() {
                        @Override
                        public void onSucceed() {
                            isOver=true;
                            HandlerUtil.runOnUiThreadDelay(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                }
                            },1500);
                        }

                        @Override
                        public void onFailed(Throwable error) {

                        }
                    });

                } else {
                    AlertUtil.showLongUrgeAlert(context,getString(R.string.walletmanage_mnemonic_validate_error));
                    resetMnemonicsUI();
                }
            }

        }
    }


    /**
     * 设置上方LabelView初始样式
     */
    public void initAboveLabelView() {

        int horizontalViewPadding = SizeUtil.dp2px(12);
        int verticalViewPadding = SizeUtil.dp2px(31f);
        viewClickToShowMne.setPadding(horizontalViewPadding,verticalViewPadding,horizontalViewPadding,verticalViewPadding);

        viewClickToShowMne.setLabelBackgroundDrawable(getResources().getDrawable(R.drawable.walletmanage_shape_verify_mnemonic_above));
        viewClickToShowMne.setWordMargin(SizeUtil.dp2px(7.5f));
        viewClickToShowMne.setLineMargin(SizeUtil.dp2px(11.5f));
        viewClickToShowMne.setSelectType(LabelsView.SelectType.NONE);
        viewClickToShowMne.setLabelTextSize(getResources().getDimension(R.dimen.font_4));
        viewClickToShowMne.setLabelTextColor(getResources().getColor(R.color.black_content));
        viewClickToShowMne.setLabelTextStyle(true);
    }

    /**
     * 设置上方LabelView正常显示样式
     */
    public void setAboveLabelView() {

        int horizontalViewPadding = SizeUtil.dp2px(12);
        int verticalViewPadding = SizeUtil.dp2px(15);
        viewClickToShowMne.setPadding(horizontalViewPadding,verticalViewPadding,horizontalViewPadding,verticalViewPadding);

        viewClickToShowMne.setLabelBackgroundDrawable(getResources().getDrawable(R.drawable.walletmanage_shape_verify_mnemonic_above));
        viewClickToShowMne.setWordMargin(SizeUtil.dp2px(7.5f));
        viewClickToShowMne.setLineMargin(SizeUtil.dp2px(11.5f));
        viewClickToShowMne.setSelectType(LabelsView.SelectType.NONE);
        viewClickToShowMne.setLabelTextSize(getResources().getDimension(R.dimen.font_4));
        viewClickToShowMne.setLabelTextColor(getResources().getColor(R.color.black_content));
        viewClickToShowMne.setLabelTextStyle(true);

        int horizontalPadding = SizeUtil.dp2px(15);
        int verticalPadding = SizeUtil.dp2px(5.5f);
        viewClickToShowMne.setLabelTextPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding);
    }

    /**
     * 设置下方LabelView样式
     */
    public void initBelowLabelView() {
        viewShowMne.setLabelBackgroundDrawable(getResources().getDrawable(R.drawable.walletmanage_shape_verify_mnemonic_bottom));
        viewShowMne.setWordMargin(SizeUtil.dp2px(7.5f));
        viewShowMne.setLineMargin(SizeUtil.dp2px(11.5f));
        viewShowMne.setSelectType(LabelsView.SelectType.SINGLE);
        viewShowMne.setLabelTextSize(getResources().getDimension(R.dimen.font_4));
        viewShowMne.setLabelTextColor(Color.WHITE);
        int horizontalPadding = SizeUtil.dp2px(15);
        int verticalPadding = SizeUtil.dp2px(5.5f);
        viewShowMne.setLabelTextPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding);
    }

    /**
     * 更新上方LabelView的显示
     *
     * @param data
     */
    public void updateAboveLabelView(List<String> data) {
        viewClickToShowMne.setLabels(data);
    }

    /**
     * 更新下方LabelView的显示
     *
     * @param data
     */
    public void updateBottomLabelView(List<String> data) {
        viewShowMne.setLabels(data);
    }

    @Override
    public int getLayoutId() {
        return R.layout.walletmanage_activity_mnemonic_verify;
    }

    @Override
    public Object newP() {
        return null;
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        // 设置横向(和安卓4.x动画相同)
        return new DefaultHorizontalAnimator();
    }
}
