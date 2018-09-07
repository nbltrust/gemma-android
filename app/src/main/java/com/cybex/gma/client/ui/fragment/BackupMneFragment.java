package com.cybex.gma.client.ui.fragment;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;

import com.cybex.gma.client.R;
import com.cybex.gma.client.widget.LabelsView;
import com.hxlx.core.lib.mvp.lite.XFragment;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.siberiadante.customdialoglib.CustomDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 备份助记词页面
 */
public class BackupMneFragment extends XFragment {

    Unbinder unbinder;
    @BindView(R.id.btn_navibar) TitleBar btnNavibar;
    @BindView(R.id.view_labels) LabelsView viewShowMne;
    @BindView(R.id.bt_copied_mne) Button btCopiedMne;

    public static BackupMneFragment newInstance() {
        Bundle args = new Bundle();
        BackupMneFragment fragment = new BackupMneFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @OnClick(R.id.bt_copied_mne)
    public void goVerifyMne(){
        start(VerifyMneFragment.newInstance());
    }

    @Override
    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
        setNavibarTitle(getResources().getString(R.string.backup_mne), true, false);
    }


    @Override
    public void initData(Bundle savedInstanceState) {
        setLabelView();
        showAlertDialog();
    }


    public void setLabelView(){
        List<String> testData = new ArrayList<>();
        testData.add("home");
        testData.add("avocado");
        testData.add("cabin");
        testData.add("flip");
        testData.add("tilt");
        testData.add("front");
        testData.add("during");
        testData.add("share");
        testData.add("style");
        testData.add("soup");
        testData.add("finger");
        testData.add("science");
        viewShowMne.setLabels(testData);
        viewShowMne.setLabelBackgroundDrawable(getResources().getDrawable(R.drawable.selector_label_bg));
        viewShowMne.setWordMargin(30);
        viewShowMne.setLineMargin(40);
        viewShowMne.setSelectType(LabelsView.SelectType.NONE);
        viewShowMne.setLabelTextSize(42);
        viewShowMne.setLabelTextPadding(40,20,40,20);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_backup_mne;
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

    /**
     * 显示请勿截图Dialog
     */
    private void showAlertDialog() {
        int[] listenedItems = {R.id.tv_i_understand};
        CustomDialog dialog = new CustomDialog(getContext(),
                R.layout.dialog_no_screenshot_mne, listenedItems, false, Gravity.CENTER);
        dialog.setOnDialogItemClickListener(new CustomDialog.OnCustomDialogItemClickListener() {

            @Override
            public void OnCustomDialogItemClick(CustomDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.tv_i_understand:
                        dialog.cancel();
                        break;
                    default:
                        break;
                }
            }
        });
        dialog.show();
    }
}
