package com.cybex.walletmanagement.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cybex.base.view.flowlayout.FlowLayout;
import com.cybex.base.view.flowlayout.TagAdapter;
import com.cybex.base.view.flowlayout.TagFlowLayout;
import com.cybex.walletmanagement.R;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.siberiadante.customdialoglib.CustomDialog;

import me.framework.fragmentation.anim.DefaultHorizontalAnimator;
import me.framework.fragmentation.anim.FragmentAnimator;



public class MnemonicShowActivity extends XActivity {

    private Button btnShowMne;
    private TagFlowLayout mFlowLayout;
    private String[] mnemonics={"wallet","happy","change","hahah","wallet","happy","change","hahah","wallet","happy","change","hahah"};

    @Override
    public void bindUI(View view) {
        btnShowMne =  findViewById(R.id.bt_copied_mne);
        mFlowLayout = findViewById(R.id.id_flowlayout);
        btnShowMne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MnemonicShowActivity.this,MnemonicVerifyActivity.class));
            }
        });
        setNavibarTitle(getResources().getString(R.string.walletmanage_backup_title), true);


        mFlowLayout.setAdapter(new TagAdapter<String>(mnemonics) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) getLayoutInflater().inflate(R.layout.walletmanage_item_tag,
                        mFlowLayout, false);
                tv.setText(s);
                return tv;
            }
        });

        showAlertDialog();
    }

    @Override
    public void initData(Bundle savedInstanceState) {
    }

    @Override
    public int getLayoutId() {
        return R.layout.walletmanage_activity_mnemonic_show;
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

    /**
     * 显示请勿截图Dialog
     */
    private void showAlertDialog() {
        int[] listenedItems = {R.id.tv_understand};
        CustomDialog dialog = new CustomDialog(this,
                R.layout.walletmanage_dialog_no_screenshot_mne, listenedItems, false, Gravity.CENTER);
        dialog.setOnDialogItemClickListener(new CustomDialog.OnCustomDialogItemClickListener() {

            @Override
            public void OnCustomDialogItemClick(CustomDialog dialog, View view) {
//                switch (view.getId()) {
//                    case R.id.tv_understand:
//                        dialog.cancel();
//                        break;
//                    default:
//                        break;
//                }
                if(view.getId()==R.id.tv_understand){
                    dialog.cancel();
                }
            }
        });
        dialog.show();
    }
}
