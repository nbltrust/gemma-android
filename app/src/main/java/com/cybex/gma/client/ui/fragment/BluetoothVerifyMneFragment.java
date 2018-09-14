package com.cybex.gma.client.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.cybex.gma.client.R;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.utils.CollectionUtils;
import com.cybex.gma.client.utils.bluetooth.BlueToothWrapper;
import com.cybex.gma.client.widget.LabelsView;
import com.extropies.common.MiddlewareInterface;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 验证助记词页面
 */

public class BluetoothVerifyMneFragment extends XFragment {

    Unbinder unbinder;
    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.view_click_to_show_mne) LabelsView viewClickToShowMne;
    @BindView(R.id.view_show_mne) LabelsView viewShowMne;

    private boolean isInit;//是否为初始状态，用来调整上方显示区域大小

    List<String> selectedLabels = new ArrayList<>();//被点选的Label
    List<String> unSelectedLabels = new ArrayList<>();

    private List<String> answerLabels = new ArrayList<>();

    BlueToothWrapper.GenSeedMnesReturnValue values = null;

    long contextHandle = 0;

    public static BluetoothVerifyMneFragment newInstance(Bundle bd) {
        BluetoothVerifyMneFragment fragment = new BluetoothVerifyMneFragment();
        fragment.setArguments(bd);
        return fragment;
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(BluetoothVerifyMneFragment.this, rootView);
        setNavibarTitle(getResources().getString(R.string.title_verify_mne), true, false);
    }


    @Override
    public void initData(Bundle savedInstanceState) {
        isInit = true;
        initAboveLabelView();
        initBelowLabelView();

        Bundle bd = getArguments();
        if (bd != null) {
            values = bd.getParcelable(ParamConstants.KEY_GEEN_SEED);
            contextHandle = bd.getLong(ParamConstants.CONTEXT_HANDLE);

            if (values != null) {
                String[] mnes = values.getStrMneWord();
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

            }
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
                if (CollectionUtils.isEqualCollection(selectedLabels, answerLabels)) {
                    //app验证通过，发送给硬件验证，并且获取硬件返回的公钥
                    if (values != null) {
                        int[] checkedIndex = values.getCheckIndex();
                        int[] checkIndexCount = values.getCheckIndexCount();
                        if (EmptyUtils.isNotEmpty(checkedIndex)) {
                            StringBuffer sb = new StringBuffer();
                            for (int m = 0; m < checkIndexCount[0]; m++) {
                                String label = answerLabels.get(checkedIndex[m]);
                                sb.append(label);
                                if (m != checkedIndex.length - 1) {
                                    sb.append(" ");
                                }
                            }

                            String strDestMnes = sb.toString();
                            int status = MiddlewareInterface.generateSeed_CheckMnes(contextHandle, 0,
                                    strDestMnes);

                            if (status == MiddlewareInterface.PAEW_RET_SUCCESS) {
                                GemmaToastUtils.showShortToast("验证通过");
                            }
                        }
                    }
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
        viewClickToShowMne.setLabelBackgroundDrawable(getResources().getDrawable(R.drawable.shape_corner_text));
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
        viewShowMne.setLabelBackgroundDrawable(getResources().getDrawable(R.drawable.shape_corner_text_clicked));
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

    public void updateViewHeight() {
        //todo 动态计算Label的高度以更新LabelsView的高度
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_bluetooth_verify_mne;
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
        unbinder.unbind();
    }
}
