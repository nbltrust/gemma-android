package com.cybex.walletmanagement.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cybex.base.view.flowlayout.FlowLayout;
import com.cybex.base.view.flowlayout.TagAdapter;
import com.cybex.componentservice.config.BaseConst;
import com.cybex.componentservice.utils.CollectionUtils;
import com.cybex.componentservice.utils.TSnackbarUtil;
import com.cybex.walletmanagement.R;
import com.cybex.walletmanagement.widget.LabelsView;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.trycatch.mysnackbar.Prompt;

import java.util.ArrayList;
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
        initBelowLabelView();

        if (getIntent() == null) {
            return;
        }
        mnes = getIntent().getStringArrayExtra(BaseConst.KEY_MNEMONIC);

        List<String> tempLabels = new ArrayList<>();
        if (EmptyUtils.isNotEmpty(mnes)) {
            for (int i = 0; i < mnes.length; i++) {
                String word = mnes[i];
                tempLabels.add(word);
            }

            answerLabels.addAll(tempLabels);

            //打乱顺序
            //Collections.shuffle(tempLabels);


            viewShowMne.setLabels(tempLabels);
            unSelectedLabels.addAll(tempLabels);
        }


        //上方助记词显示区域
        viewClickToShowMne.setOnLabelClickListener(new LabelsView.OnLabelClickListener() {
            @Override
            public void onLabelClick(TextView label, Object data, int position) {
                selectedLabels.remove(label.getText().toString());
                unSelectedLabels.add(label.getText().toString());
                updateAboveLabelView(selectedLabels);
                updateBottomLabelView(unSelectedLabels);
            }
        });
        //下方助记词显示区域
        viewShowMne.setOnLabelClickListener(new LabelsView.OnLabelClickListener() {
            @Override
            public void onLabelClick(TextView label, Object data, int position) {
                if (isInit) {
                    setAboveLabelView();
                }
                selectedLabels.add(label.getText().toString());
                unSelectedLabels.remove(label.getText().toString());
                updateAboveLabelView(selectedLabels);
                updateBottomLabelView(unSelectedLabels);
                isInit = false;

                doValidateResult();
            }
        });
    }

    /**
     * 执行验证助记词
     */
    private void doValidateResult() {
        if (EmptyUtils.isEmpty(unSelectedLabels)) {
            if (EmptyUtils.isNotEmpty(selectedLabels) && EmptyUtils.isNotEmpty(answerLabels)) {
                if (CollectionUtils.isEqualListWithSequence(selectedLabels, answerLabels)) {
                    TSnackbarUtil.showTip(viewRoot, getString(R.string.walletmanage_mnemonic_validate_success),
                            Prompt.SUCCESS);
                } else {
                    TSnackbarUtil.showTip(viewRoot, getString(R.string.walletmanage_mnemonic_validate_error),
                            Prompt.ERROR);
                }
            }
        }

    }


    /**
     * 设置上方LabelView初始样式
     */
    public void initAboveLabelView() {
        viewClickToShowMne.setLabelBackgroundDrawable(getResources().getDrawable(R.drawable.shape_corner));
        viewClickToShowMne.setWordMargin(30);
        viewClickToShowMne.setLineMargin(40);
        viewClickToShowMne.setSelectType(LabelsView.SelectType.SINGLE);
        viewClickToShowMne.setLabelTextSize(42);
        viewClickToShowMne.setLabelTextColor(getResources().getColor(R.color.whiteTwo));
        viewClickToShowMne.setLabelTextPadding(40, 20, 40, 20);
    }

    /**
     * 设置上方LabelView正常显示样式
     */
    public void setAboveLabelView() {
        viewClickToShowMne.setLabelBackgroundDrawable(getResources().getDrawable(R.drawable.walletmanage_shape_corner_text));
        viewClickToShowMne.setWordMargin(20);
        viewClickToShowMne.setLineMargin(20);
        viewClickToShowMne.setSelectType(LabelsView.SelectType.SINGLE);
        viewClickToShowMne.setLabelTextSize(42);
        viewClickToShowMne.setLabelTextColor(Color.parseColor("#4169d1"));
        viewClickToShowMne.setLabelTextPadding(40, 20, 40, 20);
    }

    /**
     * 设置下方LabelView样式
     */
    public void initBelowLabelView() {
        viewShowMne.setLabelBackgroundDrawable(getResources().getDrawable(R.drawable.walletmanage_shape_corner_text_clicked));
        viewShowMne.setWordMargin(20);
        viewShowMne.setLineMargin(20);
        viewShowMne.setSelectType(LabelsView.SelectType.SINGLE);
        viewShowMne.setLabelTextSize(42);
        viewShowMne.setLabelTextColor(Color.WHITE);
        viewShowMne.setLabelTextPadding(40, 20, 40, 20);
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
